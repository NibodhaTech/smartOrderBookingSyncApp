package com.vacation.order.bookingsync.oauth;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;

public class WebClientDevWrapper {

	public static HttpClient wrapClient(HttpClient base) {
		try {

			return HttpClients.custom()
					.setHostnameVerifier(new AllowAllHostnameVerifier())
					.setRedirectStrategy(new LaxRedirectStrategy()).build();
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
}