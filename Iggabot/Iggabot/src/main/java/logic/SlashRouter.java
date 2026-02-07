package logic;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import gambling.*;

public class SlashRouter extends ListenerAdapter{

	private final Iggacoin bank = new Iggacoin();
	private Dice dice;

	private final Map<Long, Music> musicMap = new HashMap<>();
	private final Map<Long, Voice> voiceMap = new HashMap<>();

	private Music getMusic(long guildId, net.dv8tion.jda.api.entities.Guild g) {
		return musicMap.computeIfAbsent(guildId, id -> new Music(g));
	}

	private Voice getVoice(long guildId, net.dv8tion.jda.api.entities.Guild g) {
		return voiceMap.computeIfAbsent(guildId, id -> new Voice(g));
	}
	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

		switch (event.getName()) {

		case "dice": 
			dice.handle(event);
			break;
		case "board":
			event.reply(bank.getLeaderboard()).queue();
			break;
		case "pay":
			String from = event.getUser().getId();
			String to = event.getOption("user").getAsUser().getId();
			BigInteger amt = new BigInteger(event.getOption("amount").getAsString());

			if (bank.transfer(from, to, amt)) {
				event.reply("Transferred " + amt + " coins.").queue();
			} else {
				event.reply("Not enough funds.").setEphemeral(true).queue();
			}
			break;
		case "play":
			Member m = event.getMember();
			if (m == null || m.getVoiceState() == null || m.getVoiceState().getChannel() == null) {
				event.reply("Join a voice channel first.").setEphemeral(true).queue();
				return;
			}

			VoiceChannel vc = (VoiceChannel) m.getVoiceState().getChannel();
			Music music = getMusic(event.getGuild().getIdLong(), event.getGuild());

			music.connect(vc);
			music.play(event.getOption("query").getAsString());

			event.reply("Added to queue.").queue();
			break;
		case "tts" :
			Member m = event.getMember();
			if (m == null || m.getVoiceState() == null || m.getVoiceState().getChannel() == null) {
				event.reply("Join VC first.").setEphemeral(true).queue();
				return;
			}

			VoiceChannel vc = (VoiceChannel) m.getVoiceState().getChannel();
			Voice voice = getVoice(event.getGuild().getIdLong(), event.getGuild());

			voice.say(
					event.getOption("text").getAsString(),
					event.getOption("voice").getAsString(),
					vc
					);

			event.reply("Speaking...").setEphemeral(true).queue();
			break;
		case "goon":
			String tmp = "";
			for(int i = 0;i<Main.maxGoon;i++) {
				tmp += "goon";
			}
			Main.sendMessage(tmp,event.getChannel());
			break;
		/*case "" :
		  
		 	break;
		 case "" :
		  
		 	break;
		 case "" :
		  
		 	break;
		 case "" :
		  
		 	break;
		 case "" :
		  
		 	break;
		 case "" :
		 	
		 	break;
		 case "" :
		  
		 	break;
		 case "" :
		  
		 	break;
		 case "" :
		 	
		 	break;
		 case "" :
		  
		 	break;
		 case "" :
		  
		 	break;
		 case "" :
		 
		 	break;
		  */
		}
	}
}
