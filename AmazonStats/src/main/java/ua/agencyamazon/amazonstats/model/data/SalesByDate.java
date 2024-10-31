package ua.agencyamazon.amazonstats.model.data;

import lombok.Data;

@Data
public class SalesByDate {
	private MonetaryAmount orderedProductSales;
	private MonetaryAmount orderedProductSalesB2B;
	private Integer unitsOrdered;
	private Integer unitsOrderedB2B;
	private Integer totalOrderItems;
	private Integer totalOrderItemsB2B;
	private MonetaryAmount averageSalesPerOrderItem;
	private MonetaryAmount averageSalesPerOrderItemB2B;
	private Double averageUnitsPerOrderItem;
	private Double averageUnitsPerOrderItemB2B;
	private MonetaryAmount averageSellingPrice;
	private MonetaryAmount averageSellingPriceB2B;
	private Integer unitsRefunded;
	private Double refundRate;
	private Integer claimsGranted;
	private MonetaryAmount claimsAmount;
	private MonetaryAmount shippedProductSales;
	private Integer unitsShipped;
	private Integer ordersShipped;
}
