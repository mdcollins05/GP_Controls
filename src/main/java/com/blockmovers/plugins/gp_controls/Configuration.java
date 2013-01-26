/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blockmovers.plugins.gp_controls;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;

/**
 *
 * @author MattC
 */
public class Configuration {

    GP_Controls plugin = null;
    public List<String> buildWhitelist = new ArrayList();
    //public List<String> destroyWhitelist = new ArrayList();
    public List<Long> disablePVPList = new ArrayList();
    public List<Long> disableMobList = new ArrayList();
    public Boolean defaultPVP = false;
    public Boolean defaultMob = false;

    public Configuration(GP_Controls plugin) {
        this.plugin = plugin;
    }

    public void reloadConfiguration() {
        this.plugin.reloadConfig();
        this.loadConfiguration();
    }

    public void loadConfiguration() {
        List<String> buildwhitelist = new ArrayList();
        buildwhitelist.add(Material.CHEST.name());
        buildwhitelist.add(Material.DIRT.name());
        buildwhitelist.add(Material.CROPS.name());
        buildwhitelist.add(Material.CARROT.name());
        buildwhitelist.add(Material.POTATO.name());
        buildwhitelist.add(Material.GRASS.name());
        buildwhitelist.add(Material.GRAVEL.name());
        buildwhitelist.add(Material.SAND.name());
        buildwhitelist.add(Material.LOG.name());
        buildwhitelist.add(Material.LADDER.name());
        buildwhitelist.add(Material.CACTUS.name());
        buildwhitelist.add(Material.TORCH.name());
        buildwhitelist.add(Material.VINE.name());
        buildwhitelist.add(Material.STONE.name());
        buildwhitelist.add(Material.SAPLING.name());
        plugin.getConfig().addDefault("build.whitelist", buildwhitelist);
        plugin.getConfig().addDefault("default.enable.pvp", true);
        plugin.getConfig().addDefault("default.enable.mobs", true);

        //List<String> destroywhitelist = new ArrayList();
        //destroywhitelist.add("login");
        //destroywhitelist.add("auth");
        //plugin.getConfig().addDefault("commands.ignore", destroywhitelist);

        plugin.getConfig().options().copyDefaults(true);
        //Save the config whenever you manipulate it
        plugin.saveConfig();

        this.setVars();
    }

    public void setVars() {
        this.buildWhitelist = plugin.getConfig().getStringList("build.whitelist");
        List templist = new ArrayList();
        for (String s : buildWhitelist) {
            templist.add(s.toLowerCase());
        }
        this.buildWhitelist = templist;

        this.disablePVPList = plugin.getConfig().getLongList("disable.pvp");
        this.disableMobList = plugin.getConfig().getLongList("disable.mobs");
    }

    public void setListValue(String node, List value) {
        plugin.getConfig().set(node, value);
        plugin.saveConfig();
    }

    public boolean togglePVP(Long cid) {
        if (this.disablePVPList.contains(cid)) {
            this.disablePVPList.remove(cid);
            return true;
        } else {
            this.disablePVPList.add(cid);
            return false;
        }
    }
    
    public boolean toggleMobs(Long cid) {
        if (this.disableMobList.contains(cid)) {
            this.disableMobList.remove(cid);
            return true;
        } else {
            this.disableMobList.add(cid);
            return false;
        }
    }
}