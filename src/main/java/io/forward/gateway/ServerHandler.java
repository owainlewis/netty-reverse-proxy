package io.forward.gateway;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.HttpResponseStatus;

import io.netty.util.CharsetUtil;

import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class ServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    public CompletableFuture<HttpResponse<String>> sendRequest() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest clientRequest = java.net.http.HttpRequest.newBuilder()
                .uri(URI.create("https://jsonplaceholder.typicode.com/todos/"))
                .build();

        return client.sendAsync(clientRequest, java.net.http.HttpResponse.BodyHandlers.ofString());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) {

        log.info("Received HTTP request {} {}", request.method(), request.uri());

        CompletableFuture<HttpResponse<String>> backendResponse = sendRequest();

        backendResponse.thenApply(r -> {
            ByteBuf content = Unpooled.copiedBuffer(r.body(), CharsetUtil.UTF_8);
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());

            ctx.write(response);
            ctx.flush();

            return response;
        });
    }

}
