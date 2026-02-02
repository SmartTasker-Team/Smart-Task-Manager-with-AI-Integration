package com.smarttask.manager.application.usecase.voice;

import com.smarttask.manager.domain.service.VoiceRecognitionService;
import com.smarttask.manager.infrastructure.external.Voice.VoskVoiceAdapter;

public class CaptureVoiceCommandUseCase {

    private final VoiceRecognitionService voiceService;

    public CaptureVoiceCommandUseCase() {
        this.voiceService = new VoskVoiceAdapter();
    }

    public String execute() {
        // Listen for exactly 5 seconds
        String text = voiceService.listen(5);

        if (text == null || text.trim().isEmpty()) {
            return null;
        }

        return text;
    }
}
