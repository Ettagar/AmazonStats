package ua.agencyamazon.amazonstats.service;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ua.agencyamazon.amazonstats.model.document.SalesAndTrafficByDate;
import ua.agencyamazon.amazonstats.repository.SalesByDateRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class SalesAndTrafficByDateService {
	private final SalesByDateRepository salesByDateRepository;

	public void updateSalesAndTrafficByDate(List<SalesAndTrafficByDate> salesAndTrafficByDateList) {
		salesAndTrafficByDateList.forEach(newData -> {
			List<SalesAndTrafficByDate> existingDataList = salesByDateRepository.findByDate(newData.getDate());

			if (!existingDataList.isEmpty()) {
				SalesAndTrafficByDate existingData = existingDataList.get(0);

				if (hasChanges(existingData, newData)) {
					existingData.setSalesByDate(newData.getSalesByDate());
					existingData.setTrafficByDate(newData.getTrafficByDate());
					salesByDateRepository.save(existingData);
					log.info("Updated SalesAndTrafficByDate for date: {}", newData.getDate());
				} else {
					log.info("No changes detected for date: {}", newData.getDate());
				}

				if (existingDataList.size() > 1) {
					existingDataList.stream().skip(1).forEach(salesByDateRepository::delete);
					log.warn("Removed {} duplicate entries for date: {}", existingDataList.size() - 1, newData.getDate());
				}
			} else {

				salesByDateRepository.save(newData);
				log.info("Inserted new SalesAndTrafficByDate for date: {}", newData.getDate());
			}
		});
	}

	private boolean hasChanges(SalesAndTrafficByDate existingData, SalesAndTrafficByDate newData) {
		return Stream.of(
				(Supplier<Boolean>) () -> !Objects.equals(existingData.getDate(),
						newData.getDate()),
				(Supplier<Boolean>) () -> !Objects.equals(existingData.getSalesByDate().getOrderedProductSales(),
						newData.getSalesByDate().getOrderedProductSales()),
				(Supplier<Boolean>) () -> !Objects.equals(existingData.getSalesByDate().getUnitsOrdered(),
						newData.getSalesByDate().getUnitsOrdered()),
				(Supplier<Boolean>) () -> !Objects.equals(existingData.getTrafficByDate().getBrowserPageViews(),
						newData.getTrafficByDate().getBrowserPageViews()),
				(Supplier<Boolean>) () -> !Objects.equals(existingData.getTrafficByDate().getPageViews(),
						newData.getTrafficByDate().getPageViews())
				).anyMatch(Supplier::get);
	}
}
