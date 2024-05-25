package com.rebook.review.service;

import com.rebook.book.domain.BookEntity;
import com.rebook.book.repository.BookRepository;
import com.rebook.review.domain.ReviewEntity;
import com.rebook.review.repository.ReviewRepository;
import com.rebook.review.service.dto.ReviewDto;
import com.rebook.user.domain.Role;
import com.rebook.user.domain.UserEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {
    @Mock
    private BookRepository bookRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewService reviewService;

    @DisplayName("bookId로 리뷰 목록 조회")
    @Test
    void getReviewsWithBookId() {


        //given
        // 책 객체
        BookEntity bookEntity = new BookEntity(1L, "오브젝트", "조영호", "test-image", Collections.emptyList(), Collections.emptyList());
        // user 객체
        UserEntity user = UserEntity.builder()
                .id(1L)
                .role(Role.USER)
                .email("email@email.com")
                .nickname("조영호")
                .socialId("39184802")
                .build();


        // 리뷰 객체
        List<ReviewEntity> reviews = new ArrayList<>();
        ReviewEntity reviewEntity = new ReviewEntity(1L, bookEntity, user, "first content", BigDecimal.valueOf(4.5));
        ReviewEntity reviewEntity2 = new ReviewEntity(2L, bookEntity, user, "second content", BigDecimal.valueOf(3.5));

        reviews.add(reviewEntity);
        reviews.add(reviewEntity2);


        //bookId로 책 조회시 bookEntity 반환
        when(bookRepository.findById(bookEntity.getId())).thenReturn(Optional.of(bookEntity));
        //bookId로 리뷰 목록 조회시 reviews 반환
        when(reviewRepository.findByBookIdOrderByCreatedAtAsc(bookEntity.getId())).thenReturn(reviews);

        // When
        List<ReviewDto> result = reviewService.getReviewsWithBookId(bookEntity.getId());

        // Then
        assertEquals(2, result.size());
        assertEquals("first content", result.get(0).getContent());
        assertEquals(BigDecimal.valueOf(4.5), result.get(0).getScore());
        assertEquals("second content", result.get(1).getContent());
        assertEquals(BigDecimal.valueOf(3.5), result.get(1).getScore());

    }
}