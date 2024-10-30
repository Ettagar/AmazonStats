package ua.agencyamazon.amazonstats.model.document;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "ReportSpecification")
public class ReportSpecification {
	@Id
	private String id;
	private String reportType;
	private ReportOptions reportOptions;
	private String dataStartTime;
	private String dataEndTime;
	private List<String> marketplaceIds;
}

@Data
class ReportOptions {
	private String dateGranularity;
	private String asinGranularity;
}
