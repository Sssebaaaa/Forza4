public class ControlloVittoria {

    public boolean controllaVittoria(char[][] tab, char g) {
        int righe = tab.length;
        int colonne = tab[0].length;

        for (int i = 0; i < righe; i++) {
            for (int j = 0; j < colonne - 3; j++) {
                if (tab[i][j] == g && tab[i][j + 1] == g && tab[i][j + 2] == g && tab[i][j + 3] == g)
                    return true;
            }
        }

        for (int i = 0; i < righe - 3; i++) {
            for (int j = 0; j < colonne; j++) {
                if (tab[i][j] == g && tab[i + 1][j] == g && tab[i + 2][j] == g && tab[i + 3][j] == g)
                    return true;
            }
        }

        for (int i = 0; i < righe - 3; i++) {
            for (int j = 0; j < colonne - 3; j++) {
                if (tab[i][j] == g && tab[i + 1][j + 1] == g && tab[i + 2][j + 2] == g && tab[i + 3][j + 3] == g)
                    return true;
            }
        }

        for (int i = 3; i < righe; i++) {
            for (int j = 0; j < colonne - 3; j++) {
                if (tab[i][j] == g && tab[i - 1][j + 1] == g && tab[i - 2][j + 2] == g && tab[i - 3][j + 3] == g)
                    return true;
            }
        }

        return false;
    }
}
