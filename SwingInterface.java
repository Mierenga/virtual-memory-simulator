import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.HashMap;
import java.util.Map.Entry;
import java.lang.Integer;

public class SwingInterface {

    public static AtomicInteger programController = new AtomicInteger(Const.WAIT);
    
    private static JFrame frame = new JFrame("Virtual Memory System with Page Replacement");
    private static JPanel panel = new JPanel();
    private static JLabel[] memoryLabels = new JLabel[16];
    private static JLabel[] pageTableLabels = new JLabel[16];
    private static JButton[] pcbButtons = new JButton[Const.MAX_PROCESSES];
    private static JButton stepForward = new JButton("Step");
    private static JButton runToFault = new JButton("Run to Next Fault");
    private static JButton runToEnd = new JButton("Run to End");
    private static JLabel frameRefsLabel = new JLabel();
    private static JLabel pageFaultsLabel = new JLabel();
    private static JLabel completeLabel = new JLabel();
    private static JLabel pageTableTitle = new JLabel("Click Process for Page Table");
    private static StepForward step = new StepForward();
    private static RunToFault faultRun = new RunToFault();
    private static RunToEnd endRun = new RunToEnd();
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
        
        // setup frame
        
        frame.pack();
        frame.setVisible(true);
        frame.setSize(width,height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // add panel
        
        frame.add(panel);

        // setup the layout of the panel

        int bodyRows = 0;
        int bodyCols = 3;
        int hgap = 25; // horizontal gap
        int vgap = 10; // vertical gap
        panel.setLayout(new GridLayout(bodyRows, bodyCols, hgap, vgap));
        
        // build display with action listeners
        
        buildDisplay();
        
        refreshMemoryContent();
        
    }
    
    static class StepForward implements ActionListener { 
        
        public void actionPerformed (ActionEvent e) {
            programController.set(Const.STEP_ONCE);
            while (programController.get() == Const.STEP_ONCE) {}
            
            refreshMemoryContent();
            
        }
    }
    
    static class RunToFault implements ActionListener {
    
        public void actionPerformed (ActionEvent e) {
            programController.set(Const.RUN_TO_FAULT);
            while (programController.get() == Const.RUN_TO_FAULT) {
                if (VirtualM.complete) {
                    break;
                }
            }
            
            refreshMemoryContent();
            
        }
    }
    
    static class RunToEnd implements ActionListener {
        
        public void actionPerformed (ActionEvent e) {
            programController.set(Const.RUN_TO_END);
            while (!VirtualM.complete) {}
            
            refreshMemoryContent();
            
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
            pageTableLabels[i++].setText("NO PAGES IN MEMORY");
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
        } else {
            pageTableLabels[i++].setText("NO PAGES IN MEMORY");
        }
        
        pageTableLabels[i++].setText("--------------------------------");
        pageTableLabels[i++].setText("memory refs: " + VirtualM.pcbArray[pid].getMemoryReferences());
        pageTableLabels[i++].setText("page faults: " + VirtualM.pcbArray[pid].getPageFaults());
        
        while (i < 16) {
            pageTableLabels[i++].setText("");
        }
        
    }
    
    
    static private void refreshMemoryContent()
    {
        if (VirtualM.complete) {
            completeLabel.setText("SIMULATION COMPLETE");
            stepForward.removeActionListener(step);
            runToFault.removeActionListener(faultRun);
            runToEnd.removeActionListener(endRun);
        }
    
        Collection<?> frameCollection = (Collection<?>) VirtualM.memory.viewMemoryContents();
        int f = 0;
        for (Object o : frameCollection) {
            MemoryFrame mf = (MemoryFrame) o;
            String memStr = "";
            if (mf.getOwnerID() < 0) {
                memStr = String.format("[ %02d ] FREE FRAME", mf.getFrameNumber());
            } else {
                memStr = String.format("[ %02d ] P%1d : %06d | accsd: %14d",
                mf.getFrameNumber(), mf.getOwnerID(), mf.getPageReference(), mf.getLastAccessed());
            }
            memoryLabels[f].setText(memStr);
            f++;
        }
        frameRefsLabel.setText("Total memory references: " + VirtualM.referenceCount);
        pageFaultsLabel.setText("Total page faults: " + VirtualM.pageFaults);
        
        refreshPageTableContent(currentPageTable);
    }
    
    static private void buildDisplay()
    {
        // Titles
        
        JLabel memoryTitle = new JLabel("Physical M (in LRU order)");
        memoryTitle.setHorizontalAlignment(JLabel.CENTER);
        panel.add(memoryTitle);
        
        JLabel pcbTitle = new JLabel();
        pcbTitle.setHorizontalAlignment(JLabel.CENTER);
        panel.add(pcbTitle);
        
        pageTableTitle.setHorizontalAlignment(JLabel.CENTER);
        panel.add(pageTableTitle);
        
        for (int i = 0; i < 16; i++) {
       
            // add first column, ith row
            memoryLabels[i] = new JLabel();
            panel.add(memoryLabels[i]);
            
            // add second column, ith row
            if (i == 0) {
                panel.add(new JLabel("<--- next victim"));
            } else if (i < 7 && i > 1) {
                pcbButtons[i-2] = new JButton("Process " + (i-1));
                panel.add(pcbButtons[i-2]);
            } else if (i == 8) {
                panel.add(frameRefsLabel);
            } else if (i == 9) {
                panel.add(pageFaultsLabel);
            } else if (i == 10) {
                panel.add(completeLabel);
            } else if (i == 11) {
                panel.add(stepForward);
            } else if (i == 12) {
                panel.add(runToFault);
            } else if (i == 13) {
                panel.add(runToEnd);
            } else if (i == 15) {
                panel.add(new JLabel("<--- last frame accessed"));
            } else {
                panel.add(new JLabel());
            }
            
            // add third column, ith row
            pageTableLabels[i] = new JLabel();
            panel.add(pageTableLabels[i]);
            
        }
        
        // action listeners
        
        stepForward.addActionListener(step);
        runToFault.addActionListener(faultRun);
        runToEnd.addActionListener(endRun);
        
        for (int i = 0; i < 5; i++) {
            pcbButtons[i].addActionListener(new displayPageTable(i+1));
        }
    }
    
}









