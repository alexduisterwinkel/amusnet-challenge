package nl.alex.amusnet.controller;

import nl.alex.amusnet.model.Activity;
import nl.alex.amusnet.model.Player;
import nl.alex.amusnet.service.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ApiController {

    private final ApiService apiService;

    @Autowired
    public ApiController(ApiService apiService) {
        this.apiService = apiService;
    }

    @GetMapping("/top-1")
    public String getTop1() {
        //fetch player data (currently the first 30)
        List<Player> players = apiService.getPlayerData();
        if(players.isEmpty()) {
            return "Try again later";
        }

        int highestGgr = 0;
        String player_name = "";

        //loop through the players
        for(Player player: players) {
            //fetch the gaming activity per player
            List<Activity> activities = apiService.getActivityData(player.id());
            if (!activities.isEmpty()) {
                int betAmount = 0;
                int winAmount = 0;
                for (Activity activity : activities) {
                    betAmount += activity.betAmount();
                    winAmount += activity.winAmount();
                }
                //calculate GGR based on betAmount and winAmount
                int ggr = betAmount - winAmount;
                System.out.println("Player: " + player.name() + " has aan GGR of " + ggr);
                //Keep state of the player with the highest GGR
                if (ggr < highestGgr) {
                    highestGgr = ggr;
                    player_name = player.name();
                }
            }
        }
        String response = "Player: "+ player_name +" has the highest GGR with value of " + highestGgr;
        System.out.println(response);
        return response;
    }
}
