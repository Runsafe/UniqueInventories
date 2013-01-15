package no.runsafe.UniqueInventories.Command;

import no.runsafe.UniqueInventories.InventoryHandler;
import no.runsafe.framework.command.player.PlayerCommand;
import no.runsafe.framework.server.player.RunsafePlayer;

import java.util.HashMap;

public class PushCommand extends PlayerCommand
{
	public PushCommand(InventoryHandler handler)
	{
		super("push", "Saves your inventory to the stack and gives you a default one.", "uniqueinventories.stack");
		inventoryHandler = handler;
	}

	@Override
	public String OnExecute(RunsafePlayer executor, HashMap<String, String> parameters)
	{
		inventoryHandler.PushInventory(executor);
		return "Inventory stored";
	}

	private final InventoryHandler inventoryHandler;
}
