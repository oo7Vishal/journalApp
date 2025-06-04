package net.vishal.journalApp.cache;

import jakarta.annotation.PostConstruct;
import net.vishal.journalApp.entity.ConfigJournalAppEntity;
import net.vishal.journalApp.repository.ConfigJournalAppRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppCache {

    public enum keys {
        WEATHER_API;
    }

    @Autowired
    private ConfigJournalAppRepository configJournalAppRepository;

    public Map<String,String> appCache ;


    @PostConstruct
    public void init() {
        appCache = new HashMap<>();
        List<ConfigJournalAppEntity> all = configJournalAppRepository.findAll();
         for(ConfigJournalAppEntity configJournalAppEntity : all) {
             appCache.put(configJournalAppEntity.getKey(),configJournalAppEntity.getValue());
         }

    }

}
