/******************************************
 *                                        *
 * Auth: green gerong                     *
 * Date: 3/5/14                          *
 * blog: http://greengerong.github.io/    *
 * github: https://github.com/greengerong *
 *                                        *
 ******************************************/
package com.github.greengerong;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisCommands;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.util.List;

public class RedisCacheConfig {


    public static final String REDIS_CACHE_XML = "RedisCache.xml";
    private boolean cluster;
    private String hostAndPorts;

    public List<HostAndPort> getHostAndPorts() {
        List<HostAndPort> jedisClusterNodes = Lists.newArrayList();
        if (StringUtils.isNotBlank(hostAndPorts)) {
            final String[] hostAndPortList = hostAndPorts.trim().split(",");
            for (int i = 0; i < hostAndPortList.length; i++) {
                final String[] hostAndPort = hostAndPortList[i].split(":");
                final HostAndPort item = new HostAndPort(hostAndPort[0].trim(), Integer.parseInt(hostAndPort[1].trim()));
                jedisClusterNodes.add(item);
            }
        }
        return jedisClusterNodes;
    }

    public void setHostAndPorts(String hostAndPorts) {
        this.hostAndPorts = hostAndPorts;
    }

    public boolean isCluster() {
        return cluster;
    }

    public void setCluster(boolean cluster) {
        this.cluster = cluster;
    }

    public static JedisCommands getInstance() {
        final RedisCacheConfig cacheConfig = getRedisCacheConfig();
        return createJedisCommands(cacheConfig);
    }

    private static JedisCommands createJedisCommands(RedisCacheConfig cacheConfig) {
        if (cacheConfig.isCluster()) {
            return new JedisCluster(Sets.newHashSet(cacheConfig.getHostAndPorts()));
        }

        final HostAndPort item = cacheConfig.getHostAndPorts().get(0);
        return new Jedis(item.getHost(), item.getPort());
    }

    private static RedisCacheConfig getRedisCacheConfig() {
        final InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(REDIS_CACHE_XML);
        return deserialization(stream, RedisCacheConfig.class);
    }


    public static <T> T deserialization(final InputStream inputStream, final Class<RedisCacheConfig> type) {
        try {
            JAXBContext jc = JAXBContext.newInstance(type);
            Unmarshaller u = jc.createUnmarshaller();
            return (T) u.unmarshal(inputStream);
        } catch (JAXBException e) {
            throw new RuntimeException("Get Redis Cache Config error.", e);
        }
    }
}
