package com.smarttask.manager.application.usecase.task;

import com.smarttask.manager.application.dto.AiInsightResult;
import com.smarttask.manager.infrastructure.external.ai.NLPParserImpl;

/**
 * Use Case that leverages AI to interpret user input.
 * <p>
 * Takes a raw string (e.g., "Buy milk tomorrow at 5pm") and uses the AI service to
 * convert it into a structured task with a due date and category.
 * </p>
 */
public class ParseNaturalLanguageTaskUseCase {

    private final NLPParserImpl nlpParser;

    public ParseNaturalLanguageTaskUseCase() {
        this.nlpParser = new NLPParserImpl();
    }

    public AiInsightResult execute(String rawInput) {
        // 1. Basic Validation (Business Logic)
        if (rawInput == null || rawInput.trim().isEmpty()) {
            return null;
        }

        AiInsightResult result = nlpParser.parseNaturalLanguage(rawInput);

        if (result != null && result.priority == null) {
            result.priority = "MEDIUM";
        }

        return result;
    }
}