package com.rebook.review.controller.request;

import com.rebook.review.service.command.ReviewSaveCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ReviewSaveRequest {

    @NotBlank(message = "리뷰 내용을 입력해주세요.")
    private String content;

    @NotNull(message = "별점을 등록해주세요.")
    private BigDecimal score;

    public ReviewSaveCommand toCommand(Long reviewId, ReviewSaveRequest reviewRequest) {
        return new ReviewSaveCommand(
                reviewId,
                reviewRequest.getContent(),
                reviewRequest.getScore());
    }
}
