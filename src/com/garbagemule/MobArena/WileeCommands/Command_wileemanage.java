package com.garbagemule.MobArena.WileeCommands;

import com.garbagemule.MobArena.MAUtils;

import me.StevenLawson.TotalFreedomMod.Commands.PlayerNotFoundException;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TFM_RollbackManager;
import me.StevenLawson.TotalFreedomMod.TFM_ServerInterface;
import me.StevenLawson.TotalFreedomMod.TFM_SuperadminList;
import me.StevenLawson.TotalFreedomMod.TFM_WorldEditBridge;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.bukkit.World;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.WitherSkull;

public class Command_wileemanage extends MA_Command
{
    // for /wm spanish
    public static boolean isSpanish = false;

    @Override
    public boolean run(final CommandSender sender, final Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 0)
        {
            sender.sendMessage(ChatColor.RED + "Usage: /wileemanage <power> [arg]");
        }

        else if (args[0].equalsIgnoreCase("help"))
        {
            sender.sendMessage(ChatColor.GREEN + "=====Wileemanage Help Page=====");
            sender.sendMessage(ChatColor.GREEN + "Please do not abuse any commands or over-use them. Thanks.");
            sender.sendMessage(ChatColor.RED + "/wileemanage obliviate|obv <player> - Superadmin command - Obliviate a bad player. Just for the really bad ones.");
            sender.sendMessage(ChatColor.RED + "/wileemanage nope <player> - Superadmin command - Nope a bad player.");
            sender.sendMessage(ChatColor.RED + "/wileemanage smite <player> <message> - Superadmin command - Smite a bad player WITH your own smite message!");
            sender.sendMessage(ChatColor.RED + "/wileemanage bc <message...> - Superadmin command - Broadcast to the server Essentials style.");
            sender.sendMessage(ChatColor.RED + "/wileemanage ride <player> - Superadmin command - Ride a suspicous player - may want to be invis.");
            sender.sendMessage(ChatColor.RED + "/wileemanage machat <player <message...> - Superadmin command - Take someones chat and embarrass them.");
            sender.sendMessage(ChatColor.RED + "/wileemanage strength <on|off> - Superadmin command - Toggle strength epic powaaazzz.");
            sender.sendMessage(ChatColor.RED + "/wileemanage thinice <player> - Superadmin command - For the people on thin ice.");
            sender.sendMessage(ChatColor.RED + "/wileemanage warn <player> - Superadmin command - Warn a player for permban.");
            sender.sendMessage(ChatColor.RED + "/wileemanage facepalm - Superadmin command - Facepalm. All I have to say.");
            sender.sendMessage(ChatColor.RED + "/wileemanage report [custommsg...] - Report a player for breaking a rule.");
            sender.sendMessage(ChatColor.RED + "/wileemanage savinghelp [-a] - Learn how to save structures with WorldEdit - only admins can use the -a switch.");
            sender.sendMessage(ChatColor.RED + "/wileemanage explode - Superadmin command - Create an explosion at your area.");
            sender.sendMessage(ChatColor.RED + "/wileemanage fireball [type] - Superadmin command - Create. A fucking. Fireball.");
            sender.sendMessage(ChatColor.GREEN + "Please do not abuse any commands or over-use them. Thanks.");
            sender.sendMessage(ChatColor.GREEN + "=====Wileemanage Help Page=====");
        }

