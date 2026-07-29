package ec.edu.ups.academic_events_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import ec.edu.ups.academic_events_api.security.config.CorsProperties;
import ec.edu.ups.academic_events_api.security.config.JwtProperties;
import ec.edu.ups.academic_events_api.security.config.LoginSecurityProperties;
import ec.edu.ups.academic_events_api.security.config.SwaggerSecurityProperties;

@SpringBootApplication
@EnableConfigurationProperties({ JwtProperties.class, LoginSecurityProperties.class, CorsProperties.class,
		SwaggerSecurityProperties.class })
public class AcademicEventsApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(AcademicEventsApiApplication.class, args);
	}

}
