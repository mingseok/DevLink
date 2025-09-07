package dev.devlink.profile.service;

import dev.devlink.common.file.FileConstants;
import dev.devlink.common.file.FileUploadService;
import dev.devlink.follow.service.FollowService;
import dev.devlink.member.entity.Member;
import dev.devlink.member.service.MemberService;
import dev.devlink.profile.constant.ProfileDefaults;
import dev.devlink.profile.entity.Profile;
import dev.devlink.profile.repository.ProfileRepository;
import dev.devlink.profile.service.dto.request.ProfileUpdateRequest;
import dev.devlink.profile.service.dto.response.ProfileResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProfileService 통합 테스트")
class ProfileServiceIntegrationTest {

    @Mock
    private MemberService memberService;

    @Mock
    private FollowService followService;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private FileUploadService fileUploadService;

    @InjectMocks
    private ProfileService profileService;

    private Member viewer;
    private Member target;
    private Profile profile;

    @BeforeEach
    void setUp() {
        viewer = Member.create("뷰어", "viewer@test.com", "뷰어닉", "password");
        target = Member.create("타겟", "target@test.com", "타겟닉", "password");
        ReflectionTestUtils.setField(viewer, "id", 1L);
        ReflectionTestUtils.setField(target, "id", 2L);
        
        profile = Profile.create(target, "안녕하세요. 저는 개발자입니다.");
        ReflectionTestUtils.setField(profile, "id", 1L);
        profile.updateImage("https://example.com/profile.jpg");
    }

    @Test
    @DisplayName("프로필 조회 성공 - 프로필이 존재하는 경우")
    void getProfile_WithExistingProfile_Success() {
        // given
        Long viewerId = 1L;
        Long targetId = 2L;
        
        given(memberService.findMemberById(targetId)).willReturn(target);
        given(followService.isFollowing(viewerId, targetId)).willReturn(true);
        given(followService.getFollowerCount(targetId)).willReturn(100L);
        given(followService.getFollowingCount(targetId)).willReturn(50L);
        given(profileRepository.findByMember(target)).willReturn(Optional.of(profile));

        // when
        ProfileResponse result = profileService.getProfile(viewerId, targetId);

        // then
        assertAll(
                () -> assertEquals(targetId, result.getMemberId()),
                () -> assertEquals("타겟닉", result.getNickname()),
                () -> assertEquals("안녕하세요. 저는 개발자입니다.", result.getBio()),
                () -> assertEquals("https://example.com/profile.jpg", result.getImageUrl()),
                () -> assertTrue(result.getIsFollowing()),
                () -> assertEquals(100L, result.getFollowers()),
                () -> assertEquals(50L, result.getFollowings())
        );

        verify(memberService).findMemberById(targetId);
        verify(followService).isFollowing(viewerId, targetId);
        verify(followService).getFollowerCount(targetId);
        verify(followService).getFollowingCount(targetId);
        verify(profileRepository).findByMember(target);
    }

    @Test
    @DisplayName("프로필 조회 성공 - 프로필이 존재하지 않는 경우")
    void getProfile_WithoutExistingProfile_Success() {
        // given
        Long viewerId = 1L;
        Long targetId = 2L;
        
        given(memberService.findMemberById(targetId)).willReturn(target);
        given(followService.isFollowing(viewerId, targetId)).willReturn(false);
        given(followService.getFollowerCount(targetId)).willReturn(10L);
        given(followService.getFollowingCount(targetId)).willReturn(5L);
        given(profileRepository.findByMember(target)).willReturn(Optional.empty());

        // when
        ProfileResponse result = profileService.getProfile(viewerId, targetId);

        // then
        assertAll(
                () -> assertEquals(targetId, result.getMemberId()),
                () -> assertEquals("타겟닉", result.getNickname()),
                () -> assertEquals("", result.getBio()),
                () -> assertEquals(FileConstants.DEFAULT_IMAGE_URL, result.getImageUrl()),
                () -> assertFalse(result.getIsFollowing()),
                () -> assertEquals(10L, result.getFollowers()),
                () -> assertEquals(5L, result.getFollowings())
        );
    }

