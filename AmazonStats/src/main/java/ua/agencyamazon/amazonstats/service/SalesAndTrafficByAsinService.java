package ua.agencyamazon.amazonstats.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ua.agencyamazon.amazonstats.model.document.SalesAndTrafficByAsin;
import ua.agencyamazon.amazonstats.repository.SalesByAsinRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class SalesAndTrafficByAsinService {
	private final SalesByAsinRepository salesByAsinRepository;

	public void updateSalesAndTrafficByAsin(List<SalesAndTrafficByAsin> salesAndTrafficByAsinList) {
		salesAndTrafficByAsinList.forEach(newData -> {
			Optional<SalesAndTrafficByAsin> existingDataOpt = salesByAsinRepository.findByParentAsin(newData.getParentAsin());

			if (existingDataOpt.isPresent()) {
				SalesAndTrafficByAsin existingData = existingDataOpt.get();

				if (hasChanges(existingData, newData)) {
					existingData.setSalesByAsin(newData.getSalesByAsin());
					existingData.setTrafficByAsin(newData.getTrafficByAsin());
					existingData.setSku(newData.getSku());
					salesByAsinRepository.save(existingData);
					log.info("Updated SalesAndTrafficByAsin for parentAsin: {}", newData.getParentAsin());
				} else {
					log.info("No changes detected for parentAsin: {}", newData.getParentAsin());
				}
			} else {
				salesByAsinRepository.save(newData);
				log.info("Inserted new SalesAndTrafficByAsin for parentAsin: {}", newData.getParentAsin());
			}
		});
	}

	private boolean hasChanges(SalesAndTrafficByAsin existingData, SalesAndTrafficByAsin newData) {
		return Stream.of(
				(Supplier<Boolean>) () -> !Objects.equals(existingData.getParentAsin(),
						newData.getParentAsin()),
				(Supplier<Boolean>) () -> !Objects.equals(existingData.getSku(),
						newData.getSku()),
				(Supplier<Boolean>) () -> !Objects.equals(existingData.getSalesByAsin().getOrderedProductSales(),
						newData.getSalesByAsin().getOrderedProductSales()),
				(Supplier<Boolean>) () -> !Objects.equals(existingData.getSalesByAsin().getUnitsOrdered(),
						newData.getSalesByAsin().getUnitsOrdered()),
				(Supplier<Boolean>) () -> !Objects.equals(existingData.getTrafficByAsin().getBrowserPageViews(),
						newData.getTrafficByAsin().getBrowserPageViews()),
				(Supplier<Boolean>) () -> !Objects.equals(existingData.getTrafficByAsin().getPageViews(),
						newData.getTrafficByAsin().getPageViews())
				).anyMatch(Supplier::get);
	}
}
