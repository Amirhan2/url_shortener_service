package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.TestContainersConfig;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.cache.LocalHashCache;
import faang.school.urlshortenerservice.service.cache.UrlRedisCacheService;
import liquibase.Liquibase;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class UrlServiceTest extends TestContainersConfig {

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    UrlService urlService;

    @SpyBean
    UrlRepository urlRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    DataSource dataSource;

    @MockBean
    LocalHashCache localHashCache;

    @SpyBean
    UrlRedisCacheService urlRedisCacheService;

    @Captor
    ArgumentCaptor<Url> urlArgumentCaptor;


    @BeforeEach
    void init() throws SQLException, LiquibaseException {
        redisTemplate.opsForValue().set("XZHO", "https://vkontakte.ru");

        try (Connection connection = dataSource.getConnection()) {
            Liquibase liquibase = new Liquibase(
                    "db/db.changelog-master.yaml",
                    new ClassLoaderResourceAccessor(),
                    DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection))
            );
            liquibase.dropAll();
            liquibase.update("");
        }
    }

    @AfterEach
    void clear() {
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
    }

    @Test
    void testGetOriginalUrl() {
        var result = urlService.getOriginalUrl("XZHO");
        Assertions.assertEquals("https://vkontakte.ru", result.getUrl());
    }

    @Test
    void testGetOriginalUrl_NotFoundInRedis() {
        redisTemplate.delete("XZHO");
        var result = urlService.getOriginalUrl("XZHO");

        Mockito.verify(urlRepository).findByHash("XZHO");

        assertEquals("https://vkontakte.ru", result.getUrl());
    }

    @Test
    void testGetAndDeleteUnusedUrls() {
        var result = urlService.getAndDeleteUnusedHashes();
        assertEquals(5, result.size());
        assertTrue(urlRepository.findAll().isEmpty());
    }

    @Test
    void testMakeShortUrl() {
        String receivedUrl = "https://google.ru";
        String hash = "XXXX";

        Mockito.when(localHashCache.getHash()).thenReturn(hash);
        var shortUrl = urlService.makeShortUrl(receivedUrl);

        assertEquals("localhost:8080/XXXX", shortUrl);

        Mockito.verify(urlRepository).save(urlArgumentCaptor.capture());
        assertEquals(hash, urlArgumentCaptor.getValue().getHash());
        assertEquals(receivedUrl, urlArgumentCaptor.getValue().getUrl());
        Mockito.verify(urlRedisCacheService).save(hash, receivedUrl);
        assertEquals(receivedUrl, urlRedisCacheService.get(hash).get());
    }

    @Test
    void testGetUrl() {
        var result = urlService.getUrl("XZHO");
        assertEquals("https://vkontakte.ru", result.getUrl());
    }
}