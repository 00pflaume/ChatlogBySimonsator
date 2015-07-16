package chatlog.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;

import mySql.mySql;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class main extends JavaPlugin {
	private String Berechtigung1 = "chatlog.create";
	private String Berechtigung2 = "chatlog.protectet";
	private String Berechtigung3 = "chatlog.create.nowait";
	private mySql mySql;
	private String nameDesServers;
	private String serverType;
	private String lobby = "Lobby";
	private String[] task = new String[0];
	private String prefix = "[Chatlog] ";
	private ArrayList<String> used = new ArrayList<>();
	private String dauerhaft = null;
	private String linkByUser = null;
	private String language;

	public void onEnable() {
		ladeConfig();
		String host = getConfig().getString("config.host");
		String user = getConfig().getString("config.user");
		String passwort = getConfig().getString("config.passwort");
		String database = getConfig().getString("config.database");
		int port = Integer.parseInt(getConfig().getString("config.port"));
		serverType = getConfig().getString("config.ServerType");
		nameDesServers = getConfig().getString("config.ServerName");
		linkByUser = getConfig().getString("config.ChatlogLink") + "?time=";
		Berechtigung1 = getConfig().getString("config.Permission1");
		Berechtigung2 = getConfig().getString("config.Permission2");
		Berechtigung3 = getConfig().getString("config.Permission3");
		language = getConfig().getString("config.language");
		mySql = new mySql();
		mySql.setDaten(host, user, passwort, port, database, nameDesServers, serverType);
		try {
			mySql.importieren();
		} catch (SQLException e1) {
			if (language.equalsIgnoreCase("en")) {
				System.out.println("[Chatlog] Could not import tabel");
			} else {
				System.out.println(prefix + " Tabelle konnte nicht in MySql importiert werden");
			}
			e1.printStackTrace();
		}
		new chatListenerEvent(this);
		if (serverType.equalsIgnoreCase(lobby)) {
			try {
				mySql.entferneAltLast(this);
			} catch (ClassNotFoundException | SQLException e) {
				e.printStackTrace();
			}
		}
		String localVersion = getDescription().getVersion();
		if (getConfig().getBoolean("config.UpdateNotification")) {
			try {
				HttpURLConnection con = (HttpURLConnection) new URL("http://www.spigotmc.org/api/general.php")
						.openConnection();
				con.setDoOutput(true);
				con.setRequestMethod("POST");
				con.getOutputStream()
						.write(("key=98BE0FE67F88AB82B4C197FAF1DC3B69206EFDCC4D3B80FC83A00037510B99B4&resource=9562")
								.getBytes("UTF-8"));
				String version = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
				if (localVersion.equalsIgnoreCase(version)) {

				} else {
					if (language.equalsIgnoreCase("en")) {
						System.out.println(prefix + "For the plugin Chatlog by Simonsator is an update available");
					} else {
						System.out
								.println(prefix + "Für das Plugin \"Chatlog by Simonsator\" ist ein Update verfügbar");
					}
				}
				if (language.equalsIgnoreCase("en")) {
					System.out.println(
							prefix + "\"Chatlog by Simonsator\"v." + localVersion + " was activated successfully");
				} else {
					System.out
							.println(prefix + "Simonsators Chatlog v." + localVersion + " wurde erfolgreich aktiviert");
				}
			} catch (IOException e) {
				if (language.equalsIgnoreCase("en")) {
					System.out.println(prefix + "An error occurred, while searching for updates");
				} else {
					System.out.println(prefix + "Es ist ein Fehler beim suchen nach updates aufgetreten");
				}
				e.printStackTrace();
			}
		} else {
			if (language.equalsIgnoreCase("en")) {
				System.out
						.println(prefix + "\"Chatlog by Simonsator\"v." + localVersion + " was activated successfully");
				System.out.println(prefix + "Update notification is not set to true");
			} else {
				System.out.println(prefix + "Simonsators Chatlog v." + localVersion + " wurde erfolgreich aktiviert");
				System.out.println(prefix + "Update Notification ist deaktiviert");
			}
		}
	}

	public void onDisable() {
		if (serverType.equalsIgnoreCase(lobby) == false) {
			try {
				fuehreTaskAus();
			} catch (ClassNotFoundException | SQLException e1) {
				e1.printStackTrace();
			}
		}
		try {
			mySql.allesEntfernen();
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}

		mySql.close();
		if (language.equalsIgnoreCase("en")) {
			System.out.println(prefix + "The connection to MySql got closed");
		} else {
			System.out.println(prefix + " Verbindung zur MySql Datenbank wurde geschlossen");
		}
	}

	public void fuehreTaskAus() throws ClassNotFoundException, SQLException {
		int durchlauf = 0;
		while (Array.getLength(task) > durchlauf) {
			StringTokenizer st = new StringTokenizer(task[durchlauf], "|");
			int dl2 = 0;
			String[] ausseinandergenommen = new String[3];
			while (st.hasMoreTokens()) {
				ausseinandergenommen[dl2] = st.nextToken();
				dl2++;
			}
			int zeit = Integer.parseInt(ausseinandergenommen[0]);
			int zufall = Integer.parseInt(ausseinandergenommen[1]);
			String gechatlogt = ausseinandergenommen[2];
			mySql.reinSchreibernGame(zeit, zufall, gechatlogt);
			durchlauf++;
		}
	}

	public boolean onCommand(CommandSender sender, Command cmd, String Kommando, String[] zuChatlogen) {
		if (Kommando.equalsIgnoreCase("chatlog") || Kommando.equalsIgnoreCase("rp")
				|| Kommando.equalsIgnoreCase("report") || Kommando.equalsIgnoreCase("cl")) {
			if (sender instanceof Player) {
				final Player gesendeter = (Player) sender;
				if (sender.hasPermission(Berechtigung1)) {
					if (zuChatlogen.length == 1) {
						if (Bukkit.getPlayer(zuChatlogen[0]) != null) {
							Player geChatlogt = (Player) Bukkit.getPlayer(zuChatlogen[0]);
							if (geChatlogt.getDisplayName() != gesendeter.getDisplayName()) {
								if (geChatlogt.hasPermission(Berechtigung2) == false) {
									if (!used.contains(gesendeter.getDisplayName())) {
										if (serverType.equalsIgnoreCase(lobby)) {
											int zeit = (int) (System.currentTimeMillis() / 1000L);
											Random rand = new Random();
											int zufall = rand.nextInt(999);
											String link = linkByUser + zeit + "&zufall=" + zufall;
											try {
												jemandHatDasEinChatlogGemacht(gesendeter.getDisplayName(), zeit,
														geChatlogt.getDisplayName());
											} catch (Exception e1) {
												e1.printStackTrace();
											}
											try {
												mySql.reinSchreibernLobby(zeit, zufall, geChatlogt.getDisplayName());
											} catch (ClassNotFoundException | SQLException e) {
												e.printStackTrace();
											}
											if (language.equalsIgnoreCase("en")) {
												sender.sendMessage(ChatColor.GREEN + "A chatlog of §e"
														+ geChatlogt.getDisplayName() + ChatColor.GREEN
														+ " was created, the link is: " + ChatColor.BLUE
														+ ChatColor.UNDERLINE + link);
												System.out.println(prefix + gesendeter.getDisplayName()
														+ " has created a chatlog of  " + geChatlogt.getDisplayName()
														+ ", the link is: " + link);
											} else {
												sender.sendMessage(ChatColor.GREEN + "Ein Chatlog von §e"
														+ geChatlogt.getDisplayName() + ChatColor.GREEN
														+ " wurde erstellt, der Link ist: " + ChatColor.BLUE
														+ ChatColor.UNDERLINE + link);
												System.out.println(prefix + gesendeter.getDisplayName()
														+ " hat einen Chatlog von dem Spieler "
														+ geChatlogt.getDisplayName() + " erstellt, der Link ist: "
														+ link);
											}
											if (sender.hasPermission(Berechtigung3) == false) {
												used.add(gesendeter.getDisplayName());
												Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {

													@Override
													public void run() {
														used.remove(gesendeter.getDisplayName());
													}

												}, 1200);
											}
											return true;
										} else {
											boolean jetzterstellt = false;
											String link = null;
											int zeit = 0;
											int zufall = 0;
											if (dauerhaft == null) {
												zeit = (int) (System.currentTimeMillis() / 1000L);
												Random rand = new Random();
												zufall = rand.nextInt(999);
												link = linkByUser + zeit + "&zufall=" + zufall;
												try {
													jemandHatDasEinChatlogGemacht(gesendeter.getDisplayName(), zeit,
															geChatlogt.getDisplayName());
													dauerhaft = link;
													jetzterstellt = true;
												} catch (Exception e1) {
													e1.printStackTrace();
												}
											} else {
												link = dauerhaft;
												jetzterstellt = false;
											}
											if (language.equalsIgnoreCase("en")) {
												sender.sendMessage(ChatColor.GREEN + "A chatlog of §e"
														+ geChatlogt.getDisplayName() + ChatColor.GREEN
														+ " was created, the link is: " + ChatColor.BLUE
														+ ChatColor.UNDERLINE + link + ChatColor.RESET + ChatColor.GREEN
														+ "." + " You can use this link, when the round is over.");
												System.out.println(prefix + gesendeter.getDisplayName()
														+ " has created a chatlog of " + geChatlogt.getDisplayName()
														+ ", the link is: " + link);
											} else {
												sender.sendMessage(ChatColor.GREEN + "Ein Chatlog von §e"
														+ geChatlogt.getDisplayName() + ChatColor.GREEN
														+ " wurde erstellt, der Link ist: " + ChatColor.BLUE
														+ ChatColor.UNDERLINE + link + ChatColor.RESET + ChatColor.GREEN
														+ "."
														+ " Du kannst den Chatlog aufrufen, sobald die Runde vorbei ist.");
												System.out.println(prefix + gesendeter.getDisplayName()
														+ " hat einen Chatlog von dem Spieler "
														+ geChatlogt.getDisplayName() + " erstellt, der Link ist: "
														+ link);
											}
											if (jetzterstellt == true) {
												Object newArray = Array.newInstance(task.getClass().getComponentType(),
														Array.getLength(task) + 1);
												System.arraycopy(task, 0, newArray, 0, Array.getLength(task));
												task = (String[]) newArray;
												task[Array.getLength(task) - 1] = zeit + "|" + zufall + "|"
														+ geChatlogt.getDisplayName();
												if (sender.hasPermission(Berechtigung3) == false) {
													used.add(gesendeter.getDisplayName());
													Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {

														@Override
														public void run() {
															used.remove(gesendeter.getDisplayName());
														}

													}, 1200);
												}
											}
											return true;
										}
									} else {
										if (language.equalsIgnoreCase("en")) {
											sender.sendMessage(
													ChatColor.RED + "You can only created every minute a chatlog.");
											System.out.println(prefix + gesendeter.getDisplayName()
													+ " needs to wait one minute, until he can chatlog again.");
										} else {
											sender.sendMessage(ChatColor.RED
													+ "Du darfst nur jede Minute einen Chatlog erstellen.");
											System.out.println(prefix + gesendeter.getDisplayName()
													+ " muss noch eine Minute warten, bis er wieder Chatlogen darf.");
										}
										return true;
									}
								} else {
									if (language.equalsIgnoreCase("en")) {
										sender.sendMessage(
												ChatColor.RED + "You can not creat a chatlog of this person.");
										System.out.println(prefix + gesendeter.getDisplayName()
												+ " tried to create a chatlog of " + geChatlogt.getDisplayName()
												+ ", but  " + geChatlogt.getDisplayName()
												+ " is not allowed to be chatloged.");
									} else {
										sender.sendMessage(ChatColor.RED
												+ "Du darfst von diesem Spieler keinen Chatlog erstellen.");
										System.out.println(prefix + gesendeter.getDisplayName() + " hat versucht von "
												+ geChatlogt.getDisplayName() + " zu erstellen, jedoch darf "
												+ geChatlogt.getDisplayName() + " nicht gechatlogt werden.");
									}
									return true;
								}
							} else {
								if (language.equalsIgnoreCase("en")) {
									sender.sendMessage(ChatColor.RED + "You can not chatlog yourself.");
									System.out.println(prefix + gesendeter.getDisplayName()
											+ " tried to create a chatlog of his own.");
									sender.sendMessage("§c/chatlog [playername]");
								} else {
									sender.sendMessage(ChatColor.RED + "Du kannst dich nicht selber chatlogen");
									System.out.println(prefix + gesendeter.getDisplayName()
											+ " hat versucht einen Chatlog von sich selbst zu erstellen.");
									sender.sendMessage("§c/chatlog [Spielername]");
								}
								return true;
							}
						} else {
							if (language.equalsIgnoreCase("en")) {
								sender.sendMessage("§cThe player §e" + zuChatlogen[0] + "§c was not found");
								System.out.println(prefix + "The player " + zuChatlogen[0] + " was not found.");
								sender.sendMessage("§c/chatlog [playername]");
							} else {
								sender.sendMessage("§cDer Spieler §e" + zuChatlogen[0] + "§c wurde nicht gefunden");
								System.out.println(prefix + "Der Spieler " + zuChatlogen[0] + " wurde nicht gefunden.");
								sender.sendMessage("§c/chatlog [Spielername]");
							}
							return true;
						}
					} else {
						if (zuChatlogen.length == 0) {
							if (language.equalsIgnoreCase("en")) {
								sender.sendMessage(
										"§cYou need to name the player, who you want to create a chatlog of");
								System.out.println(prefix + gesendeter.getDisplayName() + "A player must be namend");
								sender.sendMessage("§c/chatlog [playername]");
							} else {
								sender.sendMessage(
										"§cDu musst einen Spieler angeben, von dem du einen Chatlog erstellen möchtest");
								System.out.println(
										prefix + gesendeter.getDisplayName() + "Es muss ein Spieler angegeben werden");
								sender.sendMessage("§c/chatlog [Spielername]");
							}
							return true;
						} else {
							if (language.equalsIgnoreCase("en")) {
								sender.sendMessage(
										"§cOnly from §eONE §cplayer you can create a chatlog at the same time");
								System.out.println(
										prefix + "Only of one player at the same time can a chatlog be created");
								sender.sendMessage("§c/chatlog [playername]");
							} else {
								sender.sendMessage(
										"§cEs darf nur von §eEINEM §cSpieler gleichzeitig ein Chatlog erstellt werden");
								System.out.println(
										prefix + "Es darf nur ein Spieler Spieler auf einmal gechatlogt werden");
								sender.sendMessage("§c/chatlog [Spielername]");
							}
							return true;
						}
					}
				} else {
					if (language.equalsIgnoreCase("en")) {
						sender.sendMessage(ChatColor.RED + "You do not have the permission: " + ChatColor.DARK_RED
								+ Berechtigung1);
						System.out.println(prefix + gesendeter.getDisplayName()
								+ " did not have the right permissions. He needs the permission " + Berechtigung1);
					} else {
						sender.sendMessage(
								ChatColor.RED + "Dir fehlt die Permission: " + ChatColor.DARK_RED + Berechtigung1);
						System.out.println(prefix + gesendeter.getDisplayName()
								+ " hatte nicht die Notwendigen Permissions einen Chatlog zu erstellen. Er braucht die Permission "
								+ Berechtigung1);
					}
					return true;
				}
			} else {
				if (language.equalsIgnoreCase("en")) {
					System.out.println(prefix + "The command can only be used by a player.");
				} else {
					System.out.println(prefix + "Das Kommando kann nur von einem Spieler benutzt werden");
				}
				return true;
			}

		} else {
			if (language.equalsIgnoreCase("en")) {
				sender.sendMessage("§c/chatlog [playername]");
			} else {
				sender.sendMessage("§c/chatlog [Spielername]");
			}
			return true;
		}

	}

	public void ladeConfig() {
		getConfig().options().copyDefaults(true);
		saveConfig();
	}

	public String getNameDesServers() {
		return nameDesServers;
	}

	public void jemandHatEtwasGeschrieben(String pUUID, int pStart, String pText) throws Exception {
		mySql.zwischenspeichern(pUUID, pStart, pText, 0);
	}

	public void jemandIstBeigetreten(String pSpieler, int pZeit) throws Exception {
		mySql.zwischenspeichern(pSpieler, pZeit, "", 1);
	}

	public void jemandHatDasSpielVerlassen(String pSpieler, int pZeit) throws Exception {
		mySql.zwischenspeichern(pSpieler, pZeit, "", 2);
	}

	public void jemandHatDasEinChatlogGemacht(String pSpieler, int pZeit, String pGechatlogt) throws Exception {
		mySql.zwischenspeichern(pSpieler, pZeit, pGechatlogt, 3);
	}
}