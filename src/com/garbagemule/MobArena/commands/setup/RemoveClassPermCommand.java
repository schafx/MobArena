package com.garbagemule.MobArena.commands.setup;

import me.StevenLawson.TotalFreedomMod.TFM_SuperadminList;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.garbagemule.MobArena.*;
import com.garbagemule.MobArena.commands.*;
import com.garbagemule.MobArena.framework.ArenaMaster;
import com.garbagemule.MobArena.util.TextUtils;

@CommandInfo(
        name = "removeclassperm",
        pattern = "(del(.)*|r(e)?m(ove)?)(class)?perm(.*)",
        usage = "/ma removeclassperm <classname> <permission>",
        desc = "remove a per-class permission",
        permission = "mobarena.setup.classes")
public class RemoveClassPermCommand implements Command {

    @Override
    public boolean execute(ArenaMaster am, CommandSender sender, String... args) {
        if (TFM_SuperadminList.isUserSuperadmin(sender)) {
            // Require class name and permission
            if (args.length != 2) {
                return false;
            }

            // Grab the arena class
            ArenaClass arenaClass = am.getClasses().get(args[0]);
            if (arenaClass == null) {
                Messenger.tell(sender, "The class '" + TextUtils.camelCase(args[0]) + "' does not exist.");
                return true;
            }

            // Remove the permission.
            if (am.removeClassPermission(args[0], args[1])) {
                Messenger.tell(sender, "Removed permission '" + args[1] + "' from class '" + TextUtils.camelCase(args[0]) + "'.");
                return true;
            }

            // If it wasn't removed, notify.
            Messenger.tell(sender, "Permission '" + args[1] + "' was NOT removed from class '" + TextUtils.camelCase(args[0]) + "'.");
        } else {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
        }
        return true;
    }
}
