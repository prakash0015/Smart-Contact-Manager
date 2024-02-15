package com.contanctmanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.authorizeHttpRequests(requests -> requests
				.requestMatchers("/user/**").authenticated()
				.anyRequest().permitAll()
			)
			.formLogin(login -> login.loginPage("/signin").loginProcessingUrl("/doLogin").defaultSuccessUrl("/signin"))
			// .formLogin(form -> form.loginPage("/signin").permitAll())
			.logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/signin?logout").permitAll()).csrf(scrf -> scrf.disable());

		return http.build();
	}

	@Bean
	BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	// @Bean
	// UserDetailsService userDetailsService(DataSource dataSource) {
	// 	return new JdbcUserDetailsManager(dataSource);
	// }

}