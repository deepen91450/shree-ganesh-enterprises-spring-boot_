package com.shreeganesh.enterprises.security;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;

class RateLimitingFilterTest {

    @Test
    void redirectsHtmlRequestsWhenRateLimited() throws Exception {
        RateLimiterService limiter = new RateLimiterService();
        RateLimitingFilter filter = new RateLimitingFilter(limiter);
        FilterChain chain = (request, response) -> {
        };

        for (int i = 0; i < 6; i++) {
            MockHttpServletRequest request = new MockHttpServletRequest("POST", "/login");
            request.setRemoteAddr("127.0.0.1");
            MockHttpServletResponse response = new MockHttpServletResponse();

            filter.doFilter(request, response, chain);

            if (i < 5) {
                assertThat(response.getRedirectedUrl()).isNull();
            } else {
                assertThat(response.getStatus()).isEqualTo(302);
                assertThat(response.getHeader("Retry-After")).isEqualTo("60");
                assertThat(response.getRedirectedUrl()).isEqualTo("/login?rateLimited=true");
            }
        }
    }
}
