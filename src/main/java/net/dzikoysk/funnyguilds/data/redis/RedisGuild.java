package net.dzikoysk.funnyguilds.data.redis;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.BasicType;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.guild.GuildUtils;
import net.dzikoysk.funnyguilds.basic.guild.Region;
import net.dzikoysk.funnyguilds.basic.guild.RegionUtils;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.basic.user.UserUtils;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import net.dzikoysk.funnyguilds.data.flat.FlatDataModel;
import net.dzikoysk.funnyguilds.data.util.DeserializationUtils;
import net.dzikoysk.funnyguilds.util.YamlWrapper;
import net.dzikoysk.funnyguilds.util.commons.ChatUtils;
import net.dzikoysk.funnyguilds.util.commons.bukkit.LocationUtils;
import org.bukkit.Location;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author barpec12 on 21.06.2020
 */
public class RedisGuild {


    public static Guild deserialize(String message) {

        PluginConfiguration configuration = FunnyGuilds.getInstance().getPluginConfiguration();
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = null;
        try {
            jsonObject = (JSONObject) jsonParser.parse(message);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String id = (String) jsonObject.get("uuid");
        String name = (String) jsonObject.get("name");
        String tag = (String) jsonObject.get("tag");
        String ownerName = (String) jsonObject.get("owner");
        String deputyName = (String) jsonObject.get("deputy");
        String hs = (String) jsonObject.get("home");
        String regionName = (String) jsonObject.get("region");
        boolean pvp = (boolean) jsonObject.get("pvp");
        long born = (long) jsonObject.get("born");
        long validity = (long) jsonObject.get("validity");
        long attacked = (long) jsonObject.get("attacked");
        long ban = (long) jsonObject.get("ban");
        int lives = new Long((long) jsonObject.get("lives")).intValue();
        String m = (String) jsonObject.get("members");

        if (name == null) {
            FunnyGuilds.getInstance().getPluginLogger().error("[Deserialize] Cannot deserialize guild! Caused by: name is null");
            return null;
        } else if (tag == null) {
            FunnyGuilds.getInstance().getPluginLogger().error("[Deserialize] Cannot deserialize guild: " + name + "! Caused by: tag is null");
            return null;
        } else if (ownerName == null) {
            FunnyGuilds.getInstance().getPluginLogger().error("[Deserialize] Cannot deserialize guild: " + name + "! Caused by: owner is null");
            return null;
        } else if (regionName == null && configuration.regionsEnabled) {
            FunnyGuilds.getInstance().getPluginLogger().error("[Deserialize] Cannot deserialize guild: " + name + "! Caused by: region is null");
            return null;
        }


        final Region region = RegionUtils.get(regionName);
        if (region == null && configuration.regionsEnabled) {
            FunnyGuilds.getInstance().getPluginLogger().error("[Deserialize] Cannot deserialize guild: " + name + "! Caused by: region (object) is null");
            return null;
        }

        UUID uuid = UUID.randomUUID();
        if (id != null && !id.isEmpty()) {
            uuid = UUID.fromString(id);
        }

        final User owner = User.get(ownerName);

        Set<User> deputies = ConcurrentHashMap.newKeySet(1);
        if (deputyName != null && !deputyName.isEmpty()) {
            deputies = UserUtils.getUsers(ChatUtils.fromString(deputyName));
        }

        Location home = null;

        if (region != null) {
            home = region.getCenter();

            if (hs != null) {
                home = LocationUtils.parseLocation(hs);
            }
        }

        Set<User> members = new HashSet<>();
        if (m != null && !m.equals("")) {
            members = UserUtils.getUsers(ChatUtils.fromString(m));
        }
        Set<Guild> allies = GuildUtils.getGuilds(ChatUtils.fromString(m)); //todo guild may not be found

        if (born == 0) {
            born = System.currentTimeMillis();
        }

        if (validity == 0) {
            validity = System.currentTimeMillis() + configuration.validityStart;
        }

        if (lives == 0) {
            lives = configuration.warLives;
        }

        final Object[] values = new Object[17];

        values[0] = uuid;
        values[1] = name;
        values[2] = tag;
        values[3] = owner;
        values[4] = home;
        values[5] = region;
        values[6] = members;
        values[7] = allies;
        values[9] = born;
        values[10] = validity;
        values[11] = attacked;
        values[12] = lives;
        values[13] = ban;
        values[14] = deputies;
        values[15] = pvp;

        return DeserializationUtils.deserializeGuild(values);
    }

    public static JSONObject serialize(Guild guild) {
        if (guild.getName() == null) {
            FunnyGuilds.getInstance().getPluginLogger().error("[Serialize] Cannot serialize guild! Caused by: name is null");
            return null;
        } else if (guild.getTag() == null) {
            FunnyGuilds.getInstance().getPluginLogger().error("[Serialize] Cannot serialize guild: " + guild.getName() + "! Caused by: tag is null");
            return null;
        } else if (guild.getOwner() == null) {
            FunnyGuilds.getInstance().getPluginLogger().error("[Serialize] Cannot serialize guild: " + guild.getName() + "! Caused by: owner is null");
            return null;
        }
        else if (guild.getRegion() == null && FunnyGuilds.getInstance().getPluginConfiguration().regionsEnabled) {
            FunnyGuilds.getInstance().getPluginLogger().error("[Serialize] Cannot serialize guild: " + guild.getName() + "! Caused by: region is null");
            return null;
        }

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("uuid", guild.getUUID().toString());
        jsonObject.put("name", guild.getName());
        jsonObject.put("tag", guild.getTag());
        jsonObject.put("owner", guild.getOwner().getName());
        jsonObject.put("home", LocationUtils.toString(guild.getHome()));
        jsonObject.put("members", ChatUtils.toString(UserUtils.getNames(guild.getMembers()), false));
        jsonObject.put("region", RegionUtils.toString(guild.getRegion()));
        jsonObject.put("regions", null);
        jsonObject.put("allies", ChatUtils.toString(GuildUtils.getNames(guild.getAllies()), false));
        jsonObject.put("born", guild.getBorn());
        jsonObject.put("validity", guild.getValidity());
        jsonObject.put("attacked", guild.getAttacked());
        jsonObject.put("lives", guild.getLives());
        jsonObject.put("ban", guild.getBan());
        jsonObject.put("pvp", guild.getPvP());
        jsonObject.put("deputy", ChatUtils.toString(UserUtils.getNames(guild.getDeputies()), false));

        return jsonObject;
    }


}
