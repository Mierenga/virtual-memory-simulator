public class MemoryFrame {

    private int frameNumber;
    private long lastAccessed = 0;
    private int ownerID = Const.EMPTY_FRAME;
    private int pageReference = Const.EMPTY_FRAME;
    
    public MemoryFrame(int f)
    {
        frameNumber = f;
    }
    
    public void setOwnerID(int pid)
    {
        ownerID = pid;
    }
    
    public void setPageReference(int addr)
    {
        pageReference = addr;
    }
    
    public void setLastAccessed(long time)
    {
        lastAccessed = time;
    }
    
    public int getOwnerID()
    {
        return ownerID;
    }
    
    public int getPageReference() 
    {
        return pageReference;
    }
    
    public int getFrameNumber()
    {
        return frameNumber;
    }
    
    public long getLastAccessed()
    {
        return lastAccessed;
    }
    
    public String getFrameString()
    {
        return new String("[" + frameNumber + "] " + ownerID + ": " + pageReference);
    }
}
