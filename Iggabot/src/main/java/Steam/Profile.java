package Steam;

import java.util.ArrayList;

public class Profile {

    private String discName;   // discord user ID
    private String steam;      // steam64 id
    private ArrayList<String> games;

    public Profile(String discName, String steam, ArrayList<String> games) {
        this.discName = discName;
        this.steam = steam;
        this.games = games;
    }

    public String getDiscName() {
        return discName;
    }

    public String getSteam() {
        return steam;
    }

    public ArrayList<String> getGames() {
        return games;
    }

    public void setGames(ArrayList<String> games) {
        this.games = games;
    }
}
