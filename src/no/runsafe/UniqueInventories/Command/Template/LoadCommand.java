package no.runsafe.UniqueInventories.Command.Template;

import no.runsafe.UniqueInventories.InventoryHandler;
import no.runsafe.framework.command.player.PlayerCommand;
import no.runsafe.framework.server.player.RunsafePlayer;

import java.util.HashMap;

public class LoadCommand extends PlayerCommand
{
	public LoadCommand(InventoryHandler handler)
	{
		super("load", "Load the current worlds inventory template for editing purposes", "uniqueinventories.template");
		this.handler = handler;
	}

	@Override
	public String OnExecute(RunsafePlayer executor, HashMap<String, String> parameters)
	{
		handler.PushInventory(executor);
		handler.loadTemplateInventory(executor);
		return "Template for current world loaded - remember to save!";
	}

	private final InventoryHandler handler;
}
