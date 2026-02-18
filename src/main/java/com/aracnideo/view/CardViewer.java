package com.aracnideo.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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

import com.aracnideo.exception.CardNotFoundException;
import com.aracnideo.exception.ExternalServiceException;
import com.aracnideo.model.Card;
import com.aracnideo.model.CardSide;
import com.aracnideo.repository.CardRepository;
import com.aracnideo.repository.ScryfallCardRepository;
import com.aracnideo.service.CardService;

public class CardViewer extends JFrame {
	private JTextField searchField;
	private JTextPane cardTextPane;
	private JLabel imageLabel;
	private CardService service;
	private CardRepository repository;
	private Card currentCard;
	private boolean showingFront = true;
	private JButton flipButton;

	public CardViewer() {
		this.repository = new ScryfallCardRepository();
		this.service = new CardService(repository);

		setTitle("Scryviewer");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1300, 800);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());

		imageLabel = new JLabel();
		imageLabel.setHorizontalAlignment(SwingConstants.CENTER);

		JPanel searchPanel = new JPanel(new BorderLayout());
		searchField = new JTextField(20);
		searchField.addActionListener(e -> search());

		JButton randomButton = new JButton("Random");
		randomButton.addActionListener(e -> findRandom());

		JButton searchButton = new JButton("Search");
		searchButton.addActionListener(e -> search());

		flipButton = new JButton("Flip");
		flipButton.addActionListener(e -> flipCard());
		flipButton.setEnabled(false);

		JPanel leftPanel = new JPanel();

		FlowLayout layout = new FlowLayout();
		layout.setAlignment(FlowLayout.LEFT);
		layout.setHgap(30);
		layout.setVgap(10);

		leftPanel.setLayout(layout);

		leftPanel.add(searchField);
		leftPanel.add(searchButton);
		leftPanel.add(flipButton);
		leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 100, 10, 100));

		JPanel rightPanel = new JPanel();
		rightPanel.add(randomButton);
		rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 100));

		searchPanel.add(leftPanel, BorderLayout.WEST);
		searchPanel.add(rightPanel, BorderLayout.EAST);
		this.add(searchPanel, BorderLayout.NORTH);

		cardTextPane = new JTextPane();
		cardTextPane.setEditable(false);
		cardTextPane.setMargin(new Insets(20, 20, 20, 20));

		JScrollPane scrollPane = new JScrollPane(cardTextPane);
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, imageLabel, scrollPane);

		splitPane.setDividerLocation(550);
		splitPane.setResizeWeight(0);

		this.add(splitPane, BorderLayout.CENTER);

		JButton button = new JButton("Close");
		button.addActionListener(e -> this.dispose());
		this.add(button, BorderLayout.SOUTH);

		this.setVisible(true);

	}

	private void search() {
		String query = this.searchField.getText();
		try {
			Card result = service.search(query);
			this.currentCard = result;
			this.showingFront = true;
			if (result.isDoubleFaced()) {
				renderCard(result.getFrontFace());
				renderImage(result.getFrontFace());
			} else {
				renderCard(result);
				renderImage(result);
			}
			flipButton.setEnabled(result.isDoubleFaced());
		} catch (CardNotFoundException e) {
			JOptionPane.showMessageDialog(this, "Card not found.", "Warning", JOptionPane.WARNING_MESSAGE);
		} catch (ExternalServiceException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error connecting to API.", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void flipCard() {
		if (currentCard == null)
			return;

		if (!currentCard.isDoubleFaced())
			return;

		showingFront = !showingFront;

		if (showingFront) {
			renderCard(currentCard.getFrontFace());
			renderImage(currentCard.getFrontFace());
		} else {
			renderCard(currentCard.getBackFace());
			renderImage(currentCard.getBackFace());
		}
	}

	private void renderCard(CardSide result) {
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

	private void renderImage(CardSide card) {
		if (card == null || card.getImageUris() == null) {
			imageLabel.setIcon(null);
			return;
		}
		try {
			ImageIcon icon = new ImageIcon(new java.net.URL(card.getImageUris().getNormal()));
			Image img = icon.getImage().getScaledInstance(488, 680, Image.SCALE_SMOOTH);
			imageLabel.setIcon(new ImageIcon(img));
		} catch (Exception e) {
			imageLabel.setIcon(null);
		}
	}

	private void findRandom() {
		try {
			Card result = service.random();
			this.currentCard = result;
			this.showingFront = true;
			searchField.setText(result.getName());
			if (result.isDoubleFaced()) {
				renderCard(result.getFrontFace());
				renderImage(result.getFrontFace());
			} else {
				renderCard(result);
				renderImage(result);
			}
			flipButton.setEnabled(result.isDoubleFaced());
		} catch (CardNotFoundException ex) {
			JOptionPane.showMessageDialog(this, "Card not found.", "Warning", JOptionPane.WARNING_MESSAGE);
		} catch (ExternalServiceException ex) {
			JOptionPane.showMessageDialog(this, "Error connecting to API.", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

}
