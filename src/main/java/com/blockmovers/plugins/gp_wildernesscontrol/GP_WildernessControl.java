package com.blockmovers.plugins.gp_wildernesscontrol;

import java.util.logging.Logger;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class GP_WildernessControl extends JavaPlugin implements Listener {
    static final Logger log = Logger.getLogger("Minecraft"); //set up our logger
    private Configuration config = new Configuration(this);
    
    public void onEnable() {
        PluginDescriptionFile pdffile = this.getDescription();
        PluginManager pm = this.getServer().getPluginManager(); //the plugin object which allows us to add listeners later on

        pm.registerEvents(this, this);
        
        this.config.loadConfiguration();

        log.info(pdffile.getName() + " version " + pdffile.getVersion() + " is enabled.");
    }

    public void onDisable() {
        PluginDescriptionFile pdffile = this.getDescription();

        log.info(pdffile.getName() + " version " + pdffile.getVersion() + " is disabled.");
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Player p = event.getPlayer();
        if (p.hasPermission("gp_wc.exempt.build")) {
            return;
        }
        if (!this.inClaim(event.getBlock().getLocation())) {
            //ToDo: Add whitelisting/blacklisting of blocks if not inside claims
            String block = event.getBlockPlaced().getType().name().toLowerCase();
            if (!this.config.buildWhitelist.contains(block)) {
                event.setCancelled(true);
                p.sendMessage(ChatColor.RED + "You cant place " + block + " here!");
            }
        }
    }
    
//    @EventHandler
//    public void onBlockBreak(BlockBreakEvent event) {
//        if (event.isCancelled()) {
//            return;
//        }
//        Player p = event.getPlayer();
//        if (p.hasPermission("gp_wc.exempt.destroy")) {
//            return;
//        }
//        if (!this.inClaim(p)) {
//            //ToDo: Add whitelisting/blacklisting of blocks if not inside claims
//        }
//    }
    
    private boolean inClaim(Location l) {
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(l, true, null);
        if (claim == null) { //Not in a claim
            return false;
        }
        return true;
    }
}

