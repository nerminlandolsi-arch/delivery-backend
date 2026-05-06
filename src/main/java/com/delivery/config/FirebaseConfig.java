package com.delivery.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import jakarta.annotation.PostConstruct;
import java.io.InputStream;

@Configuration
@Slf4j
public class FirebaseConfig {

    @Value("${app.firebase.credentials-file:firebase-service-account.json}")
    private String credentialsFile;

    @PostConstruct
    public void initialize() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                ClassPathResource resource = new ClassPathResource(credentialsFile);
                if (resource.exists()) {
                    InputStream serviceAccount = resource.getInputStream();
                    FirebaseOptions options = FirebaseOptions.builder()
                            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                            .build();
                    FirebaseApp.initializeApp(options);
                    log.info("Firebase initialized successfully");
                } else {
                    log.warn("Firebase credentials file not found â€” notifications disabled");
                }
            }
        } catch (Exception e) {
            log.warn("Firebase initialization failed â€” notifications disabled: {}", e.getMessage());
        }
    }
}
