package net.pgfmc.survival.cmd.afk;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import net.pgfmc.core.api.playerdata.PlayerData;

public class AfkEvents implements Listener {
	
	@EventHandler
	public void onMove(PlayerMoveEvent e)
	{
		PlayerData pd = PlayerData.from(e.getPlayer());
		Location from = e.getFrom();
		Location to = e.getTo();
		
		if (!pd.hasTag("afk")) return;
		
		// Jumping disables AFK
		if (to.getY() > from.getY())
		{
			toggleAfk(pd);
			return;
		}
		
		// Lets the camera move
		from.setDirection(to.getDirection());
		e.setTo(from);
		
	}
	
	@EventHandler
	public void onClick(PlayerInteractEvent e)
	{
		PlayerData pd = PlayerData.from(e.getPlayer());
		
		if (!pd.hasTag("afk")) return;
		
		toggleAfk(pd);
		
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e)
	{
		PlayerData pd = PlayerData.from(e.getPlayer());
		
		if (!pd.hasTag("afk")) return;
		
		toggleAfk(pd);
		
	}
	
	@SuppressWarnings("deprecation")
	public static void toggleAfk(PlayerData pd)
	{
		if (pd.getPlayer() == null) return;
		
		Player p = pd.getPlayer();
		boolean isAfk = pd.hasTag("afk");
		
		if (isAfk)
		{
			p.setInvulnerable(false);
			p.sendMessage(ChatColor.RED + "AFK mode off.");
			pd.playSound(Sound.BLOCK_NOTE_BLOCK_BASS);
			
			pd.removeTag("afk");
		} else if (!isAfk)
		{
			if (p.getHealth() < p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue())
			{
				p.sendMessage(ChatColor.RED + "You must be full health to activate AFK.");
				
				return;
			}
			
			if (!p.isOnGround() || p.getNoDamageTicks() != 0 || p.isFlying()
			  || p.isDead()     || p.isFrozen() 			 || p.isGliding()
			  || p.isInWater()  || p.isRiptiding() 			 || p.isSleeping()
			  || p.isSwimming() || p.isClimbing())
			{
				p.sendMessage(ChatColor.RED + "You cannot activate AFK right now.");
				pd.playSound(Sound.ENTITY_VILLAGER_NO);
				
				return;
			}
			
			p.sendMessage(ChatColor.GREEN + "AFK mode on.");
			p.setInvulnerable(true);
			pd.playSound(Sound.BLOCK_NOTE_BLOCK_BASS);
			
			pd.addTag("afk");
			
			return;
		}
		
	}

}
