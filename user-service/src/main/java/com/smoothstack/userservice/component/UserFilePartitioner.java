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

import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserFilePartitioner implements Partitioner {

    private String filePath;
    private int gridSize;

    public UserFilePartitioner(String filePath, int gridSize) {
        this.filePath = filePath;
        this.gridSize = gridSize;
    }

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        Map<String, ExecutionContext> partitionMap = new HashMap<>();
        int totalLines = getTotalLines(filePath);
        int linesPerPartition = (int) Math.ceil((double) totalLines / gridSize);

        int startLine = 1;
        for (int i = 0; i < gridSize; i++) {
            int endLine = Math.min(startLine + linesPerPartition - 1, totalLines);

            if (startLine <= endLine) {
                ExecutionContext context = new ExecutionContext();
                context.putString("filePath", filePath);
                context.putInt("startLine", startLine);
                context.putInt("endLine", endLine);
                partitionMap.put("partition" + i, context);
            }

            startLine = endLine + 1;

            if (startLine > totalLines) {
                break;
            }
        }

        return partitionMap; //Each entry in the partitionMap represents a partition, Spring Batch executes a worker step for each partition.
    }

    private int getTotalLines(String filePath) {
        int totalLines = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            while (reader.readLine() != null) {
                totalLines++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return totalLines;
    }
}