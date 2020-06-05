# Spring Security简单案例

[TOC]

# 一、简介

 `Spring Security` 是基于 `Spring` 的身份认证`（Authentication）`和用户授权`（Authorization）`框架，提供了一

套 `Web` 应用安全性的完整解决方案。其中核心技术使用了 `Servlet` 过滤器、`IOC` 和 `AOP` 等

-  身份认证

身份认证指的是用户去访问系统资源时，系统要求验证用户的身份信息，用户身份合法才访问对应资源。常见的身份认证一般要求用户提供用户名和密码。系统通过校验用户名和密码来完成认证过程。

- 用户授权

当身份认证通过后，去访问系统的资源，系统会判断用户是否拥有访问该资源的权限，只允许访问有权限的系统资源，没有权限的资源将无法访问，这个过程叫用户授权。比如 会员管理模块有增删改查功能，有的用户只能进行查询，而有的用户可以进行修改、删除。一般来说，系统会为不同的用户分配不同的角色，而每个角色则对应一系列的权限。

# 二、身份认证方式

## 2.1、加入依赖和编写安全配置类

### 添加`Spring Security`依赖

```xml
<!-- spring security 启动器 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

### 编写安全配置类

- 创建 `com.sse.security.config.SpringSecurityConfig`类并继承 `org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter`方法

在类上添加注解 `@Confifiguration` 标识为配置类、 `@EnableWebSecurity` 启动 `SpringSecurity` 过滤器链功能。

- 重写一下两个方法
  - `confifigure(AuthenticationManagerBuilder auth)` 身份认证管理器
    - 认证信息提供方式（用户名、密码、当前用户的资源权限）
    - 可采用内存存储方式，也可能采用数据库方式等
  - `confifigure(HttpSecurity http)` 资源权限配置（过滤器链）
    - 拦截的哪一些资源
    - 资源所对应的角色权限
    - 定义认证方式： `httpBasic` 、 `httpForm`
    - 定制登录页面、登录请求地址、错误处理方式
    - 自定义 `spring security` 过滤器等
- 定义`com.sse.security.config.PasswordEncoderConfig`类实例化 `BCryptPasswordEncoder`。

`BCryptPasswordEncoder`类是 `PasswordEncoder`接口的一个实现类， `PasswordEncoder`接口有一下几个方法。

| NO   | 方法                                                         | 描述                           |
| ---- | ------------------------------------------------------------ | ------------------------------ |
| 1    | `String encode(CharSequence rawPassword);`                   | 用于加密明文                   |
| 2    | `boolean matches(CharSequence rawPassword, String encodedPassword);` | 输入的密码与数据库中的密码对比 |
| 3    | `default boolean upgradeEncoding(String encodedPassword) { return false; }` | 是否需要编码，一般不需要。     |

在这里创建一个配置类，将 `BCryptPasswordEncoder`实例注入容器后，需要使用的时候直接从容器中获取，这样比较方便。代码如下：

```java
@Configuration
public class PasswordEncoderConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        // 明文+随机盐值加密存储
        return new BCryptPasswordEncoder();
    }
}
```

- 明文密码加盐值

加密的最终结果分为两部分，盐值 + MD5(password+盐值), 调用 matches(..) 方法的时候，先从密文中得到盐值，用该盐值加密明文和最终密文作对比。这样可以避免有一个密码被破解, 其他相同的密码的帐户都可以破解.因为通过当前机制相同密码生成的密文都不一样。

加密过程（注册）： aaa (盐值) + 123(密码明文) > 生成密文  > 最终结果 盐值.密文：aaa.asdlkf 存入数据库校验过程（登录）： aaa (盐值, 数据库中得到) + 123(用户输入密码)> 生成密文 aaa.asdlkf，与数据库对比一致密码正确。

## 2.2、 HttpBasic 和HttpForm 认证方式

### HttpBasic认证方式

这种认证方式，会在浏览器的上方弹出登录窗口，这样不是很美观。详细配置如下：

```java
/**
 * 认证管理器：
 * 1、认证信息提供方式（用户名、密码、当前用户的资源权限）
 * 2、可采用内存存储方式，也可能采用数据库方式等
 * @param auth
 * @throws Exception
 */
@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    // 数据库存储的密码必须是加密后的，不然会报错：There is no PasswordEncoder mapped for the id "null"
    String password = passwordEncoder.encode("1234");
    log.info("加密之后存储的密码：" + password);
    // 设置用户名和角色
    auth.inMemoryAuthentication().withUser("admin")
        .password(password).authorities("ADMIN");
}

/**
 * 资源权限配置（过滤器链）:
 * 1、被拦截的资源
 * 2、资源所对应的角色权限
 * 3、定义认证方式：httpBasic 、httpForm
 * 4、定制登录页面、登录请求地址、错误处理方式
 * 5、自定义 spring security 过滤器
 * @param http
 * @throws Exception
 */
