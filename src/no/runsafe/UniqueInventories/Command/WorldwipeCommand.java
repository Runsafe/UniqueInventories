package no.runsafe.UniqueInventories.Command;

import no.runsafe.UniqueInventories.InventoryHandler;
import no.runsafe.UniqueInventories.InventoryRepository;
import no.runsafe.framework.command.player.PlayerCommand;
import no.runsafe.framework.server.RunsafeServer;
import no.runsafe.framework.server.player.RunsafePlayer;

import java.util.HashMap;

public class WorldwipeCommand extends PlayerCommand
{
	public WorldwipeCommand(InventoryRepository inventories, InventoryHandler handler)
	{
		super("worldwipe", "Clears all inventories in a world", "uniqueinventories.worldwipe.<world>", "world");
		this.inventories = inventories;
		this.handler = handler;
	}

	@Override
	public String OnExecute(RunsafePlayer executor, HashMap<String, String> parameters)
	{
		String world = parameters.get("world");

		inventories.Wipe(world);

		for (RunsafePlayer inWorld : RunsafeServer.Instance.getWorld(world).getPlayers())
			handler.loadTemplateInventory(inWorld);

		return String.format("World %s inventories wiped.", world);
	}

	private final InventoryRepository inventories;
	private final InventoryHandler handler;
}
