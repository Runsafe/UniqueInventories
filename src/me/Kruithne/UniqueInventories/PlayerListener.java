package me.Kruithne.UniqueInventories;

import org.bukkit.Server;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class PlayerListener implements Listener {
	
	private Server server = null;
	private InventoryHandler inventoryHandler = null;

	PlayerListener(InventoryHandler inventoryHandler, Server server)
	{
		this.server = server;
		this.inventoryHandler = inventoryHandler;
	}
	
	@EventHandler
	public void onPlayerChangedWorld(PlayerChangedWorldEvent event)
	{
		this.server.broadcastMessage(event.getPlayer().getName() + " just went from " + event.getFrom().getName() + " to " + event.getPlayer().getWorld().getName());
	}
	
}
