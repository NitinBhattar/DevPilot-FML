package com.devpilotfml.backend.service.ai;

import com.devpilotfml.backend.dto.CitationDto;
import com.devpilotfml.backend.dto.RetrievedContextDto;
import com.devpilotfml.backend.mapper.CitationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CodeContextRetrieverService {
    private static final String NO_MATCHES = "(No matching code chunks found";

    private final VectorStore vectorStore;
    private final CitationMapper citationMapper;

    public RetrievedContextDto retrieve(UUID repoId, String question) {
        Filter.Expression filter = new FilterExpressionBuilder().eq(RagSettings.METADATA_REPO_ID, repoId.toString()).build();

        SearchRequest search = SearchRequest.builder()
                .query(question)
                .topK(RagSettings.TOP_K_CHUNKS)
                .filterExpression(filter)
                .build();

        List<Document> documents = vectorStore.similaritySearch(search);

        List<CitationDto> citations = documents.stream()
                .map(citationMapper::fromDocument)
                .distinct()
                .toList();

        String contextText = documents.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n---\n\n"));

        // No matches
        if (contextText.isBlank()) {
            contextText = NO_MATCHES;
        }

        return new RetrievedContextDto(citations, contextText);
    }
}
