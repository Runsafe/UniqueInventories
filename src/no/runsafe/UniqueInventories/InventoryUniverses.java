package no.runsafe.UniqueInventories;

import no.runsafe.framework.configuration.IConfiguration;
import no.runsafe.framework.output.IOutput;
import no.runsafe.framework.server.RunsafeWorld;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;

public class InventoryUniverses implements IUniverses
{
	public InventoryUniverses(IConfiguration config, IOutput output)
	{
		ConfigurationSection grouping = config.getSection("groupedInventories");
		groupedInventories = new HashMap<String, String>();
		if (grouping == null)
			return;

		Set<String> universes = grouping.getKeys(true);
		if (universes == null)
			return;

		for (String universe : universes)
		{
			for (String world : grouping.getStringList(universe))
			{
				groupedInventories.put(world, universe);
				output.outputToConsole(String.format("Adding world %s to universe %s.", world, universe), Level.INFO);
			}
		}
	}

	@Override
	public String getInventoryName(String world)
	{
		if (groupedInventories.containsKey(world))
			return groupedInventories.get(world);

		return world;
	}

	@Override
	public boolean isDifferentUniverse(RunsafeWorld world, RunsafeWorld from)
	{
		return !getInventoryName(world.getName()).equals(getInventoryName(from.getName()));
	}

	private final HashMap<String, String> groupedInventories;
}
