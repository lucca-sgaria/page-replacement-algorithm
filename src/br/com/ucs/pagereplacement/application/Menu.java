package br.com.ucs.pagereplacement.application;

public class Menu {
    public static void main(String[] args) {
        Application app = new Application();

        String pagesList="";

        while (true) {
            if(pagesList.isEmpty())
                pagesList = app.getPages();

            int algorithm = app.getAlgorithmMode();

            app.startAplication(pagesList,algorithm);

            int exit = app.verifyExit();
            if(exit==-1) {
                break;
            } else if(exit==1) {
                pagesList="";
            }

        }
    }
}
