package net.pgfmc.core.bot.minecraft.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import net.pgfmc.core.api.playerdata.PlayerData;
import net.pgfmc.core.bot.discord.Discord;
import net.pgfmc.core.bot.util.MessageHandler;

public class OnAsyncPlayerChat implements Listener {
	
	@EventHandler(priority = EventPriority.HIGH) // Runs last right before HIGHEST
	public void onChat(AsyncPlayerChatEvent e)
	{
		if (e.isCancelled()) return;
		
		final Player player = e.getPlayer();
		final PlayerData pd = PlayerData.from(player);
		
		final MessageHandler handler = new MessageHandler(e.getMessage(), player);
		
		if (handler.getMessage().length() > 95)
		{
			player.sendMessage(ChatColor.RED + "Your message is too long (max 95 characters).");
			handler.setMessage(handler.getMessage().substring(0, 95) + "(...)");
		}
		
		if (handler.getMessage().contains("@"))
		{
			handler.setMessage(Discord.getMessageWithDiscordMentions(handler.getMessage()));
		}		
		
		e.setFormat(pd.getRankedName() + ChatColor.DARK_GRAY + " -> " + MessageHandler.getTrackColor(player.getUniqueId().toString()) + "%2$s"); // %2$s means 2nd argument (the chat message), %1$s would be the player's display name
		
		handler.send();
		
	}
	
}
