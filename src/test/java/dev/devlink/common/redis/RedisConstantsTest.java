package dev.devlink.common.redis;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RedisConstants 테스트")
class RedisConstantsTest {

    @Test
    @DisplayName("SYNC_INTERVAL_MILLIS 상수값이 올바르다")
    void syncIntervalMillis_HasCorrectValue() {
        // then
        assertEquals(60_000L, RedisConstants.SYNC_INTERVAL_MILLIS);
    }

    @Test
    @DisplayName("RANKING_REFRESH_INTERVAL 상수값이 올바르다")
    void rankingRefreshInterval_HasCorrectValue() {
        // then
        assertEquals(30_000L, RedisConstants.RANKING_REFRESH_INTERVAL);
    }

    @Test
    @DisplayName("DUPLICATE_PREVENTION_TTL 상수값이 올바르다")
    void duplicatePreventionTtl_HasCorrectValue() {
        // then
        assertEquals(300_000L, RedisConstants.DUPLICATE_PREVENTION_TTL);
    }

    @Test
    @DisplayName("REDIS_SET_ADD_SUCCESS 상수값이 올바르다")
    void redisSetAddSuccess_HasCorrectValue() {
        // then
        assertEquals(1L, RedisConstants.REDIS_SET_ADD_SUCCESS);
    }

    @Test
    @DisplayName("TOP_LIMIT 상수값이 올바르다")
    void topLimit_HasCorrectValue() {
        // then
        assertEquals(5, RedisConstants.TOP_LIMIT);
    }

    @Test
    @DisplayName("START_INDEX 상수값이 올바르다")
    void startIndex_HasCorrectValue() {
        // then
        assertEquals(0, RedisConstants.START_INDEX);
    }

    @Test
    @DisplayName("RedisConstants는 final 클래스이다")
    void redisConstants_IsFinalClass() {
        // then
        assertTrue(Modifier.isFinal(RedisConstants.class.getModifiers()));
    }

    @Test
    @DisplayName("RedisConstants는 private 생성자를 가진다")
    void redisConstants_HasPrivateConstructor() throws NoSuchMethodException {
        // given
        Constructor<RedisConstants> constructor = RedisConstants.class.getDeclaredConstructor();

        // then
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
    }

    @Test
    @DisplayName("모든 상수가 public static final이다")
    void allConstants_ArePublicStaticFinal() throws NoSuchFieldException {
        // given
        var syncIntervalField = RedisConstants.class.getField("SYNC_INTERVAL_MILLIS");
        var rankingRefreshField = RedisConstants.class.getField("RANKING_REFRESH_INTERVAL");
        var duplicatePreventionField = RedisConstants.class.getField("DUPLICATE_PREVENTION_TTL");
        var redisSetAddField = RedisConstants.class.getField("REDIS_SET_ADD_SUCCESS");
        var topLimitField = RedisConstants.class.getField("TOP_LIMIT");
        var startIndexField = RedisConstants.class.getField("START_INDEX");

        // then
        assertAll(
                () -> assertTrue(Modifier.isPublic(syncIntervalField.getModifiers())),
                () -> assertTrue(Modifier.isStatic(syncIntervalField.getModifiers())),
                () -> assertTrue(Modifier.isFinal(syncIntervalField.getModifiers())),
                
                () -> assertTrue(Modifier.isPublic(rankingRefreshField.getModifiers())),
                () -> assertTrue(Modifier.isStatic(rankingRefreshField.getModifiers())),
                () -> assertTrue(Modifier.isFinal(rankingRefreshField.getModifiers())),
                
                () -> assertTrue(Modifier.isPublic(duplicatePreventionField.getModifiers())),
                () -> assertTrue(Modifier.isStatic(duplicatePreventionField.getModifiers())),
                () -> assertTrue(Modifier.isFinal(duplicatePreventionField.getModifiers())),
                
                () -> assertTrue(Modifier.isPublic(redisSetAddField.getModifiers())),
                () -> assertTrue(Modifier.isStatic(redisSetAddField.getModifiers())),
                () -> assertTrue(Modifier.isFinal(redisSetAddField.getModifiers())),
                
                () -> assertTrue(Modifier.isPublic(topLimitField.getModifiers())),
                () -> assertTrue(Modifier.isStatic(topLimitField.getModifiers())),
                () -> assertTrue(Modifier.isFinal(topLimitField.getModifiers())),
                
                () -> assertTrue(Modifier.isPublic(startIndexField.getModifiers())),
                () -> assertTrue(Modifier.isStatic(startIndexField.getModifiers())),
                () -> assertTrue(Modifier.isFinal(startIndexField.getModifiers()))
        );
    }

    @Test
    @DisplayName("시간 관련 상수들이 합리적인 값을 가진다")
    void timeConstants_HaveReasonableValues() {
        // then
        assertAll(
                () -> assertTrue(RedisConstants.SYNC_INTERVAL_MILLIS > 0),
                () -> assertTrue(RedisConstants.RANKING_REFRESH_INTERVAL > 0),
                () -> assertTrue(RedisConstants.DUPLICATE_PREVENTION_TTL > 0),
                () -> assertTrue(RedisConstants.SYNC_INTERVAL_MILLIS > RedisConstants.RANKING_REFRESH_INTERVAL),
                () -> assertTrue(RedisConstants.DUPLICATE_PREVENTION_TTL > RedisConstants.SYNC_INTERVAL_MILLIS)
        );
    }

    @Test
    @DisplayName("인덱스 관련 상수들이 유효한 값을 가진다")
    void indexConstants_HaveValidValues() {
        // then
        assertAll(
                () -> assertTrue(RedisConstants.TOP_LIMIT > 0),
                () -> assertTrue(RedisConstants.START_INDEX >= 0),
                () -> assertTrue(RedisConstants.START_INDEX < RedisConstants.TOP_LIMIT)
        );
    }

    @Test
    @DisplayName("Redis 성공 상수가 올바른 값을 가진다")
    void redisSuccessConstant_HasCorrectValue() {
        // then
        assertTrue(RedisConstants.REDIS_SET_ADD_SUCCESS > 0);
    }

    @Test
    @DisplayName("시간 상수들이 밀리초 단위로 설정되어 있다")
    void timeConstants_AreInMilliseconds() {
        // then
        assertAll(
                // 1분 = 60,000ms
                () -> assertEquals(60 * 1000, RedisConstants.SYNC_INTERVAL_MILLIS),
                // 30초 = 30,000ms  
                () -> assertEquals(30 * 1000, RedisConstants.RANKING_REFRESH_INTERVAL),
                // 5분 = 300,000ms
                () -> assertEquals(5 * 60 * 1000, RedisConstants.DUPLICATE_PREVENTION_TTL)
        );
    }
}
