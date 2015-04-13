/* 
    A virtual memory system with page replacement.
    Takes as input a 
*/

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Collection;
import java.io.*;


class VirtualM {

    public static void main(String[] args) {
    
        File input = null;
        Scanner scan = null;
        
        try {
        
            input = new File(args[0]);
            scan = new Scanner(input);
            
        } catch (NullPointerException e) {
            System.err.println("Invalid input file.");
            System.exit(1);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Must supply input file as argument.");
            System.exit(1);
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
            System.exit(1); 
        }
        
        PCB[] pcbArray = new PCB[Const.MAX_PROCESSES + 1];
        PhysicalMemory memory = new PhysicalMemory();
        
        printMemorySnapshot(memory);
        
        try {
            for (int referenceCount = 1; scan.hasNext(); referenceCount++) {
                
                
                // get the process ID from the next line of the input
            
                int currentPid = extractPid(scan.next());
                
                // add the process to the pcbArray if it is not already there
                
                if (pcbArray[currentPid] == null) {
                    pcbArray[currentPid] = new PCB(currentPid);
                }
                
                Integer logicalAddress = scan.nextInt();
                
                System.out.printf("%03d : P%d | %06d | ", referenceCount, currentPid, logicalAddress);
                
                Integer physicalAddress =
                    pcbArray[currentPid].lookupInPageTable(logicalAddress, memory);
                
                if (physicalAddress == null) {
                
                    MemoryFrame removedFrame =
                        pcbArray[currentPid].performPageReplacement(currentPid, logicalAddress, memory);
                 
                    if (removedFrame.getOwnerID() >= 0) {
                        pcbArray[removedFrame.getOwnerID()].removeFromPageTable(removedFrame.getPageReference());
                    }
                    
                    System.out.printf(" %3d\n", removedFrame.getFrameNumber());
                    
                } else {
                
                    System.out.printf(" %3d\n", physicalAddress);
                    
                }
                
                printMemorySnapshot(memory);
                
                
            }
            
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

















