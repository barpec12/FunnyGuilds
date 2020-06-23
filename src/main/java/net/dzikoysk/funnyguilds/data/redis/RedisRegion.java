package net.dzikoysk.funnyguilds.data.redis;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Region;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import net.dzikoysk.funnyguilds.data.util.DeserializationUtils;
import net.dzikoysk.funnyguilds.util.commons.bukkit.LocationUtils;
import org.bukkit.Location;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * @author barpec12 on 21.06.2020
 */
public class RedisRegion {


    public static Region deserialize(String message) {

        PluginConfiguration configuration = FunnyGuilds.getInstance().getPluginConfiguration();
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = null;
        try {
            jsonObject = (JSONObject) jsonParser.parse(message);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String name = (String) jsonObject.get("name");
        String cs = (String) jsonObject.get("center");
        int size = new Long((long) jsonObject.get("size")).intValue();
        int enlarge = new Long((long) jsonObject.get("enlarge")).intValue();

        Location center = LocationUtils.parseLocation(cs);

        if (center == null) {
            FunnyGuilds.getInstance().getPluginLogger().error("Cannot deserialize region! Caused by: center is null");
            return null;
        }

        Object[] values = new Object[4];
        values[0] = name;
        values[1] = center;
        values[2] = size;
        values[3] = enlarge;

        return DeserializationUtils.deserializeRegion(values);
    }

    public static JSONObject serialize(Region region) {

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("name", region.getName());
        jsonObject.put("center", LocationUtils.toString(region.getCenter()));
        jsonObject.put("size", region.getSize());
        jsonObject.put("enlarge", region.getEnlarge());

        return jsonObject;
    }


}
