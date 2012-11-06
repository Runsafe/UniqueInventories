package no.runsafe.UniqueInventories.Command.Template;

import no.runsafe.UniqueInventories.InventoryHandler;
import no.runsafe.framework.command.ICommand;
import no.runsafe.framework.command.RunsafeCommand;
import no.runsafe.framework.command.RunsafePlayerCommand;
import no.runsafe.framework.server.player.RunsafePlayer;

import java.util.Collection;

public class LoadCommand extends RunsafePlayerCommand
{
	public LoadCommand(InventoryHandler handler)
	{
		super("load");
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
		handler.PushInventory(executor);
		handler.loadTemplateInventory(executor);
		return "Template for current world loaded - remember to save!";
	}

	private final InventoryHandler handler;
}
