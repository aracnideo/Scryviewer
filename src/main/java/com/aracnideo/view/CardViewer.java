package com.aracnideo.view;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.Insets;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.aracnideo.model.Card;
import com.aracnideo.service.CardSearch;

public class CardViewer {
	private JTextField searchField;
	private JTextPane cardTextPane;
	private JLabel imageLabel;

	public CardViewer() throws IOException, InterruptedException {

		JFrame frame = new JFrame("Scryviewer");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1300, 800);
		frame.setLocationRelativeTo(null);
		frame.setLayout(new BorderLayout());

		imageLabel = new JLabel();
		imageLabel.setHorizontalAlignment(SwingConstants.CENTER);

		JPanel searchPanel = new JPanel();
		searchField = new JTextField(20);
		searchField.addActionListener(e -> {
			try {
				search();
			} catch (IOException | InterruptedException e1) {
				e1.printStackTrace();
			}
		});
		
		JButton randomButton = new JButton("Random");
		randomButton.addActionListener(e -> {
			try {
				Card result = CardSearch.findRandomCard();
				searchField.setText(result.getName());
				search();
			} catch (IOException | InterruptedException e1) {
				e1.printStackTrace();
			}
		});
		
		JButton searchButton = new JButton("Search");
		searchButton.addActionListener(e -> {
			try {
				search();
			} catch (IOException | InterruptedException e1) {
				e1.printStackTrace();
			}
		});
		
		searchPanel.add(randomButton);
		searchPanel.add(searchField);
		searchPanel.add(searchButton);
		frame.add(searchPanel, BorderLayout.NORTH);

		cardTextPane = new JTextPane();
		cardTextPane.setEditable(false);
		cardTextPane.setMargin(new Insets(20, 20, 20, 20));

		JScrollPane scrollPane = new JScrollPane(cardTextPane);
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, imageLabel, scrollPane);

		splitPane.setDividerLocation(550);
		splitPane.setResizeWeight(0);

		frame.add(splitPane, BorderLayout.CENTER);

		JButton button = new JButton("Close");
		button.addActionListener(e -> frame.dispose());
		frame.add(button, BorderLayout.SOUTH);

		frame.setVisible(true);

		// Format

	}

	private void search() throws IOException, InterruptedException {
		String query = this.searchField.getText();
		Card result = CardSearch.findCard(query);
		renderCard(result);
		if (result.getImageUris() != null) {
			try {
				ImageIcon icon = new ImageIcon(new java.net.URL(result.getImageUris().getNormal()));
				Image img = icon.getImage().getScaledInstance(488, 680, Image.SCALE_SMOOTH);
				imageLabel.setIcon(new ImageIcon(img));

			} catch (Exception e) {
				imageLabel.setIcon(null);
			}
		} else {
			imageLabel.setIcon(null);
		}
	}

	private void renderCard(Card result) {
		try {
			StyledDocument doc = cardTextPane.getStyledDocument();
			doc.remove(0, doc.getLength());

			Style titleStyle = cardTextPane.addStyle("Title", null);
			StyleConstants.setBold(titleStyle, true);
			StyleConstants.setFontSize(titleStyle, 28);

			Style normalStyle = cardTextPane.addStyle("Normal", null);
			StyleConstants.setFontSize(normalStyle, 18);

			Style italicStyle = cardTextPane.addStyle("Italic", null);
			StyleConstants.setFontSize(italicStyle, 18);
			StyleConstants.setItalic(italicStyle, true);

			doc.insertString(doc.getLength(), result.getName() + "\n", titleStyle);

			if (result.getManaCost() != null)
				doc.insertString(doc.getLength(), result.getManaCost() + "\n", normalStyle);

			doc.insertString(doc.getLength(), result.getTypeLine() + "\n\n", normalStyle);

			if (result.getOracleText() != null)
				doc.insertString(doc.getLength(), result.getOracleText() + "\n\n", normalStyle);

			if (result.getPower() != null && result.getToughness() != null)
				doc.insertString(doc.getLength(), result.getPower() + "/" + result.getToughness() + "\n\n",
						normalStyle);

			if (result.getFlavorText() != null)
				doc.insertString(doc.getLength(), result.getFlavorText(), italicStyle);

		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

}
