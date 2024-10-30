package ua.agencyamazon.amazonstats.model.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import lombok.Data;

@Data
@Document
public class RefreshToken {
	@Id
	String id;

	@DocumentReference(lazy = true)
	private User owner;
}
