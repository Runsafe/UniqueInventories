package me.Kruithne.UniqueInventories;

import java.util.ArrayList;

import no.runsafe.framework.interfaces.IPluginDisabled;
import no.runsafe.framework.interfaces.IScheduler;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener, IPluginDisabled {
	
	private InventoryHandler inventoryHandler = null;
	private Server server = null;
	private ArrayList<String> limitedPlayers = new ArrayList<String>();
	private IScheduler scheduler;

	public PlayerListener(Server server, IScheduler scheduler, InventoryHandler inventoryHandler)
	{
		this.server = server;
		this.inventoryHandler = inventoryHandler;
		this.scheduler = scheduler;
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
	
	public void OnPluginDisabled()
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
		this.scheduler.setTimedEvent(
			new Runnable() {
				public void run() {
					inventoryHandler.loadInventory(player, player.getWorld());
					limitedPlayers.remove(player.getName());
				}
		    }, 
		    2L
		);
	}
	
}
