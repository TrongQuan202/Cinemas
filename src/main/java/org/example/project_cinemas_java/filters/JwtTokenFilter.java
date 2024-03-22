package org.example.project_cinemas_java.filters;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.example.project_cinemas_java.components.JwtTokenUtils;
import org.example.project_cinemas_java.model.User;
import org.example.project_cinemas_java.repository.RefreshTokenRepo;
import org.example.project_cinemas_java.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter{
    @Value("${api.prefix}")
    private String apiPrefix;
    @Value("${jwt.expiration}")
    private int expiration;

    private final UserDetailsService userDetailsService;
    private final JwtTokenUtils jwtTokenUtil;
    @Autowired
    private UserRepo userRepo;

    @Override
    protected void doFilterInternal(@NonNull  HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        try {
            if(isBypassToken(request)) {
                filterChain.doFilter(request, response); //enable bypass
                return;
            }
            final String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                return;
            }
            final String token = authHeader.substring(7);
            final String email = jwtTokenUtil.extractEmail(token);

            User user = userRepo.findByEmail(email).orElse(null);
            if (email != null
                    && SecurityContextHolder.getContext().getAuthentication() == null) {
                User userDetails = (User) userDetailsService.loadUserByUsername(email);
                if(jwtTokenUtil.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                }
            }
            filterChain.doFilter(request, response); //enable bypass
        }catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        }
    }
    private boolean isBypassToken(@NonNull  HttpServletRequest request) {

        final List<Pair<String, String>> bypassTokens = Arrays.asList(
                Pair.of(String.format("%s/roles", apiPrefix), "GET"),
//                Pair.of(String.format("%s/products", apiPrefix), "GET"),
//                Pair.of(String.format("%s/categories", apiPrefix), "GET"),
                Pair.of(String.format("%s/auth/register", apiPrefix), "POST"),
                Pair.of(String.format("%s/auth/login", apiPrefix), "POST"),
                Pair.of(String.format("%s/auth/confirm-register", apiPrefix), "POST"),
                Pair.of(String.format("%s/auth/forgot-password", apiPrefix), "POST"),
                Pair.of(String.format("%s/auth/confirm-new-password", apiPrefix), "PUT"),
                Pair.of(String.format("%s/movie/top-movies", apiPrefix), "GET"),
                Pair.of(String.format("%s/cinema/get-cinema-by-address", apiPrefix), "GET"),
                Pair.of(String.format("%s/schedule/get-schedule-by-movie", apiPrefix), "GET"),
                Pair.of(String.format("%s/schedule/get-schedule-by-day-and-movie", apiPrefix), "GET"),
                Pair.of(String.format("%s/seat/get-all-seat-by-room", apiPrefix), "GET")
//                Pair.of(String.format("%s/seat/update-seat-status", apiPrefix), "PUT")
        );
        for(Pair<String, String> bypassToken: bypassTokens) {
            if (request.getServletPath().contains(bypassToken.getFirst()) &&
                    request.getMethod().equals(bypassToken.getSecond())) {
                return true;
            }
        }
        return false;
    }
}
