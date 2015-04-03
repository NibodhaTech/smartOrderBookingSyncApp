package com.vacation.order.bookingsync.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import org.json.JSONObject;

public class BookingSyncAPIUtils {
	public static StringReader getResponse(URI uri,String accessToken) {
		HttpURLConnection connection = null;
		StringReader reader = null;
		try {
			URL url = uri.toURL();
			connection = (HttpURLConnection) url.openConnection();
			// add request header
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Content-Type",
					"application/json; charset=utf-8");
			connection.setRequestProperty("Authorization", "Bearer "+accessToken);
			connection.connect();

			BufferedReader rd = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			StringWriter writer = new StringWriter();
			String line = "";
			while ((line = rd.readLine()) != null) {
				writer.write(line);
			}
			reader = new StringReader(writer.toString());
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			if (null != connection) {
				connection.disconnect();
			}
		}
		return reader;
	}

	public static void putResponse(String url, JSONObject rentals,String accessToken) {
		HttpURLConnection con =null;
		try {
			URL object = new URL(url);

			 con = (HttpURLConnection) object.openConnection();

			con.setDoOutput(true);

			con.setDoInput(true);

			con.setRequestProperty("Content-Type", "application/vnd.api+json");

			con.setRequestProperty("Accept", "application/json");
			con.setRequestProperty("Authorization", "Bearer "+accessToken);
			con.setRequestMethod("PUT");

			

			OutputStreamWriter wr = new OutputStreamWriter(
					con.getOutputStream());

			wr.write(rentals.toString());

			wr.flush();

			// display what returns the POST request

			StringBuilder sb = new StringBuilder();

			int HttpResult = con.getResponseCode();

			if (HttpResult == HttpURLConnection.HTTP_OK) {

				BufferedReader br = new BufferedReader(new InputStreamReader(
						con.getInputStream(), "utf-8"));

				String line = null;

				while ((line = br.readLine()) != null) {
					sb.append(line + "\n");
				}

				br.close();

				System.out.println("" + sb.toString());

			} else {
				System.out.println(con.getResponseMessage());
			}
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			if (null != con) {
				con.disconnect();
			}
		}
	}
}
