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

    // Variabili per il font
    private Font customFontTitolo;
    private Font customFontBottone;
    private Font customFontDialog;

    // Pannelli per la navigazione
    private CardLayout cardLayout;
    private JPanel dynamicContentPanel; // Contenitore che usa il CardLayout

    // ======================================================
    // FONT LOADER
    // ======================================================
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

    // ======================================================
    // COSTRUTTORE PRINCIPALE
    // ======================================================
    public FinestraMenu() {
        // Caricamento finale del font
        customFontTitolo = loadCustomFont(FONT_FILE_NAME, Font.PLAIN, 125f);
        customFontBottone = loadCustomFont(FONT_FILE_NAME, Font.PLAIN, 22f);
        customFontDialog = loadCustomFont(FONT_FILE_NAME, Font.PLAIN, 18f);

        // Impostazioni della finestra
        setTitle("FORZA 4");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Pannello di sfondo
        BackgroundPanel backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(new GridBagLayout());
        setContentPane(backgroundPanel);
        GridBagConstraints gbc = new GridBagConstraints();

        // --- Layout del Menu ---

        // 1. Spazio vuoto superiore
        gbc.gridx = 0; gbc.gridy = 0; gbc.weighty = 0.5;
        backgroundPanel.add(Box.createVerticalGlue(), gbc);

        // 2. Titolo "FORZA 4"
        JLabel titoloLabel = new JLabel("FORZA 4");
        titoloLabel.setFont(customFontTitolo);
        titoloLabel.setForeground(COLORE_TESTO);
        
        JPanel titleWrapper = new JPanel(new GridBagLayout());
        titleWrapper.setOpaque(false);
        titleWrapper.add(titoloLabel);

        gbc.gridy = 1; gbc.weighty = 0; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 50, 0);
        backgroundPanel.add(titleWrapper, gbc);

        // 3. PANNELLO DINAMICO
        // Questo pannello conterrà il menu principale, la scelta modalità o la scelta difficoltà
        cardLayout = new CardLayout();
        dynamicContentPanel = new JPanel(cardLayout);
        dynamicContentPanel.setOpaque(false);

        // Creazione e aggiunta dei pannelli
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

        // 4. Spazio vuoto sotto i bottoni
        gbc.gridy = 3; gbc.weighty = 1.0;
        backgroundPanel.add(Box.createVerticalGlue(), gbc);
        
        // Mostra il pannello iniziale
        cardLayout.show(dynamicContentPanel, "MAIN");
        
        setVisible(true);
    }

    // ======================================================
    // METODI PER LA CREAZIONE DEI PANNELLI DINAMICI
    // ======================================================

    /**
     * Crea il pannello con i bottoni "Gioca" e "Esci"
     */
    private JPanel createMainButtonsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.insets = new Insets(15, 10, 15, 10);

        // Bottone "Gioca"
        RoundButton giocaButton = new RoundButton("Gioca", COLORE_VERDE, COLORE_VERDE_HOVER, customFontBottone, new Dimension(280, 75));
        giocaButton.addActionListener(e -> {
            cardLayout.show(dynamicContentPanel, "MODE");
        });

        // Bottone "Esci"
        RoundButton esciButton = new RoundButton("Esci", COLORE_ARANCIO, COLORE_ARANCIO_HOVER, customFontBottone, new Dimension(280, 75));
        esciButton.addActionListener(e -> System.exit(0));

        panel.add(giocaButton, gbc);
        gbc.gridy++;
        panel.add(esciButton, gbc);

        return panel;
    }

    /**
     * Crea il pannello per la scelta della modalità (Vs Umano, Vs Bot)
     */
    private JPanel createModeSelectionPanel() {
        // Usa StyledPanel per il "riquadro violaceo"
        StyledPanel panel = new StyledPanel(COLORE_SFONDO_BASE.darker());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; // Titolo occupa 2 colonne
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(20, 20, 20, 20); // Spaziatura interna

        // Etichetta Titolo
        JLabel titleLabel = new JLabel("SCEGLI MODALITA'");
        titleLabel.setFont(customFontBottone.deriveFont(20f));
        titleLabel.setForeground(COLORE_TESTO);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titleLabel, gbc);

        // Pannello per i bottoni in orizzontale (ridotta spaziatura a 15px)
        JPanel horizontalButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10)); 
        horizontalButtonsPanel.setOpaque(false);

        // Bottone "Gioca vs Umano" (PIU STRETTO)
        RoundButton umanoButton = new RoundButton("VS UMANO", COLORE_VERDE, COLORE_VERDE_HOVER, customFontDialog, new Dimension(180, 60));
        umanoButton.addActionListener(e -> {
            startGame(false, "");
        });

        // Bottone "Gioca vs Bot" (PIU STRETTO)
        RoundButton botButton = new RoundButton("VS BOT", COLORE_VERDE, COLORE_VERDE_HOVER, customFontDialog, new Dimension(180, 60));
        botButton.addActionListener(e -> {
            cardLayout.show(dynamicContentPanel, "DIFFICULTY");
        });

        horizontalButtonsPanel.add(umanoButton);
        horizontalButtonsPanel.add(botButton);
        
        gbc.gridy++;
        gbc.insets = new Insets(10, 20, 20, 20); // Spaziatura verticale aumentata
        panel.add(horizontalButtonsPanel, gbc);
        
        // Bottone Annulla (centrato, MOLTO PIU STRETTO E PIU CORTO)
        RoundButton cancelButton = new RoundButton("Annulla", COLORE_ARANCIO, COLORE_ARANCIO_HOVER, customFontDialog, new Dimension(120, 55));
        cancelButton.addActionListener(e -> {
            cardLayout.show(dynamicContentPanel, "MAIN"); // Torna al menu principale
        });

        gbc.gridy++;
        gbc.gridwidth = 2; 
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        // Spaziatura inferiore aumentata per alzare il riquadro
        gbc.insets = new Insets(10, 20, 30, 20); 
        panel.add(cancelButton, gbc);

        return panel;
    }

    /**
     * Crea il pannello per la scelta della difficoltà del Bot
     */
    private JPanel createDifficultySelectionPanel() {
        // Usa StyledPanel per il "riquadro violaceo"
        StyledPanel panel = new StyledPanel(COLORE_SFONDO_BASE.darker());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(20, 20, 20, 20); // Spaziatura interna

        // Etichetta del titolo
        JLabel titleLabel = new JLabel("SCEGLI LA DIFFICOLTA' DEL BOT");
        titleLabel.setFont(customFontBottone.deriveFont(20f));
        titleLabel.setForeground(COLORE_TESTO);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titleLabel, gbc);

        // Pannello per i bottoni in orizzontale
        JPanel horizontalButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10)); // 15px di spazio tra bottoni
        horizontalButtonsPanel.setOpaque(false);

        String[] difficulties = {"Facile", "Media", "Difficile"};
        Dimension difficultyButtonSize = new Dimension(140, 60); 

        for (String diff : difficulties) {
            RoundButton button = new RoundButton(diff, COLORE_VERDE, COLORE_VERDE_HOVER, customFontDialog, difficultyButtonSize);
            button.addActionListener(e -> {
                String difficoltaScelta = diff;
                startGame(true, difficoltaScelta);
            });
            horizontalButtonsPanel.add(button);
        }

        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(10, 20, 20, 20); // Spaziatura verticale aumentata
        panel.add(horizontalButtonsPanel, gbc);

        // Bottone Annulla (MOLTO PIU STRETTO E PIU CORTO)
        RoundButton cancelButton = new RoundButton("Annulla", COLORE_ARANCIO, COLORE_ARANCIO_HOVER, customFontDialog, new Dimension(120, 55));
        cancelButton.addActionListener(e -> {
            cardLayout.show(dynamicContentPanel, "MODE"); // Torna alla scelta modalità
        });
        
        gbc.gridy++;
        // Spaziatura inferiore aumentata per alzare il riquadro
        gbc.insets = new Insets(10, 20, 30, 20);
        panel.add(cancelButton, gbc);

        return panel;
    }
    
    /**
     * Metodo helper per avviare il gioco e chiudere il menu
     */
    private void startGame(boolean isVsBot, String difficulty) {
        dispose(); // Chiude la FinestraMenu
        // Avvia la FinestraGioco
        SwingUtilities.invokeLater(() -> new FinestraGioco(isVsBot, difficulty));
    }


    // ======================================================
    // CLASSI DI UTILITÀ (RoundButton, BackgroundPanel, StyledPanel)
    // ======================================================

    /**
     * Pannello riutilizzabile per il "riquadro violaceo"
     */
    private class StyledPanel extends JPanel {
        private Color backgroundColor;
        
        public StyledPanel(Color bgColor) {
            this.backgroundColor = bgColor;
            setLayout(new GridBagLayout());
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Bordo per non far attaccare i componenti
        }
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(backgroundColor);
            // Disegna il rettangolo arrotondato
            g2.fill(new RoundRectangle2D.Double(1, 1, getWidth() - 2, getHeight() - 2, RAGGIO_BORDO, RAGGIO_BORDO));
            g2.dispose();
            super.paintComponent(g);
        }

        public boolean isOpaque() {
            return false;
        }
    }

    private class RoundButton extends JButton {
        private Color baseColor, hoverColor, currentColor;

        public RoundButton(String text, Color base, Color hover, Font font, Dimension size) {
            super(text);
            this.baseColor = base;
            this.hoverColor = hover;
            this.currentColor = base;

            setPreferredSize(size);
            setFont(font);
            setForeground(COLORE_TESTO);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { currentColor = hoverColor; repaint(); }
                public void mouseExited(MouseEvent e) { currentColor = baseColor; repaint(); }
            });
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(currentColor);
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), RAGGIO_BORDO, RAGGIO_BORDO));
            super.paintComponent(g);
            g2.dispose();
        }
    }

    private class BackgroundPanel extends JPanel {
        private final ArrayList<Point> stars = new ArrayList<>();
        private final int NUM_STARS = 100;
        private final Random rand = new Random();

        public BackgroundPanel() {
            addComponentListener(new ComponentAdapter() {
                public void componentResized(ComponentEvent e) { repositionStars(getWidth(), getHeight()); }
            });

            Timer timer = new Timer(50, e -> { muoviStelle(); repaint(); });
            timer.start();
        }

        private void repositionStars(int width, int height) {
            if (width > 0 && height > 0) {
                stars.clear();
                for (int i = 0; i < NUM_STARS; i++) {
                    stars.add(new Point(rand.nextInt(width), rand.nextInt(height)));
                }
            }
        }
        
        private void muoviStelle() {
            int w = getWidth(), h = getHeight();
            if (w <= 0 || h <= 0 || stars.isEmpty()) return;

            for (Point star : stars) {
                star.x = (star.x + (rand.nextInt(3) - 1)) % w;
                star.y = (star.y + (rand.nextInt(3) - 1)) % h;
                if (star.x < 0) star.x += w;
                if (star.y < 0) star.y += h;
            }
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int width = getWidth();
            int height = getHeight();

            if (stars.isEmpty()) { repositionStars(width, height); }

            // Sfondo base
            g2d.setColor(COLORE_SFONDO_BASE);
            g2d.fillRect(0, 0, width, height);

            // Gradiente Nebulose
            float[] dist = {0.0f, 0.4f, 1.0f};

            Point2D center1 = new Point2D.Float(width / 2f, height / 2f);
            Color[] colors1 = {COLORE_NEBULOSA_MAGENTA, COLORE_NEBULOSA_SCURO, COLORE_SFONDO_BASE};
            g2d.setPaint(new RadialGradientPaint(center1, width * 0.7f, dist, colors1));
            g2d.fillOval((int) (width * 0.15), (int) (height * 0.15), (int) (width * 0.7), (int) (height * 0.7));

            Point2D center2 = new Point2D.Float(width * 0.1f, height * 0.9f);
            Color[] colors2 = {COLORE_NEBULOSA_BLU, COLORE_NEBULOSA_SCURO, COLORE_SFONDO_BASE};
            g2d.setPaint(new RadialGradientPaint(center2, width * 0.6f, dist, colors2));
            g2d.fillOval((int) (width * -0.2), (int) (height * 0.5), (int) (width * 0.8), (int) (height * 0.8));

            Point2D center3 = new Point2D.Float(width * 0.9f, height * 0.1f);
            Color[] colors3 = {COLORE_NEBULOSA_MAGENTA, COLORE_NEBULOSA_SCURO, COLORE_SFONDO_BASE};
            g2d.setPaint(new RadialGradientPaint(center3, width * 0.5f, dist, colors3));
            g2d.fillOval((int) (width * 0.5), (int) (height * -0.2), (int) (width * 0.8), (int) (height * 0.8));

            // Stelle
            g2d.setColor(COLORE_STELLA);
            for (Point star : stars) {
                g2d.fillOval(star.x, star.y, rand.nextInt(2) + 1, rand.nextInt(2) + 1);
            }

            g2d.dispose();
        }
    }
}