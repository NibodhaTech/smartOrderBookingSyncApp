/**
 * 
 */
package com.vacation.order.service;

import java.io.StringReader;
import java.net.URI;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.vacation.order.common.SmartOrderingUtils;
import com.vacation.order.model.Product;

/**
 * @author Suresh Kumar S
 *
 */
@Service
public class SmartOrderingServiceImpl implements SmartOrderingServiceInter {
	
	Logger logger = Logger.getLogger(SmartOrderingServiceImpl.class.getName());

	/* (non-Javadoc)
	 * @see com.vacation.order.service.SmartOrderingServiceInter#getScore(com.vacation.order.model.Product)
	 */
	@Override
	public Double getWalkScore(Product product) {
		Double walkScore = 0.0;
		if(product != null){
			URI uri = SmartOrderingUtils.formUri(SmartOrderingUtils.getString(product.getLatitude()), SmartOrderingUtils.getString(product.getLongitude()), product.getAddress(), "json");
			if(uri == null){
				logger.info("Wrong Url...");
				return null;
			}
			StringReader jsonReader = SmartOrderingUtils.getResponse(uri);
			JsonElement result = new JsonParser().parse(jsonReader);
			if(result.getAsJsonObject().get("status").getAsInt() == 1){
				walkScore = result.getAsJsonObject().get("walkscore").getAsDouble();
			}
		}
		return walkScore;
	}

	@Override
	public Double getAmenityScore(Product product, int genericAmenitiesCount) {
		if(product.getAmenities() != null && !product.getAmenities().isEmpty() && genericAmenitiesCount != 0) return (Double.valueOf(product.getAmenities().size())/Double.valueOf(genericAmenitiesCount));
		return 0.0;
	}
}
