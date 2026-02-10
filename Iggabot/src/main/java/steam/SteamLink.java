package steam;

import net.dv8tion.jda.api.entities.User;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class SteamLink{

	private static final String API_KEY="8494A3B55BD2E5212AED68846410A2E3";
	private static final File LINK_FILE=new File("C:/Iggacorp Bot/Logs/Steam.txt");
	private static final File CACHE_FILE=new File("C:/Iggacorp Bot/Logs/GameCache.txt");
	private static final File MULTI_CACHE_FILE=new File("C:/Iggacorp Bot/Logs/MultiCache.txt");
	private static final File BANNED_FILE=new File("C:/Iggacorp Bot/Logs/Banned.txt");

	private final Map<String,String>discordToSteam=new HashMap<>();
	private final Map<String,String>steamToDiscord=new HashMap<>();
	private final Map<String,List<Integer>>gameCache=new HashMap<>();
	private final Map<Integer,Boolean>multiplayerCache=new HashMap<>();
	private final Set<Integer>bannedApps=new HashSet<>();
	private final Random rng=new Random();

	public SteamLink(){
		loadLinks();
		loadGameCache();
		loadMultiCache();
		loadBanned();
	}

	private void loadLinks(){
		try{
			if(!LINK_FILE.exists()){LINK_FILE.getParentFile().mkdirs();LINK_FILE.createNewFile();}
			BufferedReader br=new BufferedReader(new FileReader(LINK_FILE));
			String l;
			while((l=br.readLine())!=null){
				String[]p=l.split(":");
				if(p.length==2){
					discordToSteam.put(p[0],p[1]);
					steamToDiscord.put(p[1],p[0]);
				}}
		}catch(Exception ignored){}
	}

	private void saveLinks(){
		try(BufferedWriter bw=new BufferedWriter(new FileWriter(LINK_FILE))){
			for(var e:discordToSteam.entrySet()){
				bw.write(e.getKey()+":"+e.getValue());
				bw.newLine();
			}}
		catch(Exception ignored){}
	}

	private void loadGameCache(){
		try{
			if(!CACHE_FILE.exists())return;
			BufferedReader br=new BufferedReader(new FileReader(CACHE_FILE));
			String l;
			while((l=br.readLine())!=null){
				String[]p=l.split(":");
				List<Integer>apps=new ArrayList<>();
				if(p.length==2){
					for(String s:p[1].split(","))apps.add(Integer.parseInt(s));
					gameCache.put(p[0],apps);
				}}
		}catch(Exception ignored){}
	}

	private void saveGameCache(){
		try(BufferedWriter bw=new BufferedWriter(new FileWriter(CACHE_FILE))){
			for(var e:gameCache.entrySet()){
				bw.write(e.getKey()+":");
				for(int i=0;i<e.getValue().size();i++){
					bw.write(String.valueOf(e.getValue().get(i)));
					if(i<e.getValue().size()-1)bw.write(",");
				}
				bw.newLine();
			}}
		catch(Exception ignored){}
	}

	private void loadMultiCache(){
		try{
			if(!MULTI_CACHE_FILE.exists())return;
			BufferedReader br=new BufferedReader(new FileReader(MULTI_CACHE_FILE));
			String l;
			while((l=br.readLine())!=null){
				String[]p=l.split(":");
				multiplayerCache.put(Integer.parseInt(p[0]),Boolean.parseBoolean(p[1]));
			}}
		catch(Exception ignored){}
	}

	private void saveMultiCache(){
		try(BufferedWriter bw=new BufferedWriter(new FileWriter(MULTI_CACHE_FILE))){
			for(var e:multiplayerCache.entrySet()){
				bw.write(e.getKey()+":"+e.getValue());
				bw.newLine();
			}}
		catch(Exception ignored){}
	}

	private void loadBanned(){
		try{
			if(!BANNED_FILE.exists())return;
			BufferedReader br=new BufferedReader(new FileReader(BANNED_FILE));
			String l;
			while((l=br.readLine())!=null)bannedApps.add(Integer.parseInt(l.trim()));
		}catch(Exception ignored){}
	}

	public boolean link(String discordId,String input){
		String steam=resolve(input);
		if(steam==null)return false;
		if(discordToSteam.containsKey(discordId))return false;
		if(steamToDiscord.containsKey(steam))return false;
		discordToSteam.put(discordId,steam);
		steamToDiscord.put(steam,discordId);
		saveLinks();
		return true;
	}

	public boolean unlink(String discordId){
		String steam=discordToSteam.remove(discordId);
		if(steam==null)return false;
		steamToDiscord.remove(steam);
		saveLinks();
		return true;
	}

	public String getRandomSharedGame(List<User>users){
		List<Set<Integer>>sets=new ArrayList<>();
		for(User u:users){
			String steam=discordToSteam.get(u.getId());
			if(steam==null)return"User "+u.getName()+" not linked.";
			sets.add(new HashSet<>(getOwnedGames(steam)));
		}
		Set<Integer>shared=new HashSet<>(sets.get(0));
		for(Set<Integer>s:sets)shared.retainAll(s);
		shared.removeIf(a->bannedApps.contains(a)||!isMultiplayer(a));
		if(shared.isEmpty())return"No shared multiplayer games.";
		int pick=new ArrayList<>(shared).get(rng.nextInt(shared.size()));
		return getGameName(pick);
	}

	private List<Integer>getOwnedGames(String steam){
		if(gameCache.containsKey(steam))return gameCache.get(steam);
		List<Integer>apps=new ArrayList<>();
		try{
			URL url=new URL("https://api.steampowered.com/IPlayerService/GetOwnedGames/v1/?key="+API_KEY+"&steamid="+steam+"&include_appinfo=false");
			BufferedReader br=new BufferedReader(new InputStreamReader(url.openStream()));
			StringBuilder sb=new StringBuilder();
			br.lines().forEach(sb::append);
			JSONArray arr=new JSONObject(sb.toString()).getJSONObject("response").getJSONArray("games");
			for(int i=0;i<arr.length();i++)apps.add(arr.getJSONObject(i).getInt("appid"));
		}catch(Exception ignored){}
		gameCache.put(steam,apps);
		saveGameCache();
		return apps;
	}

	private boolean isMultiplayer(int appid){
		if(multiplayerCache.containsKey(appid))return multiplayerCache.get(appid);
		boolean multi=false;
		try{
			URL url=new URL("https://store.steampowered.com/api/appdetails?appids="+appid);
			BufferedReader br=new BufferedReader(new InputStreamReader(url.openStream()));
			StringBuilder sb=new StringBuilder();
			br.lines().forEach(sb::append);
			JSONArray cats=new JSONObject(sb.toString()).getJSONObject(String.valueOf(appid)).getJSONObject("data").getJSONArray("categories");
			for(int i=0;i<cats.length();i++){
				String d=cats.getJSONObject(i).getString("description").toLowerCase();
				if(d.contains("multi"))multi=true;
			}
		}catch(Exception ignored){}
		multiplayerCache.put(appid,multi);
		saveMultiCache();
		return multi;
	}

	private String getGameName(int appid){
		try{
			URL url=new URL("https://store.steampowered.com/api/appdetails?appids="+appid);
			BufferedReader br=new BufferedReader(new InputStreamReader(url.openStream()));
			StringBuilder sb=new StringBuilder();
			br.lines().forEach(sb::append);
			return new JSONObject(sb.toString()).getJSONObject(String.valueOf(appid)).getJSONObject("data").getString("name");
		}catch(Exception e){return"App "+appid;}
	}

	private String resolve(String input){
		try{
			input=input.trim();
			if(input.matches("\\d{17}"))return input;
			if(input.contains("steamcommunity.com")){
				if(input.contains("/profiles/"))return input.split("/profiles/")[1].split("/")[0];
				if(input.contains("/id/")){
					String vanity=input.split("/id/")[1].split("/")[0];
					return resolveVanity(vanity);
				}}
			if(input.contains("s.team/p/"))return null;
			if(input.matches("\\d+"))return resolveFriendCode(input);
			return resolveVanity(input);
		}catch(Exception e){return null;}
	}

	private String resolveVanity(String vanity)throws Exception{
		URL url=new URL("https://api.steampowered.com/ISteamUser/ResolveVanityURL/v1/?key="+API_KEY+"&vanityurl="+URLEncoder.encode(vanity,StandardCharsets.UTF_8));
		BufferedReader br=new BufferedReader(new InputStreamReader(url.openStream()));
		StringBuilder sb=new StringBuilder();
		br.lines().forEach(sb::append);
		JSONObject o=new JSONObject(sb.toString()).getJSONObject("response");
		if(o.getInt("success")==1)return o.getString("steamid");
		return null;
	}

	private String resolveFriendCode(String code){
		try{
			long id=Long.parseLong(code)+76561197960265728L;
			return String.valueOf(id);
		}catch(Exception e){return null;}
	}
}
