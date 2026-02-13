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
    private static final String MODEL_NAME = "gemini-3-flash-preview";

    public GeminiAdapter() {
        this.client = new Client();
    }


    public String fetchRawJsonFromGemini(String userText) {
        LocalDate today = LocalDate.now();

        //  Define the System Instruction
        String systemRules = String.format("""
    You are a strict JSON extractor. 
    Context: Today is %s.
    
    Task: Extract fields from the user input:
    1. title (String): Clean task name (remove date/time words like "tomorrow at 5pm").
    2. date (String): ISO 8601 format (YYYY-MM-DDTHH:MM:SS). If user specifies time (e.g., "at 5pm"), include it. If no time is specified, default to T09:00:00.
    3. priority (String): Urgent,HIGH, MEDIUM, or LOW (infer from words like "urgent").
    4. category (String): Work, Personal, Study, Finance, or Health... based on task intent.
    
    Output: Return ONLY raw JSON. No Markdown.
    """, today);

        // Configure the Request
        GenerateContentConfig config = GenerateContentConfig.builder()
                .systemInstruction(Content.fromParts(Part.fromText(systemRules)))
                .build();

        try {
            GenerateContentResponse response = client.models.generateContent(
                    MODEL_NAME,
                    userText,
                    config
            );

            return response.text();

        } catch (Exception e) {
            System.err.println("Gemini SDK Error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public String analyzeProductivity(String taskHistorySummary) {

        String prompt = String.format("""
        You are a strict Productivity Analyst.
        Analyze this user's recent task history:
        "%s"
        
        Return a JSON object with:
        - score (0-100 based on completion rate)
        - summary (1 sentence observation)
        - suggestion (1 actionable tip)
        
        NO Markdown. JUST JSON.
        """, taskHistorySummary);

        GenerateContentConfig config = GenerateContentConfig.builder()
                .systemInstruction(Content.fromParts(Part.fromText(prompt)))
                .build();


        try {
            GenerateContentResponse response = client.models.generateContent(
                    MODEL_NAME,
                    taskHistorySummary,
                    config
            );

            return response.text();
        } catch (Exception e) {
            return null;
        }
    }


}
