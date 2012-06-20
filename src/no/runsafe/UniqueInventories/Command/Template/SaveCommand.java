package no.runsafe.UniqueInventories.Command.Template;

import no.runsafe.UniqueInventories.InventoryHandler;
import no.runsafe.framework.command.ICommand;
import no.runsafe.framework.command.RunsafeCommand;
import no.runsafe.framework.command.RunsafePlayerCommand;
import no.runsafe.framework.server.player.RunsafePlayer;

import java.util.Collection;

public class SaveCommand extends RunsafePlayerCommand
{
	public SaveCommand(InventoryHandler handler)
	{
		super("save", null);
		this.handler = handler;
	}

	@Override
	public String requiredPermission()
	{
		return "uniqueinventories.template";
	}

	@Override
	public String OnExecute(RunsafePlayer executor, String[] args)
	{
		handler.saveTemplateInventory(executor);
		handler.PopInventory(executor);
		return "Template saved and inventory restored!";
	}

	private InventoryHandler handler;
}
