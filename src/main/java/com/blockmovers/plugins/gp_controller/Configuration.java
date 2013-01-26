/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blockmovers.plugins.gp_controller;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;

/**
 *
 * @author MattC
 */
public class Configuration {

    GP_Controller plugin = null;
    public List<String> buildWhitelist = new ArrayList();
    //public List<String> destroyWhitelist = new ArrayList();
    public List<Long> DisablePVPList = new ArrayList();
    public List<Long> DisableMobList = new ArrayList();

    public Configuration(GP_Controller plugin) {
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

        this.DisablePVPList = plugin.getConfig().getLongList("disable.pvp");
        this.DisableMobList = plugin.getConfig().getLongList("disable.mobs");
    }

    public void setListValue(String node, List value) {
        plugin.getConfig().set(node, value);
        plugin.saveConfig();
    }

    public boolean togglePVP(Long cid) {
        if (this.DisablePVPList.contains(cid)) {
            this.DisablePVPList.remove(cid);
            return true;
        } else {
            this.DisablePVPList.add(cid);
            return false;
        }
    }
    
    public boolean toggleMobs(Long cid) {
        if (this.DisableMobList.contains(cid)) {
            this.DisableMobList.remove(cid);
            return true;
        } else {
            this.DisableMobList.add(cid);
            return false;
        }
    }
}
