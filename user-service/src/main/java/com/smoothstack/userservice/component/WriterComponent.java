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

import com.smoothstack.userservice.exception.BatchValidationException;
import com.smoothstack.userservice.model.AppUser;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class WriterComponent {

    @Autowired
    private EntityManagerFactory entityManagerFactory;

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
        String logMessage = String.format(
                "Timestamp: %s, Username: %s, Email: %s, Error: %s: %s",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                item != null ? item.getUsername() : "N/A",
                item != null ? item.getEmail() : "N/A",
                violation.getPropertyPath(),
                violation.getMessage()
        );
        writeLogMessage(logMessage);
    }

    @Bean
    @StepScope //ensures thread safety
    public ItemWriter<AppUser> writer() {
        return items -> {
            System.out.println("Writing items on thread: " + Thread.currentThread().getName());

            EntityManager em = entityManagerFactory.createEntityManager();
            List<String> validationErrors = new ArrayList<>();
            em.getTransaction().begin();
            try {
                for (AppUser item : items) {
                    if (item != null) {
                        try {
                            em.merge(item); // Handle merging or other JPA operations
                        } catch (ConstraintViolationException ex) {
                            validationErrors.addAll(ex.getConstraintViolations().stream()
                                    .map(violation -> formatConstraintViolation(item, violation))
                                    .collect(Collectors.toList()));
                        }
                    }
                }
                if (!validationErrors.isEmpty()) {
                    throw new BatchValidationException("Validation failed for one or more items.", validationErrors);
                }
                em.getTransaction().commit();
            } catch (Exception ex) {
                em.getTransaction().rollback();
                if (ex instanceof BatchValidationException) {
                    throw ex; // Re-throw the custom exception to be handled in afterJob
                }
                throw new RuntimeException("Unexpected error during batch processing", ex);
            } finally {
                em.close();
            }
        };
    }

    private String formatConstraintViolation(AppUser item, ConstraintViolation<?> violation) {
        return String.format(
                "Username: %s, Email: %s, Error: %s: %s",
                item != null ? item.getUsername() : "N/A",
                item != null ? item.getEmail() : "N/A",
                violation.getPropertyPath(),
                violation.getMessage()
        );
    }
}
