package br.com.ucs.pagereplacement.application;

import br.com.ucs.pagereplacement.model.Frame;
import br.com.ucs.pagereplacement.model.Table;

import java.util.Scanner;

public class Application {
    public String getPages() {
        Scanner scanner1 = new Scanner(System.in);
        // 6 5 4 3 2 1 1 3 5 2 4 6 1 2 3 4 5 6
        // 1 2 3 4 5 6 6 5 4 3 2 1 1 2 3 4 5 6
        while (true) {
            System.out.println("Informe as páginas ou -1 para páginas padrão 654321135246123456 ou -2 para 123456654321123456: ");
            String pages = scanner1.next().trim();

            if(!pages.isEmpty()) {
                if(pages.equals("-1")) {
                    return "654321135246123456";
                } else if(pages.equals("-2")) {
                    return "123456654321123456";
                }
            }
        }
    }

    public int getAlgorithmMode() {
        Scanner scanner1 = new Scanner(System.in);

        while (true) {
            System.out.println("Informe o algoritmo [0] - LRU ou [1] - Algoritmo ótimo : ");
            String algorithm = scanner1.next().trim();

            try {
               int  mode = Integer.parseInt(algorithm);
                if(mode==0 || mode==1) {
                    return mode;
                }
            } catch (NumberFormatException ex) {
                System.out.println("Inválido.");
            }
        }
    }

    public void startAplication(String pagesList, int algorithm) {
        Table table = new Table(3,pagesList,algorithm);

        table.getFrames().forEach(Thread::start);
        table.start();

        try {
            table.join();
            for (Frame frame : table.getFrames()) {
                frame.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public int verifyExit() {
        Scanner scanner1 = new Scanner(System.in);
        while (true) {
            System.out.println("Informe [-1] Sair , [0] Menu com mesmas páginas ou [1] Menu com nova palavra :");
            String algorithm = scanner1.next().trim();
            try {
                int  mode = Integer.parseInt(algorithm);
                if(mode==-1 || mode==0|| mode==1) {
                    return mode;
                }
            } catch (NumberFormatException ex) {
                System.out.println("Inválido.");
            }
        }
    }
}
