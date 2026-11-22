package com.devpilotfml.backend.service.indexing;

import com.devpilotfml.backend.service.ai.RagSettings;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@Component
public class CodeChunker {
    private final TokenTextSplitter splitter;
    private final CodeFileFilter fileFilter;

    // ctor
    public CodeChunker(
        @Value("${app.indexing.chunk-size:800}")
        int chunkSize,
        CodeFileFilter fileFilter) {

        // Spring AI splits by tokens; ~4 characters per token
        int chunkTokens = Math.max(50, chunkSize / 4);

        this.splitter = TokenTextSplitter.builder()
                .withChunkSize(chunkTokens)
                .build();

        this.fileFilter = fileFilter;
    }

    public List<Document> chunkFile(String repoId, String filePath, String content) {
        if (content == null || content.isBlank()) {
            return List.of();
        }

        String language = fileFilter.detectLanguage(filePath);
        String header = "// File: " + filePath + "\n";

        // Spring ai document contains text and metadata
        // text is what LLM actually reads and metadata is the files information
        Document source = new Document(header + content, baseMetadata(repoId, filePath, language));

        // Splits the document int in chunks
        List<Document> split = splitter.apply(List.of(source));

        // equals to for loop 0 -> split.size()
        return IntStream.range(0, split.size())
                .mapToObj(i -> withChunkIndex(split.get(i), repoId, filePath, language, i))
                .toList();
    }

    // Metadata combiner
    private static Map<String, Object> baseMetadata(String repoId, String filePath, String language) {
        Map<String, Object> metadata = new HashMap<>();

        metadata.put(RagSettings.METADATA_REPO_ID, repoId);
        metadata.put("filePath", filePath);
        metadata.put("language", language);

        return metadata;
    }

    // Adds chunk index to stitch data together
    private static Document withChunkIndex(
            Document chunk,
            String repoId,
            String filePath,
            String language,
            int chunkIndex) {
        Map<String, Object> metadata = new HashMap<>(chunk.getMetadata());

        metadata.put(RagSettings.METADATA_REPO_ID, repoId);
        metadata.put("filePath", filePath);
        metadata.put("language", language);
        metadata.put("chunkIndex", chunkIndex);

        return new Document(chunk.getText(), metadata);
    }
}
