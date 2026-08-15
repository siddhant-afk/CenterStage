# CenterStage

A microservices-based video streaming platform (Netflix-style) built with Spring Boot, Kafka, FFmpeg, AWS S3, PostgreSQL, and Redis. It handles the full lifecycle of a video — catalog metadata, upload, adaptive-bitrate encoding, and HLS playback via presigned URLs.

## Architecture

Requests come in through an **API Gateway**, which routes to three core services. Video processing is decoupled via **Kafka** so uploads don't block on encoding.

![High-level design diagram of CenterStage](./docs/hld.svg)

**Flow:**
1. `content-service` registers a movie's metadata (title, genre, cast, etc.) in PostgreSQL.
2. `video-service` accepts the raw video file upload, stores it in S3, and publishes a `video.uploaded` Kafka event.
3. `encoding-service` consumes that event, downloads the raw file from S3, transcodes it into multiple qualities (1080p/720p/480p/360p) with FFmpeg, generates HLS (`.m3u8`) playlists, uploads the encoded output back to S3, and publishes a `video.encoded` Kafka event.
4. `content-service` consumes `video.encoded` to update the movie's status (`PENDING → UPLOADED → ENCODING → ENCODED → READY`).
5. `streaming-service` consumes `video.encoded` to cache the playlist location in Redis, then serves presigned S3 URLs / signed HLS playlists to clients on request.

## Services

| Service | Port | Responsibility |
|---|---|---|
| `content-service` | 8081 | Movie catalog CRUD (PostgreSQL), consumes encoding-complete events |
| `video-service` | 8082 | Accepts video uploads, stores raw files in S3, emits `video.uploaded` |
| `encoding-service` | 8083 | Consumes `video.uploaded`, runs FFmpeg to produce multi-quality HLS output, emits `video.encoded` |
| `streaming-service` | 8084 | Serves presigned HLS playback URLs, caches playlist locations in Redis |

## Tech Stack

- **Language / Framework:** Java 25, Spring Boot (Web, Data JPA, Kafka, Data Redis, Validation, Actuator)
- **Messaging:** Apache Kafka (+ Zookeeper) for async, event-driven communication between services
- **Storage:** PostgreSQL (movie metadata), AWS S3 (raw and encoded video), Redis (playlist URL cache)
- **Media processing:** FFmpeg — transcodes uploads into adaptive-bitrate HLS renditions
- **Containerization:** Docker & Docker Compose (separate local and production compose files)

## API Overview

**Content Service** — `/api/v1/movies`
- `POST /` — add a movie to the catalog
- `GET /` — list all movies
- `GET /genre/{genre}` — filter by genre
- `GET /{movieId}` — get movie by ID

**Video Service** — `/api/v1/videos`
- `POST /upload/{movieId}` — multipart upload of the raw video file; kicks off encoding automatically via Kafka

**Streaming Service** — `/api/v1/stream`
- `GET /{movieId}` — get the presigned HLS master playlist URL for a movie
- `GET /{movieId}/playlist?path=...` — get a signed quality-specific `.m3u8` playlist

## Possible Next Steps

- Add authentication/authorization at the gateway
- Retry/dead-letter handling for failed encoding jobs
- CDN in front of S3 for playback instead of direct presigned URLs
- CI/CD pipeline for the four services
