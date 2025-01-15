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

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import java.util.Collection;
import java.util.Date;
import java.util.List;

@Data //generates getters and setters
@Builder //simplifies the process of creating instances of AppUser. Allows you to use .build() method.
@NoArgsConstructor //Generates a no-argument constructor, which is required by JPA and other frameworks.
@AllArgsConstructor //Generates a constructor with one argument for every field in the class.
@Entity //Marks the class as a JPA entity, meaning it is tied to a database table.
@Table(name = "app_user") //Specifies the name of the database table to be used for mapping.
public class AppUser implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //The IDENTITY strategy indicates that the database will auto-increment the key.
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "username", unique = true)
    @NotEmpty(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @Column(name = "password")
    @NotEmpty(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    @Pattern(regexp = ".*[A-Z].*", message = "Password must contain at least one uppercase letter")
    @Pattern(regexp = ".*[a-z].*", message = "Password must contain at least one lowercase letter")
    @Pattern(regexp = ".*[0-9].*", message = "Password must contain at least one digit")
    @Pattern(regexp = ".*[^a-zA-Z0-9 ].*", message = "Password must contain at least one special character")
    @Pattern(regexp = "^[^\\s]+$", message = "Password must not contain spaces")
    private String password;

    @Column(name = "email", unique = true)
    @NotEmpty(message = "Email is required")
    @Email(message = "Email should be valid and contain at least one '@' and one '.'")
    private String email;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "is_verified")
    private Boolean isVerified;

    @Getter
    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "date_created")
    private Date dateCreated;

    @Column(name = "date_modified")
    private Date dateModified;

    @Column(name = "date_of_birth")
    private Date dateOfBirth;

    @Column(name = "phone_number")
    private Long phoneNumber;

    @Column(name = "address")
    private String address;

    @Column(name = "secret_question")
    private String secretQuestion;

    @Column(name = "secret_answer")
    private String secretAnswer;

    @Enumerated(EnumType.STRING) // This specifies that the Role field should be persisted as a string representing the name of the enum constant in the database.
    private Role role;

    @Column(name = "failed_login_attempts")
    private int failedLoginAttempts;

    @Column(name = "lock_time")
    private Date lockTime;

    @PrePersist
    protected void onCreate() {
        if (dateCreated == null) {
            dateCreated = new Date();
        }
        dateModified = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        if (dateCreated == null) {
            dateCreated = new Date();
        }
        dateModified = new Date();
    }

    // methods from the UserDetails interface from Spring Security
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
       return List.of(new SimpleGrantedAuthority(role.name()));
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        if (this.lockTime == null) {
            return true;
        }
        long lockDurationMillis = 20000; //20s in milliseconds
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - this.lockTime.getTime() > lockDurationMillis) {
            this.resetFailedLoginAttempts();
            return true;
        }
        return false;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void resetFailedLoginAttempts() {
        this.failedLoginAttempts = 0;
        this.lockTime = null;
    }

    public void incrementFailedLoginAttempts() {
        this.failedLoginAttempts++;
        if (this.failedLoginAttempts >= 3) {
            this.lockTime = new Date();
        }
    }

}
