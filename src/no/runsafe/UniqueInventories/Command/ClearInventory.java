package no.runsafe.UniqueInventories.Command;

import no.runsafe.UniqueInventories.InventoryHandler;
import no.runsafe.framework.command.player.PlayerCommand;
import no.runsafe.framework.server.player.RunsafePlayer;

import java.util.HashMap;

public class ClearInventory extends PlayerCommand
{
	public ClearInventory(InventoryHandler inventoryHandler)
	{
		super("clearinventory", "Clears your inventory", "uniqueinventories.clearinventory");
		handler = inventoryHandler;
	}

	@Override
	public String OnExecute(RunsafePlayer executor, HashMap<String, String> parameters, String[] arguments)
	{
		handler.resetPlayersInventory(executor);
		return null;
	}

	InventoryHandler handler;
}
