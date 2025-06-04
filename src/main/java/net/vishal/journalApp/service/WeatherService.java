package net.vishal.journalApp.service;

import net.vishal.journalApp.api.response.WeatherResponse;
import net.vishal.journalApp.cache.AppCache;
import net.vishal.journalApp.constants.PlaceHolders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherService {


    @Value("${weather.api.key}")
    private   String apiKey;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AppCache appCache;

    @Autowired
    private RedisService redisService;

    public WeatherResponse getWeather(String city) {
        WeatherResponse weatherResponse = redisService.get("Weather of" + city, WeatherResponse.class);

        if(weatherResponse != null ) {
            return  weatherResponse;
        }else {
            String finalAPI = appCache.appCache.get(AppCache.keys.WEATHER_API.toString()).replace(PlaceHolders.CITY , city).replace( PlaceHolders.API_KEY , apiKey);
            ResponseEntity<WeatherResponse> response = restTemplate.exchange(finalAPI, HttpMethod.GET, null, WeatherResponse.class);
            WeatherResponse body = response.getBody();
            if(body != null) {
                redisService.set("Weather of" + city, body,300l);
            }
            return body;
        }


    }


}
