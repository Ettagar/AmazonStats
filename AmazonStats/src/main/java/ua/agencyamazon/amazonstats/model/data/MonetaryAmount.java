package ua.agencyamazon.amazonstats.model.data;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonetaryAmount {
	private BigDecimal amount;
	private String currencyCode;
}