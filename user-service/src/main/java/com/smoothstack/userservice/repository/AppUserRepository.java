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

package com.smoothstack.userservice.repository;
import com.smoothstack.userservice.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Integer>, JpaSpecificationExecutor<AppUser> {

    Optional<AppUser> findByUsername(String username);
    Optional<AppUser> findByEmail(String email);
}//JpaRepository provides built-in methods for basic CRUD (Create, Read, Update, Delete) operations on the AppUser entity.
// These methods include save, findById, findAll, delete, and more.
// You can use these methods to interact with the database without writing custom SQL queries.

//JpaSpecificationExecutor allows you to build and execute dynamic queries
// on JPA entity without writing SQL queries manually.