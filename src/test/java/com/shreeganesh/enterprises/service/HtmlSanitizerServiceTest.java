package com.shreeganesh.enterprises.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HtmlSanitizerServiceTest {

    @Test
    void removesScriptableHtmlButKeepsBasicFormatting() {
        HtmlSanitizerService sanitizer = new HtmlSanitizerService();

        String cleaned = sanitizer.sanitizeProductDescription(
                "<h2>Seal</h2><p onclick='alert(1)'>Good</p><script>alert(1)</script>"
        );

        assertThat(cleaned).contains("<h2>Seal</h2>");
        assertThat(cleaned).contains("<p>Good</p>");
        assertThat(cleaned).doesNotContain("script");
        assertThat(cleaned).doesNotContain("onclick");
    }
}
