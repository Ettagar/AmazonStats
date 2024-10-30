package ua.agencyamazon.amazonstats.service;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.RequiredArgsConstructor;
import ua.agencyamazon.amazonstats.model.SalesAndTrafficReport;
import ua.agencyamazon.amazonstats.repository.SalesByAsinRepository;
import ua.agencyamazon.amazonstats.repository.SalesByDateRepository;

@Service
@RequiredArgsConstructor
public class DataInitializationService {
	private final SalesByDateRepository salesByDateRepository;
	private final SalesByAsinRepository salesByAsinRepository;
	
	@Value("${file.path.report}")
	private String reportFilePath;

	@EventListener(ApplicationReadyEvent.class)
	public void loadInitialData() throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		
		File file = new ClassPathResource(reportFilePath).getFile();
		SalesAndTrafficReport report = mapper.readValue(file, SalesAndTrafficReport.class);
		salesByDateRepository.saveAll(report.getSalesAndTrafficByDate());
		salesByAsinRepository.saveAll(report.getSalesAndTrafficByAsin());
	}
}
