import javax.swing.*; //Importa tutte le classi base di Swing (GUI)
import java.awt.*; //Importa tutte le classi essenziali per la grafica 2D ovvero contorni delle forme, effetti sui bottoni e font
import java.awt.event.ComponentAdapter; //Usato per cambiamenti di ogni componente spostato, ridimensionato o reso visibile/nascosto
import java.awt.event.ComponentEvent; //Rappresenta che un componente è stato modificato
import java.awt.event.MouseAdapter; //Classe astratta utile per gestire i click del mouse
import java.awt.event.MouseEvent; //Rappresenta le interazione del mouse reali
import java.awt.event.MouseMotionAdapter; // Per gestire il movimento del mouse (hover)
import java.awt.geom.RoundRectangle2D; //Classe usata per disegnare rettangoli con gli angoli arrotondati
import java.awt.RadialGradientPaint; //Usata per creare gradienti
import java.io.File; //Usata per caricare il font
import java.io.IOException; // Eccezione usata per gestire errori come il caricamento del font
import java.util.List; // Per la lista delle pedine vincenti
import java.util.ArrayList; // Per la lista delle pedine vincenti

public class FinestraGioco extends JFrame {
    //Spazio sopra la scritta del risultato
    private final int SPAZIO_SOPRA_RISULTATO = 20;
    //Altezza fissa per la scritta che contiene il risultato
    private final int ALTEZZA_RISERVATA_RISULTATO = 100;

    //Font e costanti
    private static final String FONT_FILE_NAME = "spicy_sale/Spicy Sale.ttf";
    private final int RIGHE = 6;
    private final int COLONNE = 7;
    private final char GIOCATORE_1_CHAR = 'R';
    private final char GIOCATORE_2_CHAR = 'G';

    //Colori
    private final Color COLORE_SFONDO_GRIGIO = new Color(220, 220, 220); //Sfondo gioco
    private final Color COLORE_LEGNO_TAVOLO_CHIARO = new Color(210, 180, 140); //Superficie tavolo
    private final Color COLORE_LEGNO_TAVOLO_SCURO = new Color(170, 130, 90); //Bordo tavolo
    private final Color COLORE_PLASTICA_BLU_BORDO = new Color(20, 70, 150); //Bordo tabellone
    private final Color COLORE_PLASTICA_BLU_INTERNO = new Color(50, 120, 200); //Interno tabellone
    private final Color COLORE_PLASTICA_OMBRE = new Color(0, 0, 0, 80); //Ombra slot pedine
    //Sfumatura slot pedine
    private final Color COLORE_SLOT_VUOTO_GRADIENTE_CHIARO = new Color(0, 50, 100, 100);
    private final Color COLORE_SLOT_VUOTO_GRADIENTE_SCURO = new Color(0, 20, 50, 180); 
    //Colori pulsanti
    private final Color COLORE_ARANCIO = new Color(200, 112, 26);
    private final Color COLORE_ARANCIO_HOVER = new Color(180, 92, 16); //Hover ovvero quando il mouse passa sopra
    private final Color COLORE_RICOMINCIA = new Color(30, 138, 76);
    private final Color COLORE_RICOMINCIA_HOVER = new Color(20, 118, 66);
    private final Color COLORE_TESTO = Color.DARK_GRAY;
    private final Color COLORE_ROSSO_PEDINA = new Color(180, 0, 0);
    private final Color COLORE_GIALLO_PEDINA = new Color(200, 160, 0);
    private final int RAGGIO_BORDO = 20;
    
    // Larghezza fissa per il pannello di stato del giocatore corrente
    private final int LARGHEZZA_STATO_PANEL = 340; //Larghezza fissa finestra giocatore
    private final Color COLORE_SFONDO_STATO = new Color(245, 245, 245); //Sfondo finestra giocatore
    private Font customFontGioco; //Memorizza font

    private LogicaGioco logica;
    private Bot bot;
    private GestioneReset gestioneReset;

    //Partita
    private boolean isVsBot;
    private String difficoltaBot = "";
    private char giocatoreCorrente;
    private boolean giocoAttivo = false;
    
    // Variabili per animazione, hover e vittoria
    private int colonnaHover = -1;
    private int animatingRow = -1;
    private int animatingCol = -1;
    private char animatingPlayer;
    private int animY;
    private boolean isAnimating = false;
    private List<int[]> pedineVincenti = new ArrayList<>();

    /**
     * Classe che rappresenta un singolo coriandolo con posizione, velocità e colore
     */
    private class Coriandolo {
        double x, y;           // Posizione corrente
        double velocitaX;      // Velocità orizzontale
        double velocitaY;      // Velocità verticale
        double rotazione;      // Angolo di rotazione corrente
        double velocitaRotazione; // Velocità di rotazione
        Color colore;          // Colore del coriandolo
        int larghezza = 8;     // Larghezza del coriandolo
        int altezza = 15;      // Altezza del coriandolo
        
