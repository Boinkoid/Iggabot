package logic;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class ConsoleModule extends Main{
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
		try {
			frame.setIconImage(ImageIO.read(new File("C:/Iggacorp Bot/SadTeto.jpg")));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		frame.setVisible(true);
		System.out.println("Console ready.");
	}

	public static void injectCommand(String cmd) {
		SwingUtilities.invokeLater(() -> processCommand(cmd));
	}

	public static final String[] cmds = { "/exit", "/help", "/say", "/music", "/sing", "/music", "/setGoon", "/getDiceBigPot", "/getDiceSmallPot", "/setDiceBigPot", "/setDiceSmallPot", "/addDiceBigPot", "/addDiceSmallPot", "/setDicePayout", "/getDicePayout", "/getSuggestions", "/delete"};
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
			} else if(input.startsWith(cmds[13])){
				txt = input.substring(cmds[13].length() + 1);
				dice.setDicePayout(txt);
			} else if(input.startsWith(cmds[14])){
				txt = input.substring(cmds[14].length() + 1);
				dice.getDicePayout();
			} else if(input.startsWith(cmds[15])){
				try(BufferedReader r = new BufferedReader(new FileReader("C:/Iggacorp Bot/Logs/Suggestions.txt"))){
					List<String> str = r.lines().toList();
					if(str.size()<=0) {
						System.out.println("No suggestions");
					}
					for(int i = 0; i<str.size(); i++) {
						System.out.println((i+1) + ". " + str.get(i));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if(input.startsWith(cmds[16])){
				txt = input.substring(cmds[16].length() + 1);
				if(!txt.toLowerCase().contains("all")) {
					try(BufferedReader r = new BufferedReader(new FileReader("C:/Iggacorp Bot/Logs/Suggestions.txt"))){
						BufferedWriter w = new BufferedWriter(new FileWriter("C:/Iggacorp Bot/Logs/Suggestions.txt",true));
						List<String> str = r.lines().toList();
						ArrayList<String> tmp = new ArrayList<>();
						for(int i = 0; i<str.size(); i++) {
							if(!((i-1)==Integer.parseInt(txt))) {
								tmp.add(str.get(i));
							}
						}
						w.write("");
						String[] temp = {""};
						tmp.forEach(e->{
							temp[0]+=e+"\n";
						});
						w.append(temp[0]);
						w.flush();
						List<String> string = r.lines().toList();
						for(int i = 0; i<string.size(); i++) {
							System.out.println((i+1) + ". " + string.get(i));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if(txt.toLowerCase().contains("all")) {
					try(BufferedWriter w = new BufferedWriter(new FileWriter("C:/Iggacorp Bot/Logs/Suggestions.txt"))) {
						
						w.write("");
						w.flush();
						System.out.println("Cleared suggestions");
					} catch(Exception e) {
						e.printStackTrace();
					}
				} else {
					System.out.println("/delete arguements must be a number or all");
				}
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