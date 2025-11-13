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

    //Funzione usata per il corretto funzionamento del font
    private Font loadCustomFont(String name, int style, float size){
        try{
            File fontFile=new File(name);
            //Tentitivo di caricamento del font come risorsa interna al JAR (formato di compressione di java) se il file esterno non esiste
            if(!fontFile.exists()){
                return Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/"+name))
                        .deriveFont(style, size);
            }
            //Tentitivo di caricamento del font da un file esterno
            Font baseFont=Font.createFont(Font.TRUETYPE_FONT, fontFile);
            return baseFont.deriveFont(size);
        }catch(FontFormatException | IOException e){
            System.err.println("Errore nel caricamento del font personalizzato: "+e.getMessage());
            //Soluzione alternativa: usa un font di sistema se il caricamento fallisce
            return new Font("Serif", Font.BOLD, (int) size);
        }catch(NullPointerException e){
            //Eccezione specifica se la risorsa interna non viene trovata
            System.err.println("Errore: Il font "+name+" non è stato trovato come risorsa nel classpath.");
            //Soluzione alternativa
            return new Font("Serif", Font.BOLD, (int) size);
        }
    }

    //CLASSE PRINCIPALE
    public FinestraMenu(){
        //Caricamento finale del font
        customFontTitolo=loadCustomFont(FONT_FILE_NAME, Font.PLAIN, 40f);
        customFontBottone=loadCustomFont(FONT_FILE_NAME, Font.PLAIN, 22f);
        customFontDialog=loadCustomFont(FONT_FILE_NAME, Font.PLAIN, 18f);
        //Impostazioni della finestra totale del gioco
        setTitle("FORZA 4");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); //Centramento della finestra sullo schermo
        //Pannello personalizzato come sfondo della finestra
        BackgroundPanel backgroundPanel=new BackgroundPanel();
        backgroundPanel.setLayout(new GridBagLayout()); //Funzione per centrare i componenti verticalmente
        setContentPane(backgroundPanel);
        GridBagConstraints gbc=new GridBagConstraints(); //Vincoli per funzione GridBagLayout
        gbc.insets=new Insets(15, 10, 15, 10); //Margini tra i componenti

        //Titolo
        JLabel titoloLabel=new JLabel("FORZA 4");
        titoloLabel.setFont(customFontTitolo);
        titoloLabel.setForeground(COLORE_TESTO);
        titoloLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx=0; //Colonna 0
        gbc.gridy=0; //Riga 0
        gbc.insets=new Insets(30, 10, 30, 10); //Margine maggiore per il titolo
        backgroundPanel.add(titoloLabel, gbc);

        //Ripristina la spaziatura standard per i bottoni
        gbc.insets=new Insets(15, 10, 15, 10);

        //BOTTONI
        //Bottone "Gioca vs Umano"
        JButton giocaUmanoButton=new RoundButton("Gioca vs Umano", COLORE_VERDE, COLORE_VERDE_HOVER);
        giocaUmanoButton.addActionListener(e ->{
            //Avviamento della finestra di gioco
            SwingUtilities.invokeLater(() -> new FinestraGioco().setVisible(true));
            this.dispose(); //Chiusura della finestra del menu
        });

        //Bottone "Gioca vs Bot"
        JButton giocaBotButton=new RoundButton("Gioca vs Bot", COLORE_ARANCIO, COLORE_ARANCIO_HOVER);
        //Apertura della scelta della difficoltà
        giocaBotButton.addActionListener(e -> new BotDifficultyDialog(this).setVisible(true));

        //Bottone "Esci"
        JButton esciButton=new RoundButton("Esci", COLORE_ARANCIO, COLORE_ARANCIO_HOVER);
        esciButton.addActionListener(e -> System.exit(0)); //Termina l'applicazione

        gbc.gridy=1; //Bottone 1 (riga 1)
        backgroundPanel.add(giocaUmanoButton, gbc);
        gbc.gridy=2; //Bottone 2 (riga 2)
        backgroundPanel.add(giocaBotButton, gbc);
        gbc.gridy=3; //Bottone 3 (riga 3)
        backgroundPanel.add(esciButton, gbc);
    }

    //Scelta della difficolta del bot
    private class BotDifficultyDialog extends JDialog{
        public BotDifficultyDialog(JFrame owner){
            super(owner, "Scegli Difficoltà", true); //Blocca della finestra principale
            setUndecorated(true); //Rimozione della barra del titolo e i bordi di sistema
            setBackground(new Color(0, 0, 0, 0)); //Modifica della finestra in modo trasparente
            JPanel contentPanel=new JPanel(new GridBagLayout()){
                protected void paintComponent(Graphics g){
                    Graphics2D g2=(Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    //Sfondo arrotondato della scelta della difficoltà
                    g2.setColor(COLORE_SFONDO_BASE.darker()); //Colore dello sfondo
                    //Effetto del bordo
                    g2.fill(new RoundRectangle2D.Double(1, 1, getWidth() - 2, getHeight() - 2,
                            RAGGIO_BORDO, RAGGIO_BORDO));
                    g2.dispose();
                    super.paintComponent(g);
                }
                public boolean isOpaque(){
                    return false; //Funzionamento corretto delle finestre trasparente
                }
            };
            contentPanel.setOpaque(false); // Anche il pannello interno deve essere trasparente
            contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Padding interno
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.fill = GridBagConstraints.HORIZONTAL; // I componenti si espandono orizzontalmente
            // Etichetta del titolo del Dialog
            JLabel titleLabel = new JLabel("SCEGLI LA DIFFICOLTA' DEL BOT");
            titleLabel.setFont(customFontBottone.deriveFont(20f));
            titleLabel.setForeground(COLORE_TESTO);
            titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
            gbc.insets = new Insets(30, 10, 30, 10);
            contentPanel.add(titleLabel, gbc);

            String[] difficulties = {"Facile", "Medio", "Difficile"};
            gbc.gridy++;

            // Ciclo per creare i bottoni di difficoltà
            for (String diff : difficulties) {
                // Wrapper per centrare il bottone
                JPanel buttonWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
                buttonWrapper.setOpaque(false);

                // Bottone personalizzato RoundButton
                JButton button = new RoundButton(diff, COLORE_VERDE, COLORE_VERDE_HOVER);
                button.setFont(customFontDialog);
                button.addActionListener(e -> {
                    // Messaggio di debug/stato di avanzamento
                    JOptionPane.showMessageDialog(owner,
                            "Logica del Bot (Difficoltà: " + diff + ") non ancora implementata.",
                            "Lavori in corso", JOptionPane.INFORMATION_MESSAGE);
                    dispose(); // Chiude il Dialog dopo la selezione
                });

                buttonWrapper.add(button);
                gbc.insets = new Insets(25, 10, 25, 10);
                contentPanel.add(buttonWrapper, gbc);
                gbc.gridy++;
            }

            // Bottone Annulla
            JPanel cancelWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
            cancelWrapper.setOpaque(false);
            JButton cancelButton = new RoundButton("Annulla", COLORE_ARANCIO, COLORE_ARANCIO_HOVER);
            cancelButton.setFont(customFontDialog);
            cancelButton.addActionListener(e -> dispose()); // Chiude il Dialog
            cancelWrapper.add(cancelButton);

            gbc.insets = new Insets(30, 10, 40, 10);
            contentPanel.add(cancelWrapper, gbc);

            setContentPane(contentPanel);
            pack(); // <--- CRUCIALE: Calcola la dimensione minima necessaria per contenere tutti i componenti
            setLocationRelativeTo(owner); // Centra il Dialog rispetto alla finestra proprietaria
        }
    }
}