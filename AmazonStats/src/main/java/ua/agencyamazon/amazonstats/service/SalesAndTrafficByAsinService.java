package ua.agencyamazon.amazonstats.service;

import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ua.agencyamazon.amazonstats.model.document.SalesAndTrafficByAsin;
import ua.agencyamazon.amazonstats.repository.SalesByAsinRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class SalesAndTrafficByAsinService {

	private final SalesByAsinRepository salesByAsinRepository;

	@Cacheable(value = "salesByAsinCache", key = "#parentAsin")
	public Optional<SalesAndTrafficByAsin> findByParentAsin(String parentAsin) {
		return salesByAsinRepository.findByParentAsin(parentAsin);
	}

	@Transactional
	public void updateSalesAndTrafficByAsin(List<SalesAndTrafficByAsin> salesAndTrafficByAsinList) {
		salesAndTrafficByAsinList.forEach(newData -> {
			Optional<SalesAndTrafficByAsin> existingDataOpt = findByParentAsin(newData.getParentAsin());

			if (existingDataOpt.isPresent()) {
				SalesAndTrafficByAsin existingData = existingDataOpt.get();

				if (!existingData.equals(newData)) {
					updateAndCache(existingData, newData);
				} else {
					log.info("No changes detected for parentAsin: {}", newData.getParentAsin());
				}
			} else {
				insertAndCacheNewData(newData);
			}
		});
	}

	@CacheEvict(value = "salesByAsinCache", key = "#parentAsin")
	public void evictCacheByAsin(String parentAsin) {
		log.info("Cache evicted for parentAsin: {}", parentAsin);
	}

	@CachePut(value = "salesByAsinCache", key = "#newData.parentAsin")
	public SalesAndTrafficByAsin updateAndCache(SalesAndTrafficByAsin existingData, SalesAndTrafficByAsin newData) {
		existingData.setSalesByAsin(newData.getSalesByAsin());
		existingData.setTrafficByAsin(newData.getTrafficByAsin());
		existingData.setSku(newData.getSku());
		salesByAsinRepository.save(existingData);

		log.info("Updated SalesAndTrafficByAsin and cache for parentAsin: {}", newData.getParentAsin());
		return existingData;
	}

	@CachePut(value = "salesByAsinCache", key = "#newData.parentAsin")
	public SalesAndTrafficByAsin insertAndCacheNewData(SalesAndTrafficByAsin newData) {
		salesByAsinRepository.save(newData);
		log.info("Inserted new SalesAndTrafficByAsin and cached for parentAsin: {}", newData.getParentAsin());

		return newData;
	}
}

