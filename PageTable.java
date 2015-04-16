import java.lang.System;
import java.util.HashMap;
import java.lang.Integer;


public class PageTable {

    private int pid;
    private HashMap<Integer, Integer> pageTable;

    
   /**
    * Construct the page table found in every PCB
    */
    public PageTable(int id)
    {
        pid = id;
        pageTable = new HashMap<Integer, Integer>();
    }
    
   /**
    * Looks up the given logical address in the page table. If it exists,
    * updates the time last accessed in the corresponding memory frame and
    * returns the frame number.
    * If it does not exist in the page table, the least-recently referenced
    * frame in memory is replaced with the pid and logical address given
    *
    * @param  pageRef   the logical address of the page in question
    * @return           the physical frame that the page resides in
    *                   or null if the page is not in memory
    */
    public Integer
    referencePage(int pageRef, PhysicalMemory memory)
    {
        Integer frameNumber = pageTable.get(pageRef);
        
        if (frameNumber != null) {
            memory.referenceMemoryFrame(frameNumber);
        }
        
        return frameNumber;
    }
    
    public void addEntry(Integer pageRef, Integer frame)
    {
        pageTable.put(pageRef, frame);
    }
    
    public void removeEntry(Integer pageRef)
    {
        pageTable.remove(pageRef);
    }
    
    public HashMap<Integer, Integer> getMap()
    {
        return pageTable;
    }
}
