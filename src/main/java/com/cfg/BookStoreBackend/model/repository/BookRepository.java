package com.cfg.BookStoreBackend.model.repository;

import com.cfg.BookStoreBackend.model.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Repository to talk to books table in db
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

}
