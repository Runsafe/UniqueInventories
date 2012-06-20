package no.runsafe.UniqueInventories.Command;

import no.runsafe.UniqueInventories.InventoryRepository;
import no.runsafe.framework.command.RunsafeCommand;
import no.runsafe.framework.command.RunsafePlayerCommand;
import no.runsafe.framework.output.IOutput;
import no.runsafe.framework.server.player.RunsafePlayer;

public class ListCommand extends RunsafePlayerCommand
{
	public ListCommand(InventoryRepository repository)
	{
		super("list", null);
		this.repository = repository;
	}

	@Override
	public String requiredPermission()
	{
		return "uniqueinventories.stack";
	}

	@Override
	public String OnExecute(RunsafePlayer executor, String[] args)
	{
		return String.format("%s inventories stored", repository.get(executor).getStack() + 1);
	}

	private InventoryRepository repository;
}
