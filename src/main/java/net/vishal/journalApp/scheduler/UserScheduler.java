package net.vishal.journalApp.scheduler;

import net.vishal.journalApp.cache.AppCache;
import net.vishal.journalApp.entity.JournalEntry;
import net.vishal.journalApp.entity.User;
import net.vishal.journalApp.enums.Sentiment; // Make sure this enum exists and is correctly defined
import net.vishal.journalApp.repository.UserRepositoryImpl;
import net.vishal.journalApp.service.EmailService;
// import net.vishal.journalApp.service.SentimentData; // Assuming this class exists
import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.kafka.core.KafkaTemplate; // Don't forget this import for KafkaTemplate
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
//
//    @Autowired // Uncomment this if you're using Kafka
//    private KafkaTemplate<String, SentimentData> kafkaTemplate;

    @Scheduled(cron = "0 0 9 * * SUN") // Runs at 9:00 AM every Sunday
    public void fetchUsersAndSendSaMail() {
        List<User> users = userRepository.getUserForSA();
        for (User user : users) {
            // Filter entries from the last 7 days and get their sentiments
            List<Sentiment> sentiments = user.getJournalEntries().stream()
                    .filter(x -> x.getDate() != null && x.getDate().isAfter(LocalDateTime.now().minus(7, ChronoUnit.DAYS)))
                    .map(JournalEntry::getSentiment) // Method reference is cleaner
                    .filter(java.util.Objects::nonNull) // Ensure sentiment is not null before collecting
                    .collect(Collectors.toList());

            // Check if there are any sentiments to analyze
            if (sentiments.isEmpty()) {
                System.out.println("No journal entries with sentiment found for user: " + user.getEmail() + " in the last 7 days.");
                // Optionally, send a different email or skip for this user
                continue; // Move to the next user
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

//                // Prepare SentimentData for Kafka or direct email
//                SentimentData sentimentData = SentimentData.builder()
//                        .email(user.getEmail())
//                        .sentiment(emailContent) // Use the constructed email content here
//                        .build();

                try {
                    // Attempt to send to Kafka
              //      kafkaTemplate.send("weekly-sentiments", sentimentData.getEmail(), sentimentData);
                    System.out.println("Sent sentiment data to Kafka for user: " + user.getEmail());
                } catch (Exception e) {
                    // Fallback to sending email directly if Kafka fails
                    System.err.println("Failed to send sentiment data to Kafka for user: " + user.getEmail() + ". Sending email instead. Error: " + e.getMessage());
               //     emailService.sendEmail(sentimentData.getEmail(), emailSubject, sentimentData.getSentiment());
                }
            } else {
                System.out.println("Could not determine most frequent sentiment for user: " + user.getEmail());
                // Optionally, handle users with no clear dominant sentiment (e.g., all neutral, or only one entry)
            }
        }
    }



    @Scheduled(cron = "0 0/10 * ? * *") // Runs every 10 minutes
    public void clearAppCache() {
        appCache.init(); // Assuming appCache.init() correctly clears and reinitializes the cache
    }

}