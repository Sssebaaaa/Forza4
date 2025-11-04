import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import java.util.concurrent.ExecutionException;

/**
    ANIMAZIONE PEDINA E FINESTRA DEL FIOCO
 */
public class FinestraGioco extends JFrame {
    // costanti
    public static final int RIGHE = 6;
    public static final int COLONNE = 7;
    public static final int VUOTO = 0;
    public static final int ROSSO = 1;
    public static final int GIALLO = 2;

    // stato
    int[][] tabella = new int[RIGHE][COLONNE];
    boolean turnoGiocatore = true;
    boolean controComputer = true;

    // UI
    PannelloTabellone pannello;
    private JLabel etichettaStato;
    private JPanel barraSuperiore;

    // animazione
    PedinaCadente pedinaCadente = null;
    private final int FRAME_DELAY = 15;

    // AI
    private final int PROFONDITA_AI = 6;
    private final int livelloBot; // 0=facile,1=medio,2=difficile
    private final MotoreAI motore;

    public FinestraGioco(boolean controPC, int livelloBot) {
        super("Forza 4 — Super Ultimate");
        this.controComputer = controPC;
        this.livelloBot = Math.max(0, Math.min(2, livelloBot));
        this.motore = new MotoreAI(RIGHE, COLONNE, PROFONDITA_AI);
        inizializzaUI();
    }

    private void inizializzaUI() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(920, 820);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        pannello = new PannelloTabellone(this);
        add(pannello, BorderLayout.CENTER);

        creaBarraSuperiore();
        setJMenuBar(creaBarraMenu());

