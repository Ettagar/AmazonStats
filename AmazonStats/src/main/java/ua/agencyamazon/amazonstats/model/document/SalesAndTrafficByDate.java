package ua.agencyamazon.amazonstats.model.document;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import ua.agencyamazon.amazonstats.model.data.SalesByDate;
import ua.agencyamazon.amazonstats.model.data.TrafficByDate;

@Data
@Document(collection = "SalesAndTrafficByDate")
public class SalesAndTrafficByDate {
	@Id
	private String id;
	private Date date;
	private SalesByDate salesByDate;
	private TrafficByDate trafficByDate;
}
