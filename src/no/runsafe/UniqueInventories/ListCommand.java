package no.runsafe.UniqueInventories;

import no.runsafe.framework.command.RunsafeCommand;
import no.runsafe.framework.output.IOutput;
import no.runsafe.framework.server.player.RunsafePlayer;

public class ListCommand extends RunsafeCommand
{
	public ListCommand(InventoryRepository repository)
	{
		super("list", null);
		this.repository = repository;
	}

	@Override
	public boolean Execute(RunsafePlayer player, String[] args)
	{
		player.sendMessage(String.format("%s inventories stored", repository.get(player).getStack() + 1));
		return true;
	}

	private InventoryRepository repository;
}
