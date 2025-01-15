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

package com.smoothstack.userservice.component;

import com.smoothstack.userservice.dto.AppUserDTO;
import com.smoothstack.userservice.exception.BatchValidationException;
import com.smoothstack.userservice.model.AppUser;
import com.smoothstack.userservice.util.ViolationFormatter;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.batch.core.SkipListener;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Component
public class ListenerComponent extends JobExecutionListenerSupport implements SkipListener<AppUserDTO, AppUser> {

    private void writeLogMessage(String message) {
        Path logFilePath = Paths.get("logs", "upload_report.log");
        try {
            Files.createDirectories(logFilePath.getParent());
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFilePath.toFile(), true))) {
                writer.write(message);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void logValidationError(AppUser item, ConstraintViolation<?> violation) {
        String logMessage = ViolationFormatter.formatConstraintViolation(item, violation);
        writeLogMessage(logMessage);
    }

    @Override
    public void beforeJob(JobExecution jobExecution) {
        String jobId = UUID.randomUUID().toString();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String logMessage = String.format("Timestamp: %s, Job ID: %s, Job Name: %s", timestamp, jobId, jobExecution.getJobInstance().getJobName());
        writeLogMessage(logMessage);
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            writeLogMessage("Timestamp: " + timestamp + ", JOB COMPLETED");
        } else if (jobExecution.getStatus() == BatchStatus.FAILED) {
            writeLogMessage("Timestamp: " + timestamp + ", JOB FAILED");
            List<Throwable> exceptions = jobExecution.getAllFailureExceptions();
            if (!exceptions.isEmpty()) {
                for (Throwable exception : exceptions) {
                    if (exception instanceof BatchValidationException) {
                        BatchValidationException bve = (BatchValidationException) exception;
                        for (String error : bve.getValidationErrors()) {
                            writeLogMessage(error);
                        }
                    } else if (exception instanceof ConstraintViolationException) {
                        ConstraintViolationException cve = (ConstraintViolationException) exception;
                        for (ConstraintViolation<?> violation : cve.getConstraintViolations()) {
                            writeLogMessage(ViolationFormatter.formatConstraintViolation(null, violation)); // Format without item details
                        }
                    } else {
                        writeLogMessage("Error: " + exception.getMessage());
                    }
                }
            }
        }
    }

    @Override
    public void onSkipInRead(Throwable t) {
    }

    @Override
    public void onSkipInWrite(AppUser item, Throwable t) {
        // Log the error for skipped items during writing
        if (t instanceof ConstraintViolationException) {
            ConstraintViolationException cve = (ConstraintViolationException) t;
            for (ConstraintViolation<?> violation : cve.getConstraintViolations()) {
                logValidationError(item, violation);
            }
        } else {
            String logMessage = String.format(
                    "Timestamp: %s, Username: %s, Email: %s, Error: %s",
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    item != null ? item.getUsername() : "N/A",
                    item != null ? item.getEmail() : "N/A",
                    t.getMessage()
            );
            writeLogMessage(logMessage);
        }
    }

    @Override
    public void onSkipInProcess(AppUserDTO item, Throwable t) {
        // Log the error for skipped items during processing
        if (t instanceof ConstraintViolationException) {
            ConstraintViolationException cve = (ConstraintViolationException) t;
            for (ConstraintViolation<?> violation : cve.getConstraintViolations()) {
                String logMessage = ViolationFormatter.formatConstraintViolation(item.getUsername(), item.getEmail(), violation);
                writeLogMessage(logMessage);
            }
        } else {
            String logMessage = String.format(
                    "Timestamp: %s, Username: %s, Email: %s, Error: %s",
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    item.getUsername(),
                    item.getEmail(),
                    t.getMessage()
            );
            writeLogMessage(logMessage);
        }
    }
}
