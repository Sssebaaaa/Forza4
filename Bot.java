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

    

}