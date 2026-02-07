package Steam;

import java.util.ArrayList;

public class Profile {
	String discName;
	String steam;
	ArrayList<String> games;

	public Profile(String discName, String steam, ArrayList<String> games) {
		this.discName = discName;
		this.steam = steam;
		this.games = games;
	}

	public String getDiscName() {
		return this.discName;
	}

	public String getSteam() {
		return this.steam;
	}

	public ArrayList<String> getGames() {
		return this.games;
	}
}