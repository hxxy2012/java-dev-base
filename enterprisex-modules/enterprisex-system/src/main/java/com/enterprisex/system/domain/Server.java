package com.enterprisex.system.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Properties;

/**
 * 服务器相关信息
 *
 * @author EnterpriseX
 */
@Data
public class Server implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * CPU相关信息
     */
    private Cpu cpu = new Cpu();

    /**
     * 內存相关信息
     */
    private Mem mem = new Mem();

    /**
     * JVM相关信息
     */
    private Jvm jvm = new Jvm();

    /**
     * 服务器信息
     */
    private Sys sys = new Sys();

    /**
     * 磁盘相关信息
     */
    private java.util.List<SysFile> sysFiles = new java.util.ArrayList<>();

    /**
     * CPU相关信息
     */
    @Data
    public static class Cpu {
        /**
         * 核心数
         */
        private int cpuNum;

        /**
         * CPU总的使用率
         */
        private double total;

        /**
         * CPU系统使用率
         */
        private double sys;

        /**
         * CPU用户使用率
         */
        private double used;

        /**
         * CPU当前等待率
         */
        private double wait;

        /**
         * CPU当前空闲率
         */
        private double free;
    }

    /**
     * 內存相关信息
     */
    @Data
    public static class Mem {
        /**
         * 内存总量
         */
        private double total;

        /**
         * 已用内存
         */
        private double used;

        /**
         * 剩余内存
         */
        private double free;

        /**
         * 使用率
         */
        private double usage;
    }

    /**
     * JVM相关信息
     */
    @Data
    public static class Jvm {
        /**
         * 当前JVM占用的内存总数(M)
         */
        private double total;

        /**
         * JVM最大可用内存总数(M)
         */
        private double max;

        /**
         * JVM空闲内存(M)
         */
        private double free;

        /**
         * JVM使用率
         */
        private double usage;

        /**
         * JDK版本
         */
        private String version;

        /**
         * JDK路径
         */
        private String home;

        /**
         * JVM运行时间
         */
        private String runTime;

        /**
         * JVM启动时间
         */
        private String startTime;

        /**
         * 运行参数
         */
        private String inputArgs;
    }

    /**
     * 系统相关信息
     */
    @Data
    public static class Sys {
        /**
         * 服务器名称
         */
        private String computerName;

        /**
         * 服务器IP
         */
        private String computerIp;

        /**
         * 项目路径
         */
        private String userDir;

        /**
         * 操作系统
         */
        private String osName;

        /**
         * 系统架构
         */
        private String osArch;
    }

    /**
     * 系统文件相关信息
     */
    @Data
    public static class SysFile {
        /**
         * 盘符路径
         */
        private String dirName;

        /**
         * 盘符类型
         */
        private String sysTypeName;

        /**
         * 文件类型
         */
        private String typeName;

        /**
         * 总大小
         */
        private String total;

        /**
         * 剩余大小
         */
        private String free;

        /**
         * 已经使用量
         */
        private String used;

        /**
         * 资源的使用率
         */
        private double usage;
    }

    public void copyTo() throws Exception {
        Properties props = System.getProperties();
        sys.setComputerName(props.getProperty("user.name"));
        sys.setComputerIp(java.net.InetAddress.getLocalHost().getHostAddress());
        sys.setOsName(props.getProperty("os.name"));
        sys.setOsArch(props.getProperty("os.arch"));
        sys.setUserDir(props.getProperty("user.dir"));

        setCpuInfo();
        setMemInfo();
        setJvmInfo();
        setSysFiles();
    }

    /**
     * 设置CPU信息
     */
    private void setCpuInfo() {
        cpu.setCpuNum(Runtime.getRuntime().availableProcessors());
    }

    /**
     * 设置内存信息
     */
    private void setMemInfo() {
        long totalMemory = Runtime.getRuntime().totalMemory();
        long freeMemory = Runtime.getRuntime().freeMemory();
        mem.setTotal(totalMemory / 1024.0 / 1024.0);
        mem.setFree(freeMemory / 1024.0 / 1024.0);
        mem.setUsed(mem.getTotal() - mem.getFree());
        mem.setUsage(mem.getUsed() / mem.getTotal() * 100);
    }

    /**
     * 设置Java虚拟机信息
     */
    private void setJvmInfo() throws Exception {
        Properties props = System.getProperties();
        jvm.setTotal(Runtime.getRuntime().totalMemory() / 1024.0 / 1024.0);
        jvm.setMax(Runtime.getRuntime().maxMemory() / 1024.0 / 1024.0);
        jvm.setFree(Runtime.getRuntime().freeMemory() / 1024.0 / 1024.0);
        jvm.setUsage((jvm.getTotal() - jvm.getFree()) / jvm.getTotal() * 100);
        jvm.setVersion(props.getProperty("java.version"));
        jvm.setHome(props.getProperty("java.home"));

        long time = System.currentTimeMillis() - java.lang.management.ManagementFactory.getRuntimeMXBean().getStartTime();
        jvm.setRunTime(formatTime(time));
        jvm.setStartTime(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(new java.util.Date(java.lang.management.ManagementFactory.getRuntimeMXBean().getStartTime())));
        jvm.setInputArgs(java.lang.management.ManagementFactory.getRuntimeMXBean().getInputArguments().toString());
    }

    /**
     * 设置磁盘信息
     */
    private void setSysFiles() {
        java.io.File[] files = java.io.File.listRoots();
        for (java.io.File file : files) {
            SysFile sysFile = new SysFile();
            sysFile.setDirName(file.getPath());
            sysFile.setSysTypeName("本地磁盘");
            sysFile.setTypeName("本地磁盘");
            sysFile.setTotal(convertFileSize(file.getTotalSpace()));
            sysFile.setFree(convertFileSize(file.getFreeSpace()));
            sysFile.setUsed(convertFileSize(file.getTotalSpace() - file.getFreeSpace()));
            sysFile.setUsage((file.getTotalSpace() - file.getFreeSpace()) * 100.0 / file.getTotalSpace());
            sysFiles.add(sysFile);
        }
    }

    /**
     * 字节转换
     */
    private String convertFileSize(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;
        if (size >= gb) {
            return String.format("%.1f GB", (float) size / gb);
        } else if (size >= mb) {
            float f = (float) size / mb;
            return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
        } else if (size >= kb) {
            float f = (float) size / kb;
            return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
        } else {
            return String.format("%d B", size);
        }
    }

    /**
     * 格式化时间
     */
    private String formatTime(long time) {
        String str;
        long day = time / (24 * 60 * 60 * 1000);
        time = time % (24 * 60 * 60 * 1000);
        long hour = time / (60 * 60 * 1000);
        time = time % (60 * 60 * 1000);
        long minute = time / (60 * 1000);
        if (day > 0) {
            str = day + "天" + hour + "小时" + minute + "分钟";
        } else if (hour > 0) {
            str = hour + "小时" + minute + "分钟";
        } else {
            str = minute + "分钟";
        }
        return str;
    }
}
