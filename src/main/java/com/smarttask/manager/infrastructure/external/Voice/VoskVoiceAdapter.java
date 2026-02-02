package com.smarttask.manager.infrastructure.external.Voice;

import com.smarttask.manager.domain.service.VoiceRecognitionService;
import org.vosk.LibVosk;
import org.vosk.LogLevel;
import org.vosk.Model;
import org.vosk.Recognizer;

import javax.sound.sampled.*;


public class VoskVoiceAdapter implements VoiceRecognitionService {

    private static Model model;

    public VoskVoiceAdapter() {
        if (model == null) {
            try {

                LibVosk.setLogLevel(LogLevel.WARNINGS);

                model = new Model("vosk-model");
            } catch (Exception e) {
                System.err.println("CRITICAL: Could not load VOSK Model. Check path.");
                e.printStackTrace();
            }
        }
    }

    @Override
    public String listen(int timeoutSeconds) {
        if (model == null) return null;

        try {
            // Audio Format: 16kHz, 16-bit, Mono (Standard for VOSK)
            AudioFormat format = new AudioFormat(16000, 16, 1, true, false);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

            if (!AudioSystem.isLineSupported(info)) {
                System.err.println(" Microphone unavailable.");
                return null;
            }

            try (TargetDataLine microphone = (TargetDataLine) AudioSystem.getLine(info);
                 Recognizer recognizer = new Recognizer(model, 16000)) {

                microphone.open(format);
                microphone.start();

                System.out.println("Mic Open. Speaking...");

                byte[] buffer = new byte[4096];
                int bytesRead;

                long endTime = System.currentTimeMillis() + (timeoutSeconds * 1000L);

                // Recording Loop
                while (System.currentTimeMillis() < endTime) {
                    bytesRead = microphone.read(buffer, 0, buffer.length);

                    recognizer.acceptWaveForm(buffer, bytesRead);
                }

                // Get final result
                String jsonResult = recognizer.getFinalResult();
                microphone.stop();

                return parseJson(jsonResult);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //Extract "text" from { "text": "hello world" }
    private String parseJson(String json) {
        int start = json.indexOf(": \"");
        if (start != -1) {
            int end = json.lastIndexOf("\"");
            if (end > start + 3) {
                return json.substring(start + 3, end);
            }
        }
        return "";
    }
}