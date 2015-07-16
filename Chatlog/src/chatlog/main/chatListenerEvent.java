package chatlog.main;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class chatListenerEvent implements Listener {
	main plugin;

	public chatListenerEvent(main pPlugin) {
		plugin = pPlugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerChat(AsyncPlayerChatEvent pEvent) throws Exception {
		Player player = pEvent.getPlayer();
		String Nachricht = pEvent.getMessage();
		String spieler = player.getDisplayName();
		int zeit = (int) (System.currentTimeMillis() / 1000L);
		plugin.jemandHatEtwasGeschrieben(spieler, zeit, Nachricht);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerJoinEvent pEvent) throws Exception {
		Player player = pEvent.getPlayer();
		String spieler = player.getDisplayName();
		int zeit = (int) (System.currentTimeMillis() / 1000L);
		plugin.jemandIstBeigetreten(spieler, zeit);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerQuitEvent pEvent) throws Exception {
		Player player = pEvent.getPlayer();
		String spieler = player.getDisplayName();
		int zeit = (int) (System.currentTimeMillis() / 1000L);
		plugin.jemandHatDasSpielVerlassen(spieler, zeit);
	}
}
