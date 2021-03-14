package io.forward.backend;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class HttpBackend implements Backend {

    private final String backendUrl;

    private final HttpClient client;

    public HttpBackend(HttpClient httpClient, String url) {
        client = httpClient;
        backendUrl = url;
    }

    /**
     * Maps a java.net.http.HttpResponse into a Netty HTTP response
     *
     * @param response A java.net.HttpResponse
     *
     * @return A Netty FullHttpResponse
     */
    private FullHttpResponse mapResponse(HttpResponse<String> response) {
        ByteBuf responseBody = Unpooled.copiedBuffer(response.body(), CharsetUtil.UTF_8);
        FullHttpResponse fullResponse =
                new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.valueOf(response.statusCode()), responseBody);
        fullResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, responseBody.readableBytes());

        response.headers().map().forEach((key, value) -> {
            try {
                fullResponse.headers().set(key, value.get(0));
            } catch (Exception e) {
                log.error("Failed to set header {}", key);
            }
        });

        return fullResponse;

    }

    public FullHttpResponse dispatch() {
        HttpRequest clientRequest = java.net.http.HttpRequest.newBuilder()
                .uri(URI.create(backendUrl))
                .build();

        CompletableFuture<HttpResponse<String>> response =
                client.sendAsync(clientRequest, java.net.http.HttpResponse.BodyHandlers.ofString());

        return response.thenApply(this::mapResponse).join();
    }
}
