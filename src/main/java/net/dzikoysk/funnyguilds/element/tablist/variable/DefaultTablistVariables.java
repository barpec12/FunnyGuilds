package net.dzikoysk.funnyguilds.element.tablist.variable;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.guild.GuildUtils;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.basic.user.UserUtils;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import net.dzikoysk.funnyguilds.element.tablist.variable.impl.GuildDependentTablistVariable;
import net.dzikoysk.funnyguilds.element.tablist.variable.impl.SimpleTablistVariable;
import net.dzikoysk.funnyguilds.element.tablist.variable.impl.TimeFormattedVariable;
import net.dzikoysk.funnyguilds.hook.PluginHook;
import net.dzikoysk.funnyguilds.util.IntegerRange;
import net.dzikoysk.funnyguilds.util.commons.ChatUtils;
import net.dzikoysk.funnyguilds.util.commons.bukkit.MinecraftServerUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.time.format.TextStyle;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class DefaultTablistVariables {

    private static final Map<String, TablistVariable> FUNNY_VARIABLES = new ConcurrentHashMap<>();
    private static final Locale POLISH_LOCALE = new Locale("pl", "PL");
    
    public static Map<String, TablistVariable> getFunnyVariables() {
        if (FUNNY_VARIABLES.isEmpty()) {
            createFunnyVariables();
        }
        
        return new ConcurrentHashMap<>(FUNNY_VARIABLES);
    }
    
    public static void clearFunnyVariables() {
        FUNNY_VARIABLES.clear();
    }
    
    public static void install(TablistVariablesParser parser) {
        parser.add(new TimeFormattedVariable("HOUR", (user, time) -> String.valueOf(time.getHour())));
        parser.add(new TimeFormattedVariable("MINUTE", (user, time) -> String.valueOf(time.getMinute())));
        parser.add(new TimeFormattedVariable("SECOND", (user, time) -> String.valueOf(time.getSecond())));
        parser.add(new TimeFormattedVariable("DAY_OF_WEEK", (user, time) -> time.getDayOfWeek().getDisplayName(TextStyle.FULL, POLISH_LOCALE)));
        parser.add(new TimeFormattedVariable("DAY_OF_MONTH", (user, time) -> String.valueOf(time.getDayOfMonth())));
        parser.add(new TimeFormattedVariable("MONTH", (user, time) -> time.getMonth().getDisplayName(TextStyle.FULL, POLISH_LOCALE)));
        parser.add(new TimeFormattedVariable("MONTH_NUMBER", (user, time) -> String.valueOf(time.getMonthValue())));
        parser.add(new TimeFormattedVariable("YEAR", (user, time) -> String.valueOf(time.getYear())));

        parser.add(new SimpleTablistVariable("PLAYER", User::getName));
        parser.add(new SimpleTablistVariable("TPS", user -> {
            try {
                return MinecraftServerUtils.getRecentTPS(0);
            } catch (IntegerRange.MissingFormatException ex) {
                return "0";
            }
        }));
        
        parser.add(new SimpleTablistVariable("WORLD", user -> {
            Player userPlayer = user.getPlayer();

            if (userPlayer == null) {
                return "";
            }
            
            return userPlayer.getWorld().getName();
        }));
        
        parser.add(new SimpleTablistVariable("ONLINE", user -> {
            Player userPlayer = user.getPlayer();

            if (userPlayer == null) {
                return "";
            }
            
            return Long.toString(Bukkit.getOnlinePlayers().stream().filter(p -> p != null && userPlayer.canSee(p)).count());
        }));

        for (TablistVariable variable : getFunnyVariables().values()) {
            parser.add(variable);
        }
        
        if (PluginHook.isPresent(PluginHook.PLUGIN_WORLDGUARD)) {
            String wgRegionNoValue = FunnyGuilds.getInstance().getMessageConfiguration().wgRegionNoValue;

            parser.add(new SimpleTablistVariable("WG-REGION", user -> {
                List<String> regionNames = getWorldGuardRegionNames(user);
                return regionNames != null && !regionNames.isEmpty() ? regionNames.get(0) : wgRegionNoValue;
            }));

            parser.add(new SimpleTablistVariable("WG-REGIONS", user -> {
                List<String> regionNames = getWorldGuardRegionNames(user);
                return regionNames != null && !regionNames.isEmpty() ? StringUtils.join(regionNames, ", ") : wgRegionNoValue;
            }));
        }
    }

    private static void createFunnyVariables() {
        PluginConfiguration config = FunnyGuilds.getInstance().getPluginConfiguration();
        MessageConfiguration messages = FunnyGuilds.getInstance().getMessageConfiguration();
        
        FUNNY_VARIABLES.put("guilds", new SimpleTablistVariable("GUILDS", user -> String.valueOf(GuildUtils.getGuilds().size())));
        FUNNY_VARIABLES.put("users", new SimpleTablistVariable("USERS", user -> String.valueOf(UserUtils.getUsers().size())));
        
        FUNNY_VARIABLES.put("ping-format", new SimpleTablistVariable("PING-FORMAT", user -> IntegerRange.inRange(user.getPing(), config.pingFormat, "PING").replace("{PING}", String.valueOf(user.getPing()))));
        FUNNY_VARIABLES.put("ping", new SimpleTablistVariable("PING", user -> String.valueOf(user.getPing())));
        FUNNY_VARIABLES.put("points-format", new SimpleTablistVariable("POINTS-FORMAT", user -> IntegerRange.inRange(user.getRank().getPoints(), config.pointsFormat, "POINTS").replace("{POINTS}", String.valueOf(user.getRank().getPoints()))));
        FUNNY_VARIABLES.put("points", new SimpleTablistVariable("POINTS", user -> String.valueOf(user.getRank().getPoints())));
        FUNNY_VARIABLES.put("position", new SimpleTablistVariable("POSITION", user -> String.valueOf(user.getRank().getPosition())));
        FUNNY_VARIABLES.put("kills", new SimpleTablistVariable("KILLS", user -> String.valueOf(user.getRank().getKills())));
        FUNNY_VARIABLES.put("deaths", new SimpleTablistVariable("DEATHS", user -> String.valueOf(user.getRank().getDeaths())));
        FUNNY_VARIABLES.put("kdr", new SimpleTablistVariable("KDR", user -> String.format(Locale.US, "%.2f", user.getRank().getKDR())));

        FUNNY_VARIABLES.put("g-name", GuildDependentTablistVariable.ofGuild("G-NAME", Guild::getName, user -> messages.gNameNoValue));
        FUNNY_VARIABLES.put("g-tag", GuildDependentTablistVariable.ofGuild("G-TAG", Guild::getTag, user -> messages.gTagNoValue));
        FUNNY_VARIABLES.put("g-owner", GuildDependentTablistVariable.ofGuild("G-OWNER", guild -> guild.getOwner().getName(), user -> messages.gOwnerNoValue));
        FUNNY_VARIABLES.put("g-deputies", GuildDependentTablistVariable.ofGuild("G-DEPUTIES", guild -> guild.getDeputies().isEmpty() ? messages.gDeputiesNoValue : ChatUtils.toString(UserUtils.getNames(guild.getDeputies()), false), user -> messages.gDeputiesNoValue));

        FUNNY_VARIABLES.put("g-deputy", GuildDependentTablistVariable.ofGuild("G-DEPUTY", guild -> guild.getDeputies().isEmpty() ? messages.gDeputyNoValue : guild.getDeputies().iterator().next().getName(), user -> messages.gDeputyNoValue));
        //FUNNY_VARIABLES.put("g-deputy", GuildDependentTablistVariable.ofGuild("G-DEPUTY", guild -> guild.getDeputies().isEmpty() ? messages.gDeputyNoValue : guild.getDeputies().iterator().next(RandomUtils.RANDOM_INSTANCE.nextInt(guild.getDeputies().size())).getName(), user -> messages.gDeputyNoValue));

        FUNNY_VARIABLES.put("g-lives", new GuildDependentTablistVariable("G-LIVES", user -> String.valueOf(user.getGuild().getLives()), user -> "0"));
        FUNNY_VARIABLES.put("g-allies", new GuildDependentTablistVariable("G-ALLIES", user -> String.valueOf(user.getGuild().getAllies().size()), user -> "0"));
        FUNNY_VARIABLES.put("g-points-format", new GuildDependentTablistVariable("G-POINTS-FORMAT", user -> IntegerRange.inRange(user.getGuild().getRank().getPoints(), config.pointsFormat, "POINTS").replace("{POINTS}", String.valueOf(user.getGuild().getRank().getPoints())), user -> IntegerRange.inRange(0, config.pointsFormat, "POINTS").replace("{POINTS}", "0")));
        FUNNY_VARIABLES.put("g-points", new GuildDependentTablistVariable("G-POINTS", user -> String.valueOf(user.getGuild().getRank().getPoints()), user -> "0"));
        FUNNY_VARIABLES.put("g-kills", new GuildDependentTablistVariable("G-KILLS", user -> String.valueOf(user.getGuild().getRank().getKills()), user -> "0"));
        FUNNY_VARIABLES.put("g-deaths", new GuildDependentTablistVariable("G-DEATHS", user -> String.valueOf(user.getGuild().getRank().getDeaths()), user -> "0"));
        FUNNY_VARIABLES.put("g-kdr", new GuildDependentTablistVariable("G-KDR", user -> String.format(Locale.US, "%.2f", user.getGuild().getRank().getKDR()), user -> "0.00"));
        FUNNY_VARIABLES.put("g-members-online", new GuildDependentTablistVariable("G-MEMBERS-ONLINE", user -> String.valueOf(user.getGuild().getOnlineMembers().size()), user -> "0"));
        FUNNY_VARIABLES.put("g-members-all", new GuildDependentTablistVariable("G-MEMBERS-ALL", user -> String.valueOf(user.getGuild().getMembers().size()), user -> "0"));

        FUNNY_VARIABLES.put("g-position", new GuildDependentTablistVariable("G-POSITION", user -> user.getGuild().getMembers().size() >= FunnyGuilds.getInstance().getPluginConfiguration().minMembersToInclude ? String.valueOf(user.getGuild().getRank().getPosition()) : messages.minMembersToIncludeNoValue, user -> messages.minMembersToIncludeNoValue));
        FUNNY_VARIABLES.put("g-validity", new GuildDependentTablistVariable("G-VALIDITY", user -> FunnyGuilds.getInstance().getPluginConfiguration().dateFormat.format(user.getGuild().getValidityDate()), user -> messages.gValidityNoValue));
        FUNNY_VARIABLES.put("g-region-size", new GuildDependentTablistVariable("G-REGION-SIZE", user -> FunnyGuilds.getInstance().getPluginConfiguration().regionsEnabled ? String.valueOf(user.getGuild().getRegion().getSize()) : messages.gRegionSizeNoValue, user -> messages.gRegionSizeNoValue));
    }
    
    private static List<String> getWorldGuardRegionNames(User user) {
        if (user == null || user.getPlayer() == null) {
            return Collections.emptyList();
        }

        Location location = user.getPlayer().getLocation();

        List<String> regionNames = PluginHook.WORLD_GUARD.getRegionNames(location);

        if (regionNames != null && ! regionNames.isEmpty()) {
            return regionNames;
        }

        return null;
    }
    
    private DefaultTablistVariables() {}
    
}
