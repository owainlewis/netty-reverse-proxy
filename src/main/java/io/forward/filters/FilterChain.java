package io.forward.filters;

import java.util.LinkedList;
import java.util.List;

public class FilterChain {

    private final List<RequestFilter> requestFilters;

    public FilterChain() {
        this.requestFilters = new LinkedList<>();
    }

    /**
     * Add a filter to the filter chain
     *
     * @param filter A [[RequestFilter]] to apply to the incoming request
     */
    public void addRequestFilter(RequestFilter filter) {
        this.requestFilters.add(filter);
    }
}
