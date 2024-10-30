package ua.agencyamazon.amazonstats.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ua.agencyamazon.amazonstats.service.SalesService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stats")
public class StatisticsRestController {

	private final SalesService salesService;

	@GetMapping("/by-date")
	public ResponseEntity<Map<String, Object>> getStatsByDate(
			@RequestParam String startDate,
			@RequestParam String endDate) {

		Map<String, Object> summary = salesService.getStatsByDate(startDate, endDate);

		return ResponseEntity.ok(summary);
	}

	@GetMapping("/by-asin")
	public ResponseEntity<Map<String, Object>> getStatsByAsin(
			@RequestParam List<String> asins) {
		Map<String, Object> summary = salesService.getStatsByAsin(asins);

		return ResponseEntity.ok(summary);
	}

	@GetMapping("/summary/dates")
	public ResponseEntity<Map<String, Object>> getSummaryByDates() {
		Map<String, Object> summary = salesService.getSummaryByDates();

		return ResponseEntity.ok(summary);
	}

	@GetMapping("/summary/asins")
	public ResponseEntity<Map<String, Object>> getSummaryByAsins() {
		Map<String, Object> summary = salesService.getSummaryByAsins();

		return ResponseEntity.ok(summary);
	}
}
