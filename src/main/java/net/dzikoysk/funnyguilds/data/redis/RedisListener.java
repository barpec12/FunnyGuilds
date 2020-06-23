package net.dzikoysk.funnyguilds.data.redis;

import com.google.gson.Gson;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.guild.GuildUtils;
import net.dzikoysk.funnyguilds.basic.guild.Region;
import net.dzikoysk.funnyguilds.basic.guild.RegionUtils;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.basic.user.UserUtils;
import org.bukkit.Bukkit;
import redis.clients.jedis.JedisPubSub;

/**
 * @author barpec12 on 21.06.2020
 */
public class RedisListener extends JedisPubSub {
    @Override
    public void onMessage(String channel, String message) {
        Gson gson = new Gson();
        try {
            switch (channel) {
                case "fguilds-guilds":
                    Guild guild = RedisGuild.deserialize(message);
                    Guild guildToRemove = GuildUtils.getByName(guild.getName());
                    if (guildToRemove != null)
                        GuildUtils.removeGuild(guildToRemove);
                    GuildUtils.addGuild(guild);
                    guild.deserializationUpdate();
                    guild.wasChanged();
                    break;
                case "fguilds-regions":
                    Region region = RedisRegion.deserialize(message);
                    region.setDuringSerialization(true);
                    Region regionToRemove = RegionUtils.get(region.getName());
                    if (regionToRemove != null)
                        RegionUtils.removeRegion(regionToRemove);
                    Guild regionGuild = GuildUtils.getByName(region.getName());
                    if (regionGuild != null)
                        region.setGuild(regionGuild);
                    RegionUtils.addRegion(region);
                    region.wasChanged();
                    region.setDuringSerialization(false);
                    break;
                case "fguilds-users":
                    User user = RedisUser.deserialize(message);
                    User userToRemove = UserUtils.get(user.getUUID());
                    if (userToRemove != null)
                        UserUtils.removeUser(userToRemove);
                    UserUtils.addUser(user);
                    user.wasChanged();
                    break;
                case "fguilds-remove":
                    Guild toRemove = GuildUtils.getByName(message);
                    if (toRemove != null) {
                        GuildUtils.deleteGuild(toRemove, false);
                    }
                    break;
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
