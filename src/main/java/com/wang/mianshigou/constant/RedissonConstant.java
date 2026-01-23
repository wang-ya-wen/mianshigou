package com.wang.mianshigou.constant;

/**
 * Redisson常量
 */
public interface RedissonConstant {
    /**
     * 用户签到记录的Redis key前缀
     */
    String USER_SIGN_IN_REDIS_KEY_PREFIX = "user:signins:";

    /**
     * 获取用户签到记录的Redis key
     * @param year 年份
     * @param userId 用户id
     * @return 拼接好的Redis key
     */
    static String getUserSignInRedisKey(int year, long userId){
        return String.format(USER_SIGN_IN_REDIS_KEY_PREFIX,year,userId);
    }
}
