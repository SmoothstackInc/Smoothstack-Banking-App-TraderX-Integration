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

import com.smoothstack.userservice.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    //OncePerRequestFilter, is a Spring Security filter that ensures the logic inside it is executed once per request.

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;


    @Override
    protected void doFilterInternal(
           @NonNull HttpServletRequest request,
           @NonNull HttpServletResponse response,
           @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String appUserEmail;
        if (authHeader == null ||!authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }//It first checks if the incoming request has an Authorization header with a bearer token. If not, it allows the request to proceed without authentication.
        jwt = authHeader.substring(7);
        appUserEmail = jwtService.extractAppUserName(jwt);// If a token is present, the filter extracts the username from the JWT and checks if the user is already authenticated.
        if (appUserEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) { //If the user is not if the user is not authenticated
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(appUserEmail); //it retrieves user details from the database
            if (jwtService.isTokenValid(jwt, userDetails)){ // validates the token
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(//and creates an UsernamePasswordAuthenticationToken if the token is valid.
                        userDetails,
                        null,
                        userDetails.getAuthorities() //retrieves the authorities (roles) associated with the user.
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken); //sets the UsernamePasswordAuthenticationToken in the security context
            }
        }
    filterChain.doFilter(request, response);
    }
}// It ensures that only requests with valid tokens (representing authenticated users) are allowed to access protected resources.
