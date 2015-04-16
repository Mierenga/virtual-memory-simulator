public class Const {
    
    private Const(){}

    // memory system constants
    
    public static final int MAX_PROCESSES = 5;
    public static final int MEM_FRAMES = 16;
    public static final int LOGICAL_ADDRESS_SPACE = 64; //kilobytes
    public static final int PAGE_FRAME_SIZE = 1; //kilobytes
    public static final int EMPTY_FRAME = -1;
    
    // AtomicInteger values
    
    public static final int WAIT = 0;
    public static final int STEP_ONCE = 1;
    public static final int RUN_TO_FAULT = 2;
    public static final int RUN_TO_END = 3;

}
