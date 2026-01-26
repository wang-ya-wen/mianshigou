package com.wang.mianshigou.blackFilter;

import cn.hutool.bloomfilter.BitMapBloomFilter;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * 黑名单过滤工具类
 */
@Slf4j
public class BlackIpUtils {

    private static BitMapBloomFilter bloomFilter = new BitMapBloomFilter(100);

    // 判断 ip 是否在黑名单里
    public static boolean isBlack(String ip) {
        return bloomFilter.contains(ip);
    }

    /**
     * 重建 ip 黑名单
     *
     * @param configInfo
     */
    public static void rebuildBlackIp(String configInfo) {
        if (StrUtil.isBlank(configInfo)) {
            configInfo = "{}";
        }
        try {
            Yaml yaml = new Yaml();
            Map<String, Object> config = yaml.loadAs(configInfo, Map.class);

            // 获取配置值
            Object blackIps = config.get("blackIps");
            if (blackIps == null) {
                return;
            }

            // 处理不同类型的配置
            List<String> ipList = new ArrayList<>();
            if (blackIps instanceof List) {
                ipList = (List<String>) blackIps;
            } else if (blackIps instanceof String) {
                // 如果是字符串，按逗号分割
                String[] ips = ((String) blackIps).split(",");
                ipList = Arrays.asList(ips);
            }

            // 初始化布隆过滤器
            if (!ipList.isEmpty()) {
                bloomFilter = new BitMapBloomFilter(ipList.size());
                ipList.forEach(bloomFilter::add);
            }
        } catch (Exception e) {
            log.error("重建黑名单IP失败", e);
        }
    }

}