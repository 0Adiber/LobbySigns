package at.LobbySign.locations;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import at.LobbySign.serverstatus.ServerStatus;
import at.LobbySign.signs.Layout;
import at.LobbySign.signs.SignHandler;
import at.LobbySign.signs.SignHolder;

public class LocationHandler {

	public static File file;
	public static FileConfiguration cfg;
	
	public static void reloadFile() {
		file = new File("plugins/LobbySigns", "signs.yml");
		cfg = YamlConfiguration.loadConfiguration(file);
	}
	
	public static void saveCfg() {
		try {
			cfg.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void saveSign(String name, Location loc, Layout layout, String address, int port) {
		cfg.set(name + ".address", address + ":" + port);
		cfg.set(name + ".name", "bungee-server-name");
		
		cfg.set(name + ".location.world", loc.getWorld().getName());
		cfg.set(name + ".location.x", Double.valueOf(loc.getX()));
		cfg.set(name + ".location.y", Double.valueOf(loc.getY()));
		cfg.set(name + ".location.z", Double.valueOf(loc.getZ()));
		
		for(int i = 0; i < layout.getLines().length; i++) {
			cfg.set(name + ".layout.line_" + (i + 1), layout.getLines()[i]);
		}
		
		saveCfg();
	}
	
	public static SignHolder getSign(String name) {
		String cfgName = name;
		
		Location loc;
		try {
			World w = Bukkit.getWorld(cfg.getString(cfgName + ".location.world"));
			double x = cfg.getDouble(cfgName + ".location.x");
			double y = cfg.getDouble(cfgName + ".location.y");
			double z = cfg.getDouble(cfgName + ".location.z");

			loc = new Location(w, x, y, z);
			loc.setYaw(0);
			loc.setPitch(0);
		} catch (Exception ex) {
			ex.printStackTrace();
			loc = null;
		}
		
		String[] lines = new String[4];
		
		for(int i = 0; i < 4; i++) {
			lines[i] = cfg.getString(cfgName + ".layout.line_" + (i + 1));
		}
		
		String[] address = cfg.getString(cfgName + ".address").split(":");
		
		String nahme = cfg.getString(cfgName + ".name");
		
		return new SignHolder(name, loc, new Layout(lines), new ServerStatus(nahme, address[0], Integer.parseInt(address[1])));
	}
	
	public static List<SignHolder> getSavedSigns(){
		List<SignHolder> signs = new ArrayList<>();
		
		try {
			Set<String> savedSigns = cfg.getConfigurationSection("").getKeys(false);
			
			for(String signEntry : savedSigns) {
				signs.add(getSign(signEntry));	
			}
		}catch(NullPointerException e) {
			e.printStackTrace();
			return signs;
		}
		
		return signs;
	}
	
	public static void removeSign(SignHolder sign) {
		cfg.set("signs." + sign.getName(), null);
		saveCfg();
	}
}
