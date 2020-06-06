package com.sse.oauth2.server.config;

import com.sse.oauth2.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;

/**
 *  认证服务器配置
 * @author: GL
 * @program: springSecurity-example
 * @create: 2020年 06月 05日 10:52
 **/
@Configuration
// 开启 OAuth2 认证服务器功能
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    /**
     * 密码加密实例
     */
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 引入AuthenticationManager实例
     */
    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * 刷新令牌
     */
    @Autowired
    private CustomUserDetailsService customUserDetailsService ;

    /**
     * token管理方式，在TokenConfig类中已对添加到容器中了
     */
    @Autowired
    private TokenStore tokenStore;

    /**
     * 配置被允许访问此认证服务器的客户端详情信息
     * 方式1：内存方式管理
     * 方式2：数据库管理
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        // 使用内存方式
        clients.inMemory()
                // 客户端id
                .withClient("sse-pc")
                // 客户端密码，要加密,不然一直要求登录, 获取不到令牌, 而且一定不能被泄露
                .secret(passwordEncoder.encode("123456"))
                // 资源id, 如商品资源
                .resourceIds("product-server")
                // 授权类型, 可同时支持多种授权类型
                .authorizedGrantTypes("authorization_code", "password",
                        "implicit","client_credentials","refresh_token")
                // 授权范围标识，哪部分资源可访问（all是标识，不是代表所有）
                .scopes("all")
                // false 跳转到授权页面手动点击授权，true 不用手动授权，直接响应授权码，
                .autoApprove(false)
                // 客户端回调地址
                .redirectUris("https://www.baidu.com/");
    }

    /**
     * 关于认证服务器端点配置
     * @param endpoints
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        // 密码模式需要设置认证管理器
        endpoints.authenticationManager(authenticationManager);
        // 刷新令牌获取新令牌时需要
        endpoints.userDetailsService(customUserDetailsService);
        // 令牌的管理方式
        endpoints.tokenStore(tokenStore);
    }
}