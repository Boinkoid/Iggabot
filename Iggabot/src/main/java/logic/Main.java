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

	private static final String guildName = "Testing";
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
					.enableIntents(GatewayIntent.GUILD_MEMBERS)
					.addEventListeners(new SlashRouter()).build();
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
			guild.retrieveCommands().queue(commands -> {
			    commands.forEach(command -> command.delete().queue());
			});
			guild.updateCommands().addCommands(
					//Leaderboard
					Commands.slash("board", "Leaderboard"),
					//Money transfer
					Commands.slash("pay", "Pay coins")
					.addOption(OptionType.USER,"user", "target", true)
					.addOption(OptionType.STRING, "amount", "coins", true),
					//Gambling
					Commands.slash("dice", "Play dice")
					.addOption(OptionType.STRING, "bet", "bet amount", true)
					.addOption(OptionType.INTEGER, "sides", "dice sides", true)
					.addOption(OptionType.INTEGER, "guess", "your guess", true),
					//Goon command
					Commands.slash("goon","Goons")/*,
					
					
					
					Commands.slash("flip","Coin Flips")
					.addOption(OptionType.STRING, "bet", "bet amount", false)
					.addOption(OptionType.USER, "partner", "other player", false)
					.addOption(OptionType.INTEGER, "guess", "your guess", false),
					
					Commands.slash("flip","Coin Flips")
					.addOption(OptionType.STRING, "bet", "bet amount", false)
					.addOption(OptionType.USER, "partner", "other player", false)
					.addOption(OptionType.INTEGER, "guess", "your guess", false),

					/* Commands.slash("play", "Play music")
		            .addOption(OptionType.STRING, "query", "song/url", true),

		        	Commands.slash("tts", "Text to speech")
		            .addOption(OptionType.STRING, "text", "what to say", true)
		            .addOption(OptionType.STRING, "voice", "voice model", true)*/
					).queue();
			


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

	public static int maxGoon;
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

	// Console
	static class ConsoleModule {
		private static final List<String> history = new ArrayList<>();
		private static int historyIndex = -1;
		private static JTextArea textArea;
		private static PrintStream ps;

		public static void enableConsole() {
			JFrame frame = new JFrame("Java Server Console");
			frame.setSize(800, 800);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setLocationRelativeTo(null);

			textArea = new JTextArea();
			textArea.setEditable(false);
			textArea.setBackground(Color.BLACK);
			textArea.setForeground(Color.GREEN);
			textArea.setFont(new Font("Consolas", Font.PLAIN, 14));

			JTextField input = new JTextField();
			input.setBackground(Color.BLACK);
			input.setForeground(Color.GREEN);
			input.setFont(new Font("Consolas", Font.PLAIN, 14));

			frame.add(new JScrollPane(textArea), BorderLayout.CENTER);
			frame.add(input, BorderLayout.SOUTH);

			input.addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_UP && !history.isEmpty()) {
						historyIndex = Math.max(0, historyIndex - 1);
						input.setText(history.get(historyIndex));
					}
					if (e.getKeyCode() == KeyEvent.VK_DOWN && !history.isEmpty()) {
						historyIndex = Math.min(history.size() - 1, historyIndex + 1);
						input.setText(history.get(historyIndex));
					}
				}
			});

			input.addActionListener(e -> {
				String cmd = input.getText().trim();
				input.setText("");
				processCommand(cmd);
			});

			ps = new PrintStream(new OutputStream() {
				private final StringBuilder buffer = new StringBuilder();

				public void write(int b) {
					if (b == '\n') {
						append(buffer.toString() + "\n");
						buffer.setLength(0);
					} else {
						buffer.append((char) b);
					}
				}
			});
			System.setOut(ps);
			System.setErr(ps);

			frame.setVisible(true);
			System.out.println("Console ready.");
		}

		public static void injectCommand(String cmd) {
			SwingUtilities.invokeLater(() -> processCommand(cmd));
		}

		public static final String[] cmds = { "/exit", "/help", "/say", "/music", "/sing", "/music", "/setGoon", "/getDiceBigPot", "/getDiceSmallPot", "/setDiceBigPot", "/setDiceSmallPot", "/addDiceBigPot", "/addDiceSmallPot" };
		public static String txt;

		private static void processCommand(String input) {
			if (input.isEmpty())
				return;
			append("> " + input + "\n");
			history.add(input);
			historyIndex = history.size();

			if (prompt == 0) {// Regular commands
				if (input.startsWith(cmds[0])) {// exit
					System.exit(0);
				} else if (input.startsWith(cmds[1])) {// help
					System.out.println("Commands are " + getCMDS());
					txt = input.substring(cmds[1].length());
				} else if (input.startsWith(cmds[2])) {// say
					System.out.println("What channel would you like to join?\n" + getTXT());
					prompt = 2;
					txt = input.substring(cmds[2].length());
				} else if (input.startsWith(cmds[3])) {// speak
					System.out.println("What channel would you like to join?\n" + getVCS());
					prompt = 1;
					txt = input.substring(cmds[3].length());
				} else if (input.startsWith(cmds[4])) {// sing
					System.out.println("What channel would you like to join?\n" + getVCS());
					prompt = 1;
					txt = input.substring(cmds[4].length());
				} else if (input.startsWith(cmds[5])) {// music
					System.out.println("What channel would you like to join?\n" + getVCS());
					prompt = 1;
					txt = input.substring(cmds[5].length());
				} else if (input.startsWith(cmds[6])) {
					int in = Integer.parseInt(input.substring(cmds[6].length() + 1));
					setMaxGoon(in);
					System.out.println(in);
				} else if (input.startsWith(cmds[7])) { 
				    txt = input.substring(cmds[7].length() + 1);
				    System.out.println("Current Dice Jackpot is " + dice.getPot());

				} else if (input.startsWith(cmds[8])) { 
				    txt = input.substring(cmds[8].length() + 1);
				    System.out.println("Current Dice Jackpot is " + dice.getPot());

				} else if (input.startsWith(cmds[9])) { 
				    txt = input.substring(cmds[9].length() + 1);
				    dice.setPot(new BigInteger(txt));
				    System.out.println("Current Dice Jackpot is " + dice.getPot());

				} else if (input.startsWith(cmds[10])) {
				    txt = input.substring(cmds[10].length() + 1);
				    dice.setPot(new BigInteger(txt));
				    System.out.println("Current Dice Jackpot is " + dice.getPot());

				} else if (input.startsWith(cmds[11])) { 
				    txt = input.substring(cmds[11].length() + 1);
				    dice.addToPot(new BigInteger(txt));
				    System.out.println("Current Dice Jackpot is " + dice.getPot());

				} else if (input.startsWith(cmds[12])) {
				    txt = input.substring(cmds[12].length() + 1);
				    dice.addToPot(new BigInteger(txt));
				    System.out.println("Current Dice Jackpot is " + dice.getPot());
				} else {
					System.out.println("Command not recognized");
				}
			} else if (prompt == 1) {// VC music
				prompt = 0;
			} else if (prompt == 2) {// TXT
				prompt = 0;
				if (Integer.parseInt(input) != 0) {
					sendMessage(txt, bot.getGuildsByName(guildName, true).get(0).getTextChannels()
							.get(Integer.parseInt(input) - 1).getName());
				} else {
					System.out.println("Use just a number");
				}
			} else if (prompt == 3) {// VC voice
				prompt = 0;
				// voice.say(input);
			}
		}

		private static String getCMDS() {
			String guh = "";
			for (String i : cmds) {
				guh += i + ", ";
			}
			return guh;
		}

		private static void append(String text) {
			textArea.append(text);
			textArea.setCaretPosition(textArea.getDocument().getLength());
		}

		public static void killPS() {
			if (ps != null)
				ps.close();
		}
	}
}
