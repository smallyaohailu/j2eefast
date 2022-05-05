package com.j2eefast.framework.sys.entity.server;

import lombok.Data;

/**
 * Redis 参数
 * @author huanzhou
 */
@Data
public class RedisEntity {

    /**
     * 版本
     */
    private String version;

    /**
     * 端口
     */
    private String post;


    /**
     * 运行时长
     */
    private String runTime;

    /**
     * 启动脚本路径
     */
    private String runPath;

    /**
     * 配置文件
     */
    private String configPath;

    /**
     * 总内存
     */
    private String  totalMemory;

    /**
     * 	已使用
     */
    private String runMemory;

    /**
     * key 总数
     */
    private Long key;
}
