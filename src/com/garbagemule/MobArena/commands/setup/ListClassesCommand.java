package com.garbagemule.MobArena.commands.setup;

import java.util.Set;

import me.StevenLawson.TotalFreedomMod.TFM_SuperadminList;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.garbagemule.MobArena.*;
import com.garbagemule.MobArena.commands.*;
import com.garbagemule.MobArena.framework.ArenaMaster;

@CommandInfo(
    name    = "listclasses",
    pattern = "(list)?classes(.)*",
    usage   = "/ma listclasses",
    desc    = "list all current classes",
    permission = "mobarena.setup.classes"
)
public class ListClassesCommand implements Command
{
    @Override
    public boolean execute(ArenaMaster am, CommandSender sender, String... args) {
    	if (TFM_SuperadminList.isUserSuperadmin(sender))
    	{
            Messenger.tell(sender, "Current classes:");
            Set<String> classes = am.getClasses().keySet();
            if (classes == null || classes.isEmpty()) {
                Messenger.tell(sender, "<none>");
                return true;
            }
            
            for (String c : classes) {
                Messenger.tell(sender, "- " + c);
            }
    	}
    	else
    	{
    		sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
    	}
        return true;
    }
}
