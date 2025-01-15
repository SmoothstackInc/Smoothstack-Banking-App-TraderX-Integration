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

package com.smoothstack.userservice.controller;

import com.smoothstack.userservice.service.DuplicateCheckService;
import com.smoothstack.userservice.service.FileUploadService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/uploads")
public class FileUploadController {

    private final FileUploadService fileUploadService;
    private final JobLauncher jobLauncher;
    private final Job job;
    private final DuplicateCheckService duplicateCheckService;

    @Autowired
    public FileUploadController(FileUploadService fileUploadService, JobLauncher jobLauncher, Job job, DuplicateCheckService duplicateCheckService) {
        this.fileUploadService = fileUploadService;
        this.jobLauncher = jobLauncher;
        this.job = job;
        this.duplicateCheckService = duplicateCheckService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        if (!fileUploadService.validateFileFormat(file) || !fileUploadService.validateFileSize(file)) {
            return ResponseEntity.badRequest().body(
                    !fileUploadService.validateFileFormat(file) ?
                            "Invalid file format. Only CSV files are accepted." :
                            "File size exceeds the maximum limit of 50MB."
            );
        }
        try {
            Path tempFile = Files.createTempFile("uploads", file.getOriginalFilename());
            file.transferTo(tempFile);
            fileUploadService.setLastProcessedFile(tempFile);  // Set the file path

            boolean hasDuplicates = duplicateCheckService.checkAndPrepareDuplicates(tempFile);
            if (hasDuplicates) {
                return ResponseEntity.ok(Map.of(
                        "message", "Duplicates detected. Decision needed.",
                        "tempFilePath", tempFile.toString()
                ));
            } else {
                JobParameters params = new JobParametersBuilder()
                        .addString("input.file", tempFile.toString())
                        .addLong("timestamp", System.currentTimeMillis())
                        .addLong("grid.size", 16L)
                        .toJobParameters();
                jobLauncher.run(job, params);
                return ResponseEntity.ok("Batch job initiated.");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error processing file: " + e.getMessage());
        }
    }
}
