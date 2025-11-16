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
        setTitle("FORZA 4 CONNECT - Partita");
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

        RoundButton backButton = new RoundButton("Torna al Menu", COLORE_ARANCIO, COLORE_ARANCIO_HOVER, customFontGioco.deriveFont(22f), new Dimension(220, 65));
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
            name = "Giocatore 1";
            color = COLORE_ROSSO_PEDINA;
        } else if (isVsBot) {
            name = "Bot (" + difficoltaBot + ")";
            color = COLORE_GIALLO_PEDINA;
        } else {
            name = "Giocatore 2";
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
}