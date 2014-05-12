package uk.co.revsys.user.manager.dao;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class MongoEntityJacksonMixin {

	@JsonProperty("_id")
	private String id;
	
}
