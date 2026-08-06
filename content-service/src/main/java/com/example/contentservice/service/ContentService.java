package com.example.contentservice.service;


import com.example.contentservice.dto.MovieRequest;
import com.example.contentservice.dto.MovieResponse;
import com.example.contentservice.model.Genre;
import com.example.contentservice.model.Movie;
import com.example.contentservice.model.VideoStatus;
import com.example.contentservice.repository.ContentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ContentService {

    private final ContentRepository contentRepository;

    /**
     *  Add a Movie to the catalog
     *  Video Status will be PENDING after this
     */

    public MovieResponse addMovie(MovieRequest request){

        log.info("Adding new movie : {}",request.getTitle());

        Movie movie = Movie.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .genre(request.getGenre())
                .director(request.getDirector())
                .cast(request.getCast())
                .releaseYear(request.getReleaseYear())
                .rating(request.getRating())
                .thumbnailUrl(request.getThumbnailUrl())
                .durationMinutes(request.getDurationMinutes())
                .videoStatus(VideoStatus.PENDING)
                .build();

        Movie savedMovie = contentRepository.save(movie);

        return mapToResponse(savedMovie);
    }

    /**
     * Get all movies in the catalog
     */

    public List<MovieResponse> getAllMovies(){

        return contentRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

    }

    /**
     * Get movie by ID
     */

    public MovieResponse getMovieById(String movieId){

        Movie movie = contentRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Movie not found : "+movieId));

        return mapToResponse(movie);
    }

    /**
     *  Get movies by Genre
     */

    public List<MovieResponse> getMoviesByGenre(Genre genre){

        return contentRepository.findByGenre(genre)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     *  Search movies
     */
    public List<MovieResponse> searchMovies(String title){

        return contentRepository.findByTitle(title)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public void updateVideoKey(String movieId, String videoKey){

        log.info("Updating video key for movie : {} ", movieId);
        Movie movie = contentRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Movie not found : "+ movieId));


        movie.setVideoKey(videoKey);
        movie.setVideoStatus(VideoStatus.UPLOADED);

        contentRepository.save(movie);

    }

    public void updateHlsUrl(String movieId, String hlsUrl){
        log.info("Updating HLS URL for movie : {}",movieId);

        Movie movie = contentRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Movie not found : "+ movieId));

        movie.setHlsUrl(hlsUrl);
        movie.setVideoStatus(VideoStatus.READY);

        contentRepository.save(movie);

        log.info("Movie {} is now ready for streaming.",movieId);

    }


    private MovieResponse mapToResponse(Movie movie){
        return MovieResponse.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .description(movie.getDescription())
                .genre(movie.getGenre())
                .director(movie.getDirector())
                .cast(movie.getCast())
                .releaseYear(movie.getReleaseYear())
                .rating(movie.getRating())
                .thumbnailUrl(movie.getThumbnailUrl())
                .durationMinutes(movie.getDurationMinutes())
                .videoStatus(movie.getVideoStatus())
                .createdAt(movie.getCreatedAt())
                .hlsUrl(movie.getHlsUrl())
                .videoKey(movie.getVideoKey())
                .build();
    }

}
