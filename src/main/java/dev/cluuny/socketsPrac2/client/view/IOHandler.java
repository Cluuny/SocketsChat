package dev.cluuny.socketsPrac2.client.view;

import java.util.Scanner;

public class IOHandler {

    private final Scanner sc;

    public IOHandler() {
        this.sc = new Scanner(System.in);
    }

    public String requestInput() {
        return sc.nextLine();
    }

    public void printOutput(String output) {
        System.out.println(output);
    }
}
