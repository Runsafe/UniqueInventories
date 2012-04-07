package me.Kruithne.UniqueInventories;

import java.io.InputStream;

import no.runsafe.framework.RunsafePlugin;
import no.runsafe.framework.interfaces.IConfigurationDefaults;
import no.runsafe.framework.interfaces.IConfigurationFile;

public class UniqueInventories extends RunsafePlugin implements IConfigurationFile, IConfigurationDefaults {

	public PlayerListener playerListener = null;
	
	@Override
	protected void PluginSetup() 
	{
		addComponent(InventoryHandler.class);
		addComponent(PlayerListener.class);
		addComponent(InventoryRepository.class);
		addComponent(DebugCommand.class);
	}

	@Override
	public String getConfigurationPath() 
	{
		return "plugins/" + this.getName() + "/config.yml";
	}

	@Override
	public InputStream getDefaultConfiguration() 
	{
		return getResource("defaults.yml");
	}
}
