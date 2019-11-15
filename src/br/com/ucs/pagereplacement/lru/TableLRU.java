package br.com.ucs.pagereplacement.lru;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TableLRU extends Thread {
    private List<Frame> frames = new ArrayList<>();
    private List<Page> pages = new ArrayList<>();
    private List<Page> pagesInserted = new ArrayList<>();

    public TableLRU(int framesSize, String word) {
        super();
        configurePages(word);
        configureFrames(framesSize, pages.size());
    }

    //Configura páginas a serem inseridas a partir de uma String
    private void configurePages(String string) {
        List<Character> chars = string
                .chars()
                .mapToObj(e -> (char) e)
                .collect(Collectors.toList());

        for (int i = 0; i < chars.size(); i++) {
            Character character = chars.get(i);
            Page page1 = pages.stream().filter(page -> page.getValue().equals(character)).findFirst().orElse(null);
            //Se valor da página já existe , insere o mesmo objeto
            if(page1 != null) {
                pages.add(page1);
            } else {
                //Se não existe cria novo objeto de página
                Page page = new Page(i, chars.get(i));
                pages.add(page);
            }
        }

        pagesToString();
    }

    //Configura frames
    private void configureFrames(int size, int pagesSize) {
        for (int i = 0; i < size; i++) {
            Frame frame = new Frame(i, pagesSize);
            frames.add(frame);
        }
    }

    @Override
    public void run() {
        System.out.println("Inserindo...");
        int iteration = -1;

        for (Page page : pages) {
            iteration++;
            System.out.println("Inserir="+page.getValue());

            System.out.print("   ");
            pagesInserted.add(page);
            printPagesInserted();

            //Verifica se alguma frame está vazio para inserir neste
            Frame emptyFrame = getEmptyFrame();
            if(emptyFrame!=null) {
                insert(iteration,page,emptyFrame);
                continue;
            }

            //Verfica se algum frame esteja utilizando esta página
            Frame frameUsingThisPage = findFrameUsingThisPage(page);
            if(frameUsingThisPage != null) {
                frameUsingThisPage.setLastInsertedIndex(iteration);
                printFrames();
                continue;
            }

            //Defini qual frame deverá inserir a página
            Frame frameToInsert = compareAndGetFrameToInsert();
            insert(iteration,page,frameToInsert);
        }


    }

    //Verifica qual frame possui a página a mais tempo não é referenciada
    private Frame compareAndGetFrameToInsert() {
        Frame frameToReturn = null;
        int actualIndex = 100000;

        for (Frame frame : frames) {
            if(frame.getLastInsertedIndex() < actualIndex) {
                frameToReturn = frame;
                actualIndex = frame.getLastInsertedIndex();
            }
        }

        return frameToReturn;
    }


    //Insere página neste frame, em um índice específico
    private void insert(int iteration, Page page, Frame emptyFrame) {
        emptyFrame.insert(iteration,page);
        updateDifferentThanThis(emptyFrame,iteration);

        printFrames();
    }

    //Chama método para atualizar os frames diferentes do passado por parâmetro com última página utilizada por eles
    private void updateDifferentThanThis(Frame emptyFrame,int iteration) {
        List<Frame> collect = frames.stream().filter(frame -> frame.getId() != emptyFrame.getId()).collect(Collectors.toList());
        for (Frame frame : collect) {
            frame.update(iteration);
        }
    }

    //Retorna primeiro frame vazio
    private Frame getEmptyFrame() {
        for (Frame frame : frames) {
            boolean empty = frame.isEmpty();
            if(empty) return frame;
        }
        return null;
    }

    //Procura frame que esteja referenciado está página no momento.
    private Frame findFrameUsingThisPage(Page page) {
        for (Frame frame : frames) {
            Page pageFromFrame = frame.getActualPage();
            if(pageFromFrame.getId()==page.getId()) {
                return frame;
            }
        }
        return null;
    }

    private void printFrames() {
        for (Frame frame : frames) {
            System.out.println(frame.toDescriptiveString());
        }
    }

    private void printPagesInserted() {
        pagesInserted.forEach(pagea -> System.out.print(pagea.getValue() + " "));
        System.out.println("");
    }

    public String pagesToString() {
        String str = "";
        for (Page page : pages) {
            str += page.toString() + "\n";
        }
        return str;
    }

    public String framesToString() {
        String str = "";
        for (Frame frame : frames) {
            str += frame.toDescriptiveString();
        }
        return str;
    }
}