        setVisible(true);
        aggiornaEtichettaStato();
    }

    private JMenuBar creaBarraMenu() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(28, 34, 56));
        JMenu menu = new JMenu("Menu");

        JMenuItem nuovo1v1 = new JMenuItem("Nuova Partita 1v1");
        JMenuItem nuovoVsPC = new JMenuItem("Nuova Partita 1 vs PC");
        JMenuItem tornaMenu = new JMenuItem("Torna al Menu");
        JMenuItem esci = new JMenuItem("Esci");

        nuovo1v1.addActionListener(e -> riavviaPartita(false));
        nuovoVsPC.addActionListener(e -> {
            String[] op = {"Facile","Medio","Difficile"};
            int s = JOptionPane.showOptionDialog(this,"Scegli difficoltà:","Difficoltà",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, op, op[2]);
            if (s>=0) {
                riavviaPartita(true);
                // imposta livello
                // per cambiare livello ricrea la finestra
                dispose();
                new FinestraGioco(true, s);
            }
        });
        tornaMenu.addActionListener(e -> {
            dispose();
            MainMenu.mostraSplashMenu();
        });
        esci.addActionListener(e -> System.exit(0));

        menu.add(nuovo1v1);
        menu.add(nuovoVsPC);
        menu.addSeparator();
        menu.add(tornaMenu);
        menu.addSeparator();
        menu.add(esci);

        menuBar.add(menu);
        return menuBar;
    }

    private void creaBarraSuperiore() {
        barraSuperiore = new JPanel(new BorderLayout());
        barraSuperiore.setBackground(new Color(18, 24, 40));
        barraSuperiore.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        etichettaStato = new JLabel("", SwingConstants.LEFT);
        etichettaStato.setForeground(Color.WHITE);
        etichettaStato.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        barraSuperiore.add(etichettaStato, BorderLayout.WEST);

        JPanel destra = new JPanel();
        destra.setOpaque(false);
        JButton btnReset = creaBottonePiccolo("Reset");
        btnReset.addActionListener(e -> riavviaPartita(controComputer));
        JButton btnMenu = creaBottonePiccolo("Menu");
        btnMenu.addActionListener(e -> {
            dispose();
            MainMenu.mostraSplashMenu();
        });
        destra.add(btnReset);
        destra.add(btnMenu);
        barraSuperiore.add(destra, BorderLayout.EAST);

        add(barraSuperiore, BorderLayout.NORTH);
    }

    private JButton creaBottonePiccolo(String testo) {
        JButton b = new JButton(testo);
        b.setFocusPainted(false);
        b.setBackground(new Color(60, 70, 90));
        b.setForeground(Color.WHITE);
        return b;
    }

    void riavviaPartita(boolean controPC) {
        this.controComputer = controPC;
        this.turnoGiocatore = true;
        this.tabella = new int[RIGHE][COLONNE];
        this.pedinaCadente = null;
        pannello.repaint();
        aggiornaEtichettaStato();
    }

    void aggiornaEtichettaStato() {
        if (isTabellaPiena()) {
            etichettaStato.setText("Pareggio! Premi Reset o scegli nuova partita.");
            return;
        }
        if (turnoGiocatore) etichettaStato.setText("Tocca a TE — ROSSO");
        else etichettaStato.setText("Turno PC — GIALLO (pensando...)");
    }

    boolean isTabellaPiena() {
        for (int c = 0; c < COLONNE; c++) if (tabella[0][c] == VUOTO) return false;
        return true;
    }

    // input utente
    void clickUtenteColonna(int col) {
        if (!turnoGiocatore) return;
        if (pedinaCadente != null) return;
        if (tabella[0][col] != VUOTO) return;
        avviaCaduta(col, ROSSO);
    }

    // avvio animazione caduta
    void avviaCaduta(int col, int colore) {
        int rigaTarget = prossimoVuotoInColonna(col);
        if (rigaTarget == -1) return;

        int cellW = pannello.getWidth() / COLONNE;
        int cellH = pannello.getHeight() / RIGHE;
        int padding = Math.min(cellW, cellH) / 8;

        pedinaCadente = new PedinaCadente(col, rigaTarget, colore, cellH);

        Timer timer = new Timer(FRAME_DELAY, e -> {
            boolean finita = pedinaCadente.avanza();
            pannello.repaint();
            if (finita) {
                ((Timer)e.getSource()).stop();
                tabella[pedinaCadente.rigaTarget][pedinaCadente.colonna] = pedinaCadente.colore;
                int colorePiazzato = pedinaCadente.colore;
                pedinaCadente = null;

                if (MotoreAI.controllaVittoriaNelTabellone(tabella, colorePiazzato)) {
                    String nome = (colorePiazzato == ROSSO) ? "ROSSO (TU)" : "GIALLO (PC)";
                    JOptionPane.showMessageDialog(FinestraGioco.this, nome + " ha vinto!");
                    riavviaPartita(controComputer);
                    return;
                }

                if (isTabellaPiena()) {
                    JOptionPane.showMessageDialog(FinestraGioco.this, "Pareggio!");
                    riavviaPartita(controComputer);
                    return;
                }

                turnoGiocatore = (colorePiazzato != ROSSO);
                aggiornaEtichettaStato();

                if (!turnoGiocatore && controComputer) {
                    calcolaMossaAIInBackground();
                }
            }
        });
        timer.start();
    }

    int prossimoVuotoInColonna(int col) {
        for (int r = RIGHE - 1; r >= 0; r--) {
            if (tabella[r][col] == VUOTO) return r;
        }
        return -1;
    }

    // AI background
    private void calcolaMossaAIInBackground() {
        turnoGiocatore = false;
        aggiornaEtichettaStato();

        SwingWorker<Integer, Void> worker = new SwingWorker<>() {
            
            protected Integer doInBackground() {
                if (livelloBot == 0) {
                    return mossaRandom();
                } else if (livelloBot == 1) {
                    int c = mossaMedia();
                    if (c != -1) return c;
                    return mossaRandom();
                } else {
                    return motore.trovaMossaMigliore(tabella, GIALLO);
                }
            }

            
            protected void done() {
                try {
                    Integer col = get();
                    if (col == null || col < 0 || col >= COLONNE || tabella[0][col] != VUOTO) {
                        col = mossaRandom();
                    }
                    final int scelta = col;
                    Timer delay = new Timer(300, ev -> avviaCaduta(scelta, GIALLO));
                    delay.setRepeats(false);
                    delay.start();
                } catch (InterruptedException | ExecutionException ex) {
                    ex.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private int mossaRandom() {
        Random r = new Random();
        int col;
        do {
            col = r.nextInt(COLONNE);
        } while (tabella[0][col] != VUOTO);
        return col;
    }

    private int mossaMedia() {
        // cerca vittoria immediata
        for (int c = 0; c < COLONNE; c++) {
            int r = prossimoVuotoInColonna(c);
            if (r == -1) continue;
            int[][] copia = copiaTabella(tabella);
            copia[r][c] = GIALLO;
            if (MotoreAI.controllaVittoriaNelTabellone(copia, GIALLO)) return c;
        }
        // blocca avversario
        for (int c = 0; c < COLONNE; c++) {
            int r = prossimoVuotoInColonna(c);
            if (r == -1) continue;
            int[][] copia = copiaTabella(tabella);
            copia[r][c] = ROSSO;
            if (MotoreAI.controllaVittoriaNelTabellone(copia, ROSSO)) return c;
        }
        return -1;
    }

    private int[][] copiaTabella(int[][] orig) {
        int[][] nuova = new int[RIGHE][COLONNE];
        for (int r = 0; r < RIGHE; r++) System.arraycopy(orig[r], 0, nuova[r], 0, COLONNE);
        return nuova;
    }

    // Pedina cadente inner class
    // --- classe visibile al Pannello ---
    // Classe pubblica e statica, accessibile da PannelloTabellone
    public static class PedinaCadente {
        public int colonna;
        public int rigaTarget;
        public int colore;
        public int yPixel;

        private int altezzaCella;
        private int velocita = 15;

       public PedinaCadente(int colonna, int rigaTarget, int colore, int altezzaCella) {
            this.colonna = colonna;
            this.rigaTarget = rigaTarget;
           this.colore = colore;
           this.altezzaCella = altezzaCella;
            this.yPixel = 0;
       }

       // Metodo che fa avanzare la pedina verso la posizione target
       public boolean avanza() {
           int targetY = rigaTarget * altezzaCella;
          if (yPixel < targetY) {
              yPixel += velocita;
              if (yPixel > targetY) yPixel = targetY;
              return false;
          }
           return true;
        }
    }


}
