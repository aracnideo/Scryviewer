package com.aracnideo.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
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

	private static final double IMAGE_SCALE = 0.90;

	private JTextField searchField;
	private JTextPane cardTextPane;
	private JLabel imageLabel;
	private CardService service;
	private CardRepository repository;
	private Card currentCard;
	private boolean showingFront = true;
	private JButton flipButton;
	private JComboBox<String> formatSelector;
	private JLabel legalityLabel;

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

		JLabel searchLabel = new JLabel("Search:");
		JLabel formatSelectorLabel = new JLabel("Format:");

		formatSelector = new JComboBox<>(
				new String[] { "Standard", "Commander", "Modern", "Legacy", "Pioneer", "Pauper" });
		formatSelector.addActionListener(e -> updateLegalityColor());

		JPanel leftPanel = new JPanel();

		FlowLayout layout = new FlowLayout();
		layout.setAlignment(FlowLayout.LEFT);
		layout.setHgap(30);
		layout.setVgap(10);

		leftPanel.setLayout(layout);

		leftPanel.add(searchLabel);
		leftPanel.add(searchField);
		leftPanel.add(searchButton);
		leftPanel.add(flipButton);
		leftPanel.add(formatSelectorLabel);
		leftPanel.add(formatSelector);
		leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 36, 10, 100));

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

		JPanel southPanel = new JPanel(new BorderLayout());

		JButton button = new JButton("Close");
		button.addActionListener(e -> this.dispose());
		southPanel.add(button, BorderLayout.EAST);

		legalityLabel = new JLabel(" ");
		legalityLabel.setHorizontalAlignment(SwingConstants.CENTER);
		legalityLabel.setFont(new Font("Serif", Font.BOLD, 16));

		Border emptyBorder = BorderFactory.createEmptyBorder(8, 16, 8, 16);
		southPanel.setBorder(emptyBorder);
		southPanel.add(legalityLabel, BorderLayout.CENTER);

		this.add(southPanel, BorderLayout.SOUTH);

		this.setVisible(true);
		findRandom();

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
			updateLegalityColor();
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

			Style boldStyle = cardTextPane.addStyle("Bold", null);
			StyleConstants.setFontSize(boldStyle, 18);
			StyleConstants.setBold(boldStyle, true);

			doc.insertString(doc.getLength(), result.getName() + "\n", titleStyle);

			if (result.getManaCost() != null)
				doc.insertString(doc.getLength(), result.getManaCost() + "\n", normalStyle);

			doc.insertString(doc.getLength(), result.getTypeLine() + "\n\n", normalStyle);

			if (result.getOracleText() != null)
				doc.insertString(doc.getLength(), result.getOracleText() + "\n\n", normalStyle);

			if (result.getPower() != null && result.getToughness() != null)
				doc.insertString(doc.getLength(), result.getPower() + "/" + result.getToughness() + "\n\n", boldStyle);
			if (result.getLoyalty() != null)
				doc.insertString(doc.getLength(), "\n Loyalty: " + result.getLoyalty(), boldStyle);

			if (result.getFlavorText() != null)
				doc.insertString(doc.getLength(), result.getFlavorText(), italicStyle);

			if (result.getArtist() != null)
				doc.insertString(doc.getLength(), "\n\n\tIllustrated by " + result.getArtist(), normalStyle);
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
			java.net.URL url = new java.net.URI(card.getImageUris().getNormal()).toURL();
			java.awt.image.BufferedImage bufferedImage = javax.imageio.ImageIO.read(url);

			if (bufferedImage != null) {
				int newWidth = (int) (bufferedImage.getWidth() * IMAGE_SCALE);
				int newHeight = (int) (bufferedImage.getHeight() * IMAGE_SCALE);
				Image scaled = bufferedImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
				imageLabel.setIcon(new ImageIcon(scaled));
			} else {
				imageLabel.setIcon(null);
			}

		} catch (Exception e) {
			e.printStackTrace();
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
			updateLegalityColor();
		} catch (CardNotFoundException ex) {
			JOptionPane.showMessageDialog(this, "Card not found.", "Warning", JOptionPane.WARNING_MESSAGE);
		} catch (ExternalServiceException ex) {
			JOptionPane.showMessageDialog(this, "Error connecting to API.", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	public void updateLegalityColor() {
		updateLegalityDisplay();
		if (currentCard == null)
			return;
		String selectedFormat = (String) formatSelector.getSelectedItem();
		String status = currentCard.getLegalities().get(selectedFormat.toLowerCase());
		if (!"legal".equals(status)) {
			cardTextPane.setBackground(new Color(255, 226, 226));
		} else {
			cardTextPane.setBackground(Color.WHITE);
		}
	}

	public void updateLegalityDisplay() {
		if (currentCard == null) {
			legalityLabel.setText(" ");
			return;
		}
		String selectedFormat = (String) formatSelector.getSelectedItem();
		String status = currentCard.getLegalities().get(selectedFormat.toLowerCase());
		if (status == null) {
			legalityLabel.setText(" ");
			return;
		}
		Color color;
		switch (status) {
		case "banned":
			color = new Color(200, 0, 0);
			break;
		case "restricted":
			color = new Color(200, 150, 0);
			break;
		case "legal":
			color = new Color(0, 130, 0);
			break;
		default:
			color = Color.GRAY;
		}
		legalityLabel.setForeground(color);
		if (!"legal".equals(status)) {
			legalityLabel.setText("⚠ Not legal in " + selectedFormat + " (" + status + ")");
		} else {
			legalityLabel.setText("✓ Legal in " + selectedFormat);
		}
	}
}
