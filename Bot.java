public class Bot {

    private static final int PROF_DIFICILE = 7;

    public int giocaTurnoBot(LogicaGioco logica, char bot, char avversario, String difficolta) {
        char[][] tab = copiaTabella(logica.getTabellone());

        switch (difficolta.toLowerCase()) {
            case "facile":
                return mossaRandom(tab);

            case "media":
                return mossaMedia(tab, bot, avversario);

            case "difficile":
                return mossaDifficile(tab, bot, avversario);

            default:
                return mossaRandom(tab);
        }
    }

    private int mossaRandom(char[][] tab) {
        int col;
        do {
            col = (int) (Math.random() * 7);
        } while (tab[0][col] != ' ');
        return col;
    }

    private int mossaMedia(char[][] tab, char bot, char avv) {

        for (int c = 0; c < 7; c++) {
            if (tab[0][c] == ' ') {
                char[][] sim = simula(tab, c, bot);
                if (controlloVittoria(sim, bot)) return c;
            }
        }

        for (int c = 0; c < 7; c++) {
            if (tab[0][c] == ' ') {
                char[][] sim = simula(tab, c, avv);
                if (controlloVittoria(sim, avv)) return c;
            }
        }

        int[] priorita = {3, 2, 4, 1, 5, 0, 6};
        for (int c : priorita) {
            if (tab[0][c] == ' ') return c;
        }

        return mossaRandom(tab);
    }

    private int mossaDifficile(char[][] tab, char bot, char avv) {
        int bestCol = -1;
        int bestScore = Integer.MIN_VALUE;

        int[] ordine = {3, 2, 4, 1, 5, 0, 6};

        for (int c : ordine) {
            if (tab[0][c] == ' ') {
                char[][] sim = simula(tab, c, bot);
                int score = minimax(
                        sim, PROF_DIFICILE - 1, true, bot, avv,
                        Integer.MIN_VALUE, Integer.MAX_VALUE
                );

                if (score > bestScore) {
                    bestScore = score;
                    bestCol = c;
                }
            }
        }

        if (bestCol == -1) return mossaRandom(tab);
        return bestCol;
    }

    private int minimax(char[][] tab, int prof, boolean turnoAvv,
                        char bot, char avv, int alpha, int beta) {

        if (controlloVittoria(tab, bot)) return 1000000 + prof;
        if (controlloVittoria(tab, avv)) return -1000000 - prof;
        if (pieno(tab)) return 0;
        if (prof == 0) return valuta(tab, bot, avv);

        int[] ordine = {3, 2, 4, 1, 5, 0, 6};

        if (!turnoAvv) {

            int maxEval = Integer.MIN_VALUE;

            for (int c : ordine) {
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

            for (int c : ordine) {
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
        for (int i = 0; i < 7; i++) {
            if (tab[0][i] == ' ') return false;
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

    private int valuta(char[][] tab, char bot, char avv) {
        int score = 0;

        for (int i = 0; i < tab.length; i++) {
            if (tab[i][3] == bot) score += 8;
        }

        score += finestre(tab, bot);
        score -= finestre(tab, avv);

        return score;
    }

    private int finestre(char[][] tab, char g) {
        int score = 0;
        int rows = tab.length;
        int cols = tab[0].length;

        final int V4 = 2000;
        final int V3 = 60;
        final int V2 = 10;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {

                if (j + 3 < cols) {
                    score += valutaFinestra(
                            new char[]{tab[i][j], tab[i][j + 1], tab[i][j + 2], tab[i][j + 3]},
                            g, V2, V3, V4
                    );
                }

                if (i + 3 < rows) {
                    score += valutaFinestra(
                            new char[]{tab[i][j], tab[i + 1][j], tab[i + 2][j], tab[i + 3][j]},
                            g, V2, V3, V4
                    );
                }

                if (i + 3 < rows && j + 3 < cols) {
                    score += valutaFinestra(
                            new char[]{tab[i][j], tab[i + 1][j + 1], tab[i + 2][j + 2], tab[i + 3][j + 3]},
                            g, V2, V3, V4
                    );
                }

                if (i + 3 < rows && j - 3 >= 0) {
                    score += valutaFinestra(
                            new char[]{tab[i][j], tab[i + 1][j - 1], tab[i + 2][j - 2], tab[i + 3][j - 3]},
                            g, V2, V3, V4
                    );
                }
            }
        }

        return score;
    }

    private int valutaFinestra(char[] w, char g, int V2, int V3, int V4) {
        int count = 0;
        int empty = 0;

        for (char c : w) {
            if (c == g) count++;
            else if (c == ' ') empty++;
        }

        if (count == 4) return V4;
        if (count == 3 && empty == 1) return V3;
        if (count == 2 && empty == 2) return V2;

        return 0;
    }

    private boolean controlloVittoria(char[][] tab, char giocatore) {

        for (int i = 0; i < tab.length; i++) {
            int count = 0;
            for (int j = 0; j < tab[i].length; j++) {
                if (tab[i][j] == giocatore) count++;
                else count = 0;
                if (count == 4) return true;
            }
        }

        for (int j = 0; j < tab[0].length; j++) {
            int count = 0;
            for (int i = 0; i < tab.length; i++) {
                if (tab[i][j] == giocatore) count++;
                else count = 0;
                if (count == 4) return true;
            }
        }

        for (int i = 0; i < tab.length - 3; i++) {
            for (int j = 0; j < tab[0].length - 3; j++) {
                if (tab[i][j] == giocatore &&
                        tab[i + 1][j + 1] == giocatore &&
                        tab[i + 2][j + 2] == giocatore &&
                        tab[i + 3][j + 3] == giocatore)
                    return true;
            }
        }

        for (int i = 0; i < tab.length - 3; i++) {
            for (int j = 3; j < tab[0].length; j++) {
                if (tab[i][j] == giocatore &&
                        tab[i + 1][j - 1] == giocatore &&
                        tab[i + 2][j - 2] == giocatore &&
                        tab[i + 3][j - 3] == giocatore)
                    return true;
            }
        }

        return false;
    }
}
