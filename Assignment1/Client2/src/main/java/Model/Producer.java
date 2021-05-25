package Model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Producer implements Runnable {
    private final DataBuffer dataBuffer;
    private Integer debugLineCounter = 0;
    private final Scanner scanner;


    public Producer(File file, DataBuffer dataBuffer) throws IllegalArgumentException, FileNotFoundException {
        // data buffer
        if (dataBuffer == null) throw new IllegalArgumentException("Data buffer cannot be null");
        this.dataBuffer = dataBuffer;

        // validate file
        if (file == null) throw new IllegalArgumentException("file cannot be null.");

        // initialize a scanner to read text.
        if (!file.exists()) throw new FileNotFoundException("File not found");
        this.scanner = new Scanner(file);
    }

    @Override
    public void run() {
        try {
            while (this.scanner.hasNext()) {
                // read data line by line
                String textLine = this.scanner.nextLine();

                // put in blocking queue
                if (!textLine.isEmpty()) {
                    this.dataBuffer.queue.put(textLine);
                    this.debugLineCounter += 1;
                }
            }

            // decrement the producer count in data buffer class.
            this.dataBuffer.producerComplete.getAndDecrement();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Integer getDebugLineCounter() {
        return debugLineCounter;
    }
}
