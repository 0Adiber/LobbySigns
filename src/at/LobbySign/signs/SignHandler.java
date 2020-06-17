package at.LobbySign.signs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import at.LobbySign.locations.LocationHandler;
import at.LobbySign.main.Main;
import at.LobbySign.serverstatus.ServerStatus;

public class SignHandler {

	public static List<SignHolder> SIGNS = new ArrayList<>();
	public static Map<Player, String> SET_SIGN_QUEUE = new HashMap<>();
	private static int task = Integer.MIN_VALUE;
	
	private static final int REFRESH_TIME = 20;
	
	@SuppressWarnings("deprecation")
	public static void load() {
		try {
			Bukkit.getServer().getScheduler().cancelTask(task);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		SET_SIGN_QUEUE.clear();
		SIGNS.clear();
		SIGNS.addAll(LocationHandler.getSavedSigns());
			
		task = Bukkit.getScheduler().scheduleAsyncRepeatingTask(Main.main, new Runnable() {
			
			@Override
			public void run() {
				for(SignHolder sign : SIGNS) {
					sign.getServerStatus().update();
					
					Block b = sign
							.getLocation()
							.getBlock();
					
					Sign signBlock = (Sign) b.getState();
					if(sign.getServerStatus().isOnline()) {
						for(int i = 0; i < sign.getLayout().getLines().length; i++) {
							signBlock.setLine(i, sign.getFormattedLine(i));
						}
					} else {
						String ld = "";
						for(int i = 0; i<sign.getCurrentLoadingDot(); i++) {
							ld+=".";
						}
						sign.raiseCurrentLoadingot();
						signBlock.setLine(0, "••••••••••••••••••••••••");
						signBlock.setLine(1, "§4OFFLINE");
						signBlock.setLine(2, "§0Loading" + ld);
						signBlock.setLine(3, "••••••••••••••••••••••••");
					}
					
					signBlock.update();
					
				}
			}
		}, 20L, REFRESH_TIME);
	}
	
	public static ServerStatus getStatusByLoc(Location loc) {
		for(SignHolder h : SIGNS) {
			if(h.getLocation().equals(loc))
				return h.getServerStatus();
		}
		return null;
	}
	
	public static boolean existsSign(Location location) {
		for(SignHolder sign : SIGNS) {
			if(sign.getLocation().getBlockX() == location.getBlockX() &&
					sign.getLocation().getBlockY() == location.getBlockY() &&
					sign.getLocation().getBlockZ() == location.getBlockZ()) {
				return true;
			}
		}
		
		return false;
	}
	
	public static SignHolder getSignHolderByLocation(Location location) {
		for(SignHolder sign : SIGNS) {
			if(sign.getLocation().getBlockX() == location.getBlockX() &&
					sign.getLocation().getBlockY() == location.getBlockY() &&
					sign.getLocation().getBlockZ() == location.getBlockZ()) {
				return sign;
			}
		}
		
		return null;
	}
}
