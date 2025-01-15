/*
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

package com.smoothstack.investment_orchestrator.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FileLogger {
    private static final Logger logger = LoggerFactory.getLogger(FileLogger.class);
    private static final String LOG_DIRECTORY = "logs/";

    public static synchronized void logToFile(String logMessage) {
        String fileName = getLogFileName();
        String filePath = LOG_DIRECTORY + fileName;

        try (FileWriter writer = new FileWriter(filePath, true)) {
            writer.write(logMessage + "\n");
        } catch (IOException e) {
            logger.error("Failed to write log to file: {}", e.getMessage());
        }
    }

    private static String getLogFileName() {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        return "log_" + currentDate.format(formatter) + ".txt";
    }

    public static String getLogDirectory() {
        return LOG_DIRECTORY;
    }

    public static synchronized void createLogDirectory() {  // synchronized to prevent multiple threads from concurrently creating the same directory
        File directory = new File(LOG_DIRECTORY);
        if (!directory.exists()) {
            if (directory.mkdirs()) {
                logger.info("Log directory created: {}", LOG_DIRECTORY);
            } else {
                logger.error("Failed to create log directory: {}", LOG_DIRECTORY);
            }
        }
    }
}