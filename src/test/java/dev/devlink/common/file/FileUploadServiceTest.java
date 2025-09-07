package dev.devlink.common.file;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FileUploadService 테스트")
class FileUploadServiceTest {

    @Mock
    private AmazonS3 amazonS3;

    @Mock
    private MultipartFile multipartFile;

    private FileUploadService fileUploadService;

    private final String bucket = "test-bucket";
    private final String s3Url = "https://test-bucket.s3.amazonaws.com";

    @BeforeEach
    void setUp() {
        fileUploadService = new FileUploadService(amazonS3);
        ReflectionTestUtils.setField(fileUploadService, "bucket", bucket);
        ReflectionTestUtils.setField(fileUploadService, "s3Url", s3Url);
    }

    @Test
    @DisplayName("이미지 파일 업로드 성공")
    void uploadFile_Success() throws IOException {
        // given
        String directory = "test";
        String originalFilename = "test.jpg";
        byte[] content = "test image content".getBytes();
        
        given(multipartFile.isEmpty()).willReturn(false);
        given(multipartFile.getContentType()).willReturn("image/jpeg");
        given(multipartFile.getSize()).willReturn((long) content.length);
        given(multipartFile.getOriginalFilename()).willReturn(originalFilename);
        given(multipartFile.getInputStream()).willReturn(new ByteArrayInputStream(content));

        // when
        String result = fileUploadService.uploadFile(multipartFile, directory);

        // then
        assertAll(
                () -> assertNotNull(result),
                () -> assertTrue(result.startsWith(s3Url)),
                () -> assertTrue(result.contains(directory)),
                () -> assertTrue(result.endsWith(".jpg"))
        );

        verify(amazonS3).putObject(eq(bucket), anyString(), any(InputStream.class), any(ObjectMetadata.class));
    }

    @Test
    @DisplayName("빈 파일 업로드 시 예외 발생")
    void uploadFile_EmptyFile_ThrowsException() {
        // given
        given(multipartFile.isEmpty()).willReturn(true);

        // when & then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> fileUploadService.uploadFile(multipartFile, "test")
        );
        
