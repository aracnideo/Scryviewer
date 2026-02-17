package com.aracnideo.service;

import com.aracnideo.model.Card;
import com.aracnideo.repository.CardRepository;

public class CardService {

	private CardRepository repository;

	public CardService(CardRepository repository) {
		this.repository = repository;
	}

	public Card search(String name) {
		return repository.findByName(name);
	}

	public Card random() {
		return repository.findRandom();
	}
}
