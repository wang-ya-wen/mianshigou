package com.wang.mianshigou.config;

import com.jd.platform.hotkey.client.ClientStarter;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 热key发现配置
 *

 */
@Configuration
@ConfigurationProperties(prefix = "hotkey")
@Data
public class HotKeyConfig{

    /**
     * etcdetcd服务器完整地址
     */
    private String etcdServer="http://127.0.0.1:2379";
    /**
     * 应用名称
     */
    private String appName="mianshigou";

    /**
     * 本地缓存最大数量
     */
    private int caffeineSize=10000;
    /**
     * 批量推送key的间隔时间
     */
    private long pushPeriod=1000L;


    @Bean
    public void initHotKey(){
        ClientStarter.Builder builder=new ClientStarter.Builder();
        ClientStarter starter=builder.setAppName(appName)
                .setCaffeineSize(caffeineSize)
                .setEtcdServer(etcdServer)
                .setPushPeriod(pushPeriod)
                .build();
        starter.startPipeline();
    }
    public String getEtcdServer() {
        return etcdServer;
    }

    public void setEtcdServer(String etcdServer) {
        this.etcdServer = etcdServer;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public int getCaffeineSize() {
        return caffeineSize;
    }

    public void setCaffeineSize(int caffeineSize) {
        this.caffeineSize = caffeineSize;
    }

    public long getPushPeriod() {
        return pushPeriod;
    }

    public void setPushPeriod(long pushPeriod) {
        this.pushPeriod = pushPeriod;
    }
}
