package ua.agencyamazon.amazonstats.service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ua.agencyamazon.amazonstats.model.data.MonetaryAmount;
import ua.agencyamazon.amazonstats.model.document.SalesAndTrafficByAsin;
import ua.agencyamazon.amazonstats.model.document.SalesAndTrafficByDate;
import ua.agencyamazon.amazonstats.repository.SalesByAsinRepository;
import ua.agencyamazon.amazonstats.repository.SalesByDateRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = { "sales" })
public class SalesService {
	private final SalesByDateRepository salesByDateRepository;
	private final SalesByAsinRepository salesByAsinRepository;

	@Cacheable(value = "statsByDate", key = "#startDate + ':' + #endDate")
	public Map<String, Object> getStatsByDate(String startDate, String endDate) {
		log.info("Cache miss - Fetching stats by date from {} to {} from database", startDate, endDate);

		Date start;
		Date end;
		try {
			start = parseDate(startDate);
			end = parseDate(endDate);
		} catch (ParseException e) {
			log.error("Date parsing failed for startDate: {} and endDate: {}", startDate, endDate, e);
			throw new IllegalArgumentException("Invalid date format. Please use yyyy-MM-dd.");
		}

		List<SalesAndTrafficByDate> salesDataList = salesByDateRepository.findByDateBetween(start, end);

		Map<String, Object> summary = new HashMap<>();

		summary.put("totalSales", sumMonetaryAmount(salesDataList, data -> data.getSalesByDate().getOrderedProductSales()));
		summary.put("totalSalesB2B", sumMonetaryAmount(salesDataList, data -> data.getSalesByDate().getOrderedProductSalesB2B()));
		summary.put("totalUnitsOrdered", sumInt(salesDataList, data -> data.getSalesByDate().getUnitsOrdered()));
		summary.put("totalUnitsOrderedB2B", sumInt(salesDataList, data -> data.getSalesByDate().getUnitsOrderedB2B()));
		summary.put("totalOrderItems", sumInt(salesDataList, data -> data.getSalesByDate().getTotalOrderItems()));
		summary.put("totalOrderItemsB2B", sumInt(salesDataList, data -> data.getSalesByDate().getTotalOrderItemsB2B()));
		summary.put("totalSessions", sumInt(salesDataList, data -> data.getTrafficByDate().getSessions()));
		summary.put("averageBuyBoxPercentage", averageDouble(salesDataList, data -> data.getTrafficByDate().getBuyBoxPercentage()));

		return summary;
	}

	@Cacheable(value = "statsByAsin", key = "#asins")
	public Map<String, Object> getStatsByAsin(List<String> asins) {
		log.info("Cache miss - Fetching stats by ASINs: {} from database", asins);

		List<SalesAndTrafficByAsin> salesDataList = salesByAsinRepository.findByParentAsinIn(asins);
		return generateSalesSummary(salesDataList);
	}

	@Cacheable("summaryByDates")
	public Map<String, Object> getSummaryByDates() {
		log.info("Cache miss - Fetching summary by dates from database");

		List<SalesAndTrafficByDate> salesDataList = salesByDateRepository.findAll();
		return generateDateSummary(salesDataList);
	}

	@Cacheable("summaryByAsins")
	public Map<String, Object> getSummaryByAsins() {
		log.info("Cache miss - Fetching summary by ASINs from database");

		List<SalesAndTrafficByAsin> salesDataList = salesByAsinRepository.findAll();
		return generateSalesSummary(salesDataList);
	}

	private Map<String, Object> generateSalesSummary(List<SalesAndTrafficByAsin> salesDataList) {
		Map<String, Object> summary = new HashMap<>();

		summary.put("totalOrderedProductSales", sumMonetaryAmount(salesDataList, data -> data.getSalesByAsin().getOrderedProductSales()));
		summary.put("totalOrderedProductSalesB2B", sumMonetaryAmount(salesDataList, data -> data.getSalesByAsin().getOrderedProductSalesB2B()));

		addAsinAggregatedMetrics(summary, salesDataList);

		return summary;
	}

