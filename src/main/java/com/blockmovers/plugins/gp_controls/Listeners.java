/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blockmovers.plugins.gp_controls;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 *
 * @author MattC
 */
public class Listeners implements Listener {

    GP_Controls plugin = null;

    public Listeners(GP_Controls plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Player p = event.getPlayer();
        if (p.hasPermission("gp_c.exempt.build")) {
            return;
        }
        if (!this.plugin.util.inClaim(event.getBlock().getLocation())) {
            //ToDo: Add whitelisting/blacklisting of blocks if not inside claims
            String block = event.getBlockPlaced().getType().name().toLowerCase();
            if (!this.plugin.config.buildWhitelist.contains(block)) {
                event.setCancelled(true);
                p.sendMessage(this.plugin.msg_prefix + ChatColor.RED + "You cant place " + block + " in the wild!");
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
}