        else if (args[0].equalsIgnoreCase("obliviate") || args[0].equalsIgnoreCase("obv"))
        {
            if (TFM_SuperadminList.isUserSuperadmin(sender))
            {
                if (args.length == 1)
                {
                    sender.sendMessage(ChatColor.RED + "Usage: /wileemanage obliviate|obv <player>");
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
                MAUtils.bcastMsg(player.getName() + " has been a VERY naughty, naughty boy.", ChatColor.RED);

                final String IP = player.getAddress().getAddress().getHostAddress().trim();

                // remove from whitelist
                player.setWhitelisted(false);

                // deop
                if (player.isOp())
                {
                    player.setOp(false);
                }

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
                player.setVelocity(new Vector(player.getLocation().getX(), 70, player.getLocation().getZ()));

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
                        player.setVelocity(new Vector(player.getLocation().getX(), 1000, player.getLocation().getZ()));
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
                        player.setVelocity(new Vector(player.getLocation().getX(), 1000, player.getLocation().getZ()));
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
                player.setVelocity(new Vector(player.getLocation().getX(), 8000, player.getLocation().getZ()));

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
                        player.setVelocity(new Vector(player.getLocation().getX(), 8000, player.getLocation().getZ()));

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
                        player.setVelocity(new Vector(player.getLocation().getX(), 8000, player.getLocation().getZ()));

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
                        player.getWorld().createExplosion(player.getLocation(), 7F);

                        // kick player
                        player.kickPlayer(ChatColor.RED + "NOPE!\nAppeal at totalfreedom.boards.net\nAnd make sure you follow the rules at totalfreedom.me!");
                    }
                }.runTaskLater(plugin, 120L);

                return true;
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

        else if (args[0].equalsIgnoreCase("bc"))
        {
            if (TFM_SuperadminList.isUserSuperadmin(sender))
            {
                if (args.length == 1)
                {
                    sender.sendMessage(ChatColor.RED + "Usage: /wileemanage bc <message...>");
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
        else if (args[0].equalsIgnoreCase("ride"))
        {
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

                Player player = Bukkit.getPlayer(args[1]);
                if (player.isOnline())
                {
                    if (!player.getName().equalsIgnoreCase(sender.getName()))
                    {
                        if (!TFM_SuperadminList.isUserSuperadmin(player))
                        {
                            if (player.isEmpty())
                            {
                                player.setPassenger(sender_p);
                                sender.sendMessage(ChatColor.GREEN + "You are now riding: " + player.getName());
                            }
                            else
                            {
                                sender.sendMessage(ChatColor.RED + "That player is riding someone - you cannot ride him.");
                            }
                        }
                        else
                        {
                            sender.sendMessage(ChatColor.RED + "Unfortunetely, you cannot ride admins.");
                        }
                    }
                    else
                    {
                        sender.sendMessage(ChatColor.RED + "You cannot ride yourself.");
                    }
                }
                else
                {
                    sender.sendMessage(ChatColor.RED + "That player is not online.");
                }
            }
        }

        else if (args[0].equalsIgnoreCase("machat"))
        {
            if (TFM_SuperadminList.isUserSuperadmin(sender))
            {
                if (args.length == 1)
                {
                    sender.sendMessage(ChatColor.RED + "Usage: /wileemanage machat <player> <message...>");
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

        else if (args[0].equalsIgnoreCase("thinice"))
        {
            if (TFM_SuperadminList.isUserSuperadmin(sender))
            {
                if (args.length == 1)
                {
                    sender.sendMessage(ChatColor.RED + "Usage: /wileemanage thinice <player>");
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

                MAUtils.adminAction(sender.getName(), player.getName() + " is on THIN ICE!!!", ChatColor.RED);

                player.setOp(false);
                player.setGameMode(GameMode.SURVIVAL);
                player.getInventory().clear();

                sender_p.getWorld().strikeLightning(sender_p.getLocation());
                sender_p.getWorld().strikeLightning(sender_p.getLocation());

                Location loc = player.getLocation();
                loc.setY(loc.getY() - 10);
                player.teleport(loc);
                player.setHealth(0.0);
                player.setVelocity(new Vector(player.getLocation().getX(), -3000, player.getLocation().getZ()));
                player.setVelocity(new Vector(player.getLocation().getX(), 7000, player.getLocation().getZ()));

                // runnables
                new BukkitRunnable()
                {
                    @Override
                    public void run()
                    {
                        // show the player the moon
                        player.setVelocity(new Vector(player.getLocation().getX(), 7000, player.getLocation().getZ()));
                    }
                }.runTaskLater(plugin, 40L);

                new BukkitRunnable()
                {
                    @Override
                    public void run()
                    {
                        // kill
                        player.setHealth(0.0);

                        // lightning
                        sender_p.getWorld().strikeLightning(sender_p.getLocation());
                    }
                }.runTaskLater(plugin, 60L);

                new BukkitRunnable()
                {
                    @Override
                    public void run()
                    {
                        // tempban
                        MAUtils.adminAction(sender.getName(), player.getName() + " fell from the thin ice, tempbanning for 5 minutes...", ChatColor.RED);
                        TFM_ServerInterface.banUsername(player.getName(), ChatColor.RED + "You have been temporarily banned for 5 minutes.\nPlease read the rules at totalfreedom.me.", sender.getName(), TFM_Util.parseDateOffset("5m"));
                        player.kickPlayer(ChatColor.RED + "You fell from the thin ice, and got tempbanned for 5 minutes.\nMaybe you should read the rules at totalfreedom.me.");
                    }
                }.runTaskLater(plugin, 80L);

                return true;
            }
            else
            {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            }
        }

        else if (args[0].equalsIgnoreCase("warn"))
        {
            if (TFM_SuperadminList.isUserSuperadmin(sender))
            {
                if (args.length == 1)
                {
                    sender.sendMessage(ChatColor.RED + "Usage: /wileemanage warn <player>");
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

                MAUtils.adminAction(sender.getName(), "Warning " + player.getName() + " of permban", ChatColor.DARK_RED);

                player.setOp(false);
                player.getInventory().clear();
                player.setGameMode(GameMode.SURVIVAL);

                sender_p.getWorld().strikeLightning(sender_p.getLocation());
                sender_p.getWorld().strikeLightning(sender_p.getLocation());

                player.sendMessage(ChatColor.DARK_RED + player.getName() + ", you are at high risk of being permanently banned (name and IP) from the Total Freedom server. Please immediately review all rules listed at www.totalfreedom.me and comply with them.");
                return true;
            }
            else
            {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            }
        }

        else if (args[0].equalsIgnoreCase("facepalm"))
        {
            if (TFM_SuperadminList.isUserSuperadmin(sender))
            {
                MAUtils.bcastMsg(sender.getName() + " really needs to have a facepalm ragequit moment here...", ChatColor.RED);

                sender_p.chat("I AM SO MAD.");
                sender_p.chat("WHY. did you say that? Now my face hurts from facepalming... I'm just gonna get off my computer.");

                sender_p.setGameMode(GameMode.SURVIVAL);
                sender_p.getInventory().clear();
                sender_p.setHealth(0.0);

                sender_p.kickPlayer(ChatColor.RED + "FACEPALM RAGGGEQUIIIUTTTTTT!!!!!");
                return true;
            }
            else
            {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            }
        }

        else if (args[0].equalsIgnoreCase("report"))
        {
            if (args.length == 1)
            {
                MAUtils.adminbcastMsg(sender.getName() + " is getting griefed or has a problem!", ChatColor.RED);
                sender.sendMessage(ChatColor.GREEN + "The administration team has been notified that you are getting griefed. :)");
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

            MAUtils.adminbcastMsg("[" + ChatColor.RED + "Report:" + ChatColor.YELLOW + sender.getName() + ChatColor.WHITE + "] " + ChatColor.GREEN + message);
            sender.sendMessage(ChatColor.GREEN + "Your message has been sent to the administration team. :)");
        }

        else if (args[0].equalsIgnoreCase("savinghelp"))
        {
            if (args.length == 1)
            {
                sender.sendMessage(ChatColor.RED + "1.) Do //wand (or use the //pos commands).");
                sender.sendMessage(ChatColor.RED + "2.) Select the two outermost angles of your build.");
                sender.sendMessage(ChatColor.RED + "3.) Do //copy in order to copy your build.");
                sender.sendMessage(ChatColor.RED + "4.) Use: //schematic save yourschematicname in order to save your build.");
                sender.sendMessage(ChatColor.RED + "5.) Use: //schematic load yourschematicname in order to load it again. Then, you can use //paste to paste it into the world.");
            }
            else if (args[1].equals("-a"))
            {
                if (TFM_SuperadminList.isUserSuperadmin(sender))
                {
                    MAUtils.bcastMsg("1.) Do //wand (or use the //pos commands).", ChatColor.RED);
                    MAUtils.bcastMsg("2.) Select the two outermost angles of your build.", ChatColor.RED);
                    MAUtils.bcastMsg("3.) Do //copy in order to copy your build.", ChatColor.RED);
                    MAUtils.bcastMsg("4.) Use: //schematic save yourschematicname in order to save your build.", ChatColor.RED);
                    MAUtils.bcastMsg("5.) Use: //schematic load yourschematicname in order to load it again. Then, you can use //paste to paste it into the world.", ChatColor.RED);
                }
                else
                {
                    sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                }
            }
            return true;
        }

        else if (args[0].equalsIgnoreCase("explode"))
        {
            if (TFM_SuperadminList.isUserSuperadmin(sender))
            {
                sender_p.getWorld().createExplosion(sender_p.getLocation(), 5F);

                sender_p.getWorld().strikeLightning(sender_p.getLocation());
                sender.sendMessage(ChatColor.RED + "Exploded!");
                sender.sendMessage(ChatColor.YELLOW + "Location: " + sender_p.getLocation());
            }
            else
            {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            }
        }

        else if (args[0].equalsIgnoreCase("spanish"))
        {
            if (TFM_SuperadminList.isSeniorAdmin(sender))
            {
                // coming soon on april fools day
                // will be removed after april fools day
                // all seniors can use it but it should not be used until april fools day

                if (isSpanish)
                {
                    isSpanish = false;
                    sender.sendMessage(ChatColor.RED + "Disabled Spanish mode.");
                }
                else
                {
                    isSpanish = true;
                    sender.sendMessage(ChatColor.GREEN + "Oh boy... enabled spanish mode!");
                }
            }
            else
            {
                sender.sendMessage(ChatColor.RED + "How did you do that?");
            }
        }

        else if (args[0].equalsIgnoreCase("fireball"))
        {
            if (TFM_SuperadminList.isUserSuperadmin(sender))
            {
                Class<? extends org.bukkit.entity.Entity> type = org.bukkit.entity.Fireball.class;
                int speed = 2;
                if (args.length > 1)
                {
                    if (args[1].equalsIgnoreCase("small"))
                    {
                        type = org.bukkit.entity.SmallFireball.class;
                    }
                    else if (args[1].equalsIgnoreCase("arrow"))
                    {
                        type = org.bukkit.entity.Arrow.class;
                    }
                    else if (args[1].equalsIgnoreCase("skull"))
                    {
                        type = WitherSkull.class;
                    }
                    else if (args[1].equalsIgnoreCase("egg"))
                    {
                        type = org.bukkit.entity.Egg.class;
                    }
                    else if (args[1].equalsIgnoreCase("snowball"))
                    {
                        type = org.bukkit.entity.Snowball.class;
                    }
                    else if (args[1].equalsIgnoreCase("expbottle"))
                    {
                        type = org.bukkit.entity.ThrownExpBottle.class;
                    }
                    else if (args[1].equalsIgnoreCase("large"))
                    {
                        type = org.bukkit.entity.LargeFireball.class;
                    }
                }
                Vector direction = sender_p.getEyeLocation().getDirection().multiply(speed);
                Projectile projectile = (Projectile) sender_p.getWorld().spawn(sender_p.getEyeLocation().add(direction.getX(), direction.getY(), direction.getZ()), type);
                projectile.setShooter(sender_p);
                projectile.setVelocity(direction);
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
