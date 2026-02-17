package com.aracnideo.repository;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import com.aracnideo.exception.CardNotFoundException;
import com.aracnideo.exception.ExternalServiceException;
import com.aracnideo.model.Card;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ScryfallCardRepository implements CardRepository {

	private static final String BASE_URL = "https://api.scryfall.com";
	private static final String NAMED = "/cards/named?fuzzy=";
	private static final String RANDOM = "/cards/random";

	private HttpClient client;
	private ObjectMapper mapper;

	public ScryfallCardRepository() {
		this.client = HttpClient.newHttpClient();
		this.mapper = new ObjectMapper();
	}

	@Override
	public Card findByName(String name) {
		String encodedQuery = URLEncoder.encode(name, StandardCharsets.UTF_8);
		return executeRequest(NAMED + encodedQuery);

	}

	@Override
	public Card findRandom() {
		return executeRequest(RANDOM);
	}

	private Card executeRequest(String endpoint) {
		String fullUrl = BASE_URL + endpoint;
		try {
			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(fullUrl)).GET().build();
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			int status = response.statusCode();
			if (status == 404) {
				throw new CardNotFoundException("Card not found.");
			}
			if (status != 200) {
				throw new ExternalServiceException("Scryfall API returned status: " + status, null);
			}
			String json = response.body();
			Card card = this.mapper.readValue(json, Card.class);
			return card;
		} catch (IOException | InterruptedException e) {
			throw new ExternalServiceException("Error communicating with Scryfall API.", e);
		}
	}

}
