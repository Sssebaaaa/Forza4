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