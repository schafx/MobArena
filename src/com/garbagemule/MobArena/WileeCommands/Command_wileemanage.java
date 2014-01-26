package com.garbagemule.MobArena.WileeCommands;

import java.util.Random;

import me.StevenLawson.TotalFreedomMod.TFM_ConfigEntry;
import me.StevenLawson.TotalFreedomMod.TFM_RollbackManager;
import me.StevenLawson.TotalFreedomMod.TFM_ServerInterface;
import me.StevenLawson.TotalFreedomMod.TFM_SuperadminList;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TFM_WorldEditBridge;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import me.StevenLawson.TotalFreedomMod.Commands.PlayerNotFoundException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;

import com.garbagemule.MobArena.MAUtils;

public class Command_wileemanage extends MA_Command
{
    @Override
    public boolean run(final CommandSender sender, final Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 0)
        {
            sender.sendMessage(ChatColor.RED + "Usage: /wileemanage <power> [arg]");
        }
        else if (args[0].equalsIgnoreCase("help"))
        {
            sender.sendMessage(ChatColor.GREEN + "=====WileeManage Help Page=====");
            sender.sendMessage(ChatColor.GREEN + "Please do not abuse any commands or over-use them. Thanks.");
            sender.sendMessage(ChatColor.RED + "/wileemanage obliviate <player> - Superadmin command - Obliviate a bad player. Just for the really bad ones.");
            sender.sendMessage(ChatColor.RED + "/wileemanage nope <player> - Superadmin command - Nope a bad player.");
            sender.sendMessage(ChatColor.RED + "/wileemanage desuperme - Dev/senior command - Desuper yourself.");
            sender.sendMessage(ChatColor.RED + "/wileemanage smite <player> <custommsg> - Superadmin command - Smite a player, with your own custom message.");
            sender.sendMessage(ChatColor.RED + "/wileemanage nope <player> - Superadmin command - Nope a bad player.");
            sender.sendMessage(ChatColor.RED + "/wileemanage bukkitban <player> - Superadmin command - Ban a bad player, default Bukkit style.");
            sender.sendMessage(ChatColor.RED + "/wileemanage bukkitsay <message> - Superadmin command - Broadcast a message, default Bukkit style.");
            sender.sendMessage(ChatColor.RED + "/wileemanage bukkitop <player> - OP Command - OP a player.");
            sender.sendMessage(ChatColor.RED + "/wileemanage bukkitdeop <player> - Superadmin command - Deop a bad player.");
            sender.sendMessage(ChatColor.RED + "/wileemanage bukkitpardon <player> - Superadmin command - Unban a player.");
            sender.sendMessage(ChatColor.RED + "/wileemanage rawsay <message> - Superadmin command - Send a message, with no formatting.");
            sender.sendMessage(ChatColor.RED + "/wileemanage autoexpel <on> - Superadmin command - Automatically use expel when a player comes near you.");
            sender.sendMessage(ChatColor.RED + "/wileemanage emg <1|2> - Senior/Wilee command - Manage the server's lockdown modes. Credit to Wild1145.");
            sender.sendMessage(ChatColor.RED + "/wileemanage plugintoggle <plugin> - Wilee command - Toggle a Wilee plugin.");
            sender.sendMessage(ChatColor.RED + "/wileemanage log <message> - Superadmin command - Log anything to the log file as [INFO]. No other formatting.");
            sender.sendMessage(ChatColor.RED + "/wileemanage ebroadcast <message> - Superadmin command - Broadcast to the server Essentials style.");
            sender.sendMessage(ChatColor.RED + "/wileemanage king - Superadmin command - KingDragonRider's personal command that he earned.");
            sender.sendMessage(ChatColor.RED + "/wileemanage ride <player|off> - Superadmin command - Ride any player.");
            sender.sendMessage(ChatColor.RED + "/wileemanage machat <player> <message> - Superadmin command - Take someones chat and embarrass them.");
            sender.sendMessage(ChatColor.RED + "/wileemanage strength <on|off> - Superadmin command - Toggle strength epic powaaazzz.");
            sender.sendMessage(ChatColor.GREEN + "Please do not abuse any commands or over-use them. Thanks.");
            sender.sendMessage(ChatColor.GREEN + "=====WileeManage Help Page=====");
        }
        else if (args[0].equalsIgnoreCase("obliviate"))
        {
            if (TFM_SuperadminList.isUserSuperadmin(sender))
            {
                if (args.length == 1)
                {
                    sender.sendMessage(ChatColor.RED + "Usage: /wileemanage obliviate <player>");
                    return true;
                }

                final Player player;
                try
                {
                    player = getPlayer(args[1]);
                }
                catch (PlayerNotFoundException ex)
                {
                    sender.sendMessage(ex.getMessage());
                    return true;
                }

                MAUtils.adminAction(sender.getName(), "Casting complete holy obliviation over " + player.getName(), ChatColor.RED);
                MAUtils.bcastMsg(player.getName() + " will be completely obliviated using thy satanic holy powers!", ChatColor.RED);

                final String IP = player.getAddress().getAddress().getHostAddress().trim();

                // remove from whitelist
                player.setWhitelisted(false);

                // deop
                player.setOp(false);

                // ban IP
                TFM_ServerInterface.banIP(IP, null, null, null);

                // ban name
                TFM_ServerInterface.banUsername(player.getName(), null, null, null);

                // set gamemode to survival
                player.setGameMode(GameMode.SURVIVAL);

                // clear inventory
                player.closeInventory();
                player.getInventory().clear();

                // ignite player
                player.setFireTicks(10000);

                // rollback + undo
                TFM_WorldEditBridge.getInstance().undo(player, 15);

                TFM_RollbackManager.rollback(player.getName());

                // generate explosion
                player.getWorld().createExplosion(player.getLocation(), 7F);

                // go up into the sky
                player.setVelocity(new org.bukkit.util.Vector(0, 20, 0));

                // runnables

                new BukkitRunnable()
                {
                    @Override
                    public void run()
                    {
                        // ask the player a question
                        MAUtils.bcastMsg("Hey, " + player.getName() + ", what's the difference between jelly and jam?", ChatColor.LIGHT_PURPLE);
                    }
                }.runTaskLater(plugin, 40L);

                new BukkitRunnable()
                {
                    @Override
                    public void run()
                    {
                        // answer it
                        MAUtils.bcastMsg("I can't jelly my banhammer down your throat.", ChatColor.LIGHT_PURPLE);

                        // generate explosion
                        player.getWorld().createExplosion(player.getLocation(), 7F);

                        // strike lightning
                        player.getWorld().strikeLightning(player.getLocation());
                    }
                }.runTaskLater(plugin, 100L);

                new BukkitRunnable()
                {
                    @Override
                    public void run()
                    {
                        // strike lightning
                        player.getWorld().strikeLightning(player.getLocation());

                        // kill (if not done already)
                        player.setHealth(0.0);

                        // generate explosion
                        player.getWorld().createExplosion(player.getLocation(), 7F);

                        // go up into the sky
                        player.setVelocity(new org.bukkit.util.Vector(0, 20, 0));
                    }
                }.runTaskLater(plugin, 140L);

                new BukkitRunnable()
                {
                    @Override
                    public void run()
                    {
                        // strike lightning
                        player.getWorld().strikeLightning(player.getLocation());

                        // kill (if not done already)
                        player.setHealth(0.0);

                        // generate explosion
                        player.getWorld().createExplosion(player.getLocation(), 7F);

                        // go up into the sky
                        player.setVelocity(new org.bukkit.util.Vector(0, 20, 0));
                    }
                }.runTaskLater(plugin, 160L);

                new BukkitRunnable()
                {
                    @Override
                    public void run()
                    {
                        // message
                        MAUtils.adminAction(sender.getName(), "Banning: " + player.getName() + ", IP: " + IP, ChatColor.RED);

                        // generate explosion
                        player.getWorld().createExplosion(player.getLocation(), 7F);

                        // kick player
                        player.kickPlayer(ChatColor.RED + "FUCKOFF, and get your MOTHER FUCKING shit together!");
                    }
                }.runTaskLater(plugin, 190L);

                return true;
            }
            else
            {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            }
        }
        else if (args[0].equalsIgnoreCase("nope"))
        {
            if (TFM_SuperadminList.isUserSuperadmin(sender))
            {
                if (args.length == 1)
                {
                    sender.sendMessage(ChatColor.RED + "Usage: /wileemanage nope <player>");
                    return true;
                }

                final Player player;
                try
                {
                    player = getPlayer(args[1]);
                }
                catch (PlayerNotFoundException ex)
                {
                    sender.sendMessage(ex.getMessage());
                    return true;
                }

                final String IP = player.getAddress().getAddress().getHostAddress().trim();
                MAUtils.adminAction(sender.getName(), "Starting a huge nope fest over " + player.getName(), ChatColor.RED);

                // go up into the sky
                player.setVelocity(new org.bukkit.util.Vector(0, 4, 0));

                // blow up
                player.getWorld().createExplosion(player.getLocation(), 4F);

                // strike lightning
                player.getWorld().strikeLightning(player.getLocation());

                // ban IP
                TFM_ServerInterface.banIP(IP, null, null, null);

                // ban name
                TFM_ServerInterface.banUsername(player.getName(), null, null, null);

                // rollback + undo
                TFM_WorldEditBridge.getInstance().undo(player, 15);

                TFM_RollbackManager.rollback(player.getName());

                // runnables

                new BukkitRunnable()
                {
                    @Override
                    public void run()
                    {
                        // go up into the sky
                        player.setVelocity(new org.bukkit.util.Vector(0, 30, 0));

                        // blow up
                        player.getWorld().createExplosion(player.getLocation(), 4F);

                        // strike lightning
                        player.getWorld().strikeLightning(player.getLocation());
                    }
                }.runTaskLater(plugin, 50L);

                new BukkitRunnable()
                {
                    @Override
                    public void run()
                    {
                        // go up into the sky
                        player.setVelocity(new org.bukkit.util.Vector(0, 30, 0));

                        // blow up
                        player.getWorld().createExplosion(player.getLocation(), 4F);

                        // strike lightning
                        player.getWorld().strikeLightning(player.getLocation());
                    }
                }.runTaskLater(plugin, 90L);

                new BukkitRunnable()
                {
                    @Override
                    public void run()
                    {
                        // message
                        MAUtils.adminAction(sender.getName(), "Banning: " + player.getName() + ", IP: " + IP, ChatColor.RED);

                        // generate explosion
                        player.getWorld().createExplosion(player.getLocation(), 4F);

                        // kick player
                        player.kickPlayer(ChatColor.RED + "NOPE!\nAppeal at totalfreedom.boards.net\nAnd make sure you follow the rules at totalfreedom.me");
                    }
                }.runTaskLater(plugin, 120L);

                return true;
            }
            else
            {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            }
        }
        else if (args[0].equalsIgnoreCase("desuperme"))
        {
            if (!TFM_SuperadminList.isSeniorAdmin(sender) || TFM_Util.DEVELOPERS.contains(sender))
            {
                Player p = Bukkit.getPlayer(sender.getName());
                MAUtils.adminAction(sender.getName(), "Removing " + sender.getName() + " from the superadmin list.", ChatColor.RED);
                TFM_SuperadminList.removeSuperadmin(p);
            }
            else
            {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            }
        }
        else if (args[0].equalsIgnoreCase("smite"))
        {
            if (TFM_SuperadminList.isUserSuperadmin(sender))
            {
                if (args.length < 3)
                {
                    sender.sendMessage(ChatColor.RED + "Usage: /wileemanage smite <player> <message>");
                    return true;
                }

                final Player player;
                String message = " ";
                for (int i = 2; i < args.length; i++)
                {
                    if (i > 2)
                    {
                        message += " ";
                    }
                    message += args[i];
                }
                try
                {
                    player = getPlayer(args[1]);
                }
                catch (PlayerNotFoundException ex)
                {
                    sender.sendMessage(ex.getMessage());
                    return true;
                }

                MAUtils.bcastMsg(ChatColor.RED + player.getName() + message);

                //Deop
                player.setOp(false);

                //Set gamemode to survival:
                player.setGameMode(GameMode.SURVIVAL);

                //Clear inventory:
                player.getInventory().clear();

                //Strike with lightning effect:
                final Location targetPos = player.getLocation();
                final World world = player.getWorld();
                for (int x = -1; x <= 1; x++)
                {
                    for (int z = -1; z <= 1; z++)
                    {
                        final Location strike_pos = new Location(world, targetPos.getBlockX() + x, targetPos.getBlockY(), targetPos.getBlockZ() + z);
                        world.strikeLightning(strike_pos);
                    }
                }

                //Kill:
                player.setHealth(0.0);

                return true;
            }
            else
            {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            }
        }
        else if (args[0].equalsIgnoreCase("bukkitban"))
        {
            if (TFM_SuperadminList.isUserSuperadmin(sender))
            {
                if (args.length == 1)
                {
                    sender.sendMessage(ChatColor.RED + "Usage: /wileemanage bukkitban <player>");
                    return true;
                }

                Bukkit.getOfflinePlayer(args[1]).setBanned(true);

                Player player = Bukkit.getPlayer(args[1]);
                if (player != null)
                {
                    player.kickPlayer("Banned by admin.");
                }

                Command.broadcastCommandMessage(sender, "Banned player " + args[1]);

                return true;
            }
            else
            {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            }
        }
        else if (args[0].equalsIgnoreCase("bukkitsay"))
        {
            if (TFM_SuperadminList.isUserSuperadmin(sender))
            {
                if (args.length == 1)
                {
                    sender.sendMessage(ChatColor.RED + "Usage: /wileemanage bukkitsay <message>");
                    return true;
                }

                String message = "";
                for (int i = 1; i < args.length; i++)
                {
                    if (i > 1)
                    {
                        message += " ";
                    }
                    message += args[i];
                }

                MAUtils.bcastMsg("[Server] " + message, ChatColor.LIGHT_PURPLE);
                return true;
            }
            else
            {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            }
        }
        else if (args[0].equalsIgnoreCase("bukkitop"))
        {
            if (args.length == 1)
            {
                sender.sendMessage(ChatColor.RED + "Usage: /wileemanage bukkitop <player>");
                return true;
            }

            OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
            player.setOp(true);

            if (player instanceof Player)
            {
                ((Player) player).sendMessage(ChatColor.YELLOW + "You are now OP!");
            }

            Command.broadcastCommandMessage(sender, "Opped " + args[1]);
            return true;
        }
        else if (args[0].equalsIgnoreCase("bukkitdeop"))
        {
            if (TFM_SuperadminList.isUserSuperadmin(sender))
            {
                if (args.length == 1)
                {
                    sender.sendMessage(ChatColor.RED + "Usage: /wileemanage bukkitdeop <player>");
                    return true;
                }

                OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
                player.setOp(false);

                if (player instanceof Player)
                {
                    ((Player) player).sendMessage(ChatColor.YELLOW + "You are no longer OP!");
                }

                Command.broadcastCommandMessage(sender, "De-opped " + args[1]);
                return true;
            }
            else
            {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            }
        }
        else if (args[0].equalsIgnoreCase("bukkitpardon"))
        {
            if (TFM_SuperadminList.isUserSuperadmin(sender))
            {
                if (args.length == 1)
                {
                    sender.sendMessage(ChatColor.RED + "Usage: /wileemanage bukkitpardon <player>");
                    return true;
                }

                Bukkit.getOfflinePlayer(args[1]).setBanned(false);
                Command.broadcastCommandMessage(sender, "Pardoned " + args[1]);
                return true;
            }
            else
            {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            }
        }
        else if (args[0].equalsIgnoreCase("rawsay"))
        {
            if (TFM_SuperadminList.isUserSuperadmin(sender))
            {
                String message = "";
                for (int i = 1; i < args.length; i++)
                {
                    if (i > 1)
                    {
                        message += " ";
                    }
                    message += args[i];
                }

                MAUtils.bcastMsg(TFM_Util.colorize(message));
                return true;
            }
            else
            {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            }
        }
        else if (args[0].equalsIgnoreCase("autoexpel"))
        {
            if (TFM_SuperadminList.isUserSuperadmin(sender))
            {
                if (args.length < 1)
                {
                    sender.sendMessage(ChatColor.RED + "Usage: /wileemanage autoexpel <on>");
                    return true;
                }

                boolean fuckoff_enabled = false;
                double fuckoff_range = 25.0;

                if (args[1].equalsIgnoreCase("on"))
                {
                    fuckoff_enabled = true;

                    if (args.length >= 2)
                    {
                        try
                        {
                            fuckoff_range = Math.max(5.0, Math.min(100.0, Double.parseDouble(args[1])));
                        }
                        catch (NumberFormatException ex)
                        {
                        }
                    }
                }

                if (TotalFreedomMod.fuckoffEnabledFor.containsKey(sender_p))
                {
                    TotalFreedomMod.fuckoffEnabledFor.remove(sender_p);
                }

                if (fuckoff_enabled)
                {
                    TotalFreedomMod.fuckoffEnabledFor.put(sender_p, new Double(fuckoff_range));
                }

                sender.sendMessage(ChatColor.GRAY + "Auto-expel " + (fuckoff_enabled ? ("enabled. Range: " + fuckoff_range + ".") : "disabled."));
                return true;
            }
            else
            {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            }
        }
        else if (args[0].equalsIgnoreCase("emg"))
        {
            if (TFM_SuperadminList.isSeniorAdmin(sender) || sender.getName().equalsIgnoreCase("xXWilee999Xx"))
            {
                if (args.length == 1)
                {
                    sender.sendMessage(ChatColor.RED + "Usage: /wileemanage emg <mode>");
                    return true;
                }
                else if (args[1].equalsIgnoreCase("1"))
                {
                    MAUtils.adminAction(sender.getName(), "Activating emergency sequence 1", ChatColor.RED);
                    TotalFreedomMod.lockdownEnabled = true;
                    TFM_ServerInterface.setOnlineMode(true);
                    for (Player p : Bukkit.getOnlinePlayers())
                    {
                        if (TFM_SuperadminList.isUserSuperadmin(p))
                        {
                        }
                        else
                        {
                            p.kickPlayer(ChatColor.RED + "This server is activating emergency sequence 1.\nDuring this sequence, the server will be locked down to new players and in online mode.");
                        }
                    }
                }
                else if (args[1].equalsIgnoreCase("2"))
                {
                    MAUtils.adminAction(sender.getName(), "Activating emergency sequence 2", ChatColor.RED);
                    TotalFreedomMod.lockdownEnabled = true;
                    TFM_ServerInterface.setOnlineMode(true);
                    for (Player p : Bukkit.getOnlinePlayers())
                    {
                        if (TFM_SuperadminList.isUserSuperadmin(p))
                        {
                        }
                        else
                        {
                            p.kickPlayer(ChatColor.RED + "This server is activating emergency sequence 2.\nDuring this sequence, the server will be locked down to new players (also adminmode) and in online mode.");
                        }
                    }
                    TFM_ConfigEntry.ADMIN_ONLY_MODE.setBoolean(true);
                }

                sender.sendMessage(ChatColor.GREEN + "Credit to Wild1145 for the command!");
                return true;
            }
            else
            {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            }
        }
        else if (args[0].equalsIgnoreCase("plugintoggle"))
        {
            if (TFM_SuperadminList.isSeniorAdmin(sender) || TFM_Util.DEVELOPERS.contains(sender.getName()))
            {
                if (args.length == 1)
                {
                    sender.sendMessage(ChatColor.RED + "Usage: /wileemanage plugintoggle <plugin>");
                    return true;
                }

                else if (args[1].equalsIgnoreCase("DisguiseCraft"))
                {
                    Plugin disguiseCraft = Bukkit.getServer().getPluginManager().getPlugin("DisguiseCraft");
                    if (disguiseCraft != null)
                    {
                        // :'(
                        PluginManager pm = Bukkit.getServer().getPluginManager();
                        boolean enabled = disguiseCraft.isEnabled();
                        if (enabled)
                        {
                            MAUtils.adminAction(sender.getName(), "Disabling plugin: DisguiseCraft", ChatColor.RED);
                            pm.disablePlugin(disguiseCraft);
                        }
                        else
                        {
                            MAUtils.adminAction(sender.getName(), "Enabling plugin: DisguiseCraft", ChatColor.RED);
                            pm.enablePlugin(disguiseCraft);
                        }
                    }
                    else // impossibilities
                    {
                        sender.sendMessage(ChatColor.RED + "MobArena is not installed on this server.");
                    }
                }
                else if (args[1].equalsIgnoreCase("MobArena"))
                {
                    Plugin mobArena = Bukkit.getServer().getPluginManager().getPlugin("MobArena");
                    if (mobArena != null)
                    {
                        // :'(
                        PluginManager pm = Bukkit.getServer().getPluginManager();
                        MAUtils.adminAction(sender.getName(), "Disabling plugin: MobArena", ChatColor.RED);
                        pm.disablePlugin(mobArena);
                    }
                    else // impossibilities
                    {
                        sender.sendMessage(ChatColor.RED + "MobArena is not installed on this server.");
                    }
                }

                return true;
            }
            else
            {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            }
        }
        else if (args[0].equalsIgnoreCase("log"))
        {
            if (TFM_SuperadminList.isUserSuperadmin(sender))
            {
                if (args.length < 3)
                {
                    sender.sendMessage(ChatColor.RED + "Usage: /wileemanage log <loglevel> <message>");
                    return true;
                }

                String message = "";
                for (int i = 2; i < args.length; i++)
                {
                    if (i > 2)
                    {
                        message += " ";
                    }
                    message += args[i];
                }
                
                if (args[1].eqaulsIgnoreCase("info"))
                {
                    log.info(message);
                }
                
                else if (args[1].eqaulsIgnoreCase("severe"))
                {
                    log.severe(message);
                }
                
                else if (args[1].eqaulsIgnoreCase("warning"))
                {
                    log.warning(message);
                }
                
                else
                {
                    sender.sendMessage(ChatColor.RED + "Invalid logging level.");
                    sender.sendMessage(ChatColor.RED + "Please type in info, severe, or warning.");
                }
                return true;
            }
            else
            {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            }
        }
        else if (args[0].equalsIgnoreCase("ebroadcast"))
        {
            if (TFM_SuperadminList.isUserSuperadmin(sender))
            {
                if (args.length == 1)
                {
                    sender.sendMessage(ChatColor.RED + "Usage: /wileemanage ebroadcast <message>");
                    return true;
                }

                String message = "";
                for (int i = 1; i < args.length; i++)
                {
                    if (i > 1)
                    {
                        message += " ";
                    }
                    message += args[i];
                }

                MAUtils.bcastMsg(ChatColor.RED + "[" + ChatColor.GREEN + "Broadcast" + ChatColor.RED + "] " + ChatColor.AQUA + message);
            }
            else
            {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            }
        }
        // King's command that he earned
        else if (args[0].equalsIgnoreCase("king"))
        {
            if (TFM_SuperadminList.isUserSuperadmin(sender))
            {
                final String KING_LYRICS = "KingDragonRider has given you a personalised cookie for free!";
                StringBuilder output = new StringBuilder();
                Random randomGenerator = new Random();

                String[] words = KING_LYRICS.split(" ");
                for (String word : words)
                {
                    String color_code = Integer.toHexString(1 + randomGenerator.nextInt(14));
                    output.append(ChatColor.COLOR_CHAR).append(color_code).append(word).append(" ");
                }

                for (Player player : server.getOnlinePlayers())
                {
                    server.dispatchCommand(player, "i cookie 1 name:&8&lK&7&li&f&ln&e&lg&6&lD&c&l'&4&ls&5&l_&d&lC&b&lo&9&lo&1&lk&3&li&2&le");
                }

                MAUtils.bcastMsg(output.toString());
                return true;
            }
            else
            {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            }
        }
        else if (args[0].equalsIgnoreCase("ride"))
        {
            // broken command, working on a fix
            if (!TFM_SuperadminList.isUserSuperadmin(sender) || sender instanceof ConsoleCommandSender)
            {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command, or you are runnning this command from the console.");
            }
            else
            {
                if (args.length == 1)
                {
                    sender.sendMessage(ChatColor.RED + "Usage: /wileemanage ride <player>");
                    return true;
                }
                else if (args[1].equalsIgnoreCase("off"))
                {
                    if (sender_p.getVehicle() != null && sender_p.getVehicle() instanceof Player)
                    {
                        Player otherp = (Player) sender_p.getVehicle();
                        otherp.setPassenger(sender_p);
                        sender.sendMessage(ChatColor.RED + "You have stopped riding:o " + otherp.getName());
                    }
                    else
                    {
                        sender.sendMessage(ChatColor.RED + "You must be riding someone.");
                    }
                }

                for (Player player : Bukkit.getOnlinePlayers())
                {
                    if (player.getName().contains(args[1]))
                    {
                        if (!player.getName().equalsIgnoreCase(sender.getName()))
                        {
                            player.setPassenger(sender_p);
                            sender.sendMessage(ChatColor.GREEN + "You are now riding: " + player.getName());
                        }
                        else
                        {
                            sender.sendMessage(ChatColor.RED + "You cannot ride yourself.");
                        }
                    }
                }
            }
        }
        else if (args[0].equalsIgnoreCase("machat"))
        {
            if (TFM_SuperadminList.isUserSuperadmin(sender))
            {
                if (args.length == 1)
                {
                    sender.sendMessage(ChatColor.RED + "Usage: /wileemanage machat <player> <message>");
                    return true;
                }

                final Player player;
                try
                {
                    player = getPlayer(args[1]);
                }
                catch (PlayerNotFoundException ex)
                {
                    sender.sendMessage(ex.getMessage());
                    return true;
                }

                String message = "";
                for (int i = 2; i < args.length; i++)
                {
                    if (i > 2)
                    {
                        message += " ";
                    }
                    message += args[i];
                }

                if (message.startsWith("/"))
                {
                    sender.sendMessage(ChatColor.RED + "You cannot start with a command, please use /gcmd for commands.");
                }
                else
                {
                    player.chat(message);
                }

                return true;
            }
            else
            {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            }
        }
        else if (args[0].equalsIgnoreCase("strength"))
        {
            if (TFM_SuperadminList.isUserSuperadmin(sender))
            {
                if (args.length == 1)
                {
                    sender.sendMessage(ChatColor.RED + "Usage: /wileemanage strength <on|off>");
                    return true;
                }
                else if (args[1].equalsIgnoreCase("on"))
                {
                    if (!MAUtils.strengthEnabled.contains(sender.getName()))
                    {
                        MAUtils.strengthEnabled.add(sender.getName());
                    }
                    sender.sendMessage(ChatColor.GREEN + "Strength has been enabled.");
                    return true;
                }
                else if (args[1].equalsIgnoreCase("off"))
                {
                    if (MAUtils.strengthEnabled.contains(sender.getName()))
                    {
                        MAUtils.strengthEnabled.remove(sender.getName());
                    }
                    sender.sendMessage(ChatColor.RED + "Strength has been disabled.");
                    return true;
                }

                return true;
            }
            else
            {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            }
        }
        else
        {
            sender.sendMessage(ChatColor.RED + "Usage: /wileemanage <power> [arg]");
        }

        return true;
    }
}
