package dev.cluuny.socketsPrac2.server.controllers;

import java.io.IOException;

public interface MessageListener {
    void onMessageReceived();

    void sendMessage(String message) throws IOException;

    void broadcastMessage(String message) throws IOException;
}
