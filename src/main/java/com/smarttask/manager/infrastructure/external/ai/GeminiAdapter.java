package com.smarttask.manager.infrastructure.external.ai;

import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;

import java.time.LocalDate;

/**
 * Adapter for communicating with the Google Gemini API using the official SDK.
 * <p>
 * Responsible for sending prompts and handling responses for features like smart categorization
 * and productivity insights.
 * </p>
 */
public class GeminiAdapter {

    private final Client client;
    // We use the model you specified (or gemini-3-flash-preview which is standard)
    private static final String MODEL_NAME = "gemini-3-flash-preview";

    public GeminiAdapter() {
        // The SDK automatically looks for the GOOGLE_API_KEY or GEMINI_API_KEY env variable.
        // Or you can pass the key directly: new Client("YOUR_KEY");
        this.client = new Client();
    }

    /**
     * Sends the text to Gemini and asks for a structured JSON response.
     * @param userText The natural language text (e.g. "Buy milk tomorrow")
     * @return The raw JSON string response from AI.
     */
    public String fetchRawJsonFromGemini(String userText) {
        LocalDate today = LocalDate.now();

        // 1. Define the System Instruction (The "Brain" rules)
        // We replace "You are a cat" with the JSON Extractor logic.
        String systemRules = String.format("""
            You are a strict JSON extractor. 
            Context: Today is %s.
            
            Task: Extract fields from the user input:
            1. title (String): Clean task name.
            2. date (String): YYYY-MM-DD. Calculate relative dates.
            3. priority (String): HIGH, MEDIUM, or LOW.
            4. category (String): Work, Personal, Study, Finance, or Health.
            
            Output: Return ONLY raw JSON. No Markdown.
            """, today);

        // 2. Configure the Request
        GenerateContentConfig config = GenerateContentConfig.builder()
                .systemInstruction(Content.fromParts(Part.fromText(systemRules)))
                .build();

        try {
            // 3. Call the API
            GenerateContentResponse response = client.models.generateContent(
                    MODEL_NAME,
                    userText,
                    config
            );

            // 4. Return the text result
            return response.text();

        } catch (Exception e) {
            System.err.println("Gemini SDK Error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
