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

package com.smoothstack.userservice.config;

import com.smoothstack.userservice.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
/**
 * Configuration class for application security and authentication.
 */
@Configuration
@RequiredArgsConstructor

public class ApplicationConfig {

    private final AppUserRepository repository;

    /**
     * Provides the user details service bean.
     *
     * @return the user details service
     * @throws UsernameNotFoundException if the user is not found
     */

    @Bean
    public UserDetailsService userDetailsService() {
        return appUsername -> repository.findByUsername(appUsername)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    /**
     * Provides the authentication provider bean.
     *
     * @return the authentication provider
     */

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }//This method defines a bean responsible for providing authentication.
    // It uses a DaoAuthenticationProvider and configures it with the userDetailsService() bean and a PasswordEncoder (defined in the next method).

    /**
     * Provides the authentication manager bean.
     *
     * @param config the authentication configuration
     * @return the authentication manager
     * @throws Exception if an error occurs
     */

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }//This method defines a bean for the AuthenticationManager. It takes an AuthenticationConfiguration as an argument and retrieves the AuthenticationManager from it.
    // The AuthenticationManager is a key component of Spring Security responsible for handling authentication requests.

    /**
     * Provides the password encoder bean.
     *
     * @return the password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
