package io.forward;

import io.forward.gateway.listener.HttpsListener;

public class Application {
    public static void main(String[] args) throws Exception {
        new HttpsListener(8080).run();
    }
}