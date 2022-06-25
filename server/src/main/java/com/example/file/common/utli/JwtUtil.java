package com.example.file.common.utli;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.example.file.common.exception.base.SecurityException;
import com.example.file.common.resp.Consts;
import com.example.file.common.resp.Status;
import com.example.file.config.JwtConfig;
import com.example.file.domain.LoginUser;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@EnableConfigurationProperties(JwtConfig.class)
@Configuration
public class JwtUtil {
    @Autowired
    private JwtConfig jwtConfig;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    public String createJWT(Boolean rememberMe, Long id, String subject) {
        Date now = new Date();
        JwtBuilder builder = Jwts.builder().setId(id.toString()).setSubject(subject).setIssuedAt(now).signWith(SignatureAlgorithm.HS256, jwtConfig.getKey());

        // 设置过期时间
        Long ttl = rememberMe ? jwtConfig.getRemember() : jwtConfig.getTtl();
        if (ttl > 0) { //查看是否有记住
            builder.setExpiration(DateUtil.offsetMillisecond(now, ttl.intValue()));
        }

        String jwt = builder.compact();
        // 将生成的JWT保存至Redis 参数 redis 前缀
        stringRedisTemplate.opsForValue().set(Consts.REDIS_JWT_KEY_PREFIX + id, jwt, ttl, TimeUnit.MILLISECONDS);

        return jwt; //生成token
    }

    public void  deleteJWT(Long id) {
        // 根据id删除掉 redis
        stringRedisTemplate.delete(Consts.REDIS_JWT_KEY_PREFIX + id);
    }



    public String createJWT(Authentication authentication, Boolean rememberMe) {
        LoginUser userPrincipal = (LoginUser) authentication.getPrincipal();
        return createJWT(rememberMe, userPrincipal.getUserPrincipal().getId(), userPrincipal.getUsername());
    }

    public Claims parseJWT(String jwt) {
        try {
            Claims claims = Jwts.parser().setSigningKey(jwtConfig.getKey()).parseClaimsJws(jwt).getBody();
            //获得id
            String id = claims.getId();
            String redisKey = Consts.REDIS_JWT_KEY_PREFIX + id;

            // 校验redis中的JWT是否存在
            Long expire = stringRedisTemplate.getExpire(redisKey, TimeUnit.MILLISECONDS);
            if (Objects.isNull(expire) || expire <= 0) {
                throw new SecurityException(Status.TOKEN_BLANK);
            }

            // 校验redis中的JWT是否与当前的一致，不一致则代表用户已注销/用户在不同设备登录，均代表JWT已过期
            String redisToken = stringRedisTemplate.opsForValue().get(redisKey);
            if (!StrUtil.equals(jwt, redisToken)) {
                throw new SecurityException(Status.TOKEN_ERROR);
            }
            return claims;
        } catch (ExpiredJwtException e) {
            log.error("Token 已过期");
            throw new SecurityException(Status.TOKEN_ERROR);
        } catch (UnsupportedJwtException e) {
            log.error("不支持的 Token");
            throw new SecurityException(Status.TOKEN_ERROR);
        } catch (MalformedJwtException e) {
            log.error("Token 无效");
            throw new SecurityException(Status.TOKEN_ERROR);
        } catch (SignatureException e) {
            log.error("无效的 Token 签名");
            throw new SecurityException(Status.TOKEN_ERROR);
        } catch (IllegalArgumentException e) {
            log.error("Token 参数不存在");
            throw new SecurityException(Status.TOKEN_ERROR);
        }
    }

    /**
     * 设置JWT过期
     *
     * @param request 请求
     */
    public void invalidateJWT(HttpServletRequest request) throws SecurityException {
        String jwt = getJwtFromRequest(request);
        String username = getUsernameFromJWT(jwt);
        stringRedisTemplate.delete(Consts.REDIS_JWT_KEY_PREFIX + username);
    }

    /**
     * 根据 jwt 获取用户名
     *
     * @param jwt JWT
     * @return 用户名
     */
    public String getUsernameFromJWT(String jwt)  {
        Claims claims = parseJWT(jwt);
        return claims.getSubject();
    }

    /**
     * 从 request 的 header 中获取 JWT
     * @param request 请求
     * @return JWT
     */
    public String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StrUtil.isNotBlank(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

}
