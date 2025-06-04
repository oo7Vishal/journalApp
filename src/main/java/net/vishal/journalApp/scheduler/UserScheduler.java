package net.vishal.journalApp.scheduler;

import net.vishal.journalApp.cache.AppCache;
import net.vishal.journalApp.entity.JournalEntry;
import net.vishal.journalApp.entity.User;
import net.vishal.journalApp.enums.Sentiment; // Make sure this enum exists and is correctly defined
import net.vishal.journalApp.model.SentimentData; // Keeping this import
import net.vishal.journalApp.repository.UserRepositoryImpl;
import net.vishal.journalApp.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class UserScheduler {

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepositoryImpl userRepository;

    @Autowired
    private AppCache appCache;

    @Autowired
    private KafkaTemplate<String, SentimentData> kafkaTemplate;

    @Scheduled(cron = "0 0 9 * * SUN") // Runs at 9:00 AM every Sunday
    public void fetchUsersAndSendSaMail() {
        List<User> users = userRepository.getUserForSA();
        for (User user : users) {
            List<JournalEntry> journalEntries = user.getJournalEntries();

            // Filter entries from the last 7 days, get sentiments, and filter out nulls
            List<Sentiment> sentiments = journalEntries.stream()
                    .filter(x -> x.getDate() != null && x.getDate().isAfter(LocalDateTime.now().minus(7, ChronoUnit.DAYS)))
                    .map(JournalEntry::getSentiment)
                    .filter(java.util.Objects::nonNull) // Ensure sentiment itself is not null
                    .collect(Collectors.toList());

            // If no sentiments found for the period, skip this user
            if (sentiments.isEmpty()) {
                System.out.println("No journal entries with sentiment found for user: " + user.getEmail() + " in the last 7 days.");
                continue;
            }

            // Count occurrences of each sentiment
            Map<Sentiment, Integer> sentimentCounts = new HashMap<>();
            for (Sentiment sentiment : sentiments) {
                sentimentCounts.put(sentiment, sentimentCounts.getOrDefault(sentiment, 0) + 1);
            }

            // Find the most frequent sentiment
            Sentiment mostFrequentSentiment = null;
            int maxCount = 0;
            for (Map.Entry<Sentiment, Integer> entry : sentimentCounts.entrySet()) {
                if (entry.getValue() > maxCount) {
                    maxCount = entry.getValue();
                    mostFrequentSentiment = entry.getKey();
                }
            }

            // If a most frequent sentiment is found, process it
            if (mostFrequentSentiment != null) {
                String emailSubject = "Your Weekly Journal Sentiment Summary";
                String emailContent = "Based on your journal entries from the last 7 days, your most frequent sentiment was: " + mostFrequentSentiment;

                SentimentData sentimentData = SentimentData.builder()
                        .email(user.getEmail())
                        .sentiment(emailContent)
                        .build();

                try {
                    kafkaTemplate.send("weekly-sentiments", sentimentData.getEmail(), sentimentData);
                    System.out.println("Sent sentiment data to Kafka for user: " + user.getEmail());
                } catch (Exception e) {
                    System.err.println("Failed to send sentiment data to Kafka for user: " + user.getEmail() + ". Sending email instead. Error: " + e.getMessage());
                    emailService.sendEmail(sentimentData.getEmail(), emailSubject, sentimentData.getSentiment());
                }
            } else {
                System.out.println("Could not determine a dominant sentiment for user: " + user.getEmail() + " from recent entries.");
            }
        }
    }

    ---

    @Scheduled(cron = "0 0/10 * ? * *") // Runs every 10 minutes
    public void clearAppCache() {
        appCache.init();
    }
}