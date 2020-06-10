package com.sse.oauth2.server.config;

import com.sse.oauth2.server.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.TokenStore;

import javax.sql.DataSource;

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
     * 获取数据源
     */
    @Autowired
    private DataSource dataSource;

    /**
     *  创建jdbcClientDetailsService实例，并注入spring容器中，不要少了@Bean
     *  注意：访问修饰符不要写错了。
     * @return
     */
    @Bean
    public ClientDetailsService jdbcClientDetailsService(){
        return new JdbcClientDetailsService(dataSource);
    }

    /**
     * 令牌端点的安全配置
     * @param security
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        // 所有人可访问 /oauth/token_key 后面要获取公钥, 默认拒绝访问
        security.tokenKeyAccess("permitAll()");
        // 认证后可访问 /oauth/check_token , 默认拒绝访问
        security.checkTokenAccess("isAuthenticated()");
    }

    /**
     * 配置被允许访问此认证服务器的客户端详情信息
     * 方式1：内存方式管理
     * 方式2：数据库管理
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
       /* // 使用内存方式
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
                .redirectUris("https://www.baidu.com/")
                // 访问令牌有效时长，默认为 12 小时，单位为（秒），下面是8小时
                .accessTokenValiditySeconds(60*60*8)
                // 刷新令牌有效时长,默认是30天，单位为（秒），下面是60天
                .refreshTokenValiditySeconds(60*60*24*60)

                // 上面这是配置一个客服端，如果需要配置多个客户端，可以使用and()链接，
            .and()
                // 客户端id
                .withClient("sse-APP")
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
                .redirectUris("https://www.baidu.com/")
                // 访问令牌有效时长，默认为 12 小时，单位为（秒），下面是8小时
                .accessTokenValiditySeconds(60*60*8)
                // 刷新令牌有效时长,默认是30天，单位为（秒），下面是60天
                .refreshTokenValiditySeconds(60*60*24*60)
        ;*/
        // 使用JDBC方式管理客服端
        clients.withClientDetails(jdbcClientDetailsService());

    }

    /**
     * 授权码管理策略
     * @return
     */
    @Bean
    public AuthorizationCodeServices jdbcAuthorizationCodeServices() {
        // 注入数据源
        return new JdbcAuthorizationCodeServices(dataSource);
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
        // 授权码管理策略
        endpoints.authorizationCodeServices(jdbcAuthorizationCodeServices());
    }
}
