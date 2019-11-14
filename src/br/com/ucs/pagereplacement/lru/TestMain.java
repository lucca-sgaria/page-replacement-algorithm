package br.com.ucs.pagereplacement.lru;

public class TestMain {
    public static void main(String[] args) {
        TableLRU lru = new TableLRU(3,"70120304230321201701");
        System.out.println(lru.pagesToString());
        System.out.println(lru.framesToString());

        lru.run();
    }
}
