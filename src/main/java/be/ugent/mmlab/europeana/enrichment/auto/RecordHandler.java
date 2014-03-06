package be.ugent.mmlab.europeana.enrichment.auto;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Copyright 2014 MMLab, UGent
 * Created by ghaesen on 2/17/14.
 */
public class RecordHandler {

    private final static BlockingQueue<Record> recordQueue = new LinkedBlockingQueue<>();

    static {
        Thread recordHandlerThread = new Thread(new Consumer(), "Record Handler");
        recordHandlerThread.start();
    }

    public boolean addRecord(String reference, String record) {
        return recordQueue.offer(new Record(reference, record));
    }


    private static class Consumer implements Runnable {

        @Override
        public void run() {
            try {
                while (true) {
                    Record record = recordQueue.take();
                    System.out.println("record.getReference() = " + record.getReference());
                }
            } catch (InterruptedException e) {
                System.err.println("*** interrupted!! ***");
            }
        }
    }

}
