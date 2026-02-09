package logic;

import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class Iggacoin {

    private static final File COIN_FILE =
            new File("C:/Iggacorp Bot/Logs/Iggacoin.txt");

    private static final File LEADERBOARD_FILE =
            new File("C:/Iggacorp Bot/Logs/Leaderboard.txt");

    // username -> wallet
    private final ConcurrentHashMap<String, BigInteger> wallets =
            new ConcurrentHashMap<>();
    private final ArrayList<String> users = new ArrayList<>();
    public Iggacoin() {
        load();
        Main.guild.getMembers().forEach(e -> {
        	users.add(e.getUser().getName());
        });
    }

    /* ================= LOAD / SAVE ================= */

    private synchronized void load() {
        try {
            if (!COIN_FILE.exists()) {
                COIN_FILE.getParentFile().mkdirs();
                COIN_FILE.createNewFile();
                try(BufferedWriter write = new BufferedWriter(new FileWriter(COIN_FILE))){
                	Main.guild.getMembers().forEach(e->{
                		
                	});
                }
                return;
            }

            BufferedReader br = new BufferedReader(new FileReader(COIN_FILE));
            String line;

            while ((line = br.readLine()) != null) {
                // username:W1234
                String[] parts = line.split(":");
                if (parts.length != 2 || !parts[1].startsWith("W"))
                    continue;

                wallets.put(
                        parts[0],
                        new BigInteger(parts[1].substring(1))
                );
            }
            br.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized void save() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(COIN_FILE))) {
            for (var e : wallets.entrySet()) {
                bw.write(e.getKey() + ":W" + e.getValue());
                bw.newLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        updateLeaderboard();
    }

    /* ================= WALLET API ================= */

    public BigInteger getWallet(String user) {
        return wallets.getOrDefault(user, BigInteger.ZERO);
    }

    public synchronized void addToWallet(String user, BigInteger amt) {
        wallets.merge(user, amt, BigInteger::add);
        save();
    }

    public synchronized boolean subtractFromWallet(String user, BigInteger amt) {
        BigInteger bal = getWallet(user);
        if (bal.compareTo(amt) < 0)
            return false;

        wallets.put(user, bal.subtract(amt));
        save();
        return true;
    }

    public synchronized boolean transfer(String from, String to, BigInteger amt) {
        if (!subtractFromWallet(from, amt))
            return false;

        addToWallet(to, amt);
        return true;
    }

    /* ================= LEADERBOARD ================= */
    ArrayList<BigInteger> tmp = new ArrayList<>();
    public synchronized String getLeaderboard() {
    	try(BufferedReader read = new BufferedReader(new FileReader(LEADERBOARD_FILE))) {
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    	organize();
    	String guh = "";
    	
    	
    	
    	
    	return guh;
    }

    private void organize() {
    	
    	ArrayList<BigInteger> guh = new ArrayList<>();
		for(int i = 0; i<tmp.size(); i++) {
			
		}
	}

	private synchronized void updateLeaderboard() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(LEADERBOARD_FILE))) {
            bw.write(getLeaderboard());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

