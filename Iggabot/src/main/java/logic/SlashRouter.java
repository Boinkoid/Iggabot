package logic;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.entities.User;

import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Steam.SteamLink;
import gambling.*;

public class SlashRouter extends ListenerAdapter{

	private Iggacoin bank;
	private SteamLink steam = new SteamLink();
	private Dice dice = new Dice();
	public final File SUGGESTION_FILE= new File("C:/Iggacorp Bot/Logs/Suggestions.txt");
	private final Map<Long, Music> musicMap = new HashMap<>();
	private final Map<Long, Voice> voiceMap = new HashMap<>();
	private Music getMusic(long guildId, net.dv8tion.jda.api.entities.Guild g) {
		return musicMap.computeIfAbsent(guildId, id -> new Music(g));
	}

	private Voice getVoice(long guildId, net.dv8tion.jda.api.entities.Guild g) {
		return voiceMap.computeIfAbsent(guildId, id -> new Voice(g));
	}
	boolean guh = true;
	Member m;
	VoiceChannel vc;
	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		if(guh) {
			bank = Main.$;
			guh = false;
		}
		m = event.getMember();

		switch (event.getName()) {

		case "dice" -> { 
			dice.handle(event);
		}
		case "pay" -> {
			String from = event.getUser().getId();
			String to = event.getOption("user").getAsUser().getId();
			BigInteger amt = new BigInteger(event.getOption("amount").getAsString());

			if (bank.transfer(from, to, amt)) {
				event.reply("Transferred " + amt + " coins.").queue();
			} else {
				event.reply("Not enough funds.").setEphemeral(true).queue();
			}
		}
		case "play" -> {
			vc = (VoiceChannel) m.getVoiceState().getChannel();
			if (m == null || m.getVoiceState() == null || m.getVoiceState().getChannel() == null) {
				event.reply("Join a voice channel first.").setEphemeral(true).queue();
				return;
			}
			Music music = getMusic(event.getGuild().getIdLong(), event.getGuild());

			music.connect(vc);
			music.play(event.getOption("query").getAsString());

			event.reply("Added to queue.").queue();
		}
		case "tts" -> {
			vc = (VoiceChannel) m.getVoiceState().getChannel();
			if (m == null || m.getVoiceState() == null || m.getVoiceState().getChannel() == null) {
				event.reply("Join VC first.").setEphemeral(true).queue();
				return;
			}


			Voice voice = getVoice(event.getGuild().getIdLong(), event.getGuild());

			voice.say(
					event.getOption("text").getAsString(),
					event.getOption("voice").getAsString(),
					vc
					);

			event.reply("Speaking...").setEphemeral(true).queue();
		}
		case "goon" -> {
			String tmp = "";
			for(int i = 0;i<Main.maxGoon;i++) {
				tmp += ":tongue:";
			}
			event.reply(tmp).setEphemeral(true).queue();;
		}
		case "link" -> {

			boolean ok =
					steam.link(
							event.getUser().getId(),
							event.getOption("account").getAsString()
							);

			if(ok)
				event.reply("✅ Steam linked.").setEphemeral(true).queue();
			else
				event.reply("❌ Could not resolve Steam account.")
				.setEphemeral(true).queue();
		}

		case "unlink" -> {

			if(steam.unlink(event.getUser().getId()))
				event.reply("🗑️ Steam unlinked.")
				.setEphemeral(true).queue();
			else
				event.reply("❌ No Steam account linked.")
				.setEphemeral(true).queue();
		}
		case "blackjack" -> {
			new Blackjack();
		}
		case "coom" -> {
			try(BufferedReader read = new BufferedReader(new FileReader("C:/Iggacorp Bot/Logs/Goodnight Coom.txt"))){
				String buh = "";
				for(String e : read.lines().toList()) {
					buh+=e+"\n";
				}
				event.reply(buh).setEphemeral(true).queue();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		case "board" -> {
			event.reply(bank.getLeaderboard()).setEphemeral(true).queue();
		}
		case "suggest" -> {
			try(BufferedWriter w = new BufferedWriter(new FileWriter(SUGGESTION_FILE,true))){
				SUGGESTION_FILE.createNewFile();
				w.append("@" + event.getMember().getUser().getName() + "(" + event.getMember().getNickname() + ") suggests \"" + event.getOption("suggestion").getAsString() + "\"");
				w.newLine();
				w.flush();
				event.reply("Thanks for the suggestion! These help me know what people want and help me to add features that the community wants."
						+ "\nFor any questions, email me at the link in Iggabots bio!").setEphemeral(true).queue();
			} catch(Exception e) {
				e.printStackTrace();
				System.out.println("Suggestion got fucked");
			}
		}
		case "games" -> {

		    List<User> users = new ArrayList<>();

		    for(int i=1;i<=25;i++){
		        var opt = event.getOption("user"+i);
		        if(opt!=null)
		            users.add(opt.getAsUser());
		    }

		    String game = steam.getRandomSharedGame(users);

		    event.reply("🎮 Try playing: **"+game+"**").queue();
		}

		/*case "" -> {

		 	 }
		 case "" -> {

		 	 }
		 case "" -> {

		 	 }
		 case "" -> {

		 	 }
		 case "" -> {

		 	 }
		 */
		}
	}
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {

	}
}
