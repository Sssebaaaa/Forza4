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