package logic;

import java.io.*;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Iggacoin {

    private static final File COIN_FILE =
            new File("C:/Iggacorp Bot/Logs/Iggacoin.txt");

    // userid -> balance
    private final ConcurrentHashMap<String, BigInteger> balances =
            new ConcurrentHashMap<>();

    public Iggacoin() {
        load();
        syncGuildMembers();
        save();
    }

    /* ================= LOAD ================= */

    private synchronized void load() {
        try {

            if (!COIN_FILE.exists()) {
                COIN_FILE.getParentFile().mkdirs();
                COIN_FILE.createNewFile();
                return;
            }

            BufferedReader br = new BufferedReader(new FileReader(COIN_FILE));
            String line;

            while ((line = br.readLine()) != null) {

                if (!line.contains(":"))
                    continue;

                String[] parts = line.split(":");

                if (parts.length < 2)
                    continue;

                try {
                    balances.put(parts[0], new BigInteger(parts[1]));
                } catch (Exception ignored) {
                    System.out.println("Bad coin line skipped: " + line);
                }
            }

            br.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* ================= SAVE ================= */

    private synchronized void save() {

        try (BufferedWriter bw =
                     new BufferedWriter(new FileWriter(COIN_FILE))) {

            for (var e : balances.entrySet()) {
                bw.write(e.getKey() + ":" + e.getValue());
                bw.newLine();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* ================= MEMBER SYNC ================= */

    private synchronized void syncGuildMembers() {

        if (Main.guild == null)
            return;

        Main.guild.getMembers().forEach(m -> {

            String id = m.getId();

            balances.putIfAbsent(id, new BigInteger("1000"));
        });
    }

    /* ================= ECONOMY ================= */

    public BigInteger getBalance(String userID) {
        return balances.getOrDefault(userID, BigInteger.ZERO);
    }

    public synchronized void addCoins(String userID, BigInteger amt) {
        balances.merge(userID, amt, BigInteger::add);
        save();
    }

    public synchronized boolean subtractCoins(String userID, BigInteger amt) {

        BigInteger bal = getBalance(userID);

        if (bal.compareTo(amt) < 0)
            return false;

        balances.put(userID, bal.subtract(amt));
        save();
        return true;
    }

    public synchronized boolean transfer(
            String fromID,
            String toID,
            BigInteger amt
    ) {

        if (!subtractCoins(fromID, amt))
            return false;

        addCoins(toID, amt);
        return true;
    }

    /* ================= LEADERBOARD ================= */

    public synchronized String getLeaderboard() {

        ArrayList<Map.Entry<String, BigInteger>> list =
                new ArrayList<>(balances.entrySet());

        list.sort((a,b) -> b.getValue().compareTo(a.getValue()));

        StringBuilder sb = new StringBuilder();

        for (var e : list) {

            String name = e.getKey();

            if (Main.guild != null &&
                Main.guild.getMemberById(e.getKey()) != null) {

                name = Main.guild
                        .getMemberById(e.getKey())
                        .getUser()
                        .getName();
            }

            sb.append(name)
              .append(" - ")
              .append(e.getValue())
              .append("\n");
        }

        return sb.toString();
    }
}