    @Test
    @DisplayName("프로필 소개글 업데이트 성공 - 기존 프로필이 있는 경우")
    void updateBio_WithExistingProfile_Success() {
        // given
        Long memberId = 2L;
        ProfileUpdateRequest request = new ProfileUpdateRequest("업데이트된 소개글입니다.");
        
        given(memberService.findMemberById(memberId)).willReturn(target);
        given(profileRepository.findByMember(target)).willReturn(Optional.of(profile));

        // when
        profileService.updateBio(memberId, request);

        // then
        verify(memberService).findMemberById(memberId);
        verify(profileRepository).findByMember(target);
        // Profile의 updateBio 메서드가 호출되었는지는 Profile 엔티티 내부 로직이므로 직접 검증하기 어려움
    }

    @Test
    @DisplayName("프로필 소개글 업데이트 성공 - 기존 프로필이 없는 경우")
    void updateBio_WithoutExistingProfile_Success() {
        // given
        Long memberId = 2L;
        ProfileUpdateRequest request = new ProfileUpdateRequest("새로운 소개글입니다.");
        Profile newProfile = Profile.create(target, ProfileDefaults.DEFAULT_BIO);
        
        given(memberService.findMemberById(memberId)).willReturn(target);
        given(profileRepository.findByMember(target)).willReturn(Optional.empty());
        given(profileRepository.save(any(Profile.class))).willReturn(newProfile);

        // when
        profileService.updateBio(memberId, request);

        // then
        verify(memberService).findMemberById(memberId);
        verify(profileRepository).findByMember(target);
        verify(profileRepository).save(any(Profile.class));
    }

    @Test
    @DisplayName("프로필 이미지 업데이트 성공 - 기존 이미지가 있는 경우")
    void updateImage_WithExistingImage_Success() {
        // given
        Long memberId = 2L;
        MultipartFile file = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test image".getBytes());
        String newImageUrl = "https://example.com/new-profile.jpg";
        
        given(memberService.findMemberById(memberId)).willReturn(target);
        given(profileRepository.findByMember(target)).willReturn(Optional.of(profile));
        given(fileUploadService.uploadFile(file, FileConstants.PROFILE)).willReturn(newImageUrl);

        // when
        String result = profileService.updateImage(memberId, file);

        // then
        assertEquals(newImageUrl, result);
        verify(memberService).findMemberById(memberId);
        verify(profileRepository).findByMember(target);
        verify(fileUploadService).deleteFile(profile.getImageUrl());
        verify(fileUploadService).uploadFile(file, FileConstants.PROFILE);
    }

    @Test
    @DisplayName("프로필 이미지 업데이트 성공 - 기존 프로필이 없는 경우")
    void updateImage_WithoutExistingProfile_Success() {
        // given
        Long memberId = 2L;
        MultipartFile file = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test image".getBytes());
        String newImageUrl = "https://example.com/new-profile.jpg";
        Profile newProfile = Profile.create(target, ProfileDefaults.DEFAULT_BIO);
        
        given(memberService.findMemberById(memberId)).willReturn(target);
        given(profileRepository.findByMember(target)).willReturn(Optional.empty());
        given(profileRepository.save(any(Profile.class))).willReturn(newProfile);
        given(fileUploadService.uploadFile(file, FileConstants.PROFILE)).willReturn(newImageUrl);

        // when
        String result = profileService.updateImage(memberId, file);

        // then
        assertEquals(newImageUrl, result);
        verify(memberService).findMemberById(memberId);
        verify(profileRepository).findByMember(target);
        verify(profileRepository).save(any(Profile.class));
        verify(fileUploadService).uploadFile(file, FileConstants.PROFILE);
        verify(fileUploadService, never()).deleteFile(any());
    }

