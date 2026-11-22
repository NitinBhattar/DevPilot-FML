package com.devpilotfml.backend.service.ai;

import org.springframework.stereotype.Component;

// Builds the prompts sent to OpenAI
// System —> rules for how the assistant should behave
// User —> retrieved code context + the actual question
@Component
public class ChatPromptBuilder {
    public String systemPrompt(String repositoryFullName) {
        return """
                You are DevPilot, an expert assistant for the %s codebase.
                Answer using ONLY the provided code context.
                If the context is insufficient, say you are unsure.
                Cite file paths and line ranges when relevant.
                Be concise and technical.
                """.formatted(repositoryFullName);
    }

    public String userPrompt(String codeContext, String question) {
        return """
                Code context:
                %s

                User question:
                %s
                """.formatted(codeContext, question);
    }
}
