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

package com.smoothstack.userservice.model;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class VerificationToken {

    private int tokenId;
    private int userId;
    private String token;
    private Timestamp expiration;
    private boolean isUsed;
    private Timestamp lastVerificationRequest;

    // Constructors
    public VerificationToken() {}

    public VerificationToken(int tokenId, int userId, String token, Timestamp expiration, boolean isUsed, Timestamp lastVerificationRequest) {
        this.tokenId = tokenId;
        this.userId = userId;
        this.token = token;
        this.expiration = expiration;
        this.isUsed = isUsed;
        this.lastVerificationRequest = lastVerificationRequest;
    }
}
