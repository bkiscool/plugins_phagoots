package net.pgfmc.survival.masterbook.inv.home.inv;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import net.pgfmc.core.api.inventory.ListInventory;
import net.pgfmc.core.api.inventory.extra.Butto;
import net.pgfmc.core.api.playerdata.PlayerData;
import net.pgfmc.core.util.ItemWrapper;
import net.pgfmc.survival.cmd.home.Homes;

public class HomeSelect extends ListInventory<String> {
	
	private PlayerData pd;
	private HashMap<String, Location> homes;
	
	public HomeSelect(PlayerData pd)
	{
		super(27, ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Home Select");
		
		this.pd = pd;
		this.homes = Homes.getHomes(pd);
		
		setBack(0, new HomeHomepage(pd).getInventory());
	}
	
	@Override
	public List<String> load() {
		return homes.keySet().stream().collect(Collectors.toList());
	}
	
	@Override
	protected Butto toAction(String entry) {
		
		if (!pd.hasPermission("pgf.cmd.home.home"))
		{
			return (p, e) -> {
				pd.sendMessage(ChatColor.RED + "You don't have permission to execute this command.");
			};
			
		}
		
		if (homes.size() == 0)
		{
			return (p, e) -> {
				pd.sendMessage(ChatColor.RED + "You do not have any homes.");
			};
			
		}
		
		return (p, e) -> {
			p.performCommand("home " + entry);
			p.closeInventory();
		};
		
	}
	
	@Override
	protected ItemStack toItem(String entry) {
		return new ItemWrapper(Material.PAPER).n(ChatColor.RESET + "" + ChatColor.GOLD + entry).gi();
	}
	
}
