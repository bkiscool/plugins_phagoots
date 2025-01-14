package net.pgfmc.core.cmd.donator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.pgfmc.core.CoreMain;
import net.pgfmc.core.api.playerdata.PlayerData;

public class Nick implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (!(sender instanceof Player))
		{
			sender.sendMessage(ChatColor.RED + "Only players can execute this command.");
			return true;
		}
		
		if (args.length <= 0)
		{
			sender.sendMessage(ChatColor.RED + "Please include a nickname.");
			return true;
		}
		
		if (String.join("", args) == null || String.join("", args).strip().equals(""))
		{
			sender.sendMessage(ChatColor.RED + "Invalid nickname: Invalid characters.");
			return true;
		}
		
		if (!((Player) sender).hasPermission("pgf.cmd.donator.nick"))
		{
			sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
			return true;
		}
		
		setNick(PlayerData.from((Player) sender), String.join("", args));
		
		return true;
	}
	
	public static void setNick(PlayerData pd, String nick)
	{
		Player p = pd.getPlayer();
		
		String nickWithColor = "~" + nick.strip();
		nickWithColor = ChatColor.translateAlternateColorCodes('&', nickWithColor);
		nickWithColor = nickWithColor.replaceAll("[^A-Za-z0-9&]", "")
				.replace(ChatColor.COLOR_CHAR + "k", "")
				.replace(ChatColor.COLOR_CHAR + "m", "")
				.replace(ChatColor.COLOR_CHAR + "o", "")
				.replace(ChatColor.COLOR_CHAR + "n", "")
				.replace(ChatColor.COLOR_CHAR + "l", "")
				.replace(ChatColor.COLOR_CHAR + "r", "");
		
		String nickWithoutColor = ChatColor.stripColor(nickWithColor);
		
		/*
		 * A raw length of 0 means the nickname had no content, just color codes (lmao)
		 */
		if (nickWithoutColor.length() <= 0)
		{
			pd.sendMessage(ChatColor.RED + "Invalid nickname: Not long enough.");
			return;
		}
		
		/*
		 * The nickname without color codes must be less than 20 characters
		 */
		if (nickWithoutColor.length() > 21)
		{
			pd.sendMessage(ChatColor.RED + "Invalid nickname: Too long.");
			return;
		}
		
		/*
		 * If the raw nickname is "off" or "reset" or the player's name
		 * then it will reset the nickname to Player.getName()
		 */
		if (nickWithoutColor.equals("off") || nickWithoutColor.equals("reset") || nickWithColor.equals(p.getName()) || nickWithColor.equals(""))
		{
			pd.setData("nick", null).queue();
			pd.sendMessage(ChatColor.GOLD + "Nickname changed to " + pd.getRankedName() + ChatColor.GOLD + "!");
			
			return;
		}
		
		pd.setData("nick", nickWithColor).queue();
		pd.sendMessage(ChatColor.GOLD + "Nickname changed to " + pd.getRankedName() + ChatColor.GOLD + "!");
		
		p.setPlayerListName(pd.getRankedName());
		p.setCustomName(pd.getRankedName());
		p.setCustomNameVisible(true);
		
		Bukkit.getOnlinePlayers().stream().forEach(player -> {
			p.hidePlayer(CoreMain.plugin, player);
			p.showPlayer(CoreMain.plugin, player);
		});
		
	}

}
