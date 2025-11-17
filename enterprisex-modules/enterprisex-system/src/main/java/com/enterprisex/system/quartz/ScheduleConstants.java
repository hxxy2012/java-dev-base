package com.enterprisex.system.quartz;

/**
 * 任务调度常量
 *
 * @author EnterpriseX
 */
public class ScheduleConstants {

    public static final String TASK_CLASS_NAME = "TASK_CLASS_NAME";

    /**
     * 执行目标key
     */
    public static final String TASK_PROPERTIES = "TASK_PROPERTIES";

    /**
     * 默认组名
     */
    public static final String DEFAULT_GROUP = "DEFAULT";

    /**
     * 任务状态
     */
    public enum Status {
        /**
         * 正常
         */
        NORMAL(1),
        /**
         * 暂停
         */
        PAUSE(0);

        private final int value;

        Status(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
