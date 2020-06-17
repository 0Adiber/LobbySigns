package at.LobbySign.signs;

import org.bukkit.Location;

import at.LobbySign.serverstatus.ServerStatus;

public class SignHolder {
	private String name;
	private Location location;
	private Layout layout;
	private ServerStatus serverStatus;
	
	private int currentLoadingDot = 0;

	public SignHolder(String name, Location location, Layout layout, ServerStatus serverStatus) {
		super();
		this.name = name;
		this.location = location;
		this.layout = layout;
		this.serverStatus = serverStatus;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public Layout getLayout() {
		return layout;
	}

	public void setLayout(Layout layout) {
		this.layout = layout;
	}

	public ServerStatus getServerStatus() {
		return serverStatus;
	}

	public void setServerStatus(ServerStatus serverStatus) {
		this.serverStatus = serverStatus;
	}
	
	public int getCurrentLoadingDot() {
		return currentLoadingDot;
	}
	
	public void raiseCurrentLoadingot() {
		currentLoadingDot++;
		if(currentLoadingDot == 4)
			currentLoadingDot = 1;
	}

	public String getFormattedLine(int i) {
		String result = layout.getLines()[i];
		
		result = result.replaceAll(Layout.MOTD, serverStatus.getMOTD());
		result = result.replaceAll(Layout.ONLINE_PLAYERS, serverStatus.getOnlinePlayers() + "");
		result = result.replaceAll(Layout.MAX_PLAYERS, serverStatus.getMaxPlayers() + "");
		result = result.replaceAll(Layout.ADDRESS, serverStatus.getIP());
		result = result.replaceAll(Layout.PING, serverStatus.getPing()+"");
		result = result.replaceAll(Layout.VERSION, serverStatus.getVersion());
		result = result.replaceAll(Layout.PORT, serverStatus.getPort()+"");
		result = result.replaceAll(Layout.SERVER_NAME, serverStatus.getName());
		
		return result.replaceAll("&", "§");
	}
}
