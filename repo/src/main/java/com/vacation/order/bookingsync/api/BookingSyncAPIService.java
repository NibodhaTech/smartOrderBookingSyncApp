package com.vacation.order.bookingsync.api;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vacation.order.bookingsync.oauth.BookingSyncOAuthClient;

@RestController
@RequestMapping(value = "/bookingSyncAPIService")
public class BookingSyncAPIService {
	public static final Logger LOG = Logger
			.getLogger(BookingSyncAPIService.class.getName());
	public static final String BOOKING_SYNC_GET_RENTALS_URL = "https://www.bookingsync.com/api/v3/rentals";
	public static final String BOOKING_SYNC_UPDATE_RENTALS_URL = "https://www.bookingsync.com/api/v3/rentals/:rental_id";
	public static final String BOOKING_SYNC_GET_AMENITY_URL = "https://www.bookingsync.com/api/v3/amenities";
	public static Map<String, String> propertiesMap = new HashMap<String, String>();
	/*
	 * public static final int BASIC_AMENITY_SCORE = 10; public static final int
	 * SPECIAL_AMENITY_SCORE = 30; public static final int
	 * PET_FRIENDLY_AMENITY_SCORE = 20; public static final int
	 * DISABLED_FACILITY_AMENITY_SCORE = 30; public static final int
	 * OTHER_AMENITY_SCORE = 10;
	 */
	@Autowired
	BookingSyncOAuthClient bookingSyncOAuthClient;

	public void LoadAmenities() {
		try {
			/* Load the properties into HashMap */
			Properties properties = new Properties();
			InputStream input = new FileInputStream(
					"D:\\project\\smartOrderBookingSyncApp\\repo\\src\\main\\resources\\rentalAmenities.properties");
			properties.load(input);

			// properties.load(BookingSyncAPIService.class.getResourceAsStream("application1.properties"));
			for (String key : properties.stringPropertyNames()) {
				String value = properties.getProperty(key);
				System.out.println("Loading file" + key + value);
				propertiesMap.put(key, value);
			}
			input.close();
		} catch (Exception e) {
			System.out.println("Exception in loading properties file"
					+ e.getMessage());
		}

	}

