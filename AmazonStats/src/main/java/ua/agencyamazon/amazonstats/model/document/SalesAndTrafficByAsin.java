package ua.agencyamazon.amazonstats.model.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import ua.agencyamazon.amazonstats.model.data.SalesByAsin;
import ua.agencyamazon.amazonstats.model.data.TrafficByAsin;

@Data
@Document(collection = "SalesAndTrafficByAsin")
public class SalesAndTrafficByAsin {
	@Id
	private String id;
	private String parentAsin;
	private String sku;
	private SalesByAsin salesByAsin;
	private TrafficByAsin trafficByAsin;
}
