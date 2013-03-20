package com.blockmovers.plugins.gp_controls;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class GP_Controls extends JavaPlugin implements Listener {

    static final Logger log = Logger.getLogger("Minecraft"); //set up our logger
    public Configuration config = new Configuration(this);
    public Utilities util = new Utilities(this);
    public String msg_prefix = ChatColor.WHITE + "[" + ChatColor.GRAY + "GP_C" + ChatColor.WHITE + "] ";
    public List<String> goodMobs = new ArrayList();

    public void onEnable() {
        PluginDescriptionFile pdffile = this.getDescription();
        PluginManager pm = this.getServer().getPluginManager(); //the plugin object which allows us to add listeners later on

        pm.registerEvents(new Listeners(this), this);

        this.config.loadConfiguration();
        this.setMobLists();

        log.info(pdffile.getName() + " version " + pdffile.getVersion() + " is enabled.");
    }

    public void onDisable() {
        PluginDescriptionFile pdffile = this.getDescription();

        log.info(pdffile.getName() + " version " + pdffile.getVersion() + " is disabled.");
    }

    public boolean onCommand(CommandSender cs, Command cmd, String alias, String[] args) {
        if (cmd.getName().equalsIgnoreCase("wild")) {
            if (args.length >= 1) {
                if (args[0].equalsIgnoreCase("version")) {
                    PluginDescriptionFile pdf = this.getDescription();
                    cs.sendMessage(this.msg_prefix + pdf.getName() + " " + pdf.getVersion() + " by MDCollins05");
                    return true;
                } else if (args[0].equalsIgnoreCase("build")) {
                    if (this.util.hasServerPerm(cs, true, "gp_c.modify.buildlist")) {
                        if (args.length <= 1) {
                            cs.sendMessage(this.msg_prefix + ChatColor.RED + "Valid options are list, add, remove.");
                            return true;
                        }
                        if (args[1].equalsIgnoreCase("list")) {
                            cs.sendMessage(this.msg_prefix + ChatColor.GREEN + "Build list: Block(ID): " + ChatColor.RESET + this.util.list2String(this.config.buildWhitelist));
                        } else if (args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("remove")) {
                            if (args.length <= 2) {
                                cs.sendMessage(this.msg_prefix + ChatColor.RED + "You must specify an item name or ID.");
                                return true;
                            }
                            Material material = null;
                            if (this.util.isInteger(args[2])) {
                                material = Material.getMaterial(Integer.valueOf(args[2]));
                            } else {
                                material = Material.matchMaterial(args[2]);
                            }
                            if (material == null) {
                                cs.sendMessage(this.msg_prefix + ChatColor.RED + "Unknown material '" + args[2] + "'!");
                                return true;
                            }
                            //if (args.length == 4) { //do stuff for world specific stuff vs global
                            //    
                            //}
                            Boolean onList = this.config.buildWhitelist.contains(material.name().toLowerCase());
                            if (args[1].equalsIgnoreCase("add")) {
                                if (onList) {
                                    cs.sendMessage(this.msg_prefix + ChatColor.RED + material.name() + " is already on the list!");
                                    return true;
                                }
                                this.config.buildWhitelist.add(material.name().toLowerCase());
                                this.config.setListValue("build.whitelist", this.config.buildWhitelist);
                                cs.sendMessage(this.msg_prefix + ChatColor.GREEN + "Added '" + material.name() + "' to the build list!");
                            } else if (args[1].equalsIgnoreCase("remove")) {
                                if (!onList) {
                                    cs.sendMessage(this.msg_prefix + ChatColor.RED + material.name() + " is not on the list!");
                                    return true;
                                }
                                this.config.buildWhitelist.remove(material.name().toLowerCase());
                                this.config.setListValue("build.whitelist", this.config.buildWhitelist);
                                cs.sendMessage(this.msg_prefix + ChatColor.GREEN + "Removed '" + material.name() + "' from the build list!");
                            }
                            return true;
                        } else {
                            cs.sendMessage(this.msg_prefix + ChatColor.RED + "That is not a valid option!");
                        }
                    } else {
                        cs.sendMessage(this.msg_prefix + ChatColor.RED + "You don't have permission to do this!");
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                cs.sendMessage(this.msg_prefix + ChatColor.RED + "Valid options are version and build");
                return true;
            }
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("toggle")) {
            Long claimID = null;
            String claimOwner = null;
            Player p = null;
            if (args.length >= 1) {
                if (args[0].equalsIgnoreCase("version")) {
                    PluginDescriptionFile pdf = this.getDescription();
                    cs.sendMessage(this.msg_prefix + pdf.getName() + " " + pdf.getVersion() + " by MDCollins05");
                    return true;
                } else if (args[0].equalsIgnoreCase("mobs") || args[0].equalsIgnoreCase("animals") || args[0].equalsIgnoreCase("pvp")) {
                    if (!(cs instanceof Player)) { //Is most likely console
                        claimID = -1l;
                    } else {
                        p = (Player) cs;
                        claimID = this.util.getClaimID(p.getLocation());
                        claimOwner = this.util.getClaimOwner(p.getLocation());
                    }
                    if (!cs.getName().equals(claimOwner)) { //not the owner of the claim or in the wild
                        if (args[0].equalsIgnoreCase("pvp")) {
                            if (!this.util.hasServerPerm(cs, true, "gp_c.toggle.pvp.any")) {
                                cs.sendMessage(this.msg_prefix + ChatColor.RED + "You do not have permissions to do this! (gp_c.toggle.pvp.any");
                                return true;
                            }
                        } else {
                            if (!this.util.hasServerPerm(cs, true, "gp_c.toggle.mobs.any")) {
                                cs.sendMessage(this.msg_prefix + ChatColor.RED + "You do not have permissions to do this! (gp_c.toggle.mobs.any)");
                                return true;
                            }
                        }
                    } else { //owner of the claim
                        if (args[0].equalsIgnoreCase("pvp")) {
                            if (!this.util.hasServerPerm(cs, false, "gp_c.toggle.pvp.self")) {
                                cs.sendMessage(this.msg_prefix + ChatColor.RED + "You do not have permissions to do this! (gp_c.toggle.pvp.self)");
                                return true;
                            }
                        } else {
                            if (!this.util.hasServerPerm(cs, false, "gp_c.toggle.mobs.self")) {
                                cs.sendMessage(this.msg_prefix + ChatColor.RED + "You do not have permission to do this! (gp_c.toggle.mobs.self)");
                                return true;
                            }
                        }
                    }
                    if (args[0].equalsIgnoreCase("mobs")) {
                        if (this.config.toggleMobs(claimID)) {
                            cs.sendMessage(this.msg_prefix + ChatColor.GREEN + "You've toggled mobs in this claim ON!");
                        } else {
                            cs.sendMessage(this.msg_prefix + ChatColor.GREEN + "You've toggled mobs in this claim OFF!");
                        }
                    } else if (args[0].equalsIgnoreCase("animals")) {
                        if (this.config.toggleAnimals(claimID)) {
                            cs.sendMessage(this.msg_prefix + ChatColor.GREEN + "You've toggled animals in this claim ON!");
                        } else {
                            cs.sendMessage(this.msg_prefix + ChatColor.GREEN + "You've toggled animals in this claim OFF!");
                        }
                    } else if (args[0].equalsIgnoreCase("pvp")) {
                        if (this.config.togglePVP(claimID)) {
                            cs.sendMessage(this.msg_prefix + ChatColor.GREEN + "You've toggled PVP in this claim ON!");
                        } else {
                            cs.sendMessage(this.msg_prefix + ChatColor.GREEN + "You've toggled PVP in this claim OFF!");
                        }
                    }
                } else if (args[0].equalsIgnoreCase("info")) {
                    if (!(cs instanceof Player)) { //Is most likely console
                        claimID = -1l;
                    } else {
                        p = (Player) cs;
                        claimID = this.util.getClaimID(p.getLocation());
                        claimOwner = this.util.getClaimOwner(p.getLocation());
                    }
                    if (claimOwner == null) {
                        claimOwner = "An Administrator";
                    }
                    cs.sendMessage(this.msg_prefix + ChatColor.GREEN + "ClaimID: " + claimID + "; ClaimOwner:" + claimOwner);
                } else if (args[0].equalsIgnoreCase("clean")) {
                    //For the future when I can get a claim by ID to remove IDs that no longer exist (please bigscary? :D)
                } else if (args[0].equalsIgnoreCase("default")) {
                    if (args.length == 2) {
                        if (!this.util.hasServerPerm(cs, true, "gp_c.toggle.defaults")) {
                            cs.sendMessage(this.msg_prefix + ChatColor.RED + "You do not have permissions to do this! (gp_c.toggle.defaults");
                            return true;
                        }
                        if (args[1].equalsIgnoreCase("pvp")) {
                            if (this.config.toggleDefaultPVP()) {
                                cs.sendMessage(this.msg_prefix + ChatColor.GREEN + "You've toggled the default PVP option to ON!");
                            } else {
                                cs.sendMessage(this.msg_prefix + ChatColor.GREEN + "You've toggled the default PVP option to OFF!");
                            }
                        } else if (args[1].equalsIgnoreCase("mobs")) {
                            if (this.config.toggleDefaultMobs()) {
                                cs.sendMessage(this.msg_prefix + ChatColor.GREEN + "You've toggled the default Mobs option to ON!");
                            } else {
                                cs.sendMessage(this.msg_prefix + ChatColor.GREEN + "You've toggled the default Mobs option to OFF!");
                            }
                        } else if (args[1].equalsIgnoreCase("animals")) {
                            if (this.config.toggleDefaultAnimals()) {
                                cs.sendMessage(this.msg_prefix + ChatColor.GREEN + "You've toggled the default Animals option to ON!");
                            } else {
                                cs.sendMessage(this.msg_prefix + ChatColor.GREEN + "You've toggled the default Animals option to OFF!");
                            }
                        }
                    } else {
                        cs.sendMessage(this.msg_prefix + ChatColor.RED + "Valid options are pvp, mobs and animals");
                    }
                } else {
                    return false;
                }
            } else {
                if (!(cs instanceof Player)) { //Is most likely console
                    claimID = -1l;
                } else {
                    p = (Player) cs;
                    claimID = this.util.getClaimID(p.getLocation());
                }
                StringBuilder sb = new StringBuilder();
                sb.append("Where you stand; PVP: ");
                if (this.config.getPVP(claimID)) {
                    sb.append("ON");
                } else {
                    sb.append("OFF");
                }
                sb.append("; Mobs: ");
                if (this.config.getMobs(claimID)) {
                    sb.append("ON");
                } else {
                    sb.append("OFF");
                }
                sb.append("; Animals: ");
                if (this.config.getAnimals(claimID)) {
                    sb.append("ON");
                } else {
                    sb.append("OFF");
                }
                cs.sendMessage(this.msg_prefix + sb.toString());
                cs.sendMessage(this.msg_prefix + ChatColor.RED + "Valid options are version, mobs, animals, pvp and default");
                return true;
            }
            return true;
        }
        return false;
    }

    private void setMobLists() {
        this.goodMobs.add(EntityType.CHICKEN.getName());
        this.goodMobs.add(EntityType.COW.getName());
        this.goodMobs.add(EntityType.IRON_GOLEM.getName());
        this.goodMobs.add(EntityType.OCELOT.getName());
        this.goodMobs.add(EntityType.PIG.getName());
        this.goodMobs.add(EntityType.SHEEP.getName());
        this.goodMobs.add(EntityType.SQUID.getName());
        this.goodMobs.add(EntityType.VILLAGER.getName());
        //We assume others to be bad.. Yes, this may not the the best idea, but
        //in the event of an update, this won't break any code that uses this list
        //and I'd rather new mobs be assumed bad and disallow, rather than assume
        //good and allow something bad. ;)
    }
}
