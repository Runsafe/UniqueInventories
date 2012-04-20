package no.runsafe.UniqueInventories;

import no.runsafe.framework.command.RunsafeCommand;
import no.runsafe.framework.server.player.RunsafePlayer;

public class PopCommand extends RunsafeCommand
{
	public PopCommand(InventoryHandler inventoryHandler)
	{
		super("pop", null);
		handler = inventoryHandler;
	}

	@Override
	public String requiredPermission()
	{
		return "uniqueinventories.stack";
	}

	@Override
	public boolean Execute(RunsafePlayer player, String[] args)
	{
		handler.PopInventory(player);
		player.sendMessage("Inventory restored");
		return true;
	}

	private InventoryHandler handler;
}
