package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlRequestDto;
import faang.school.urlshortenerservice.dto.UrlResponseDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/v1/url")
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;

    @PostMapping
    public UrlResponseDto createShortUrl(@Valid @RequestBody UrlRequestDto urlRequestDto) {
        return urlService.createShortUrl(urlRequestDto.getLongUrl());
    }

    @GetMapping("/{hash}")
    public RedirectView redirectToLongUrl(@PathVariable String hash) {
        String longUrl = urlService.getLongUrlByHash(hash);
        return new RedirectView(longUrl);
    }
}
