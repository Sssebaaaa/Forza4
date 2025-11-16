import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.IOException;

public class FinestraGioco extends JFrame {

    //Font e costanti
    private static final String FONT_FILE_NAME = "spicy_sale/Spicy Sale.ttf";
    private final int RIGHE = 6;
    private final int COLONNE = 7;
    private final char GIOCATORE_1_CHAR = 'R'; //Rosso giocatore 1
    private final char GIOCATORE_2_CHAR = 'G'; //Giallo giocatore 2

    //Colori
    private final Color COLORE_SFONDO_GRIGIO = new Color(220, 220, 220); 
    private final Color COLORE_SUPPORTO_TAVOLO = new Color(245, 245, 245); 
    private final Color COLORE_LEGNO_CHIARO = new Color(190, 140, 90); 
    private final Color COLORE_LEGNO_MEDIO = new Color(150, 100, 60); 
    private final Color COLORE_LEGNO_BORDO_SCURO = new Color(70, 30, 10); 
    private final Color COLORE_LEGNO_OMBRE = new Color(50, 20, 0, 100); 
    private final Color COLORE_ARANCIO = new Color(200, 112, 26); 
    private final Color COLORE_ARANCIO_HOVER = new Color(180, 92, 16); 
    private final Color COLORE_TESTO = Color.DARK_GRAY; 
    private final Color COLORE_ROSSO_PEDINA = new Color(180, 0, 0); 
    private final Color COLORE_GIALLO_PEDINA = new Color(200, 160, 0); 
    private final Color COLORE_SLOT_VUOTO_INTERNO = new Color(80, 50, 20); 
    private final int RAGGIO_BORDO = 20; 
    private Font customFontGioco;

    //Logica del gioco
    private LogicaGioco logica;
    private Bot bot;
    private GestioneReset gestioneReset;

    private boolean isVsBot;
    private String difficoltaBot = "";
    private char giocatoreCorrente;
    private boolean giocoAttivo = false;
    
    //Componenti
    private GameBoardPanel gamePanel;
    private PlayerStatusPanel playerStatusPanel; 
    private final GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    private FinestraMenu parentMenu; 

    public FinestraGioco(FinestraMenu menu, boolean isVsBot, String difficolta) { 
        this.parentMenu = menu; 
        this.isVsBot = isVsBot;
        this.difficoltaBot = difficolta;
        
        this.logica = new LogicaGioco();
        this.bot = new Bot();
        this.gestioneReset = new GestioneReset();
        
        this.giocatoreCorrente = GIOCATORE_1_CHAR; 
        
        initializeGUI();
        iniziaNuovaPartita();
    }
    
