package com.rebook.review.service.dto;

import com.rebook.book.domain.BookEntity;
import com.rebook.review.domain.ReviewEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ReviewDto {
    private BookEntity book;
    private Long id;
    private String content;
    private BigDecimal score;
    private ZonedDateTime createdAt;

    public static ReviewDto fromEntity(ReviewEntity reviewEntity) {
        return new ReviewDto(reviewEntity.getBook(),
                reviewEntity.getId(),
                reviewEntity.getContent(),
                reviewEntity.getScore(),
                reviewEntity.getCreatedAt());
    }
}
