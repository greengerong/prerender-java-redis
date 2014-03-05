/******************************************
 *                                        *
 * Auth: green gerong                     *
 * Date: 2014-03-04                       *
 * blog: http://greengerong.github.io/    *
 * github: https://github.com/greengerong *
 *                                        *
 ******************************************/

package com.github.greengerong;

import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisCommands;

import javax.servlet.http.HttpServletRequest;

public class PrerenderRedisCache implements PreRenderEventHandler {

    private final static Logger log = LoggerFactory.getLogger(PrerenderRedisCache.class);

    private JedisCommands jedisCommands = null;

    public PrerenderRedisCache() {
        jedisCommands = RedisCacheConfig.getInstance();
    }

    @Override
    public String beforeRender(HttpServletRequest clientRequest) {
        log.debug(String.format("request from %s going...", clientRequest.getRequestURL().toString()));
        try {
            return jedisCommands.get(clientRequest.getRequestURL().toString());
        } catch (Exception e) {
            log.error("Get cache form redis error", e);
        }
        return null;
    }

    @Override
    public void afterRender(HttpServletRequest clientRequest, HttpResponse prerenderResponse, String html) {
        final String url = clientRequest.getRequestURL().toString();
        log.debug(String.format("Cache for %s:\r\n%s", url, html));
        try {
            jedisCommands.set(url, html);
        } catch (Exception e) {
            log.error("Get cache form redis error", e);
        }
    }

    @Override
    public void destroy() {
        if (jedisCommands != null) {
            jedisCommands = null;
        }
    }
}
