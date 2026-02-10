package Steam;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class SteamLink {

    private static final String API_KEY = "PUT_YOUR_STEAM_API_KEY_HERE";

    private static final File file =
            new File("C:/Iggacorp Bot/Logs/Steam.txt");

    private final ArrayList<Profile> profile =
            new ArrayList<>();

    public SteamLink(){
        makeFile();
        loadFiles();
    }

    private void makeFile(){
        try{
            file.getParentFile().mkdirs();
            if(!file.exists())
                file.createNewFile();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    // =========================
    // FILE LOADING
    // =========================

    public void loadFiles(){

        profile.clear();

        try(BufferedReader read =
                new BufferedReader(new FileReader(file))){

            String line;

            while((line=read.readLine())!=null){

                String[] split = line.split(":");
                if(split.length<2) continue;

                String disc = split[0];
                String steam = split[1];

                profile.add(
                        new Profile(disc,steam,new ArrayList<>())
                );
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void save(){

        try(BufferedWriter write =
                new BufferedWriter(new FileWriter(file,false))){

            for(Profile p:profile){

                write.write(
                        p.getDiscName()
                        +":"+p.getSteam()
                );

                write.newLine();
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    // =========================
    // LINK / UNLINK
    // =========================

    public void link(String discordID,String input){

        String steamID = resolve(input);

        if(steamID==null)
            return;

        unlink(discordID);

        profile.add(
                new Profile(discordID,steamID,new ArrayList<>())
        );

        save();
    }

    public void unlink(String discordID){

        profile.removeIf(
                p->p.getDiscName().equals(discordID)
        );

        save();
    }

    // =========================
    // COMMAND: /games
    // =========================

    public void pickGame(
            ArrayList<String> users,
            SlashCommandInteractionEvent event){

        try{

            List<Set<String>> libraries =
                    new ArrayList<>();

            for(String u:users){

                Profile p = find(u);
                if(p==null) continue;

                libraries.add(
                        new HashSet<>(
                                getOwnedGames(p.getSteam())
                        )
                );
            }

            if(libraries.isEmpty()){
                event.reply("No linked users.")
                     .setEphemeral(true).queue();
                return;
            }

            Set<String> shared =
                    new HashSet<>(libraries.get(0));

            for(Set<String> s:libraries)
                shared.retainAll(s);

            if(shared.isEmpty()){
                event.reply("No shared multiplayer games.")
                     .queue();
                return;
            }

            String[] arr =
                    shared.toArray(new String[0]);

            String pick =
                    arr[new Random().nextInt(arr.length)];

            event.reply("🎮 Play: **"+pick+"**").queue();

        }catch(Exception e){
            e.printStackTrace();
            event.reply("Steam failed").queue();
        }
    }

    private Profile find(String disc){
        for(Profile p:profile)
            if(p.getDiscName().equals(disc))
                return p;
        return null;
    }

    // =========================
    // STEAM RESOLVE
    // =========================

    private String resolve(String input){

        try{

            // steam64 direct
            if(input.matches("\\d{17}"))
                return input;

            // extract from url
            if(input.contains("/profiles/"))
                return input.split("/profiles/")[1]
                        .split("/")[0];

            // vanity url
            if(input.contains("/id/"))
                input=input.split("/id/")[1]
                        .split("/")[0];

            // friend code (simple convert)
            if(input.matches("\\d+"))
                return input;

            // vanity lookup
            String url =
            "https://api.steampowered.com/ISteamUser/ResolveVanityURL/v1/"
            +"?key="+API_KEY
            +"&vanityurl="+input;

            String json = readURL(url);

            if(json.contains("steamid")){
                return json.split("\"steamid\":\"")[1]
                        .split("\"")[0];
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }

    // =========================
    // GET OWNED MULTIPLAYER GAMES
    // =========================

    private List<String> getOwnedGames(String steamID){

        List<String> games=new ArrayList<>();

        try{

            String url =
            "https://api.steampowered.com/IPlayerService/GetOwnedGames/v1/"
            +"?key="+API_KEY
            +"&steamid="+steamID
            +"&include_appinfo=true";

            String json = readURL(url);

            String[] names=json.split("\"name\":\"");
            String[] ids=json.split("\"appid\":");

            int idx=1;

            for(int i=1;i<names.length;i++){

                String name =
                        names[i].split("\"")[0];

                int appid =
                        Integer.parseInt(
                                ids[idx++].split(",")[0]);

                if(isMultiplayer(appid))
                    games.add(name);
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        return games;
    }

    private boolean isMultiplayer(int appid){

        try{

            String json = readURL(
            "https://store.steampowered.com/api/appdetails?appids="
            +appid);

            return json.contains("\"id\":1")
                || json.contains("\"id\":9")
                || json.contains("\"id\":38")
                || json.contains("\"id\":24")
                || json.contains("\"id\":27");

        }catch(Exception e){
            return false;
        }
    }

    // =========================
    // HTTP HELPER
    // =========================

    private String readURL(String urlStr)
            throws Exception{

        HttpURLConnection conn =
                (HttpURLConnection)
                        new URL(urlStr).openConnection();

        BufferedReader br =
                new BufferedReader(
                        new InputStreamReader(
                                conn.getInputStream()));

        String out =
                br.lines().reduce("",String::concat);

        br.close();

        return out;
    }
}
