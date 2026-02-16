package com.aracnideo.view;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

import com.aracnideo.model.Card;
import com.aracnideo.service.CardSearch;

public class CardViewer {
	private JTextField searchField;
	private JTextArea cardTextArea;

	public CardViewer() throws IOException, InterruptedException {
		Card card = new Card();

		JFrame frame = new JFrame("Card Viewer");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 250);
		frame.setLocationRelativeTo(null);
		frame.setLayout(new BorderLayout());

		JPanel searchPanel = new JPanel();
		searchField = new JTextField(20);
		searchField.addActionListener(e->{
			try {
				search();
			} catch (IOException | InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		JButton searchButton = new JButton("Search");
		searchButton.addActionListener(e -> {
			try {
				search();
			} catch (IOException | InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		searchPanel.add(searchField);
		searchPanel.add(searchButton);
		frame.add(searchPanel, BorderLayout.NORTH);

		cardTextArea = new JTextArea(card.toString());

		cardTextArea.setLineWrap(true);
		cardTextArea.setWrapStyleWord(true);
		cardTextArea.setEditable(false);
		cardTextArea.setFont(new Font("Arial", Font.PLAIN, 14));
		frame.add(cardTextArea);

		JButton button = new JButton("Close");
		button.addActionListener(e -> frame.dispose());
		frame.add(button, BorderLayout.SOUTH);

		frame.setVisible(true);
		search("Black Lotus");

	}

	private void search() throws IOException, InterruptedException {
		String query = this.searchField.getText();
		Card result = CardSearch.findCard(query);
		cardTextArea.setText(result.toString());
	}
	
	//Initialization method
	private void search(String query) throws IOException, InterruptedException {
		Card result = CardSearch.findCard(query);
		cardTextArea.setText(result.toString());
	}

}
