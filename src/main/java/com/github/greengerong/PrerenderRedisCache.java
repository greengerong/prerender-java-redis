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
import javax.servlet.http.HttpServletResponse;

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
    public String afterRender(HttpServletRequest clientRequest, HttpServletResponse clientResponse, HttpResponse prerenderResponse, String responseHtml) {
        final String url = clientRequest.getRequestURL().toString();
        log.debug(String.format("Cache for %s:\r\n%s", url, responseHtml));
        try {
            jedisCommands.set(url, responseHtml);
        } catch (Exception e) {
            log.error("Get cache form redis error", e);
        }
        return responseHtml;
    }

    @Override
    public void destroy() {
        if (jedisCommands != null) {
            jedisCommands = null;
        }
    }
}
