package dev.devlink.member.service;

import dev.devlink.common.jwt.JwtToken;
import dev.devlink.common.jwt.JwtTokenProvider;
import dev.devlink.member.entity.Member;
import dev.devlink.member.exception.MemberError;
import dev.devlink.member.exception.MemberException;
import dev.devlink.member.repository.MemberRepository;
import dev.devlink.member.service.dto.request.SignInRequest;
import dev.devlink.member.service.dto.request.SignUpRequest;
import dev.devlink.member.service.dto.response.JwtTokenResponse;
import dev.devlink.member.service.dto.response.NicknameResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MemberService 통합 테스트")
class MemberServiceIntegrationTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MemberService memberService;

    private Member testMember;
    private SignUpRequest signUpRequest;
    private SignInRequest signInRequest;

    @BeforeEach
    void setUp() {
        testMember = Member.create("테스트유저", "test@example.com", "테스트닉네임", "encodedPassword");
        ReflectionTestUtils.setField(testMember, "id", 1L);

        signUpRequest = new SignUpRequest("새유저", "new@example.com", "새닉네임", "password123");
        signInRequest = new SignInRequest("test@example.com", "password123");
    }

    @Test
    @DisplayName("회원가입 성공")
    void signUp_Success() {
        // given
        given(memberRepository.existsByEmail(signUpRequest.getEmail())).willReturn(false);
        given(memberRepository.existsByNickname(signUpRequest.getNickname())).willReturn(false);
        given(passwordEncoder.encode(signUpRequest.getPassword())).willReturn("encodedPassword");
        given(memberRepository.save(any(Member.class))).willReturn(testMember);

        // when
        assertDoesNotThrow(() -> memberService.signUp(signUpRequest));

        // then
        verify(memberRepository).existsByEmail(signUpRequest.getEmail());
        verify(memberRepository).existsByNickname(signUpRequest.getNickname());
        verify(passwordEncoder).encode(signUpRequest.getPassword());
        verify(memberRepository).save(any(Member.class));
    }

    @Test
    @DisplayName("이메일 중복으로 회원가입 실패")
    void signUp_EmailDuplicated_ThrowsException() {
        // given
        given(memberRepository.existsByEmail(signUpRequest.getEmail())).willReturn(true);

        // when & then
        MemberException exception = assertThrows(MemberException.class, () ->
                memberService.signUp(signUpRequest));

        assertEquals(MemberError.EMAIL_DUPLICATED, exception.getCommonError());
        verify(memberRepository).existsByEmail(signUpRequest.getEmail());
        verify(memberRepository, never()).existsByNickname(anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    @DisplayName("로그인 성공")
    void signin_Success() {
        // given
        JwtToken jwtToken = new JwtToken("access-token", "refresh-token");
        given(memberRepository.findByEmail(signInRequest.getEmail())).willReturn(Optional.of(testMember));
        given(passwordEncoder.matches(signInRequest.getPassword(), testMember.getPassword())).willReturn(true);
        given(jwtTokenProvider.generateToken(testMember.getId())).willReturn(jwtToken);

        // when
        JwtTokenResponse result = memberService.signin(signInRequest);

        // then
        assertNotNull(result);
        assertEquals(jwtToken.getAccessToken(), result.getAccessToken());
        assertEquals(jwtToken.getRefreshToken(), result.getRefreshToken());
        verify(memberRepository).findByEmail(signInRequest.getEmail());
        verify(passwordEncoder).matches(signInRequest.getPassword(), testMember.getPassword());
        verify(jwtTokenProvider).generateToken(testMember.getId());
    }

    @Test
    @DisplayName("회원 ID로 회원 조회 성공")
    void findMemberById_Success() {
        // given
        Long memberId = 1L;
        given(memberRepository.findById(memberId)).willReturn(Optional.of(testMember));

        // when
        Member result = memberService.findMemberById(memberId);

        // then
        assertEquals(testMember, result);
        assertEquals(testMember.getId(), result.getId());
        assertEquals(testMember.getEmail(), result.getEmail());
        verify(memberRepository).findById(memberId);
    }

    @Test
    @DisplayName("존재하지 않는 회원 ID로 회원 조회 실패")
    void findMemberById_MemberNotFound_ThrowsException() {
        // given
        Long memberId = 999L;
        given(memberRepository.findById(memberId)).willReturn(Optional.empty());

        // when & then
        MemberException exception = assertThrows(MemberException.class, () ->
                memberService.findMemberById(memberId));

        assertEquals(MemberError.MEMBER_NOT_FOUND, exception.getCommonError());
        verify(memberRepository).findById(memberId);
    }
}
