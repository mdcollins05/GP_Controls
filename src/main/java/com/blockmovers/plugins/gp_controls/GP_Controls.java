package com.blockmovers.plugins.gp_controls;

import java.util.List;
import java.util.logging.Logger;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.ChatPaginator;

public class GP_Controls extends JavaPlugin implements Listener {

    static final Logger log = Logger.getLogger("Minecraft"); //set up our logger
    private Configuration config = new Configuration(this);
    private String msg_prefix = ChatColor.GOLD + "[" + ChatColor.WHITE + "GP_C" + ChatColor.GOLD + "] ";

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

    public boolean onCommand(CommandSender cs, Command cmd, String alias, String[] args) {
        if (cmd.getName().equalsIgnoreCase("wild")) {
            if (args.length >= 1) {
                if (args[0].equalsIgnoreCase("version")) {
                    PluginDescriptionFile pdf = this.getDescription();
                    cs.sendMessage(this.msg_prefix + pdf.getName() + " " + pdf.getVersion() + " by MDCollins05");
                    return true;
                } else if (args[0].equalsIgnoreCase("build")) {
                    if (this.hasServerPerm(cs, true, "gp_c.modify.buildlist")) {
                        if (args.length <= 1) {
                            cs.sendMessage(this.msg_prefix + ChatColor.RED + "Valid options are list, add, remove.");
                            return true;
                        }
                        if (args[1].equalsIgnoreCase("list")) {
                            cs.sendMessage(this.msg_prefix + ChatColor.GREEN + "Build list: Block(ID): " + ChatColor.RESET + this.list2String(this.config.buildWhitelist));
                        } else if (args[1].equalsIgnoreCase("add")) {
                            if (args.length <= 2) {
                                cs.sendMessage(this.msg_prefix + ChatColor.RED + "You must specify an item name or ID.");
                                return true;
                            }
                            Material material = null;
                            if (this.isInteger(args[2])) {
                                material = Material.getMaterial(Integer.valueOf(args[2]));
                            } else {
                                material = Material.matchMaterial(args[2]);
                            }
                            if (material == null) {
                                cs.sendMessage(this.msg_prefix + ChatColor.RED + "Unknown material '" + args[2] + "'!");
                                return true;
                            }
                            if (this.config.buildWhitelist.contains(material.name().toLowerCase())) {
                                cs.sendMessage(this.msg_prefix + ChatColor.RED + material.name() + " is already on the list!");
                                return true;
                            }
                            this.config.buildWhitelist.add(material.name().toLowerCase());
                            this.config.setListValue("build.whitelist", this.config.buildWhitelist);
                            cs.sendMessage(this.msg_prefix + ChatColor.GREEN + "Added '" + material.name() + "' to the build list!");
                            return true;
                        } else if (args[1].equalsIgnoreCase("remove")) {
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
            if (args.length >= 1) {
                if (args[0].equalsIgnoreCase("version")) {
                    PluginDescriptionFile pdf = this.getDescription();
                    cs.sendMessage(this.msg_prefix + pdf.getName() + " " + pdf.getVersion() + " by MDCollins05");
                    return true;
                } else if (args[0].equalsIgnoreCase("pvp")) {
                    if (!(cs instanceof Player)) {
                        claimID = -1l;
                    } else {
                        Player p = (Player) cs;
                        Claim c = this.getClaim(p.getLocation());
                        claimID = c.getID();
                        String claimOwner = c.getOwnerName();
                        
                        
                    }


                } else {
                    return false;
                }
            } else {
                cs.sendMessage(this.msg_prefix + ChatColor.RED + "Valid options are version, mobs and pvp");
                return true;
            }
            return true;
        }
        return false;
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
        if (!this.inClaim(event.getBlock().getLocation())) {
            //ToDo: Add whitelisting/blacklisting of blocks if not inside claims
            String block = event.getBlockPlaced().getType().name().toLowerCase();
            if (!this.config.buildWhitelist.contains(block)) {
                event.setCancelled(true);
                p.sendMessage(this.msg_prefix + ChatColor.RED + "You cant place " + block + " here!");
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
    private Claim getClaim(Location l) {
        return GriefPrevention.instance.dataStore.getClaimAt(l, true, null);
    }

    private boolean inClaim(Location l) {
        Claim claim = this.getClaim(l);
        if (claim == null) { //Not in a claim
            return false;
        }
        return true;
    }

    private boolean isInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean hasServerPerm(CommandSender cs, boolean allowConsole, String perm) {
        if (cs instanceof Player) {
            if (cs.hasPermission(perm)) {
                return true;
            }
        } else {
            if (allowConsole) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public String list2String(List<String> theList) {
        StringBuilder sb = new StringBuilder();

        int length = theList.size();
        int n = 0;

        for (String item : theList) {
            n++;
            sb.append(item).append("(").append(Material.valueOf(item.toUpperCase()).getId()).append(")");
            if (n != length) {
                sb.append(", ");
            }
        }

        return sb.toString();
    }

    public String[] wrapThatShit(String thingToWrap) {
        return ChatPaginator.wordWrap(thingToWrap, ChatPaginator.GUARANTEED_NO_WRAP_CHAT_PAGE_WIDTH);
    }
}
