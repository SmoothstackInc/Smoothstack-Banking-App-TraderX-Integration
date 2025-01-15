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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/duplicates")
public class DuplicateController {

    @Autowired
    private DuplicateCheckService duplicateCheckService;

    @Autowired
    private FileUploadService fileUploadService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/pending")
    public ResponseEntity<Map<String, String>> getPendingDuplicates() {
        Map<String, String> pending = duplicateCheckService.getPendingDuplicates();
        return ResponseEntity.ok(pending);
    }

    @PostMapping("/resolve")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> resolveDuplicates(@RequestBody Map<String, String> decisions) {
        decisions.forEach((username, decision) -> {
            System.out.println("Resolving duplicate for: " + username + " with decision: " + decision);
            duplicateCheckService.resolveDuplicate(username, decision);
        });

        try {
            fileUploadService.retriggerJob();
            duplicateCheckService.clearDuplicateCache();
            return ResponseEntity.ok("Duplicates resolved and job retriggered");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error retriggering job: " + e.getMessage());
        }
    }

}