    private void initializeGUI() {
        setTitle("FORZA 4");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        setUndecorated(true);
        gd.setFullScreenWindow(this);

        customFontGioco = loadCustomFont(FONT_FILE_NAME, Font.PLAIN, 24f);

        JPanel backgroundPanel = new JPanel();
        backgroundPanel.setBackground(COLORE_SFONDO_GRIGIO);
        backgroundPanel.setLayout(new BorderLayout());
        setContentPane(backgroundPanel);

        JPanel topContainerPanel = new JPanel(new BorderLayout());
        topContainerPanel.setOpaque(false);
        topContainerPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 20, 50)); 
        playerStatusPanel = new PlayerStatusPanel();
        topContainerPanel.add(playerStatusPanel, BorderLayout.EAST);

        final int SPACING_WIDTH = 250; 
        JPanel emptyWestPanel = new JPanel();
        emptyWestPanel.setOpaque(false);
        emptyWestPanel.setPreferredSize(new Dimension(SPACING_WIDTH, 1)); 
        topContainerPanel.add(emptyWestPanel, BorderLayout.WEST);

        JLabel titolo = new JLabel("FORZA 4");
        titolo.setFont(customFontGioco.deriveFont(42f));
        titolo.setForeground(COLORE_TESTO);
        titolo.setHorizontalAlignment(SwingConstants.CENTER); 
        
        JPanel titleWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER)); 
        titleWrapper.setOpaque(false);
        titleWrapper.add(titolo);

        topContainerPanel.add(titleWrapper, BorderLayout.CENTER);
        backgroundPanel.add(topContainerPanel, BorderLayout.NORTH);
        
        gamePanel = new GameBoardPanel();
        backgroundPanel.add(gamePanel, BorderLayout.CENTER); 
        
        // Aggiungo il listener qui, così posso riaggiungerlo dopo il reset, se necessario.
        gamePanel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (!giocoAttivo) return;

                if (isVsBot && giocatoreCorrente == GIOCATORE_2_CHAR) {
                    return; 
                }
                gestisciMossaUtente(e.getX());
            }
        });
        
        addComponentListener(new ComponentAdapter() {
            public void componentShown(ComponentEvent e) {
                gamePanel.calculateBoardDimensions(); 
                gamePanel.repaint();
                updatePlayerStateBox(); 
                removeComponentListener(this); 
            }
        });

        RoundButton backButton = new RoundButton("TORNA AL MENU'", COLORE_ARANCIO, COLORE_ARANCIO_HOVER, customFontGioco.deriveFont(22f), new Dimension(220, 65));
        backButton.addActionListener(e -> tornaAlMenu());

        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        southPanel.setOpaque(false);
        southPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 40, 40));
        southPanel.add(backButton);
        backgroundPanel.add(southPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    //Gestione bot
    private void iniziaNuovaPartita() {
        gestioneReset.resetPartita(logica); 
        giocatoreCorrente = GIOCATORE_1_CHAR;
        giocoAttivo = true;
        
        updatePlayerStateBox();
        gamePanel.repaint();

        if (gamePanel.getMouseListeners().length == 0) {
            gamePanel.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (!giocoAttivo) return;
                    if (isVsBot && giocatoreCorrente == GIOCATORE_2_CHAR) return;
                    gestisciMossaUtente(e.getX());
                }
            });
        }
    }
    
    private void updatePlayerStateBox() {
        String name;
        Color color;
        
        if (giocatoreCorrente == GIOCATORE_1_CHAR) {
            name = "GIOCATORE 1";
            color = COLORE_ROSSO_PEDINA;
        } else if (isVsBot) {
            name = "Bot (" + difficoltaBot + ")";
            color = COLORE_GIALLO_PEDINA;
        } else {
            name = "GIOCATORE 2";
            color = COLORE_GIALLO_PEDINA;
        }
        
        playerStatusPanel.updateStatus(name, color);
    }

    private void gestisciMossaUtente(int clickX) {
        if (!giocoAttivo) return;

        int cellWidth = gamePanel.getCalculatedCellWidth(); 
        int colonna = (clickX - gamePanel.getBoardStartX()) / cellWidth; 

        if (colonna >= 0 && colonna < COLONNE) {
            eseguiMossa(colonna, giocatoreCorrente);
        }
    }

    private void gestisciMossaBot() {
        if (!giocoAttivo || giocatoreCorrente != GIOCATORE_2_CHAR || !isVsBot) return;

        Timer botTimer = new Timer(500, e -> {
            int colonnaBot = bot.giocaTurnoBot(logica, GIOCATORE_2_CHAR, GIOCATORE_1_CHAR, difficoltaBot);
            eseguiMossa(colonnaBot, giocatoreCorrente);
            ((Timer)e.getSource()).stop();
        });
        botTimer.setRepeats(false);
        botTimer.start();
    }

    private void eseguiMossa(int colonna, char giocatore) {
        boolean mossaValida = logica.inserisciPedina(colonna, giocatore);
        
        if (mossaValida) { 
            gamePanel.repaint();

            // 1. Controlla Vittoria
            if (logica.controlloVittoria(giocatore)) {
                giocoAttivo = false;
                String vincitore;
                if (giocatore == GIOCATORE_1_CHAR) {
                    vincitore = "IL GIOCATORE 1 HA VINTO!";
                } else if (isVsBot) {
                    vincitore = "IL BOT HA VINTO!";
                } else {
                    vincitore = "IL GIOCATORE 2 HA VINTO!";
                }
                
                playerStatusPanel.updateStatus("VITTORIA!", (giocatore == GIOCATORE_1_CHAR) ? COLORE_ROSSO_PEDINA : COLORE_GIALLO_PEDINA);
                mostraDialogoFinePartita(vincitore);
                return;
            }

            // 2. Controlla Pareggio
            if (logica.controllaPareggio()) {
                giocoAttivo = false;
                playerStatusPanel.updateStatus("PAREGGIO", Color.GRAY);
                mostraDialogoFinePartita("PAREGGIO!");
                return;
            }

            // 3. Cambia Turno
            giocatoreCorrente = logica.cambiaGiocatore(giocatore);
            
            updatePlayerStateBox(); 

            // 4. Se è turno del Bot, chiama la mossa del Bot
            if (isVsBot && giocatoreCorrente == GIOCATORE_2_CHAR) {
                gestisciMossaBot();
            }
        } 
    }
    
    private void mostraDialogoFinePartita(String messaggio) {
        //Blocco interazione con il tabellone
        giocoAttivo = false; 

        //Evitare click fantasma
        MouseListener[] listeners = gamePanel.getMouseListeners();
        for (MouseListener ml : listeners) {
            gamePanel.removeMouseListener(ml);
        }

        //Mostra il dialogo modale
        CustomGameDialog dialog = new CustomGameDialog(this, "PARTITA TERMINATA", messaggio);
        dialog.setVisible(true);

        //Se il dialogo si chiude per un motivo diverso dai bottoni, ripristino il gioco (anche se non dovrebbe accadere)
        if (giocoAttivo == false) {
        }
    }
    
    private void tornaAlMenu() {
        gd.setFullScreenWindow(null); 
        dispose(); 
        
        if (parentMenu != null) {
            SwingUtilities.invokeLater(() -> {
                //Assumo che FinestraMenu abbia questo metodo per ripristinare il suo stato iniziale
                parentMenu.tornaAllaSelezionePrincipale(); 
                parentMenu.setVisible(true);
            });
        } else {
            //Crea una nuova istanza di FinestraMenu se non è stata passata
            SwingUtilities.invokeLater(() -> new FinestraMenu()); 
        }
    }

    private class CustomGameDialog extends JDialog {
        
        public CustomGameDialog(JFrame owner, String title, String message) {
            super(owner, title, true); 
            setUndecorated(true); 
            setBackground(new Color(0, 0, 0, 0)); 
            
            JPanel contentPanel = new JPanel(new GridBagLayout());
            contentPanel.setOpaque(false);
            
            StyledPanel dialogPanel = new StyledPanel(new Color(50, 50, 50)); 
            dialogPanel.setLayout(new GridBagLayout());
            dialogPanel.setPreferredSize(new Dimension(450, 200));
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;

            //PRIMA RIGA: Vincitore (usa il messaggio passato)
            JLabel winnerLabel = new JLabel(message);
            winnerLabel.setFont(customFontGioco.deriveFont(24f)); // Font grande e personalizzato
            winnerLabel.setForeground(Color.WHITE);
            winnerLabel.setHorizontalAlignment(SwingConstants.CENTER);
            dialogPanel.add(winnerLabel, gbc);

            //SECONDA RIGA: Domanda
            JLabel questionLabel = new JLabel("COSA VUOI FARE ORA?");
            questionLabel.setFont(customFontGioco.deriveFont(18f)); // Font più piccolo
            questionLabel.setForeground(new Color(200, 200, 200)); 
            questionLabel.setHorizontalAlignment(SwingConstants.CENTER);
            gbc.gridy = 1;
            dialogPanel.add(questionLabel, gbc);

            //PULSANTE 1: RICOMINCIA
            RoundButton yesButton = new RoundButton("RICOMINCIA", new Color(30, 138, 76), new Color(20, 118, 66), customFontGioco.deriveFont(18f), new Dimension(160, 50));
            yesButton.addActionListener(e -> {
                dispose();
                iniziaNuovaPartita(); //Riavvia la partita nel frame esistente
            });
            
            gbc.gridy = 2; gbc.gridwidth = 1; gbc.weightx = 0.5; gbc.anchor = GridBagConstraints.EAST;
            gbc.insets = new Insets(20, 10, 20, 10);
            dialogPanel.add(yesButton, gbc);

            //PULSANTE 2: TORNA AL MENU
            RoundButton noButton = new RoundButton("TORNA AL MENU", COLORE_ARANCIO, COLORE_ARANCIO_HOVER, customFontGioco.deriveFont(18f), new Dimension(160, 50));
            noButton.addActionListener(e -> {
                dispose();
                tornaAlMenu(); //Chiude FinestraGioco e torna al Menu
            });
            
            gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
            dialogPanel.add(noButton, gbc);

            contentPanel.add(dialogPanel, new GridBagConstraints());
            setContentPane(contentPanel);
            pack();
            setLocationRelativeTo(owner); 
        }
    }
    
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
        public boolean isOpaque() { return false; }
    }

    private class PlayerStatusPanel extends JPanel {
        private String playerName = "";
        private Color pedinaColor = COLORE_ROSSO_PEDINA; 
        private final int PEDINA_RADIUS = 20;

        public PlayerStatusPanel() {
            setPreferredSize(new Dimension(220, 100));
            setMinimumSize(new Dimension(220, 100));
            setMaximumSize(new Dimension(220, 100));
            setOpaque(false); 
        }
        public void updateStatus(String name, Color color) {
            this.playerName = name;
            this.pedinaColor = color;
            repaint();
        }
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int width = getWidth();
            int height = getHeight();
            int arc = 20;
            g2d.setColor(COLORE_SUPPORTO_TAVOLO); 
            g2d.fill(new RoundRectangle2D.Double(0, 0, width - 1, height - 1, arc, arc));
            g2d.setColor(COLORE_TESTO.darker());
            g2d.setStroke(new BasicStroke(2));
            g2d.draw(new RoundRectangle2D.Double(0, 0, width - 1, height - 1, arc, arc));
            g2d.setStroke(new BasicStroke(1));
            g2d.setFont(customFontGioco.deriveFont(22f));
            g2d.setColor(COLORE_TESTO);
            FontMetrics fm = g2d.getFontMetrics();
            int textX = (width - fm.stringWidth(playerName)) / 2;
            int textY = 30 + fm.getAscent() / 2;
            g2d.drawString(playerName, textX, textY);
            int pedinaX = (width - PEDINA_RADIUS * 2) / 2;
            int pedinaY = height - PEDINA_RADIUS * 2 - 15; 
            RadialGradientPaint rgp = new RadialGradientPaint(
                pedinaX + PEDINA_RADIUS - PEDINA_RADIUS / 3, pedinaY + PEDINA_RADIUS - PEDINA_RADIUS / 3, 
                PEDINA_RADIUS * 1.5f, 
                new float[]{0f, 1f},
                new Color[]{Color.WHITE, pedinaColor} 
            );
            g2d.setPaint(rgp);
            g2d.fillOval(pedinaX, pedinaY, PEDINA_RADIUS * 2, PEDINA_RADIUS * 2);
            g2d.setColor(Color.BLACK.darker());
            g2d.setStroke(new BasicStroke(1.5f));
            g2d.drawOval(pedinaX, pedinaY, PEDINA_RADIUS * 2, PEDINA_RADIUS * 2);
            g2d.setStroke(new BasicStroke(1)); 
            g2d.dispose();
        }
    }

    private class GameBoardPanel extends JPanel {
        private int boardWidth;
        private int boardHeight;
        private int cellWidth;
        private int cellHeight;
        private int radius;
        private int boardStartX; 
        private int boardStartY; 
        
        public GameBoardPanel() {
            setOpaque(false); 
            setPreferredSize(new Dimension(1000, 800)); 
            
            addComponentListener(new ComponentAdapter() {
                public void componentResized(ComponentEvent e) {
                    calculateBoardDimensions(); 
                    repaint(); 
                }
            });
            calculateBoardDimensions(); 
        }

        public void calculateBoardDimensions() {
            int panelWidth = getWidth();
            int panelHeight = getHeight();
            
            if (panelWidth <= 0 || panelHeight <= 0) {
                panelWidth = getPreferredSize().width;
                panelHeight = getPreferredSize().height;
            }

            double gridAspectRatio = (double) COLONNE / RIGHE; 
            
            int targetGridHeight = (int) (panelHeight * 0.55); 
            boardWidth = (int) (targetGridHeight * gridAspectRatio);
            boardHeight = targetGridHeight; 

            if (boardWidth > panelWidth * 0.85) { 
                boardWidth = (int) (panelWidth * 0.85);
                boardHeight = (int) (boardWidth / gridAspectRatio);
            }

            cellWidth = boardWidth / COLONNE;
            cellHeight = boardHeight / RIGHE;
            radius = (int) (Math.min(cellWidth, cellHeight) * 0.4); 

            boardStartX = (panelWidth - boardWidth) / 2;
            boardStartY = (panelHeight - boardHeight) / 2 - (int)(cellHeight * 0.5); 
        }

        public int getBoardStartX() { return boardStartX; }
        public int getCalculatedCellWidth() { return cellWidth; }
        
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create(); 
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            
            calculateBoardDimensions(); 

            int frameThickness = (int)(cellWidth * 0.4); 
            
            int gridX = boardStartX;
            int gridY = boardStartY;
            int gridW = boardWidth;
            int gridH = boardHeight;

            int totalBoardX = gridX - frameThickness;
            int totalBoardY = gridY - frameThickness; 
            int totalBoardW = gridW + frameThickness * 2;
            int totalBoardH = gridH + frameThickness * 2;

            int cornerArcFrame = RAGGIO_BORDO + frameThickness; 

            //Tavolo
            int tableTopY = gridY + gridH - (int)(cellHeight * 0.5); 
            if (tableTopY < 0) tableTopY = 0; 
            
            g2d.setColor(COLORE_SUPPORTO_TAVOLO.darker()); 
            g2d.fillRoundRect(0, tableTopY, getWidth(), getHeight() - tableTopY, 20, 20); 
            g2d.setColor(COLORE_SUPPORTO_TAVOLO); 
            g2d.fillRoundRect(5, tableTopY + 5, getWidth() - 10, getHeight() - tableTopY - 5, 15, 15);
            g2d.setColor(COLORE_SUPPORTO_TAVOLO.darker().darker()); 
            g2d.setStroke(new BasicStroke(2));
            g2d.drawLine(5, tableTopY + 5, getWidth() - 5, tableTopY + 5);
            g2d.setStroke(new BasicStroke(1)); 


            //Tabellone
            
            // 1. La cornice esterna
            g2d.setColor(COLORE_LEGNO_MEDIO);
            g2d.fill(new RoundRectangle2D.Double(totalBoardX, totalBoardY, totalBoardW, totalBoardH, cornerArcFrame, cornerArcFrame));
            
            // 2. Bordo esterno della cornice
            g2d.setStroke(new BasicStroke(frameThickness / 2));
            g2d.setColor(COLORE_LEGNO_BORDO_SCURO);
            g2d.draw(new RoundRectangle2D.Double(totalBoardX, totalBoardY, totalBoardW, totalBoardH, cornerArcFrame, cornerArcFrame));
            g2d.setStroke(new BasicStroke(1));


            // 3. La "griglia"
            g2d.setColor(COLORE_LEGNO_CHIARO);
            g2d.fillRoundRect(gridX, gridY, gridW, gridH, RAGGIO_BORDO, RAGGIO_BORDO);
            g2d.setColor(COLORE_LEGNO_BORDO_SCURO);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRoundRect(gridX, gridY, gridW, gridH, RAGGIO_BORDO, RAGGIO_BORDO);
            g2d.setStroke(new BasicStroke(1));
            

            //Slot e pedine
            char[][] tab = logica.getTabellone();
            for (int i = 0; i < RIGHE; i++) {
                for (int j = 0; j < COLONNE; j++) {
                    //Calcola le coordinate centrali per ogni slot
                    int centerX = gridX + j * cellWidth + cellWidth / 2;
                    int centerY = gridY + i * cellHeight + cellHeight / 2;
                    int x = centerX - radius;
                    int y = centerY - radius;
                    
                    //Disegna lo slot vuoto
                    g2d.setColor(COLORE_SLOT_VUOTO_INTERNO);
                    g2d.fillOval(x, y, radius * 2, radius * 2);

                    //Aggiungi un'ombra leggera per profondità allo slot
                    g2d.setColor(COLORE_LEGNO_OMBRE); 
                    g2d.fillOval(x + radius / 4, y + radius / 4, radius * 2 - radius / 2, radius * 2 - radius / 2);
                    g2d.setColor(Color.BLACK.darker());
                    g2d.drawOval(x, y, radius * 2, radius * 2); 

                    //Se c'è una pedina, disegnala
                    if (tab[i][j] != ' ') {
                        Color pedinaColor = (tab[i][j] == GIOCATORE_1_CHAR) ? COLORE_ROSSO_PEDINA : COLORE_GIALLO_PEDINA;
                        
                        //Pedina con effetto 3D (gradiente radiale per la lucentezza)
                        RadialGradientPaint rgp = new RadialGradientPaint(
                            centerX - radius / 3, centerY - radius / 3, 
                            radius * 1.5f, 
                            new float[]{0f, 1f},
                            new Color[]{Color.WHITE, pedinaColor} 
                        );
                        g2d.setPaint(rgp);
                        g2d.fillOval(x, y, radius * 2, radius * 2);
                        
                        // Contorno per le pedine
                        g2d.setColor(Color.BLACK.darker());
                        g2d.setStroke(new BasicStroke(1.5f));
                        g2d.drawOval(x, y, radius * 2, radius * 2);
                        g2d.setStroke(new BasicStroke(1)); 
                    }
                }
            }
            g2d.dispose();
        }
    }

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
    
    private class RoundButton extends JButton {
        private Color baseColor, hoverColor, currentColor;

        public RoundButton(String text, Color base, Color hover, Font font, Dimension size) {
            super(text);
            this.baseColor = base;
            this.hoverColor = hover;
            this.currentColor = base;
            setPreferredSize(size);
            setFont(font);
            setForeground(Color.WHITE); 
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
}