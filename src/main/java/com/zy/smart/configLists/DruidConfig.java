package com.zy.smart.configLists;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Configuration
public class DruidConfig {
    
    @Value("${spring.datasource.url:#{null}}")
    private String dbUrl;
    
    @Value("${spring.datasource.username:#{null}}")
    private String username;
    
    @Value("${spring.datasource.password:#{null}}")
    private String password;
    
    @Value("${spring.datasource.driverClassName:#{null}}")
    private String driverClassName;
    
    @Value("${spring.datasource.druid.initialSize:#{null}}")
    private Integer initialSize;
    
    @Value("${spring.datasource.druid.minIdle:#{null}}")
    private Integer minIdle;
    
    @Value("${spring.datasource.druid.maxActive:#{null}}")
    private Integer maxActive;
    
    @Value("${spring.datasource.druid.maxWait:#{null}}")
    private Integer maxWait;
    
    @Value("${spring.datasource.druid.timeBetweenEvictionRunsMillis:#{null}}")
    private Integer timeBetweenEvictionRunsMillis;
    
    @Value("${spring.datasource.druid.minEvictableIdleTimeMillis:#{null}}")
    private Integer minEvictableIdleTimeMillis;
    
    @Value("${spring.datasource.druid.validationQuery:#{null}}")
    private String validationQuery;
    
    @Value("${spring.datasource.druid.testWhileIdle:#{null}}")
    private Boolean testWhileIdle;
    
    @Value("${spring.datasource.druid.testOnBorrow:#{null}}")
    private Boolean testOnBorrow;
    
    @Value("${spring.datasource.druid.testOnReturn:#{null}}")
    private Boolean testOnReturn;
    
    @Value("${spring.datasource.druid.poolPreparedStatements:#{null}}")
    private Boolean poolPreparedStatements;
    
    @Value("${spring.datasource.druid.maxPoolPreparedStatementPerConnectionSize:#{null}}")
    private Integer maxPoolPreparedStatementPerConnectionSize;
    
    @Value("${spring.datasource.druid.filters:#{null}}")
    private String filters;
    
    @Value("${spring.datasource.druid.connectionProperties:#{null}}")
    private String connectionProperties;
    
    @Bean
    @Primary
    public DataSource dataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(dbUrl);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName(driverClassName);
        
        Optional.ofNullable(initialSize).ifPresent(s -> dataSource.setInitialSize(s));
        Optional.ofNullable(minIdle).ifPresent(s -> dataSource.setMinIdle(s));
        Optional.ofNullable(maxActive).ifPresent(s -> dataSource.setMaxActive(s));
        Optional.ofNullable(maxWait).ifPresent(s -> dataSource.setMaxWait(s));
        Optional.ofNullable(timeBetweenEvictionRunsMillis)
            .ifPresent(s -> dataSource.setTimeBetweenEvictionRunsMillis(s));
        Optional.ofNullable(minEvictableIdleTimeMillis).ifPresent(s -> dataSource.setMinEvictableIdleTimeMillis(s));
        Optional.ofNullable(validationQuery).ifPresent(s -> dataSource.setValidationQuery(s));
        Optional.ofNullable(testWhileIdle).ifPresent(s -> dataSource.setTestWhileIdle(s));
        Optional.ofNullable(testOnBorrow).ifPresent(s -> dataSource.setTestOnBorrow(s));
        Optional.ofNullable(testOnReturn).ifPresent(s -> dataSource.setTestOnReturn(s));
        Optional.ofNullable(poolPreparedStatements).ifPresent(s -> dataSource.setPoolPreparedStatements(s));
        Optional.ofNullable(maxPoolPreparedStatementPerConnectionSize)
            .ifPresent(s -> dataSource.setMaxPoolPreparedStatementPerConnectionSize(s));
        Optional.ofNullable(connectionProperties).ifPresent(s -> dataSource.setConnectionProperties(s));
        List<Filter> filters = new ArrayList<>();
        filters.add(statFilter());
        filters.add(wallFilter());
        dataSource.setProxyFilters(filters);
        
        return dataSource;
    }
    
    @Bean
    public WallFilter wallFilter() {
        WallFilter wallFilter = new WallFilter();
        // 允许执行多条SQL
        WallConfig config = new WallConfig();
        config.setMultiStatementAllow(true);
        wallFilter.setConfig(config);
        return wallFilter;
    }
    
    @Bean
    public StatFilter statFilter() {
        StatFilter statFilter = new StatFilter();
        statFilter.setLogSlowSql(true); // slowSqlMillis用来配置SQL慢的标准，执行时间超过slowSqlMillis的就是慢。
        statFilter.setMergeSql(true); // SQL合并配置
        statFilter.setSlowSqlMillis(1000); // slowSqlMillis的缺省值为3000，也就是3秒。
        return statFilter;
    }
    
    @Bean
    public ServletRegistrationBean druidServlet() {
        ServletRegistrationBean servletRegistrationBean =
            new ServletRegistrationBean(new StatViewServlet(), "/druid/*");
        // 控制台管理用户，加入下面2行 进入druid后台就需要登录
        servletRegistrationBean.addInitParameter("loginUsername", "admin");
        servletRegistrationBean.addInitParameter("loginPassword", "admin");
        return servletRegistrationBean;
    }
    
    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new WebStatFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
        filterRegistrationBean.addInitParameter("profileEnable", "true");
        return filterRegistrationBean;
    }
    
}
