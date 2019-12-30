package pers.liujunyi.cloud.common.redis.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import pers.liujunyi.cloud.common.redis.FastJsonRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;

/**
 * redis 配置
 */
@Configuration
@ConditionalOnClass(RedisOperations.class)
@EnableConfigurationProperties(RedisProperties.class)
@EnableCaching
@RefreshScope
public class RedisConfig extends CachingConfigurerSupport {

    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private int redisPort;

    @Value("${spring.redis.password}")
    private String redisPwd;

    @Value("${spring.redis.timeout}")
    private String redisTimeout;

    @Value("${spring.redis.database}")
    private int redisDb;

    @Value("${spring.redis.jedis.pool.max-active}")
    private int maxActive;

    @Value("${spring.redis.jedis.pool.max-wait}")
    private String maxWait;

    @Value("${spring.redis.jedis.pool.max-idle}")
    private int maxIdle;

    @Value("${spring.redis.jedis.pool.min-idle}")
    private int minIdle;


    /**
     * redis 连接工厂
     * @return
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        // 连接池配置
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(maxActive);
        poolConfig.setMaxIdle(maxIdle);
        long maxWaitMillis = -1;
        if (maxWait.indexOf("ms") != -1) {
            String millis = maxWait.substring(0, maxWait.length()-2);
            maxWaitMillis = Long.valueOf(millis);
        } else {
            maxWaitMillis = Long.valueOf(maxWait);
        }
        poolConfig.setMaxWaitMillis(maxWaitMillis);
        poolConfig.setMinIdle(minIdle);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(false);
        poolConfig.setTestWhileIdle(true);

        // 单点redis
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        // 哨兵redis
        // RedisSentinelConfiguration redisStandaloneConfiguration = new RedisSentinelConfiguration();
        // 集群redis
        // RedisClusterConfiguration redisStandaloneConfiguration = new RedisClusterConfiguration();
        redisStandaloneConfiguration.setHostName(redisHost);
        redisStandaloneConfiguration.setPort(redisPort);
        redisStandaloneConfiguration.setDatabase(redisDb);
        redisStandaloneConfiguration.setPassword(RedisPassword.of(redisPwd));
        long redisTimeOut = 60000;
        if (redisTimeout.indexOf("ms") != -1) {
            String millis = redisTimeout.substring(0, redisTimeout.length()-2);
            redisTimeOut = Long.valueOf(millis);
        } else {
            redisTimeOut = Long.valueOf(redisTimeout);
        }
        JedisClientConfiguration jedisClientConfiguration = JedisClientConfiguration.builder()
                .usePooling().poolConfig(poolConfig).and().readTimeout(Duration.ofMillis(redisTimeOut)).build();

        JedisConnectionFactory connectionFactory = new JedisConnectionFactory(redisStandaloneConfiguration,
                jedisClientConfiguration);
        return connectionFactory;
    }


    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory){
        return RedisCacheManager.create(redisConnectionFactory);
    }

    @Bean
    @ConditionalOnMissingBean(name = "redisTemplate")
    public RedisTemplate<Object, Object> redisTemplate(
            RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();

        //使用fastjson序列化
        FastJsonRedisSerializer fastJsonRedisSerializer = new FastJsonRedisSerializer(Object.class);
        // value值的序列化采用fastJsonRedisSerializer
        template.setValueSerializer(fastJsonRedisSerializer);
        template.setHashValueSerializer(fastJsonRedisSerializer);
        // key的序列化采用StringRedisSerializer
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    @Bean
    @ConditionalOnMissingBean(StringRedisTemplate.class)
    public StringRedisTemplate stringRedisTemplate(
            RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }


}
