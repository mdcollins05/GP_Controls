package com.blockmovers.plugins.gp_controls;

import java.util.logging.Logger;
import me.ryanhamshire.GriefPrevention.Claim;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
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

    public void onEnable() {
        PluginDescriptionFile pdffile = this.getDescription();
        PluginManager pm = this.getServer().getPluginManager(); //the plugin object which allows us to add listeners later on

        pm.registerEvents(new Listeners(this), this);

        this.config.loadConfiguration();

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
                        Claim c = this.util.getClaim(p.getLocation());
                        if (c == null) {
                            claimID = -1l;
                            claimOwner = "";
                        } else {
                            claimID = c.getID();
                            claimOwner = c.getOwnerName();
                        }
                    }
                    if (!cs.getName().equals(claimOwner)) { //not the owner of the claim or in the wild
                        if (args[0].equalsIgnoreCase("pvp")) {
                            if (!this.util.hasServerPerm(cs, true, "gp_c.toggle.pvp.any")) {
                                cs.sendMessage(ChatColor.RED + "You do not have permissions to do this! (gp_c.toggle.pvp.any");
                                return true;
                            }
                        } else {
                            if (!this.util.hasServerPerm(cs, true, "gp_c.toggle.mobs.any")) {
                                cs.sendMessage(ChatColor.RED + "You do not have permissions to do this! (gp_c.toggle.mobs.any)");
                                return true;
                            }
                        }
                    } else { //owner of the claim
                        if (args[0].equalsIgnoreCase("pvp")) {
                            if (!this.util.hasServerPerm(cs, false, "gp_c.toggle.pvp.self")) {
                                cs.sendMessage(ChatColor.RED + "You do not have permissions to do this! (gp_c.toggle.pvp.self)");
                                return true;
                            }
                        } else {
                            if (!this.util.hasServerPerm(cs, false, "gp_c.toggle.mobs.self")) {
                                cs.sendMessage(ChatColor.RED + "You do not have permission to do this! (gp_c.toggle.mobs.self)");
                                return true;
                            }
                        }
                    }
                    if (args[0].equalsIgnoreCase("mobs")) {
                        if (this.config.toggleMobs(claimID)) {
                            cs.sendMessage(ChatColor.GREEN + "You've toggled mobs in this claim ON!");
                        } else {
                            cs.sendMessage(ChatColor.GREEN + "You've toggled mobs in this claim OFF!");
                        }
                    } else if (args[0].equalsIgnoreCase("animals")) {
                        if (this.config.toggleAnimals(claimID)) {
                            cs.sendMessage(ChatColor.GREEN + "You've toggled animals in this claim ON!");
                        } else {
                            cs.sendMessage(ChatColor.GREEN + "You've toggled animals in this claim OFF!");
                        }
                    } else if (args[0].equalsIgnoreCase("pvp")) {
                        if (this.config.togglePVP(claimID)) {
                            cs.sendMessage(ChatColor.GREEN + "You've toggled PVP in this claim ON!");
                        } else {
                            cs.sendMessage(ChatColor.GREEN + "You've toggled PVP in this claim OFF!");
                        }
                    }

                } else if (args[0].equalsIgnoreCase("view")) {
                    if (!(cs instanceof Player)) { //Is most likely console
                        claimID = -1l;
                    } else {
                        p = (Player) cs;
                        Claim c = this.util.getClaim(p.getLocation());
                        if (c == null) {
                            claimID = -1l;
                            claimOwner = "";
                        } else {
                            claimID = c.getID();
                            claimOwner = c.getOwnerName();
                        }
                    }
                } else {
                    return false;
                }
            } else {
                cs.sendMessage(this.msg_prefix + ChatColor.RED + "Valid options are version, mobs, animals, pvp and view");
                return true;
            }
            return true;
        }
        return false;
    }
}
