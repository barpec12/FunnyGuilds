package net.dzikoysk.funnyguilds.data.redis;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.guild.GuildUtils;
import net.dzikoysk.funnyguilds.basic.guild.Region;
import net.dzikoysk.funnyguilds.basic.guild.RegionUtils;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.basic.user.UserUtils;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import net.dzikoysk.funnyguilds.data.util.DeserializationUtils;
import net.dzikoysk.funnyguilds.util.commons.ChatUtils;
import net.dzikoysk.funnyguilds.util.commons.bukkit.LocationUtils;
import org.bukkit.Location;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author barpec12 on 21.06.2020
 */
public class RedisUser {


    public static User deserialize(String message) {

        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = null;
        try {
            jsonObject = (JSONObject) jsonParser.parse(message);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String id = (String) jsonObject.get("uuid");
        String name = (String) jsonObject.get("name");
        int points = new Long((long) jsonObject.get("points")).intValue();
        int kills = new Long((long) jsonObject.get("kills")).intValue();
        int deaths = new Long((long) jsonObject.get("deaths")).intValue();

        long ban = 0;
        String reason = null;
        if(jsonObject.containsKey("ban")) {
            ban = (long) jsonObject.get("ban");
            reason = (String) jsonObject.get("reason");
        }
        if (id == null || name == null) {
            return null;
        }

        Object[] values = new Object[8];
        values[0] = id;
        values[1] = name;
        values[2] = points;
        values[3] = kills;
        values[4] = deaths;
        values[5] = ban;
        values[6] = reason;
        if(jsonObject.containsKey("guild")) {
            String guild = (String) jsonObject.get("guild");
            values[7] = guild;
        }

        return DeserializationUtils.deserializeUser(values);
    }

    public static JSONObject serialize(User user) {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("uuid", user.getUUID().toString());
        jsonObject.put("name", user.getName());
        jsonObject.put("points", user.getRank().getPoints());
        jsonObject.put("kills", user.getRank().getKills());
        jsonObject.put("deaths", user.getRank().getDeaths());
        if(user.hasGuild())
            jsonObject.put("guild", user.getGuild().getName());

        if (user.isBanned()) {
            jsonObject.put("ban", user.getBan().getBanTime());
            jsonObject.put("reason", user.getBan().getReason());
        }

        return jsonObject;
    }


}
