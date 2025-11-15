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
}