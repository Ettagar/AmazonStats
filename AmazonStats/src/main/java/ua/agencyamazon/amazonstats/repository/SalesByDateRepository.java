package ua.agencyamazon.amazonstats.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import ua.agencyamazon.amazonstats.model.document.SalesAndTrafficByDate;

@Repository
public interface SalesByDateRepository extends MongoRepository<SalesAndTrafficByDate, String> {

	List<SalesAndTrafficByDate> findByDateBetween(Date startDate, Date endDate);
}
