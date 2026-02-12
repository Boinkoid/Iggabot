package logic;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import steam.SteamLink;
import net.dv8tion.jda.api.entities.User;

import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import gambling.*;
import music.*;
public class SlashRouter extends ListenerAdapter{

	private Iggacoin bank;
	private SteamLink steam = new SteamLink();
	private Dice dice = new Dice();
	public final File SUGGESTION_FILE= new File("C:/Iggacorp Bot/Logs/Suggestions.txt");

	boolean guh = true;
	Member m;
	VoiceChannel vc;
	private Music music;
	public void linkMusic() {
		music = Main.music;
	}
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
		case "tts" -> {
			event.reply("Not implemented yet. Igg needs to record his lines, bully him ab it").setEphemeral(true).queue();
			/*vc = (VoiceChannel) m.getVoiceState().getChannel();
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

			event.reply("Speaking...").setEphemeral(true).queue();*/
		}
		case "goon" -> {
			String tmp = "";
			for(int i = 0;i<Main.maxGoon;i++) {
				tmp += ":tongue:";
			}
			event.reply(tmp).setEphemeral(true).queue();;
		}
		case "link" -> {
			event.deferReply(true).queue();

			String steamInput = event.getOption("account").getAsString();
			String disc = event.getUser().getId();

			boolean ok = steam.link(disc,steamInput);

			if(ok)
				event.getHook().sendMessage("Steam linked").queue();
			else
				event.getHook().sendMessage("Failed to link Steam").queue();
		}
		case "unlink" -> {
			event.deferReply(true).queue();

			boolean ok = steam.unlink(event.getUser().getId());

			if(ok)
				event.getHook().sendMessage("Steam unlinked").queue();
			else
				event.getHook().sendMessage("No link found").queue();


		}
		case "blackjack" -> {
			event.reply("Not Implemented yet").setEphemeral(true).queue();;
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
			List<User> users=new ArrayList<>();

			for(int i=1;i<=25;i++){
				OptionMapping o=event.getOption("user"+i);
				if(o!=null&&o.getAsUser()!=null)
					users.add(o.getAsUser());
			}

			String game = steam.getRandomSharedGame(users);

			if(game==null){
				event.reply("No shared multiplayer games found.").setEphemeral(true).queue();
			}else{
				event.reply("🎮 Shared Game: "+game).queue();
			}

		}
		//Music
		case "play" -> {
			/*if(m.getVoiceState()==null) {
				event.reply("You have to be in a VC first!").queue();
				return;
			}
			Main.bot.getDirectAudioController().connect(m.getVoiceState().getChannel());
			music.play(event.getOptions().get(0).getAsString(), event);*/
			event.reply("Not yet implemented").setEphemeral(true).queue();
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
