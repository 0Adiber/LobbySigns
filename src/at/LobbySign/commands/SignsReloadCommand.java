package at.LobbySign.commands;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.SQLException;

import at.LobbySign.utils.Permissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import at.LobbySign.locations.LocationHandler;
import at.LobbySign.main.Main;
import at.LobbySign.signs.SignHandler;
import at.LobbySign.signs.SignHolder;
import at.LobbySign.utils.Messages;

public class SignsReloadCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if(!(sender instanceof Player)) {
			LocationHandler.reloadFile();
			SignHandler.load();
			sender.sendMessage(Messages.SIGNS_RELOADED);
			return true;
		}

		Player player = (Player)sender;

		if(!player.hasPermission(Permissions.RELOAD)) {
			player.sendMessage(Messages.MISSING_PERMISSION);
			return false;
		}

		if(args.length == 1 && args[0].equalsIgnoreCase("reload")) {
			LocationHandler.reloadFile();
			SignHandler.load();
			player.sendMessage(Messages.SIGNS_RELOADED);
			return true;
		}
		
		player.sendMessage(Messages.WRONG_COMMAND);
		return false;
	}

}
