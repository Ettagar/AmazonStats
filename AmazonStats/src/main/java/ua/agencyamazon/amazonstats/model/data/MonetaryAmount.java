package ua.agencyamazon.amazonstats.model.data;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonetaryAmount implements Serializable{
	private static final long serialVersionUID = 1L;

	private BigDecimal amount;
	private String currencyCode;
}