package net.pgfmc.modtools.cmd.toggle;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import net.pgfmc.core.api.playerdata.PlayerData;

/**
 * God command
 * @author bk
 *
 */
public class God implements CommandExecutor, Listener {
	
	/**
	 * Toggles God state.
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
			
		if (!(sender instanceof Player))
		{
			sender.sendMessage(ChatColor.RED + "Only players can execute this command.");
			return true;
		}
		
		Player p = (Player) sender;
		
		PlayerData pd = PlayerData.from(p);
		boolean god = pd.hasTag("god"); // Gets "god" from PlayerData, default to false if null, converts to boolean
		
		if (god) {
			pd.removeTag("god");
			p.sendMessage(ChatColor.RED + "cDisabled god mode.");
			
		} else {
			pd.addTag("god");
			p.sendMessage(ChatColor.GREEN + "Enabled god mode!");
			
		}
		
		p.setInvulnerable(!god);
		
		return true;
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) { // disables god when a player joines the servere
		Player p = e.getPlayer();
		
		boolean god = PlayerData.from(p).hasTag("god");
		
		if (god)
		{
			p.setInvulnerable(true);
			p.sendMessage(ChatColor.GREEN + "Enabled god mode!");
			
		}
		
	}
	
}
