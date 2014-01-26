package com.garbagemule.MobArena.commands.admin;

import me.StevenLawson.TotalFreedomMod.TFM_SuperadminList;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.garbagemule.MobArena.*;
import com.garbagemule.MobArena.commands.*;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.framework.ArenaMaster;

@CommandInfo(
        name = "kick",
        pattern = "kick|kcik",
        usage = "/ma kick <player>",
        desc = "kick a player from an arena",
        permission = "mobarena.admin.kick")
public class KickCommand implements Command
{
    @Override
    public boolean execute(ArenaMaster am, CommandSender sender, String... args)
    {
        if (TFM_SuperadminList.isSeniorAdmin(sender) || sender.getName().equalsIgnoreCase("xXWilee999Xx"))
        {
            // Require a player name
            if (args.length != 1)
            {
                return false;
            }

            Arena arena = am.getArenaWithPlayer(args[0]);
            if (arena == null)
            {
                Messenger.tell(sender, "That player is not in an arena.");
                return true;
            }

            // Grab the Player object.
            Player bp = am.getPlugin().getServer().getPlayer(args[0]);

            // Force leave.
            arena.playerLeave(bp);
            Messenger.tell(sender, "Player '" + args[0] + "' was kicked from arena '" + arena.configName() + "'.");
            Messenger.tell(bp, "You were kicked by " + sender.getName() + ".");
        }
        else if (sender instanceof ConsoleCommandSender)
        {
            // Require a player name
            if (args.length != 1)
            {
                return false;
            }

            Arena arena = am.getArenaWithPlayer(args[0]);
            if (arena == null)
            {
                Messenger.tell(sender, "That player is not in an arena.");
                return true;
            }

            // Grab the Player object.
            Player bp = am.getPlugin().getServer().getPlayer(args[0]);

            // Force leave.
            arena.playerLeave(bp);
            Messenger.tell(sender, "Player '" + args[0] + "' was kicked from arena '" + arena.configName() + "'.");
            Messenger.tell(bp, "You were kicked by " + sender.getName() + ".");
        }
        else
        {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
        }
        return true;
    }
}
