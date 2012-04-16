package me.Kruithne.UniqueInventories;

import no.runsafe.framework.timer.IScheduler;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

import java.util.ArrayList;

public class PlayerListener implements Listener
{
	private InventoryHandler inventoryHandler = null;
	private Server server = null;
	private ArrayList<String> limitedPlayers = new ArrayList<String>();
	private IScheduler scheduler;
	private IUniverses universes;

	public PlayerListener(Server server, IScheduler scheduler, InventoryHandler inventoryHandler, IUniverses universes)
	{
		this.server = server;
		this.inventoryHandler = inventoryHandler;
		this.scheduler = scheduler;
		this.universes = universes;
	}

	@EventHandler
	public void onPlayerChangedWorld(PlayerChangedWorldEvent event)
	{
		if (this.universes.isDifferentUniverse(event.getPlayer().getWorld(), event.getFrom()))
		{
			this.saveInventory(event.getPlayer(), event.getFrom());
			this.updateInventory(event.getPlayer());
		}
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event)
	{
		if (this.limitedPlayers.contains(event.getPlayer().getName()))
		{
			event.setCancelled(true);
		}
	}

	private void saveInventory(Player player, World fromWorld)
	{
		this.inventoryHandler.saveInventory(player, fromWorld);
	}

	private void updateInventory(final Player player)
	{
		this.limitedPlayers.add(player.getName());
		this.scheduler.startSyncTask(
			new Runnable()
			{
				public void run()
				{
					inventoryHandler.loadInventory(player, player.getWorld());
					limitedPlayers.remove(player.getName());
				}
			},
			2L
		);
	}

}
