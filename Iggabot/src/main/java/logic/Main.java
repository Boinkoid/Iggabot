package logic;
import gambling.Dice;
// Reader
import java.io.*;
import java.math.BigInteger;
// Networking
import java.net.*;
// JFrame
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
// JDA
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Main{

	/*
	 * Installer https://discord.com/oauth2/authorize?client_id=1460785433314066566&
	 * permissions=8&integration_type=0&scope=bot+applications.commands
	 */

	public static final String guildName = "Testing";
	private static Voice voice;
	static JDA bot;
	public static int prompt = 0;
	static String log = "";
	public static ProcessBuilder pb;
	private static final Map<Long, Music> vcMusicMap = new HashMap<>();
	public static Iggacoin $;
	static Guild guild; 
	public static Dice dice;
	public static void main(String[] args) {
		dice = new Dice();
		System.setProperty("jna.library.path", ".");
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void start() {
				try (BufferedWriter writer = new BufferedWriter(new FileWriter("C:/Iggacorp Bot/Logs/log.txt"))) {
					writer.write(log);
					writer.flush();
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("Writer fucked");
					System.out.println(log);
				}
			}
		});
		try (BufferedReader reader = new BufferedReader(new FileReader("C:/Iggacorp Bot/Logs/key.txt"))) {
			bot = JDABuilder.createDefault(reader.readLine())
					.enableIntents(GatewayIntent.MESSAGE_CONTENT)
					.enableIntents(GatewayIntent.GUILD_MEMBERS).build();
			bot.awaitReady(); // IMPORTANT

			Optional<Guild> guildOpt = bot.getGuilds()
					.stream()
					.filter(g -> g.getName().equalsIgnoreCase("Testing"))
					.findFirst();

			if (guildOpt.isEmpty()) {
				System.err.println("ERROR: Guild '"+guildName+"' not found. Bot is in:");
				bot.getGuilds().forEach(g -> System.err.println(" - " + g.getName()));
				return;
			}

			guild = guildOpt.get();
			bot.addEventListener(new SlashRouter());
			guild.retrieveCommands().queue(commands -> {
				commands.forEach(command -> command.delete());
			});
			guild.updateCommands().addCommands(
					// Leaderboard
					Commands.slash("board", "Leaderboard"),

					// Money transfer
					Commands.slash("pay", "Pay coins")
					.addOption(OptionType.USER, "user", "target", true)
					.addOption(OptionType.STRING, "amount", "coins", true),

					// Gambling
					Commands.slash("dice", "Play dice")
					.addOption(OptionType.INTEGER, "bet", "bet amount", true)
					.addOption(OptionType.INTEGER, "sides", "dice sides", true)
					.addOption(OptionType.STRING, "guess", "number OR even OR odd", true),

					// Goon
					Commands.slash("goon", "Goons"),

					// Steam
					Commands.slash("link", "Links your Steam Account")
					.addOption(OptionType.STRING, "account", "friend code or account link", true),

					Commands.slash("unlink", "Unlinks your Steam Account"),

					Commands.slash("games", "Pick a shared game owned by everyone")
				    .addOption(OptionType.USER, "user1", "user", true)
				    .addOption(OptionType.USER, "user2", "user", true)
				    .addOption(OptionType.USER, "user3", "user", false)
				    .addOption(OptionType.USER, "user4", "user", false)
				    .addOption(OptionType.USER, "user5", "user", false)
				    .addOption(OptionType.USER, "user6", "user", false)
				    .addOption(OptionType.USER, "user7", "user", false)
				    .addOption(OptionType.USER, "user8", "user", false)
				    .addOption(OptionType.USER, "user9", "user", false)
				    .addOption(OptionType.USER, "user10", "user", false)
				    .addOption(OptionType.USER, "user11", "user", false)
				    .addOption(OptionType.USER, "user12", "user", false)
				    .addOption(OptionType.USER, "user13", "user", false)
				    .addOption(OptionType.USER, "user14", "user", false)
				    .addOption(OptionType.USER, "user15", "user", false)
				    .addOption(OptionType.USER, "user16", "user", false)
				    .addOption(OptionType.USER, "user17", "user", false)
				    .addOption(OptionType.USER, "user18", "user", false)
				    .addOption(OptionType.USER, "user19", "user", false)
				    .addOption(OptionType.USER, "user20", "user", false)
				    .addOption(OptionType.USER, "user21", "user", false)
				    .addOption(OptionType.USER, "user22", "user", false)
				    .addOption(OptionType.USER, "user23", "user", false)
				    .addOption(OptionType.USER, "user24", "user", false)
				    .addOption(OptionType.USER, "user25", "user", false),
				    
					Commands.slash("coom", "Goodnight Coom"),

					Commands.slash("suggest", "Make a suggestion")
					.addOption(OptionType.STRING, "suggestion", "Help improve Iggabot", true)/*,

					Commands.slash().addOption(),

					Commands.slash().addOption(),

					Commands.slash().addOption(),

					Commands.slash().addOption(),

					/* Commands.slash("play", "Play music")
		            .addOption(OptionType.STRING, "query", "song/url", true),

		        	Commands.slash("tts", "Text to speech")
		            .addOption(OptionType.STRING, "text", "what to say", true)
		            .addOption(OptionType.STRING, "voice", "voice model", true)*/
					).queue();
			guild.updateCommands();
		} catch (Exception e) {
			ConsoleModule.killPS();
			log += e.getStackTrace();
			e.printStackTrace();
			System.exit(0);
		}
		ConsoleModule.enableConsole();
		new ServerThread().start();
		voice = new Voice(guild);
		$ = new Iggacoin();
		Syncer.start();
	}

	public static Music getMusicForVC(VoiceChannel vc) {
		return vcMusicMap.computeIfAbsent(vc.getIdLong(), id -> new Music(vc.getGuild()));
	}

	private static final int limit = 2000;
	public static void sendMessage(String longContent, MessageChannelUnion channel) {
		for (int i = 0; i < longContent.length(); i += limit) {
			int endIndex = Math.min(i + limit, longContent.length());
			String chunk = longContent.substring(i, endIndex);
			channel.sendMessage(chunk).queue();
		}
	}

	public static void sendMessage(String str, String channel) {
		bot.getGuilds().stream().filter(g -> g.getName()
				.equalsIgnoreCase(guildName)).findFirst().orElseThrow()
		.getTextChannelsByName(channel, true).get(0).sendMessage(str).queue();
	}

	public static int maxGoon = 20;
	public static void setMaxGoon(int i) {
		maxGoon = i;
	}

	public void onReady(ReadyEvent event) {
		List<Guild> guilds = bot.getGuildsByName(guildName, true);
		if (!guilds.isEmpty()) {
			new Music(guilds.get(0));
			System.out.println("Music initialized for guild: " + guildName);
		} else {
			System.out.println("Guild not found: " + guildName);
		}
	}

	public static String getVCS() {
		String guh = "";
		List<VoiceChannel> VC = bot.getGuildsByName(guildName, true).get(0).getVoiceChannels();
		for (int i = 0; i < VC.size(); i++) {
			guh += i + ". " + VC.get(i).getName() + "\n";
		}

		return guh;
	}

	public static String getTXT() {
		String guh = "";
		List<TextChannel> VC = bot.getGuildsByName(guildName, true).get(0).getTextChannels();
		for (int i = 1; i - 1 < VC.size(); i++) {
			guh += i + ". " + VC.get(i - 1).getName() + "\n";
		}

		return guh;
	}

	// Console Server
	static class ServerThread extends Thread {
		private static final int PORT = 5050;

		public void run() {
			try (ServerSocket server = new ServerSocket(PORT)) {
				System.out.println("Remote console server listening on " + PORT);
				while (true)
					new ClientHandler(server.accept()).start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// Console Server
	static class ClientHandler extends Thread {
		private final Socket socket;

		ClientHandler(Socket socket) {
			this.socket = socket;
		}

		public void run() {
			try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
				String line;
				while ((line = in.readLine()) != null)
					ConsoleModule.injectCommand(line);
			} catch (IOException e) {
				System.out.println("Remote client disconnected");
			}
		}
	}
}
