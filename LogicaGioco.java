public class LogicaGioco {

    private char[][] tabellone = new char[6][7];

    public void inizializzaTabellone(){
        for(int i=0; i<tabellone.length; i++){
            for(int j=0; j<tabellone[i].length; j++){
                tabellone[i][j] = ' ';
            }
        }
    }

    public char[][] getTabellone() {
        return tabellone;
    }

    public boolean inserisciPedina(int colonna, char giocatore) {
        for(int i=tabellone.length-1; i>=0; i--){
            if(tabellone[i][colonna] == ' '){
                tabellone[i][colonna] = giocatore;
                return true;
            }
        }
        return false;
    }

    public char cambiaGiocatore(char giocatoreCorrente){
        if(giocatoreCorrente == 'R'){
            return 'G';
        } else {
            return 'R';
        }
    }

    public boolean controllaPareggio(){
        for(int i=0; i<tabellone.length; i++){
            for(int j=0; j<tabellone[i].length; j++){
                if(tabellone[i][j] == ' '){
                    return false;
                }
            }
        }
        return true; 
    }
    
    public boolean controlloVittoria(char giocatoreCorrente){
        //controllo orizzontale
        for(int i=0; i<tabellone.length; i++){
            int quattroDiFila = 0;
            for(int j=0; j<tabellone[i].length; j++){
                if(tabellone[i][j] == giocatoreCorrente){
                    quattroDiFila++;
                    if(quattroDiFila == 4){
                        return true;
                    } 
                } else {
                    quattroDiFila = 0;
                }
            }
        }

        //controllo verticale
        for(int j=0; j<tabellone[0].length; j++){
            int quattroDiFila = 0;
            for(int i=0; i<tabellone.length; i++){
                if(tabellone[i][j] == giocatoreCorrente){
                    quattroDiFila++;
                    if(quattroDiFila == 4){
                        return true;
                    } 
                } else {
                    quattroDiFila = 0;
                }
            }
        }

        //controllo diagonale 
        for(int i=0; i<tabellone.length-3; i++){
            for(int j=0; j<tabellone[0].length -3; j++){
                if (tabellone[i][j] == giocatoreCorrente && tabellone[i+1][j+1] == giocatoreCorrente && tabellone[i+2][j+2] == giocatoreCorrente && tabellone[i+3][j+3] == giocatoreCorrente) {
                    return true;
                }
            }
        }

        //controllo diagonale 2
        for (int i = 0; i < tabellone.length - 3; i++) {
            for (int j = 3; j < tabellone[0].length; j++) {
                if (tabellone[i][j] == giocatoreCorrente && tabellone[i+1][j-1] == giocatoreCorrente && tabellone[i+2][j-2] == giocatoreCorrente && tabellone[i+3][j-3] == giocatoreCorrente) {
                    return true;
                }
            }
        }

        return false;

    }


}