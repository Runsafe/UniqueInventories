package no.runsafe.UniqueInventories.Command;

import no.runsafe.UniqueInventories.InventoryHandler;
import no.runsafe.framework.command.RunsafeAsyncPlayerCommand;
import no.runsafe.framework.command.RunsafePlayerCommand;
import no.runsafe.framework.server.player.RunsafePlayer;
import no.runsafe.framework.timer.IScheduler;

public class ClearInventory extends RunsafePlayerCommand
{

	public ClearInventory(InventoryHandler inventoryHandler)
	{
		super("clearinventory");
		handler = inventoryHandler;
	}

	@Override
	public String requiredPermission()
	{
		return "uniqueinventories.clearinventory";
	}

	@Override
	public String OnExecute(RunsafePlayer executor, String[] args)
	{
		handler.resetPlayersInventory(executor);
		return null;
	}

	InventoryHandler handler;
}
