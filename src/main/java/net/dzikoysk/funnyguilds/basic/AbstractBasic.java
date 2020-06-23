package net.dzikoysk.funnyguilds.basic;

import com.google.gson.Gson;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.guild.Region;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.data.redis.Redis;
import net.dzikoysk.funnyguilds.data.redis.RedisGuild;
import net.dzikoysk.funnyguilds.data.redis.RedisRegion;
import net.dzikoysk.funnyguilds.data.redis.RedisUser;
import org.bukkit.Bukkit;
import org.json.simple.JSONObject;
import redis.clients.jedis.Jedis;

public abstract class AbstractBasic implements Basic {

    private boolean wasChanged = true;
    private static Gson gson = new Gson();
    private boolean duringSerialization;

    @Override
    public void markChanged() {
        this.wasChanged = true;

        if(!FunnyGuilds.getInstance().getPluginConfiguration().redisConfig.enabled && FunnyGuilds.getInstance().isEnabled())
            return;
        if(this.duringSerialization)
            return;
        String channel = "fguilds-";
        JSONObject jsonObject;
        if(this instanceof Guild) {
            channel +="guilds";
            jsonObject = RedisGuild.serialize((Guild) this);
        }else if(this instanceof Region){
            channel +="regions";
            if(((Region)this).getGuild() == null)
                return;
            jsonObject = RedisRegion.serialize((Region) this);
        }else if (this instanceof User){
            channel +="users";
            jsonObject = RedisUser.serialize((User) this);
        }else return;

        if(jsonObject != null) {
            Jedis jedis = Redis.getInstance().getJedisPool().getResource();
            jedis.publish(channel, jsonObject.toJSONString());
            Bukkit.getLogger().info(channel);
            jedis.close();
        }
    }

    @Override
    public boolean wasChanged() {
        boolean changedState = this.wasChanged;

        if (changedState) {
            this.wasChanged = false;
        }

        return changedState;
    }
    public void setDuringSerialization(boolean bool){
        this.duringSerialization = bool;
    }
}
