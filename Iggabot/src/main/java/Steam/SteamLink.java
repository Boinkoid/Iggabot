package Steam;

import net.dv8tion.jda.api.entities.User;
import org.json.*;

import java.io.*;
import java.net.*;
import java.util.*;

public class SteamLink {

    private static final String API =
    "8494A3B55BD2E5212AED68846410A2E3";

    private static final File LINK_FILE =
    new File("C:/Iggacorp Bot/Logs/Steam.txt");

    private static final File GAME_CACHE =
    new File("C:/Iggacorp Bot/Logs/SteamCache/games_cache.txt");

    private static final File MULTI_CACHE =
    new File("C:/Iggacorp Bot/Logs/SteamCache/multi_cache.txt");

    private final Map<String,String> links = new HashMap<>();
    private final Map<String,List<Integer>> gameCache = new HashMap<>();
    private final Map<Integer,Boolean> multiplayerCache = new HashMap<>();

    private final Random rng = new Random();

    public SteamLink(){
        loadLinks();
        loadCaches();
    }

    /* ================= LOAD LINKS ================= */

    private void loadLinks(){
        try{
            if(!LINK_FILE.exists()){
                LINK_FILE.getParentFile().mkdirs();
                LINK_FILE.createNewFile();
            }
            BufferedReader br=new BufferedReader(new FileReader(LINK_FILE));
            String line;
            while((line=br.readLine())!=null){
                if(!line.contains(":"))continue;
                String[] p=line.split(":");
                links.put(p[0],p[1]);
            }
            br.close();
        }catch(Exception e){e.printStackTrace();}
    }

    private void saveLinks(){
        try(BufferedWriter bw=new BufferedWriter(new FileWriter(LINK_FILE))){
            for(var e:links.entrySet()){
                bw.write(e.getKey()+":"+e.getValue());
                bw.newLine();
            }
        }catch(Exception e){e.printStackTrace();}
    }

    /* ================= CACHE LOAD ================= */

    private void loadCaches(){
        try{
            GAME_CACHE.getParentFile().mkdirs();
            GAME_CACHE.createNewFile();
            MULTI_CACHE.createNewFile();

            BufferedReader br=new BufferedReader(new FileReader(GAME_CACHE));
            String line;
            while((line=br.readLine())!=null){
                if(!line.contains(":"))continue;
                String[] p=line.split(":");
                List<Integer> list=new ArrayList<>();
                for(String g:p[1].split(","))
                    if(!g.isBlank())
                        list.add(Integer.parseInt(g));
                gameCache.put(p[0],list);
            }
            br.close();

            br=new BufferedReader(new FileReader(MULTI_CACHE));
            while((line=br.readLine())!=null){
                if(!line.contains(":"))continue;
                String[] p=line.split(":");
                multiplayerCache.put(
                Integer.parseInt(p[0]),
                Boolean.parseBoolean(p[1]));
            }
            br.close();

        }catch(Exception e){e.printStackTrace();}
    }

    private void saveGameCache(){
        try(BufferedWriter bw=
        new BufferedWriter(new FileWriter(GAME_CACHE))){
            for(var e:gameCache.entrySet()){
                bw.write(e.getKey()+":");
                for(int g:e.getValue())
                    bw.write(g+",");
                bw.newLine();
            }
        }catch(Exception e){e.printStackTrace();}
    }

    private void saveMultiCache(){
        try(BufferedWriter bw=
        new BufferedWriter(new FileWriter(MULTI_CACHE))){
            for(var e:multiplayerCache.entrySet()){
                bw.write(e.getKey()+":"+e.getValue());
                bw.newLine();
            }
        }catch(Exception e){e.printStackTrace();}
    }

    /* ================= LINK ================= */

    public boolean link(String discordId,String steam64){
        links.put(discordId,steam64);
        saveLinks();
        return true;
    }

    public boolean unlink(String discordId){
        if(!links.containsKey(discordId))
            return false;
        links.remove(discordId);
        saveLinks();
        return true;
    }

    /* ================= OWNED GAMES WITH CACHE ================= */

    @SuppressWarnings("deprecation")
	private List<Integer> getGames(String steam64){

        if(gameCache.containsKey(steam64))
            return gameCache.get(steam64);

        List<Integer> list=new ArrayList<>();

        try{
            URL url=new URL(
            "https://api.steampowered.com/IPlayerService/GetOwnedGames/v1/?key="
            +API+"&steamid="+steam64);

            BufferedReader br=
            new BufferedReader(
            new InputStreamReader(url.openStream()));

            StringBuilder sb=new StringBuilder();
            br.lines().forEach(sb::append);

            JSONArray arr=
            new JSONObject(sb.toString())
            .getJSONObject("response")
            .getJSONArray("games");

            for(int i=0;i<arr.length();i++)
                list.add(arr.getJSONObject(i)
                .getInt("appid"));

        }catch(Exception ignored){}

        gameCache.put(steam64,list);
        saveGameCache();
        return list;
    }

    /* ================= MULTIPLAYER CACHE ================= */

    private boolean isMultiplayer(int appid){

        if(multiplayerCache.containsKey(appid))
            return multiplayerCache.get(appid);

        boolean multi=false;

        try{
            URL url=new URL(
            "https://store.steampowered.com/api/appdetails?appids="
            +appid);

            BufferedReader br=
            new BufferedReader(
            new InputStreamReader(url.openStream()));

            StringBuilder sb=new StringBuilder();
            br.lines().forEach(sb::append);

            JSONArray cats=
            new JSONObject(sb.toString())
            .getJSONObject(String.valueOf(appid))
            .getJSONObject("data")
            .getJSONArray("categories");

            for(int i=0;i<cats.length();i++){
                String d=cats.getJSONObject(i)
                .getString("description")
                .toLowerCase();
                if(d.contains("multi"))
                    multi=true;
            }
        }catch(Exception ignored){}

        multiplayerCache.put(appid,multi);
        saveMultiCache();
        return multi;
    }

    /* ================= RANDOM SHARED GAME ================= */

    public String getRandomSharedGame(List<User> users){

        List<Set<Integer>> all=new ArrayList<>();

        for(User u:users){
            String steam64=links.get(u.getId());
            if(steam64==null)return null;
            all.add(new HashSet<>(getGames(steam64)));
        }

        Set<Integer> shared=new HashSet<>(all.get(0));
        for(Set<Integer>s:all)
            shared.retainAll(s);

        List<Integer> multi=new ArrayList<>();
        for(int app:shared)
            if(isMultiplayer(app))
                multi.add(app);

        if(multi.isEmpty())
            return null;

        return "https://store.steampowered.com/app/"
        +multi.get(rng.nextInt(multi.size()));
    }
}
