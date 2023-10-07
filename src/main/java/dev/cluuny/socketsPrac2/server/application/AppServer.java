package dev.cluuny.socketsPrac2.server.application;


import dev.cluuny.socketsPrac2.server.controllers.ConnectionHandler;
import dev.cluuny.socketsPrac2.server.view.IOHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class AppServer implements Runnable {

    private final ServerSocket serverSocket;

    public AppServer() throws IOException {
        this.serverSocket = new ServerSocket(7000);
        IOHandler ioHandler = new IOHandler();
        ioHandler.printOutput("Server ON");
    }

    @Override
    public void run() {
        try {
            while (!this.serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                Thread clientThread = new Thread(new ConnectionHandler(socket));
                clientThread.start();
            }
        } catch (Exception e) {
            this.shutDown();
        }
    }

    public void shutDown() {
        try {
            serverSocket.close();
        } catch (IOException ignore) {
        }
    }

    public static void main(String[] args) {
        try {
            Thread serverThread = new Thread(new AppServer());
            serverThread.start();
        } catch (IOException ignored) {
        }
    }
}
