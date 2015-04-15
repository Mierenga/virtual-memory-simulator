import javax.swing.*;
import java.awt.*;

public class swingInterface {

    public static void main (String[] args) {
        
        JFrame frame = new JFrame("Virtual Memory System");
        frame.setVisible(true);
        frame.setSize(600,600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // make the panel
        JPanel mainPanel = new JPanel();
        int mainRows = Const.MEM_FRAMES;
        int mainColumns = 2;
        int hgap = 5; // horizontal gap
        int vgap = 10; // vertical gap
        mainPanel.setLayout(new GridLayout(mainRows, mainColumns, hgap, vgap));
        //panel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        frame.add(mainPanel);
        
        
        
        JLabel[] memoryLabels = new JLabel[Const.MEM_FRAMES];
        JButton[] pcbButtons = new JButton[Const.MAX_PROCESSES];
        for (int i = 0; i < mainRows; i++) {
            memoryLabels[i] = new JLabel("M " + i);
            mainPanel.add(memoryLabels[i]);
            pcbButtons[i] = new JButton("Process " + (i+1));
            if (i < 5) {
                mainPanel.add(pcbButtons[i]);
            } else {
                mainPanel.add(new JLabel("empty"));
            }
        }
        
    }
    
}
