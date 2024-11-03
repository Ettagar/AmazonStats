package ua.agencyamazon.amazonstats.model.document;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ua.agencyamazon.amazonstats.model.data.SalesByDate;
import ua.agencyamazon.amazonstats.model.data.TrafficByDate;

@Data
@Document(collection = "SalesAndTrafficByDate")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SalesAndTrafficByDate implements Serializable{
	private static final long serialVersionUID = 1L;

	@Id
	private String id;

	@EqualsAndHashCode.Include
	private Date date;

	@EqualsAndHashCode.Include
	private SalesByDate salesByDate;

	@EqualsAndHashCode.Include
	private TrafficByDate trafficByDate;
}
