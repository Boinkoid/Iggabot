package gambling;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.io.*;
import java.math.BigInteger;
import java.util.Random;

public class Dice {

    private static final File POT_FILE =
            new File("C:/Iggacorp Bot/Logs/pot/DicePot.txt");

    private BigInteger pot = BigInteger.ZERO;
    private final Random rng = new Random();

    public Dice() {
        loadPot();
    }

    /* ================= POT IO ================= */

    private synchronized void loadPot() {
        try {
            if (!POT_FILE.exists()) {
                POT_FILE.getParentFile().mkdirs();
                POT_FILE.createNewFile();
                savePot();
                return;
            }

            try (BufferedReader br = new BufferedReader(new FileReader(POT_FILE))) {
                String line = br.readLine();
                if (line != null && !line.isBlank())
                    pot = new BigInteger(line);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized void savePot() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(POT_FILE))) {
            bw.write(pot.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* ================= POT API ================= */

    public synchronized BigInteger getPot() {
        return pot;
    }

    public synchronized void setPot(BigInteger amt) {
        pot = amt.max(BigInteger.ZERO);
        savePot();
    }

    public synchronized void addToPot(BigInteger amt) {
        pot = pot.add(amt);
        savePot();
    }

    public synchronized BigInteger drainPot() {
        BigInteger win = pot;
        pot = BigInteger.ZERO;
        savePot();
        return win;
    }
    public synchronized void setDicePayout(String stuff) {
    	System.out.println(stuff);
    	String one = stuff.split("/")[0];
    	String two = stuff.split("/")[1];
    	System.out.println(one + "\n" + two);
    	oddTop = Integer.parseInt(one);
    	oddBot = Integer.parseInt(two);
    }
    public synchronized void getDicePayout() {
    	System.out.println(oddTop + "/" + oddBot);
    }
    /* ================= SLASH HANDLER ================= */
    int oddTop = 6;
    int oddBot = 4;
    public void handle(SlashCommandInteractionEvent event) {

        int sides = event.getOption("sides", 6, OptionMapping::getAsInt);
        BigInteger bet = new BigInteger(
                event.getOption("bet", "0", OptionMapping::getAsString)
        );

        OptionMapping guessOpt = event.getOption("guess");
        OptionMapping eoOpt = event.getOption("evenodd");

        int roll = rng.nextInt(sides) + 1;
        boolean win = false;

        // Number guess
        if (guessOpt != null) {
            int guess = guessOpt.getAsInt();
            win = (roll == guess);
        }

        // Even / Odd
        else if (eoOpt != null) {
            boolean even = eoOpt.getAsString().equalsIgnoreCase("even");
            win = even == (roll % 2 == 0);
        }

        // Invalid usage
        else {
            event.reply("You must guess a **number** or **even/odd**.")
                 .setEphemeral(true)
                 .queue();
            return;
        }

        // Resolve
        if (win) {
            BigInteger winnings = bet.multiply(BigInteger.valueOf(6)).divide(BigInteger.valueOf(4));
            BigInteger jackpot = drainPot();

            event.reply(
                    "🎲 Rolled **" + roll + "** — YOU WIN!\n" +
                    "💰 Winnings: " + winnings +
                    (jackpot.signum() > 0 ? "\n🏆 Jackpot: " + jackpot : "")
            ).queue();
        } else {
            addToPot(bet);
            event.reply("🎲 Rolled **" + roll + "** — You lost.").queue();
        }
    }
}
