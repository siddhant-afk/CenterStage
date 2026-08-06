package com.example.contentservice.dto;

import com.example.contentservice.model.Genre;
import com.example.contentservice.model.VideoStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MovieResponse {


    private String id;


    private String title;


    private String description;


    private Genre genre;

    private String director;

    private String cast;

    private int releaseYear;

    private double rating;

    private String thumbnailUrl;

    private int durationMinutes;


    private String videoKey;

    private String hlsUrl;

    private VideoStatus videoStatus;

    private LocalDateTime createdAt;

}
