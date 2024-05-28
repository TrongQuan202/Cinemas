package org.example.project_cinemas_java.configurations;


import lombok.RequiredArgsConstructor;
import org.example.project_cinemas_java.filters.JwtTokenFilter;
import org.example.project_cinemas_java.model.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Arrays;
import java.util.List;

import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableWebSecurity
@EnableWebMvc
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final JwtTokenFilter jwtTokenFilter;
    @Value("${api.prefix}")
    private String apiPrefix;
    @Bean
    //Pair.of(String.format("%s/products", apiPrefix), "GET"),
    public SecurityFilterChain securityFilterChain(HttpSecurity http)  throws Exception{
        http

                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(requests -> {
                    requests
                            .requestMatchers(
                                    String.format("%s/auth/register", apiPrefix),
                                    String.format("%s/auth/login", apiPrefix),
                                    String.format("%s/auth/confirmEmail", apiPrefix),
                                    String.format("%s/auth/forgot-password", apiPrefix),
                                    String.format("%s/auth/confirm-new-password", apiPrefix),
                                    String.format("%s/movie/get-all-movie-by-cinema", apiPrefix),
                                    String.format("%s/movie/get-movie-detail", apiPrefix),
                                    String.format("%s/movie/get-movie-by-schedule", apiPrefix),
                                    String.format("%s/movie/get-all-movie-suggest", apiPrefix),
                                    String.format("%s/cinema/get-cinema-by-address", apiPrefix),
                                    String.format("%s/promotion/get-promotions", apiPrefix),
                                    String.format("%s/promotion/get-promotion-detail", apiPrefix),
                                    String.format("%s/schedule/get-schedule-by-movie", apiPrefix),
                                    String.format("%s/schedule/get-schedule-by-day-and-movie", apiPrefix),
                                    String.format("%s/schedule/get-all-schedule-by-movie", apiPrefix),
                                    String.format("%s/seat/get-all-seat-by-room", apiPrefix),
                                    String.format("%s/seat/update-seatStatus", apiPrefix),
                                    String.format("%s/seat/get-all-seat", apiPrefix),
                                    String.format("%s/auth/refresh-token", apiPrefix),
                                    String.format("%s/actor/get-all-actor-name", apiPrefix),
                                    String.format("%s/cinema/get-all-cinema-name", apiPrefix),
                                    String.format("%s/movie/get-movie-type-name", apiPrefix),
                                    String.format("%s/movie/get-movie", apiPrefix),
                                    String.format("%s/movie/get-all-movie-schedule-by-admin", apiPrefix),
                                    String.format("%s/room/get-all-room-schedule-by-admin", apiPrefix),
                                    String.format("%s/blog/get-all-blog", apiPrefix),
                                      "/check/**",
//                                      "/submitOrder/**"
                                      "/vnpay-payment/**",
                                    "/booking/**")

//                                    String.format("%s/seat/update-seat-status", apiPrefix)
                            .permitAll()

                            .requestMatchers(POST, String.format("/%s/auth/refreshtoken",apiPrefix)).hasAnyRole(Role.USER,Role.ADMIN)


                            .requestMatchers(POST, String.format("/%s/cinema/create-cinema",apiPrefix)).hasRole("ADMIN")
                            .requestMatchers(POST, String.format("/%s/room/create-room",apiPrefix)).hasRole("ADMIN")
                            .requestMatchers(POST, String.format("/%s/seat/create-seat",apiPrefix)).hasRole("ADMIN")
                            .requestMatchers(POST, String.format("/%s/food/create-food",apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(POST, String.format("/%s/movie/create-movie",apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(POST, String.format("/%s/bill/create-bill",apiPrefix)).hasAnyRole(Role.USER,Role.ADMIN)
                            .requestMatchers(POST, String.format("/%s/payment/submitOrder",apiPrefix)).hasAnyRole(Role.USER,Role.ADMIN)
                            .requestMatchers(POST, String.format("/%s/billFood/create-billFood",apiPrefix)).hasAnyRole(Role.USER,Role.ADMIN)
                            .requestMatchers(POST, String.format("/%s/actor/create-actor",apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(POST, String.format("/%s/schedule/create-schedule",apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(POST, String.format("/%s/blog/create-blog",apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(POST, String.format("/%s/user/confirm-author",apiPrefix)).hasAnyRole(Role.USER,Role.ADMIN)
                            .requestMatchers(POST, String.format("/submitOrder")).hasRole(Role.USER)
                            .requestMatchers(POST, String.format("/%s/promotion/create-promotion",apiPrefix)).hasRole(Role.ADMIN)


                            .requestMatchers(PUT, String.format("/%s/cinema/update-cinema",apiPrefix)).hasRole("ADMIN")
                            .requestMatchers(PUT, String.format("/%s/room/update-room",apiPrefix)).hasRole("ADMIN")
                            .requestMatchers(PUT, String.format("/%s/seat/update-seat",apiPrefix)).hasRole("ADMIN")
                            .requestMatchers(PUT, String.format("/%s/movie/update-movie",apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(PUT, String.format("/%s/billFood/remove-billFood",apiPrefix)).hasAnyRole(Role.USER,Role.ADMIN)
                            .requestMatchers(PUT, String.format("/%s/auth/change-password",apiPrefix)).hasAnyRole(Role.USER,Role.ADMIN)
                            .requestMatchers(PUT, String.format("/%s/movie/stop-show-movie",apiPrefix)).hasRole(Role.ADMIN)

                            .requestMatchers(DELETE, String.format("/%s/cinema/delete-cinema",apiPrefix)).hasRole("ADMIN")
                            .requestMatchers(PUT, String.format("/%s/seat/update-seat-status",apiPrefix)).hasAnyRole(Role.USER,Role.ADMIN)
                            .requestMatchers(PUT, String.format("/%s/seat/reset-seat-status",apiPrefix)).hasAnyRole(Role.USER,Role.ADMIN)
                            .requestMatchers(PUT, String.format("/%s/seat/reset-seat-by-user",apiPrefix)).hasAnyRole(Role.USER,Role.ADMIN)
                            .requestMatchers(PUT, String.format("/%s/seat/reset-seat-status-by-user",apiPrefix)).hasAnyRole(Role.USER,Role.ADMIN)
                            .requestMatchers(PUT, String.format("/%s/billFood/update-billFood",apiPrefix)).hasAnyRole(Role.USER,Role.ADMIN)
                            .requestMatchers(PUT, String.format("/%s/food/update-food",apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(PUT, String.format("/%s/schedule/delete-schedule",apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(PUT, String.format("/%s/bill/delete-bill",apiPrefix)).hasRole(Role.ADMIN)

                            .requestMatchers(GET, String.format("/%s/schedule/get-price-by-schedule",apiPrefix)).hasAnyRole(Role.USER,Role.ADMIN)
                            .requestMatchers(GET, String.format("/%s/user/get-phone-user",apiPrefix)).hasAnyRole(Role.USER,Role.ADMIN)
                            .requestMatchers(GET, String.format("/%s/food/get-all-food",apiPrefix)).hasAnyRole(Role.USER,Role.ADMIN)
                            .requestMatchers(GET, String.format("/%s/food/get-all-food-admin",apiPrefix)).hasAnyRole(Role.USER,Role.ADMIN)
                            .requestMatchers(GET, String.format("/%s/promotion/get-all-promotion-by-user",apiPrefix)).hasAnyRole(Role.USER,Role.ADMIN)
                            .requestMatchers(GET, String.format("/%s/rank/get-rank-by-user",apiPrefix)).hasAnyRole(Role.USER,Role.ADMIN)
                            .requestMatchers(PUT, String.format("/%s/promotion/get-discount-amount",apiPrefix)).hasAnyRole(Role.USER,Role.ADMIN)
                            .requestMatchers(GET, String.format("/%s/payment/vnpay-payment",apiPrefix)).hasAnyRole(Role.USER,Role.ADMIN)
                            .requestMatchers(GET, String.format("/%s/cinema/get-Revenue",apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(GET, String.format("/%s/movie/get-all-movie-by-admin",apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(GET, String.format("/%s/schedule/get-all-schedule-by-admin",apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(GET, String.format("/%s/user/get-all-user-by-admin",apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(GET, String.format("/%s/cinema/get-all-revenue",apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(GET, String.format("/%s/food/get-food-by-admin",apiPrefix)).hasAnyRole(Role.ADMIN,Role.USER)
                            .requestMatchers(GET, String.format("/%s/bill/get-all-bill",apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(GET, String.format("/%s/bill/get-totalMoney",apiPrefix)).hasRole(Role.USER)
                            .requestMatchers(GET, String.format("/%s/bill/get-history-bill-by-user",apiPrefix)).hasRole(Role.USER)
                            .requestMatchers(GET, String.format("/%s/user/get-profile-user",apiPrefix)).hasRole(Role.USER)
                            .requestMatchers(GET, String.format("/%s/blog/get-blog-detail",apiPrefix)).hasRole(Role.USER)
                            .requestMatchers(GET, String.format("/%s/promotion/get-all-promotion-by-admin",apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(GET, String.format("/%s/user/get-point",apiPrefix)).hasAnyRole(Role.ADMIN,Role.USER)
                            .anyRequest().authenticated();
                    //.anyRequest().permitAll();

                })
                .csrf(AbstractHttpConfigurer::disable);
//                .headers(headers -> headers
//                                .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin
//                                )
//                        );
        http.cors(new Customizer<CorsConfigurer<HttpSecurity>>() {
            @Override
            public void customize(CorsConfigurer<HttpSecurity> httpSecurityCorsConfigurer) {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOrigins(List.of("http://localhost:3000","http://localhost:3001","https://spacecinema-wheat.vercel.app","https://cinema-admin-one.vercel.app"));
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
                configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-auth-token"));
                configuration.setExposedHeaders(List.of("x-auth-token"));
                configuration.setAllowCredentials(true);
                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                httpSecurityCorsConfigurer.configurationSource(source);
            }
        });

        return http.build();
    }
}
