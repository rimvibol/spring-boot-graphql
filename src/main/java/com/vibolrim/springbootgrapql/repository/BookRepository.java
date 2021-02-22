package com.vibolrim.springbootgrapql.repository;

import com.vibolrim.springbootgrapql.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, String> {
}
