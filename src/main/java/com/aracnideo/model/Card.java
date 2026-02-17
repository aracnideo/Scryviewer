package com.aracnideo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Card {

	@JsonProperty("name")
	private String name;
	@JsonProperty("mana_cost")
	private String manaCost;
	@JsonProperty("type_line")
	private String typeLine;
	@JsonProperty("oracle_text")
	private String oracleText;
	@JsonProperty("power")
	private String power;
	@JsonProperty("toughness")
	private String toughness;
	@JsonProperty("flavor_text")
	private String flavorText;
	@JsonProperty("image_uris")
	private ImageUris imageUris;

	public Card() {
	}

	public Card(String name, String manaCost, String oracleText, String typeLine, String power, String toughness,
			String flavorText, ImageUris imageUris) {
		this.name = name;
		this.manaCost = manaCost;
		this.oracleText = oracleText;
		this.typeLine = typeLine;
		this.power = power;
		this.toughness = toughness;
		this.flavorText = flavorText;
		this.imageUris = imageUris;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getManaCost() {
		return manaCost;
	}

	public void setManaCost(String manaCost) {
		this.manaCost = manaCost;
	}

	public String getOracleText() {
		return oracleText;
	}

	public void setOracleText(String oracleText) {
		this.oracleText = oracleText;
	}

	public String getTypeLine() {
		return typeLine;
	}

	public void setTypeLine(String typeLine) {
		this.typeLine = typeLine;
	}

	public String getPower() {
		return power;
	}

	public void setPower(String power) {
		this.power = power;
	}

	public String getToughness() {
		return toughness;
	}

	public void setToughness(String toughness) {
		this.toughness = toughness;
	}

	public String getFlavorText() {
		return flavorText;
	}

	public void setFlavorText(String flavorText) {
		this.flavorText = flavorText;
	}

	public ImageUris getImageUris() {
		return imageUris;
	}

	public void setImageUris(ImageUris imageUris) {
		this.imageUris = imageUris;
	}

	@Override
	public String toString() {
		return "Card [name=" + name + ", manaCost=" + manaCost + ", typeLine=" + typeLine + ", oracleText=" + oracleText
				+ ", power=" + power + ", toughness=" + toughness + ", flavorText=" + flavorText + ", imageUris="
				+ imageUris + "]";
	}

}
