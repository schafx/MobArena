package com.garbagemule.MobArena;

import com.garbagemule.MobArena.WileeCommands.MA_Command;
import com.garbagemule.MobArena.commands.CommandHandler;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.framework.ArenaMaster;
import com.garbagemule.MobArena.listeners.MAGlobalListener;
import com.garbagemule.MobArena.listeners.MagicSpellsListener;
import com.garbagemule.MobArena.metrics.Metrics;
import com.garbagemule.MobArena.util.config.ConfigUtils;
import com.garbagemule.MobArena.util.inventory.InventoryManager;
import com.garbagemule.MobArena.waves.ability.AbilityManager;
import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * MobArena
 *
 * @author garbagemule
 */
public class MobArena extends JavaPlugin {
    public static MobArena plugin;
    public static final Logger log = Bukkit.getLogger();
    public static final double MIN_PLAYER_DISTANCE_SQUARED = 225D;
    public static final int ECONOMY_MONEY_ID = -29;
    public static Random random = new Random();
    public static final String COMMAND_PATH = "com.garbagemule.MobArena.WileeCommands";
    public static final String COMMAND_PREFIX = "Command_";

    public static File getRoot() {
        return new File(".");
    }

    public static File getPluginsFolder() {
        return new File(getRoot(), "plugins");
    }

    private ArenaMaster arenaMaster;
    private CommandHandler commandHandler;
    // Inventories from disconnects
    private Set<String> inventoriesToRestore;
    // Vault
    private Economy economy;

    @Override
    public void onLoad() {
        plugin = this;
    }

    @Override
    public void onEnable() {
        // Initialize config-file
        loadConfigFile();

        // Initialize announcements-file
        loadAnnouncementsFile();

        // Load boss abilities
        loadAbilities();

        // Set up soft dependencies
        setupVault();
        setupMagicSpells();

        // Set up the ArenaMaster
        arenaMaster = new ArenaMasterImpl(this);
        arenaMaster.initialize();

        // Register any inventories to restore.
        registerInventories();

        // Register event listeners
        registerListeners();

        // gogo Metrics
        startMetrics();

        // Announce enable!
        log.info("[MobArena] Plugin enabled! Authors: " + getDescription().getAuthors());
    }

    @Override
    public void onDisable() {
        // Force all arenas to end.
        if (arenaMaster == null) {
            return;
        }
        for (Arena arena : arenaMaster.getArenas()) {
            arena.forceEnd();
        }
        arenaMaster.resetArenaMap();

        log.info("[MobArena] Plugin disabled.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        try {
            Player sender_p = null;
            boolean senderIsConsole = false;
            if (sender instanceof Player) {
                sender_p = (Player) sender;
                log.info(String.format("[PLAYER_COMMAND] %s(%s): /%s %s",
                        sender_p.getName(),
                        ChatColor.stripColor(sender_p.getDisplayName()),
                        commandLabel,
                        MAUtils.implodeStringList(" ", Arrays.asList(args))));
            } else {
                senderIsConsole = true;
                log.info(String.format("[CONSOLE_COMMAND] %s: /%s %s",
                        sender.getName(),
                        commandLabel,
                        MAUtils.implodeStringList(" ", Arrays.asList(args))));
            }

            MA_Command dispatcher;
            try {
                ClassLoader classLoader = MobArena.class.getClassLoader();
                dispatcher = (MA_Command) classLoader.loadClass(String.format("%s.%s%s", COMMAND_PATH, COMMAND_PREFIX, cmd.getName().toLowerCase())).newInstance();
                dispatcher.setPlugin(this);
            } catch (Throwable ex) {
                log.log(Level.SEVERE, "[" + getDescription().getName() + "] Command not loaded: " + cmd.getName(), ex);
                sender.sendMessage(ChatColor.RED + "Command Error: Command not loaded: " + cmd.getName());
                return true;
            }

            try {
                return dispatcher.run(sender, sender_p, cmd, commandLabel, args, senderIsConsole);
            } catch (Throwable ex) {
                sender.sendMessage(ChatColor.RED + "Command Error: " + ex.getMessage());
            }

            dispatcher = null;
        } catch (Throwable ex) {
            log.log(Level.SEVERE, "[" + getDescription().getName() + "] Command Error: " + commandLabel, ex);
            sender.sendMessage(ChatColor.RED + "Unknown Command Error.");
        }

        return true;
    }

