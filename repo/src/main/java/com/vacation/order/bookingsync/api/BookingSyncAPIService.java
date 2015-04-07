package com.vacation.order.bookingsync.api;

import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vacation.order.bookingsync.oauth.BookingSyncOAuthClient;

@RestController
@RequestMapping(value="/bookingSyncAPIService")
public class BookingSyncAPIService {
	public static final Logger LOG = Logger
			.getLogger(BookingSyncAPIService.class.getName());
	public static final String BOOKING_SYNC_GET_RENTALS_URL = "https://www.bookingsync.com/api/v3/rentals";
	public static final String BOOKING_SYNC_UPDATE_RENTALS_URL="https://www.bookingsync.com/api/v3/rentals/:rental_id";
	
	@Autowired
	BookingSyncOAuthClient bookingSyncOAuthClient ;
	
	
	
	@RequestMapping(value="/calculateAndPersistScore", method=RequestMethod.GET)
	public void calculateAndPersistScore(){
		String refreshedAccessToken=bookingSyncOAuthClient.refreshToken();
		URI uri;
		Map<String,String> scoreMap=new HashMap<String,String>();
		try {
			uri = new URI(BOOKING_SYNC_GET_RENTALS_URL);
			StringReader jsonReader = BookingSyncAPIUtils.getResponse(uri,refreshedAccessToken);
			JsonElement result = new JsonParser().parse(jsonReader);
			JsonObject rentalsObject=result.getAsJsonObject();
			JsonArray rentalsArray=rentalsObject.getAsJsonArray("rentals");
			Iterator<JsonElement> rentalIterator=rentalsArray.iterator();
			while(rentalIterator.hasNext()){
				JsonObject rentalObject=rentalIterator.next().getAsJsonObject();
				String rentalId=rentalObject.get("id").getAsString();
				String description=rentalObject.get("description").getAsJsonObject().get("en").getAsString();
				JsonArray amenitiesArray=rentalObject.getAsJsonArray("rentals_amenities");
				int amenitiesCounter=0;
				if(amenitiesArray!=null){
				Iterator<JsonElement> amenitiesIterator=amenitiesArray.iterator();
				
				while(amenitiesIterator.hasNext()){
					amenitiesCounter++;
					}
				}
				//put the amenetiesScore in the map
				scoreMap.put(rentalId, description+" amenitiesScore: "+amenitiesCounter);
			}
			System.out.println("After calculating amenities score");
			for(Entry<String,String> entry:scoreMap.entrySet()){
				System.out.println("Creating put request for "+(String) entry.getKey() +" score is "+entry.getValue());
				JSONObject descriptionTobeWritten = new JSONObject();
				JSONObject enTobeWritten = new JSONObject();
				JSONArray rentalTobeWritten = new JSONArray();
				JSONObject rentalsTobeWritten = new JSONObject();
				enTobeWritten.put("description_en", entry.getValue());
			//	descriptionTobeWritten.put("description", enTobeWritten);
				rentalTobeWritten.put(enTobeWritten);
				rentalsTobeWritten.put("rentals", rentalTobeWritten);
				String updateUrl=BOOKING_SYNC_UPDATE_RENTALS_URL.replaceAll(":rental_id", (String) entry.getKey());
				System.out.println("put request url"+updateUrl);
				System.out.println("rentalsTobeWritten "+rentalsTobeWritten);
				BookingSyncAPIUtils.putResponse(updateUrl, rentalsTobeWritten,refreshedAccessToken);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
			
		
	}
}
