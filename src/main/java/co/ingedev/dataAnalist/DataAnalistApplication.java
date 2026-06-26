package co.ingedev.dataAnalist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class DataAnalistApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataAnalistApplication.class, args);
	}

}
