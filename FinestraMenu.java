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

        JLabel titleLabel = new JLabel("SCEGLI LA MODALITA'");
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

    private JPanel createDifficultySelectionPanel() {
        StyledPanel panel = new StyledPanel(COLORE_SFONDO_BASE.darker());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(20, 20, 20, 20); 

        JLabel titleLabel = new JLabel("SCEGLI LA DIFFICOLTA' DEL BOT");
        titleLabel.setFont(customFontBottone.deriveFont(20f));
        titleLabel.setForeground(COLORE_TESTO);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titleLabel, gbc);

        JPanel horizontalButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10)); 
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
        gbc.insets = new Insets(10, 20, 20, 20); 
        panel.add(horizontalButtonsPanel, gbc);

        RoundButton cancelButton = new RoundButton("Annulla", COLORE_ARANCIO, COLORE_ARANCIO_HOVER, customFontDialog, new Dimension(120, 55));
        cancelButton.addActionListener(e -> {
            cardLayout.show(dynamicContentPanel, "MODE"); 
        });
        
        gbc.gridy++;
        gbc.insets = new Insets(10, 20, 30, 20);
        panel.add(cancelButton, gbc);

        return panel;
    }
    
    private void startGame(boolean isVsBot, String difficulty) {
        setVisible(false);
        SwingUtilities.invokeLater(() -> new FinestraGioco(this, isVsBot, difficulty));
    }


    //Elementi
    private class StyledPanel extends JPanel {
        private Color backgroundColor;
        private final int RAGGIO_BORDO_PANEL = 30;
        
        public StyledPanel(Color bgColor) {
            this.backgroundColor = bgColor;
            setLayout(new GridBagLayout());
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); 
        }
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(backgroundColor);
            g2.fill(new RoundRectangle2D.Double(1, 1, getWidth() - 2, getHeight() - 2, RAGGIO_BORDO_PANEL, RAGGIO_BORDO_PANEL));
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
            
            Color originalColor = g2.getColor();
            g2.setColor(getForeground());
            FontMetrics fm = g2.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(getText())) / 2;
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(getText(), x, y);
            g2.setColor(originalColor);
            
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

            g2d.setColor(COLORE_SFONDO_BASE);
            g2d.fillRect(0, 0, width, height);

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

            g2d.setColor(COLORE_STELLA);
            for (Point star : stars) {
                g2d.fillOval(star.x, star.y, rand.nextInt(2) + 1, rand.nextInt(2) + 1);
            }

            g2d.dispose();
        }
    }
}