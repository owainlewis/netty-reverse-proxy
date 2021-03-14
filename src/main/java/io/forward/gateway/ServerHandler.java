package io.forward.gateway;

import io.forward.backend.HttpBackend;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;

import java.net.http.HttpClient;

import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

@Slf4j
public class ServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    /**
     * Send an HTTP response back
     *
     * @param ctx
     * @param request
     * @param status an HTTP status
     */
    private void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest request, HttpResponseStatus status) {
        final FullHttpResponse resp = new DefaultFullHttpResponse(HTTP_1_1, status);
        resp.headers().add("Content-Length", "0");
        final ChannelFuture cf = ctx.channel().writeAndFlush(resp);
        if (!HttpUtil.isKeepAlive(request)) {
            cf.addListener(ChannelFutureListener.CLOSE);
        }
    }

    private void sendFullHttpResponse(ChannelHandlerContext ctx, FullHttpRequest request, FullHttpResponse resp) {
        final ChannelFuture cf = ctx.channel().writeAndFlush(resp);
        if (!HttpUtil.isKeepAlive(request)) {
            cf.addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) {

        log.info("Received HTTP request {} {}", request.method(), request.uri());

        final HttpBackend backend = new HttpBackend(HttpClient.newHttpClient(), "http://localhost:8009/");

        final String path = request.uri();
        if (path == null) {
            sendHttpResponse(ctx, request, BAD_REQUEST);
            return;
        }

        FullHttpResponse response = backend.dispatch();



        // Default response
        sendHttpResponse(ctx, request, NOT_FOUND);
    }
}
