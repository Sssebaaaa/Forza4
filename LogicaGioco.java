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


}