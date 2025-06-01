package com.example.looksy_ytta.config;

import com.example.looksy_ytta.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler; // Pastikan ini terimport

import java.util.List; // Pastikan List terimport jika digunakan untuk SimpleGrantedAuthority


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final UserRepository userRepository;
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler; 

    // Modifikasi konstruktor untuk menerima CustomAuthenticationSuccessHandler
    public SecurityConfig(UserRepository userRepository, CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler) {
        this.userRepository = userRepository;
        this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByUsername(username)
                .map(user -> new org.springframework.security.core.userdetails.User(
                        user.getUsername(),
                        user.getPassword(),
                        List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
                ))
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Bean DaoAuthenticationProvider ini opsional di Spring Security 6+
    // Spring akan otomatis mendeteksi UserDetailsService dan PasswordEncoder sebagai Bean
    /*
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    */

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable) // Pertimbangkan untuk mengaktifkannya dan mengelola token CSRF untuk form
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/api/auth/**", "/register", "/login", "/").permitAll() // Halaman yang bisa diakses tanpa otentikasi
                .requestMatchers("/admin/**").hasRole("ADMIN") // Hanya admin yang bisa akses
                .requestMatchers("/user/**").hasRole("USER")   // Hanya user biasa yang bisa akses
                .anyRequest().authenticated() // Semua request lain harus terotentikasi
            )
            .formLogin(form -> form
                .loginPage("/login") // Halaman login kustom Anda
                // Hapus baris .defaultSuccessUrl("/dashboard", true)
                .successHandler(customAuthenticationSuccessHandler) // <--- GANTI dengan custom handler di sini
                .failureUrl("/login?error=true") // Redirect jika login gagal
                .permitAll() // Izinkan semua untuk mengakses form login
            )
            .logout(logout -> logout
                .logoutUrl("/logout") // URL untuk proses logout
                .logoutSuccessUrl("/login?logout=true") // Redirect setelah logout berhasil
                .permitAll() // Izinkan semua untuk mengakses logout
            );

        return http.build();
    }
}
