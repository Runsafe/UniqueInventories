package no.runsafe.UniqueInventories.Command;

import no.runsafe.UniqueInventories.InventoryHandler;
import no.runsafe.UniqueInventories.InventoryRepository;
import no.runsafe.framework.command.RunsafeCommand;
import no.runsafe.framework.server.RunsafeWorld;
import no.runsafe.framework.server.player.RunsafePlayer;

public class WorldwipeCommand extends RunsafeCommand
{
	public WorldwipeCommand(InventoryRepository inventories, InventoryHandler handler)
	{
		super("worldwipe", null);
		this.inventories = inventories;
		this.handler = handler;
	}

	@Override
	public String requiredPermission()
	{
		return "uniqueinventories.worldwipe";
	}

	@Override
	public boolean Execute(RunsafePlayer player, String[] args)
	{
		if (args == null || args.length != 1)
			return false;

		inventories.Wipe(args[0]);

		for (RunsafePlayer inWorld : new RunsafeWorld(args[0]).getPlayers())
			handler.loadTemplateInventory(inWorld);

		return true;
	}

	private InventoryRepository inventories;
	private InventoryHandler handler;
}
