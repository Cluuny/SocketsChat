package dev.cluuny.socketsPrac2.client.application;

import dev.cluuny.socketsPrac2.client.view.IOHandler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class AppClient implements Runnable {

    private Socket socket;
    private DataInputStream reader;
    private DataOutputStream writer;
    private IOHandler ioHandler;

    public AppClient() {
        try {
            this.ioHandler = new IOHandler();
            this.socket = new Socket("127.0.0.1", 7000);
            this.reader = new DataInputStream(this.socket.getInputStream());
            this.writer = new DataOutputStream(this.socket.getOutputStream());
        } catch (Exception e) {
            assert ioHandler != null;
            ioHandler.printOutput(e.getMessage());
        }
    }

    @Override
    public void run() {
        this.handleInput();
        this.handleMessages();
    }


    public void handleInput() {
        new Thread(() -> {
            try {
                while (this.socket.isConnected()) {
                    String inputData = ioHandler.requestInput();
                    if (inputData.startsWith("/quit")) {
                        throw new IOException("Cerrando sesión...");
                    }
                    writer.writeUTF(inputData);
                }
            } catch (Exception e) {
                this.shutdownClient(e);
            }
        }).start();
    }

    public void handleMessages() {
        new Thread(() -> {
            try {
                while (this.socket.isConnected()) {
                    if (!this.socket.isClosed()) {
                        String inputData = reader.readUTF();
                        ioHandler.printOutput(inputData);
                    }
                }
            } catch (Exception e) {
                this.shutdownClient(e);
            }
        }).start();
    }

    public void shutdownClient(Exception e) {
        try {
            ioHandler.printOutput(e.getMessage());
            writer.writeUTF("/quit");
            reader.close();
            writer.close();
            socket.close();
        } catch (Exception ignored) {
        }
    }

    public static void main(String[] args) {
        Thread mainThread = new Thread(new AppClient());
        mainThread.start();
    }
}
