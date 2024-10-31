package ua.agencyamazon.amazonstats.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import ua.agencyamazon.amazonstats.model.document.SalesAndTrafficByAsin;

@Repository
public interface SalesByAsinRepository extends MongoRepository<SalesAndTrafficByAsin, String> {

	Optional<SalesAndTrafficByAsin> findByParentAsin(String parentAsin);

	List<SalesAndTrafficByAsin> findByParentAsinIn(List<String> asins);
}
