package com.aracnideo.service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import com.aracnideo.model.Card;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CardSearch {

	public static Card findCard(String query) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
		String url = "https://api.scryfall.com/cards/named?fuzzy=" + encodedQuery;
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		String json = response.body();
		// Control
		System.out.println(json);
		ObjectMapper mapper = new ObjectMapper();
		Card card = mapper.readValue(json, Card.class);

		return card;
	}

	public static Card findRandomCard() throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		String url = "https://api.scryfall.com/cards/random";
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		String json = response.body();
		ObjectMapper mapper = new ObjectMapper();
		Card card = mapper.readValue(json, Card.class);
		return card;
	}

}