        assertEquals("파일이 비어있습니다.", exception.getMessage());
        verifyNoInteractions(amazonS3);
    }

    @Test
    @DisplayName("이미지가 아닌 파일 업로드 시 예외 발생")
    void uploadFile_NonImageFile_ThrowsException() {
        // given
        given(multipartFile.isEmpty()).willReturn(false);
        given(multipartFile.getContentType()).willReturn("text/plain");

        // when & then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> fileUploadService.uploadFile(multipartFile, "test")
        );
        
        assertEquals("이미지 파일만 업로드 가능합니다.", exception.getMessage());
        verifyNoInteractions(amazonS3);
    }

    @Test
    @DisplayName("null ContentType 파일 업로드 시 예외 발생")
    void uploadFile_NullContentType_ThrowsException() {
        // given
        given(multipartFile.isEmpty()).willReturn(false);
        given(multipartFile.getContentType()).willReturn(null);

        // when & then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> fileUploadService.uploadFile(multipartFile, "test")
        );
        
        assertEquals("이미지 파일만 업로드 가능합니다.", exception.getMessage());
        verifyNoInteractions(amazonS3);
    }

    @Test
    @DisplayName("파일 크기 초과 시 예외 발생")
    void uploadFile_FileSizeExceeded_ThrowsException() {
        // given
        long maxSize = 10 * 1024 * 1024; // 10MB
        given(multipartFile.isEmpty()).willReturn(false);
        given(multipartFile.getContentType()).willReturn("image/jpeg");
        given(multipartFile.getSize()).willReturn(maxSize + 1);

        // when & then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> fileUploadService.uploadFile(multipartFile, "test")
        );
        
        assertEquals("파일 크기는 10MB를 초과할 수 없습니다.", exception.getMessage());
        verifyNoInteractions(amazonS3);
    }

    @Test
    @DisplayName("파일 업로드 중 IOException 발생 시 RuntimeException으로 변환")
    void uploadFile_IOException_ThrowsRuntimeException() throws IOException {
        // given
        given(multipartFile.isEmpty()).willReturn(false);
        given(multipartFile.getContentType()).willReturn("image/jpeg");
        given(multipartFile.getSize()).willReturn(1000L);
        given(multipartFile.getOriginalFilename()).willReturn("test.jpg");
        given(multipartFile.getInputStream()).willThrow(new IOException("IO 에러"));

        // when & then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> fileUploadService.uploadFile(multipartFile, "test")
        );
        
        assertEquals("파일 업로드 중 오류가 발생했습니다.", exception.getMessage());
        assertInstanceOf(IOException.class, exception.getCause());
    }

    @Test
    @DisplayName("S3 URL을 포함한 파일 삭제 성공")
    void deleteFile_ValidS3Url_Success() {
        // given
        String fileUrl = s3Url + "/test/file.jpg";
        String expectedFileName = "test/file.jpg";

        // when
        fileUploadService.deleteFile(fileUrl);

        // then
        verify(amazonS3).deleteObject(bucket, expectedFileName);
    }

    @Test
    @DisplayName("S3 URL이 아닌 파일 삭제 시 무시")
    void deleteFile_NonS3Url_Ignored() {
        // given
        String fileUrl = "https://other-domain.com/file.jpg";

        // when
        fileUploadService.deleteFile(fileUrl);

        // then
        verifyNoInteractions(amazonS3);
    }

    @Test
    @DisplayName("null 파일 URL 삭제 시 무시")
    void deleteFile_NullUrl_Ignored() {
        // when
        fileUploadService.deleteFile(null);

        // then
        verifyNoInteractions(amazonS3);
    }

    @Test
    @DisplayName("확장자가 없는 파일 업로드")
    void uploadFile_NoExtension_Success() throws IOException {
        // given
        String directory = "test";
        byte[] content = "test content".getBytes();
        
        given(multipartFile.isEmpty()).willReturn(false);
        given(multipartFile.getContentType()).willReturn("image/jpeg");
        given(multipartFile.getSize()).willReturn((long) content.length);
        given(multipartFile.getOriginalFilename()).willReturn("filename_without_extension");
        given(multipartFile.getInputStream()).willReturn(new ByteArrayInputStream(content));

        // when
        String result = fileUploadService.uploadFile(multipartFile, directory);

        // then
        assertAll(
                () -> assertNotNull(result),
                () -> assertTrue(result.startsWith(s3Url)),
                () -> assertTrue(result.contains(directory)),
                () -> assertFalse(result.endsWith(".")) // 확장자가 없어야 함
        );
    }

    @Test
    @DisplayName("ObjectMetadata가 올바르게 설정된다")
    void uploadFile_SetsCorrectMetadata() throws IOException {
        // given
        String contentType = "image/png";
        long fileSize = 1024L;
        byte[] content = new byte[(int) fileSize];
        
        given(multipartFile.isEmpty()).willReturn(false);
        given(multipartFile.getContentType()).willReturn(contentType);
        given(multipartFile.getSize()).willReturn(fileSize);
        given(multipartFile.getOriginalFilename()).willReturn("test.png");
        given(multipartFile.getInputStream()).willReturn(new ByteArrayInputStream(content));

        ArgumentCaptor<ObjectMetadata> metadataCaptor = ArgumentCaptor.forClass(ObjectMetadata.class);

        // when
        fileUploadService.uploadFile(multipartFile, "test");

        // then
        verify(amazonS3).putObject(anyString(), anyString(), any(InputStream.class), metadataCaptor.capture());
        
        ObjectMetadata capturedMetadata = metadataCaptor.getValue();
        assertAll(
                () -> assertEquals(contentType, capturedMetadata.getContentType()),
                () -> assertEquals(fileSize, capturedMetadata.getContentLength())
        );
    }
}
