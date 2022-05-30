package net.pgfmc.claims;

import org.bukkit.plugin.java.JavaPlugin;

import net.pgfmc.claims.ownable.OwnableFile;
import net.pgfmc.claims.ownable.block.events.BBEvent;
import net.pgfmc.claims.ownable.block.events.BExEvent;
import net.pgfmc.claims.ownable.block.events.BPE;
import net.pgfmc.claims.ownable.block.events.BlockInteractEvent;
import net.pgfmc.claims.ownable.entities.TameEvent;
import net.pgfmc.claims.ownable.inspector.ClaimTPCommand;
import net.pgfmc.claims.ownable.inspector.EditOwnableCommand;
import net.pgfmc.claims.ownable.inspector.InspectCommand;
import net.pgfmc.core.file.Mixins;
import net.pgfmc.core.playerdataAPI.PlayerDataManager;

public class Main extends JavaPlugin {
	
	// all relevant file paths.
	public static final String BlockContainersPath = "plugins\\PGF-Claims\\BlockContainers.yml";
	public static final String EntityContainersPath = "plugins\\PGF-Claims\\EntityContainers.yml";
	
	public static Main plugin;
	
	@Override
	public void onEnable() {
		
		plugin = this;
		plugin.saveDefaultConfig();
		plugin.reloadConfig();
		
		// loads files.
		Mixins.getFile(BlockContainersPath);
		Mixins.getFile(EntityContainersPath);
		
		// new EscapeCommand();
		
		PlayerDataManager.setPostLoad((x) -> OwnableFile.loadContainers());
		
		// initializes all Event Listeners and Command Executors.
		getServer().getPluginManager().registerEvents(new BlockInteractEvent(), this);
		getServer().getPluginManager().registerEvents(new BBEvent(), this);
		getServer().getPluginManager().registerEvents(new BPE(), this);
		getServer().getPluginManager().registerEvents(new TameEvent(), this);
		getServer().getPluginManager().registerEvents(new BExEvent(), this);
		
		getCommand("inspector").setExecutor(new InspectCommand());
		getCommand("editownable").setExecutor(new EditOwnableCommand());
		getCommand("claimtp").setExecutor(new ClaimTPCommand());
	}
	
	@Override
	public void onDisable() {
		OwnableFile.saveContainers();
	}
	
	public static JavaPlugin getPlugin() {
		return plugin;
	}
}
