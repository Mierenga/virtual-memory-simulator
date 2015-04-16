import java.util.HashSet;
import java.util.HashMap;

public class PCB {
    
    private int pid;
    private PageTable pageTable;
    private int pageFaults = 0;
    private int memoryReferences = 0;
    private HashSet<Integer> pages = new HashSet<Integer>();
    
   /**
    * Construct the unique PCB for a process on the system
    */
    public PCB(int p)
    {
        pid = p;
        pageTable = new PageTable(pid);
    }
    
   /**
    * Returns the pid of this PCB
    *
    * @return   the pid of this PCB
    */
    public int getPid()
    {
        return pid;
    }
    
   /**
    * Returns the location of a logical address in memory if it exists,
    * else performs page replacement on the least-recently-used frame
    * in memory.
    *
    * @param  pageRef   the logical address of the page in question
    * @return           the frame number that the page resides in
    */
    public Integer lookupInPageTable(int pageRef, PhysicalMemory mem)
    {
        memoryReferences++;
        
        if (!pages.contains((Integer) pageRef)) {
            pages.add((Integer) pageRef);
        }
        return pageTable.referencePage(pageRef, mem);
    }
    
    public MemoryFrame performPageReplacement(int pid, int pageRef, PhysicalMemory mem)
    {
        pageFaults++;
        VirtualM.pageFaults++;
        MemoryFrame oldFrame = mem.replaceMemoryFrame(pid, pageRef);
        pageTable.addEntry((Integer) pageRef, (Integer) oldFrame.getFrameNumber());
        
        return oldFrame;
    }
    
    public void removeFromPageTable(int pageRef)
    {
        pageTable.removeEntry(pageRef);
    }
    
    public HashMap<Integer, Integer> getPageTable()
    {
        return pageTable.getMap();
    }
}
