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

public class FinestraMenu extends JFrame{
    //Font
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
    //Caricamento del font
    private Font loadCustomFont(String name, int style, float size) {
        try {
            File fontFile = new File(name);
            Font baseFont;
            if (fontFile.exists()) {
                baseFont = Font.createFont(Font.TRUETYPE_FONT, fontFile);
            } else {
                // Tenta di caricare dalla risorsa interna (utile in JAR)
                baseFont = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/" + name));
            }
            return baseFont.deriveFont(style, size);
        } catch (FontFormatException | IOException | NullPointerException e) {
            System.err.println("Errore caricamento font: " + e.getMessage());
            return new Font("Serif", Font.BOLD, (int) size);
        }
    }
    //Costruttore
    public FinestraMenu(){
        //Dimnensioni del font
        customFontTitolo = loadCustomFont(FONT_FILE_NAME, Font.PLAIN, 125f);
        customFontBottone = loadCustomFont(FONT_FILE_NAME, Font.PLAIN, 22f);
        customFontDialog = loadCustomFont(FONT_FILE_NAME, Font.PLAIN, 18f);
        //Impostazioni della finestra iniziale
        setTitle("FORZA 4");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        //Sfondo
        BackgroundPanel backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(new GridBagLayout());
        setContentPane(backgroundPanel);
        GridBagConstraints gbc = new GridBagConstraints();
        //Spazio vuoto superiore
        gbc.gridx = 0; gbc.gridy = 0; gbc.weighty = 0.5;
        backgroundPanel.add(Box.createVerticalGlue(), gbc);
        //Titolo
        JLabel titoloLabel = new JLabel("FORZA 4");
        titoloLabel.setFont(customFontTitolo);
        titoloLabel.setForeground(COLORE_TESTO);
        JPanel titleWrapper = new JPanel(new GridBagLayout());
        titleWrapper.setOpaque(false);
        titleWrapper.add(titoloLabel);
        gbc.gridy = 1; gbc.weighty = 0; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 50, 0);
        backgroundPanel.add(titleWrapper, gbc);
        gbc.gridy = 2; gbc.weighty = 0.1;
        backgroundPanel.add(Box.createVerticalGlue(), gbc);
        //Ripristina la spaziatura per i bottoni
        gbc.insets = new Insets(15, 10, 15, 10);
        gbc.weighty = 0; gbc.fill = GridBagConstraints.NONE;
        //Bottoni nel menu principale
        //Bottone "Gioca"
        RoundButton giocaButton = new RoundButton("Gioca", COLORE_VERDE, COLORE_VERDE_HOVER, customFontBottone, new Dimension(280, 75));
        giocaButton.addActionListener(e -> {
            new GameModeDialog(this).setVisible(true);
        });
        //Bottone "Esci"
        RoundButton esciButton = new RoundButton("Esci", COLORE_ARANCIO, COLORE_ARANCIO_HOVER, customFontBottone, new Dimension(280, 75));
        esciButton.addActionListener(e -> System.exit(0));
        gbc.gridy = 3; 
        backgroundPanel.add(giocaButton, gbc);
        gbc.gridy = 4;
        backgroundPanel.add(esciButton, gbc);
        //Spazio vuoto sotto i bottoni
        gbc.gridy = 5; gbc.weighty = 1.0;
        backgroundPanel.add(Box.createVerticalGlue(), gbc);
        setVisible(true);
    }
    //Scelta della modalità
    private class GameModeDialog extends JDialog{
        public GameModeDialog(JFrame owner) {
            super(owner, "Scegli Modalità", true);
            setUndecorated(true);
            setBackground(new Color(0, 0, 0, 0));
            JPanel contentPanel = createStyledContentPanel(COLORE_SFONDO_BASE.darker());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0; gbc.gridy = 0; gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(30, 10, 30, 10);
            //Etichetta titolo modalità
            JLabel titleLabel = new JLabel("SCEGLI MODALITA'");
            titleLabel.setFont(customFontBottone.deriveFont(20f));
            titleLabel.setForeground(COLORE_TESTO);
            titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
            contentPanel.add(titleLabel, gbc);
            //Bottone "Gioca vs Umano"
            RoundButton umanoButton = new RoundButton("VS UMANO", COLORE_VERDE, COLORE_VERDE_HOVER, customFontDialog, new Dimension(200, 60));
            umanoButton.addActionListener(e -> {
                owner.dispose();
                dispose();
                //Avvia la modalità vs umano 
                SwingUtilities.invokeLater(() -> new FinestraGioco(false, "")); 
            });
            //Bottone "Gioca vs Bot"
            RoundButton botButton = new RoundButton("VS BOT", COLORE_VERDE, COLORE_VERDE_HOVER, customFontDialog, new Dimension(200, 60));
            botButton.addActionListener(e -> {
                new BotDifficultyDialog(owner).setVisible(true);
                dispose();
            });
            //Aggiunta dei bottoni al pannello
            gbc.gridy++; gbc.insets = new Insets(20, 10, 20, 10);
            contentPanel.add(wrapButton(umanoButton), gbc);
            gbc.gridy++;
            contentPanel.add(wrapButton(botButton), gbc);
            //Bottone Annulla
            RoundButton cancelButton = new RoundButton("Annulla", COLORE_ARANCIO, COLORE_ARANCIO_HOVER, customFontDialog, new Dimension(200, 60));
            cancelButton.addActionListener(e -> dispose());
            gbc.gridy++; gbc.insets = new Insets(30, 10, 40, 10);
            contentPanel.add(wrapButton(cancelButton), gbc);
            setContentPane(contentPanel);
            pack();
            setLocationRelativeTo(owner);
        }
    }
}