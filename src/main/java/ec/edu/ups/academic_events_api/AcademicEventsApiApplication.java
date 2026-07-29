package ec.edu.ups.academic_events_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import ec.edu.ups.academic_events_api.security.config.JwtProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class AcademicEventsApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(AcademicEventsApiApplication.class, args);
	}

}
