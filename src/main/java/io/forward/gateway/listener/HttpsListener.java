package io.forward.gateway.listener;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import io.forward.gateway.ServerInitializer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpsListener {
    /**
     * boss thread pool
     */
    private final EventLoopGroup bossGroup;
    /**
     * worker thread pool
     */
    private final EventLoopGroup workerGroup;

    private final int port;

    public HttpsListener(int listenerPort) {
        port = listenerPort;
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
    }

    public void shutdown() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    public void run() throws Exception {
        // A helper class that simplifies server configuration
        ServerBootstrap httpBootstrap = new ServerBootstrap();

        // Configure the server
        httpBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ServerInitializer())
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

        try {
            // Bind and start to accept incoming connections.
            ChannelFuture httpChannel = httpBootstrap.bind(port).sync();
            if (httpChannel.isSuccess()) {
                log.info("API Gateway server started on {}...", port);
            }
            // Wait until server socket is closed
            httpChannel.channel().closeFuture().sync();
        }
        finally {
            shutdown();
        }
    }
}
