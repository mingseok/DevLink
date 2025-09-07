package dev.devlink.common.file;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("FileConstants 테스트")
class FileConstantsTest {

    @Test
    @DisplayName("FEED 상수값이 올바르다")
    void feedConstant_HasCorrectValue() {
        // then
        assertEquals("feed", FileConstants.FEED);
    }

    @Test
    @DisplayName("PROFILE 상수값이 올바르다")
    void profileConstant_HasCorrectValue() {
        // then
        assertEquals("profile", FileConstants.PROFILE);
    }

    @Test
    @DisplayName("IMAGE_URL_KEY 상수값이 올바르다")
    void imageUrlKeyConstant_HasCorrectValue() {
        // then
        assertEquals("imageUrl", FileConstants.IMAGE_URL_KEY);
    }

    @Test
    @DisplayName("DEFAULT_IMAGE_URL 상수값이 올바르다")
    void defaultImageUrlConstant_HasCorrectValue() {
        // then
        assertEquals("/images/default.png", FileConstants.DEFAULT_IMAGE_URL);
    }

    @Test
    @DisplayName("FileConstants는 final 클래스이다")
    void fileConstants_IsFinalClass() {
        // then
        assertTrue(Modifier.isFinal(FileConstants.class.getModifiers()));
    }

    @Test
    @DisplayName("FileConstants는 private 생성자를 가진다")
    void fileConstants_HasPrivateConstructor() throws NoSuchMethodException {
        // given
        Constructor<FileConstants> constructor = FileConstants.class.getDeclaredConstructor();

        // then
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
    }

    @Test
    @DisplayName("모든 상수가 public static final이다")
    void allConstants_ArePublicStaticFinal() throws NoSuchFieldException {
        // given
        var feedField = FileConstants.class.getField("FEED");
        var profileField = FileConstants.class.getField("PROFILE");
        var imageUrlKeyField = FileConstants.class.getField("IMAGE_URL_KEY");
        var defaultImageUrlField = FileConstants.class.getField("DEFAULT_IMAGE_URL");

        // then
        assertAll(
                () -> assertTrue(Modifier.isPublic(feedField.getModifiers())),
                () -> assertTrue(Modifier.isStatic(feedField.getModifiers())),
                () -> assertTrue(Modifier.isFinal(feedField.getModifiers())),
                
                () -> assertTrue(Modifier.isPublic(profileField.getModifiers())),
                () -> assertTrue(Modifier.isStatic(profileField.getModifiers())),
                () -> assertTrue(Modifier.isFinal(profileField.getModifiers())),
                
                () -> assertTrue(Modifier.isPublic(imageUrlKeyField.getModifiers())),
                () -> assertTrue(Modifier.isStatic(imageUrlKeyField.getModifiers())),
                () -> assertTrue(Modifier.isFinal(imageUrlKeyField.getModifiers())),
                
                () -> assertTrue(Modifier.isPublic(defaultImageUrlField.getModifiers())),
                () -> assertTrue(Modifier.isStatic(defaultImageUrlField.getModifiers())),
                () -> assertTrue(Modifier.isFinal(defaultImageUrlField.getModifiers()))
        );
    }

    @Test
    @DisplayName("상수값들이 null이 아니다")
    void constants_AreNotNull() {
        // then
        assertAll(
                () -> assertNotNull(FileConstants.FEED),
                () -> assertNotNull(FileConstants.PROFILE),
                () -> assertNotNull(FileConstants.IMAGE_URL_KEY),
                () -> assertNotNull(FileConstants.DEFAULT_IMAGE_URL)
        );
    }

    @Test
    @DisplayName("상수값들이 빈 문자열이 아니다")
    void constants_AreNotEmpty() {
        // then
        assertAll(
                () -> assertFalse(FileConstants.FEED.isEmpty()),
                () -> assertFalse(FileConstants.PROFILE.isEmpty()),
                () -> assertFalse(FileConstants.IMAGE_URL_KEY.isEmpty()),
                () -> assertFalse(FileConstants.DEFAULT_IMAGE_URL.isEmpty())
        );
    }
}
