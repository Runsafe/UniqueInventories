package no.runsafe.UniqueInventories.Command;

import no.runsafe.UniqueInventories.InventoryHandler;
import no.runsafe.framework.command.player.PlayerCommand;
import no.runsafe.framework.server.player.RunsafePlayer;

import java.util.HashMap;

public class PopCommand extends PlayerCommand
{
	public PopCommand(InventoryHandler inventoryHandler)
	{
		super("pop", "Loads the inventory on top of your stack", "uniqueinventories.stack");
		handler = inventoryHandler;
	}

	@Override
	public String OnExecute(RunsafePlayer executor, HashMap<String, String> parameters)
	{
		handler.PopInventory(executor);
		return "Inventory restored";
	}

	private final InventoryHandler handler;
}
