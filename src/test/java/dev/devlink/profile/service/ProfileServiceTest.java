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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @InjectMocks
    private ProfileService profileService;

    @Mock
    private MemberService memberService;

    @Mock
    private FollowService followService;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private FileUploadService fileUploadService;

    @Mock
    private MultipartFile file;

    @Test
    @DisplayName("프로필 조회 - 프로필이 존재하는 경우")
    void getProfile_WhenExists_Success() {
        // given
        Long viewerId = 1L;
        Long targetId = 2L;
        Member target = Member.create("김민석", "test@example.com", "테스트닉네임", "password");
        Profile profile = Profile.create(target, "안녕하세요");
        
        given(memberService.findMemberById(targetId)).willReturn(target);
        given(followService.isFollowing(viewerId, targetId)).willReturn(true);
        given(followService.getFollowerCount(targetId)).willReturn(10L);
        given(followService.getFollowingCount(targetId)).willReturn(5L);
        given(profileRepository.findByMember(target)).willReturn(Optional.of(profile));

        // when
        ProfileResponse response = profileService.getProfile(viewerId, targetId);

        // then
        assertThat(response.getMemberId()).isEqualTo(target.getId());
        assertThat(response.getNickname()).isEqualTo(target.getNickname());
        assertThat(response.getBio()).isEqualTo("안녕하세요");
        assertThat(response.getImageUrl()).isEqualTo(FileConstants.DEFAULT_IMAGE_URL);
        assertThat(response.getIsFollowing()).isTrue();
        assertThat(response.getFollowersCount()).isEqualTo(10L);
        assertThat(response.getFollowingsCount()).isEqualTo(5L);
    }

    @Test
    @DisplayName("프로필 조회 - 프로필이 존재하지 않는 경우")
    void getProfile_WhenNotExists_Success() {
        // given
        Long viewerId = 1L;
        Long targetId = 2L;
        Member target = Member.create("김선우", "test2@example.com", "테스트닉네임2", "password");
        
        given(memberService.findMemberById(targetId)).willReturn(target);
        given(followService.isFollowing(viewerId, targetId)).willReturn(false);
        given(followService.getFollowerCount(targetId)).willReturn(0L);
        given(followService.getFollowingCount(targetId)).willReturn(0L);
        given(profileRepository.findByMember(target)).willReturn(Optional.empty());

        // when
        ProfileResponse response = profileService.getProfile(viewerId, targetId);

        // then
        assertThat(response.getMemberId()).isEqualTo(target.getId());
        assertThat(response.getNickname()).isEqualTo(target.getNickname());
        assertThat(response.getBio()).isEmpty();
        assertThat(response.getImageUrl()).isEqualTo(FileConstants.DEFAULT_IMAGE_URL);
        assertThat(response.getIsFollowing()).isFalse();
        assertThat(response.getFollowersCount()).isZero();
        assertThat(response.getFollowingsCount()).isZero();
    }

    @Test
    @DisplayName("소개글 수정 - 기존 프로필이 있는 경우")
    void updateBio_WhenProfileExists_Success() {
        // given
        Long memberId = 1L;
        Member member = Member.create("김현우", "test3@example.com", "테스트닉네임3", "password");
        Profile existingProfile = Profile.create(member, "기존 소개글");
        ProfileUpdateRequest request = new ProfileUpdateRequest();
        
        given(memberService.findMemberById(memberId)).willReturn(member);
        given(profileRepository.findByMember(member)).willReturn(Optional.of(existingProfile));

        // when
        profileService.updateBio(memberId, request);

        // then
        then(profileRepository).should(never()).save(any(Profile.class));
    }

    @Test
    @DisplayName("소개글 수정 - 기존 프로필이 없는 경우")
    void updateBio_WhenProfileNotExists_Success() {
        // given
        Long memberId = 2L;
        Member member = Member.create("이영희", "test4@example.com", "테스트닉네임4", "password");
        Profile newProfile = Profile.create(member, ProfileDefaults.DEFAULT_BIO);
        ProfileUpdateRequest request = new ProfileUpdateRequest();
        
        given(memberService.findMemberById(memberId)).willReturn(member);
        given(profileRepository.findByMember(member)).willReturn(Optional.empty());
        given(profileRepository.save(any(Profile.class))).willReturn(newProfile);

        // when
        profileService.updateBio(memberId, request);

        // then
        then(profileRepository).should().save(any(Profile.class));
    }

    @Test
    @DisplayName("프로필 이미지 수정 - 기존 이미지가 있는 경우")
    void updateImage_WhenExistingImage_Success() {
        // given
        Long memberId = 3L;
        Member member = Member.create("박철수", "test5@example.com", "테스트닉네임5", "password");
        Profile profile = Profile.builder()
                .member(member)
                .bio("소개글")
                .imageUrl("https://example.com/old-image.jpg")
                .build();
        String newImageUrl = "https://example.com/new-image.jpg";
        
        given(memberService.findMemberById(memberId)).willReturn(member);
        given(profileRepository.findByMember(member)).willReturn(Optional.of(profile));
        given(fileUploadService.uploadFile(file, FileConstants.PROFILE)).willReturn(newImageUrl);

        // when
        String result = profileService.updateImage(memberId, file);

        // then
        assertThat(result).isEqualTo(newImageUrl);
        then(fileUploadService).should().deleteFile("https://example.com/old-image.jpg");
        then(fileUploadService).should().uploadFile(file, FileConstants.PROFILE);
    }

    @Test
    @DisplayName("프로필 이미지 수정 - 이미지 URL이 null인 경우")
    void updateImage_WhenImageUrlIsNull_Success() {
        // given
        Long memberId = 4L;
        Member member = Member.create("김영수", "test6@example.com", "테스트닉네임6", "password");
        Profile profile = Profile.builder()
                .member(member)
                .bio("소개글")
                .imageUrl(null)
                .build();
        String newImageUrl = "https://example.com/new-image.jpg";
        
        given(memberService.findMemberById(memberId)).willReturn(member);
        given(profileRepository.findByMember(member)).willReturn(Optional.of(profile));
        given(fileUploadService.uploadFile(file, FileConstants.PROFILE)).willReturn(newImageUrl);

        // when
        String result = profileService.updateImage(memberId, file);

        // then
        assertThat(result).isEqualTo(newImageUrl);
        then(fileUploadService).should(never()).deleteFile(any());
        then(fileUploadService).should().uploadFile(file, FileConstants.PROFILE);
    }

    @Test
    @DisplayName("프로필 이미지 수정 - 프로필이 존재하지 않는 경우")
    void updateImage_WhenProfileNotExists_Success() {
        // given
        Long memberId = 5L;
        Member member = Member.create("박영희", "test7@example.com", "테스트닉네임7", "password");
        Profile newProfile = Profile.create(member, ProfileDefaults.DEFAULT_BIO);
        String newImageUrl = "https://example.com/new-image.jpg";
        
        given(memberService.findMemberById(memberId)).willReturn(member);
        given(profileRepository.findByMember(member)).willReturn(Optional.empty());
        given(profileRepository.save(any(Profile.class))).willReturn(newProfile);
        given(fileUploadService.uploadFile(file, FileConstants.PROFILE)).willReturn(newImageUrl);

        // when
        String result = profileService.updateImage(memberId, file);

        // then
        assertThat(result).isEqualTo(newImageUrl);
        then(profileRepository).should().save(any(Profile.class));
        then(fileUploadService).should().deleteFile(FileConstants.DEFAULT_IMAGE_URL);
        then(fileUploadService).should().uploadFile(file, FileConstants.PROFILE);
    }

    @Test
    @DisplayName("프로필 이미지 URL 조회 - 프로필이 존재하는 경우")
    void getProfileImageUrl_WhenProfileExists_Success() {
        // given
        Long memberId = 6L;
        String imageUrl = "https://example.com/image.jpg";
        Profile profile = Profile.builder()
                .member(Member.create("테스트", "test@example.com", "닉네임", "password"))
                .bio("소개글")
                .imageUrl(imageUrl)
                .build();
        
        given(profileRepository.findByMemberId(memberId)).willReturn(Optional.of(profile));

        // when
        String result = profileService.getProfileImageUrl(memberId);

        // then
        assertThat(result).isEqualTo(imageUrl);
    }

    @Test
    @DisplayName("프로필 이미지 URL 조회 - 프로필이 존재하지 않는 경우")
    void getProfileImageUrl_WhenProfileNotExists_ReturnDefault() {
        // given
        Long memberId = 7L;
        
        given(profileRepository.findByMemberId(memberId)).willReturn(Optional.empty());

        // when
        String result = profileService.getProfileImageUrl(memberId);

        // then
        assertThat(result).isEqualTo(FileConstants.DEFAULT_IMAGE_URL);
    }

    @Test
    @DisplayName("프로필 이미지 URL 조회 - 이미지 URL이 null인 경우")
    void getProfileImageUrl_WhenImageUrlIsNull_ReturnDefault() {
        // given
        Long memberId = 8L;
        Profile profile = Profile.builder()
                .member(Member.create("테스트", "test@example.com", "닉네임", "password"))
                .bio("소개글")
                .imageUrl(null)
                .build();
        
        given(profileRepository.findByMemberId(memberId)).willReturn(Optional.of(profile));

        // when
        String result = profileService.getProfileImageUrl(memberId);

        // then
        assertThat(result).isEqualTo(FileConstants.DEFAULT_IMAGE_URL);
    }
}
