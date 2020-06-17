package at.LobbySign.events;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.SQLException;

import at.LobbySign.utils.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import at.LobbySign.locations.LocationHandler;
import at.LobbySign.main.Main;
import at.LobbySign.serverstatus.ServerStatus;
import at.LobbySign.signs.Layout;
import at.LobbySign.signs.SignHandler;
import at.LobbySign.signs.SignHolder;
import at.LobbySign.utils.Messages;

public class EventPlayerInteract implements Listener {
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		
		Player player = event.getPlayer();
		
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Block block = event.getClickedBlock();
			if(block != null) {
				if(block.getType() == Material.WALL_SIGN) {
					Player p = event.getPlayer();
					
					if(SignHandler.SET_SIGN_QUEUE.containsKey(p)) {
						//create new sign
						event.setCancelled(true);
						String name = SignHandler.SET_SIGN_QUEUE.get(p);
						Layout layout = Layout.getEmptyLayout();
						ServerStatus status = ServerStatus.getDefaultServerStatus();
						
						LocationHandler.saveSign(name, block.getLocation(), layout, status.getIP(), status.getPort());
						SignHandler.SIGNS.add(new SignHolder(name, block.getLocation(), layout, status));
						SignHandler.SET_SIGN_QUEUE.remove(p);
						p.sendMessage("§3The sign (§aName: " + name + "§3) has been set successfully!");
					}else{
						ServerStatus s = SignHandler.getStatusByLoc(event.getClickedBlock().getLocation());
						if(s == null || !s.isOnline())
							return;

						if(!player.hasPermission(Permissions.USE)) {
							player.sendMessage(Messages.NO_PERMISSION_USE);
							return;
						}
						
						if(s.getOnlinePlayers() >= s.getMaxPlayers()) {
							player.sendMessage(Messages.SERVER_FULL);
							return;
						}
						
						ByteArrayOutputStream b = new ByteArrayOutputStream();
						DataOutputStream d = new DataOutputStream(b);
						try {
							d.writeUTF("Connect");
							d.writeUTF(s.getName());
						} catch(IOException e) {
							e.printStackTrace();
							return;
						}
						
						player.sendPluginMessage(Main.main, "BungeeCord", b.toByteArray());
						try {
							b.close();
							d.close();
						} catch (IOException e) {
							e.printStackTrace();
							return;
						}
					}
				}
			}
		}
	}
}
