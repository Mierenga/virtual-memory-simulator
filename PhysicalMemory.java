import java.util.Comparator;
import java.util.TreeMap;
import java.lang.Integer;
import java.lang.Long;
import java.util.Collection;

public class PhysicalMemory {
    
    private TreeMap<Long, MemoryFrame> frames = null;
    
   /**
    * Construct the object representing the physical memory
    * in the system. Const.MEM_FRAMES specifies the number of
    * frames to be built.
    */
    public PhysicalMemory()
    {
        frames = new TreeMap<Long, MemoryFrame>();
        for (int i = 0; i < Const.MEM_FRAMES; i++) {
            MemoryFrame frame = new MemoryFrame(i);
            Long zero = new Long(i);
            frames.put(zero, frame);
        }
    }
    
   /**
    * Returns an object representing the physical memory of the system
    *
    * @return  a collection of MemoryFrame objects that represent each of the
    *          frames in memory.
    */
    public Collection viewMemoryContents()
    {
        Collection<MemoryFrame> frameCollection = frames.values();
        return frameCollection;
    }
    
   /**
    * Replaces the contents of the physical frame that was least-recently
    * referenced in memory with the given logical page
    * 
    * @param pid      the id # of the new process to own the memory frame
    * @param pageRef  the logical address corresponding to the new
    *                 contents of the memory frame
    * @return         the frame number whose contents were replaced
    */
    public MemoryFrame replaceMemoryFrame(int pid, int pageRef)
    {
        MemoryFrame oldFrame = frames.remove(frames.firstKey());
        MemoryFrame newFrame = new MemoryFrame(oldFrame.getFrameNumber());
        newFrame.setOwnerID(pid);
        newFrame.setPageReference(pageRef);
        long time = System.nanoTime();
        newFrame.setLastAccessed(time);
        frames.put((Long) time, newFrame);
        
        return oldFrame;
    }
    
   /**
    * Updates the time last accessed of the memory frame for the given frame
    * number.
    * 
    * @param pid      the id # of the process that owns the memory frame
    * @param pageRef  the logical address corresponding to the contents of
    *                 the memory frame
    * @return         the frame number that was updated
    */
    public MemoryFrame referenceMemoryFrame(Integer num)
    {
        Collection<MemoryFrame> frameCollection = frames.values();
        MemoryFrame frame = null;
        
        for (MemoryFrame f : frameCollection) {
            if (f.getFrameNumber() == (int) num) {
                frame = frames.remove((Long) f.getLastAccessed());
                break;
            }
        }
        
        long time = System.nanoTime();
        frame.setLastAccessed(time);
        
        frames.put((Long) time, frame);
        
        return frame;
    }
    
}






