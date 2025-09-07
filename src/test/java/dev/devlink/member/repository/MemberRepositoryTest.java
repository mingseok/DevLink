package dev.devlink.member.repository;

import dev.devlink.member.entity.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("MemberRepository 테스트")
class MemberRepositoryTest {

    @Mock
    private MemberRepository memberRepository;

    private Member testMember;

    @BeforeEach
    void setUp() {
        testMember = Member.create("테스트유저", "test@example.com", "테스트닉네임", "password123");
        ReflectionTestUtils.setField(testMember, "id", 1L);
    }

    @Test
    @DisplayName("이메일로 회원 존재 여부 확인 - 존재하는 경우")
    void existsByEmail_WhenExists_ReturnsTrue() {
        // given
        String email = "test@example.com";
        given(memberRepository.existsByEmail(email)).willReturn(true);

        // when
        boolean result = memberRepository.existsByEmail(email);

        // then
        assertTrue(result);
        verify(memberRepository).existsByEmail(email);
    }

    @Test
    @DisplayName("이메일로 회원 존재 여부 확인 - 존재하지 않는 경우")
    void existsByEmail_WhenNotExists_ReturnsFalse() {
        // given
        String email = "nonexistent@example.com";
        given(memberRepository.existsByEmail(email)).willReturn(false);

        // when
        boolean result = memberRepository.existsByEmail(email);

        // then
        assertFalse(result);
        verify(memberRepository).existsByEmail(email);
    }

    @Test
    @DisplayName("닉네임으로 회원 존재 여부 확인 - 존재하는 경우")
    void existsByNickname_WhenExists_ReturnsTrue() {
        // given
        String nickname = "테스트닉네임";
        given(memberRepository.existsByNickname(nickname)).willReturn(true);

        // when
        boolean result = memberRepository.existsByNickname(nickname);

        // then
        assertTrue(result);
        verify(memberRepository).existsByNickname(nickname);
    }

    @Test
    @DisplayName("닉네임으로 회원 존재 여부 확인 - 존재하지 않는 경우")
    void existsByNickname_WhenNotExists_ReturnsFalse() {
        // given
        String nickname = "존재하지않는닉네임";
        given(memberRepository.existsByNickname(nickname)).willReturn(false);

        // when
        boolean result = memberRepository.existsByNickname(nickname);

        // then
        assertFalse(result);
        verify(memberRepository).existsByNickname(nickname);
    }

    @Test
    @DisplayName("이메일로 회원 조회 - 존재하는 경우")
    void findByEmail_WhenExists_ReturnsMember() {
        // given
        String email = "test@example.com";
        given(memberRepository.findByEmail(email)).willReturn(Optional.of(testMember));

        // when
        Optional<Member> result = memberRepository.findByEmail(email);

        // then
        assertTrue(result.isPresent());
        assertEquals(testMember.getEmail(), result.get().getEmail());
        assertEquals(testMember.getNickname(), result.get().getNickname());
        verify(memberRepository).findByEmail(email);
    }

    @Test
    @DisplayName("이메일로 회원 조회 - 존재하지 않는 경우")
    void findByEmail_WhenNotExists_ReturnsEmpty() {
        // given
        String email = "nonexistent@example.com";
        given(memberRepository.findByEmail(email)).willReturn(Optional.empty());

        // when
        Optional<Member> result = memberRepository.findByEmail(email);

        // then
        assertTrue(result.isEmpty());
        verify(memberRepository).findByEmail(email);
    }

    @Test
    @DisplayName("ID로 닉네임 조회 - 존재하는 경우")
    void findNicknameById_WhenExists_ReturnsNickname() {
        // given
        Long memberId = 1L;
        String expectedNickname = "테스트닉네임";
        given(memberRepository.findNicknameById(memberId)).willReturn(Optional.of(expectedNickname));

        // when
        Optional<String> result = memberRepository.findNicknameById(memberId);

        // then
        assertTrue(result.isPresent());
        assertEquals(expectedNickname, result.get());
        verify(memberRepository).findNicknameById(memberId);
    }

