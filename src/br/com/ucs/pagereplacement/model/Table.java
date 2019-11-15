package br.com.ucs.pagereplacement.model;

import br.com.ucs.pagereplacement.util.AlgorithmMode;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Table extends Thread {
    private List<Frame> frames = new ArrayList<>();
    private List<Page> pages = new ArrayList<>();
    private List<Page> pagesInserted = new ArrayList<>();
    private int algorithm;
    private int replacements;

    public Table(int framesSize, String word, int algorithm) {
        super();
        this.algorithm = algorithm;
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
            if (page1 != null) {
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
            Frame frame = new Frame(i, pagesSize,this);
            frames.add(frame);
        }
    }

    @Override
    public void run() {
        System.out.println("Inserindo...");
        int iteration = -1;

        for (Page page : pages) {
            iteration++;
            System.out.println("Inserir=" + page.getValue());

            System.out.print("   ");
            pagesInserted.add(page);
            printPagesInserted();

            //Verifica se alguma frame está vazio para inserir neste
            Frame emptyFrame = getEmptyFrame();
            if (emptyFrame != null) {
                insert(iteration, page, emptyFrame);
                continue;
            }

            //Verfica se algum frame esteja utilizando esta página
            Frame frameUsingThisPage = findFrameUsingThisPage(page);
            if (frameUsingThisPage != null) {
                frameUsingThisPage.setLastInsertedIndex(iteration);
                frames.forEach(Frame::setOperationNull);
                printFrames();
                continue;
            }

            //Defini qual frame deverá inserir a página
            Frame frameToInsert = compareAndGetFrameToInsert(iteration);
            insert(iteration, page, frameToInsert);
        }
        System.out.println("Replacements="+replacements);
    }

    //Verifica qual frame possui a página a mais tempo não é referenciada
    private Frame compareAndGetFrameToInsert(int iteration) {
        Frame frameToReturn = null;
        if (algorithm == AlgorithmMode.LRU) {

            int actualIndex = 900000;

            for (Frame frame : frames) {
                if (frame.getLastInsertedIndex() < actualIndex) {
                    frameToReturn = frame;
                    actualIndex = frame.getLastInsertedIndex();
                }
            }

        } else if (algorithm == AlgorithmMode.OPTIMAL) {

            int periodToUse = -999;

            for (Frame frame : frames) {

                int id = frame.getActualPage().getId();

                //Copiar lista de páginas
                List<Page> pages = new ArrayList<>();
                pages.addAll(this.pages);
                List<Page> pagesSublist = pages.subList(iteration, pages.size() - 1);
                pagesSublist.remove(0);

                //Calclar distancia até a página ser usada novamente
                int timeToUseAgain = pagesSublist.size()+1;
                for (Page page : pagesSublist) {
                    timeToUseAgain++;
                    if (page.getId() == id) break;
                }

                //Se distancia for a maior que a auxiliar, define como maior distancia atual.
                if (timeToUseAgain > periodToUse) {
                    frameToReturn = frame;
                    periodToUse = timeToUseAgain;
                }
            }
        }

        return frameToReturn;
    }


    //Insere página neste frame, em um índice específico
    private void insert(int iteration, Page page, Frame emptyFrame) {

        try {
            emptyFrame.setIndexToInsert(iteration);
            emptyFrame.setOperation(0);
            emptyFrame.setPageToInsert(page);

            emptyFrame.getSemaphore().release();
            emptyFrame.getSemToInsert().acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        replacements++;

        updateDifferentThanThis(emptyFrame, iteration);
        printFrames();
    }

    //Chama método para atualizar os frames diferentes do passado por parâmetro com última página utilizada por eles
    private void updateDifferentThanThis(Frame emptyFrame, int iteration) {
        List<Frame> collect = frames.stream().filter(frame -> frame.getId() != emptyFrame.getId())
                .collect(Collectors.toList());
        for (Frame frame : collect) {

            try {
                frame.setOperation(1);
                frame.setIndexToInsert(iteration);

                frame.getSemaphore().release();
                frame.getSemToInsert().acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    //Retorna primeiro frame vazio
    private Frame getEmptyFrame() {
        for (Frame frame : frames) {
            boolean empty = frame.isEmpty();
            if (empty) return frame;
        }
        return null;
    }

    //Procura frame que esteja referenciado está página no momento.
    private Frame findFrameUsingThisPage(Page page) {
        for (Frame frame : frames) {
            Page pageFromFrame = frame.getActualPage();
            if (pageFromFrame.getId() == page.getId()) {
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

    public List<Frame> getFrames() {
        return frames;
    }


    public List<Page> getPages() {
        return pages;
    }
}
