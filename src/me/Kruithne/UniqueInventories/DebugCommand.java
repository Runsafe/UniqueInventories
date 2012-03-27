package me.Kruithne.UniqueInventories;

import java.util.List;
import java.util.logging.Level;

import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import no.runsafe.framework.IOutput;
import no.runsafe.framework.RunsafeCommandHandler;

public class DebugCommand extends RunsafeCommandHandler 
{
	public DebugCommand(IOutput output)
	{
		this.output = output;
	}
	
	@Override
	protected boolean getConsoleAccessible() 
	{
		return true;
	}

	@Override
	public String getName() 
	{
		return "uniqueinv";
	}

	protected boolean playerExecute(Player player, List<String> args)
	{
		if(args.get(0).equalsIgnoreCase("debug"))
		{
			setDebug(args.get(1));
			return true;
		}
		return false;
	}
	
	protected boolean consoleExecute(ConsoleCommandSender console, List<String> args)
	{
		if(args.get(0).equalsIgnoreCase("debug"))
		{
			setDebug(args.get(1));
			return true;
		}
		return false;
	}
	
	private void setDebug(String level)
	{
		try
		{
			output.setDebugLevel(Level.parse(level));
		}
		catch(IllegalArgumentException e)
		{
			output.setDebugLevel(Level.OFF);
		}
	}
	
	private IOutput output;
}
