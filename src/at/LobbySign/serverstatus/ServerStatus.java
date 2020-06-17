package at.LobbySign.serverstatus;

import java.io.IOException;

public class ServerStatus {
  private String name;
  
  private String ip;
  
  private String status;
  
  private ServerListPing pinger;

  private int port;
  
  private int playercount;
  
  private int maxpcount;
  
  private boolean online;

  private String version;

  private int ping;
  
  public ServerStatus(String name, String ip, int port) {
    this.name = name;
    this.ip = ip;
    this.port = port;
    this.pinger = new ServerListPing(ip, port);
  }
  
  public String getName() {
    return this.name;
  }
  
  public String getIP() {
    return this.ip;
  }
  
  public boolean isOnline() {
    return this.online;
  }
  
  public int getPort() {
    return this.port;
  }
  
  public int getOnlinePlayers() {
    return this.playercount;
  }
  
  public int getMaxPlayers() {
    return this.maxpcount;
  }
  
  public String getMOTD() {
    return this.status;
  }

  public String getVersion() {
    return version;
  }

  public int getPing() {
    return ping;
  }

  public void update() {
    try {
      ServerListPing.StatusResponse sr = this.pinger.fetchData();
      if (sr == null) {
        this.online = false;
        return;
      } 
      this.online = true;
      this.playercount = sr.getPlayers().getOnline();
      this.maxpcount = sr.getPlayers().getMax();
      this.status = sr.getDescription();
      this.version = sr.getVersion().getName();
      this.ping = sr.getTime();

    } catch (IOException e) {
      this.online = false;
      e.printStackTrace();
    } 
  }
  
  public static ServerStatus getDefaultServerStatus() {
    return new ServerStatus("test", "127.0.0.1", 25565);
  }
}
