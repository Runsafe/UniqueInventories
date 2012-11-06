package no.runsafe.UniqueInventories.Command;

import no.runsafe.UniqueInventories.InventoryHandler;
import no.runsafe.framework.command.RunsafeCommand;
import no.runsafe.framework.command.RunsafePlayerCommand;
import no.runsafe.framework.server.player.RunsafePlayer;

public class PopCommand extends RunsafePlayerCommand
{
	public PopCommand(InventoryHandler inventoryHandler)
	{
		super("pop");
		handler = inventoryHandler;
	}

	@Override
	public String requiredPermission()
	{
		return "uniqueinventories.stack";
	}

	@Override
	public String OnExecute(RunsafePlayer executor, String[] args)
	{
		handler.PopInventory(executor);
		return "Inventory restored";
	}

	private final InventoryHandler handler;
}