        public Coriandolo(int startX, int startY) {
            this.x = startX;
            this.y = startY;
            // Velocità orizzontale casuale (sinistra o destra)
            this.velocitaX = (Math.random() - 0.5) * 8;
            // Velocità verticale iniziale verso l'alto
            this.velocitaY = -Math.random() * 15 - 5;
            this.rotazione = Math.random() * 360;
            // Velocità di rotazione casuale
            this.velocitaRotazione = (Math.random() - 0.5) * 20;
            // Colori vivaci casuali per i coriandoli
            Color[] coloriPossibili = {
                new Color(255, 0, 0),      // Rosso
                new Color(255, 165, 0),    // Arancione
                new Color(255, 255, 0),    // Giallo
                new Color(0, 255, 0),      // Verde
                new Color(0, 0, 255),      // Blu
                new Color(138, 43, 226),   // Viola
                new Color(255, 20, 147)    // Rosa
            };
            this.colore = coloriPossibili[(int)(Math.random() * coloriPossibili.length)];
        }
        
        // Aggiorna posizione e rotazione (simula gravità)
        public void aggiorna() {
            x += velocitaX;
            y += velocitaY;
            velocitaY += 0.5; // Gravità
            rotazione += velocitaRotazione;
            
            // Leggera resistenza dell'aria sulla velocità orizzontale
            velocitaX *= 0.99;
        }
        
        // Disegna il coriandolo
        public void disegna(Graphics2D g2d) {
            // Salva lo stato corrente del Graphics2D
            java.awt.geom.AffineTransform vecchioTransform = g2d.getTransform();
            
            // Trasla e ruota
            g2d.translate(x, y);
            g2d.rotate(Math.toRadians(rotazione));
            
            // Disegna un rettangolo colorato
            g2d.setColor(colore);
            g2d.fillRect(-larghezza/2, -altezza/2, larghezza, altezza);
            
            // Ripristina il transform originale
            g2d.setTransform(vecchioTransform);
        }
        
        // Controlla se il coriandolo è uscito dallo schermo
        public boolean fuoriSchermo(int altezzaFinestra) {
            return y > altezzaFinestra + 50; // Margine extra
        }
    }

    

    /**
     * Pannello overlay trasparente per disegnare i coriandoli sopra tutto
     */
    private class CoriandoloPannello extends JPanel {
        private List<Coriandolo> coriandoli = new ArrayList<>();
        private Timer timer;
        
        public CoriandoloPannello() {
            setOpaque(false); // Trasparente per vedere sotto
            setLayout(null);  // Nessun layout
        }
        
        // Avvia l'animazione dei coriandoli
        public void avviaAnimazione() {
            coriandoli.clear();
            
            // Crea coriandoli in posizioni casuali nella parte alta dello schermo
            int numeroCoriandoli = 420; // Numero totale di coriandoli
            for (int i = 0; i < numeroCoriandoli; i++) {
                int startX = (int)(Math.random() * getWidth());
                int startY = -50; // Partono da sopra lo schermo
                coriandoli.add(new Coriandolo(startX, startY));
            }
            
            // Timer per aggiornare e ridisegnare i coriandoli
            if (timer != null && timer.isRunning()) {
                timer.stop();
            }
            
            timer = new Timer(20, e -> { // 50 FPS
                // Aggiorna tutti i coriandoli
                coriandoli.forEach(Coriandolo::aggiorna);
                
                // Rimuovi coriandoli fuori schermo
                coriandoli.removeIf(c -> c.fuoriSchermo(getHeight()));
                
                // Ferma il timer quando tutti i coriandoli sono caduti
                if (coriandoli.isEmpty()) {
                    ((Timer)e.getSource()).stop();
                }
                
                repaint();
            });
            timer.start();
        }
        
        // Ferma l'animazione
        public void fermaAnimazione() {
            if (timer != null && timer.isRunning()) {
                timer.stop();
            }
            coriandoli.clear();
            repaint();
        }
        
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Disegna tutti i coriandoli
            for (Coriandolo c : coriandoli) {
                c.disegna(g2d);
            }
            
