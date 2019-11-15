package br.com.ucs.pagereplacement.lru;

public class TestMain {
    public static void main(String[] args) {
        TableLRU lru = new TableLRU(3,"70120304230321201701");

        lru.getFrames().forEach(Thread::start);
        lru.start();

        try {
            lru.join();
            for (Frame frame : lru.getFrames()) {
                frame.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }



    }
}
