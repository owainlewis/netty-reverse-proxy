package io.forward;

import io.forward.gateway.listener.HttpsListener;

public class App {
    public static void main(String[] args) throws Exception {
        new HttpsListener(8080).run();
    }
}