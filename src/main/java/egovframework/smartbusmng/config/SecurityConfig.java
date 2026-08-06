package egovframework.smartbusmng.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import egovframework.smartbusmng.auth.jwt.UserActivityTrackingFilter;
import egovframework.smartbusmng.auth.security.CustomAuthenticationFailureHandler;
import egovframework.smartbusmng.auth.security.CustomAuthenticationProvider;
import egovframework.smartbusmng.auth.security.CustomLoginSuccessHandler;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           CustomAuthenticationProvider provider,
                                           CustomLoginSuccessHandler successHandler,
                                           CustomAuthenticationFailureHandler failureHandler,
                                           UserActivityTrackingFilter trackingFilter)
            throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfig()))
//                .csrf(csrf -> csrf
//                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
//                    )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/login", "/api/register", "/api/checkEmail", "/api/checkGroupKey", "/api/login/groups").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**","/swagger-resources/**","/webjars/**").permitAll() // 나중에 swagger 지울 때 이곳 활성화
                        .requestMatchers("/api/notice/**").permitAll() //나중에 swagger 지울 때 이곳 활성화
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
//                .formLogin(AbstractHttpConfigurer::disable)   나중에 swagger 지울 때 이곳 활성화
                .formLogin(form -> form
                        .loginPage("/api/login")
                        .loginProcessingUrl("/api/login")
                        .usernameParameter("userEmail")
                        .passwordParameter("password")
                        .successHandler(successHandler)
                        .failureHandler(failureHandler)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/api/logout")
//                        .logoutSuccessUrl("/api/login?logout")
                        .logoutSuccessHandler((req, res, auth) -> res.setStatus(200))
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID", "XSRF-TOKEN")
                )
                .sessionManagement(session ->
                        session
                                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                                .maximumSessions(1)
                                .maxSessionsPreventsLogin(false)
                )
                .authenticationProvider(provider)
                .addFilterAfter(trackingFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfig() {
        CorsConfiguration c = new CorsConfiguration();
//        c.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
//        c.setAllowedHeaders(List.of("Content-Type", "X-XSRF-TOKEN", "X-Requested-With"));
//        c.setExposedHeaders(List.of("Set-Cookie"));
        c.setAllowedOriginPatterns(List.of(
		  "http://localhost:5173",
		  "http://10.10.10.77:5173",
		  "http://10.10.10.*:5173"
		));
        c.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
        c.setAllowedHeaders(List.of("*"));
        c.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource s = new UrlBasedCorsConfigurationSource();
        s.registerCorsConfiguration("/**", c);
        return s;
    }
}
