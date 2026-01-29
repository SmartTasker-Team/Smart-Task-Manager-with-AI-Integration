package com.smarttask.manager.infrastructure.external.ai;

import com.smarttask.manager.application.dto.AiInsightResult;
import com.google.gson.*; // Import Gson, GsonBuilder, JsonDeserializer, etc.
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Implementation of the natural language parsing service.
 * <p>
 * Connects to the Gemini Adapter to convert unstructured text input
 * (e.g., "Meeting with team tomorrow at 3 PM") into structured data.
 * </p>
 */
public class NLPParserImpl {

    private final GeminiAdapter adapter;
    private final Gson gson;

    public NLPParserImpl() {
        this.adapter = new GeminiAdapter();

        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new JsonDeserializer<LocalDate>() {
                    @Override
                    public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                        return LocalDate.parse(json.getAsString()); // Converts "2026-02-06" -> Java LocalDate
                    }
                })
                .create();
    }

    public AiInsightResult parseNaturalLanguage(String text) {
        // 1. Get the text directly from the SDK Adapter
        // (The SDK handles the complex "candidates" extraction for us now!)
        String rawResponse = adapter.fetchRawJsonFromGemini(text);

        if (rawResponse == null || rawResponse.isEmpty()) {
            return null;
        }

        try {
            // 2. Clean up Markdown (Gemini often adds ```json ... ``` wrappers)
            String cleanJson = rawResponse.replace("```json", "")
                    .replace("```", "")
                    .trim();

            // 3. Convert the clean JSON string directly to our Java Object
            return gson.fromJson(cleanJson, AiInsightResult.class);

        } catch (Exception e) {
            System.err.println("Error parsing AI response: " + e.getMessage());
            // Optional: Print the raw text to see what went wrong
            System.err.println("Raw Text was: " + rawResponse);
            return null;
        }
    }
}
