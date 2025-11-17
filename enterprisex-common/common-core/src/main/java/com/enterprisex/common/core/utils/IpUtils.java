package com.enterprisex.common.core.utils;

import lombok.extern.slf4j.Slf4j;

/**
 * IP地址工具类
 *
 * @author EnterpriseX
 */
@Slf4j
public class IpUtils {

    /**
     * 检查IP是否在CIDR范围内
     *
     * @param ip   要检查的IP地址
     * @param cidr CIDR格式的IP范围，如 192.168.1.0/24
     * @return true-在范围内 false-不在范围内
     */
    public static boolean isIpInCidr(String ip, String cidr) {
        try {
            // 解析CIDR
            String[] parts = cidr.split("/");
            if (parts.length != 2) {
                log.warn("无效的CIDR格式: {}", cidr);
                return false;
            }

            String networkIp = parts[0];
            int prefixLength = Integer.parseInt(parts[1]);

            // 验证前缀长度
            if (prefixLength < 0 || prefixLength > 32) {
                log.warn("无效的CIDR前缀长度: {}", prefixLength);
                return false;
            }

            // 将IP地址转换为long型整数
            long ipLong = ipToLong(ip);
            long networkIpLong = ipToLong(networkIp);

            // 计算子网掩码
            long mask = (prefixLength == 0) ? 0 : (0xFFFFFFFF << (32 - prefixLength));

            // 检查IP是否在网络范围内
            return (ipLong & mask) == (networkIpLong & mask);

        } catch (Exception e) {
            log.error("检查IP是否在CIDR范围内失败: ip={}, cidr={}", ip, cidr, e);
            return false;
        }
    }

    /**
     * 将IP地址字符串转换为long型整数
     *
     * @param ip IP地址
     * @return long型整数
     */
    public static long ipToLong(String ip) {
        if (ip == null || ip.isEmpty()) {
            throw new IllegalArgumentException("IP地址不能为空");
        }

        String[] parts = ip.split("\\.");
        if (parts.length != 4) {
            throw new IllegalArgumentException("无效的IP地址格式: " + ip);
        }

        long result = 0;
        for (int i = 0; i < 4; i++) {
            int value = Integer.parseInt(parts[i]);
            if (value < 0 || value > 255) {
                throw new IllegalArgumentException("无效的IP地址段: " + value);
            }
            result = (result << 8) | value;
        }

        return result;
    }

    /**
     * 将long型整数转换为IP地址字符串
     *
     * @param ipLong long型整数
     * @return IP地址字符串
     */
    public static String longToIp(long ipLong) {
        return ((ipLong >> 24) & 0xFF) + "." +
               ((ipLong >> 16) & 0xFF) + "." +
               ((ipLong >> 8) & 0xFF) + "." +
               (ipLong & 0xFF);
    }

    /**
     * 验证IP地址格式
     *
     * @param ip IP地址
     * @return true-格式正确 false-格式错误
     */
    public static boolean isValidIp(String ip) {
        if (ip == null || ip.isEmpty()) {
            return false;
        }

        String[] parts = ip.split("\\.");
        if (parts.length != 4) {
            return false;
        }

        try {
            for (String part : parts) {
                int value = Integer.parseInt(part);
                if (value < 0 || value > 255) {
                    return false;
                }
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 验证CIDR格式
     *
     * @param cidr CIDR格式字符串
     * @return true-格式正确 false-格式错误
     */
    public static boolean isValidCidr(String cidr) {
        if (cidr == null || cidr.isEmpty()) {
            return false;
        }

        String[] parts = cidr.split("/");
        if (parts.length != 2) {
            return false;
        }

        try {
            // 验证IP部分
            if (!isValidIp(parts[0])) {
                return false;
            }

            // 验证前缀长度
            int prefixLength = Integer.parseInt(parts[1]);
            return prefixLength >= 0 && prefixLength <= 32;

        } catch (NumberFormatException e) {
            return false;
        }
    }
}
