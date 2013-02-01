/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blockmovers.plugins.gp_controls;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

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
    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Long claimID = this.plugin.util.getClaimID(event.getLocation());
        //We listen for custom because that's a plugin spawning 'em
        if (event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.CHUNK_GEN)
                || event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.NATURAL)
                || event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.CUSTOM)) {
            if (this.plugin.goodMobs.contains(event.getEntityType().getName())) {
                if (!this.plugin.config.getAnimals(claimID)) {
                    event.setCancelled(true);
                }
            } else {
                if (!this.plugin.config.getMobs(claimID)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Entity att = event.getDamager();
        Player attacker = null;
        if (att instanceof Player) {
            attacker = (Player) att;
        } else if (att instanceof Projectile) {
            Projectile arr = (Projectile) att;
            Entity e = arr.getShooter();
            if (e instanceof Player) {
                attacker = (Player) e;
            }
        }

        if (event.getEntity() instanceof Player && attacker instanceof Player) {
            Player attacked = (Player) event.getEntity();
            boolean attackerPVP = this.plugin.config.getPVP(this.plugin.util.getClaimID(attacker.getLocation()));
            boolean attackedPVP = this.plugin.config.getPVP(this.plugin.util.getClaimID(attacked.getLocation()));
            Boolean cancel = !(attackerPVP & attackedPVP);
            if (cancel) {
                attacker.sendMessage(ChatColor.RED + "PVP is disabled here!");
                //attacked.sendMessage(ChatColor.RED + attacker.getName() + " is attempting to attack you!");
                event.setCancelled(true);
            }
        }
    }
}
