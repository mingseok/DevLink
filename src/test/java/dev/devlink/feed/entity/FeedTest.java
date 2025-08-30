package dev.devlink.feed.entity;

import dev.devlink.member.entity.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FeedTest {

    private Member testMember;

    @BeforeEach
    void setUp() {
        testMember = Member.builder()
                .name("김민석")
                .email("hong@example.com")
                .nickname("김민석닉네임")
                .password("password")
                .build();
    }

    @Test
    @DisplayName("피드를 생성할 수 있다")
    void create_Success() {
        // when
        Feed feed = Feed.create(
                testMember,
                "피드 내용입니다.",
                "https://example.com/image.jpg"
        );

        // then
        assertThat(feed.getMember()).isEqualTo(testMember);
        assertThat(feed.getContent()).isEqualTo("피드 내용입니다.");
        assertThat(feed.getImageUrl()).isEqualTo("https://example.com/image.jpg");
    }

    @Test
    @DisplayName("이미지 없이 피드를 생성할 수 있다")
    void create_WithoutImage_Success() {
        // when
        Feed feed = Feed.create(
                testMember,
                "텍스트만 있는 피드입니다.",
                null
        );

        // then
        assertThat(feed.getMember()).isEqualTo(testMember);
        assertThat(feed.getContent()).isEqualTo("텍스트만 있는 피드입니다.");
        assertThat(feed.getImageUrl()).isNull();
    }

    @Test
    @DisplayName("작성자 닉네임을 반환한다")
    void getWriterNickname_ReturnsCorrectNickname() {
        // given
        Feed feed = Feed.create(testMember, "내용", null);

        // when
        String writerNickname = feed.getWriterNickname();

        // then
        assertThat(writerNickname).isEqualTo("김민석닉네임");
    }

    @Test
    @DisplayName("memberId가 null인 경우 false를 반환한다")
    void isAuthor_WithNullId_ReturnsFalse() {
        // given
        Feed feed = Feed.create(testMember, "내용", null);

        // when
        boolean isAuthor = feed.isAuthor(null);

        // then
        assertThat(isAuthor).isFalse();
    }

    @Test
    @DisplayName("Member와 연관관계가 올바르게 설정된다")
    void memberRelationship() {
        // given
        Feed feed = Feed.create(testMember, "내용", null);

        // when & then
        assertThat(feed.getMember()).isEqualTo(testMember);
        assertThat(feed.getWriterNickname()).isEqualTo(testMember.getNickname());
    }
}
