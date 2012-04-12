package me.Kruithne.UniqueInventories;

import no.runsafe.framework.configuration.IConfiguration;
import no.runsafe.framework.output.IOutput;
import org.bukkit.World;
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
	public boolean isDifferentUniverse(World world, World from)
	{
		return !getInventoryName(world.getName()).equals(getInventoryName(from.getName()));
	}

	private HashMap<String, String> groupedInventories;
}
