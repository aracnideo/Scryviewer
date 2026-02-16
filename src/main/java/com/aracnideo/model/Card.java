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


	public Card() {
	}

	public Card(String name, String manaCost, String oracleText, String typeLine) {
		this.name = name;
		this.manaCost = manaCost;
		this.oracleText = oracleText;
		this.typeLine = typeLine;
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

	private String showIf(String string, String rest) {
		if (string != null) {
			StringBuilder sb = new StringBuilder();
			sb.append(rest);
			sb.append("\n");
			sb.append(string);
			return sb.toString();
		} 
		else {
			return rest;
		}
	}

	@Override
	public String toString() {
		String rest = new String();
		rest = showIf(manaCost, rest);
		rest = showIf(typeLine, rest);
		rest = showIf(oracleText, rest);
		return name + rest;
	}

}
