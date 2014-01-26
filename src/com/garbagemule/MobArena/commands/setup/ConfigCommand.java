package com.garbagemule.MobArena.commands.setup;

import me.StevenLawson.TotalFreedomMod.TFM_SuperadminList;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import com.garbagemule.MobArena.*;
import com.garbagemule.MobArena.commands.*;
import com.garbagemule.MobArena.framework.ArenaMaster;

@CommandInfo(
        name = "config",
        pattern = "config|cfg",
        usage = "/ma config reload|save",
        desc = "reload or save the config-file",
        permission = "mobarena.setup.config")
public class ConfigCommand implements Command {

    @Override
    public boolean execute(ArenaMaster am, CommandSender sender, String... args) {
        if (TFM_SuperadminList.isUserSuperadmin(sender)) {
            // Require reload/save
            if (args.length != 1) {
                return false;
            }

            if (args[0].equals("reload")) {
                am.reloadConfig();
                Messenger.tell(sender, "Config reloaded.");
            } else if (args[0].equals("save")) {
                am.saveConfig();
                Messenger.tell(sender, "Config saved.");
            } else {
                return false;
            }
        } else if (sender instanceof ConsoleCommandSender) {
            // Require reload/save
            if (args.length != 1) {
                return false;
            }

            if (args[0].equals("reload")) {
                am.reloadConfig();
                Messenger.tell(sender, "Config reloaded.");
            } else if (args[0].equals("save")) {
                am.saveConfig();
                Messenger.tell(sender, "Config saved.");
            } else {
                return false;
            }
        } else {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
        }
        return true;
    }
}
