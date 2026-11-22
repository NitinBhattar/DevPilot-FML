package com.devpilotfml.backend.service.ai;

public final class RagSettings {
    // Chunks that can be fetched from vector database per question
    public static final int TOP_K_CHUNKS = 8;

    // Max time in ms to keep SSE stream open while this model is responding
    // SSE (Server-Sent Events) is a mechanism that allows a server to continuously send data to a client over a single HTTP connection
    // The backend starts processing the question. Instead of waiting until the entire answer is generated, it can send the response piece by piece to the frontend
    public static final long STREAM_TIMEOUT_MS = 180_000L;

    // Metadata key in each embedded document
    public static final String METADATA_REPO_ID = "repoId";

    private RagSettings() {}
}
