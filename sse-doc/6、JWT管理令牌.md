# JWT管理令牌

官网： [https://jwt.io](https://jwt.io) 

# 一、简介

## 1.1、简单介绍

`JSON Web Token（JWT）`是一个开放的行业标准（RFC 7519），它定义了一种紧凑且独立的方式，用于在各方之间作为JSON对象安全地传输信息。此信息可以通过数字签名进行验证和信任。JWT可以使用秘密（使用**HMAC**算法）或使用**RSA**或**ECDSA**的公钥/私钥对进行**签名** ，防止被篡改。

### **JWT** 的构成

JWT 有三部分构成：头部、有效载荷、签名。

> 例如：aaaaa.bbbbbb.cccccccc

- 头部：

包含令牌的类型（JWT） 与加密的签名算法（（如 SHA256 或 ES256） ，Base64编码后加入第一部分。

- 有效载荷：

通俗一点讲就是token中需要携带的信息都将存于此部分，比如：用户id、权限标识等信息。

> 注：该部分信息任何人都可以读出来，所以添加的信息需要加密才会保证信息的安全性

- 签名：

用于防止 JWT 内容被篡改, 会将头部和有效载荷分别进行 Base64编码，编码后用 . 连接组成新的字符串，然后再使用头部声明的签名算法进行签名。在具有秘钥的情况下，可以验证JWT的准确性，是否被篡改。



### JWT优缺点

- JWT 的优点
  - JWT 基于 json，非常方便解析。
  - 可以在令牌中自定义丰富的内容，易扩展。
  - 通过非对称加密算法及数字签名技术，JWT 防止篡改，安全性高。
  - 资源服务器使用 JWT 可以不依赖认证服务器，即可完成授权。

- JWT 的缺点：
  -  JWT令牌较长，占存储空间比较大，但是用户信息是有限的，所以在可接受范围。

## 1.2、解决的问题

当认证服务器和资源服务器不是在同一工程时, 要使用 `ResourceServerTokenServices`  去远程请求认证服务器来校验，令牌的合法性，如果用户访问量较大时将会影响系统的性能。

生成令牌采用 JWT 格式就可以解决上面的问题。因为当用户认证后获取到一个JWT令牌，而这个 JWT 令牌包含了用户基本信息，客户端只需要携带JWT访问资源服务器，资源服务器会通过事先约定好的算法进行解析出来，然后直接对 JWT 令牌校验，不需要每次远程请求认证服务器完成授权。



# 二、创建资源服务器

创建一个`sse-cloud-oauth2-product`资源服务，资源服务器实际上就是对系统功能的增删改查，比如：商品管理、订单管理、积分管理、会员管理等资源，而在微服务架构中，而这每个资源实际上就是每一个微服务。当用户请求某个微服务资源时，首先通过认证服务器进行认证与授权，通过后再才可访问到对应资源。

## 2.1、创建资源模块

创建 `sse-cloud-oauth2-product`模块为资源模块，并在`pom.xml`引入依赖。

```xml
<!--spring mvc相关的-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<!-- Spring Security、OAuth2 和JWT等 -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-oauth2</artifactId>
</dependency>
```

## 2.2、创建启动类

```java
package com.sse.oauth2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @version : 1.0.0
 * @author: GL
 * @create: 2020年 06月 10日 21:56
 **/
@SpringBootApplication
public class ProductResourceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductResourceApplication.class,args);
    }
}
```

## 2.3、创建前端控制器

```java
package com.sse.oauth2.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 资源服务器相关配置类
 * @version : 1.0.0
 * @author: GL
 * @create: 2020年 06月 10日 21:58
 **/
@RestController
@RequestMapping("/product")
public class ProductController {

    /**
     * 访问访问权限设置，必须有product权限的才可以访问
     * @PreAuthorize("hasAuthority('product')")
     * @return
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('sys:user:list')")
    public List<String> list() {
        List<String> list = new ArrayList<>();
        list.add("权限框架学习");
        list.add("高级java学习");
        list.add("数据中台学习");
        list.add("领域驱动设计思想");
        return list;
    }
}
```

## 2.4、配置资源服务器

创建资源服务器`ResourceServerConfig`并继承`ResourceServerConfigurerAdapter`。在类上面写上`@Configuration`注解标识为配置类，`@EnableResourceServer` 标识为资源服务器，请求服务中的资源，就要带着token过来，找不到token或token是无效访问不了资源，`	@EnableGlobalMethodSecurity(prePostEnabled = true)`开启方法级别权限控制。这里先使用方法级别控制权限，但是后期建议使用`URL`级别控制权限。

完整代码如下：

```java
package com.sse.oauth2.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;

/**
 * 资源服务器相关配置类
 * @version : 1.0.0
 * @author: GL
 * @create: 2020年 06月 10日 21:57
 **/
@Configuration
@EnableResourceServer // 标识为资源服务器，请求服务中的资源，就要带着token过来，找不到token或token是无效访问不了资源
@EnableGlobalMethodSecurity(prePostEnabled = true) // 开启方法级别权限控制
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    /**
     * 配置当前资源服务器的ID
     */
    public static final String RESOURCE_ID = "product_api";

    /**
     * 配置当前资源服务器的ID
     * @param resources
     * @throws Exception
     */
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        // 当前资源服务器的资源id，认证服务会认证客户端有没有访问这个资源id的权限，有则可以访问当前服务
        resources.resourceId(RESOURCE_ID)
                // 实现令牌服务, ResourceServerTokenServices实例
                .tokenServices(tokenService())
        ;
    }

    /**
     * 配置资源服务器如何验证token有效性
     * 1. DefaultTokenServices
     * 如果认证服务器和资源服务器同一服务时,则直接采用此默认服务验证即可
     * 2. RemoteTokenServices (当前采用这个)
     * 当认证服务器和资源服务器不是同一服务时, 要使用此服务去远程认证服务器验证
     * @return
     */
    public ResourceServerTokenServices tokenService(){
        // 远程认证服务器进行校验 token 是否有效
        RemoteTokenServices service = new RemoteTokenServices();
        // 请求认证服务器校验的地址，默认情况 这个地址在认证服务器它是拒绝访问，要设置为认证通过可访问
        service.setCheckTokenEndpointUrl("http://localhost:8090/auth/oauth/check_token");
        service.setClientId("sse-pc");
        service.setClientSecret("123456");
        return service;
    }
}
```

## 2.5、测试

启动`sse-cloud-oauth2-auth-server`服务器和 `sse-cloud-oauth2-product`服务器，因为`token`校验是发送 `http://localhost:8090/auth/oauth/check_token`这个请求去校验的。<font color="blue">核实下是否已经配置了放行校验令牌端点  `/oauth/check_token`，之前配置过了，这里就不在讲解。</font>

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200613144248186.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)

