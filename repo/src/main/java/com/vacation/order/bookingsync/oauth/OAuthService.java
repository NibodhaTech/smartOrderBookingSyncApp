package com.vacation.order.bookingsync.oauth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.log4j.Logger;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest.AuthenticationRequestBuilder;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.springframework.stereotype.Service;

@Service
public class OAuthService {
	public static final Logger LOG = Logger.getLogger(OAuthService.class
			.getName());

	public String authorize(OAuthAccessTokenParams oauthAccessTokenParams,HttpServletResponse response) {
		StringBuffer result = new StringBuffer();
		// sample usage
		// http://localhost:8080/Razor/bookingPalOAuthService/authorize?pos=1234&requestURL=https://www.bookingsync.com/oauth/authorize&clientId=123&scope=test

		try {
			OAuthClientRequest request = this
					.authorizeUsingOAuth2(oauthAccessTokenParams);
			HttpClient client = HttpClientBuilder.create().build();
			client = WebClientDevWrapper.wrapClient(client);
			HttpGet getRequest = new HttpGet(request.getLocationUri());

			System.out.println("request.getLocationUri() "
					+ getRequest.getURI());
			response.sendRedirect(request.getLocationUri());
			System.out.println(" After response.sendRedirect ");

		

		} catch (OAuthSystemException e) {
			LOG.error(e.getMessage(), e);
		} catch (ClientProtocolException e) {
			LOG.error(e.getMessage(), e);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
		return result.toString();
		
	}

	private OAuthClientRequest authorizeUsingOAuth2(
			OAuthAccessTokenParams oauthAccessTokenParams)
			throws OAuthSystemException {
		String requestURL = convertToUTF8(oauthAccessTokenParams
				.getRequestURL());
		String clientId = convertToUTF8(oauthAccessTokenParams.getClientId());
		String scope = convertToUTF8(oauthAccessTokenParams.getScope());
		AuthenticationRequestBuilder requestBuilder = OAuthClientRequest
				.authorizationLocation(requestURL);
		requestBuilder.setClientId(clientId);
		requestBuilder.setRedirectURI(oauthAccessTokenParams.getRedirectURL());
		requestBuilder.setResponseType("code");
		// Note: To request authorization for multiple scopes, use a space
		// separated list. In URLs it would look like
		// &scope=bookings_read%20rentals_read (%20 represent a space within
		// URLs)
		requestBuilder.setScope(scope);
		return requestBuilder.buildQueryMessage();

	}

	public String fetchAccessToken(OAuthAccessTokenParams oauthAccessTokenParams) {
		HttpClient client = HttpClientBuilder.create().build();
		StringBuffer result = new StringBuffer();
		try {

			client = WebClientDevWrapper.wrapClient(client);
			HttpPost httpPost = new HttpPost(
					oauthAccessTokenParams.getRequestURL());
			BasicNameValuePair[] params = {
					new BasicNameValuePair("client_id",
							oauthAccessTokenParams.getClientId()),
					new BasicNameValuePair("client_secret",
							oauthAccessTokenParams.getClientSecret()),
					new BasicNameValuePair("code",
							oauthAccessTokenParams.getCode()),
					new BasicNameValuePair("grant_type", "authorization_code"),
					new BasicNameValuePair("redirect_uri",
							oauthAccessTokenParams.getRedirectURL()) };

			httpPost.setHeader("Content-Type",
					"application/x-www-form-urlencoded");

			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
					Arrays.asList(params));
			urlEncodedFormEntity.setContentEncoding(HTTP.UTF_8);
			httpPost.setEntity(urlEncodedFormEntity);
			HttpResponse response = client.execute(httpPost);
			int statusCode = 0;
			statusCode = response.getStatusLine().getStatusCode();

			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));

			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}

			System.out.println(" Response Status Code : " + statusCode);
			LOG.info(" Response Status Code : " + statusCode);
			System.out.println(" Response Status Code : " + result);
			LOG.info(" Response Status Code : " + result);

		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return result.toString();
	}

	public static String convertToUTF8(String s) {
		String out = null;
		try {
			if (s != null) {
				out = new String(s.getBytes("UTF-8"), "ISO-8859-1");
			}
		} catch (java.io.UnsupportedEncodingException e) {
			return null;
		}
		return out;
	}

	public String refreshToken(OAuthAccessTokenParams oauthAccessTokenParams) {
		HttpClient client = HttpClientBuilder.create().build();
		StringBuffer result = new StringBuffer();
		try {

			client =  WebClientDevWrapper.wrapClient(client);
			HttpPost httpPost = new HttpPost(
					oauthAccessTokenParams.getRequestURL());
			BasicNameValuePair[] params = {
					new BasicNameValuePair("client_id",
							oauthAccessTokenParams.getClientId()),
					new BasicNameValuePair("client_secret",
							oauthAccessTokenParams.getClientSecret()),
					new BasicNameValuePair("refresh_token",
							oauthAccessTokenParams.getRefreshToken()),
					new BasicNameValuePair("grant_type", "refresh_token"),
					new BasicNameValuePair("redirect_uri",
							oauthAccessTokenParams.getRedirectURL()) };

			httpPost.setHeader("Content-Type",
					"application/x-www-form-urlencoded");

			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
					Arrays.asList(params));
			urlEncodedFormEntity.setContentEncoding(HTTP.UTF_8);
			httpPost.setEntity(urlEncodedFormEntity);
			HttpResponse response = client.execute(httpPost);
			int statusCode = 0;
			statusCode = response.getStatusLine().getStatusCode();

			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));

			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}

			System.out.println(" Response Status Code : " + statusCode);
			LOG.info(" Response Status Code : " + statusCode);
			System.out.println(" Response Status Code : " + result);
			LOG.info(" Response Status Code : " + result);

		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return result.toString();
	}

}
