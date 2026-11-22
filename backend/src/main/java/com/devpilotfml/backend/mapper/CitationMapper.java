package com.devpilotfml.backend.mapper;

import com.devpilotfml.backend.dto.CitationDto;
import com.devpilotfml.backend.util.Conversions;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

import static com.devpilotfml.backend.util.Conversions.toInteger;

@Component
@RequiredArgsConstructor
// Convert vectors into api citation
public class CitationMapper {
    private final JsonMapper jsonMapper;

    public CitationDto fromDocument(Document document) {
        var meta = document.getMetadata();
        return new CitationDto(
                Conversions.toString(meta.get("filePath")),
                toInteger(meta.get("startLine")),
                toInteger(meta.get("endLine")),
                Conversions.toString(meta.get("language")));
    }

    public String toJson(List<CitationDto> citations) {
        try {
            return jsonMapper.writeValueAsString(citations);
        } catch (JacksonException e) {
            return "[]";
        }
    }

    public List<CitationDto> fromJson(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }

        try {
            return jsonMapper.readValue(json, new TypeReference<>() {});
        } catch (JacksonException e) {
            return List.of();
        }
    }
}
