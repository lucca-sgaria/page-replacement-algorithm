package br.com.ucs.pagereplacement.lru;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;

public class Frame extends Thread {
    private int frameId;
    private List<Page> pages = new ArrayList<>();
    private int lastInsertedIndex = -1;

    private Semaphore semToInsert = new Semaphore(0);
    private Page pageToInsert;
    private int indexToInsert;
    private int operation;
    private Semaphore sem = new Semaphore(0);


    public Frame(int frameId, int size) {
        this.frameId = frameId;
        initPages(size);
    }

    @Override
    public void run() {
        try {
            while (true) {
                sem.acquire();
                if (operation == 0) {
                    insert(indexToInsert, pageToInsert);
                } else {
                    update(indexToInsert);
                }
                semToInsert.release();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public int getFrameId() {
        return frameId;
    }

    public int getLastInsertedIndex() {
        return lastInsertedIndex;
    }

    public void setLastInsertedIndex(int lastInsertedIndex) {
        this.lastInsertedIndex = lastInsertedIndex;
    }

    //Iniciar páginas com valor default.
    public void initPages(int size) {
        while (pages.size() < size) pages.add(new Page());
    }

    //Inserir uma página em uma posição específica.
    public void insert(int index, Page page) {
        page.setIndexEntered(index);
        pages.set(index, page);
        lastInsertedIndex = index;
    }

    //Atualizar este indice com a última página utilizada pelo frame.
    public void update(int index) {
        try {
            Page page = getActualPage();
            if (page != null && page.isInserted()) {
                pages.set(index, page);
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
        }
    }

    //Retorna última página utilizada pelo frame.
    public Page getActualPage() {
        List<Page> reversed = new ArrayList<>();
        reversed.addAll(pages);
        Collections.reverse(reversed);

        for (Page page : reversed) {
            if (page.isInserted()) return page;
        }
        return null;
    }


    public boolean isEmpty() {
        return !pages.stream().anyMatch(page -> page.isInserted());
    }


    public Semaphore getSemToInsert() {
        return semToInsert;
    }

    public void setSemToInsert(Semaphore semToInsert) {
        this.semToInsert = semToInsert;
    }

    public Page getPageToInsert() {
        return pageToInsert;
    }

    public void setPageToInsert(Page pageToInsert) {
        this.pageToInsert = pageToInsert;
    }

    public int getIndexToInsert() {
        return indexToInsert;
    }

    public void setIndexToInsert(int indexToInsert) {
        this.indexToInsert = indexToInsert;
    }

    public int getOperation() {
        return operation;
    }

    public void setOperation(int operation) {
        this.operation = operation;
    }

    public Semaphore getSem() {
        return sem;
    }

    public void setSem(Semaphore sem) {
        this.sem = sem;
    }

    public String toDescriptiveString() {
        String desc = "";
        desc += frameId + "-|";
        for (Page page : pages) {
            desc += page.toDescriptiveString() + "|";
        }
        desc += "\n";
        return desc;
    }

    @Override
    public String toString() {
        return "Frame{" +
                "frameId=" + frameId +
                ", pages=" + pages +
                ", lastInsertedIndex=" + lastInsertedIndex +
                ", semToInsert=" + semToInsert +
                ", pageToInsert=" + pageToInsert +
                ", indexToInsert=" + indexToInsert +
                ", operation=" + operation +
                ", sem=" + sem +
                '}';
    }
}
