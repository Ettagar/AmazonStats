package ua.agencyamazon.amazonstats.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;

import jakarta.annotation.PostConstruct;

@Configuration
public class MongoIndexConfig {
	private final MongoTemplate mongoTemplate;

	public MongoIndexConfig(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	@PostConstruct
	public void initIndexes() {
		mongoTemplate.indexOps("SalesAndTrafficByDate")
		.ensureIndex(new Index().on("date", Sort.Direction.ASC).unique());
	}
}
