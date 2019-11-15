package br.com.ucs.pagereplacement.lru;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Frame{
    private int id;
    private List<Page> pages = new ArrayList<>();
    private int lastInsertedIndex = -1;

    public Frame(int id, int size) {
        this.id = id;
        initPages(size);
    }

    public int getId() {
        return id;
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

    public String toDescriptiveString() {
        String desc = "";
        desc += id + "-|";
        for (Page page : pages) {
            desc += page.toDescriptiveString() + "|";
        }
        desc += "\n";
        return desc;
    }


}
