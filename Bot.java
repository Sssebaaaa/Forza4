public class Bot {

    private ControlloVittoria controllo = new ControlloVittoria();

    public int giocaTurnoBot(LogicaGioco logica, char bot, char avversario, String difficolta) {
        char[][] tab = copiaTabella(logica.getTabellone());

        if (difficolta.equalsIgnoreCase("facile"))
            return mossaRandom(tab);

        if (difficolta.equalsIgnoreCase("media"))
            return mossaMedia(tab, bot, avversario);

        return mossaDifficile(tab, bot, avversario);
    }

    private int mossaRandom(char[][] tab) {
        int col;
        do col = (int) (Math.random() * 7);
        while (tab[0][col] != ' ');
        return col;
    }

    private int mossaMedia(char[][] tab, char bot, char avv) {
        for (int c = 0; c < 7; c++) {
            if (tab[0][c] == ' ') {
                char[][] sim = simula(tab, c, bot);
                if (controllo.controllaVittoria(sim, bot)) return c;
            }
        }
        for (int c = 0; c < 7; c++) {
            if (tab[0][c] == ' ') {
                char[][] sim = simula(tab, c, avv);
                if (controllo.controllaVittoria(sim, avv)) return c;
            }
        }
        int[] priorita = {3, 2, 4, 1, 5, 0, 6};
        for (int c : priorita) if (tab[0][c] == ' ') return c;
        return mossaRandom(tab);
    }

    private int mossaDifficile(char[][] tab, char bot, char avv) {
        int bestCol = -1;
        int bestScore = Integer.MIN_VALUE;
        for (int c = 0; c < 7; c++) {
            if (tab[0][c] == ' ') {
                char[][] sim = simula(tab, c, bot);
                int score = minimax(sim, 5, false, bot, avv, Integer.MIN_VALUE, Integer.MAX_VALUE);
                if (score > bestScore) {
                    bestScore = score;
                    bestCol = c;
                }
            }
        }
        return bestCol;
    }

    private int minimax(char[][] tab, int prof, boolean turnoAvv, char bot, char avv, int alpha, int beta) {
        if (controllo.controllaVittoria(tab, bot)) return 1000;
        if (controllo.controllaVittoria(tab, avv)) return -1000;
        if (prof == 0 || pieno(tab)) return 0;

        if (!turnoAvv) {
            int maxEval = Integer.MIN_VALUE;
            for (int c = 0; c < 7; c++) {
                if (tab[0][c] == ' ') {
                    char[][] sim = simula(tab, c, bot);
                    int eval = minimax(sim, prof - 1, true, bot, avv, alpha, beta);
                    maxEval = Math.max(maxEval, eval);
                    alpha = Math.max(alpha, eval);
                    if (beta <= alpha) break;
                }
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (int c = 0; c < 7; c++) {
                if (tab[0][c] == ' ') {
                    char[][] sim = simula(tab, c, avv);
                    int eval = minimax(sim, prof - 1, false, bot, avv, alpha, beta);
                    minEval = Math.min(minEval, eval);
                    beta = Math.min(beta, eval);
                    if (beta <= alpha) break;
                }
            }
            return minEval;
        }
    }

    private boolean pieno(char[][] tab) {
        for (int i = 0; i < 7; i++){
            if (tab[0][i] == ' '){
                return false;
            }
        }
        return true;
    }

    private char[][] simula(char[][] tab, int col, char g) {
        char[][] copia = copiaTabella(tab);
        for (int i = copia.length - 1; i >= 0; i--) {
            if (copia[i][col] == ' ') {
                copia[i][col] = g;
                break;
            }
        }
        return copia;
    }

    private char[][] copiaTabella(char[][] orig) {
        char[][] c = new char[orig.length][orig[0].length];
        for (int i = 0; i < orig.length; i++)
            for (int j = 0; j < orig[i].length; j++)
                c[i][j] = orig[i][j];
        return c;
    }
}