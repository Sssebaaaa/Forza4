import javax.swing.*; //Classi principali di Swing per la GUI
import java.awt.*; //Classi AWT (disegni e pezzi di grafica avanzati)
import java.awt.event.ComponentAdapter; //Gestione del ridimensionamento dello sfondo
import java.awt.event.ComponentEvent; //Gestione delle stelle nello sfondo
import java.awt.event.MouseAdapter; //Gestione della dinamicità del mouse
import java.awt.event.MouseEvent; //Gestione del movimento del mouse
import java.awt.geom.Point2D; //Gestione della pittura con sfumature
import java.awt.geom.RoundRectangle2D; //Disegno dei rettangoli arrotondati
import java.io.File; //Caricamento del font da un file
import java.io.IOException; //Gestione degli errori per il caricamento del font (file esterno)
import java.util.ArrayList; //Memorizzazione delle posizioni delle stelle
import java.util.Random; //Generazione delle posizioni casuali e del movimento delle stelle

class FinestraMenu extends JFrame{

    //Caricamento del file del font dal percorso del file
    private static final String FONT_FILE_NAME="spicy_sale/Spicy Sale.ttf";

    //Colori dell'interfaccia del menu
    private final Color COLORE_SFONDO_BASE=new Color(45, 34, 63); //Colore viola scuro/bluastro (spazio attorno)
    private final Color COLORE_NEBULOSA_MAGENTA=new Color(255, 100, 200, 90); //Magenta semi-trasparente
    private final Color COLORE_NEBULOSA_BLU=new Color(100, 150, 255, 90); //Blu semi-trasparente
    private final Color COLORE_NEBULOSA_SCURO=new Color(30, 20, 50, 200); //Colore intermedio per la sfumatura
    private final Color COLORE_STELLA=new Color(255, 255, 255, 200); //Bianco semi-trasparente per le stelle
    private final Color COLORE_VERDE=new Color(30, 138, 76); //Verde scuro per bottoni primari
    private final Color COLORE_VERDE_HOVER=new Color(20, 118, 66); //Versione più scura per l'interazione con il mouse
    private final Color COLORE_ARANCIO=new Color(200, 112, 26); //Arancione per bottoni secondari o azione
    private final Color COLORE_ARANCIO_HOVER=new Color(180, 92, 16); //Versione più scura per l'interazione con il mouse
    private final Color COLORE_TESTO=Color.WHITE; //Colore del testo
    private final int RAGGIO_BORDO=30; //Raggio di curvatura per gli angoli arrotondati del testo

    //Variabili per il font
    private Font customFontTitolo; //Font per il titolo del gioco
    private Font customFontBottone; //Font per i bottoni principali
    private Font customFontDialog; //Font per i bottoni o testo
}