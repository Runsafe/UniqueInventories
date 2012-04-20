package no.runsafe.UniqueInventories;

import no.runsafe.framework.event.player.IPlayerChangedWorldEvent;
import no.runsafe.framework.event.player.IPlayerDropItemEvent;
import no.runsafe.framework.server.RunsafeWorld;
import no.runsafe.framework.server.event.player.RunsafePlayerChangedWorldEvent;
import no.runsafe.framework.server.event.player.RunsafePlayerDropItemEvent;
import no.runsafe.framework.server.player.RunsafePlayer;
import no.runsafe.framework.timer.IScheduler;

import java.util.ArrayList;

public class PlayerListener implements IPlayerChangedWorldEvent, IPlayerDropItemEvent
{
	private InventoryHandler inventoryHandler = null;
	private ArrayList<String> limitedPlayers = new ArrayList<String>();
	private IScheduler scheduler;
	private IUniverses universes;

	public PlayerListener(IScheduler scheduler, InventoryHandler inventoryHandler, IUniverses universes)
	{
		this.inventoryHandler = inventoryHandler;
		this.scheduler = scheduler;
		this.universes = universes;
	}

	@Override
	public void OnPlayerChangedWorld(RunsafePlayerChangedWorldEvent event)
	{
		if (this.universes.isDifferentUniverse(event.getPlayer().getWorld(), event.getSourceWorld()))
		{
			this.saveInventory(event.getPlayer(), event.getSourceWorld());
			this.updateInventory(event.getPlayer());
		}
	}

	@Override
	public void OnPlayerDropItem(RunsafePlayerDropItemEvent event)
	{
		if (this.limitedPlayers.contains(event.getPlayer().getName()))
		{
			event.setCancelled(true);
		}
	}

	private void saveInventory(RunsafePlayer player, RunsafeWorld fromWorld)
	{
		this.inventoryHandler.saveInventory(player, fromWorld);
	}

	private void updateInventory(final RunsafePlayer player)
	{
		this.limitedPlayers.add(player.getName());
		this.scheduler.startSyncTask(
			new Runnable()
			{
				public void run()
				{
					inventoryHandler.loadInventory(player);
					limitedPlayers.remove(player.getName());
				}
			},
			2L
		);
	}
}
