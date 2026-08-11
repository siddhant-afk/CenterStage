package com.example.streamingservice.event;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 *
 * Consumed from kafka topic : video.encoded
 * Published by Encodinf service after FFmpeg processing.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoEncodedEvent {

    private String movieId;
    private String hlsUrl;
    private String masterPlaylistKey;
    private boolean success;
    private String errorMessage;

}
