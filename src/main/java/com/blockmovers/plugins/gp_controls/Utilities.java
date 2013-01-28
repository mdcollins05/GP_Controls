/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blockmovers.plugins.gp_controls;

import java.util.List;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.ChatPaginator;

/**
 *
 * @author MattC
 */
public class Utilities {
    
    GP_Controls plugin = null;

    public Utilities(GP_Controls plugin) {
        this.plugin = plugin;
    }
    
    public Claim getClaim(Location l) {
        return GriefPrevention.instance.dataStore.getClaimAt(l, true, null);
    }

    public boolean inClaim(Location l) {
        Claim claim = this.getClaim(l);
        if (claim == null) { //Not in a claim
            return false;
        }
        return true;
    }

    public boolean isInteger(String input) {
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
