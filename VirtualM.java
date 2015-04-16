/* 
    A virtual memory system with page replacement.
*/

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Collection;
import java.io.*;
import java.util.concurrent.atomic.AtomicInteger;

public class VirtualM implements Runnable {

    private static String inputFileName;    
    
    public static volatile PCB[] pcbArray = new PCB[Const.MAX_PROCESSES + 1];
    public static volatile PhysicalMemory memory = new PhysicalMemory();
    public static volatile int referenceCount = 0;
    public static volatile int pageFaults = 0;
    public static volatile boolean complete = false;
    
    public VirtualM(String input) {
        inputFileName = input;
        System.out.println(input);
    }

    public void run() {
        File input = null;
        Scanner scan = null;
        
        try {
        
            input = new File(inputFileName);
            scan = new Scanner(input);
            
        } catch (NullPointerException e) {
            System.err.println("Invalid input file.");
            System.exit(1);
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
            System.exit(1); 
        }
        
        int prevPageFaults = pageFaults;
        
        try {
        
            while (scan.hasNext()) {
                
                while(true) {
                    if (SwingInterface.programController.get() == Const.STEP_ONCE) {
                        break;
                    } else if (SwingInterface.programController.get() == Const.RUN_TO_FAULT) {
                        break;
                    } else if (SwingInterface.programController.get() == Const.RUN_TO_END) {
                        break;
                    }
                }
                referenceCount++;
                
                // get the process ID from the next line of the input
            
                int currentPid = extractPid(scan.next());
                
                // add the process to the pcbArray if it is not already there
                
                if (pcbArray[currentPid] == null) {
                    pcbArray[currentPid] = new PCB(currentPid);
                }
                
                Integer logicalAddress = scan.nextInt();
                
                Integer physicalAddress =
                    pcbArray[currentPid].lookupInPageTable(logicalAddress, memory);
                
                if (physicalAddress == null) {
                
                    MemoryFrame removedFrame =
                        pcbArray[currentPid].performPageReplacement(currentPid, logicalAddress, memory);
                 
                    if (removedFrame.getOwnerID() >= 0) {
                        pcbArray[removedFrame.getOwnerID()].removeFromPageTable(removedFrame.getPageReference());
                    }
                    
                }
                
                if (SwingInterface.programController.get() == Const.RUN_TO_FAULT) {
                    if (prevPageFaults < pageFaults) {
                        SwingInterface.programController.set(Const.WAIT);
                    }
                } else if (SwingInterface.programController.get() == Const.STEP_ONCE) {
                    SwingInterface.programController.set(Const.WAIT);
                }
                
                prevPageFaults = pageFaults;
            }
            
            complete = true;
            
            while (true) {}
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        
        
    }
   /**
    * Returns the integer process ID from the string
    * token in the input file.
    *     e.g., extractPid("P2:") returns 2
    *       
    * @param token  The first token read by a Scanner in a line
    *                 of the input file
    * @return       The pid of process in the token
    *
    */
    public static int
    extractPid(String token)
    {
        Matcher matcher = Pattern.compile("\\d+").matcher(token);
        matcher.find();
        return Integer.valueOf(matcher.group());
    }
   /**
    * Prints a picture of the current memory in the system
    *
    * @param mem  The PhysicalMemory object to print
    *
    */
    public static void
    printMemorySnapshot(PhysicalMemory mem)
    {
        Collection<?> frameCollection = (Collection<?>) mem.viewMemoryContents();
        for (Object o : frameCollection) {
            MemoryFrame mf = (MemoryFrame) o;
            System.out.printf("\t[%02d] P", mf.getFrameNumber());
            System.out.print(mf.getOwnerID() + " : ");
            System.out.printf("%06d", mf.getPageReference());
            System.out.print(" | time: " + mf.getLastAccessed() + "\n");
        }
    }

    
}

















