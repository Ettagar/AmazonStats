package ua.agencyamazon.amazonstats;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableMongoRepositories
@EnableScheduling
@EnableCaching
public class AmazonStatsApplication {

	public static void main(String[] args) {
		SpringApplication.run(AmazonStatsApplication.class, args);
	}

}
