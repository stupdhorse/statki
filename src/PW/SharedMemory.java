package PW;

import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.Semaphore;

public class SharedMemory {
    private static final int SEMAPHORE_SIZE = 4;
    private static final int TOTAL_SIZE = SEMAPHORE_SIZE * 2;

    private MappedByteBuffer buffer;
    private Semaphore semaphore1;
    private Semaphore semaphore2;

    public SharedMemory() throws Exception {
        RandomAccessFile file = new RandomAccessFile("sharedMemory.dat", "rw");
        FileChannel channel = file.getChannel();
        buffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, TOTAL_SIZE);

        semaphore1 = new Semaphore(1);
        semaphore2 = new Semaphore(1);
    }
}
