package com.aracnideo.repository;

import com.aracnideo.model.Card;

public interface CardRepository {

	Card findByName(String name);

	Card findRandom();

}
