package com.rebook.book.service;

import com.rebook.book.domain.entity.BookEntity;
import com.rebook.book.dto.response.BookResponse;
import com.rebook.book.dto.request.BookCreateRequest;
import com.rebook.book.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BookService {
    private final BookRepository bookRepository;

    @Transactional
    public BookResponse save(final BookCreateRequest bookCreateRequest) {
        BookEntity book = BookEntity.of(
                bookCreateRequest.getTitle(),
                bookCreateRequest.getAuthor(),
                bookCreateRequest.getThumbnailUrl()
        );

        return BookResponse.of(bookRepository.save(book));
    }

    @Transactional(readOnly = true)
    public List<BookResponse> getBooks() {
        List<BookEntity> books = bookRepository.findAll();

        return books.stream()
                .map(BookResponse::of)
                .toList();
    }
}
