package com.garbagemule.MobArena.WileeCommands;

import java.util.List;
import java.util.logging.Logger;

import me.StevenLawson.TotalFreedomMod.Commands.PlayerNotFoundException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.garbagemule.MobArena.MobArena;

public class MA_Command
{
    public MobArena plugin;
    public static final Logger log = Bukkit.getLogger();
    public static final Server server = Bukkit.getServer();

    public MA_Command()
    {
    }

    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        log.severe("Command Error: Command not implemented: " + cmd.getName());
        sender.sendMessage(ChatColor.RED + "Command Error: Command not implemented: " + cmd.getName());
        return false;
    }

    public void setPlugin(MobArena plugin)
    {
        this.plugin = plugin;
    }

    public Player getPlayer(String partialname) throws PlayerNotFoundException
    {
        List<Player> matches = Bukkit.matchPlayer(partialname);
        if (matches.isEmpty())
        {
            throw new PlayerNotFoundException(partialname);
        }
        else
        {
            return matches.get(0);
        }
    }
}
