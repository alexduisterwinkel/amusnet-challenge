package nl.alex.amusnet.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.alex.amusnet.config.ApiConfig;
import nl.alex.amusnet.model.Activity;
import nl.alex.amusnet.model.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApiService {

    private final WebClient webClient;

    @Autowired
    private ApiConfig apiConfig;

    ObjectMapper mapper = new ObjectMapper();

    public ApiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    /*
    API call to fetch the player data with parameter of number of results.
    Because we want to handle any inconsistency in the response, there is .onErrorResume to proceed without any exceptions
    For the player data, it is quite crucial, so I will return a message to try again later.
     */
    public List<Player> getPlayerData() {
        Mono<Player[]> response = this.webClient.get()
                .uri(apiConfig.getPlayerUrl(), 30)
                .retrieve()
                .bodyToMono(Player[].class)
                .onErrorResume(ex -> {
                    System.err.println("Error occurred during retrieving: " + ex.getMessage());
                    return Mono.just(new Player[0]);
                });
        Player[] players = response.block();
        if(players != null && players.length != 0) {
            return Arrays.stream(players)
                    .map(player -> mapper.convertValue(player, Player.class))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    /*
    API call to fetch the gaming activity of a certain player with parameter of number of activities.
    Because we want to handle any inconsistency in the response, there is .onErrorResume to proceed without any exceptions
    For now, I have made the decision to proceed and skip this specific player if this error occurs.
     */
    public List<Activity> getActivityData(int playerID) {
        Mono<Activity[]> response = this.webClient.get()
                .uri(apiConfig.getActivityUrl(), playerID, 20)
                .retrieve()
                .bodyToMono(Activity[].class)
                .onErrorResume(ex -> {
                    System.err.println("Error occurred during retrieving: " + ex.getMessage());
                    return Mono.just(new Activity[0]);
                });;
        Activity[] activities = response.block();
        if (activities != null && activities.length != 0) {
            return Arrays.stream(activities)
                    .map(activity -> mapper.convertValue(activity, Activity.class))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