    /*
     @Override
     public boolean onCommand(final CommandSender sender, Command cmd, String label, String[] args)
     {
     if (cmd.getName().equalsIgnoreCase("wileemanage"))
     {
     if (TFM_SuperadminList.isUserSuperadmin(sender))
     {
     if (args.length == 0)
     {
     return false;
     }
     else
     {
     final Player pl = Bukkit.getPlayer(args[1]);
     TFM_Superadmin entry = TFM_SuperadminList.getAdminEntry((Player) pl);
     if (entry == null)
     {
     entry = TFM_SuperadminList.getAdminEntryByIP(pl.getAddress().getAddress().getHostAddress().trim());
     }
     if (args[0].equalsIgnoreCase("obliviate"))
     {
     if (entry.isTelnetAdmin())
     {
     if (args.length == 0)
     {
     return false;
     }
        					
     final Player player = Bukkit.getPlayer(args[1]);
     MAUtils.adminAction(sender.getName(), "Casting complete oblivion over " + player.getName(), ChatColor.RED);
     MAUtils.bcastMsg(player.getName() + " will be completely obliviated using thy holy powers!", ChatColor.RED);
                                
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

     // generate explosion
     player.getWorld().createExplosion(player.getLocation(), 4F);

     new BukkitRunnable()
     {
     @Override
     public void run()
     {
     // strike lightning
     player.getWorld().strikeLightning(player.getLocation());

     // kill (if not done already)
     player.setHealth(0.0);
     }
     }.runTaskLater(plugin, 20L * 2L);

     new BukkitRunnable()
     {
     @Override
     public void run()
     {
     // message
     MAUtils.adminAction(sender.getName(), "Banning " + player.getName() + ", IP: " + IP, ChatColor.RED);

     // generate explosion
     player.getWorld().createExplosion(player.getLocation(), 4F);

     // kick player
     player.kickPlayer(ChatColor.RED + "FUCKOFF, and get your MOTHER FUCKING shit together!");
     }
     }.runTaskLater(plugin, 20L * 3L);
     }
     else
     {
     sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
     }
     }
    				
     else if (args[0].equalsIgnoreCase("nope"))
     {
     if (entry.isTelnetAdmin())
     {
     if (args.length == 0)
     {
     return false;
     }
        					
     final Player player = Bukkit.getPlayer(args[1]);
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
                            
     new BukkitRunnable()
     {
     @Override
     public void run()
     {
     // go up into the sky
     player.setVelocity(new org.bukkit.util.Vector(0, 4, 0));
                                    
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
     // message
     MAUtils.adminAction(sender.getName(), "Banning " + player.getName() + ", IP: " + IP, ChatColor.RED);

     // generate explosion
     player.getWorld().createExplosion(player.getLocation(), 4F);

     // kick player
     player.kickPlayer(ChatColor.RED + "NOPE!\nAppeal at totalfreedom.boards.net\nAnd make sure you follow the rules at totalfreedom.me");
     }
     }.runTaskLater(plugin, 20L * 3L);
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
     MAUtils.adminAction(sender.getName(), "Removing " + sender.getName() + "from the superadmin list.", ChatColor.RED);
     TFM_SuperadminList.removeSuperadmin(p);
     }
     else
     {
     sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
     }
     }
     }
     }
     else
     {
     sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
     }
     }
     return true;
     }
     */
    private void loadConfigFile() {
        // Create if missing
        saveDefaultConfig();

        // Set the header and save
        getConfig().options().header(getHeader());
        saveConfig();
    }

