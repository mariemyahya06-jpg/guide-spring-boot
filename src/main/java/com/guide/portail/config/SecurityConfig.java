package com.guide.portail.config;

import com.guide.portail.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

/**
 * Configuration de la securite : authentification par formulaire, hachage BCrypt
 * des mots de passe, et protection des routes selon le role
 * (/admin -> ADMIN, /mentor -> MENTOR, /etudiant -> ETUDIANT).
 * Chaque utilisateur est redirige vers son tableau de bord apres connexion.
 */
@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .userDetailsService(userDetailsService)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login", "/register", "/css/**", "/js/**", "/images/**",
                                 "/webjars/**", "/h2-console/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/mentor/**").hasAnyRole("MENTOR", "ADMIN")
                .requestMatchers("/etudiant/**").hasRole("ETUDIANT")
                .requestMatchers("/catalogue/**", "/ressource/**").authenticated()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler(successHandler())
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            // Pour la console H2 (profil de demonstration uniquement)
            .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
            .headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }

    /** Redirige chaque utilisateur vers son tableau de bord selon son role. */
    @Bean
    public SimpleUrlAuthenticationSuccessHandler successHandler() {
        SimpleUrlAuthenticationSuccessHandler handler = new SimpleUrlAuthenticationSuccessHandler();
        handler.setDefaultTargetUrl("/dashboard");
        return handler;
    }
}
