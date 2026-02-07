package Steam;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class SteamLink {
	static BufferedReader read;
	static BufferedWriter write;
	static File file = new File("C:/Iggacorp Bot/Logs/Steam.txt");
	ArrayList<Profile> profile = new ArrayList<>();

	public SteamLink() {
		this.loadFiles();
	}

	public void pickGame(ArrayList<String> var1, SlashCommandInteractionEvent var2) {
		throw new Error("Unresolved compilation problems: \n\tSlashCommandInteractionEvent cannot be resolved to a type\n\tMain cannot be resolved\n");
	}

	public void loadFiles() {
		try {
			read = new BufferedReader(new FileReader(file));
			write = new BufferedWriter(new FileWriter(file));

			for(String i = read.readLine(); i != null; i = read.readLine()) {
				ArrayList<String> games = new ArrayList();
				String[] guh = i.split(":")[1].split("+947+");
				String[] var7 = guh;
				int var6 = guh.length;

				for(int var5 = 0; var5 < var6; ++var5) {
					String g = var7[var5];
					games.add(g);
				}

				this.profile.add(new Profile(i.split("+957+")[0], i.split(":")[1], games));
			}
		} catch (Exception var8) {
			var8.printStackTrace();
			System.out.println("Steam Link got fucked");
		}

	}

	public void link(String user) {
		if (user != null) {
			boolean guh = false;
			Iterator var4 = this.profile.iterator();

			while(var4.hasNext()) {
				Profile p = (Profile)var4.next();
				if (p.getSteam().equals(this.resolve(user))) {
					guh = true;
				}
			}

			if (guh) {
				try {
					String buh = "";
					Iterator var5 = this.profile.iterator();

					while(var5.hasNext()) {
						Profile p = (Profile)var5.next();
						ArrayList<String> bust = p.getGames();

						for(int i = 0; i < bust.size(); ++i) {
							if (i <= bust.size() - 1) {
								buh = buh + (String)bust.get(i) + "+947+";
							} else {
								buh = buh + (String)bust.get(i);
							}
						}
					}

					write.write("guhg");
					write.flush();
					this.loadFiles();
				} catch (IOException var8) {
					var8.printStackTrace();
					System.out.println("Writer fucked writing");
				}
			} else {
				System.out.println("User is already linked!");
			}
		} else {
			System.out.println("User is null");
		}

	}

	private String resolve(String user) {
		return "";//Steam API = 8494A3B55BD2E5212AED68846410A2E3
	}
}