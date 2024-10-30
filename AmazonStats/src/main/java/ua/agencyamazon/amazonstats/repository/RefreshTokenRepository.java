package ua.agencyamazon.amazonstats.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import ua.agencyamazon.amazonstats.model.document.RefreshToken;

@Repository
public interface RefreshTokenRepository extends MongoRepository<RefreshToken, String> {
	void deleteByOwnerId(ObjectId id);
	
	default void deleteByOwnerId(String id) {
		deleteByOwnerId(new ObjectId(id));
	}
}
