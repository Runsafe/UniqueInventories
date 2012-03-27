package me.Kruithne.UniqueInventories;

import java.io.InputStream;

import no.runsafe.framework.IConfigurationDefaults;
import no.runsafe.framework.IConfigurationFile;
import no.runsafe.framework.IDatabaseTypeProvider;
import no.runsafe.framework.RunsafePlugin;

public class UniqueInventories extends RunsafePlugin implements IConfigurationFile, IConfigurationDefaults, IDatabaseTypeProvider {

	public PlayerListener playerListener = null;
	
	@Override
	protected void PluginSetup() 
	{
		addComponent(InventoryHandler.class);
		addComponent(PlayerListener.class);
		this.playerListener = this.container.getComponent(PlayerListener.class);
	}

	@Override
	public void onDisable()
	{
		this.playerListener.onServerClosing();
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

	@SuppressWarnings("rawtypes")
	@Override
	public Class[] getModelClasses() 
	{
		return new Class[] { InventoryStorage.class };
	}
}
