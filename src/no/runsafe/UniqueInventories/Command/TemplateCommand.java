package no.runsafe.UniqueInventories.Command;

import no.runsafe.UniqueInventories.Command.Template.LoadCommand;
import no.runsafe.UniqueInventories.Command.Template.SaveCommand;
import no.runsafe.UniqueInventories.InventoryHandler;
import no.runsafe.framework.command.ICommand;
import no.runsafe.framework.command.RunsafeCommand;
import no.runsafe.framework.server.player.RunsafePlayer;

import java.util.ArrayList;
import java.util.Collection;

public class TemplateCommand extends RunsafeCommand
{
	public TemplateCommand(InventoryHandler handler)
	{
		super("template", null);
		subCommands.put("load", new LoadCommand(handler));
		subCommands.put("save", new SaveCommand(handler));
	}
}
