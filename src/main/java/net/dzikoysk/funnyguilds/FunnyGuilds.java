package net.dzikoysk.funnyguilds;

import com.google.common.base.Throwables;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.guild.GuildUtils;
import net.dzikoysk.funnyguilds.basic.rank.RankRecalculationTask;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.basic.user.UserUtils;
import net.dzikoysk.funnyguilds.command.Commands;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyManager;
import net.dzikoysk.funnyguilds.data.DataModel;
import net.dzikoysk.funnyguilds.data.DataPersistenceHandler;
import net.dzikoysk.funnyguilds.data.InvitationPersistenceHandler;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import net.dzikoysk.funnyguilds.data.database.Database;
import net.dzikoysk.funnyguilds.data.redis.Redis;
import net.dzikoysk.funnyguilds.data.redis.RedisListener;
import net.dzikoysk.funnyguilds.element.gui.GuiActionHandler;
import net.dzikoysk.funnyguilds.element.tablist.AbstractTablist;
import net.dzikoysk.funnyguilds.element.tablist.TablistBroadcastHandler;
import net.dzikoysk.funnyguilds.hook.PluginHook;
import net.dzikoysk.funnyguilds.listener.EntityDamage;
import net.dzikoysk.funnyguilds.listener.EntityInteract;
import net.dzikoysk.funnyguilds.listener.PlayerChat;
import net.dzikoysk.funnyguilds.listener.PlayerDeath;
import net.dzikoysk.funnyguilds.listener.PlayerJoin;
import net.dzikoysk.funnyguilds.listener.PlayerLogin;
import net.dzikoysk.funnyguilds.listener.PlayerQuit;
import net.dzikoysk.funnyguilds.listener.dynamic.DynamicListenerManager;
import net.dzikoysk.funnyguilds.listener.region.BlockBreak;
import net.dzikoysk.funnyguilds.listener.region.BlockIgnite;
import net.dzikoysk.funnyguilds.listener.region.BlockPhysics;
import net.dzikoysk.funnyguilds.listener.region.BlockPlace;
import net.dzikoysk.funnyguilds.listener.region.BucketAction;
import net.dzikoysk.funnyguilds.listener.region.EntityExplode;
import net.dzikoysk.funnyguilds.listener.region.HangingBreak;
import net.dzikoysk.funnyguilds.listener.region.HangingPlace;
import net.dzikoysk.funnyguilds.listener.region.PlayerCommand;
import net.dzikoysk.funnyguilds.listener.region.PlayerInteract;
import net.dzikoysk.funnyguilds.listener.region.PlayerMove;
import net.dzikoysk.funnyguilds.listener.region.PlayerRespawn;
import net.dzikoysk.funnyguilds.system.GuildValidationHandler;
import net.dzikoysk.funnyguilds.util.commons.ConfigHelper;
import net.dzikoysk.funnyguilds.util.metrics.MetricsCollector;
import net.dzikoysk.funnyguilds.util.nms.DescriptionChanger;
import net.dzikoysk.funnyguilds.util.nms.GuildEntityHelper;
import net.dzikoysk.funnyguilds.util.nms.PacketExtension;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import redis.clients.jedis.Jedis;

import java.io.File;
import java.io.IOException;

public class FunnyGuilds extends JavaPlugin {

    private static FunnyGuilds funnyguilds;

    private String fullVersion;
    private String mainVersion;

    private final File pluginConfigurationFile  = new File(this.getDataFolder(), "config.yml");
    private final File messageConfigurationFile = new File(this.getDataFolder(), "messages.yml");
    private final File pluginDataFolderFile     = new File(this.getDataFolder(), "data");

    private FunnyGuildsLogger      logger;
    private PluginConfiguration    pluginConfiguration;
    private MessageConfiguration   messageConfiguration;
    private ConcurrencyManager     concurrencyManager;
    private DynamicListenerManager dynamicListenerManager;

    private volatile BukkitTask guildValidationTask;
    private volatile BukkitTask tablistBroadcastTask;
    private volatile BukkitTask rankRecalculationTask;

    private DataModel                    dataModel;
    private DataPersistenceHandler       dataPersistenceHandler;
    private InvitationPersistenceHandler invitationPersistenceHandler;

    private GitHub githubAPI;

    private boolean isDisabling;
    private boolean forceDisabling;

