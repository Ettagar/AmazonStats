package ua.agencyamazon.amazonstats.model.document;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ua.agencyamazon.amazonstats.model.data.SalesByAsin;
import ua.agencyamazon.amazonstats.model.data.TrafficByAsin;

@Data
@Document(collection = "SalesAndTrafficByAsin")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SalesAndTrafficByAsin implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private String id;

	@EqualsAndHashCode.Include
	private String parentAsin;

	@EqualsAndHashCode.Include
	private String sku;

	@EqualsAndHashCode.Include
	private SalesByAsin salesByAsin;

	@EqualsAndHashCode.Include
	private TrafficByAsin trafficByAsin;
}
