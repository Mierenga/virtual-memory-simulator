import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.HashMap;
import java.util.Map.Entry;
import java.lang.Integer;

public class SwingInterface {

    public static AtomicInteger programController = new AtomicInteger(0);
    private static JLabel[] memoryLabels = new JLabel[16];
    private static JLabel[] pageTableLabels = new JLabel[16];
    private static JButton[] pcbButtons = new JButton[Const.MAX_PROCESSES];
    private static JButton stepForward = new JButton("Step");
    private static JLabel frameRefsLabel = new JLabel();
    private static JLabel pageFaultsLabel = new JLabel();
    private static JLabel pageTableTitle = new JLabel("Click Process for Page Table");
    private static int currentPageTable = 0;
    
    public static void main (String[] args) {
        
        VirtualM programRunnable = null;
        
        try {
        
            programRunnable = new VirtualM(args[0]);
            
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Must supply input file as argument.");
            System.exit(1);
        } catch (Exception e) {
            System.err.println(e);
            System.exit(1);
        }
        
        Thread programThread = new Thread(programRunnable);
        programThread.start();
        
        
        int width = 1000;
        int height = 800;
        int selectedProcess = 0;
        
        // create the JFrame
        
        JFrame frame = new JFrame("Virtual Memory System with Page Replacement");
        frame.pack();
        frame.setVisible(true);
        frame.setSize(width,height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // add a panel
        
        JPanel panel = new JPanel();
        frame.add(panel);

        // setup the layout of the panel

        int bodyRows = 0;
        int bodyCols = 3;
        int hgap = 25; // horizontal gap
        int vgap = 10; // vertical gap
        panel.setLayout(new GridLayout(bodyRows, bodyCols, hgap, vgap));
        
        // Titles
        
        JLabel memoryTitle = new JLabel("Physical M (in LRU order)");
        memoryTitle.setHorizontalAlignment(JLabel.CENTER);
        panel.add(memoryTitle);
        
        JLabel pcbTitle = new JLabel("Processes");
        pcbTitle.setHorizontalAlignment(JLabel.CENTER);
        panel.add(pcbTitle);
        
        
        pageTableTitle.setHorizontalAlignment(JLabel.CENTER);
        panel.add(pageTableTitle);
        
        // Build display
        
        buildDisplay(panel);
        
        refreshMemoryContent(programRunnable.memory);

        stepForward.addActionListener(new StepForward(programRunnable.memory));
        for (int i = 0; i < 5; i++) {
            pcbButtons[i].addActionListener(new displayPageTable(i+1));
        }
        
    }
    
    static class StepForward implements ActionListener {
    
        private PhysicalMemory memory;
        
        public StepForward(PhysicalMemory mem) {
            memory = mem;
        }
        
        public void actionPerformed (ActionEvent e) {
            programController.set(1);
            while (programController.get() == 1) {}
            
            refreshMemoryContent(memory);
            
        }
    }
    
    static class displayPageTable implements ActionListener {
        
        private int pid;
        
        public displayPageTable(int id) {
            pid = id;
        }
    
        public void actionPerformed (ActionEvent e) {
            refreshPageTableContent(pid);
        }
    }
    
    static private void refreshPageTableContent(int pid)
    {
        if (pid == 0) {
            return;
        }
        int i = 0;
        
        pageTableTitle.setText("Page Table for P" + pid);
        currentPageTable = pid;
        HashMap<Integer, Integer> pageMap = null;
        try {
            pageMap = VirtualM.pcbArray[pid].getPageTable();
        } catch (NullPointerException e) {
            pageTableLabels[i++].setText("EMPTY");
            while (i < 16) {
                pageTableLabels[i++].setText("");
            }
            return;
        }
        
        if (!pageMap.isEmpty()) {
            
            for (Entry<Integer, Integer> e : pageMap.entrySet()) {
                if (i > 15) {
                    break;
                }
                pageTableLabels[i++].setText("page: " + e.getKey()+ " | frame: "+e.getValue());
            }
        }
        while (i < 16) {
            pageTableLabels[i++].setText("");
        }

          
    }
    
    
    static private void refreshMemoryContent(PhysicalMemory mem)
    {
        Collection<?> frameCollection = (Collection<?>) mem.viewMemoryContents();
        int f = 0;
        for (Object o : frameCollection) {
            MemoryFrame mf = (MemoryFrame) o;
            String memStr = "";
            if (mf.getOwnerID() < 0) {
                memStr = String.format("[%02d] FREE FRAME", mf.getFrameNumber());
            } else {
                memStr = String.format("[%02d] P%1d : %06d | accsd: %14d",
                mf.getFrameNumber(), mf.getOwnerID(), mf.getPageReference(), mf.getLastAccessed());
            }
            memoryLabels[f].setText(memStr);
            f++;
        }
        frameRefsLabel.setText("Total memory references: " + VirtualM.referenceCount);
        pageFaultsLabel.setText("Total page faults: " + VirtualM.pageFaults);
        refreshPageTableContent(currentPageTable);
    }
    
    static private void buildDisplay(JPanel panel)
    {
        for (int i = 0; i < 16; i++) {
            // add first column, ith row
            memoryLabels[i] = new JLabel();
            panel.add(memoryLabels[i]);
            
            // add second column, ith row
            if (i < 5) {
                pcbButtons[i] = new JButton("Process " + (i+1));
                panel.add(pcbButtons[i]);
            } else if (i == 10) {
                panel.add(frameRefsLabel);
            } else if (i == 11) {
                panel.add(pageFaultsLabel);
            } else if (i == 14) {
                panel.add(stepForward);
            } else {
                panel.add(new JLabel());
            }
            
            // add third column, ith row
            pageTableLabels[i] = new JLabel();
            panel.add(pageTableLabels[i]);
            
        }
    }
    
}









