package io.forward.gateway;

import io.forward.backend.HttpBackend;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) {
        log.info("Received HTTP request {} {}", request.method(), request.uri());

        FullHttpResponse response = new HttpBackend("https://jsonplaceholder.typicode.com/todos/").dispatch();

        ctx.write(response);
        ctx.flush();
    }
}
