package com.wang.mianshigou.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 100%生效的Druid配置，适配Spring Boot 2.7.2 + 上下文路径/api
 */
@Configuration // 必须加这个注解！
public class DruidConfig {

    // 配置数据源（确保Druid连接池生效）
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource druidDataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        System.out.println("===== Druid数据源配置已加载 ====="); // 加日志验证
        return dataSource;
    }

    // 注册Druid监控页面Servlet
    @Bean
    public ServletRegistrationBean<StatViewServlet> statViewServlet() {
        ServletRegistrationBean<StatViewServlet> bean = new ServletRegistrationBean<>(new StatViewServlet(), "/api/druid/*");

        // 配置登录账号密码
        Map<String, String> initParams = new HashMap<>();
        initParams.put("loginUsername", "root");
        initParams.put("loginPassword", "123");
        initParams.put("allow", ""); // 允许所有IP访问
        initParams.put("resetEnable", "false");

        bean.setInitParameters(initParams);
        System.out.println("===== Druid监控Servlet已注册：/api/druid/* ====="); // 加日志验证
        return bean;
    }

    // 注册Druid监控Filter
    @Bean
    public FilterRegistrationBean<WebStatFilter> webStatFilter() {
        FilterRegistrationBean<WebStatFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new WebStatFilter());

        // 配置过滤规则
        Map<String, String> initParams = new HashMap<>();
        initParams.put("exclusions", "*.js,*.css,/api/druid/*");

        bean.setInitParameters(initParams);
        bean.addUrlPatterns("/*");
        System.out.println("===== Druid监控Filter已注册 ====="); // 加日志验证
        return bean;
    }
}