package com.zy.smart.configLists;

import com.zy.smart.sys.ShiroRealm;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * shiro配置
 */
@Configuration
public class ShiroConfig {
    
    /**
     * lifecycleBeanPostProcessor是负责生命周期的 , 初始化和销毁的类 (可选)
     */
    @Bean(name = "lifecycleBeanPostProcessor")
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }
    
    /**
     * 密码校验规则HashedCredentialsMatcher 这个类是为了对密码进行编码的 , 防止密码在数据库里明码保存 , 当然在登陆认证的时候 , 这个类也负责对form里输入的密码进行编码
     * 处理认证匹配处理器：如果自定义需要实现继承HashedCredentialsMatcher
     */
    @Bean(name = "hashedCredentialsMatcher")
    public HashedCredentialsMatcher hashedCredentialsMatcher() {
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        // 指定加密方式为MD5
        hashedCredentialsMatcher.setHashAlgorithmName("MD5");
        // 加密次数
        hashedCredentialsMatcher.setHashIterations(1024);
        hashedCredentialsMatcher.setStoredCredentialsHexEncoded(true);
        return hashedCredentialsMatcher;
    }
    
    @Bean(name = "shiroRealm")
    @DependsOn(value = "lifecycleBeanPostProcessor")
    public ShiroRealm shiroRealm(@Qualifier(value = "hashedCredentialsMatcher") HashedCredentialsMatcher matcher) {
        ShiroRealm shiroRealm = new ShiroRealm();
        shiroRealm.setCredentialsMatcher(matcher);
        return shiroRealm;
    }
    
    @Bean(name = "ehCacheManager")
    @DependsOn(value = "lifecycleBeanPostProcessor")
    public EhCacheManager ehCacheManager() {
        EhCacheManager ehCacheManager = new EhCacheManager();
        return ehCacheManager;
    }
    
    @Bean(name = "securityManager")
    public DefaultWebSecurityManager securityManager(@Qualifier(value = "shiroRealm") ShiroRealm realm,
        @Qualifier(value = "ehCacheManager") EhCacheManager ehCacheManager) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(realm);
        // 用户授权/认证信息Cache, 采用EhCache 缓存
        securityManager.setCacheManager(ehCacheManager);
        return securityManager;
    }
    
    @Bean(name = "shiroFilter")
    public ShiroFilterFactoryBean shiroFilterFactoryBean(
        @Qualifier(value = "securityManager") DefaultWebSecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        // 设置securityManager
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        // 设置登录页面
        // 可以写路由也可以写JSP页面的访问路径
        shiroFilterFactoryBean.setLoginUrl("/login");
        // 设置登录成功跳转的页面
        shiroFilterFactoryBean.setSuccessUrl("/welcome");
        // 设置未授权跳转的页面
        shiroFilterFactoryBean.setUnauthorizedUrl("/unauthorized");
        // 定义过滤器
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        filterChainDefinitionMap.put("/", "anon");
        filterChainDefinitionMap.put("/logout", "logout");
        filterChainDefinitionMap.put("/welcome", "authc");
        filterChainDefinitionMap.put("/login", "anon");
        filterChainDefinitionMap.put("/drawImage", "anon");
        filterChainDefinitionMap.put("/loginUser", "anon");
        filterChainDefinitionMap.put("/druid/**", "anon");
        filterChainDefinitionMap.put("/static/**", "anon");
        filterChainDefinitionMap.put("/**", "authc");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }
    
    /**
     * Spring的一个bean , 由Advisor决定对哪些类的方法进行AOP代理 .
     * 
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator proxyCreator = new DefaultAdvisorAutoProxyCreator();
        proxyCreator.setProxyTargetClass(true);
        return proxyCreator;
    }
    
    /**
     * 配置shiro跟spring的关联
     * 
     * @param securityManager
     * @return
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(
        @Qualifier(value = "securityManager") DefaultWebSecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }
    
}
