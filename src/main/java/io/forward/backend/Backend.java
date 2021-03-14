package io.forward.backend;

import io.netty.handler.codec.http.FullHttpResponse;

public interface Backend {
    public FullHttpResponse dispatch();
}
