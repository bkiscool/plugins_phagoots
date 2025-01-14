package net.pgfmc.core.bot.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

import net.dv8tion.jda.api.entities.User;
import net.pgfmc.core.api.playerdata.PlayerData;
import net.pgfmc.core.bot.discord.Discord;

public class MessageHandler {
	
	public enum SenderType {
		DISCORD,
		MINECRAFT;
	}
	
	private static String lastSender = "";
	private static boolean altColor = false;
	
	private SenderType type;
	private String message = "";
	private User user = null;
	private OfflinePlayer player = null;
	
	public MessageHandler(String message, User user)
	{
		type = SenderType.DISCORD;
		this.message = message;
		this.user = user;
	}
	
	public MessageHandler(String message, OfflinePlayer player)
	{
		type = SenderType.MINECRAFT;
		this.message = message;
		this.player = player;
	}
	
	public MessageHandler setMessage(String message)
	{
		this.message = message;
		return this;
	}
	
	public SenderType getSenderType()
	{
		return type;
	}
	
	public String getMessage()
	{
		return message;
	}
	
	public User getUser()
	{
		return user;
	}
	
	public OfflinePlayer getPlayer()
	{
		return player;
	}
	
	public void send()
	{
		if (type == SenderType.DISCORD)
		{
			Bukkit.getServer().broadcastMessage(message);
		} else
		if (type == SenderType.MINECRAFT)
		{			
			Discord.sendMessage(ChatColor.stripColor(PlayerData.from(player).getRankedName()) + " -> " + message).queue();
		}
		
	}
	
	public static ChatColor getTrackColor(String sender)
	{
		if (sender.equals(lastSender)) return (altColor) ? ChatColor.GRAY : ChatColor.WHITE;
			
		lastSender = sender;
		altColor = !altColor;
		return (altColor) ? ChatColor.GRAY : ChatColor.WHITE;
	}
	
}
