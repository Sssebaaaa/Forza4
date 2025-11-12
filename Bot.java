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