package ua.agencyamazon.amazonstats;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class AmazonStatsApplication {

	public static void main(String[] args) {
		SpringApplication.run(AmazonStatsApplication.class, args);
	}

}
