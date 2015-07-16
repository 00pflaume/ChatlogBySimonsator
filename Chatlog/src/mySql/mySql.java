package mySql;

import chatlog.main.main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class mySql {
	private Connection connect;
	private Statement statement = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;
	String username;
	String password;
	String url;
	String database;
	String Server;
	String serverType;

	public void setDaten(String pHost, String pUsername, String pPassword, int pPort, String pDatabase, String pServer,
			String pServerType) {
		this.username = pUsername;
		this.password = pPassword;
		this.url = ("jdbc:mysql://" + pHost + ":" + pPort + "/?user=" + this.username + "&password=" + this.password);
		this.database = pDatabase;
		this.Server = pServer;
		this.serverType = pServerType;
	}

	public void verbinde() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		connect = DriverManager.getConnection(url);
		statement = connect.createStatement();
	}

	public void zwischenspeichern(String pSpieler, int pZeit, String pText, int pType) throws Exception {
		try {
			verbinde();
			preparedStatement = connect.prepareStatement(
					"insert into  " + database + ".chatlog_zwischenspeicher values (?, ?, ?, ?, ?, ?)");
			preparedStatement.setString(1, Server);
			preparedStatement.setString(2, serverType);
			preparedStatement.setInt(3, pType);
			preparedStatement.setString(4, pSpieler);
			preparedStatement.setInt(5, pZeit);
			preparedStatement.setString(6, pText);
			preparedStatement.executeUpdate();
		} catch (Exception e) {
			throw e;
		} finally {
			close();
		}
	}

	public void entferneAltLast(main myPlugin) throws SQLException, ClassNotFoundException {
		myPlugin.getServer().getScheduler().runTaskTimerAsynchronously(myPlugin, new Runnable() {
			public void run() {
				try {
					entferne();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}, 200L, 200L);
	}

	public void entferne() throws ClassNotFoundException, SQLException {
		verbinde();
		int zeit = (int) (System.currentTimeMillis() / 1000L);
		int zuloeschen = zeit - 300;
		int del2 = zeit - 604800;
		this.preparedStatement = this.connect
				.prepareStatement("DELETE FROM  " + this.database + ".chatlog_zwischenspeicher WHERE Zeit < '"
						+ zuloeschen + "' AND Server = '" + this.Server + "' AND EventArt!='1'");
		this.preparedStatement.executeUpdate();
		this.preparedStatement = this.connect
				.prepareStatement("DELETE FROM  " + this.database + ".chatlog_zwischenspeicher WHERE Zeit < '" + del2
						+ "' AND Server = '" + this.Server + "' AND EventArt='1'");
		this.preparedStatement.executeUpdate();
		close();
	}

	public void allesEntfernen() throws ClassNotFoundException, SQLException {
		verbinde();
		try {
			this.preparedStatement = this.connect.prepareStatement(
					"DELETE FROM  " + this.database + ".chatlog_zwischenspeicher WHERE Server = '" + this.Server + "'");
			this.preparedStatement.executeUpdate();
		} catch (SQLException e) {
		}
		close();
	}

	public void entferneJoinEvent(String pSpieler) throws ClassNotFoundException, SQLException {
		verbinde();
		this.preparedStatement = this.connect
				.prepareStatement("DELETE FROM  " + this.database + ".chatlog_zwischenspeicher WHERE Server = '"
						+ this.Server + "' AND Spieler ='" + pSpieler + "' AND EventArt = '1'");
		this.preparedStatement.executeUpdate();
		close();
	}

	public void reinSchreibernLobby(int pZeit, int pZufall, String pSpieler)
			throws ClassNotFoundException, SQLException {
		verbinde();
		int id = zuordnungErstellen(pZeit, pZufall, pSpieler);
		int zeit = 0;
		this.resultSet = this.statement.executeQuery("select Zeit from " + this.database
				+ ".chatlog_zwischenspeicher WHERE EventArt = '1' AND Spieler = '" + pSpieler + "'");
		this.resultSet.next();
		zeit = this.resultSet.getInt("Zeit");
		this.resultSet = this.statement.executeQuery("select * from " + this.database
				+ ".chatlog_zwischenspeicher WHERE Zeit >= '" + zeit + "' AND Server='" + this.Server + "'");
		while (this.resultSet.next()) {
			int EventArt = this.resultSet.getInt("EventArt");
			String Spieler = this.resultSet.getString("Spieler");
			int Zeit = this.resultSet.getInt("Zeit");
			String Inhalt = this.resultSet.getString("Inhalt");
			this.preparedStatement = this.connect
					.prepareStatement("insert into  " + this.database + ".chatlog_speichern values (?, ?, ?, ?, ?)");
			this.preparedStatement.setString(1, Spieler);
			this.preparedStatement.setInt(2, Zeit);
			this.preparedStatement.setString(3, Inhalt);
			this.preparedStatement.setInt(4, EventArt);
			this.preparedStatement.setInt(5, id);
			this.preparedStatement.executeUpdate();
		}
		close();
	}

	public void reinSchreibernGame(int pZeit, int pZufall, String pSpieler)
			throws ClassNotFoundException, SQLException {
		verbinde();
		int id = zuordnungErstellen(pZeit, pZufall, pSpieler);
		this.resultSet = this.statement.executeQuery(
				"select * from " + this.database + ".chatlog_zwischenspeicher WHERE Server = '" + this.Server + "'");
		while (this.resultSet.next()) {
			int EventArt = this.resultSet.getInt("EventArt");
			String Spieler = this.resultSet.getString("Spieler");
			int Zeit = this.resultSet.getInt("Zeit");
			String Inhalt = this.resultSet.getString("Inhalt");
			this.preparedStatement = this.connect
					.prepareStatement("insert into  " + this.database + ".chatlog_speichern values (?, ?, ?, ?, ?)");
			this.preparedStatement.setString(1, Spieler);
			this.preparedStatement.setInt(2, Zeit);
			this.preparedStatement.setString(3, Inhalt);
			this.preparedStatement.setInt(4, EventArt);
			this.preparedStatement.setInt(5, id);
			this.preparedStatement.executeUpdate();
		}
		close();
	}

	public int zuordnungErstellen(int pZeit, int pZufall, String pSpieler) throws SQLException, ClassNotFoundException {
		this.preparedStatement = this.connect
				.prepareStatement("insert into  " + this.database + ".chatlog_zuordnung values (?, ?, ?, ?, ?, ?)");
		this.preparedStatement.setString(1, pSpieler);
		this.preparedStatement.setInt(2, pZeit);
		this.preparedStatement.setInt(3, pZufall);
		this.preparedStatement.setString(4, this.serverType);
		this.preparedStatement.setString(5, this.Server);
		this.preparedStatement.setNull(6, 6);
		this.preparedStatement.executeUpdate();
		this.resultSet = this.statement
				.executeQuery("select last_insert_id() from " + this.database + ".chatlog_zuordnung");
		this.resultSet.next();
		int id = this.resultSet.getInt("last_insert_id()");
		return id;
	}

	public void close() {
		try {
			if (this.resultSet != null) {
				this.resultSet.close();
			}
			if (this.statement != null) {
				this.statement.close();
			}
			if (this.connect != null) {
				this.connect.close();
			}
		} catch (Exception localException) {
		}
	}

	public void importieren() throws SQLException {
		try {
			verbinde();
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		this.preparedStatement = this.connect.prepareStatement("CREATE DATABASE IF NOT EXISTS " + database);
		this.preparedStatement.executeUpdate();
		this.preparedStatement = this.connect.prepareStatement("CREATE TABLE IF NOT EXISTS " + database
				+ ".`chatlog_speichern` (`Spieler` text CHARACTER SET utf8 COLLATE utf8_bin NOT NULL, `Zeit` int(11) NOT NULL, `Inhalt` text CHARACTER SET utf8 COLLATE utf8_bin NOT NULL, `Event` int(11) NOT NULL, `Zugehoerig` int(11) NOT NULL) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_german2_ci");
		this.preparedStatement.executeUpdate();
		this.preparedStatement = this.connect.prepareStatement("CREATE TABLE IF NOT EXISTS " + database
				+ ".`chatlog_zuordnung` (`Gechatlogt` text CHARACTER SET utf8 COLLATE utf8_bin NOT NULL, `Zeit` int(11) NOT NULL, `Zufall` int(11) NOT NULL, `ServerType` text CHARACTER SET utf8 COLLATE utf8_bin NOT NULL, `Server` text CHARACTER SET utf8 COLLATE utf8_bin NOT NULL, `ID` int(10) NOT NULL) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_german2_ci;");
		this.preparedStatement.executeUpdate();
		this.preparedStatement = this.connect.prepareStatement("CREATE TABLE IF NOT EXISTS " + database
				+ ".`chatlog_zwischenspeicher` (`Server` text CHARACTER SET utf8 COLLATE utf8_bin NOT NULL, `ServerType` text CHARACTER SET utf8 COLLATE utf8_bin NOT NULL, `EventArt` int(11) NOT NULL,`Spieler` text CHARACTER SET utf8 COLLATE utf8_bin NOT NULL, `Zeit` int(11) NOT NULL, `Inhalt` text CHARACTER SET utf8 COLLATE utf8_bin NOT NULL) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_german2_ci;");
		this.preparedStatement.executeUpdate();
		try {
			this.preparedStatement = this.connect.prepareStatement("ALTER TABLE " + database
					+ ".`chatlog_zuordnung` ADD PRIMARY KEY (`ID`), ADD UNIQUE KEY `ID` (`ID`)");
			this.preparedStatement.executeUpdate();
		} catch (SQLException e) {

		}
		this.preparedStatement = this.connect.prepareStatement("ALTER TABLE " + database
				+ ".`chatlog_zuordnung` MODIFY `ID` int(10) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=2;");
		this.preparedStatement.executeUpdate();
		close();
	}
}
