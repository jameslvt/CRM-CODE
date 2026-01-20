package com.crm.common.utils;

import lombok.extern.slf4j.Slf4j;

/**
 * 雪花算法 ID 生成器
 * 用于生成全局唯一的分布式 ID
 *
 * 雪花算法结构（64位）：
 * 1位符号位（固定为0） + 41位时间戳 + 10位工作机器ID + 12位序列号
 *
 * @author CRM System
 * @since 1.0.0
 */
@Slf4j
public class IdGenerator {

    /**
     * 起始时间戳（2020-01-01 00:00:00）
     */
    private static final long START_TIMESTAMP = 1577808000000L;

    /**
     * 机器 ID 所占的位数
     */
    private static final long WORKER_ID_BITS = 5L;

    /**
     * 数据中心 ID 所占的位数
     */
    private static final long DATACENTER_ID_BITS = 5L;

    /**
     * 支持的最大机器 ID（结果是 31）
     */
    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);

    /**
     * 支持的最大数据中心 ID（结果是 31）
     */
    private static final long MAX_DATACENTER_ID = ~(-1L << DATACENTER_ID_BITS);

    /**
     * 序列号所占的位数
     */
    private static final long SEQUENCE_BITS = 12L;

    /**
     * 机器 ID 向左移 12 位
     */
    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;

    /**
     * 数据中心 ID 向左移 17 位（12 + 5）
     */
    private static final long DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;

    /**
     * 时间戳向左移 22 位（12 + 5 + 5）
     */
    private static final long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS;

    /**
     * 生成序列号的掩码（结果是 4095）
     */
    private static final long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);

    /**
     * 工作机器 ID（0-31）
     */
    private final long workerId;

    /**
     * 数据中心 ID（0-31）
     */
    private final long datacenterId;

    /**
     * 毫秒内序列号（0-4095）
     */
    private long sequence = 0L;

    /**
     * 上次生成 ID 的时间戳
     */
    private long lastTimestamp = -1L;

    /**
     * 单例实例
     */
    private static volatile IdGenerator instance;

    /**
     * 私有构造函数
     *
     * @param workerId     工作机器 ID
     * @param datacenterId 数据中心 ID
     */
    private IdGenerator(long workerId, long datacenterId) {
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException(
                    String.format("Worker ID 不能大于 %d 或小于 0", MAX_WORKER_ID));
        }
        if (datacenterId > MAX_DATACENTER_ID || datacenterId < 0) {
            throw new IllegalArgumentException(
                    String.format("Datacenter ID 不能大于 %d 或小于 0", MAX_DATACENTER_ID));
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
        log.info("IdGenerator 初始化完成，workerId: {}, datacenterId: {}", workerId, datacenterId);
    }

    /**
     * 获取单例实例（使用默认的 workerId 和 datacenterId）
     *
     * @return IdGenerator 实例
     */
    public static IdGenerator getInstance() {
        if (instance == null) {
            synchronized (IdGenerator.class) {
                if (instance == null) {
                    // 默认使用 workerId=1, datacenterId=1
                    instance = new IdGenerator(1L, 1L);
                }
            }
        }
        return instance;
    }

    /**
     * 获取单例实例（自定义 workerId 和 datacenterId）
     *
     * @param workerId     工作机器 ID
     * @param datacenterId 数据中心 ID
     * @return IdGenerator 实例
     */
    public static IdGenerator getInstance(long workerId, long datacenterId) {
        if (instance == null) {
            synchronized (IdGenerator.class) {
                if (instance == null) {
                    instance = new IdGenerator(workerId, datacenterId);
                }
            }
        }
        return instance;
    }

    /**
     * 生成下一个 ID（线程安全）
     *
     * @return 唯一 ID
     */
    public synchronized long nextId() {
        long timestamp = timeGen();

        // 如果当前时间小于上一次 ID 生成的时间戳，说明系统时钟回退过，抛出异常
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(
                    String.format("时钟回退，拒绝生成 ID，时间差: %d 毫秒", lastTimestamp - timestamp));
        }

        // 如果是同一毫秒内生成的，则进行毫秒内序列
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & SEQUENCE_MASK;
            // 毫秒内序列溢出
            if (sequence == 0) {
                // 阻塞到下一个毫秒，获得新的时间戳
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            // 时间戳改变，毫秒内序列重置
            sequence = 0L;
        }

        // 上次生成 ID 的时间戳
        lastTimestamp = timestamp;

        // 移位并通过或运算拼到一起组成 64 位的 ID
        return ((timestamp - START_TIMESTAMP) << TIMESTAMP_LEFT_SHIFT)
                | (datacenterId << DATACENTER_ID_SHIFT)
                | (workerId << WORKER_ID_SHIFT)
                | sequence;
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     *
     * @param lastTimestamp 上次生成 ID 的时间戳
     * @return 当前时间戳
     */
    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 返回以毫秒为单位的当前时间
     *
     * @return 当前时间（毫秒）
     */
    private long timeGen() {
        return System.currentTimeMillis();
    }

    /**
     * 静态方法：生成 ID
     *
     * @return 唯一 ID
     */
    public static long generateId() {
        return getInstance().nextId();
    }
}
