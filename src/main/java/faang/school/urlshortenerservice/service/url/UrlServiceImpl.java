package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.NotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.cache.CacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.view.RedirectView;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Slf4j
@Service
public class UrlServiceImpl implements UrlService{

    private final UrlCacheRepository urlCacheRepository;
    private final UrlRepository urlRepository;
    private final CacheService cacheService;

    @Transactional(readOnly = true)
    public RedirectView getRedirectView(String hash) {
        String url = urlCacheRepository.getUrlByHash(hash).orElseGet(
                () -> urlRepository.getUrlByHash(hash).map(Url::getUrl).orElseThrow(
                        () -> new NotFoundException("URL not found for hash: " + hash)));
        return new RedirectView(url);
    }

    @Override
    @Transactional
    public String createShortUrl(UrlDto dto) {
        String hash = cacheService.getHash();

        Url url = urlRepository.save(new Url(hash, dto.getUrl(), LocalDateTime.now()));
        log.info("Saved url: {}", dto.getUrl());

        urlCacheRepository.saveUrlByHash(dto.getUrl(), hash);
        log.info("Saved url: {} by hash: {} in cache", dto.getUrl(), hash);
        return url.getHash();
    }
}