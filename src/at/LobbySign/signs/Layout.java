package at.LobbySign.signs;

public class Layout {
	
	public static final String MOTD = "%motd%";
	public static final String ONLINE_PLAYERS = "%online%";
	public static final String MAX_PLAYERS = "%max%";

	public static final String SERVER_NAME = "%name%";
	public static final String VERSION = "%version%";
	public static final String ADDRESS = "%address%";
	public static final String PORT = "%port%";
	public static final String PING = "%ping%";


	private String[] lines = new String[4];

	public Layout(String[] lines) {
		this.lines = lines;
	}

	public String[] getLines() {
		return lines;
	}

	public void setLines(String[] lines) {
		this.lines = lines;
	}
	
	public static Layout getEmptyLayout() {
		String[] layout = new String[4];
		
		for(int i = 0; i < layout.length; i++) {
			layout[i] = "Line " + (i + 1);
		}
		
		return new Layout(layout);
	}
}