    private void loadAnnouncementsFile() {
        // Create if missing
        File file = new File(getDataFolder(), "announcements.yml");
        try {
            if (file.createNewFile()) {
                Messenger.info("announcements.yml created.");
                YamlConfiguration yaml = Msg.toYaml();
                yaml.save(file);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Otherwise, load the announcements from the file
        try {
            YamlConfiguration yaml = new YamlConfiguration();
            yaml.load(file);
            ConfigUtils.addMissingRemoveObsolete(file, Msg.toYaml(), yaml);
            Msg.load(yaml);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void registerListeners() {
        // Bind the /ma, /mobarena commands to MACommands.
        commandHandler = new CommandHandler(this);
        getCommand("ma").setExecutor(commandHandler);
        getCommand("mobarena").setExecutor(commandHandler);

        PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents(new MAGlobalListener(this, arenaMaster), this);
    }

    // Permissions stuff
    public boolean has(Player p, String s) {
        return p.hasPermission(s);
    }

    public boolean has(CommandSender sender, String s) {
        if (sender instanceof ConsoleCommandSender) {
            return true;
        }
        return has((Player) sender, s);
    }

    private void setupVault() {
        Plugin vaultPlugin = this.getServer().getPluginManager().getPlugin("Vault");
        if (vaultPlugin == null) {
            Messenger.warning("Vault was not found. Economy rewards will not work!");
            return;
        }

        ServicesManager manager = this.getServer().getServicesManager();
        RegisteredServiceProvider<Economy> e = manager.getRegistration(net.milkbowl.vault.economy.Economy.class);

        if (e != null) {
            economy = e.getProvider();
            Messenger.info("Vault found; economy rewards enabled.");
        } else {
            Messenger.warning("Vault found, but no economy plugin detected. Economy rewards will not work!");
        }
    }

    private void setupMagicSpells() {
        Plugin spells = this.getServer().getPluginManager().getPlugin("MagicSpells");
        if (spells == null) {
            return;
        }

        Messenger.info("MagicSpells found, loading config-file.");
        this.getServer().getPluginManager().registerEvents(new MagicSpellsListener(this), this);
    }

    private void loadAbilities() {
        File dir = new File(this.getDataFolder(), "abilities");
        if (!dir.exists()) {
            dir.mkdir();
        }

        AbilityManager.loadCoreAbilities();
        AbilityManager.loadCustomAbilities(dir);
    }

    private void startMetrics() {
        try {
            Metrics m = new Metrics(this);
            m.start();
        } catch (Exception e) {
            Messenger.warning("y u disable stats :(");
        }
    }

    public ArenaMaster getArenaMaster() {
        return arenaMaster;
    }

    public CommandHandler getCommandHandler() {
        return commandHandler;
    }

    private String getHeader() {
        String sep = System.getProperty("line.separator");
        return "MobArena v" + this.getDescription().getVersion() + " - Config-file" + sep
                + "Read the Wiki for details on how to set up this file: http://goo.gl/F5TTc" + sep
                + "Note: You -must- use spaces instead of tabs!";
    }

    private void registerInventories() {
        this.inventoriesToRestore = new HashSet<String>();

        File dir = new File(getDataFolder(), "inventories");
        if (!dir.exists()) {
            dir.mkdir();
            return;
        }

        for (File f : dir.listFiles()) {
            if (f.getName().endsWith(".inv")) {
                inventoriesToRestore.add(f.getName().substring(0, f.getName().indexOf(".")));
            }
        }
    }

    public void restoreInventory(Player p) {
        if (!inventoriesToRestore.contains(p.getName())) {
            return;
        }

        if (InventoryManager.restoreFromFile(this, p)) {
            inventoriesToRestore.remove(p.getName());
        }
    }

    public boolean giveMoney(Player p, ItemStack item) {
        if (economy != null) {
            EconomyResponse result = economy.depositPlayer(p.getName(), getAmount(item));
            return (result.type == ResponseType.SUCCESS);
        }
        return false;
    }

    public boolean takeMoney(Player p, ItemStack item) {
        if (economy != null) {
            EconomyResponse result = economy.withdrawPlayer(p.getName(), getAmount(item));
            return (result.type == ResponseType.SUCCESS);
        }
        return false;
    }

    public boolean hasEnough(Player p, ItemStack item) {
        if (economy != null) {
            return (economy.getBalance(p.getName()) >= getAmount(item));
        }
        return true;
    }

    public String economyFormat(ItemStack item) {
        if (economy != null) {
            return economy.format(getAmount(item));
        }
        return null;
    }

    private double getAmount(ItemStack item) {
        double major = item.getAmount();
        double minor = item.getDurability() / 100D;
        return major + minor;
    }
}
