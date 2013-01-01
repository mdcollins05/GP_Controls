/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blockmovers.plugins.gp_wildernesscontrol;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;

/**
 *
 * @author MattC
 */
public class Configuration {
    
    GP_WildernessControl plugin = null;
    public String seperator = ".";
    public List<String> buildWhitelist = new ArrayList();
    //public List<String> destroyWhitelist = new ArrayList();
    

    public Configuration(GP_WildernessControl plugin) {
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
        buildWhitelist = templist;
    }
}
