package at.LobbySign.events;

import at.LobbySign.utils.Permissions;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import at.LobbySign.locations.LocationHandler;
import at.LobbySign.signs.SignHandler;
import at.LobbySign.signs.SignHolder;
import at.LobbySign.utils.Messages;

public class EventBlockBreak implements Listener {

	@EventHandler
	public void onBreakBlock(BlockBreakEvent event) {
		SignHolder sign = SignHandler.getSignHolderByLocation(event.getBlock().getLocation());
		
		if(event.getPlayer().getGameMode() != GameMode.CREATIVE) 
			return;

		if(sign != null) {

			if(!event.getPlayer().hasPermission(Permissions.DESTROY)) {
				event.getPlayer().sendMessage(Messages.MISSING_PERMISSION);
				return;
			}

			LocationHandler.removeSign(sign);
			SignHandler.SIGNS.remove(sign);
			event.getPlayer().sendMessage(Messages.PREFIX + "§cThe lobby sign has been removed!");
		}
	}
}
