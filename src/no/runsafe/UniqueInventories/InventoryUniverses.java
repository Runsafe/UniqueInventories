package no.runsafe.UniqueInventories;

import no.runsafe.framework.configuration.IConfiguration;
import no.runsafe.framework.event.IConfigurationChanged;
import no.runsafe.framework.output.IOutput;
import no.runsafe.framework.server.RunsafeWorld;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryUniverses implements IUniverses, IConfigurationChanged
{
	public InventoryUniverses(IOutput output)
	{
		console = output;
	}

	@Override
	public void OnConfigurationChanged(IConfiguration configuration)
	{
		Map<String, List<String>> section = configuration.getConfigSectionsAsList("groupedInventories");
		if (section == null || section.isEmpty())
		{
			console.writeColoured("&cNo universes loaded!");
			return;
		}
		ArrayList<String> universes = new ArrayList<String>();
		for (String name : section.keySet())
		{
			universes.add(String.format("{&6%s&r=&a%s&r}", name, StringUtils.join(section.get(name), "&r,&a")));
			for (String world : section.get(name))
				groupedInventories.put(world, name);
		}
		console.writeColoured("Loaded universes: %s", StringUtils.join(universes, " "));
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

	private final Map<String, String> groupedInventories = new HashMap<String, String>();
	private final IOutput console;
}
