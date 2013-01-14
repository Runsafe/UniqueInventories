package no.runsafe.UniqueInventories.Command;

import no.runsafe.UniqueInventories.InventoryRepository;
import no.runsafe.framework.command.player.PlayerAsyncCommand;
import no.runsafe.framework.server.player.RunsafePlayer;
import no.runsafe.framework.timer.IScheduler;

import java.util.HashMap;

public class ListCommand extends PlayerAsyncCommand
{
	public ListCommand(InventoryRepository repository, IScheduler scheduler)
	{
		super("list", "Lists how many inventories you have in your stack", "uniqueinventories.stack", scheduler);
		this.repository = repository;
	}

	@Override
	public String OnAsyncExecute(RunsafePlayer executor, HashMap<String, String> parameters, String[] arguments)
	{
		return String.format("%s inventories stored", repository.get(executor).getStack() + 1);
	}

	private final InventoryRepository repository;
}
