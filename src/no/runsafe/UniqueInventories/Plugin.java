package no.runsafe.UniqueInventories;

import no.runsafe.UniqueInventories.Command.*;
import no.runsafe.UniqueInventories.Command.Template.LoadCommand;
import no.runsafe.UniqueInventories.Command.Template.SaveCommand;
import no.runsafe.framework.RunsafeConfigurablePlugin;
import no.runsafe.framework.command.Command;
import no.runsafe.framework.configuration.IConfigurationFile;

public class Plugin extends RunsafeConfigurablePlugin implements IConfigurationFile
{
	@Override
	protected void pluginSetup()
	{
		addComponent(InventoryHandler.class);
		addComponent(PlayerListener.class);
		addComponent(InventoryRepository.class);
		addComponent(TemplateRepository.class);
		addComponent(InventoryUniverses.class);
		addComponent(ClearInventory.class);

		Command template = new Command("template", "Manage inventory templates", "uniqueinventories.template");
		template.addSubCommand(getInstance(LoadCommand.class));
		template.addSubCommand(getInstance(SaveCommand.class));

		Command command = new Command("uniqueinv", "Inventory management commands", "uniqueinventories.command");
		command.addSubCommand(getInstance(ListCommand.class));
		command.addSubCommand(getInstance(PushCommand.class));
		command.addSubCommand(getInstance(PopCommand.class));
		command.addSubCommand(getInstance(WorldwipeCommand.class));
		command.addSubCommand(template);

		addComponent(command);
	}
}
