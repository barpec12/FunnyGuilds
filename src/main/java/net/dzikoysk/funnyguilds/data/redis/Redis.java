package net.dzikoysk.funnyguilds.data.redis;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.net.ssl.*;

/**
 * @author barpec12 on 21.06.2020
 */
public class Redis {

    private static Redis instance;

    private static JedisPoolConfig config;
    private final JedisPool jedisPool;
    private final PluginConfiguration.RedisConfig c;

    public Redis() {
        instance = this;

        c = FunnyGuilds.getInstance().getPluginConfiguration().redisConfig;

        JedisPoolConfig poolConfig = getPoolConfig();

        this.jedisPool = new JedisPool(poolConfig, c.host, c.port, c.connectionTimeout, c.password, c.database);
    }

    public static Redis getInstance() {
        if (instance == null) {
            return new Redis();
        }

        return instance;
    }

    public JedisPool getJedisPool() {
        return this.jedisPool;
    }

    public JedisPoolConfig getPoolConfig() {
        if (config == null) {
            JedisPoolConfig poolConfig = new JedisPoolConfig();

            // Each thread trying to access Redis needs its own Jedis instance from the pool.
            // Using too small a value here can lead to performance problems, too big and you have wasted resources.
            int maxConnections = 200;
            poolConfig.setMaxTotal(maxConnections);
            poolConfig.setMaxIdle(maxConnections);

            // Using "false" here will make it easier to debug when your maxTotal/minIdle/etc settings need adjusting.
            // Setting it to "true" will result better behavior when unexpected load hits in production
            poolConfig.setBlockWhenExhausted(true);

            // How long to wait before throwing when pool is exhausted
            poolConfig.setMaxWaitMillis(c.operationTimeout);

            // This controls the number of connections that should be maintained for bursts of load.
            // Increase this value when you see pool.getResource() taking a long time to complete under burst scenarios
            poolConfig.setMinIdle(50);

            Redis.config = poolConfig;
        }

        return config;
    }
    private static class SimpleHostNameVerifier implements HostnameVerifier {

        private String exactCN;
        private String wildCardCN;
        public SimpleHostNameVerifier(String cacheHostname)
        {
            exactCN = "CN=" + cacheHostname;
            wildCardCN = "CN=*" + cacheHostname.substring(cacheHostname.indexOf('.'));
        }

        public boolean verify(String s, SSLSession sslSession) {
            try {
                String cn = sslSession.getPeerPrincipal().getName();
                return cn.equalsIgnoreCase(wildCardCN) || cn.equalsIgnoreCase(exactCN);
            } catch (SSLPeerUnverifiedException ex) {
                return false;
            }
        }
    }
    public void shutdown() {
        this.jedisPool.close();
    }
}
