import javax.swing.*; //Importa tutte le classi base di Swing (GUI)

//Usata per creare gradienti
import java.awt.*; //Importa tutte le classi essenziali per la grafica 2D ovvero contorni delle forme, effetti sui bottoni e font
import java.awt.event.ComponentAdapter; //Usato per cambiamenti di ogni componente spostato, ridimensionato o reso visibile/nascosto
import java.awt.event.ComponentEvent; //Rappresenta che un componente è stato modificato
import java.awt.event.MouseAdapter; //Classe astratta utile per gestire i click del mouse
import java.awt.event.MouseEvent; //Rappresenta le interazione del mouse reali
import java.awt.geom.RoundRectangle2D; //Classe usata per disegnare rettangoli con gli angoli arrotondati
import java.io.File; //Usata per caricare il font
import java.io.IOException; // Eccezione usata per gestire errori come il caricamento del font

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
    
    //Componenti 
    private GameBoardPanel gamePanel; //Disegnare griglia e pedine
    private PlayerStatusPanel playerStatusPanel; //Mostrare turno corrente
    private JLabel modeStatusLabel; //Mostrare modalità di gioco
    private JLabel gameResultLabel; //Mostrare il risultato
    private FinestraMenu parentMenu; //Ritorno menu principale
    
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
        setVisible(true);
    }
}