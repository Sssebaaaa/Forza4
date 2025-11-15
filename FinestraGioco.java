import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.IOException;

public class FinestraGioco extends JFrame {

    // --- FONT E COSTANTI ---
    private static final String FONT_FILE_NAME = "spicy_sale/Spicy Sale.ttf";
    private final int RIGHE = 6;
    private final int COLONNE = 7;
    private final char GIOCATORE_1_CHAR = 'R'; // Rosso (P1)
    private final char GIOCATORE_2_CHAR = 'G'; // Giallo (P2 / Bot)

    // --- COLORI SFONDO E SUPPORTO ---
    private final Color COLORE_SFONDO_GRIGIO = new Color(220, 220, 220); // Grigio chiaro
    private final Color COLORE_SUPPORTO_TAVOLO = new Color(245, 245, 245); // Tavolo legno bianco/grigio chiaro
    
    // Colori per il tabellone (LEGNO) - Pulito, lasciando solo quelli usati
    private final Color COLORE_LEGNO_CHIARO = new Color(190, 140, 90); 
    private final Color COLORE_LEGNO_MEDIO = new Color(150, 100, 60); 
    private final Color COLORE_LEGNO_BORDO_SCURO = new Color(70, 30, 10); // Per i bordi esterni più scuri
    private final Color COLORE_LEGNO_OMBRE = new Color(50, 20, 0, 100); // Per ombre leggere

    private final Color COLORE_ARANCIO = new Color(200, 112, 26); 
    private final Color COLORE_ARANCIO_HOVER = new Color(180, 92, 16); 
    private final Color COLORE_TESTO = Color.DARK_GRAY; 

    // Pedine: Rosso e Giallo
    private final Color COLORE_ROSSO_PEDINA = new Color(180, 0, 0); // Rosso scuro
    private final Color COLORE_GIALLO_PEDINA = new Color(200, 160, 0); // Giallo scuro
    // Colore per lo slot vuoto all'interno del legno (per dare profondità)
    private final Color COLORE_SLOT_VUOTO_INTERNO = new Color(80, 50, 20); 

    private final int RAGGIO_BORDO = 20; //Raggio per i bordi arrotondati
    private Font customFontGioco;

    //Logica
    private LogicaGioco logica;
    private Bot bot;
    private GestioneReset gestioneReset;

    private boolean isVsBot;
    private String difficoltaBot = "";
    private char giocatoreCorrente;
    private boolean giocoAttivo = false;
    
    //Componenti
    private GameBoardPanel gamePanel;
    private PlayerStatusPanel playerStatusPanel; // Pannello per lo stato del giocatore
    private final GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

    public FinestraGioco(boolean isVsBot, String difficolta) {
        this.isVsBot = isVsBot;
        this.difficoltaBot = difficolta;
        this.logica = new LogicaGioco();
        this.bot = new Bot();
        this.gestioneReset = new GestioneReset();
        this.giocatoreCorrente = GIOCATORE_1_CHAR; 
        
        initializeGUI();
        iniziaNuovaPartita();
    }
    
    // Inizializza l'interfaccia grafica
    private void initializeGUI() {
        setTitle("FORZA 4 CONNECT - Partita");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true);
        gd.setFullScreenWindow(this);

        customFontGioco = loadCustomFont(FONT_FILE_NAME, Font.PLAIN, 24f);

        // --- BACKGROUND PANEL (Semplice grigio) ---
        JPanel backgroundPanel = new JPanel();
        backgroundPanel.setBackground(COLORE_SFONDO_GRIGIO);
        backgroundPanel.setLayout(new BorderLayout());
        setContentPane(backgroundPanel);

