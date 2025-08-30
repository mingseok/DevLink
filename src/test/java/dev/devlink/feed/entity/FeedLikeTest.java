package dev.devlink.feed.entity;

import dev.devlink.member.entity.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FeedLikeTest {

    private Member testMember;
    private Feed testFeed;

    @BeforeEach
    void setUp() {
        testMember = Member.builder()
                .name("김민석")
                .email("hong@example.com")
                .nickname("김민석닉네임")
                .password("password")
                .build();

        testFeed = Feed.create(
                testMember,
                "피드 내용",
                "https://example.com/image.jpg"
        );
    }

    @Test
    @DisplayName("피드 좋아요를 생성할 수 있다")
    void create_Success() {
        // when
        FeedLike feedLike = FeedLike.create(testFeed, testMember);

        // then
        assertThat(feedLike.getFeed()).isEqualTo(testFeed);
        assertThat(feedLike.getMember()).isEqualTo(testMember);
    }

    @Test
    @DisplayName("다른 사용자가 피드에 좋아요를 할 수 있다")
    void create_WithDifferentMember_Success() {
        // given
        Member anotherMember = Member.builder()
                .name("김선우")
                .email("kim@example.com")
                .nickname("김선우닉네임")
                .password("password")
                .build();

        // when
        FeedLike feedLike = FeedLike.create(testFeed, anotherMember);

        // then
        assertThat(feedLike.getFeed()).isEqualTo(testFeed);
        assertThat(feedLike.getMember()).isEqualTo(anotherMember);
    }

    @Test
    @DisplayName("피드 좋아요 엔티티의 연관관계가 올바르게 설정된다")
    void relationships_SetCorrectly() {
        // when
        FeedLike feedLike = FeedLike.create(testFeed, testMember);

        // then
        assertThat(feedLike.getFeed().getContent()).isEqualTo("피드 내용");
        assertThat(feedLike.getMember().getNickname()).isEqualTo("김민석닉네임");
    }
}
