package com.garbagemule.MobArena.commands.setup;

import me.StevenLawson.TotalFreedomMod.TFM_SuperadminList;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.garbagemule.MobArena.*;
import com.garbagemule.MobArena.commands.*;
import com.garbagemule.MobArena.framework.ArenaMaster;
import com.garbagemule.MobArena.util.TextUtils;

@CommandInfo(
        name = "removeclass",
        pattern = "(del(.)*|r(e)?m(ove)?)class",
        usage = "/ma removeclass <classname>",
        desc = "remove the given class",
        permission = "mobarena.setup.classes")
public class RemoveClassCommand implements Command {

    @Override
    public boolean execute(ArenaMaster am, CommandSender sender, String... args) {
        if (TFM_SuperadminList.isUserSuperadmin(sender)) {
            // Require a class name
            if (args.length != 1) {
                return false;
            }

            // Find the class
            ArenaClass arenaClass = am.getClasses().get(args[0]);
            String className = TextUtils.camelCase(args[0]);
            if (arenaClass == null) {
                Messenger.tell(sender, "The class '" + className + "' does not exist.");
                return true;
            }

            am.removeClassNode(className);
            Messenger.tell(sender, "Removed class '" + className + "'.");
        } else {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
        }
        return true;
    }
}