        // --- Pannello Superiore (NORTH) per Titolo e Box Stato ---
        JPanel topContainerPanel = new JPanel(new BorderLayout());
        topContainerPanel.setOpaque(false);
        topContainerPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 20, 50)); // Padding

        // 1. Box Stato Giocatore (EAST - Alto a destra)
        playerStatusPanel = new PlayerStatusPanel();
        topContainerPanel.add(playerStatusPanel, BorderLayout.EAST);

        // 2. Pannello vuoto (WEST) per spingere il titolo (CENTER) a destra
        // Regola il valore 250 per cambiare il grado di spostamento a destra.
        // Un valore più grande sposta il titolo più a destra.
        final int SPACING_WIDTH = 250; 
        JPanel emptyWestPanel = new JPanel();
        emptyWestPanel.setOpaque(false);
        emptyWestPanel.setPreferredSize(new Dimension(SPACING_WIDTH, 1)); 
        topContainerPanel.add(emptyWestPanel, BorderLayout.WEST);

        // 3. Titolo "FORZA 4" (CENTER) - Ora è posizionato a destra a causa dello spazio a OVEST
        JLabel titolo = new JLabel("FORZA 4");
        titolo.setFont(customFontGioco.deriveFont(42f));
        titolo.setForeground(COLORE_TESTO);
        titolo.setHorizontalAlignment(SwingConstants.CENTER); 
        
        // Questo pannello avvolge il titolo e assicura che il titolo si centri nello spazio CENTER disponibile
        JPanel titleWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER)); 
        titleWrapper.setOpaque(false);
        titleWrapper.add(titolo);

        topContainerPanel.add(titleWrapper, BorderLayout.CENTER);
        
        backgroundPanel.add(topContainerPanel, BorderLayout.NORTH);
        
        // --- Pannello di Gioco (CENTER) ---
        gamePanel = new GameBoardPanel();
        
        backgroundPanel.add(gamePanel, BorderLayout.CENTER); 
        
        // Listener per il click sul tabellone
        gamePanel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (!giocoAttivo) return;

                if (isVsBot && giocatoreCorrente == GIOCATORE_2_CHAR) {
                    return; // Ignora i click durante il turno del bot
                }
                gestisciMossaUtente(e.getX());
            }
        });
        
        // Forza il ricalcolo delle dimensioni al primo show
        addComponentListener(new ComponentAdapter() {
            public void componentShown(ComponentEvent e) {
                gamePanel.calculateBoardDimensions(); 
                gamePanel.repaint();
                updatePlayerStateBox(); // Inizializza il box dello stato
                removeComponentListener(this); 
            }
        });

        // --- Pulsante Torna al Menu (SOUTH) ---
        RoundButton backButton = new RoundButton("Torna al Menu", COLORE_ARANCIO, COLORE_ARANCIO_HOVER, customFontGioco.deriveFont(22f), new Dimension(220, 65));
        backButton.addActionListener(e -> tornaAlMenu());

        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        southPanel.setOpaque(false);
        southPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 40, 40));
        southPanel.add(backButton);
        backgroundPanel.add(southPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    // LOGICA DI GIOCO E GESTIONE BOT
    private void iniziaNuovaPartita() {
        gestioneReset.resetPartita(logica);
        giocatoreCorrente = GIOCATORE_1_CHAR;
        giocoAttivo = true;
        
        updatePlayerStateBox();
        gamePanel.repaint();
    }
    
    
    // Aggiorna il pannello in alto a destra
    private void updatePlayerStateBox() {
        String name;
        Color color;
        
        if (giocatoreCorrente == GIOCATORE_1_CHAR) {
            name = "Giocatore 1";
            color = COLORE_ROSSO_PEDINA;
        } else if (isVsBot) {
            name = "Bot";
            color = COLORE_GIALLO_PEDINA;
        } else {
            name = "Giocatore 2";
            color = COLORE_GIALLO_PEDINA;
        }
        
        playerStatusPanel.updateStatus(name, color);
    }

    private void gestisciMossaUtente(int clickX) {
        if (!giocoAttivo) return;

        // Calcola la colonna in base al click, considerando la posizione del tabellone
        int cellWidth = gamePanel.getCalculatedCellWidth(); 
        int colonna = (clickX - gamePanel.getBoardStartX()) / cellWidth; 

        if (colonna >= 0 && colonna < COLONNE) {
            eseguiMossa(colonna, giocatoreCorrente);
        }
    }

    private void gestisciMossaBot() {
        if (!giocoAttivo || giocatoreCorrente != GIOCATORE_2_CHAR || !isVsBot) return;

        // Ritardo per simulare il "pensiero" del bot
        Timer botTimer = new Timer(500, e -> {
            int colonnaBot = bot.giocaTurnoBot(logica, GIOCATORE_2_CHAR, GIOCATORE_1_CHAR, difficoltaBot);
            eseguiMossa(colonnaBot, giocatoreCorrente);
            ((Timer)e.getSource()).stop();
        });
        botTimer.setRepeats(false);
        botTimer.start();
    }
    
    private void eseguiMossa(int colonna, char giocatore) {
        int riga = logica.inserisciPedina(colonna, giocatore);
        
        if (riga != -1) { // Mossa valida
            gamePanel.repaint();

            // 1. Controlla Vittoria
            if (logica.controlloVittoria(giocatore)) {
                giocoAttivo = false;
                String vincitore;
                if (giocatore == GIOCATORE_1_CHAR) {
                    vincitore = "Rosso (P1)";
                    playerStatusPanel.updateStatus("Vittoria P1!", COLORE_ROSSO_PEDINA);
                } else if (isVsBot) {
                    vincitore = "Bot (" + difficoltaBot + ")";
                    playerStatusPanel.updateStatus("Vittoria Bot!", COLORE_GIALLO_PEDINA);
                } else {
                    vincitore = "Giallo (P2)";
                    playerStatusPanel.updateStatus("Vittoria P2!", COLORE_GIALLO_PEDINA);
                }
                
                mostraDialogoFinePartita("Il giocatore " + vincitore + " ha vinto!");
                return;
            }

            // 2. Controlla Pareggio
            if (logica.controllaPareggio()) {
                giocoAttivo = false;
                playerStatusPanel.updateStatus("Pareggio", Color.GRAY);
                mostraDialogoFinePartita("PAREGGIO! Riprova.");
                return;
            }

            // 3. Cambia Turno
            giocatoreCorrente = logica.cambiaGiocatore(giocatore);
            
            updatePlayerStateBox(); // AGGIORNA IL BOX STATO

            // 4. Se è turno del Bot, chiama la mossa del Bot
            if (isVsBot && giocatoreCorrente == GIOCATORE_2_CHAR) {
                gestisciMossaBot();
            }

        } else {
            // Colonna piena: non facciamo nulla in GUI se non c'è più la statoLabel
            // Si aspetta un nuovo click
        }
    }

    private void mostraDialogoFinePartita(String messaggio) {
        int scelta = JOptionPane.showConfirmDialog(this, 
            messaggio + "\nVuoi iniziare una nuova partita?", 
            "Partita Terminata", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.QUESTION_MESSAGE);

        if (scelta == JOptionPane.YES_OPTION) {
            iniziaNuovaPartita();
        } else {
            tornaAlMenu();
        }
    }
    
    private void tornaAlMenu() {
        gd.setFullScreenWindow(null);
        dispose();
        // FinestraMenu deve essere visibile in modo corretto
        // SwingUtilities.invokeLater(() -> new FinestraMenu().setVisible(true));
    }

    // CLASSE INTERNA (STATO GIOCATORE)

    private class PlayerStatusPanel extends JPanel {
        private String playerName = "";
        private Color pedinaColor = COLORE_ROSSO_PEDINA; // Default P1
        private final int PEDINA_RADIUS = 20;

        public PlayerStatusPanel() {
            // Dimensioni fisse per il box per non farlo collassare
            setPreferredSize(new Dimension(220, 100));
            setMinimumSize(new Dimension(220, 100));
            setMaximumSize(new Dimension(220, 100));
            setOpaque(false); // Renderizza lo sfondo personalizzato
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

            // 1. Disegna il riquadro di sfondo arrotondato
            g2d.setColor(COLORE_SUPPORTO_TAVOLO); // Grigio chiaro
            g2d.fill(new RoundRectangle2D.Double(0, 0, width - 1, height - 1, arc, arc));
            
            // 2. Bordo
            g2d.setColor(COLORE_TESTO.darker());
            g2d.setStroke(new BasicStroke(2));
            g2d.draw(new RoundRectangle2D.Double(0, 0, width - 1, height - 1, arc, arc));
            g2d.setStroke(new BasicStroke(1));

            // 3. Disegna il nome del giocatore/Bot
            g2d.setFont(customFontGioco.deriveFont(22f));
            g2d.setColor(COLORE_TESTO);
            FontMetrics fm = g2d.getFontMetrics();
            int textX = (width - fm.stringWidth(playerName)) / 2;
            int textY = 30 + fm.getAscent() / 2;
            g2d.drawString(playerName, textX, textY);
            
            // 4. Disegna la pedina sotto
            int pedinaX = (width - PEDINA_RADIUS * 2) / 2;
            int pedinaY = height - PEDINA_RADIUS * 2 - 15; // Posizione centrata in basso

            // Pedina con effetto 3D (gradiente radiale)
            RadialGradientPaint rgp = new RadialGradientPaint(
                pedinaX + PEDINA_RADIUS - PEDINA_RADIUS / 3, pedinaY + PEDINA_RADIUS - PEDINA_RADIUS / 3, 
                PEDINA_RADIUS * 1.5f, 
                new float[]{0f, 1f},
                new Color[]{Color.WHITE, pedinaColor} 
            );
            g2d.setPaint(rgp);
            g2d.fillOval(pedinaX, pedinaY, PEDINA_RADIUS * 2, PEDINA_RADIUS * 2);

            // Contorno per la pedina
            g2d.setColor(Color.BLACK.darker());
            g2d.setStroke(new BasicStroke(1.5f));
            g2d.drawOval(pedinaX, pedinaY, PEDINA_RADIUS * 2, PEDINA_RADIUS * 2);
            
            g2d.dispose();
        }
    }

    // CLASSE INTERNA 

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
            boardStartY = (panelHeight - boardHeight) / 2 - (int)(cellHeight * 0.5); // Posiziona più in alto per il tavolo
        }

        public int getBoardStartX() { return boardStartX; }
        public int getCalculatedCellWidth() { return cellWidth; }
        
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g; 
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            
            calculateBoardDimensions(); 

            // --- PARAMETRI PER LA NUOVA FORMA RETTANGOLARE ---
            int frameThickness = (int)(cellWidth * 0.4); // Spessore della cornice principale
            
            // Coordinate della griglia di buchi
            int gridX = boardStartX;
            int gridY = boardStartY;
            int gridW = boardWidth;
            int gridH = boardHeight;

            // Coordinate del tabellone totale inclusa la cornice
            int totalBoardX = gridX - frameThickness;
            int totalBoardY = gridY - frameThickness; // La cornice avvolge la griglia
            int totalBoardW = gridW + frameThickness * 2;
            int totalBoardH = gridH + frameThickness * 2;

            int cornerArcFrame = RAGGIO_BORDO + frameThickness; // Arrotondamento per la cornice esterna

            // --- DISEGNA IL TAVOLO DI LEGNO BIANCO ---
            int tableTopY = gridY + gridH - (int)(cellHeight * 0.5); // Posizionato sotto la griglia di buchi
            if (tableTopY < 0) tableTopY = 0; // Evita valori negativi iniziali
            
            g2d.setColor(COLORE_SUPPORTO_TAVOLO.darker()); // Bordo più scuro per il tavolo
            g2d.fillRoundRect(0, tableTopY, getWidth(), getHeight() - tableTopY, 20, 20); 
            g2d.setColor(COLORE_SUPPORTO_TAVOLO); // Colore principale del tavolo
            g2d.fillRoundRect(5, tableTopY + 5, getWidth() - 10, getHeight() - tableTopY - 5, 15, 15);
            g2d.setColor(COLORE_SUPPORTO_TAVOLO.darker().darker()); // Linea superiore del tavolo
            g2d.setStroke(new BasicStroke(2));
            g2d.drawLine(5, tableTopY + 5, getWidth() - 5, tableTopY + 5);
            g2d.setStroke(new BasicStroke(1)); 


            // --- DISEGNA IL TABELLONE (RETTANGOLO SEMPLICE, COLORE LEGNO) ---
            
            // 1. La cornice esterna (rettangolo arrotondato)
            g2d.setColor(COLORE_LEGNO_MEDIO);
            g2d.fill(new RoundRectangle2D.Double(totalBoardX, totalBoardY, totalBoardW, totalBoardH, cornerArcFrame, cornerArcFrame));
            
            // 2. Bordo esterno della cornice
            g2d.setStroke(new BasicStroke(frameThickness / 2));
            g2d.setColor(COLORE_LEGNO_BORDO_SCURO);
            g2d.draw(new RoundRectangle2D.Double(totalBoardX, totalBoardY, totalBoardW, totalBoardH, cornerArcFrame, cornerArcFrame));
            g2d.setStroke(new BasicStroke(1));


            // 3. La "griglia" interna dei buchi (rettangolo arrotondato, colore legno chiaro)
            g2d.setColor(COLORE_LEGNO_CHIARO);
            g2d.fillRoundRect(gridX, gridY, gridW, gridH, RAGGIO_BORDO, RAGGIO_BORDO);
            g2d.setColor(COLORE_LEGNO_BORDO_SCURO);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRoundRect(gridX, gridY, gridW, gridH, RAGGIO_BORDO, RAGGIO_BORDO);
            g2d.setStroke(new BasicStroke(1));
            

            // --- DISEGNA GLI SLOT E LE PEDINE ---
            char[][] tab = logica.getTabellone();
            for (int i = 0; i < RIGHE; i++) {
                for (int j = 0; j < COLONNE; j++) {
                    // Calcola le coordinate centrali per ogni slot
                    int centerX = gridX + j * cellWidth + cellWidth / 2;
                    int centerY = gridY + i * cellHeight + cellHeight / 2;
                    int x = centerX - radius;
                    int y = centerY - radius;
                    
                    // Disegna lo slot vuoto (il buco nel tabellone)
                    g2d.setColor(COLORE_SLOT_VUOTO_INTERNO);
                    g2d.fillOval(x, y, radius * 2, radius * 2);

                    // Aggiungi un'ombra leggera per profondità allo slot
                    g2d.setColor(COLORE_LEGNO_OMBRE); // Ombra più scura per effetto foro
                    g2d.fillOval(x + radius / 4, y + radius / 4, radius * 2 - radius / 2, radius * 2 - radius / 2);
                    g2d.setColor(Color.BLACK.darker());
                    g2d.drawOval(x, y, radius * 2, radius * 2); 

                    // Se c'è una pedina, disegnala
                    if (tab[i][j] != ' ') {
                        Color pedinaColor;
                        if (tab[i][j] == GIOCATORE_1_CHAR) {
                            pedinaColor = COLORE_ROSSO_PEDINA;
                        } else {
                            pedinaColor = COLORE_GIALLO_PEDINA;
                        }
                        
                        // Pedina con effetto 3D (gradiente radiale per la lucentezza)
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
        }
    }

    
}