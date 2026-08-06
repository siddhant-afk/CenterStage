package com.example.contentservice.repository;
import com.example.contentservice.model.Genre;
import com.example.contentservice.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContentRepository extends JpaRepository<Movie, String> {

    List<Movie> findByGenre(Genre genre);
    List<Movie> findByTitle(String title);
}
