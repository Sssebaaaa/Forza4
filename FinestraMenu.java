import javax.swing.*;
import java.awt.*;

class FinestraMenu extends JFrame{
    public FinestraMenu(){
        setTitle("Menu Principale - Forza 4");
        setSize(350, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); //centro il titolo
        setLayout(new GridBagLayout()); //centro i bottoni
    }
}