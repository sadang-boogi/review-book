package com.rebook.review.service;

import com.rebook.book.domain.BookEntity;
import com.rebook.book.repository.BookRepository;
import com.rebook.common.exception.NotFoundException;
import com.rebook.review.domain.ReviewEntity;
import com.rebook.review.repository.ReviewRepository;
import com.rebook.review.service.command.ReviewSaveCommand;
import com.rebook.review.service.command.ReviewUpdateCommand;
import com.rebook.review.service.dto.ReviewDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.rebook.common.exception.ExceptionCode.NOT_FOUND_BOOK_ID;

@RequiredArgsConstructor
@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;

    @Transactional
    public ReviewDto save(ReviewSaveCommand reviewCommand, Long userId) {
        BookEntity book = bookRepository.findById(reviewCommand.getBookId())
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_BOOK_ID));
        ReviewEntity reviewEntity = ReviewEntity.of(book, userId, reviewCommand.getContent(), reviewCommand.getScore());
        ReviewEntity savedReview = reviewRepository.save(reviewEntity);
        return ReviewDto.fromEntity(savedReview);
    }

    public List<ReviewDto> getReviewsWithBookId(Long bookId) {
        bookRepository.findById(bookId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_BOOK_ID));
        List<ReviewEntity> reviews = reviewRepository.findByBookIdOrderByCreatedAtAsc(bookId);
        return reviews.stream()
                .map(ReviewDto::fromEntity)
                .toList();
    }

    @Transactional
    public ReviewDto update(ReviewUpdateCommand reviewCommand, Long userId) {

        // 존재하는 책인지 확인
        BookEntity bookEntity = bookRepository.findById(reviewCommand.getBookId())
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_BOOK_ID));

        // 리뷰 id로 리뷰 조회
        ReviewEntity review = reviewRepository.findById(reviewCommand.getReviewId())
                .orElseThrow(() -> new NotFoundException("리뷰 수정에 실패했습니다.", "리뷰를 찾을 수 없습니다."));

        if (!review.getUserId().equals(userId)) {
            throw new NotFoundException("권한이 없습니다.", "리뷰를 수정할 권한이 없습니다.");
        }

        // 기존 리뷰 엔티티 업데이트
        review.update(bookEntity, reviewCommand.getContent(), reviewCommand.getScore());

        return ReviewDto.fromEntity(review);
    }

    @Transactional
    public void softDelete(Long reviewId, Long userId) {
        ReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("리뷰 삭제에 실패했습니다.", "리뷰를 찾을 수 없습니다."));
        if (!review.getUserId().equals(userId)) {
            throw new NotFoundException("권한이 없습니다.", "리뷰를 삭제할 권한이 없습니다.");
        }
        review.softDelete();
    }
}
