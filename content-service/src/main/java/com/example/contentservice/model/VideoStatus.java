package com.example.contentservice.model;

/*
* Tracks the video processing lifecycle
* FLOW:
*   PENDING -> UPLOADED -> ENCODING -> ENCODED -> READY
*                                   -> FAILED
 */
public enum VideoStatus {

    PENDING, // movie added but not yet uploaded.
    UPLOADED, // raw video uploaded to s3
    ENCODING, // FFmpeg is encoding the video
    ENCODED, // Encoding complete
    READY, // HLS playlist ready - can be streamed
    FAILED // Encoding failed
}