    @Override
    public void onLoad() {
        funnyguilds = this;
        this.logger = new FunnyGuildsLogger(this);

        DescriptionChanger descriptionChanger = new DescriptionChanger(super.getDescription());
        Pair<String, String> versions = descriptionChanger.extractVersion();

        this.fullVersion = versions.getLeft();
        this.mainVersion = versions.getValue();

        try {
            Class.forName("net.md_5.bungee.api.ChatColor");
        }
        catch (Exception spigotNeeded) {
            FunnyGuilds.getInstance().getPluginLogger().error("FunnyGuilds requires spigot to work, your server seems to be using something else");
            FunnyGuilds.getInstance().getPluginLogger().error("If you think that is not true - contact plugin developers");
            FunnyGuilds.getInstance().getPluginLogger().error("https://github.com/FunnyGuilds/FunnyGuilds");

            getServer().getPluginManager().disablePlugin(this);
            this.forceDisabling = true;

            return;
        }

        if (! this.getDataFolder().exists()) {
            this.getDataFolder().mkdir();
        }

        try {
            this.pluginConfiguration = ConfigHelper.loadConfig(this.pluginConfigurationFile, PluginConfiguration.class);
            this.messageConfiguration = ConfigHelper.loadConfig(this.messageConfigurationFile, MessageConfiguration.class);

            this.pluginConfiguration.load();
            this.messageConfiguration.load();
        }
        catch (Exception ex) {
            this.getPluginLogger().error("Could not load plugin configuration", ex);
            this.getServer().getPluginManager().disablePlugin(this);
            this.forceDisabling = true;
            return;
        }

        PluginConfiguration settings = FunnyGuilds.getInstance().getPluginConfiguration();
        descriptionChanger.rename(settings.pluginName);

        this.concurrencyManager = new ConcurrencyManager(settings.concurrencyThreads);
        this.concurrencyManager.printStatus();

        Commands commands = new Commands();
        commands.register();

        this.dynamicListenerManager = new DynamicListenerManager(this);
    }

