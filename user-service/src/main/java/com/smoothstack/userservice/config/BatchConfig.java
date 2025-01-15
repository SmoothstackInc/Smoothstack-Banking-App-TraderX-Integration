/*
 * TraderX - A trading automation software.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.smoothstack.userservice.config;

import com.smoothstack.userservice.component.ListenerComponent;
import com.smoothstack.userservice.component.UserFilePartitioner;
import com.smoothstack.userservice.component.WriterComponent;
import com.smoothstack.userservice.dto.AppUserDTO;
import com.smoothstack.userservice.model.AppUser;
import com.smoothstack.userservice.component.UserItemProcessor;
import jakarta.validation.ConstraintViolationException;
import com.smoothstack.userservice.exception.SkippedUserException;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.beans.PropertyEditor;
import java.text.SimpleDateFormat;
import java.util.*;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private UserItemProcessor userItemProcessor;

    @Autowired
    private ListenerComponent listenerComponent;

    @Autowired
    private WriterComponent writerComponent;

    @Bean
    @StepScope
    public FlatFileItemReader<AppUserDTO> reader(@Value("#{stepExecutionContext['filePath']}") String filePath,
                                                 @Value("#{stepExecutionContext['startLine']}") int startLine,
                                                 @Value("#{stepExecutionContext['endLine']}") int endLine) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return new FlatFileItemReaderBuilder<AppUserDTO>()
                .name("userItemReader")
                .resource(new FileSystemResource(filePath))
                .linesToSkip(startLine)
                .maxItemCount(endLine - startLine + 1)
                .delimited()
                .names("username", "password", "email", "firstName", "lastName",
                        "isVerified", "isActive", "dateOfBirth", "phoneNumber", "address",
                        "secretQuestion", "secretAnswer", "role", "failedLoginAttempts")
                .fieldSetMapper(new BeanWrapperFieldSetMapper<AppUserDTO>() {{
                    setTargetType(AppUserDTO.class);
                    setCustomEditors(new HashMap<Class<?>, PropertyEditor>() {{
                        put(Date.class, new CustomDateEditor(dateFormat, true));
                        put(java.sql.Timestamp.class, new CustomDateEditor(timestampFormat, true));
                    }});
                }})
                .build();
    }


    @Bean
    public Step workerStep(PlatformTransactionManager transactionManager) {
        return new StepBuilder("workerStep", jobRepository)
                .<AppUserDTO, AppUser>chunk(100, transactionManager)
                .reader(reader(null, 0, 0))
                .processor(userItemProcessor)
                .writer(writerComponent.writer())
                .faultTolerant()
                .skipPolicy((throwable, skipCount) -> {
                    if (throwable instanceof ConstraintViolationException || throwable instanceof SkippedUserException) {
                        return true;
                    }
                    return false;
                })
                .listener(listenerComponent)
                .build();
    }

    @Bean
    @StepScope
    public Partitioner userFilePartitioner(@Value("#{jobParameters['input.file']}") String filePath,
                                           @Value("#{jobParameters['grid.size']}") Integer gridSize) {
        if (filePath == null || gridSize == null) {
            throw new IllegalArgumentException("File path and grid size must be provided");
        }
        return new UserFilePartitioner(filePath, gridSize);
    }

    @Bean
    public Step masterStep(PlatformTransactionManager transactionManager) {
        return new StepBuilder("masterStep", jobRepository)
                .partitioner("workerStep", userFilePartitioner(null, null))
                .step(workerStep(transactionManager))
                .gridSize(8)
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor() {
            @Override
            public void execute(Runnable task, long startTimeout) {
                System.out.println("Starting task on thread: " + Thread.currentThread().getName());
                super.execute(task, startTimeout);
            }
        };
        taskExecutor.setConcurrencyLimit(8);
        return taskExecutor;
    }

    @Bean
    public Job importUserJob(Step masterStep) {
        return new JobBuilder("importUserJob", jobRepository)
                .start(masterStep)
                .listener(new JobExecutionListener() {
                    private long startTime;

                    @Override
                    public void beforeJob(JobExecution jobExecution) {
                        startTime = System.currentTimeMillis();
                        System.out.println("Job started at: " + startTime);
                    }

                    @Override
                    public void afterJob(JobExecution jobExecution) {
                        long endTime = System.currentTimeMillis();
                        System.out.println("Job ended at: " + endTime);
                        System.out.println("Job duration: " + (endTime - startTime) + " milliseconds");
                    }
                })
                .build();
    }
}
