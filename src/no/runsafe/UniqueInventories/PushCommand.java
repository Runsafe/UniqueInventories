package no.runsafe.UniqueInventories;

import no.runsafe.framework.command.RunsafeCommand;
import no.runsafe.framework.server.player.RunsafePlayer;

public class PushCommand extends RunsafeCommand
{
	public PushCommand(InventoryHandler handler)
	{
		super("push", null);
		inventoryHandler = handler;
	}

	@Override
	public String requiredPermission()
	{
		return "uniqueinventories.stack";
	}

	@Override
	public boolean Execute(RunsafePlayer player, String[] args)
	{
		inventoryHandler.PushInventory(player);
		player.sendMessage("Inventory stored");
		return true;
	}

	private InventoryHandler inventoryHandler;
}
