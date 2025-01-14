package net.pgfmc.core;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.event.server.ServerLoadEvent.LoadType;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.luckperms.api.LuckPerms;
import net.pgfmc.core.api.inventory.extra.InventoryPressEvent;
import net.pgfmc.core.api.playerdata.PlayerData;
import net.pgfmc.core.api.playerdata.PlayerDataManager;
import net.pgfmc.core.api.playerdata.cmd.DumpCommand;
import net.pgfmc.core.api.playerdata.cmd.PlayerDataSetCommand;
import net.pgfmc.core.api.playerdata.cmd.TagCommand;
import net.pgfmc.core.api.request.RequestEvents;
import net.pgfmc.core.api.request.RequestType;
import net.pgfmc.core.api.teleport.SpawnProtect;
import net.pgfmc.core.bot.Bot;
import net.pgfmc.core.bot.minecraft.cmd.LinkCommand;
import net.pgfmc.core.bot.minecraft.cmd.UnlinkCommand;
import net.pgfmc.core.bot.minecraft.listeners.OnAsyncPlayerChat;
import net.pgfmc.core.bot.minecraft.listeners.OnPlayerDeath;
import net.pgfmc.core.bot.minecraft.listeners.OnPlayerJoin;
import net.pgfmc.core.bot.minecraft.listeners.OnPlayerQuit;
import net.pgfmc.core.cmd.admin.Skull;
import net.pgfmc.core.cmd.donator.Nick;
import net.pgfmc.core.util.roles.Roles;

/**
 * @author bk and CrimsonDart
 */
public class CoreMain extends JavaPlugin implements Listener {
	
	public static CoreMain plugin;
	
	public static LuckPerms luckPermsAPI;
	
	/**
	 * creates all files, loads all worlds, PlayerData, commands and events.
	 * @author bk
	 */
	@Override
	public void onEnable()
	{
		
		/**
		 * Constants
		 */
		plugin = this;
		
		/**
		 * Create defaults *.yml
		 */
		saveDefaultConfig();
		reloadConfig();
		
		/**
		 * LuckPerms API
		 */
		RegisteredServiceProvider<LuckPerms> lpProvider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
		if (lpProvider != null) {
		    LuckPerms lpAPI = lpProvider.getProvider();
		    luckPermsAPI = lpAPI;
		    
		}
		
		/**
		 * PlayerData initialization
		 */
		PlayerDataManager.setInit(pd -> pd.setData("Name", pd.getName()).queue());
		
		PlayerDataManager.setInit(pd -> {
			
			Map<String, Location> homes = new HashMap<>();
			FileConfiguration db = pd.loadFile();
			
			if (db == null) return;
		
			ConfigurationSection config = db.getConfigurationSection("homes");
			
			if (config != null) {
				config.getKeys(false).forEach(home -> homes.put(home, config.getLocation(home)));
			}
			
			pd.setData("homes", homes);
			
		});
		
		PlayerDataManager.setInit(pd -> {
			
			FileConfiguration db = pd.loadFile();
			
			if (db == null) return;
			
			pd.setData("nick", db.getString("nick"));
			
		});
		
		PlayerDataManager.setInit(pd -> {
			FileConfiguration config = pd.loadFile();
			
			pd.setData("Discord", config.getString("Discord"));
			
		});
		
		PlayerDataManager.setPostLoad(x -> {
			PlayerData.getPlayerDataSet().forEach(pd -> Roles.setRole(pd));
		});
		
		/**
		 * Register commands and listeners
		 */
		getCommand("nick").setExecutor(new Nick());
		
		getCommand("link").setExecutor(new LinkCommand());
		getCommand("unlink").setExecutor(new UnlinkCommand());
		
		getServer().getPluginManager().registerEvents(new InventoryPressEvent(), this);
		getServer().getPluginManager().registerEvents(new PlayerDataManager(), this);
		getServer().getPluginManager().registerEvents(new SpawnProtect(), this);
		getServer().getPluginManager().registerEvents(new RequestEvents(), this);
		
		getServer().getPluginManager().registerEvents(new OnAsyncPlayerChat(), this);
		getServer().getPluginManager().registerEvents(new OnPlayerDeath(), this);
		getServer().getPluginManager().registerEvents(new OnPlayerJoin(), this);
		getServer().getPluginManager().registerEvents(new OnPlayerQuit(), this);
		
		getServer().getPluginManager().registerEvents(new Roles(), this);
		
		getServer().getPluginManager().registerEvents(this, this);
		
		/**
		 * Initialize classes
		 */
		new Skull();
		new DumpCommand();
		new TagCommand();
		new PlayerDataSetCommand();
		new Bot();
	}
	
	@Override
	public void onDisable() {
		Bot.shutdown();
		PlayerDataManager.saveQ();
		RequestType.saveRequestsToFile();
		
	}
	
	@EventHandler
	public void onLoad(ServerLoadEvent e) {
		if (e.getType() == LoadType.RELOAD) return;
		
		PlayerDataManager.initializePlayerData();
		startRestartThread();
		
	}
	
	private void startRestartThread()
	{
		Calendar now = Calendar.getInstance();
		Calendar restartDate = now;
		
		restartDate.add(Calendar.HOUR, 12 - Math.abs(3 - now.get(Calendar.HOUR))); // Finds how many hours until 3 AM/PM
		restartDate.add(Calendar.MINUTE, -1 * now.get(Calendar.MINUTE));
		restartDate.setTimeZone(TimeZone.getDefault()); // ZonedDateTime from restart date and system's time zone
		
		long secondsUntilRestartCountdown = (Duration.between(Instant.now(), restartDate.toInstant()).getSeconds()) - (60 * 10);  // Calculate amount of time to wait until we run.
		
		Bukkit.getLogger().warning("Restart date:" + new SimpleDateFormat("MMM dd, YYYY @ kkmm").format(restartDate.getTime()));
		
		
		Executors.newSingleThreadScheduledExecutor().schedule(new Runnable() {
			
			private int secondsElapsed = 0;

			@Override
			public void run() {
				
				Bukkit.getScheduler().runTaskTimer(CoreMain.plugin, new Runnable() {

					@Override
					public void run() {
						
						switch (secondsElapsed) {
						case 0:
							Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + "Scheduled restart in 10 minutes.");
							break;
						case 60 * 5:
							Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + "Scheduled restart in 5 minutes.");
							break;
						case 60 * 9:
							Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + "Scheduled restart in 60 seconds.");
							break;
						case (60 * 9) + 50:
							Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + "Scheduled restart in 10 seconds."
													+ ChatColor.RED + "\n" + "This won't take long!");
							break;
						case (60 * 9) + 57:
							Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + "Scheduled restart in 3 seconds."
													+ ChatColor.RED + "\n" + "Be back soon!");
							break;
						case 60 * 10:
							Bukkit.shutdown();
							break;
						default:
							break;
							
						}
						
						secondsElapsed += 1;
						
					}
				
				}, 0, 20);
				
			}}, secondsUntilRestartCountdown, TimeUnit.SECONDS);
		
	}
	
}
