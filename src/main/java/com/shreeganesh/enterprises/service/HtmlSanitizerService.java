package com.shreeganesh.enterprises.service;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Service;

@Service
public class HtmlSanitizerService {

    private final Safelist productDescriptionSafelist = Safelist.basicWithImages()
            .addTags("h1", "h2", "h3", "h4", "table", "thead", "tbody", "tr", "th", "td")
            .addAttributes("a", "target", "rel")
            .addAttributes("img", "width", "height", "alt")
            .addAttributes("table", "border")
            .preserveRelativeLinks(true);

    public String sanitizeProductDescription(String html) {
        if (html == null || html.isBlank()) {
            return "";
        }

        return Jsoup.clean(html, productDescriptionSafelist);
    }
}
