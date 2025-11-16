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
}