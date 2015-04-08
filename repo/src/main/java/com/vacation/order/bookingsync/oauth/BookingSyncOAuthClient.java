package com.vacation.order.bookingsync.oauth;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;


@RestController
@RequestMapping(value="/bookingSyncOAuthClient")
public class BookingSyncOAuthClient {
	public static final Logger LOG = Logger
			.getLogger(BookingSyncOAuthClient.class.getName());
	public static final String BOOKING_SYNC_AUTHORIZE_URL = "https://www.bookingsync.com/oauth/authorize";
	public static final String BOOKING_SYNC_TOKEN_URL="https://www.bookingsync.com/oauth/token";
	public static final String BOOKING_SYNC_CLIENT_ID= "b3624d4943b8784de5ea506a4214d5e25288c0754c2f63367b15547364209eb9";
	public static final String BOOKING_SYNC_CLIENT_SCERET = "acb5a852df5248c1a7096dd9ccaf890738115ecda0fdccefa4c773a8ba18323c";
	public static final String BOOKING_SYNC_REDIRECT_URL="https://localhost:8443/SmartOrderingBookingSyncApp-0.0.1-SNAPSHOT/bookingSyncOAuthClient/redirect";
	
	@Autowired
	OAuthService oAuthService ;
	
	private String refreshToken;
	
	private HttpServletResponse response;
	
	/*public static void main(String[] args) {
		BookingSyncOAuthClient bookingSyncOAuthClient=new BookingSyncOAuthClient();
	//	bookingSyncOAuthClient.authorize();
	//	bookingSyncOAuthClient.refreshToken("8d6ae93d721e79d6c5f617f055f704aa547b496931a0abb9d86e4c13de8adbb9");
	}*/

	@RequestMapping(value="/pingService", method=RequestMethod.GET)
	public String pingService() {
		return "output from pingService";
	}
	
	@RequestMapping(value="/authorize", method=RequestMethod.GET)
	@ResponseBody
	public String authorize(HttpServletResponse response) {
		System.out.println("response "+response);
		OAuthAccessTokenParams oauthAccessTokenParams = new OAuthAccessTokenParams();
		oauthAccessTokenParams.setRequestURL(BOOKING_SYNC_AUTHORIZE_URL);
		oauthAccessTokenParams.setClientId(BOOKING_SYNC_CLIENT_ID);
		oauthAccessTokenParams.setRedirectURL(BOOKING_SYNC_REDIRECT_URL);
		oauthAccessTokenParams.setScope("rentals_read rentals_write");
		OAuthService oAuthService=new OAuthService();
		this.setResponse(response);
		return oAuthService.authorize(oauthAccessTokenParams,response);
		
	}


	@RequestMapping(value="/redirect", method=RequestMethod.GET)
	public void redirect(@RequestParam("code") String code) {
		LOG.info("Code from OAuth Authorization server " + code);
		
		OAuthService oAuthService = new OAuthService();
		OAuthAccessTokenParams oauthAccessTokenParams = new OAuthAccessTokenParams();
		oauthAccessTokenParams.setRequestURL(BOOKING_SYNC_TOKEN_URL);
		oauthAccessTokenParams.setRedirectURL(BOOKING_SYNC_REDIRECT_URL);
		oauthAccessTokenParams.setCode(code);
		
		oauthAccessTokenParams.setClientId(BOOKING_SYNC_CLIENT_ID);
		oauthAccessTokenParams.setClientSecret(BOOKING_SYNC_CLIENT_SCERET);
		String oAuthResponse = oAuthService
				.fetchAccessToken(oauthAccessTokenParams);
		System.out.println("Inside redirect oAuthResponse "+oAuthResponse);
		LOG.info("Inside redirect oAuthResponse  "+oAuthResponse);
		JsonElement oAuthResponseJson = new JsonParser().parse(oAuthResponse);
		String refreshToken=oAuthResponseJson.getAsJsonObject().get("refresh_token").getAsString();
		this.setRefreshToken(refreshToken);
		try {
			response.sendRedirect("../test.html");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	@RequestMapping(value="/refreshToken", method=RequestMethod.GET)
	@ResponseBody
	public String refreshToken() {
		LOG.info("inside refreshToken " + this.getRefreshToken());
	
		OAuthAccessTokenParams oauthAccessTokenParams = new OAuthAccessTokenParams();
		oauthAccessTokenParams.setRequestURL(BOOKING_SYNC_TOKEN_URL);
		oauthAccessTokenParams.setRedirectURL(BOOKING_SYNC_REDIRECT_URL);
		oauthAccessTokenParams.setRefreshToken(this.getRefreshToken());
		
		oauthAccessTokenParams.setClientId(BOOKING_SYNC_CLIENT_ID);
		oauthAccessTokenParams.setClientSecret(BOOKING_SYNC_CLIENT_SCERET);
		String oAuthResponse = oAuthService
				.refreshToken(oauthAccessTokenParams);
		System.out.println(" inside refreshToken oAuthResponse "+oAuthResponse);
		LOG.info("inside refreshToken  oAuthResponse "+oAuthResponse);
		JsonElement oAuthResponseJson = new JsonParser().parse(oAuthResponse);
		String accessToken=oAuthResponseJson.getAsJsonObject().get("access_token").getAsString();
		String refreshTokenTemp=oAuthResponseJson.getAsJsonObject().get("refresh_token").getAsString();
		this.setRefreshToken(refreshTokenTemp);
		return accessToken;

	}


	public HttpServletResponse getResponse() {
		return response;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
}