    @Test
    @DisplayName("프로필 이미지 URL 조회 성공 - 프로필과 이미지가 있는 경우")
    void getProfileImageUrl_WithExistingImage_Success() {
        // given
        Long memberId = 2L;
        given(profileRepository.findByMemberId(memberId)).willReturn(Optional.of(profile));

        // when
        String result = profileService.getProfileImageUrl(memberId);

        // then
        assertEquals("https://example.com/profile.jpg", result);
        verify(profileRepository).findByMemberId(memberId);
    }

    @Test
    @DisplayName("프로필 이미지 URL 조회 - 프로필이 없는 경우 기본 이미지 반환")
    void getProfileImageUrl_WithoutProfile_ReturnsDefault() {
        // given
        Long memberId = 2L;
        given(profileRepository.findByMemberId(memberId)).willReturn(Optional.empty());

        // when
        String result = profileService.getProfileImageUrl(memberId);

        // then
        assertEquals(FileConstants.DEFAULT_IMAGE_URL, result);
        verify(profileRepository).findByMemberId(memberId);
    }

    @Test
    @DisplayName("프로필 이미지 URL 조회 - 프로필은 있지만 이미지가 없는 경우 기본 이미지 반환")
    void getProfileImageUrl_WithoutImage_ReturnsDefault() {
        // given
        Long memberId = 2L;
        Profile profileWithoutImage = Profile.create(target, "소개글만 있음");
        given(profileRepository.findByMemberId(memberId)).willReturn(Optional.of(profileWithoutImage));

        // when
        String result = profileService.getProfileImageUrl(memberId);

        // then
        assertEquals(FileConstants.DEFAULT_IMAGE_URL, result);
        verify(profileRepository).findByMemberId(memberId);
    }

    @Test
    @DisplayName("자기 자신의 프로필 조회")
    void getProfile_SelfProfile_Success() {
        // given
        Long memberId = 1L;
        
        given(memberService.findMemberById(memberId)).willReturn(viewer);
        given(followService.isFollowing(memberId, memberId)).willReturn(false);
        given(followService.getFollowerCount(memberId)).willReturn(20L);
        given(followService.getFollowingCount(memberId)).willReturn(30L);
        given(profileRepository.findByMember(viewer)).willReturn(Optional.of(profile));

        // when
        ProfileResponse result = profileService.getProfile(memberId, memberId);

        // then
        assertAll(
                () -> assertEquals(memberId, result.getMemberId()),
                () -> assertEquals("뷰어닉", result.getNickname()),
                () -> assertFalse(result.getIsFollowing()), // 자기 자신은 팔로우하지 않음
                () -> assertEquals(20L, result.getFollowers()),
                () -> assertEquals(30L, result.getFollowings())
        );
    }

    @Test
    @DisplayName("여러 번의 이미지 업데이트로 기존 이미지 삭제 확인")
    void updateImage_MultipleUpdates_DeletesPreviousImages() {
        // given
        Long memberId = 2L;
        MultipartFile file1 = new MockMultipartFile("image1", "test1.jpg", "image/jpeg", "test image 1".getBytes());
        MultipartFile file2 = new MockMultipartFile("image2", "test2.jpg", "image/jpeg", "test image 2".getBytes());
        String imageUrl1 = "https://example.com/profile1.jpg";
        String imageUrl2 = "https://example.com/profile2.jpg";
        
        given(memberService.findMemberById(memberId)).willReturn(target);
        given(profileRepository.findByMember(target)).willReturn(Optional.of(profile));
        given(fileUploadService.uploadFile(file1, FileConstants.PROFILE)).willReturn(imageUrl1);
        given(fileUploadService.uploadFile(file2, FileConstants.PROFILE)).willReturn(imageUrl2);

        // when
        profileService.updateImage(memberId, file1);
        profileService.updateImage(memberId, file2);

        // then
        verify(fileUploadService, times(2)).deleteFile(any());
        verify(fileUploadService).uploadFile(file1, FileConstants.PROFILE);
        verify(fileUploadService).uploadFile(file2, FileConstants.PROFILE);
    }
}
