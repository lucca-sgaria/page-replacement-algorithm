package br.com.ucs.pagereplacement.lru;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class TableLRU extends Thread {
    private List<Frame> frames = new ArrayList<>();
    private List<Page> pages = new ArrayList<>();

    public TableLRU(int framesSize, String word) {
        super();
        configurePages(word);
        configureFrames(framesSize, pages.size());
    }

    @Override
    public void run() {
        System.out.println("Inserindo...");
        int iteration = -1;

        for (Page page : pages) {
            iteration++;
            System.out.println("Inserir="+page.toString());

            Frame emptyFrame = getEmptyFrame();
            if(emptyFrame!=null) {
                insert(iteration,page,emptyFrame);
                continue;
            }

            Frame frameUsingThisPage = findFrameUsingThisPage(page);
            if(frameUsingThisPage != null) {
                frameUsingThisPage.setLastInsertedIndex(iteration);

                for (Frame frame2 : frames) {
                    System.out.println(frame2.toDescriptiveString());
                }

                updateDifferentThanThis(frameUsingThisPage,iteration+1);
                continue;
            }

            Frame frameToInsert = compareAndGetFrameToInsert();
            insert(iteration,page,frameToInsert);
        }


    }

    private Frame findFrameUsingThisPage(Page page) {
        for (Frame frame : frames) {
            Page pageFromFrame = frame.getActualPage();
            if(pageFromFrame.getId()==page.getId()) {
                return frame;
            }
        }

        return null;
    }

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

    private void insert(int iteration, Page page, Frame emptyFrame) {
        emptyFrame.insert(iteration,page);
        updateDifferentThanThis(emptyFrame,iteration);

        for (Frame frame : frames) {
            System.out.println(frame.toDescriptiveString());
        }

//        for (Frame frame : frames) {
//            System.out.println("frame " + frame.getId());
//            frame.getPages().forEach(p -> System.out.println(p.toString()));
//        }
    }

    private void updateDifferentThanThis(Frame emptyFrame,int iteration) {
        List<Frame> collect = frames.stream().filter(frame -> frame.getId() != emptyFrame.getId()).collect(Collectors.toList());
        for (Frame frame : collect) {
            frame.update(iteration);
        }
    }

    private Frame getEmptyFrame() {
        for (Frame frame : frames) {
            boolean empty = frame.isEmpty();
            if(empty) return frame;
        }

        return null;
    }

    private void configureFrames(int size, int pagesSize) {
        for (int i = 0; i < size; i++) {
            Frame frame = new Frame(i, pagesSize);
            frames.add(frame);
        }
    }

    private void configurePages(String string) {
        List<Character> chars = string
                .chars()
                .mapToObj(e -> (char) e)
                .collect(Collectors.toList());

        for (int i = 0; i < chars.size(); i++) {
            Character character = chars.get(i);
            Page page1 = pages.stream().filter(page -> page.getValue().equals(character)).findFirst().orElse(null);
            if(page1 != null) {
                pages.add(page1);
            } else {
                Page page = new Page(i, chars.get(i));
                pages.add(page);
            }
        }

        pagesToString();
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