    @Override
    public void onEnable() {
        funnyguilds = this;
        if (this.forceDisabling) {
            return;
        }

        this.dataModel = DataModel.create(this, this.pluginConfiguration.dataModel);
        this.dataModel.load();

        if(this.pluginConfiguration.redisConfig.enabled) {
            new Thread("FGuilds Redis Subscriber") {
                @Override
                public void run() {
                    Bukkit.getLogger().info("Connecting to Redis...");
                    Jedis jedis = Redis.getInstance().getJedisPool().getResource();
                    jedis.subscribe(new RedisListener(), "fguilds-guilds", "fguilds-regions", "fguilds-users", "fguilds-remove");
                    jedis.close();
                }
            }.start();
        }
        this.dataPersistenceHandler = new DataPersistenceHandler(this);
        this.dataPersistenceHandler.startHandler();

        this.invitationPersistenceHandler = new InvitationPersistenceHandler(this);
        this.invitationPersistenceHandler.loadInvitations();
        this.invitationPersistenceHandler.startHandler();

        MetricsCollector collector = new MetricsCollector(this);
        collector.start();

        this.guildValidationTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this, new GuildValidationHandler(), 100L, 20L);
        this.tablistBroadcastTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this, new TablistBroadcastHandler(), 20L, this.pluginConfiguration.playerListUpdateInterval_);
        this.rankRecalculationTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this, new RankRecalculationTask(), 20L, this.pluginConfiguration.rankingUpdateInterval_);

        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new GuiActionHandler(), this);
        pluginManager.registerEvents(new EntityDamage(), this);
        pluginManager.registerEvents(new EntityInteract(), this);
        pluginManager.registerEvents(new PlayerChat(), this);
        pluginManager.registerEvents(new PlayerDeath(), this);
        pluginManager.registerEvents(new PlayerJoin(this), this);
        pluginManager.registerEvents(new PlayerLogin(), this);
        pluginManager.registerEvents(new PlayerQuit(), this);

        this.dynamicListenerManager.registerDynamic(() -> pluginConfiguration.regionsEnabled,
                new BlockBreak(),
                new BlockIgnite(),
                new BlockPlace(),
                new BucketAction(),
                new EntityExplode(),
                new HangingBreak(),
                new HangingPlace(),
                new PlayerCommand(),
                new PlayerInteract()
        );

        this.dynamicListenerManager.registerDynamic(() -> pluginConfiguration.regionsEnabled && pluginConfiguration.eventMove, new PlayerMove());
        this.dynamicListenerManager.registerDynamic(() -> pluginConfiguration.regionsEnabled && pluginConfiguration.eventPhysics, new BlockPhysics());
        this.dynamicListenerManager.registerDynamic(() -> pluginConfiguration.regionsEnabled && pluginConfiguration.respawnInBase, new PlayerRespawn());
        this.dynamicListenerManager.reloadAll();
        this.patch();

        try {
            this.githubAPI = new GitHubBuilder().build();
        }
        catch (IOException ex) {
            this.getPluginLogger().debug("Could not initialize GitHub API!");
            this.getPluginLogger().debug(Throwables.getStackTraceAsString(ex));
        }

        FunnyGuildsVersion.isNewAvailable(this.getServer().getConsoleSender(), true);
        PluginHook.init();

        FunnyGuilds.getInstance().getPluginLogger().info("~ Created by FunnyGuilds Team ~");
    }

    @Override
    public void onDisable() {
        if (this.forceDisabling) {
            return;
        }

        this.isDisabling = true;

        this.dynamicListenerManager.unregisterAll();
        GuildEntityHelper.despawnGuildHearts();

        this.guildValidationTask.cancel();
        this.tablistBroadcastTask.cancel();
        this.rankRecalculationTask.cancel();

        for (User user : UserUtils.getUsers()) {
            user.getBossBar().removeNotification();
        }

        this.dataModel.save(true);
        this.dataPersistenceHandler.stopHandler();

        this.invitationPersistenceHandler.saveInvitations();
        this.invitationPersistenceHandler.stopHandler();

        this.getServer().getScheduler().cancelTasks(this);
        Database.getInstance().shutdown();

        if(this.pluginConfiguration.redisConfig.enabled)
            Redis.getInstance().shutdown();

        funnyguilds = null;
    }

    private void patch() {
        for (Player player : this.getServer().getOnlinePlayers()) {
            this.getServer().getScheduler().runTask(this, () -> PacketExtension.registerPlayer(player));

            User user = User.get(player);

            if (user.getCache().getScoreboard() == null) {
                user.getCache().setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            }

            user.getCache().getDummy();

            if (!pluginConfiguration.playerListEnable) {
                continue;
            }

            AbstractTablist.createTablist(pluginConfiguration.playerList, pluginConfiguration.playerListHeader, pluginConfiguration.playerListFooter, pluginConfiguration.playerListPing, player);
        }

        for (Guild guild : GuildUtils.getGuilds()) {
            if (pluginConfiguration.createEntityType != null) {
                GuildEntityHelper.spawnGuildHeart(guild);
            }

            guild.updateRank();
        }
    }

    public boolean isDisabling() {
        return this.isDisabling;
    }

    public FunnyGuildsLogger getPluginLogger() {
        return this.logger;
    }

    public File getPluginDataFolder() {
        return this.pluginDataFolderFile;
    }

    public DataModel getDataModel() {
        return this.dataModel;
    }

    public DataPersistenceHandler getDataPersistenceHandler() {
        return this.dataPersistenceHandler;
    }

    public InvitationPersistenceHandler getInvitationPersistenceHandler() {
        return this.invitationPersistenceHandler;
    }

    public PluginConfiguration getPluginConfiguration() {
        return this.pluginConfiguration;
    }

    public File getPluginConfigurationFile() {
        return this.pluginConfigurationFile;
    }

    public MessageConfiguration getMessageConfiguration() {
        return this.messageConfiguration;
    }

    public ConcurrencyManager getConcurrencyManager() {
        return this.concurrencyManager;
    }

    public DynamicListenerManager getDynamicListenerManager() {
        return this.dynamicListenerManager;
    }

    public void reloadPluginConfiguration() {
        this.pluginConfiguration = ConfigHelper.loadConfig(this.pluginConfigurationFile, PluginConfiguration.class);
        this.pluginConfiguration.load();
    }

    public void reloadMessageConfiguration() {
        this.messageConfiguration = ConfigHelper.loadConfig(this.messageConfigurationFile, MessageConfiguration.class);
        this.messageConfiguration.load();
    }

    public String getFullVersion() {
        return this.fullVersion;
    }

    public String getMainVersion() {
        return this.mainVersion;
    }

    protected GitHub getGithubAPI() {
        return this.githubAPI;
    }

    public static FunnyGuilds getInstance() {
        return funnyguilds;
    }

}
