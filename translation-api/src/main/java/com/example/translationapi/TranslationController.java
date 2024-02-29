package com.example.translationapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

import java.io.FileInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
@RestController
public class TranslationController {
	private static final Logger logger = LoggerFactory.getLogger(TranslationController.class);
    public static void main(String[] args) {
        SpringApplication.run(TranslationController.class, args);
    }

    @PostMapping("/translate")
    public ResponseEntity<?> translate(@RequestBody TranslationRequest request) {
    	logger.info("Received translation request: {}", request.getText());
        String projectId = "translation-api-415606";
        String credentialsPath = "translation-api.json";

        try {
            Translate translate = TranslateOptions.newBuilder()
                    .setCredentials(ServiceAccountCredentials.fromStream(new FileInputStream(credentialsPath)))
                    .setProjectId(projectId)
                    .build()
                    .getService();

            Translation translation = translate.translate(request.getText(),
                    Translate.TranslateOption.sourceLanguage("en"),
                    Translate.TranslateOption.targetLanguage("fr"));

            TranslationResponse response = new TranslationResponse(translation.getTranslatedText());
            logger.info("Translation successful: {}", response.getTranslation());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
        	logger.error("Error translating text", e);
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error translating text");
        }
    }

    static class TranslationRequest {
        private String text;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    static class TranslationResponse {
        private String translation;

        public TranslationResponse(String translation) {
            this.translation = translation;
        }

        public String getTranslation() {
            return translation;
        }

        public void setTranslation(String translation) {
            this.translation = translation;
        }
    }
}
