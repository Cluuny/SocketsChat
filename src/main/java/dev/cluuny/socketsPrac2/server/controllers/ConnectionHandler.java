package dev.cluuny.socketsPrac2.server.controllers;

import dev.cluuny.socketsPrac2.server.view.IOHandler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class ConnectionHandler implements Runnable, MessageListener {
    public static final ArrayList<ConnectionHandler> handlers = new ArrayList<>();
    private static final ArrayList<ConnectionHandler> toRemoveFromHandlers = new ArrayList<>();
    private final IOHandler ioHandler = new IOHandler();
    private final Socket connectionSocket;
    private DataInputStream reader;
    private DataOutputStream writer;
    private String userNickname;


    public ConnectionHandler(Socket connectionSocket) {
        this.connectionSocket = connectionSocket;
        try {
            this.reader = new DataInputStream(this.connectionSocket.getInputStream());
            this.writer = new DataOutputStream(this.connectionSocket.getOutputStream());
            handlers.add(this);
        } catch (IOException e) {
            this.closeConnection();
        }
    }

    @Override
    public void run() {
        try {
            this.writer.writeUTF("Escribe tu nombre para continuar: ");
            this.userNickname = this.reader.readUTF();
            this.broadcastMessage("SERVER: " + this.userNickname + " se ha conectado!");
            this.onMessageReceived();
        } catch (IOException e) {
            this.closeConnection();
        }
    }

    @Override
    public void onMessageReceived() {
        new Thread(() -> {
            try {
                while (this.connectionSocket.isConnected()) {
                    String input = this.reader.readUTF();
                    if (input.startsWith("/quit")) {
                        throw new IOException("Sesión Cerrada.");
                    }
                    String receivedMessage = this.userNickname + ": " + input;
                    this.broadcastMessage(receivedMessage);
                }
            } catch (IOException e) {
                ioHandler.printOutput(e.getMessage());
                this.closeConnection();
            }
        }).start();
    }

    @Override
    public void sendMessage(String message) throws IOException {
        this.writer.writeUTF(message);
    }

    @Override
    public void broadcastMessage(String message) {
        handlers.forEach(handler -> {
            try {
                if (!handler.connectionSocket.isClosed()) {
                    handler.sendMessage(message);
                }
            } catch (IOException e) {
                this.closeConnection();
            }
        });
    }

    public void closeConnection() {
        try {
            this.broadcastMessage("SERVER: " + this.userNickname + " ha abandonado el chat!");
            this.reader.close();
            this.writer.close();
            this.connectionSocket.close();
            this.cleanHandlers();
        } catch (IOException ignore) {
        }
    }

    public void cleanHandlers() {
        new Thread(() -> {
            handlers.forEach(handler -> {
                if (handler.connectionSocket.isClosed()) {
                    toRemoveFromHandlers.add(handler);
                }
            });
            toRemoveFromHandlers.forEach(handlers::remove);
        });
    }
}
