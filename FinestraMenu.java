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

    public FinestraMenu() {
        //Caricamento finale del font
        customFontTitolo = loadCustomFont(FONT_FILE_NAME, Font.PLAIN, 125f);
        customFontBottone = loadCustomFont(FONT_FILE_NAME, Font.PLAIN, 22f);
        customFontDialog = loadCustomFont(FONT_FILE_NAME, Font.PLAIN, 18f);

        //Impostazioni della finestra principale del gioco
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

        //Titolo "FORZA 4"
        JLabel titoloLabel = new JLabel("FORZA 4");
        titoloLabel.setFont(customFontTitolo);
        titoloLabel.setForeground(COLORE_TESTO);
        
        JPanel titleWrapper = new JPanel(new GridBagLayout());
        titleWrapper.setOpaque(false);
        titleWrapper.add(titoloLabel);

        gbc.gridy = 1; gbc.weighty = 0; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 50, 0);
        backgroundPanel.add(titleWrapper, gbc);

        //Spazio necessario
        gbc.gridy = 2; gbc.weighty = 0.1;
        backgroundPanel.add(Box.createVerticalGlue(), gbc);

        // Ripristina la spaziatura per i bottoni
        gbc.insets = new Insets(15, 10, 15, 10);
        gbc.weighty = 0; gbc.fill = GridBagConstraints.NONE;

        //BOTTONI
        
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

    //Scelta modalità principale
    private class GameModeDialog extends JDialog {
        public GameModeDialog(JFrame owner) {
            super(owner, "Scegli Modalità", true);
            setUndecorated(true);
            setBackground(new Color(0, 0, 0, 0));
            
            JPanel contentPanel = createStyledContentPanel(COLORE_SFONDO_BASE.darker());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0; gbc.gridy = 0; gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(30, 10, 30, 10);
            
            //Etichetta titolo modalità principale
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
                //Avvia la modalità "vs umano"
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
        
        private JPanel wrapButton(JButton button) {
            JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
            wrapper.setOpaque(false);
            wrapper.add(button);
            return wrapper;
        }

        private JPanel createStyledContentPanel(Color bgColor) {
            JPanel panel = new JPanel(new GridBagLayout()) {
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(bgColor);
                    g2.fill(new RoundRectangle2D.Double(1, 1, getWidth() - 2, getHeight() - 2, RAGGIO_BORDO, RAGGIO_BORDO));
                    g2.dispose();
                    super.paintComponent(g);
                }
                public boolean isOpaque() {
                    return false;
                }
            };
            panel.setOpaque(false);
            panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            return panel;
        }
    }

    //Difficoltà bot implementata
    private class BotDifficultyDialog extends JDialog {
        public BotDifficultyDialog(JFrame owner) {
            super(owner, "Scegli Difficoltà", true);
            setUndecorated(true);
            setBackground(new Color(0, 0, 0, 0));
            JPanel contentPanel = new JPanel(new GridBagLayout()) {
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(COLORE_SFONDO_BASE.darker());
                    g2.fill(new RoundRectangle2D.Double(1, 1, getWidth() - 2, getHeight() - 2, RAGGIO_BORDO, RAGGIO_BORDO));
                    g2.dispose();
                    super.paintComponent(g);
                }
                public boolean isOpaque() { return false; }
            };
            contentPanel.setOpaque(false);
            contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0; gbc.gridy = 0; gbc.fill = GridBagConstraints.HORIZONTAL;

            //Etichetta del titolo
            JLabel titleLabel = new JLabel("SCEGLI LA DIFFICOLTA' DEL BOT");
            titleLabel.setFont(customFontBottone.deriveFont(20f));
            titleLabel.setForeground(COLORE_TESTO);
            titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
            gbc.insets = new Insets(30, 10, 30, 10);
            contentPanel.add(titleLabel, gbc);

            String[] difficulties = {"Facile", "Media", "Difficile"};
            gbc.gridy++;

            //Ciclo per creare i bottoni di difficoltà
            for (String diff : difficulties) {
                JPanel buttonWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
                buttonWrapper.setOpaque(false);

                RoundButton button = new RoundButton(diff, COLORE_VERDE, COLORE_VERDE_HOVER, customFontDialog, new Dimension(200, 60));
                button.addActionListener(e -> {
                    String difficoltaScelta = diff;
                    
                    // Avvia la FinestraGioco in modalità Bot
                    owner.dispose();
                    dispose();

                    SwingUtilities.invokeLater(() -> new FinestraGioco(true, difficoltaScelta));
                });

                buttonWrapper.add(button);
                gbc.insets = new Insets(2, 10, 2, 10);
                contentPanel.add(buttonWrapper, gbc);
                gbc.gridy++;
            }

            //Bottone Annulla
            JPanel cancelWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
            cancelWrapper.setOpaque(false);
            RoundButton cancelButton = new RoundButton("Annulla", COLORE_ARANCIO, COLORE_ARANCIO_HOVER, customFontDialog, new Dimension(200, 60));
            cancelButton.addActionListener(e -> dispose());
            cancelWrapper.add(cancelButton);

            gbc.insets = new Insets(30, 10, 40, 10);
            contentPanel.add(cancelWrapper, gbc);

            setContentPane(contentPanel);
            pack();
            setLocationRelativeTo(owner);
        }
    }

    //Animazione con cursore sopra bottoni
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

    //Sfondo del menu
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

            //Sfondo base
            g2d.setColor(COLORE_SFONDO_BASE);
            g2d.fillRect(0, 0, width, height);

            //Sfumatura nebulose
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

            //Stelle
            g2d.setColor(COLORE_STELLA);
            for (Point star : stars) {
                g2d.fillOval(star.x, star.y, rand.nextInt(2) + 1, rand.nextInt(2) + 1);
            }

            g2d.dispose();
        }
    }
}