package ua.agencyamazon.amazonstats.service;

import java.util.Date;
import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ua.agencyamazon.amazonstats.model.document.SalesAndTrafficByDate;
import ua.agencyamazon.amazonstats.repository.SalesByDateRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class SalesAndTrafficByDateService {

	private final SalesByDateRepository salesByDateRepository;

	@Cacheable(value = "salesByDateCache", key = "#date")
	public List<SalesAndTrafficByDate> findByDate(Date date) {
		return salesByDateRepository.findByDate(date);
	}

	@Transactional
	public void updateSalesAndTrafficByDate(List<SalesAndTrafficByDate> salesAndTrafficByDateList) {
		salesAndTrafficByDateList.forEach(newData -> {
			List<SalesAndTrafficByDate> existingDataList = findByDate(newData.getDate());

			if (!existingDataList.isEmpty()) {
				SalesAndTrafficByDate existingData = existingDataList.get(0);

				if (!existingData.equals(newData)) {
					existingData.setSalesByDate(newData.getSalesByDate());
					existingData.setTrafficByDate(newData.getTrafficByDate());
					saveAndUpdateCache(existingData);
				} else {
					log.info("No changes detected for date: {}", newData.getDate());
				}
				removeDuplicates(existingDataList);
			} else {
				insertAndCacheNewData(newData);
			}
		});
	}

	@CacheEvict(value = "salesByDateCache", key = "#date")
	public void evictSalesByDateCache(Date date) {
		log.info("Cache evicted for date: {}", date);
	}

	@CachePut(value = "salesByDateCache", key = "#salesAndTrafficByDate.date")
	public SalesAndTrafficByDate saveAndUpdateCache(SalesAndTrafficByDate salesAndTrafficByDate) {
		salesByDateRepository.save(salesAndTrafficByDate);
		log.info("Updated SalesAndTrafficByDate and cache for date: {}", salesAndTrafficByDate.getDate());
		return salesAndTrafficByDate;
	}

	private void removeDuplicates(List<SalesAndTrafficByDate> existingDataList) {
		if (existingDataList.size() > 1) {
			existingDataList.stream().skip(1).forEach(salesByDateRepository::delete);
			log.warn("Removed {} duplicate entries", existingDataList.size() - 1);
		}
	}

	@CachePut(value = "salesByDateCache", key = "#newData.date")
	public SalesAndTrafficByDate insertAndCacheNewData(SalesAndTrafficByDate newData) {
		salesByDateRepository.save(newData);
		log.info("Inserted new SalesAndTrafficByDate and cached for date: {}", newData.getDate());
		return newData;
	}
}
