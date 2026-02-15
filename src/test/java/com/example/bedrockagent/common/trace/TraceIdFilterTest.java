package com.example.bedrockagent.common.trace;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;

class TraceIdFilterTest {

    @Test
    void addsTraceIdHeaderAndRequestAttribute() throws Exception {
        var filter = new TraceIdFilter();
        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();
        var chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        assertThat(response.getHeader(TraceIdFilter.TRACE_ID_HEADER)).isNotBlank();
        assertThat(request.getAttribute(TraceIdFilter.TRACE_ID_KEY)).isEqualTo(response.getHeader(TraceIdFilter.TRACE_ID_HEADER));
    }
}
