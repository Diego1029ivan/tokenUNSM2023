package com.example.demo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import static org.springframework.security.config.Customizer.withDefaults;

import com.example.demo.security.jwt.JwtFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	@Autowired
	private CustomerDetailsService customerDetailsService;
	
	@Autowired
	private JwtFilter jwtFilter;
	
	@Bean
	public PasswordEncoder passwordEnconder() {
		return NoOpPasswordEncoder.getInstance();
	}
	
	@Bean
	protected SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception { //acceso a rutas para autentifiación
	
		httpSecurity
			.cors(withDefaults())
			.csrf((config)-> config.disable())
			.authorizeHttpRequests((auth) ->
	        auth
	                .requestMatchers("/user/login","/user/signup","/user/forgotPassword").permitAll()
	                .anyRequest().authenticated()
			)
			.exceptionHandling((exceptionHandling) -> //página de redirección de acceso denegado
				exceptionHandling
					.accessDeniedPage("/user/login")
			)
			.sessionManagement((session)->
			session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			);
			
			
			
		httpSecurity.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
		
			return httpSecurity.build();
		
	}
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
		return authenticationConfiguration.getAuthenticationManager();
	}
}
