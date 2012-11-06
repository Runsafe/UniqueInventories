package no.runsafe.UniqueInventories.Command;

import no.runsafe.UniqueInventories.InventoryHandler;
import no.runsafe.framework.command.RunsafeCommand;
import no.runsafe.framework.command.RunsafePlayerCommand;
import no.runsafe.framework.server.player.RunsafePlayer;

public class PushCommand extends RunsafePlayerCommand
{
	public PushCommand(InventoryHandler handler)
	{
		super("push");
		inventoryHandler = handler;
	}

	@Override
	public String requiredPermission()
	{
		return "uniqueinventories.stack";
	}

	@Override
	public String OnExecute(RunsafePlayer executor, String[] args)
	{
		inventoryHandler.PushInventory(executor);
		return  "Inventory stored";
	}

	private final InventoryHandler inventoryHandler;
}
