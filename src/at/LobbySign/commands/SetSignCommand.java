package at.LobbySign.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import at.LobbySign.signs.SignHandler;
import at.LobbySign.utils.Messages;
import at.LobbySign.utils.Permissions;


public class SetSignCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage(String.format(Messages.YOU_NEED_TO_BE_A_PLAYER, cmd.getName()));
			return false;
		}
		
		Player p = (Player)sender;

		if(!p.hasPermission(Permissions.SETUP)) {
			p.sendMessage(Messages.MISSING_PERMISSION);
			return false;
		}
		
		if(args.length != 1) {
			p.sendMessage(Messages.PREFIX + "§cUse §e/setsign <id>");
			return false;
		}
		
		if(!SignHandler.SET_SIGN_QUEUE.containsKey(p)) {
			SignHandler.SET_SIGN_QUEUE.put(p, args[0]);
		}
		
		p.sendMessage(Messages.PREFIX + "§aClick on the sign now!");
		return true;
	}
}