    @Test
    @DisplayName("ID로 닉네임 조회 - 존재하지 않는 경우")
    void findNicknameById_WhenNotExists_ReturnsEmpty() {
        // given
        Long memberId = 999L;
        given(memberRepository.findNicknameById(memberId)).willReturn(Optional.empty());

        // when
        Optional<String> result = memberRepository.findNicknameById(memberId);

        // then
        assertTrue(result.isEmpty());
        verify(memberRepository).findNicknameById(memberId);
    }

    @Test
    @DisplayName("회원 저장 성공")
    void save_Success() {
        // given
        Member newMember = Member.create("새유저", "new@example.com", "새닉네임", "password");
        given(memberRepository.save(newMember)).willReturn(testMember);

        // when
        Member result = memberRepository.save(newMember);

        // then
        assertNotNull(result);
        assertEquals(testMember.getId(), result.getId());
        verify(memberRepository).save(newMember);
    }

    @Test
    @DisplayName("회원 ID로 조회 성공")
    void findById_Success() {
        // given
        Long memberId = 1L;
        given(memberRepository.findById(memberId)).willReturn(Optional.of(testMember));

        // when
        Optional<Member> result = memberRepository.findById(memberId);

        // then
        assertTrue(result.isPresent());
        assertEquals(testMember.getId(), result.get().getId());
        verify(memberRepository).findById(memberId);
    }

    @Test
    @DisplayName("회원 ID로 조회 - 존재하지 않는 경우")
    void findById_WhenNotExists_ReturnsEmpty() {
        // given
        Long memberId = 999L;
        given(memberRepository.findById(memberId)).willReturn(Optional.empty());

        // when
        Optional<Member> result = memberRepository.findById(memberId);

        // then
        assertTrue(result.isEmpty());
        verify(memberRepository).findById(memberId);
    }

    @Test
    @DisplayName("회원 삭제 성공")
    void delete_Success() {
        // given & when
        memberRepository.delete(testMember);

        // then
        verify(memberRepository).delete(testMember);
    }

    @Test
    @DisplayName("회원 개수 조회")
    void count_Success() {
        // given
        given(memberRepository.count()).willReturn(10L);

        // when
        long result = memberRepository.count();

        // then
        assertEquals(10L, result);
        verify(memberRepository).count();
    }

    @Test
    @DisplayName("회원 존재 여부 확인")
    void existsById_Success() {
        // given
        Long memberId = 1L;
        given(memberRepository.existsById(memberId)).willReturn(true);

        // when
        boolean result = memberRepository.existsById(memberId);

        // then
        assertTrue(result);
        verify(memberRepository).existsById(memberId);
    }

    @Test
    @DisplayName("빈 이메일로 조회시 빈 결과 반환")
    void findByEmail_WithEmptyString_ReturnsEmpty() {
        // given
        String emptyEmail = "";
        given(memberRepository.findByEmail(emptyEmail)).willReturn(Optional.empty());

        // when
        Optional<Member> result = memberRepository.findByEmail(emptyEmail);

        // then
        assertTrue(result.isEmpty());
        verify(memberRepository).findByEmail(emptyEmail);
    }

    @Test
    @DisplayName("특수문자가 포함된 이메일로 회원 존재 여부 확인")
    void existsByEmail_WithSpecialCharacters_Success() {
        // given
        String specialEmail = "test+special@example.com";
        given(memberRepository.existsByEmail(specialEmail)).willReturn(true);

        // when
        boolean result = memberRepository.existsByEmail(specialEmail);

        // then
        assertTrue(result);
        verify(memberRepository).existsByEmail(specialEmail);
    }

    @Test
    @DisplayName("긴 닉네임으로 회원 존재 여부 확인")
    void existsByNickname_WithLongNickname_Success() {
        // given
        String longNickname = "매우긴닉네임".repeat(5);
        given(memberRepository.existsByNickname(longNickname)).willReturn(false);

        // when
        boolean result = memberRepository.existsByNickname(longNickname);

        // then
        assertFalse(result);
        verify(memberRepository).existsByNickname(longNickname);
    }
}
