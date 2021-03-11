package io.forward.filters;

import io.netty.handler.codec.http.FullHttpRequest;

public interface RequestFilter {
    /**
     * Modify an incoming request before dispatching to a backend destination
     *
     * @param request An HTTP request
     * @return boolean
     */
    public boolean apply(FullHttpRequest request);
}
