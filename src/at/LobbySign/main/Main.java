package at.LobbySign.main;

import at.LobbySign.locations.LocationHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import at.LobbySign.commands.SetSignCommand;
import at.LobbySign.commands.SignsReloadCommand;
import at.LobbySign.events.EventBlockBreak;
import at.LobbySign.events.EventPlayerInteract;
import at.LobbySign.signs.SignHandler;
import at.LobbySign.utils.Messages;

public class Main extends JavaPlugin {
	
	public static Main main;
	
	@Override
	public void onEnable() {
		main = this;
		
	    this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		
		initCommands();
		initEvents();

		LocationHandler.reloadFile();
		LocationHandler.saveCfg();

		SignHandler.load();
		Bukkit.getConsoleSender().sendMessage(Messages.PREFIX + "§aLobbySign has been loaded successfully!");
	}
	
	@Override
	public void onDisable() {
	
	}
	
	private void initEvents() {
		Bukkit.getPluginManager().registerEvents(new EventBlockBreak(), this);
		Bukkit.getPluginManager().registerEvents(new EventPlayerInteract(), this);
	}
	
	private void initCommands() {
		this.getCommand("setsign").setExecutor(new SetSignCommand());
		this.getCommand("signs").setExecutor(new SignsReloadCommand());
	}
	
}
