package dev.devlink.profile.repository;

import dev.devlink.member.entity.Member;
import dev.devlink.profile.entity.Profile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProfileRepositoryTest {

    @Mock
    private ProfileRepository profileRepository;

    private Member member;
    private Profile profile;

    @BeforeEach
    void setUp() {
        member = Member.create("김민석", "test@example.com", "테스트닉네임", "password");
        ReflectionTestUtils.setField(member, "id", 1L);
        
        profile = Profile.create(member, "안녕하세요");
        ReflectionTestUtils.setField(profile, "id", 1L);
    }

    @Test
    @DisplayName("회원으로 프로필을 조회할 수 있다")
    void findByMember_Success() {
        // given
        given(profileRepository.findByMember(member)).willReturn(Optional.of(profile));

        // when
        Optional<Profile> result = profileRepository.findByMember(member);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(profile.getId());
        assertThat(result.get().getMember().getId()).isEqualTo(member.getId());
        assertThat(result.get().getBio()).isEqualTo("안녕하세요");
        verify(profileRepository).findByMember(member);
    }

    @Test
    @DisplayName("회원 ID로 프로필을 조회할 수 있다")
    void findByMemberId_Success() {
        // given
        given(profileRepository.findByMemberId(member.getId())).willReturn(Optional.of(profile));

        // when
        Optional<Profile> result = profileRepository.findByMemberId(member.getId());

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(profile.getId());
        assertThat(result.get().getMember().getId()).isEqualTo(member.getId());
        assertThat(result.get().getBio()).isEqualTo("안녕하세요");
        verify(profileRepository).findByMemberId(member.getId());
    }

    @Test
    @DisplayName("존재하지 않는 회원으로 프로필 조회시 빈 결과를 반환한다")
    void findByMember_WhenNotExists_ReturnEmpty() {
        // given
        Member nonExistentMember = Member.create("김선우", "test2@example.com", "테스트닉네임2", "password");
        given(profileRepository.findByMember(nonExistentMember)).willReturn(Optional.empty());

        // when
        Optional<Profile> result = profileRepository.findByMember(nonExistentMember);

        // then
        assertThat(result).isEmpty();
        verify(profileRepository).findByMember(nonExistentMember);
    }

    @Test
    @DisplayName("존재하지 않는 회원 ID로 프로필 조회시 빈 결과를 반환한다")
    void findByMemberId_WhenNotExists_ReturnEmpty() {
        // given
        Long nonExistentMemberId = 999L;
        given(profileRepository.findByMemberId(nonExistentMemberId)).willReturn(Optional.empty());

        // when
        Optional<Profile> result = profileRepository.findByMemberId(nonExistentMemberId);

        // then
        assertThat(result).isEmpty();
        verify(profileRepository).findByMemberId(nonExistentMemberId);
    }

    @Test
    @DisplayName("프로필을 저장할 수 있다")
    void save_Success() {
        // given
        Member newMember = Member.create("김현우", "test3@example.com", "테스트닉네임3", "password");
        ReflectionTestUtils.setField(newMember, "id", 2L);
        
        Profile newProfile = Profile.create(newMember, "새로운 소개글");
        Profile savedProfile = Profile.create(newMember, "새로운 소개글");
        ReflectionTestUtils.setField(savedProfile, "id", 2L);
        
        given(profileRepository.save(newProfile)).willReturn(savedProfile);

        // when
        Profile result = profileRepository.save(newProfile);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getBio()).isEqualTo("새로운 소개글");
        verify(profileRepository).save(newProfile);
    }

    @Test
    @DisplayName("회원별로 유니크한 프로필을 조회할 수 있다")
    void findByMember_UniquePerMember() {
        // given
        Member member1 = Member.create("김민석", "test1@example.com", "닉네임1", "password");
        Member member2 = Member.create("김선우", "test2@example.com", "닉네임2", "password");
        ReflectionTestUtils.setField(member1, "id", 1L);
        ReflectionTestUtils.setField(member2, "id", 2L);
        
        Profile profile1 = Profile.create(member1, "첫 번째 소개글");
        Profile profile2 = Profile.create(member2, "두 번째 소개글");
        ReflectionTestUtils.setField(profile1, "id", 1L);
        ReflectionTestUtils.setField(profile2, "id", 2L);
        
        given(profileRepository.findByMember(member1)).willReturn(Optional.of(profile1));
        given(profileRepository.findByMember(member2)).willReturn(Optional.of(profile2));

        // when
        Optional<Profile> result1 = profileRepository.findByMember(member1);
        Optional<Profile> result2 = profileRepository.findByMember(member2);

        // then
        assertThat(result1).isPresent();
        assertThat(result1.get().getBio()).isEqualTo("첫 번째 소개글");
        
        assertThat(result2).isPresent();
        assertThat(result2.get().getBio()).isEqualTo("두 번째 소개글");
        
        verify(profileRepository).findByMember(member1);
        verify(profileRepository).findByMember(member2);
    }
}
