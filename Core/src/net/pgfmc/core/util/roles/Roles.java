package net.pgfmc.core.util.roles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
import net.pgfmc.core.CoreMain;
import net.pgfmc.core.api.playerdata.PlayerData;
import net.pgfmc.core.bot.discord.Discord;

public class Roles implements Listener {
	
	/**
	 * Set and apply roles to player (update roles)
	 * 
	 * @param pd The player to update roles
	 * @param role The role to apply
	 */
	public static void setRole(PlayerData pd, PGFRole role)
	{
		Bukkit.getLogger().warning("Recalculating role for player " + pd.getName());
		
		UserManager userManager = CoreMain.luckPermsAPI.getUserManager();
		
		// Remove then add groups, save changes
		userManager.modifyUser(pd.getUniqueId(), user -> {
	        user.data().clear(NodeType.INHERITANCE::matches);
	        
	        if (role.getName().equals("member"))
			{
				Node node = Node.builder("group.default").build();
				Bukkit.getLogger().warning("Updated roles: " + user.data().add(node).toString());
				
			} else
			{
	            Node node = Node.builder("group." + role.getName()).build();
	            Bukkit.getLogger().warning("Updated roles: " + user.data().add(node).toString());
	            
			}
	        
		});
		
		pd.setData("role", role);
		
	}
	
	public static void setRole(PlayerData pd)
	{
		// Get roles, get top role
		List<PGFRole> playerRoles = getPlayerRoles(pd);
		PGFRole role = getTop(playerRoles);
		
		setRole(pd, role);
		
	}	
	
	public static List<PGFRole> getPlayerRoles(PlayerData pd)
	{
		if (pd == null) return new ArrayList<PGFRole>(Arrays.asList(PGFRole.MEMBER));
		
		List<String> rolesAsString = Discord.getMemberRoles(pd.getData("Discord"));
		if (rolesAsString == null || rolesAsString.isEmpty()) return new ArrayList<PGFRole>(Arrays.asList(PGFRole.MEMBER));
		
		// Takes a list of string names and gets PGFRole enums and potential null values
		// Then removes the null values
		return rolesAsString.stream()
				.map(r -> PGFRole.get(r))
				.collect(Collectors.toList()).stream()
				.filter(r -> r != null)
				.collect(Collectors.toList());
		
	}
	
	public static PGFRole getTop(Collection<PGFRole> roles)
	{
		// Return MEMBER Role if null or empty
		// Return the only Role if only 1 Role in list
		if (roles == null || roles.isEmpty()) return PGFRole.MEMBER;
		if (roles.size() == 1) return roles.stream().collect(Collectors.toList()).get(0);
		
		// Quick sort against the order of the enums to find the top Role
		return roles.stream()
				.sorted((r1, r2) -> r1.compareTo(r2))
				.collect(Collectors.toList())
				.get(0);
		
	}
	public static PGFRole getTop(PlayerData pd)
	{
		return getTop(getPlayerRoles(pd));
	}
	
	@EventHandler(priority = EventPriority.LOW) // Runs first before all others!
	public void assignPlayerRoleOnJoin(PlayerJoinEvent e)
	{
		PlayerData pd = PlayerData.from(e.getPlayer());
		
		setRole(pd);
	}
	
}
