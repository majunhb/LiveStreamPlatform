package com.livestream.common.util;

import cn.hutool.core.lang.Snowflake;

/**
 * ID生成工具类
 */
public class IdUtil {

    private static final Snowflake SNOWFLAKE = new Snowflake(1, 1);

    /**
     * 生成雪花ID
     */
    public static Long snowflakeId() {
        return SNOWFLAKE.nextId();
    }

    /**
     * 生成雪花ID字符串
     */
    public static String snowflakeIdStr() {
        return String.valueOf(SNOWFLAKE.nextId());
    }

    /**
     * 生成UUID（去横线）
     */
    public static String uuid() {
        return cn.hutool.core.util.IdUtil.fastSimpleUUID();
    }

    /**
     * 生成带横线的UUID
     */
    public static String uuidWithHyphen() {
        return cn.hutool.core.util.IdUtil.simpleUUID();
    }
}
