package ua.agencyamazon.amazonstats.model.data;

import java.io.Serializable;

import lombok.Data;

@Data
public class SalesByAsin implements Serializable{
	private static final long serialVersionUID = 1L;

	private Integer unitsOrdered;
	private Integer unitsOrderedB2B;
	private MonetaryAmount orderedProductSales;
	private MonetaryAmount orderedProductSalesB2B;
	private Integer totalOrderItems;
	private Integer totalOrderItemsB2B;
}
