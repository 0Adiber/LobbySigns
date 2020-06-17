package at.LobbySign.serverstatus;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.List;

public class ServerListPing {
	private InetSocketAddress host;

	private int timeout = 3000;

	private Gson gson = new Gson();

	public ServerListPing(String ip, int port) {
		this.host = new InetSocketAddress(ip, port);
	}

	public void setAddress(InetSocketAddress host) {
		this.host = host;
	}

	public InetSocketAddress getAddress() {
		return this.host;
	}

	void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	int getTimeout() {
		return this.timeout;
	}

	public int readVarInt(DataInputStream in) throws IOException {
		int k, i = 0;
		int j = 0;
		do {
			k = in.readByte();
			i |= (k & 0x7F) << j++ * 7;
			if (j > 5)
				throw new RuntimeException("VarInt too big");
		} while ((k & 0x80) == 128);
		return i;
	}

	public void writeVarInt(DataOutputStream out, int paramInt) throws IOException {
		while (true) {
			if ((paramInt & 0xFFFFFF80) == 0) {
				out.writeByte(paramInt);
				return;
			}
			out.writeByte(paramInt & 0x7F | 0x80);
			paramInt >>>= 7;
		}
	}

	//https://gist.github.com/zh32/7190955
	
	public StatusResponse fetchData() throws IOException {
		Socket socket = new Socket();
		socket.setSoTimeout(this.timeout);
		try {
			socket.connect(this.host, this.timeout);
		} catch (ConnectException e) {
			socket.close();
			return null;
		} catch(SocketTimeoutException e) {
			socket.close();
			return null;
		}
		OutputStream outputStream = socket.getOutputStream();
		DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
		InputStream inputStream = socket.getInputStream();
		InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream handshake = new DataOutputStream(b);
		handshake.writeByte(0);
		writeVarInt(handshake, 4);
		writeVarInt(handshake, this.host.getHostString().length());
		handshake.writeBytes(this.host.getHostString());
		handshake.writeShort(this.host.getPort());
		writeVarInt(handshake, 1);
		writeVarInt(dataOutputStream, b.size());
		dataOutputStream.write(b.toByteArray());
		dataOutputStream.writeByte(1);
		dataOutputStream.writeByte(0);
		DataInputStream dataInputStream = new DataInputStream(inputStream);
		int size = readVarInt(dataInputStream);
		int id = readVarInt(dataInputStream);
		if (id == -1)
			throw new IOException("Premature end of stream.");
		if (id != 0)
			throw new IOException("Invalid packetID");
		int length = readVarInt(dataInputStream);
		if (length == -1)
			throw new IOException("Premature end of stream.");
		if (length == 0)
			throw new IOException("Invalid string length.");
		byte[] in = new byte[length];
		dataInputStream.readFully(in);
		String json = new String(in);

		long now = System.currentTimeMillis();
		dataOutputStream.writeByte(9);
		dataOutputStream.writeByte(1);
		dataOutputStream.writeLong(now);
		readVarInt(dataInputStream);
		long pingtime = System.currentTimeMillis()-now;
		id = readVarInt(dataInputStream);
		if (id == -1)
			throw new IOException("Premature end of stream.");
		if (id != 1)
			throw new IOException("Invalid packetID");

		StatusResponse response = null;
		try {
			response = (StatusResponse) this.gson.fromJson(json, StatusResponse.class);
		}catch(JsonSyntaxException e) {
			StatusResponse15 r = (StatusResponse15) this.gson.fromJson(json, StatusResponse15.class);
			response = new StatusResponse(r.getDescription().getText(), r.getPlayers(), r.getVersion(), r.getFavicon());
		}

		response.setTime((int) pingtime);
		dataOutputStream.close();
		outputStream.close();
		inputStreamReader.close();
		inputStream.close();
		socket.close();
		return response;
	}

	public class StatusResponse {
		private String description;

		private ServerListPing.Players players;

		private ServerListPing.Version version;

		private String favicon;

		private int time;
		
		public StatusResponse(String desc, ServerListPing.Players players, ServerListPing.Version version, String favicon) {
			this.description = desc;
			this.players = players;
			this.version = version;
			this.favicon = favicon;
		}

		public String getDescription() {
			return this.description;
		}

		public ServerListPing.Players getPlayers() {
			return this.players;
		}

		public ServerListPing.Version getVersion() {
			return this.version;
		}

		public String getFavicon() {
			return this.favicon;
		}

		public int getTime() {
			return this.time;
		}

		public void setTime(int time) {
			this.time = time;
		}
	}
	
	//for mc versions > 1.8 => the description became an object instead of a String
    public class StatusResponse15 {
        private Description description;
        private Players players;
        private Version version;
        private String favicon;
        private int time;

        public Description getDescription() {
            return description;
        }

        public Players getPlayers() {
            return players;
        }

        public Version getVersion() {
            return version;
        }

        public String getFavicon() {
            return favicon;
        }

        public int getTime() {
            return time;
        }      

        public void setTime(int time) {
            this.time = time;
        }
        
    }

	public class Players {
		private int max;

		private int online;

		private List<ServerListPing.Player> sample;

		public int getMax() {
			return this.max;
		}

		public int getOnline() {
			return this.online;
		}

		public List<ServerListPing.Player> getSample() {
			return this.sample;
		}
	}

	public class Player {
		private String name;

		private String id;

		public String getName() {
			return this.name;
		}

		public String getId() {
			return this.id;
		}
	}

	public class Version {
		private String name;

		private String protocol;

		public String getName() {
			return this.name;
		}

		public String getProtocol() {
			return this.protocol;
		}
	}
	
	public class Description {
        private String text;

        public String getText() {
            return text;
        }

    }
}