            g2d.dispose();
        }
    }
    
    //Componenti 
    private GameBoardPanel gamePanel; //Disegnare griglia e pedine
    private PlayerStatusPanel playerStatusPanel; //Mostrare turno corrente
    private JLabel modeStatusLabel; //Mostrare modalità di gioco
    private JLabel gameResultLabel; //Mostrare il risultato
    private FinestraMenu parentMenu; //Ritorno menu principale
    private CoriandoloPannello coriandoloPannello; // Pannello per i coriandoli
    
    public FinestraGioco(FinestraMenu menu, boolean isVsBot, String difficolta) {
        this.parentMenu = menu;
        this.isVsBot = isVsBot;
        this.difficoltaBot = difficolta;
        
        //Eventuali problemi con altri file di gioco
        try {
            this.logica = new LogicaGioco();
            this.bot = new Bot();
            this.gestioneReset = new GestioneReset();
        } catch (Exception e) {
             System.err.println("Assicurati che le classi LogicaGioco, Bot e GestioneReset siano definite.");
        }
        
        this.giocatoreCorrente = GIOCATORE_1_CHAR;
        
        initializeGUI();
        iniziaNuovaPartita();
    }
    
    private void initializeGUI() {
        setTitle("FORZA 4");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(false); //Lascio finestra computer
        setExtendedState(JFrame.MAXIMIZED_BOTH); //Finestra a schermo intero

        //Caricamento font
        customFontGioco = loadCustomFont(FONT_FILE_NAME, Font.PLAIN, 24f);

        //Disegno tavolo
        CustomBackgroundPanel backgroundPanel = new CustomBackgroundPanel();
        setContentPane(backgroundPanel);

        //scritte superiori
        JPanel topContainerPanel = new JPanel(new BorderLayout());
        topContainerPanel.setOpaque(false); //Segue lo sfondo
        topContainerPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 20, 50));
        
        //Turno
        playerStatusPanel = new PlayerStatusPanel();
        
        //Dimensioni fisse per turno
        Dimension statusPanelDim = new Dimension(LARGHEZZA_STATO_PANEL, 100);
        playerStatusPanel.setPreferredSize(statusPanelDim);
        playerStatusPanel.setMinimumSize(statusPanelDim);
        playerStatusPanel.setMaximumSize(statusPanelDim);
        
        //Turno in alto a destra
        topContainerPanel.add(playerStatusPanel, BorderLayout.EAST);

        //Spazio vuoto in alto a destra per centrare
        final int SPACING_WIDTH = LARGHEZZA_STATO_PANEL; 
        JPanel emptyWestPanel = new JPanel();
        emptyWestPanel.setOpaque(false);
        emptyWestPanel.setPreferredSize(new Dimension(SPACING_WIDTH, 1));
        topContainerPanel.add(emptyWestPanel, BorderLayout.WEST);

        //Centrare scritte superiori
        JLabel titolo = new JLabel("FORZA 4");
        titolo.setFont(customFontGioco.deriveFont(42f));
        titolo.setForeground(COLORE_TESTO);
        titolo.setHorizontalAlignment(SwingConstants.CENTER);
        
        modeStatusLabel = new JLabel();
        modeStatusLabel.setFont(customFontGioco.deriveFont(20f));
        modeStatusLabel.setForeground(COLORE_TESTO);
        modeStatusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        //Incolonnamento scritte superiori
        JPanel titleWrapper = new JPanel();
        titleWrapper.setOpaque(false);
        titleWrapper.setLayout(new BoxLayout(titleWrapper, BoxLayout.Y_AXIS));
        Dimension titleDim = new Dimension(400, 120);
        titleWrapper.setPreferredSize(titleDim);
        titleWrapper.setMinimumSize(titleDim);
        titleWrapper.setMaximumSize(titleDim);
        
        titolo.setAlignmentX(Component.CENTER_ALIGNMENT);
        modeStatusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        titleWrapper.add(titolo);
        titleWrapper.add(Box.createVerticalStrut(0));
        titleWrapper.add(modeStatusLabel);
        
        topContainerPanel.add(titleWrapper, BorderLayout.CENTER);

        //Risultato a fine partita
        gameResultLabel = new JLabel();
        gameResultLabel.setFont(customFontGioco.deriveFont(45f));
        gameResultLabel.setForeground(COLORE_TESTO);
        gameResultLabel.setHorizontalAlignment(SwingConstants.CENTER);
        //Spazio vuoto per altezza fissa
        gameResultLabel.setText(" "); 
        
        JPanel resultPanel = new JPanel();
        resultPanel.setOpaque(false);
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        
        //Dimensione fissa per risultato
        Dimension resultDim = new Dimension(600, ALTEZZA_RISERVATA_RISULTATO);
        resultPanel.setPreferredSize(resultDim);
        resultPanel.setMinimumSize(resultDim);
        resultPanel.setMaximumSize(resultDim);
        
        gameResultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        resultPanel.add(Box.createVerticalStrut(SPAZIO_SOPRA_RISULTATO));
        resultPanel.add(gameResultLabel);
        resultPanel.add(Box.createVerticalGlue()); //Spazio extra se serve
        
        topContainerPanel.add(resultPanel, BorderLayout.SOUTH);
        
        backgroundPanel.add(topContainerPanel, BorderLayout.NORTH);
        
        //Griglia gioco
        gamePanel = new GameBoardPanel();
        backgroundPanel.add(gamePanel, BorderLayout.CENTER);
        
        //Click su slot
        gamePanel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (!giocoAttivo) return;

                //Blocca click al turno del bot
                if (isVsBot && giocatoreCorrente == GIOCATORE_2_CHAR) {
                    return;
                }
                gestisciMossaUtente(e.getX());
            }
            
            public void mouseExited(MouseEvent e) {
                colonnaHover = -1;
                gamePanel.repaint();
            }
        });

        // Listener per hover colonna
        gamePanel.addMouseMotionListener(new MouseMotionAdapter() {
            
            public void mouseMoved(MouseEvent e) {
                if (!giocoAttivo || isAnimating) {
                    colonnaHover = -1;
                    gamePanel.repaint();
                    return;
                }
                
                int cellWidth = gamePanel.getCalculatedCellWidth();
                int x = e.getX() - gamePanel.getBoardStartX();
                int col = x / cellWidth;
                
                if (col >= 0 && col < COLONNE) {
                    colonnaHover = col;
                } else {
                    colonnaHover = -1;
                }
                gamePanel.repaint();
            }
        });
        
        //Dimensioni tabellone corrette
        addComponentListener(new ComponentAdapter() {
            public void componentShown(ComponentEvent e) {
                gamePanel.calculateBoardDimensions();
                gamePanel.repaint();
                updatePlayerStateBox();
                removeComponentListener(this);
            }
        });

        //Pulsanti inferiori
        RoundButton backButton = new RoundButton("TORNA AL MENU'", COLORE_ARANCIO, COLORE_ARANCIO_HOVER, customFontGioco.deriveFont(22f), new Dimension(220, 65));
        backButton.addActionListener(e -> tornaAlMenu());
        
        RoundButton restartButton = new RoundButton("RICOMINCIA", COLORE_RICOMINCIA, COLORE_RICOMINCIA_HOVER, customFontGioco.deriveFont(22f), new Dimension(220, 65));
        restartButton.addActionListener(e -> iniziaNuovaPartita());

        //Pannello pulsanti
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 40));
        southPanel.setOpaque(false);
        southPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 40, 40));
        southPanel.add(restartButton);
        southPanel.add(backButton);
        backgroundPanel.add(southPanel, BorderLayout.SOUTH);

        updateModeStatusLabel();
        
        // Pannello overlay per i coriandoli (deve essere sopra tutto)
        coriandoloPannello = new CoriandoloPannello();
        setGlassPane(coriandoloPannello);
        coriandoloPannello.setVisible(true);
        
        setVisible(true);
    }

    //Resetta tutto
    private void iniziaNuovaPartita() {
        // Ferma eventuali coriandoli in corso
        if (coriandoloPannello != null) {
            coriandoloPannello.fermaAnimazione();
        }
        gestioneReset.resetPartita(logica);
        giocatoreCorrente = GIOCATORE_1_CHAR;
        giocoAttivo = true;
        isAnimating = false;
        colonnaHover = -1;
        pedineVincenti.clear(); // Pulisce le pedine vincenti
        
        gameResultLabel.setText(" ");
        
        updatePlayerStateBox();
        updateModeStatusLabel();
        gamePanel.repaint();
    }
    
    //Scritta modalita
    private void updateModeStatusLabel() {
        String mode;
        if (isVsBot) {
            mode = "1 VS BOT - DIFFICOLTA' " + difficoltaBot.toUpperCase();
        } else {
            mode = "1 VS 1";
        }
        if (modeStatusLabel != null) {
            modeStatusLabel.setText(mode);
            modeStatusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        }
    }

    //Scritta giocatore
    private void updatePlayerStateBox() {
        String name;
        Color color;
        
        if (giocatoreCorrente == GIOCATORE_1_CHAR) {
            name = "TURNO DEL GIOCATORE ROSSO";
            color = COLORE_ROSSO_PEDINA;
        } else if (isVsBot) {
            name = "TURNO DEL BOT";
            color = COLORE_GIALLO_PEDINA;
        } else {
            name = "TURNO DEL GIOCATORE GIALLO";
            color = COLORE_GIALLO_PEDINA;
        }
        
        playerStatusPanel.updateStatus(name, color);
    }

    //Mossa utente
    private void gestisciMossaUtente(int clickX) {
        if (!giocoAttivo) return;

        int cellWidth = gamePanel.getCalculatedCellWidth();
        int colonna = (clickX - gamePanel.getBoardStartX()) / cellWidth;

        if (colonna >= 0 && colonna < COLONNE) {
            eseguiMossa(colonna, giocatoreCorrente);
        }
    }

    //Mossa bot
    private void gestisciMossaBot() {
        if (!giocoAttivo || giocatoreCorrente != GIOCATORE_2_CHAR || !isVsBot) return;

        //Ritardo dopo utente
        Timer botTimer = new Timer(500, e -> {
            int colonnaBot = bot.giocaTurnoBot(logica, GIOCATORE_2_CHAR, GIOCATORE_1_CHAR, difficoltaBot);
            eseguiMossa(colonnaBot, giocatoreCorrente);
            ((Timer)e.getSource()).stop();
        });
        botTimer.setRepeats(false);
        botTimer.start();
    }
    
    //Calcolo mossa con animazione
    private void eseguiMossa(int colonna, char giocatore) {
        if (isAnimating) return; // Blocca mosse durante animazione
            
        int riga;
        try {
             // Uso il metodo getRigaDisponibile che mi hai fornito
             riga = logica.getRigaDisponibile(colonna); 
        } catch (Exception ex) {
            System.err.println("Metodo 'getRigaDisponibile' non trovato in LogicaGioco. Eseguo mossa senza animazione.");
            eseguiMossaSenzaAnimazione(colonna, giocatore);
            return;
        }

        if (riga == -1) return; // Colonna piena

        animatingRow = riga;
        animatingCol = colonna;
        animatingPlayer = giocatore;
        isAnimating = true;
        colonnaHover = -1; // Nascondi hover durante animazione

        // Calcolo coordinate animazione
        int startY = gamePanel.getBoardStartY() - gamePanel.getCalculatedCellHeight(); // Inizia da sopra
        int endY = gamePanel.getBoardStartY() + animatingRow * gamePanel.getCalculatedCellHeight();
        animY = startY;

        Timer timer = new Timer(8, null); // Timer per animazione (8ms)
        timer.addActionListener(e -> {
            // Calcolo step per un'animazione più fluida (rallenta alla fine)
            int distance = endY - animY;
            int step = Math.max(2, distance / 10 + 10); 
            animY += step;

            if (animY >= endY) {
                animY = endY;
                ((Timer) e.getSource()).stop();
                isAnimating = false;

                // Mossa logica (come da FinestraGioco.java)
                boolean mossaValida = logica.inserisciPedina(animatingCol, animatingPlayer);
                gamePanel.repaint(); // Disegno finale con pedina inserita
                
                if (mossaValida) { // Logica di fine partita (da FinestraGioco.java)
                    // Uso il metodo boolean che mi hai fornito
                    boolean vittoria = logica.controlloVittoria(animatingPlayer); 
                    boolean pareggio = !vittoria && logica.controllaPareggio();

                    if (vittoria || pareggio) {
                        giocoAttivo = false;
                        String risultatoMsg;
                        
                        if (vittoria) {
                            // NUOVO: Trova le pedine vincenti da evidenziare
                            trovaEAggiungiPedineVincenti(animatingPlayer);
                            
                            if (animatingPlayer == GIOCATORE_1_CHAR) {
                                risultatoMsg = "IL GIOCATORE ROSSO HA VINTO!";
                            } else if (isVsBot) {
                                risultatoMsg = "IL BOT HA VINTO!";
                            } else {
                                risultatoMsg = "IL GIOCATORE GIALLO HA VINTO!";
                            }
                        } else {
                            risultatoMsg = "PAREGGIO!";
                        }

                        gameResultLabel.setText(risultatoMsg);
                        gameResultLabel.setForeground(COLORE_TESTO);
                        gameResultLabel.getParent().revalidate();
                        gameResultLabel.getParent().repaint();
                        
                        // Avvia animazione coriandoli
                        coriandoloPannello.avviaAnimazione();
                        
                        updatePlayerStateBox();
                        return;
                    }

                    giocatoreCorrente = logica.cambiaGiocatore(animatingPlayer);
                    updatePlayerStateBox();

                    if (isVsBot && giocatoreCorrente == GIOCATORE_2_CHAR) {
                        gestisciMossaBot();
                    }
                }
                
            } else {
                 gamePanel.repaint(); // Disegno frame animazione
            }
        });
        timer.setRepeats(true);
        timer.start();
    }
    
    // Metodo di fallback se getRigaDisponibile non esiste
    private void eseguiMossaSenzaAnimazione(int colonna, char giocatore) {
        boolean mossaValida = logica.inserisciPedina(colonna, giocatore);
        
        if (mossaValida) {
            gamePanel.repaint(); //Disegno con pedina inserita
            
            boolean vittoria = logica.controlloVittoria(giocatore);
            boolean pareggio = !vittoria && logica.controllaPareggio();

            if (vittoria || pareggio) {
                giocoAttivo = false;
                String risultatoMsg;
                
                //Scritta risultato
                if (vittoria) {
                    // NUOVO: Trova le pedine vincenti da evidenziare
                    trovaEAggiungiPedineVincenti(giocatore);
                            
                    if (giocatore == GIOCATORE_1_CHAR) {
                        risultatoMsg = "IL GIOCATORE ROSSO HA VINTO!";
                    } else if (isVsBot) {
                        risultatoMsg = "IL BOT HA VINTO!";
                    } else {
                        risultatoMsg = "IL GIOCATORE GIALLO HA VINTO!";
                    }
                } else {
                    risultatoMsg = "PAREGGIO!";
                }

                gameResultLabel.setText(risultatoMsg);
                gameResultLabel.setForeground(COLORE_TESTO);
                gameResultLabel.getParent().revalidate();
                gameResultLabel.getParent().repaint();
                
                // Avvia animazione coriandoli
                coriandoloPannello.avviaAnimazione();
                
                updatePlayerStateBox();
                return;
            }

            giocatoreCorrente = logica.cambiaGiocatore(giocatore);
            updatePlayerStateBox();

            if (isVsBot && giocatoreCorrente == GIOCATORE_2_CHAR) {
                gestisciMossaBot();
            }
        }
    }
    
    /**
     * NUOVO METODO
     * Cerca le 4 pedine vincenti sul tabellone logico e le salva nella lista
     * 'pedineVincenti' per permettere a GameBoardPanel di disegnarle.
     * Questo duplica la logica di 'controlloVittoria' ma è necessario
     * per ottenere le coordinate.
     */
    private void trovaEAggiungiPedineVincenti(char giocatoreCorrente) {
        char[][] tabellone = logica.getTabellone();
        if (tabellone == null) return;
        
        pedineVincenti.clear();

        //controllo orizzontale
        for(int i=0; i<RIGHE; i++){
            for(int j=0; j<COLONNE - 3; j++){
                if(tabellone[i][j] == giocatoreCorrente && 
                   tabellone[i][j+1] == giocatoreCorrente &&
                   tabellone[i][j+2] == giocatoreCorrente &&
                   tabellone[i][j+3] == giocatoreCorrente) {
                    
                    pedineVincenti.add(new int[]{i, j});
                    pedineVincenti.add(new int[]{i, j+1});
                    pedineVincenti.add(new int[]{i, j+2});
                    pedineVincenti.add(new int[]{i, j+3});
                    gamePanel.repaint();
                    return; // Trovate
                } 
            }
        }

        //controllo verticale
        for(int j=0; j<COLONNE; j++){
            for(int i=0; i<RIGHE - 3; i++){
                if(tabellone[i][j] == giocatoreCorrente &&
                   tabellone[i+1][j] == giocatoreCorrente &&
                   tabellone[i+2][j] == giocatoreCorrente &&
                   tabellone[i+3][j] == giocatoreCorrente) {
                    
                    pedineVincenti.add(new int[]{i, j});
                    pedineVincenti.add(new int[]{i+1, j});
                    pedineVincenti.add(new int[]{i+2, j});
                    pedineVincenti.add(new int[]{i+3, j});
                    gamePanel.repaint();
                    return; // Trovate
                }
            }
        }

        //controllo diagonale (da sin-alto a des-basso)
        for(int i=0; i<RIGHE-3; i++){
            for(int j=0; j<COLONNE -3; j++){
                if (tabellone[i][j] == giocatoreCorrente && 
                    tabellone[i+1][j+1] == giocatoreCorrente && 
                    tabellone[i+2][j+2] == giocatoreCorrente && 
                    tabellone[i+3][j+3] == giocatoreCorrente) {
                    
                    pedineVincenti.add(new int[]{i, j});
                    pedineVincenti.add(new int[]{i+1, j+1});
                    pedineVincenti.add(new int[]{i+2, j+2});
                    pedineVincenti.add(new int[]{i+3, j+3});
                    gamePanel.repaint();
                    return; // Trovate
                }
            }
        }

        //controllo diagonale 2 (da des-alto a sin-basso)
        for (int i = 0; i < RIGHE - 3; i++) {
            for (int j = 3; j < COLONNE; j++) {
                if (tabellone[i][j] == giocatoreCorrente && 
                    tabellone[i+1][j-1] == giocatoreCorrente && 
                    tabellone[i+2][j-2] == giocatoreCorrente && 
                    tabellone[i+3][j-3] == giocatoreCorrente) {
                    
                    pedineVincenti.add(new int[]{i, j});
                    pedineVincenti.add(new int[]{i+1, j-1});
                    pedineVincenti.add(new int[]{i+2, j-2});
                    pedineVincenti.add(new int[]{i+3, j-3});
                    gamePanel.repaint();
                    return; // Trovate
                }
            }
        }
    }
    
    private void tornaAlMenu() {
        dispose(); //Chiude finestra attuale
        
        if (parentMenu != null) {
            SwingUtilities.invokeLater(() -> {
                parentMenu.tornaAllaSelezionePrincipale();
                parentMenu.setVisible(true); //Menu visibile
            });
        } else {
            //RItorno
        }
    }

    //Font
    private Font loadCustomFont(String name, int style, float size) {
        try {
            File fontFile = new File(name);
            Font baseFont;
            if (fontFile.exists()) {
                //Carica da esterno
                baseFont = Font.createFont(Font.TRUETYPE_FONT, fontFile);
            } else {
                //Carica da file corretto per java
                java.net.URL resource = getClass().getResource("/" + name);
                if (resource != null) {
                    baseFont = Font.createFont(Font.TRUETYPE_FONT, resource.openStream());
                } else {
                    throw new IOException("Font file not found in file system or classpath.");
                }
            }
            return baseFont.deriveFont(style, size);
        } catch (FontFormatException | IOException | NullPointerException e) {
            System.err.println("Errore caricamento font: " + e.getMessage());
            //Caso di errore
            return new Font("Serif", Font.BOLD, (int) size); 
        }
    }
    
    //Disegno tavolo
    private class CustomBackgroundPanel extends JPanel {
        private int tableLineY = 0; //Linea tavolo

        public CustomBackgroundPanel() {
            setBackground(COLORE_SFONDO_GRIGIO);
            setLayout(new BorderLayout());
            
            addComponentListener(new ComponentAdapter() {
                public void componentResized(ComponentEvent e) {
                    if (gamePanel != null) {
                        gamePanel.calculateBoardDimensions();
                        //Inizio linea tavolo
                        tableLineY = gamePanel.boardStartY + gamePanel.boardHeight + (int)(gamePanel.cellHeight * 2.5);
                        if (tableLineY < 0) tableLineY = 0;
                    }
                    repaint();
                }
            });
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g); //Disegna sfondo
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (tableLineY > 0) {
                int tableBottomY = getHeight();

                //Corpo tavolo
                g2d.setColor(COLORE_LEGNO_TAVOLO_CHIARO.darker()); 
                g2d.fillRect(0, tableLineY, getWidth(), tableBottomY - tableLineY);

                //Superficie tavolo
                g2d.setColor(COLORE_LEGNO_TAVOLO_CHIARO);
                g2d.fillRect(0, tableLineY, getWidth(), tableBottomY - tableLineY);

                //Bordo superiore
                g2d.setColor(COLORE_LEGNO_TAVOLO_SCURO);
                g2d.setStroke(new BasicStroke(4)); 
                g2d.drawLine(0, tableLineY, getWidth(), tableLineY);
                g2d.setStroke(new BasicStroke(1));
            }
            
            g2d.dispose();
        }
    }

    private class PlayerStatusPanel extends JPanel {
        private String playerName = "";
        private Color pedinaColor = COLORE_ROSSO_PEDINA;
        private final int PEDINA_RADIUS = 20;

        public PlayerStatusPanel() {
            setOpaque(false);
        }
        
        //Aggiorno giocatore
        public void updateStatus(String name, Color color) {
            this.playerName = name;
            this.pedinaColor = color;
            repaint();
        }
        
        //Finestra giocatore
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int width = getWidth();
            int height = getHeight();
            int arc = 20;
            
            //Sfondo 
            g2d.setColor(COLORE_SFONDO_STATO);
            g2d.fill(new RoundRectangle2D.Double(0, 0, width - 1, height - 1, arc, arc));
            //Bordo
            g2d.setColor(COLORE_TESTO.darker());
            g2d.setStroke(new BasicStroke(2));
            g2d.draw(new RoundRectangle2D.Double(0, 0, width - 1, height - 1, arc, arc));
            g2d.setStroke(new BasicStroke(1));
            //Testp
            g2d.setFont(customFontGioco.deriveFont(22f));
            g2d.setColor(COLORE_TESTO);
            FontMetrics fm = g2d.getFontMetrics();
            int textX = (width - fm.stringWidth(playerName)) / 2;
            int textY = 20 + fm.getAscent(); //Dal bordo superiore
            g2d.drawString(playerName, textX, textY);
            //Pedina
            int pedinaX = (width - PEDINA_RADIUS * 2) / 2;
            int pedinaY = height - PEDINA_RADIUS * 2 - 20; //Dal bordo inferiore
            
            //Sfumatura pedina
            RadialGradientPaint rgp = new RadialGradientPaint(
                pedinaX + PEDINA_RADIUS - PEDINA_RADIUS / 3, pedinaY + PEDINA_RADIUS - PEDINA_RADIUS / 3,
                PEDINA_RADIUS * 1.5f,
                new float[]{0f, 1f},
                new Color[]{Color.WHITE, pedinaColor}
            );
            g2d.setPaint(rgp);
            g2d.fillOval(pedinaX, pedinaY, PEDINA_RADIUS * 2, PEDINA_RADIUS * 2);
            //Bordo della pedina
            g2d.setColor(Color.BLACK.darker());
            g2d.setStroke(new BasicStroke(1.5f));
            g2d.drawOval(pedinaX, pedinaY, PEDINA_RADIUS * 2, PEDINA_RADIUS * 2);
            g2d.setStroke(new BasicStroke(1));
            g2d.dispose();
        }
    }

    private class GameBoardPanel extends JPanel {
        //Dimensioni calcolate
        private int boardWidth;
        private int boardHeight;
        private int cellWidth;
        private int cellHeight;
        private int radius; //Raggio delle pedine
        private int boardStartX; //Posizione X di inizio del tabellone
        private int boardStartY; //Posizione Y di inizio del tabellone
        
        public GameBoardPanel() {
            setOpaque(false);
            setPreferredSize(new Dimension(1000, 800));
            
            //Ricalcola le dimensioni in caso di ridimensionamento della finestra
            addComponentListener(new ComponentAdapter() {
                public void componentResized(ComponentEvent e) {
                    calculateBoardDimensions();
                    repaint();
                }
            });
            calculateBoardDimensions();
        }

        //Disegno elementi tabellone
        public void calculateBoardDimensions() {
            int panelWidth = getWidth();
            int panelHeight = getHeight();
            
            //Gestione dei valori zero o negativi
            if (panelWidth <= 0 || panelHeight <= 0) {
                panelWidth = FinestraGioco.this.getWidth();
                panelHeight = FinestraGioco.this.getHeight();
                if (panelWidth <= 0 || panelHeight <= 0) {
                    panelWidth = getPreferredSize().width;
                    panelHeight = getPreferredSize().height;
                }
            }

            double gridAspectRatio = (double) COLONNE / RIGHE;
            
            //Ridimensiona se l'altezza supera lo spazio disponibile
            int targetGridHeight = (int) (panelHeight * 0.78);
            boardWidth = (int) (targetGridHeight * gridAspectRatio);
            boardHeight = targetGridHeight;

            //Ridimensiona se la larghezza supera lo spazio disponibile
            if (boardWidth > panelWidth * 0.95) {
                boardWidth = (int) (panelWidth * 0.95);
                boardHeight = (int) (boardWidth / gridAspectRatio);
            }

            cellWidth = boardWidth / COLONNE;
            cellHeight = boardHeight / RIGHE;
            radius = (int) (Math.min(cellWidth, cellHeight) * 0.4);

            boardStartX = (panelWidth - boardWidth) / 2; //Centro orizzontalmente
            
            //Centraggio
            final int VERTICAL_OFFSET = 30;
            boardStartY = (panelHeight - boardHeight) / 2 + VERTICAL_OFFSET;
        }

        //Dimensioni calcolate
        public int getBoardStartX() { return boardStartX; }
        public int getBoardStartY() { return boardStartY; }
        public int getCalculatedCellWidth() { return cellWidth; }
        public int getCalculatedCellHeight() { return cellHeight; }
        
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            
            int frameThickness = (int)(cellWidth * 0.4); //Spessore della cornice
            
            int gridX = boardStartX;
            int gridY = boardStartY;
            int gridW = boardWidth;
            int gridH = boardHeight;

            //Dimensioni tabellone
            int totalBoardX = gridX - frameThickness;
            int totalBoardY = gridY - frameThickness;
            int totalBoardW = gridW + frameThickness * 2;
            int totalBoardH = gridH + frameThickness * 2;

            int cornerArcFrame = RAGGIO_BORDO + frameThickness; //Arco per angoli cornice
            
            //Cornice esterna tabellone
            g2d.setColor(COLORE_PLASTICA_BLU_BORDO);
            g2d.fill(new RoundRectangle2D.Double(totalBoardX, totalBoardY, totalBoardW, totalBoardH, cornerArcFrame, cornerArcFrame));
            
            //Bordo esterno
            g2d.setStroke(new BasicStroke(frameThickness / 2));
            g2d.setColor(COLORE_PLASTICA_BLU_BORDO.darker());
            g2d.draw(new RoundRectangle2D.Double(totalBoardX, totalBoardY, totalBoardW, totalBoardH, cornerArcFrame, cornerArcFrame));
            g2d.setStroke(new BasicStroke(1));

            //Griglia
            g2d.setColor(COLORE_PLASTICA_BLU_INTERNO);
            g2d.fillRoundRect(gridX, gridY, gridW, gridH, RAGGIO_BORDO, RAGGIO_BORDO);
            //Bordo interno
            g2d.setColor(COLORE_PLASTICA_BLU_BORDO.darker());
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
                    
                    //Disegna lo slot vuoto con sfumatura
                    RadialGradientPaint slotGradient = new RadialGradientPaint(
                        centerX, centerY, radius, 
                        new float[]{0f, 0.7f, 1f},
                        new Color[]{COLORE_SLOT_VUOTO_GRADIENTE_CHIARO, COLORE_SLOT_VUOTO_GRADIENTE_SCURO, COLORE_PLASTICA_BLU_BORDO.darker()} 
                    );
                    g2d.setPaint(slotGradient);
                    g2d.fillOval(x, y, radius * 2, radius * 2);

                    //Ombra e bordo per gli slot
                    g2d.setColor(COLORE_PLASTICA_OMBRE); 
                    //Ombra leggermente spostata
                    g2d.fillOval(x + radius / 4, y + radius / 4, radius * 2 - radius / 2, radius * 2 - radius / 2);
                    g2d.setColor(COLORE_PLASTICA_BLU_BORDO.darker()); 
                    g2d.setStroke(new BasicStroke(1.5f));
                    g2d.drawOval(x, y, radius * 2, radius * 2);
                    g2d.setStroke(new BasicStroke(1)); 


                    //Disegno pedina (solo se non è quella in animazione)
                    if (tab[i][j] != ' ' && !(isAnimating && i == animatingRow && j == animatingCol)) {
                        Color pedinaColor = (tab[i][j] == GIOCATORE_1_CHAR) ? COLORE_ROSSO_PEDINA : COLORE_GIALLO_PEDINA;
                        //Pedina con effetto 3D
                        RadialGradientPaint rgp = new RadialGradientPaint(
                            centerX - radius / 3, centerY - radius / 3, //Punto di luce spostato
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
            
            // Evidenzia colonna hover
            if (giocoAttivo && !isAnimating && colonnaHover >= 0 && colonnaHover < COLONNE) {
                int x = gridX + colonnaHover * cellWidth;
                int y = gridY;
                int w = cellWidth;
                int h = gridH;
                g2d.setColor(new Color(255, 255, 255, 60)); // Bianco semi-trasparente
                g2d.fillRect(x, y, w, h);
            }
            
            // Pedina animata
            if (isAnimating) {
                int centerX = gridX + animatingCol * cellWidth + cellWidth / 2;
                int x = centerX - radius;
                int y = animY; // Posizione Y corrente dall'animazione
                
                int animCenterY = y + radius; // Centro Y della pedina in caduta

                Color pedinaColor = (animatingPlayer == GIOCATORE_1_CHAR) ? COLORE_ROSSO_PEDINA : COLORE_GIALLO_PEDINA;

                RadialGradientPaint rgp = new RadialGradientPaint(
                    centerX - radius / 3, animCenterY - radius / 3, //Punto di luce
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

            // NUOVO: Evidenzia le 4 pedine vincenti
            if (pedineVincenti != null && !pedineVincenti.isEmpty()) {
                // Imposta un "alone" giallo
                g2d.setStroke(new BasicStroke(5)); // Spessore 5
                g2d.setColor(new Color(0, 255, 0, 200)); // Giallo semi-trasparente

                for (int[] pos : pedineVincenti) {
                    int riga = pos[0];
                    int col = pos[1];

                    // Calcola le coordinate X,Y del cerchio (come nel disegno pedine)
                    int centerX = gridX + col * cellWidth + cellWidth / 2;
                    int centerY = gridY + riga * cellHeight + cellHeight / 2;
                    int x = centerX - radius;
                    int y = centerY - radius;
                    
                    int diametro = radius * 2;
                    int offset = 5; // Spaziatura dell'alone

                    // Disegna un cerchio (ovale) più grande della pedina
                    g2d.drawOval(x - offset, y - offset, diametro + (offset*2), diametro + (offset*2));
                }
            }
            
            g2d.dispose();
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
           //Non disegna in automatico
            setContentAreaFilled(false); 
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            //Gestione dell'effetto hover
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { currentColor = hoverColor; repaint(); }
                public void mouseExited(MouseEvent e) { currentColor = baseColor; repaint(); }
            });
        }
        
        //Disegno totale
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            //Disegno pulsanti
            g2.setColor(currentColor);
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), RAGGIO_BORDO, RAGGIO_BORDO));
            
            //Disegno testo al centro
            Color originalColor = g2.getColor();
            g2.setColor(getForeground());
            FontMetrics fm = g2.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(getText())) / 2;
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent(); //Allineamento verticale
            g2.drawString(getText(), x, y);
            
            g2.setColor(originalColor);
            g2.dispose();
        }
    }
}