package me.Kruithne.UniqueInventories;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
	
	private UniqueInventories plugin = null;
	private InventoryHandler inventoryHandler = null;
	private Server server = null;
	private ArrayList<String> limitedPlayers = new ArrayList<String>();

	PlayerListener(UniqueInventories plugin)
	{
		this.plugin = plugin;
		this.server = this.plugin.getServer();
		this.inventoryHandler = new InventoryHandler(new DatabaseConnection(Logger.getLogger("Minecraft")));
	}
	
	@EventHandler
	public void onPlayerChangedWorld(PlayerChangedWorldEvent event)
	{
		this.saveInventory(event.getPlayer(), event.getFrom());
		this.updateInventory(event.getPlayer());	
	}
	
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event)
	{
		if (this.limitedPlayers.contains(event.getPlayer().getName()))
		{
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event)
	{
		this.updateInventory(event.getPlayer());	
	}
	
	public void onServerClosing()
	{
		this.inventoryHandler.saveAllInventories(this.server);
	}
	
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		this.saveInventory(event.getPlayer(), event.getPlayer().getWorld());
	}
	
	private void saveInventory(Player player, World fromWorld)
	{
		this.inventoryHandler.saveInventory(player, fromWorld);
	}
	
	private void updateInventory(final Player player)
	{
		this.limitedPlayers.add(player.getName());
		this.server.getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
		    public void run() {
		    	inventoryHandler.loadInventory(player, player.getWorld());
		    	limitedPlayers.remove(player.getName());
		    }
		    
		}, 2L);
	}
	
}
