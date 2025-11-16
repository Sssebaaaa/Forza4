import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class FinestraMenu extends JFrame {

    //Font e costanti
    private static final String FONT_FILE_NAME = "spicy_sale/Spicy Sale.ttf";

    //Colori
    private final Color COLORE_SFONDO_BASE = new Color(45, 34, 63);
    private final Color COLORE_NEBULOSA_MAGENTA = new Color(255, 100, 200, 90);
    private final Color COLORE_NEBULOSA_BLU = new Color(100, 150, 255, 90);
    private final Color COLORE_NEBULOSA_SCURO = new Color(30, 20, 50, 200);
    private final Color COLORE_STELLA = new Color(255, 255, 255, 200);
    private final Color COLORE_VERDE = new Color(30, 138, 76);
    private final Color COLORE_VERDE_HOVER = new Color(20, 118, 66);
    private final Color COLORE_ARANCIO = new Color(200, 112, 26);
    private final Color COLORE_ARANCIO_HOVER = new Color(180, 92, 16);
    private final Color COLORE_TESTO = Color.WHITE;
    private final int RAGGIO_BORDO = 30;

    //Variabili per il font
    private Font customFontTitolo;
    private Font customFontBottone;
    private Font customFontDialog;

    //Pannelli per la navigazione
    private CardLayout cardLayout;
    private JPanel dynamicContentPanel;

    //Caricamento del font
    private Font loadCustomFont(String name, int style, float size) {
        try {
            File fontFile = new File(name);
            Font baseFont;
            if (fontFile.exists()) {
                baseFont = Font.createFont(Font.TRUETYPE_FONT, fontFile);
            } else {
                baseFont = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/" + name));
            }
            return baseFont.deriveFont(style, size);
        } catch (FontFormatException | IOException | NullPointerException e) {
            System.err.println("Errore caricamento font: " + e.getMessage());
            return new Font("Serif", Font.BOLD, (int) size);
        }
    }

    public FinestraMenu() {
        customFontTitolo = loadCustomFont(FONT_FILE_NAME, Font.PLAIN, 125f);
        customFontBottone = loadCustomFont(FONT_FILE_NAME, Font.PLAIN, 22f);
        customFontDialog = loadCustomFont(FONT_FILE_NAME, Font.PLAIN, 18f);

        setTitle("FORZA 4");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        BackgroundPanel backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(new GridBagLayout());
        setContentPane(backgroundPanel);
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0; gbc.gridy = 0; gbc.weighty = 0.5;
        backgroundPanel.add(Box.createVerticalGlue(), gbc);

        JLabel titoloLabel = new JLabel("FORZA 4");
        titoloLabel.setFont(customFontTitolo);
        titoloLabel.setForeground(COLORE_TESTO);
        
        JPanel titleWrapper = new JPanel(new GridBagLayout());
        titleWrapper.setOpaque(false);
        titleWrapper.add(titoloLabel);

        gbc.gridy = 1; gbc.weighty = 0; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 50, 0);
        backgroundPanel.add(titleWrapper, gbc);

        cardLayout = new CardLayout();
        dynamicContentPanel = new JPanel(cardLayout);
        dynamicContentPanel.setOpaque(false);

        JPanel mainButtonsPanel = createMainButtonsPanel();
        JPanel modeSelectionPanel = createModeSelectionPanel();
        JPanel difficultySelectionPanel = createDifficultySelectionPanel();

        dynamicContentPanel.add(mainButtonsPanel, "MAIN");
        dynamicContentPanel.add(modeSelectionPanel, "MODE");
        dynamicContentPanel.add(difficultySelectionPanel, "DIFFICULTY");

        gbc.gridy = 2;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(15, 10, 15, 10);
        backgroundPanel.add(dynamicContentPanel, gbc);

        gbc.gridy = 3; gbc.weighty = 1.0;
        backgroundPanel.add(Box.createVerticalGlue(), gbc);
        
        cardLayout.show(dynamicContentPanel, "MAIN");
        
        setVisible(true);
    }
    
    //Tasto TORNA AL MENU
    public void tornaAllaSelezionePrincipale() {
        cardLayout.show(dynamicContentPanel, "MAIN");
    }

    //Pannelli dinamici
    private JPanel createMainButtonsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.insets = new Insets(15, 10, 15, 10);

        RoundButton giocaButton = new RoundButton("Gioca", COLORE_VERDE, COLORE_VERDE_HOVER, customFontBottone, new Dimension(280, 75));
        giocaButton.addActionListener(e -> {
            cardLayout.show(dynamicContentPanel, "MODE");
        });

        RoundButton esciButton = new RoundButton("Esci", COLORE_ARANCIO, COLORE_ARANCIO_HOVER, customFontBottone, new Dimension(280, 75));
        esciButton.addActionListener(e -> System.exit(0));

        panel.add(giocaButton, gbc);
        gbc.gridy++;
        panel.add(esciButton, gbc);
        return panel;
    }

    private JPanel createModeSelectionPanel() {
        StyledPanel panel = new StyledPanel(COLORE_SFONDO_BASE.darker());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(20, 20, 20, 20); 

        JLabel titleLabel = new JLabel("SCEGLI MODALITA'");
        titleLabel.setFont(customFontBottone.deriveFont(20f));
        titleLabel.setForeground(COLORE_TESTO);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titleLabel, gbc);

        JPanel horizontalButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10)); 
        horizontalButtonsPanel.setOpaque(false);

        RoundButton umanoButton = new RoundButton("1 VS 1", COLORE_VERDE, COLORE_VERDE_HOVER, customFontDialog, new Dimension(180, 60));
        umanoButton.addActionListener(e -> {
            startGame(false, "");
        });

        RoundButton botButton = new RoundButton("1 VS BOT", COLORE_VERDE, COLORE_VERDE_HOVER, customFontDialog, new Dimension(180, 60));
        botButton.addActionListener(e -> {
            cardLayout.show(dynamicContentPanel, "DIFFICULTY");
        });

        horizontalButtonsPanel.add(umanoButton);
        horizontalButtonsPanel.add(botButton);
        
        gbc.gridy++;
        gbc.insets = new Insets(10, 20, 20, 20); 
        panel.add(horizontalButtonsPanel, gbc);
        
        RoundButton cancelButton = new RoundButton("Annulla", COLORE_ARANCIO, COLORE_ARANCIO_HOVER, customFontDialog, new Dimension(120, 55));
        cancelButton.addActionListener(e -> {
            cardLayout.show(dynamicContentPanel, "MAIN"); 
        });

        gbc.gridy++;
        gbc.gridwidth = 2; 
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 20, 30, 20); 
        panel.add(cancelButton, gbc);

        return panel;
    }
}