@Override
protected void configure(HttpSecurity http) throws Exception {
    // 采用 httpBasic认证方式
    http.httpBasic()
        // 表单登录方式
        .and()
        // 认证请求
        .authorizeRequests()
        .anyRequest()
        //所有访问该应用的http请求都要通过身份认证才可以访问
        .authenticated()
        ; // 注意不要少了分号
}
```

在 `sse-security-web`工程下创建 `MainController`类，认证通过就访问这里。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200603190152248.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)

启动后在浏览器访问：[http://localhost:8080/index](http://localhost:8080/index)

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200603190257868.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)

> 输入用户名：`admin`和密码：1234，就可以访问到资源。

### HttpForm 表单认证方式

将 `http.httpBasic()`替换成 `http.formLogin()`就行了。

![在这里插入图片描述](https://img-blog.csdnimg.cn/2020060319072565.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)

启动后在浏览器访问：[http://localhost:8080/index](http://localhost:8080/index)，访问路径会自动跳转到`login`路径，验证通过后又跳转到当初访问的路径。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200603191000330.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)

当前版本号：`bd91e527929e4f42797df1ab0ba0f1657734f988`”

# 三、身份认证实践

## 3.1、指定登录跳转地址

直接在资源配置类中添加 `loginPage(String loginPage) `方法指定就可以了，但是需要注意：现在拦截的是所有全部请求，自定义一个登录请求，需要将这个地址设置为不认证也可以访问。如果不这样设置，页面会提示“重定向次数过多”。因为登录的时候会访问“login"l路径，设置新的登录地址后，一直来访问新的这个地址，但是这个地址必须登录才可以访问，所以一直循环这样调用，就会出现重定向次数过多。设置如下：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200603194751224.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)

访问登录页面的时候，需要跳转到“login/page"地址，但是没有这个登录也。所以需要在创建这个路径，否则会提示`404`找不到这个路径。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200603194935252.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)

还可以放行所有静态资源，如果是那些前后端没有分离的项目，所有前端的静态资源都保存在代码中，如果不放行这些静态资源，没有登录时不能访问的。配置如下：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200603195135929.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)

## 3.2、用户名和密码的默认名称

有的时候，前端提交的用户名和密码的名称，就是根据名称获取前端的用户名和密码的这个名称。如果不喜欢使用默认的，也是可以更改。![在这里插入图片描述](https://img-blog.csdnimg.cn/20200603200048508.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)

默认的用户名变量名：`username`，密码变量名：`password`。

可以通过 `usernameParameter(String usernameParameter)`和 `passwordParameter(String passwordParameter)`方法更改。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200603200130476.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200603200412870.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)

当前版本号：`04d73e4ae03a52af9d16d087e3ae2c968965fa95`

## 3.3、将配置更改为动态配置

将登录地址、密码和用户名称这些值设置为动态配置的，在代码中去配置不是很理想。直接在`yml`或者`properties`文件中动态配置比较乐观，现在就更改为这样的配置方式。

创建 `com.sse.security.properites.SecurityProperties`配置文件映射类，在创建 `com.sse.security.properites.AuthenticationProperties`保存配置信息类。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200604180902934.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)

在配置文件中就可以直接调用配置类，获取配置信息就行了。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200604180947557.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)



## 3.4、实现成功处理类

有些程序登录会做一些非同寻常的操作，那么可以创建一个登录成功处理类。主要关注的是 `org.springframework.security.web.authentication.AuthenticationSuccessHandler`这个接口，但是我为了能够实现返回`JSON`格式数据还是重定向地址，我就实现这个接口的实现类，`SavedRequestAwareAuthenticationSuccessHandler`。

创建 `CustomAuthenticationSuccessHandler`类，并继承 `SavedRequestAwareAuthenticationSuccessHandler`类，实现 `onAuthenticationSuccess`方法，在这个方法里面就可以做一些登录成功需要处理的数据。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200604181541515.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)

在 `SpringSecurityConfig`中配置 `successHandler`并指定成功处理类就可以生效了。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200604181705631.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)

判断是返回`JSON`格式数据还是重定向地址呢？我们直接在配置文件中就可以指定了。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200604182156132.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)

## 3.5、实现失败处理类

实现效果和成功处理类差不多，只是关注的接口不一样。主要关注 `org.springframework.security.web.authentication.AuthenticationFailureHandler`接口，为了方便我就实现该接口的实现类 `SimpleUrlAuthenticationFailureHandler`，并重写 `onAuthenticationFailure`方法。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200604181918526.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)

在`SpringSecurityConfig`中配置`failureHandler`方法指定失败处理类就行了。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200604182005979.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)

全部配置文件如下：

```yaml
sse:
  security:
    authentication:
      loginPage: /login/page # 响应认证(登录)页面的URL
      loginProcessingUrl: /login/form # 登录表单提交处理的url
      usernameParameter: name # 登录表单提交的用户名的属性名
      passwordParameter: pwd  # 登录表单提交的密码的属性名
      loginType: JSON
      staticPaths: # 静态资源 "/dist/**", "/modules/**", "/plugins/**"
        - /dist/**
        - /modules/**
        - /plugins/**
```

<font color="red">**如果你设置为返回JSON格式的数据，那么在浏览器上访问的最初那个地址就不在执行。**</font>

当前版本号：`f3f1ae3d5327316cfad3b93c9c65a20627fc6608`

源码地址：[https://github.com/gl-stars/springSecurity-example.git](https://github.com/gl-stars/springSecurity-example.git)