package com.rebook.review.service;

import com.rebook.book.domain.BookEntity;
import com.rebook.book.repository.BookRepository;
import com.rebook.common.exception.ExceptionCode;
import com.rebook.common.exception.NotFoundException;
import com.rebook.review.domain.ReviewEntity;
import com.rebook.review.repository.ReviewRepository;
import com.rebook.review.service.command.ReviewSaveCommand;
import com.rebook.review.service.command.ReviewUpdateCommand;
import com.rebook.review.service.dto.ReviewDto;
import com.rebook.user.domain.UserEntity;
import com.rebook.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Transactional
    public ReviewDto save(ReviewSaveCommand reviewCommand, Long userId) {
        BookEntity book = bookRepository.findById(reviewCommand.getBookId())
                .orElseThrow(() -> new NotFoundException(ExceptionCode.NOT_FOUND_BOOK_ID));
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ExceptionCode.NOT_FOUND_USER_ID));
        ReviewEntity reviewEntity = ReviewEntity.of(book, user, reviewCommand.getContent(), reviewCommand.getScore());
        ReviewEntity savedReview = reviewRepository.save(reviewEntity);
        return ReviewDto.fromEntity(savedReview);
    }

    public List<ReviewDto> getReviewsWithBookId(Long bookId) {
        bookRepository.findById(bookId)
                .orElseThrow(() -> new NotFoundException(ExceptionCode.NOT_FOUND_BOOK_ID));
        List<ReviewEntity> reviews = reviewRepository.findByBookIdOrderByCreatedAtAsc(bookId);
        return reviews.stream()
                .map(ReviewDto::fromEntity)
                .toList();
    }

    @Transactional
    public ReviewDto update(ReviewUpdateCommand reviewCommand) {

        // 존재하는 책인지 확인
        BookEntity bookEntity = bookRepository.findById(reviewCommand.getBookId())
                .orElseThrow(() -> new NotFoundException(ExceptionCode.NOT_FOUND_BOOK_ID));

        // 리뷰 id로 리뷰 조회
        ReviewEntity reviewEntity = reviewRepository.findById(reviewCommand.getBookId())
                .orElseThrow(() -> new NotFoundException(ExceptionCode.NOT_FOUND_REVIEW_ID));

        // 기존 리뷰 엔티티 업데이트
        reviewEntity.update(bookEntity, reviewCommand.getContent(), reviewCommand.getScore());

        return ReviewDto.fromEntity(reviewEntity);
    }

    @Transactional
    public void softDelete(Long reviewId) {
        ReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException(ExceptionCode.NOT_FOUND_REVIEW_ID));
        review.softDelete();
    }
}
