package br.com.ucs.pagereplacement.lru;

public class Page {
    private int id;
    private Character value;
    private int indexEntered;

    public Page() {
        id=-1;
        value=' ';
        indexEntered=-1;
    }

    public Page(int id ,Character value) {
        this.id = id;
        this.value = value;
        indexEntered=-1;
    }

    public boolean isInserted() {
        return id!=-1;
    }

    public int getId() {
        return id;
    }

    public Character getValue() {
        return value;
    }

    public void setIndexEntered(int indexEntered) {
        this.indexEntered = indexEntered;
    }

    public String toDescriptiveString() {
        return value.toString();
    }

    @Override
    public String toString() {
        return "Page{" +
                "id=" + id +
                ", value=" + value +
                ", indexEntered=" + indexEntered +
                '}';
    }
}
