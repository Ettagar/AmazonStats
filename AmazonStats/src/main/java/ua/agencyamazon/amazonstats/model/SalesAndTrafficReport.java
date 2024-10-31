package ua.agencyamazon.amazonstats.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import ua.agencyamazon.amazonstats.model.document.ReportSpecification;
import ua.agencyamazon.amazonstats.model.document.SalesAndTrafficByAsin;
import ua.agencyamazon.amazonstats.model.document.SalesAndTrafficByDate;

@Data
@Document(collection = "SalesAndTrafficReport")
public class SalesAndTrafficReport {
	@Id
	private String id;

	@DBRef
	private ReportSpecification reportSpecification;

	@DBRef
	private List<SalesAndTrafficByDate> salesAndTrafficByDate;

	@DBRef
	private List<SalesAndTrafficByAsin> salesAndTrafficByAsin;
}
