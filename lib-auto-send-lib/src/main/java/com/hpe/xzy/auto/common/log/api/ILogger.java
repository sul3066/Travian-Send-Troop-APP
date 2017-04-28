package com.hpe.xzy.auto.common.log.api;

/**
 * Created by xuzhenya on 10/28/2016.
 */

public interface ILogger {
    enum LogLevel{
        TRACE(0),
        DEBUG(1),
        INFO(2),
        WARNING(3),
        ERROR(4);
        private int intvalue=2;
        private LogLevel(int value){
            intvalue=value;
        }
        public int getIntvalue(){
            return intvalue;
        }
    }
    public void setLogLevel(LogLevel level);
    public void trace(Class<?> c, String message);
    public void info(Class<?> c, String message);
    public void debug(Class<?> c, String message);
    public void warn(Class<?> c, String message);
    public void warn(Class<?> c, Throwable message);
    public void error(Class<?> c, String message);
    public void error(Class<?> c, Throwable ex);


}
