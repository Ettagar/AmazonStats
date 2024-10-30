package ua.agencyamazon.amazonstats.service;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ua.agencyamazon.amazonstats.model.SalesAndTrafficReport;
import ua.agencyamazon.amazonstats.repository.SalesByAsinRepository;
import ua.agencyamazon.amazonstats.repository.SalesByDateRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduledUpdateService {
	private final SalesByDateRepository salesByDateRepository;
    private final SalesByAsinRepository salesByAsinRepository;
    private final ObjectMapper objectMapper;

    @Value("${file.path.report}")
    private String reportFilePath;
    
    @Scheduled(fixedRateString = "${scheduled.refreshRate}")
    public void updateStatistics() {
        try {
            File file = new File(reportFilePath);
            SalesAndTrafficReport report = objectMapper.readValue(file, SalesAndTrafficReport.class);

            salesByDateRepository.saveAll(report.getSalesAndTrafficByDate());
            salesByAsinRepository.saveAll(report.getSalesAndTrafficByAsin());

        } catch (IOException e) {
            log.error("Error updating statistics from file: {}", reportFilePath, e);
        }
    }
}

