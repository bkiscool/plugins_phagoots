package net.pgfmc.survival.cmd.home;

import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;

import net.pgfmc.core.api.playerdata.PlayerData;
import net.pgfmc.core.util.commands.PlayerCommand;

public class SetHome extends PlayerCommand {

	public SetHome(String name) {
		super(name);
	}
	
	
	@Override
	public List<String> tabComplete(PlayerData pd, String alias, String[] args) {
		return null;
	}

	@Override
	public boolean execute(PlayerData pd, String alias, String[] args) {
		
		if (args.length == 0)
		{
			pd.sendMessage(ChatColor.RED + "Please enter a name.");
			return true;
		}
		
		
		setHome(pd, String.join("_", args), null);
		return true;
	}
	
	public static void setHome(PlayerData pd, String name, Location loc)
	{
		HashMap<String, Location> homes = Homes.getHomes(pd);
		
		name = name.toLowerCase().strip().replace(" ", "_");
		
		if (homes.containsKey(name))
		{
			pd.sendMessage(ChatColor.RED + "You cannot have duplicate home names: " + ChatColor.GOLD + name);
			return;
		}
		
		if (loc != null)
		{
			homes.put(name, loc);
		} else
		{
			homes.put(name, pd.getPlayer().getLocation());
		}
		
		pd.sendMessage(ChatColor.GREEN + "Set home " + ChatColor.GOLD + name + ChatColor.GREEN + "!");
		pd.setData("homes", homes).queue();
	}

	

}
