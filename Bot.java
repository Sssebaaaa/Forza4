public class Bot {

    public int giocaTurnoBot(LogicaGioco logica, char bot, char avversario) {
        char[][] tab = copiaTabella(logica.getTabellone());
        int colonna;

        for (colonna = 0; colonna < 7; colonna++) {
            if (colonnaValida(tab, colonna)) {
                char[][] test = simulaMossa(tab, colonna, bot);
                if (controllaVittoria(test, bot)) return colonna;
            }
        }

        for (colonna = 0; colonna < 7; colonna++) {
            if (colonnaValida(tab, colonna)) {
                char[][] test = simulaMossa(tab, colonna, avversario);
                if (controllaVittoria(test, avversario)) return colonna;
            }
        }

        int[] priorita = {3, 2, 4, 1, 5, 0, 6};
        for (int i = 0; i < priorita.length; i++) {
            if (colonnaValida(tab, priorita[i])) return priorita[i];
        }

        for (colonna = 0; colonna < 7; colonna++) {
            if (colonnaValida(tab, colonna)) return colonna;
        }

        return -1;
    }

    

    private boolean colonnaValida(char[][] tab, int col) {
        return tab[0][col] == ' ';
    }

    private char[][] simulaMossa(char[][] tab, int col, char giocatore) {
        char[][] copia = copiaTabella(tab);
        for (int i = copia.length - 1; i >= 0; i--) {
            if (copia[i][col] == ' ') {
                copia[i][col] = giocatore;
                break;
            }
        }
        return copia;
    }

    private boolean controllaVittoria(char[][] tab, char g) {
        int righe = tab.length;
        int colonne = tab[0].length;
        int i, j, k;

        for (i = 0; i < righe; i++) {
            for (j = 0; j < colonne - 3; j++) {
                if (tab[i][j] == g && tab[i][j + 1] == g && tab[i][j + 2] == g && tab[i][j + 3] == g)
                    return true;
            }
        }

}