- 获取令牌

我使用密码模式获取，为了简单呗。这里就大概提一下，详细步骤看“SpringSecurityOAuth2认证”这篇文章。

```http
http://localhost:8090/auth/oauth/token
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/2020061314470432.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)

校验`token`

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200613144917149.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)

- 测试

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200613145223526.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200613145253427.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)

如果访问不到，注意这两种情况。

1、获取token的客服端是否具有访问资源服务器的范围。

2、当前用户是否有访问该资源的权限。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200613145408553.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)

![在这里插入图片描述](https://img-blog.csdnimg.cn/2020061314554553.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)



## 2.6、使用URL级别权限

> 下面的代码和上面版本的代码添加一些，是因为我将jwt对称加密和非对称加密都写完了，最后我又来添加这个环境的。但是jwt非对称加密这种方式有些时候可以，有些时候回加载不到私钥，后期需要做一下更改，这里我就同意使用jwt对称加密了。

将前端控制器上的权限注解注释了，在资源服务器配置`ResourceServerConfig`中添加 `configure(HttpSecurity http)`方法配置。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200614163938355.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200614164027451.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)

## 2.7、测试

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200614164142951.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)

# 三、认证服务器实现JWT对称加密

生成`JWT`令牌需要签名，签名就使用对称加密来进行签名。对称加密就是加密和解密的秘钥都是同一个，也称为单秘钥加密。

## 3.1、指定jwt管理令牌

在`Token`管理工具`TokenConfig`中指定`JWT`管理令牌，并将访问令牌转换器注入容器中。

![在这里插入图片描述](https://img-blog.csdnimg.cn/2020061320245155.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)

## 3.2、认证服务器中指定jwt转换器

在认证服务器`AuthorizationServerConfig`配置中，端点配置 `configure(AuthorizationServerEndpointsConfigurer endpoints)`里面指定`JWT`转换器。<font color="red">`jwt`转换器一定要注入容器，否则这里是获取不到实例的。我在jwt管理令牌中注入了，不要描述。</font>

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200613202627890.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)

## 3.3、测试

获取`token`

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200613203009183.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200613203206762.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)

当前版本号：`2b2919641ae5caf93c08f27912a525d78fefa0f3`

# 四、资源服务器jwt对称加密

## 4.1、创建token管理工具

将令牌转换器`JwtAccessTokenConverter`和指定`token`管理方式`TokenStore`注入到容器中。

在 `sse-cloud-oauth2-product`资源服务器中创建 `com.sse.oauth2.config.TokenConfig`类，代码如下：

```java
package com.sse.oauth2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

/**
 * Token管理工具
 * @author: GL
 * @program: springSecurity-example
 * @create: 2020年 06月 06日 15:01
 **/
@Configuration
public class TokenConfig {

    /**
     * 访问令牌的转换器
     * @return
     */
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        // 对称加密进行签名令牌，资源服务器也要采用此密钥来进行解密,来校验令牌合法性
        converter.setSigningKey("abcdefg");
        return converter;
    }

    /**
     * 指定token管理方式
     * @return
     */
    @Bean
    public TokenStore tokenStore() {
        // 指定jwt管理令牌
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

}
```

## 4.2、更改资源服务器相关配置

更改资源服务器配置`ResourceServerConfig`，`tokenService()`配置文件就不需要了，因为`token`里面就带了相关数据了。但是必须在 `token`管理工具`TokenConfig`里面设置`token`管理方式和`token`转换器，并且注入容器中。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200613205351968.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200613205729485.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)

# 五、认证和资源服务器非对称加密

生成`JWT`的`RSA`非对称加密秘钥参考：[https://blog.csdn.net/qq_41853447/article/details/105748272](https://blog.csdn.net/qq_41853447/article/details/105748272)

## 4.1、认证服务器实现非对称加密

将生成的私钥放在认证服务器`sse-cloud-oauth2-auth-server`的`resources`目录下，并在token管理工具中配置令牌转换器。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200614132332188.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)



## 4.2、资源服务器实现非对称加密

将公钥放在资源服务器 `sse-cloud-oauth2-product`的`resources`目录下，在`token`管理工具`TokenConfig`中设置`token`转换器。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200614132638595.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)



## 4.3、测试

启动认证服务器获取token，然后在启动资源服务器使用刚获取到的token在访问资源。

![在这里插入图片描述](https://img-blog.csdnimg.cn/2020061413283611.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200614132908811.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)

当前版本号：`6916af759e75af85e2349827697141493b3c7da2`