	@RequestMapping(value = "/calculateAndPersistScore", method = RequestMethod.GET)
	public void calculateAndPersistScore() {

		String refreshedAccessToken = bookingSyncOAuthClient.refreshToken();
		URI uri;
		URI amenityURI;

		Map<String, String> scoreMap = new HashMap<String, String>();
		System.out.println("-----About to calculate-----");
		try {
			this.LoadAmenities();
			uri = new URI(BOOKING_SYNC_GET_RENTALS_URL);
			StringReader jsonReader = BookingSyncAPIUtils.getResponse(uri,
					refreshedAccessToken);
			JsonElement result = new JsonParser().parse(jsonReader);
			JsonObject rentalsObject = result.getAsJsonObject();
			JsonArray rentalsArray = rentalsObject.getAsJsonArray("rentals");

			Iterator<JsonElement> rentalIterator = rentalsArray.iterator();
			while (rentalIterator.hasNext()) {
				JsonObject rentalObject = rentalIterator.next()
						.getAsJsonObject();
				String rentalId = rentalObject.get("id").getAsString();
				System.out.println("----Rental ID--:" + rentalId);
				String description = rentalObject.get("description")
						.getAsJsonObject().get("en").getAsString();
				System.out.println("----Rental desc--:" + description);
				JsonObject linksObject = rentalObject.get("links")
						.getAsJsonObject();
				JsonArray amenitiesArray = linksObject
						.getAsJsonArray("rentals_amenities");

				int amenityScore = 0;
				int basicScore = 0, petScore = 0, otherScore = 0, disabledScore = 0, specialScore = 0, kitchenScore = 0, washerScore = 0, computerScore = 0, ambienceScore = 0,poolScore=0;
				int basicScore_counter = 0, petScore_counter = 0, disabledScore_counter = 0, specialScore_counter = 0, otherScore_counter = 0, Kitchen_counter = 0, washer_counter = 0, computer_counter = 0, ambience_counter = 0,pool_counter = 0;
				if (amenitiesArray != null && !amenitiesArray.isJsonNull()) {
					for (JsonElement amenity : amenitiesArray.getAsJsonArray()) {
						System.out.println("----Amenity123----"
								+ amenity.getAsString());

						String amenityCategory = propertiesMap.get(amenity
								.getAsString());
						if (amenityCategory != null) {
							if (amenityCategory.equalsIgnoreCase("basic")) {
								basicScore = basicScore + 2;
								basicScore_counter++;
							} else if (amenityCategory
									.equalsIgnoreCase("special")) {
								specialScore = specialScore + 8;
								specialScore_counter++;
							} else if (amenityCategory.equalsIgnoreCase("pets")) {
								petScore = petScore + 10;
								petScore_counter++;
							} else if (amenityCategory
									.equalsIgnoreCase("disabled")) {
								disabledScore = disabledScore + 5;
								disabledScore_counter++;
							} 
							 else if (amenityCategory
										.equalsIgnoreCase("kitchen")) {
								 kitchenScore = kitchenScore + 28;
								 Kitchen_counter++;
								}
							 else if (amenityCategory
										.equalsIgnoreCase("ambience")) {
								 ambienceScore = ambienceScore + 22;
								 ambience_counter++;
								}
							 else if (amenityCategory
										.equalsIgnoreCase("washer/dryer")) {
								 washerScore = washerScore + 5;
								 washer_counter++;
								}
							 else if (amenityCategory
										.equalsIgnoreCase("computer/Internet")) {
								 washerScore = washerScore + 5;
								 washer_counter++;
								}
							 else if (amenityCategory
										.equalsIgnoreCase("pool")) {
								 poolScore = poolScore + 15;
								 pool_counter++;
								}
							else {
								otherScore = 0;
								otherScore_counter++;
							}
						}
					}

					if (basicScore_counter != 0) {
						basicScore = (basicScore / basicScore_counter);
						amenityScore = amenityScore + basicScore;
					}
					if (specialScore_counter != 0) {
						specialScore = (specialScore / specialScore_counter);
						amenityScore = amenityScore + specialScore;
					}

					if (disabledScore_counter != 0) {
						disabledScore = (disabledScore / disabledScore_counter);
						amenityScore = amenityScore + disabledScore;
					}

					if (petScore_counter != 0) {
						petScore = (petScore / petScore_counter);
						amenityScore = amenityScore + petScore;
					}

					if (otherScore_counter != 0) {
						otherScore = (otherScore / otherScore_counter);
						amenityScore = amenityScore + otherScore;
					}

					if (Kitchen_counter != 0) {
						kitchenScore = (kitchenScore / Kitchen_counter);
						amenityScore = amenityScore + kitchenScore;
					}
					if (washer_counter != 0) {
						washerScore = (washerScore / washer_counter);
						amenityScore = amenityScore + washerScore;
					}
					if (computer_counter != 0) {
						computerScore = (computerScore / computer_counter);
						amenityScore = amenityScore + computerScore;
					}
					if (ambience_counter != 0) {
						ambienceScore = (ambienceScore / ambience_counter);
						amenityScore = amenityScore + ambienceScore;
					}
					if (pool_counter != 0) {
						poolScore = (poolScore / pool_counter);
						amenityScore = amenityScore + poolScore;
					}
				}
				System.out.println("Amenity Score 123 " + amenityScore);
				System.out.println("Updating amenities score for rentalid  "
						+ rentalId + " as " + description + " amenitiesScore: "
						+ amenityScore);
				scoreMap.put(rentalId, description + " amenitiesScore: "
						+ amenityScore);
				amenityScore = 0;

			}
			System.out.println("After calculating amenities score");
			for (Entry<String, String> entry : scoreMap.entrySet()) {
				System.out.println("Creating put request for "
						+ (String) entry.getKey() + " score is "
						+ entry.getValue());
				JSONObject descriptionTobeWritten = new JSONObject();
				JSONObject enTobeWritten = new JSONObject();
				JSONArray rentalTobeWritten = new JSONArray();
				JSONObject rentalsTobeWritten = new JSONObject();
				enTobeWritten.put("description_en", entry.getValue());
				// descriptionTobeWritten.put("description", enTobeWritten);
				rentalTobeWritten.put(enTobeWritten);
				rentalsTobeWritten.put("rentals", rentalTobeWritten);
				String updateUrl = BOOKING_SYNC_UPDATE_RENTALS_URL.replaceAll(
						":rental_id", (String) entry.getKey());
				System.out.println("put request url" + updateUrl);
				System.out.println("rentalsTobeWritten " + rentalsTobeWritten);
				BookingSyncAPIUtils.putResponse(updateUrl, rentalsTobeWritten,
						refreshedAccessToken);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
