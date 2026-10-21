package com.smarttask.manager.domain.service;

/**
 * Interface defining the contract for voice recognition.
 * This allows us to swap VOSK for Google/OpenAI later without breaking the app.
 */

public interface VoiceRecognitionService {
    String listen(int timeoutSeconds);
}
