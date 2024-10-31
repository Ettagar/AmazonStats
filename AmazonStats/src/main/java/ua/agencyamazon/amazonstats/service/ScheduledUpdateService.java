package ua.agencyamazon.amazonstats.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ua.agencyamazon.amazonstats.model.SalesAndTrafficReport;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduledUpdateService {
	private final SalesAndTrafficByDateService salesAndTrafficByDateService;
	private final SalesAndTrafficByAsinService salesAndTrafficByAsinService;
	private final ObjectMapper objectMapper;

	@Value("${file.path.report}")
	private String reportFilePath;

	@EventListener(ApplicationReadyEvent.class)
	public void onApplicationStartup() {
		updateStatistics();
	}

	@Scheduled(fixedRateString = "${scheduled.refreshRate}")
	public void updateStatistics() {
		File externalFile = new File(reportFilePath);
		if (!externalFile.exists()) {
			log.warn("File not found: {}. Skipping update.", reportFilePath);
			return;
		}

		try (InputStream inputStream = new FileInputStream(externalFile)) {
			SalesAndTrafficReport report = objectMapper.readValue(inputStream, SalesAndTrafficReport.class);

			salesAndTrafficByDateService.updateSalesAndTrafficByDate(report.getSalesAndTrafficByDate());
			salesAndTrafficByAsinService.updateSalesAndTrafficByAsin(report.getSalesAndTrafficByAsin());

			log.info("Database update completed successfully from {}", reportFilePath);

		} catch (IOException e) {
			log.error("Error updating statistics from file: {}", reportFilePath, e);
		}
	}
}
