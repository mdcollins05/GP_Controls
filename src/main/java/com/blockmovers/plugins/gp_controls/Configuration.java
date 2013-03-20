/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blockmovers.plugins.gp_controls;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.bukkit.Material;

/**
 *
 * @author MattC
 */
public class Configuration {

    GP_Controls plugin = null;
    public List<String> genWorldsEnabled = new ArrayList();
    public List<String> buildWhitelist = new ArrayList();
    //public List<String> destroyWhitelist = new ArrayList();
    public Map<Long, Boolean> PVPList = new HashMap();
    public Map<Long, Boolean> MobList = new HashMap();
    public Map<Long, Boolean> AnimalList = new HashMap();
    public Boolean defaultPVP = false;
    public Boolean defaultMobs = false;
    public Boolean defaultAnimals = false;

    public Configuration(GP_Controls plugin) {
        this.plugin = plugin;
    }

    public void reloadConfiguration() {
        this.plugin.reloadConfig();
        this.loadConfiguration();
    }

    public void loadConfiguration() {
        plugin.getConfig().addDefault("general.worlds.enable", "");
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
        plugin.getConfig().addDefault("default.enable.animals", true);

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
        this.genWorldsEnabled = plugin.getConfig().getStringList("general.worlds.enable");
        this.buildWhitelist = plugin.getConfig().getStringList("build.whitelist");
        List templist = new ArrayList();
        for (String s : buildWhitelist) {
            templist.add(s.toLowerCase());
        }
        this.buildWhitelist = templist;

        this.PVPList = this.parseStringList(plugin.getConfig().getStringList("pvp"));
        this.MobList = this.parseStringList(plugin.getConfig().getStringList("mobs"));
        this.AnimalList = this.parseStringList(plugin.getConfig().getStringList("animals"));

        this.defaultPVP = plugin.getConfig().getBoolean("default.enable.pvp");
        this.defaultMobs = plugin.getConfig().getBoolean("default.enable.mobs");
        this.defaultAnimals = plugin.getConfig().getBoolean("default.enable.animals");

    }
    
    public void setListValue(String node, List value) {
        plugin.getConfig().set(node, value);
        plugin.saveConfig();
    }

    public Map parseStringList(List<String> theList) {
        Map<Long, Boolean> theMap = new HashMap();
        for (String s : theList) {
            String[] allSplitUp = s.split(":");
            try {
                theMap.put(Long.valueOf(allSplitUp[0]), Boolean.valueOf(allSplitUp[1]));
            } catch(Exception e) {
                this.plugin.log.severe("Something went wrong in the config (Did you change something by hand?)");
            }
        }
        return theMap;
    }

    public List<String> parseLongBooleanMap(Map<Long, Boolean> theMap) {
        List<String> theList = new ArrayList();
        Iterator entries = theMap.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry entry = (Map.Entry) entries.next();
            theList.add(entry.getKey() + ":" + entry.getValue());
        }
        return theList;
    }

    public boolean toggleMapClaim(Long cid, Map<Long, Boolean> map, Boolean defValue) {
        if (map.containsKey(cid)) {
            if (map.get(cid)) {
                map.put(cid, false);
                return false;
            } else {
                map.put(cid, true);
                return true;
            }
        } else {
            map.put(cid, !defValue);
            return !defValue;
        }
    }

    public boolean togglePVP(Long cid) {
        Boolean value = this.toggleMapClaim(cid, this.PVPList, this.defaultPVP);
        this.plugin.getConfig().set("pvp", this.parseLongBooleanMap(PVPList));
        this.plugin.saveConfig();
        return value;
    }

    public boolean toggleMobs(Long cid) {
        Boolean value = this.toggleMapClaim(cid, this.MobList, this.defaultMobs);
        this.plugin.getConfig().set("mobs", this.parseLongBooleanMap(MobList));
        this.plugin.saveConfig();
        return value;
    }

    public boolean toggleAnimals(Long cid) {
        Boolean value = this.toggleMapClaim(cid, this.AnimalList, this.defaultAnimals);
        this.plugin.getConfig().set("animals", this.parseLongBooleanMap(AnimalList));
        this.plugin.saveConfig();
        return value;
    }

    public boolean getBooleanClaim(Long cid, Map<Long, Boolean> map, Boolean defValue) {
        if (map.containsKey(cid)) {
            return map.get(cid);
        }
        return defValue;
    }

    public boolean getPVP(Long cid) {
        return this.getBooleanClaim(cid, this.PVPList, this.defaultPVP);
    }

    public boolean getMobs(Long cid) {
        return this.getBooleanClaim(cid, this.MobList, this.defaultMobs);
    }

    public boolean getAnimals(Long cid) {
        return this.getBooleanClaim(cid, this.AnimalList, this.defaultAnimals);
    }
    
    public boolean toggleItem(Boolean node) {
        return !node;
    }
    
    public boolean toggleDefaultPVP() {
        boolean newValue = this.toggleItem(this.defaultPVP);
        this.defaultPVP = newValue;
        this.plugin.getConfig().set("default.enable.pvp", newValue);
        this.plugin.saveConfig();
        return newValue;
    }
    
    public boolean toggleDefaultMobs() {
        boolean newValue = this.toggleItem(this.defaultMobs);
        this.defaultMobs = newValue;
        this.plugin.getConfig().set("default.enable.mobs", newValue);
        this.plugin.saveConfig();
        return newValue;
    }
    
    public boolean toggleDefaultAnimals() {
        boolean newValue = this.toggleItem(this.defaultAnimals);
        this.defaultAnimals = newValue;
        this.plugin.getConfig().set("default.enable.animals", newValue);
        this.plugin.saveConfig();
        return newValue;
    }
}
