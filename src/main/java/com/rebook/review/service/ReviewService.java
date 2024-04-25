package com.rebook.review.service;

import com.rebook.book.domain.entity.BookEntity;
import com.rebook.book.repository.BookRepository;
import com.rebook.review.domain.Review;
import com.rebook.review.domain.ReviewEntity;
import com.rebook.review.dto.ReviewRequest;
import com.rebook.review.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;

    public ReviewService(ReviewRepository reviewRepository, BookRepository bookRepository) {
        this.reviewRepository = reviewRepository;
        this.bookRepository = bookRepository;
    }
    @Transactional
    public Review save(Long bookId,ReviewRequest reviewRequest){
        BookEntity book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("bookId not found: " + bookId));
        ReviewEntity reviewEntity = ReviewEntity.of(book,reviewRequest);
        ReviewEntity savedReview = reviewRepository.save(reviewEntity);
        return Review.from(savedReview);
    }

    public List<Review> getReviewsWithBookId(Long bookId){
        BookEntity book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BadRequestException(ExceptionCode.NOT_FOUND_BOOK_ID));
        List<ReviewEntity> reviews = reviewRepository.findByBookIdOrderByCreatedAtAsc(bookId);
        return reviews.stream()
                .map(Review::from)
                .toList();
    }

    @Transactional
    public Review update(Long bookId, Long reviewId, ReviewRequest reviewRequest) {
        // 존재하는 책인지 확인
        BookEntity bookEntity = bookRepository.findById(bookId)
                .orElseThrow(() -> new BadRequestException(ExceptionCode.NOT_FOUND_BOOK_ID));

        // 리뷰 id로 리뷰 조회
        ReviewEntity reviewEntity = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BadRequestException(ExceptionCode.NOT_FOUND_REVIEW_ID));

        // 기존 리뷰 엔티티 업데이트
        reviewEntity.setContent(reviewRequest.getContent());
        reviewEntity.setStarRate(reviewRequest.getStarRate());

        return Review.from(reviewEntity);
    }

    @Transactional
    public void softDelete(Long reviewId) {
        ReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BadRequestException(ExceptionCode.NOT_FOUND_REVIEW_ID));
        reviewRepository.deleteById(reviewId);
    }
}
