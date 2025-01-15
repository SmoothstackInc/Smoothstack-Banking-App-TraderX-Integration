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

package com.smoothstack.userservice.service;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

@Service
public class FileUploadService {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;

    private volatile Path lastProcessedFile; // variable as being stored in main memory

    public boolean validateFileFormat(MultipartFile file) {
        return "text/csv".equals(file.getContentType());
    }

    public boolean validateFileSize(MultipartFile file) {
        final long MAX_SIZE = 50 * 1024 * 1024; // 50MB in bytes
        return file.getSize() <= MAX_SIZE;
    }

    public synchronized void setLastProcessedFile(Path filePath) {
        this.lastProcessedFile = filePath;
    }
    //prevents race conditions
    public synchronized Path getLastProcessedFile() {
        return lastProcessedFile;
    }

    public void retriggerJob() throws Exception {
        Path filePath = getLastProcessedFile();
        if (filePath != null) {
            JobParameters params = new JobParametersBuilder()
                    .addString("input.file", filePath.toString())
                    .addLong("timestamp", System.currentTimeMillis())
                    .addLong("grid.size", 8L)
                    .toJobParameters();
            jobLauncher.run(job, params);
        } else {
            throw new IllegalStateException("No file path available for retriggering the job.");
        }
    }
}
