package no.runsafe.UniqueInventories;

import no.runsafe.framework.event.player.IPlayerChangedWorldEvent;
import no.runsafe.framework.event.player.IPlayerDropItemEvent;
import no.runsafe.framework.event.player.IPlayerMove;
import no.runsafe.framework.server.RunsafeLocation;
import no.runsafe.framework.server.RunsafeWorld;
import no.runsafe.framework.server.event.player.RunsafePlayerChangedWorldEvent;
import no.runsafe.framework.server.event.player.RunsafePlayerDropItemEvent;
import no.runsafe.framework.server.player.RunsafePlayer;
import no.runsafe.framework.timer.ForegroundWorker;
import no.runsafe.framework.timer.IScheduler;

import java.util.ArrayList;

public class PlayerListener extends ForegroundWorker<String, RunsafePlayer> implements IPlayerChangedWorldEvent, IPlayerDropItemEvent, IPlayerMove
{
	public PlayerListener(IScheduler scheduler, InventoryHandler inventoryHandler, IUniverses universes)
	{
		super(scheduler);
		this.inventoryHandler = inventoryHandler;
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
	public boolean OnPlayerMove(RunsafePlayer player, RunsafeLocation from, RunsafeLocation to)
	{
		if (limitedPlayers.contains(player.getName()) && !isQueued(player.getName()))
			Push(player.getName(), player);
		return true;
	}

	@Override
	public void OnPlayerDropItem(RunsafePlayerDropItemEvent event)
	{
		if (this.limitedPlayers.contains(event.getPlayer().getName()))
			event.setCancelled(true);
	}

	private void saveInventory(RunsafePlayer player, RunsafeWorld fromWorld)
	{
		this.inventoryHandler.saveInventory(player, fromWorld);
	}

	private void updateInventory(final RunsafePlayer player)
	{
		limitedPlayers.add(player.getName());
		Push(player.getName(), player);
	}

	@Override
	public void process(String name, RunsafePlayer player)
	{
		inventoryHandler.loadInventory(player);
		limitedPlayers.remove(name);
	}

	private InventoryHandler inventoryHandler = null;
	private final ArrayList<String> limitedPlayers = new ArrayList<String>();
	private final IUniverses universes;
}
