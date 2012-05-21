package no.runsafe.UniqueInventories.Command.Template;

import no.runsafe.UniqueInventories.InventoryHandler;
import no.runsafe.framework.command.ICommand;
import no.runsafe.framework.command.RunsafeCommand;
import no.runsafe.framework.server.player.RunsafePlayer;

import java.util.Collection;

public class LoadCommand extends RunsafeCommand
{
	public LoadCommand(InventoryHandler handler)
	{
		super("load", null);
		this.handler = handler;
	}

	@Override
	public String requiredPermission()
	{
		return "uniqueinventories.template";
	}

	@Override
	public boolean Execute(RunsafePlayer player, String[] args)
	{
		handler.PushInventory(player);
		handler.loadTemplateInventory(player);
		return true;
	}

	private InventoryHandler handler;
}