	private Map<String, Object> generateDateSummary(List<SalesAndTrafficByDate> salesDataList) {
		Map<String, Object> summary = new HashMap<>();

		summary.put("totalSales", sumMonetaryAmount(salesDataList, data -> data.getSalesByDate().getOrderedProductSales()));
		summary.put("totalSalesB2B", sumMonetaryAmount(salesDataList, data -> data.getSalesByDate().getOrderedProductSalesB2B()));

		addDateAggregatedMetrics(summary, salesDataList);

		return summary;
	}

	private void addAsinAggregatedMetrics(Map<String, Object> summary, List<SalesAndTrafficByAsin> salesDataList) {
		summary.put("totalUnitsOrdered", sumInt(salesDataList, data -> data.getSalesByAsin().getUnitsOrdered()));
		summary.put("totalUnitsOrderedB2B", sumInt(salesDataList, data -> data.getSalesByAsin().getUnitsOrderedB2B()));
		summary.put("totalOrderItems", sumInt(salesDataList, data -> data.getSalesByAsin().getTotalOrderItems()));
		summary.put("totalOrderItemsB2B", sumInt(salesDataList, data -> data.getSalesByAsin().getTotalOrderItemsB2B()));
		summary.put("totalSessions", sumInt(salesDataList, data -> data.getTrafficByAsin().getSessions()));
		summary.put("totalSessionsB2B", sumInt(salesDataList, data -> data.getTrafficByAsin().getSessionsB2B()));
		summary.put("averageBuyBoxPercentage", averageDouble(salesDataList, data -> data.getTrafficByAsin().getBuyBoxPercentage()));
		summary.put("averageUnitSessionPercentage", averageDouble(salesDataList, data -> data.getTrafficByAsin().getUnitSessionPercentage()));
	}

	private void addDateAggregatedMetrics(Map<String, Object> summary, List<SalesAndTrafficByDate> salesDataList) {
		summary.put("totalUnitsOrdered", sumInt(salesDataList, data -> data.getSalesByDate().getUnitsOrdered()));
		summary.put("totalUnitsOrderedB2B", sumInt(salesDataList, data -> data.getSalesByDate().getUnitsOrderedB2B()));
		summary.put("totalOrderItems", sumInt(salesDataList, data -> data.getSalesByDate().getTotalOrderItems()));
		summary.put("totalOrderItemsB2B", sumInt(salesDataList, data -> data.getSalesByDate().getTotalOrderItemsB2B()));
		summary.put("totalSessions", sumInt(salesDataList, data -> data.getTrafficByDate().getSessions()));
		summary.put("averageBuyBoxPercentage", averageDouble(salesDataList, data -> data.getTrafficByDate().getBuyBoxPercentage()));
	}

	private <T> MonetaryAmount sumMonetaryAmount(List<T> dataList, ToMonetaryAmountFunction<T> extractor) {
		BigDecimal totalAmount = dataList.stream()
				.map(extractor::applyAsMonetaryAmount)
				.map(MonetaryAmount::getAmount)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		String currencyCode = dataList.isEmpty() ? "USD" : extractor.applyAsMonetaryAmount(dataList.get(0)).getCurrencyCode();

		return new MonetaryAmount(totalAmount, currencyCode);
	}

	private <T> int sumInt(List<T> dataList, ToIntFunction<T> extractor) {
		return dataList.stream().mapToInt(extractor).sum();
	}

	private <T> double averageDouble(List<T> dataList, ToDoubleFunction<T> extractor) {
		return dataList.stream().mapToDouble(extractor).average().orElse(0.0);
	}

	private Date parseDate(String date) throws ParseException {
		return new SimpleDateFormat("yyyy-MM-dd").parse(date);
	}

	private interface ToMonetaryAmountFunction<T> {
		MonetaryAmount applyAsMonetaryAmount(T value);
	}
}
