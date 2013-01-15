package no.runsafe.UniqueInventories.Command.Template;

import no.runsafe.UniqueInventories.InventoryHandler;
import no.runsafe.framework.command.player.PlayerCommand;
import no.runsafe.framework.server.player.RunsafePlayer;

import java.util.HashMap;

public class SaveCommand extends PlayerCommand
{
	public SaveCommand(InventoryHandler handler)
	{
		super("load", "Saves your inventory as the current worlds inventory template ", "uniqueinventories.template");
		this.handler = handler;
	}

	@Override
	public String OnExecute(RunsafePlayer executor, HashMap<String, String> parameters)
	{
		handler.saveTemplateInventory(executor);
		handler.PopInventory(executor);
		return "Template saved and inventory restored!";
	}

	private final InventoryHandler handler;
}
