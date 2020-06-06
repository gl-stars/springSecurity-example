# SpringSecurityOAuth2认证

[TOC]

<font color="blue" size=4  face="verdana" >源码地址：</font>[https://github.com/gl-stars/springSecurity-example.git](https://github.com/gl-stars/springSecurity-example.git)

# 一、OAuth2 协议四种授权方式

## 1.1、OAuth2 简单介绍

官网：[https://oauth.net/2/](https://oauth.net/2/)

中文说明文档：[https://github.com/jeansfish/RFC6749.zh-cn/blob/master/SUMMARY.md](https://github.com/jeansfish/RFC6749.zh-cn/blob/master/SUMMARY.md)

OAuth 2.0 是目前最流行的授权机制，第三方应用授权登录：在APP或者网页接入一些第三方应用时，时常会需要用户登录另一个合作平台，比如QQ，微博，微信的授权登录,第三方应用通过oauth2方式获取用户信息。OAuth 协议为用户资源的授权提供了一个安全的、开放而又简易的**规范标准**。很多大公司如 阿里、腾讯、 Google，Yahoo，Microsoft等都提供了 OAuth 认证服务。

OAuth 协议1.0版本过于复杂，目前发展到2.0版本，2.0版本已得到广泛应用。

### 认证流程

`OAuth`的思路是在”客户端”与”服务提供商”之间，设置了一个授权层`（authorization layer）`。”客户端”不能直接登录”服务提供商”，只能登录授权层，以此将用户与客户端区分开来。”客户端”登录授权层所用的令牌（`token`），与用户的密码不同。用户可以在登录的时候，指定授权层令牌的权限范围和有效期。”客户端”登录授权层以后，”服务提供商”根据令牌的权限范围和有效期，向”客户端”开放用户储存的资料。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200605185430745.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)

### OAuth2五大角色

- 资源所有者（Resource Owner）

 通常为 "用户"（user），如昵称、头像等这些资源的拥有者（用户只是将这些资源放到了服务提供商的资源服务器中）。

- 第三方应用（Third-party application）

又称为客户端（Client），比如 梦学谷官网想要使用微信的资源（昵称、头像等），梦学谷官网对于QQ、微信等系统来说是第三者，我们称梦学谷官网为第三方应用。

- 认证服务器（Authorization server）

 专门用来对资源所有者的身份进行认证、对要访问的资源进行授权、产生令牌的服务器。想访问资源，需要通过认证服务器由资源所有者授权后才可访问。

- 资源服务器（Resource server）

 存储用户的资源（昵称、头像等）、验证令牌有效性。比如: 微信的资源服务器存储了微信的用户信息，淘宝的资源服务器存储了淘宝的用户信息等。注意：认证服务器 和资源服务器 虽然是两个解决，但其实他们可以是同一台服务器、同一个应用。

- 服务提供商（Service Provider）

如 QQ、微信等 （包含认证和资源服务器）。

绘图工具：[https://c.runoob.com/more/shapefly-diagram/#](https://c.runoob.com/more/shapefly-diagram/#)

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200605191754385.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)

## 1.1、授权方式

- 授权码模式`(Authorization Code)`

功能最完整，流程最严密的授权模式。它的特点就是通过客户端的后台服务器，与"服务提供商"的认证服务器进行互动。国内各大服务提供商（微信、`QQ`、微博、淘宝 、百度）都采用此模式进行授权。可以确定是用真正同意授权；而且令牌是认证服务器发放给第三方应用的服务器，而不是浏览器上。

- 简化模式(`Implicit`)

令牌是发放给浏览器的，oauth客户端运行在浏览器中 ，通过JS脚本去申请令牌。而不是发放给第三方应用的服务器。

- 密码模式`(Resource Owner Password Credentials)`

将用户名和密码传过去，直接获取 `access_token` 。用户同意授权动作是在第三方应用上完成 ，而不是在认证服务器上。第三方应用申请令牌时，直接带着用户名密码去向认证服务器申请令牌。这种方式认证服务器无法断定用户是否真的授权了，用户名密码可能是第三方用盗取来的。

- 客户端证书模式`(Client credentials)`

使用比较少，当一个第三应用自己本身需要获取资源（而不是以用户的名
义），而不是获取用户的资源时，客户端模式十分有用。

## 1.2、授权码模式流程

具体步骤如下：

> （A）用户访问客户端，后者将前者导向认证服务器。
>
> （B）用户选择是否给予客户端授权。
>
> （C）假设用户给予授权，认证服务器将用户导向客户端事先指定的"重定向URI"（redirection URI），同时附上一个授权码。
>
> （D）客户端收到授权码，附上早先的"重定向URI"，向认证服务器申请令牌。这一步是在客户端的后台的服务器上完成的，对用户不可见。
>
> （E）认证服务器核对了授权码和重定向URI，确认无误后，向客户端发送访问令牌（access token）和更新令牌（refresh token）。

## 1.3、密码模式流程

![这是一张图片](https://img-blog.csdnimg.cn/20200510145738411.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0xlb25fSmluaGFpX1N1bg==,size_16,color_FFFFFF,t_70)

- 用户向客户端直接提供认证服务器平台的用户名和密码。
- 客户端将用户名和密码发给认证服务器，向后者请求令牌。
-  认证服务器确认无误后，向客户端提供访问令牌。

## 1.4、客户端模式流程

- 客户端向认证服务器进行身份认证，并要求一个访问令牌。
- 认证服务器确认无误后，向客户端提供访问令牌。

客户端模式`（Client Credentials Grant）`指客户端以自己的名义，而不是以用户的名义，向"服务提供商"进行认证。在这种模式中，用户直接向客户端注册，客户端以自己的名义要求"服务提供商"提供服务，其实不存在授权问题。

它的步骤如下：

（A）客户端向认证服务器进行身份认证，并要求一个访问令牌。

（B）认证服务器确认无误后，向客户端提供访问令牌。

客户端发出的`HTTP`请求，包含以下参数：

`granttype`：表示授权类型，此处的值固定为"`clientcredentials`"，必选项。

`scope`：表示权限范围，可选项。

# 二、授权码认证

## 2.1、创建认证服务器

- 配置方式说明
  - 内存方式
  - 数据库管理方式
- 令牌管理
  - 令牌管理策略（`JDBC`、`Redis`、`JWT`）
  - 令牌生成策略
  - 令牌端点
  - 令牌端点的安全配置

- 配置认证服务器（授权码模式）

配置允许访问该认证服务器的客户端资源，没有在这里配置的客户端是不能访问的。如果在这里配置后，访问是必须带上这里配置的客户端名称和密码。

- 创建认证服务器配置类`AuthorizationServerConfig`

创建 `AuthorizationServerConfig`类并继承 `AuthorizationServerConfigurerAdapter`类，实现 `public void configure(ClientDetailsServiceConfigurer clients) `方法。在类上添加 `@Configuration`注解标识是配置类，`@EnableAuthorizationServer`注解开启`OAuth2`认证服务器功能。

- 内存方式配置说明

`withClient`（必须）：允许访问此认证服务器的客户端id , 如：PC、APP、小程序各不同的的客户端id。

`secret`（必须）：客户端密码，要加密存储，不然获取不到令牌一直要求登录,, 而且一定不能被泄露。

`resourceIds`（非必须）：资源服务器id，就相当于微服务id，可以使用逗号隔开，配置多个。如果不配置，所有的资源服务器（每个微服务）都可以访问。

`authorizedGrantTypes`: 授权类型, 可同时支持多种授权类型：

> 可配置："authorization_code", "password", "implicit","client_credentials","refresh_token"。这些值是固定的，可以配置多个。
>
> authorization_code：授权码
>
> password：密码模式
>
> implicit：简化模式
>
> client_credentials：客户端模式
>
> refresh_token：刷新令牌

`scopes`（非必须）：授权范围标识，如指定微服务名称，则只能访问指定的微服务。

`autoApprove`（非必须）：false 跳转到授权页面手动点击授权，true 不用手动授权，直接响应授权码。

`redirectUris`（非必须） 当获取授权码后，认证服务器回调地址，并且带着一个授权码 `code` 响应回来。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200605200412972.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)

## 2.2、创建安全配置类

创建安全配置类 `SpringSecurityConfig`并继承 `WebSecurityConfigurerAdapter`类，实现 `configure(AuthenticationManagerBuilder auth)`方法。在该类上添加 `@EnableWebSecurity`注解，开启`SpringSecurity`过滤连`file`，但是这个注解已经包含 `@Configuration`注解了，所以这个类上就不用再添加这个注解。

![在这里插入图片描述](https://img-blog.csdnimg.cn/2020060520050138.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)

## 2.3、令牌访问端点

> `Spring Security` 对 `OAuth2` 默认提供了可直接访问端点，就是在访问的`URL`地址。
> `/oauth/authorize` ：申请授权码 `code`,  涉及的类 `AuthorizationEndpoint`
> `/oauth/token` ：获取令牌 `token`,  涉及的类 `TokenEndpoint`
> `/oauth/check_token` ：用于资源服务器请求端点来检查令牌是否有效,  涉及的类 `CheckTokenEndpoint`
> `/oauth/confirm_access` ：用户确认授权提交,  涉及的类 `WhitelabelApprovalEndpoint`
> `/oauth/error` ：授权服务错误信息,  涉及的类   `WhitelabelErrorEndpoint`
> `/oauth/token_key` ：提供公有密匙的端点，使用 JWT 令牌时会使用 , 涉及的类 `TokenKeyEndpoint`

## 2.4、获取授权码

直接访问令牌对应的访问端点`/oauth/authorize`，根据用户名和密码登录成功后就可以获取到授权码。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200605214135286.png)

注意本地的端口为`8090`，服务器名称为`auth`，所以访问的时候需要在最前面添加上这个服务器名称才能访问到。访问地址为：

```http
http://localhost:8090/auth/oauth/authorize?client_id=sse-pc&response_type=code
```

> 提交方式：`post`
>
> `client_id` 客户端`id`
>
> `response_type`：相应的类型是`code`授权码

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200605214935968.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)

当在浏览器访问这个地址时，自动跳转到登录页面，输入安全配置类 `SpringSecurityConfig`中配置的用户名和密码。点击登录后，如果配置的是自动授权，会自动跳转到我们配置的客户端回调地址，路径最后带有`code`参数，这个参数值就是授权码了，如果不是自动授权，那么会弹出一个授权页面授权才能获取授权码。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200605215643896.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200605215225683.png)

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200605214750352.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200605215719341.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)

## 2.5、根据授权码获取token

打开`postman`，使用`post`提交方式，访问地址为：[http://localhost:8090/auth/oauth/token](http://localhost:8090/auth/oauth/token)。这个访问地址是令牌访问端点配置好了，`auth`是服务器的名称，在`yml`文件中配置的，上面也说明了。

配置将“客户端id”和“客户端密码”加密了，加密步骤如下。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200605220347881.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)

![在这里插入图片描述](https://img-blog.csdnimg.cn/202006052204420.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200605220720187.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)

点击发送后，会返回令牌的相关信息，返回结果如下。

```json
{
    "access_token": "4be57993-2eb2-420a-bdc3-ec108601324d",
    "token_type": "bearer",
    "refresh_token": "4e84f086-b916-4c0f-83c0-8ee6eb36cb24",
    "expires_in": 43199,
    "scope": "all"
}
```

> `access_token`：令牌
>
> `token_type`：令牌类型，返回都是这个值
>
> `refresh_token`：刷新令牌
>
> `expires_in`：令牌的时差，一般是12个小时
>
> `scope`：访问范围，在 `.scopes("all")`这里配置的。

每一个授权码只能申请一次令牌，不管申请失败还是成功，都只能申请一次。但是同一个用户不同的授权码获取到的令牌都是一样的。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200605221051481.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)

出现这样的局面，说明授权码回娘家休息去了，你要重新发送 `http://localhost:8090/auth/oauth/authorize?client_id=sse-pc&response_type=code`这个地址重新获取授权码，不要一直在重定向回来的那个地址一直刷新，结果发现授权码一直都是那样，因为你并没有重新获取授权码，这是一种很愚蠢的做法。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200605221348671.png)

当前版本号：`e8656486abd96ec334eb373389ef0d029d47218a`

# 三、密码授权模式(password)

密码模式`（Resource Owner Password Credentials Grant`）中 ，用户向客户端提供自己在服务提供商（认证服务器）上的用户名和密码，然后客户端通过用户提供的用户名和密码向服务提供商（认证服务器）获取令牌。如果用户名和密码遗漏，服务提供商（认证服务器）无法判断客户端提交的用户和密码是否盗取来的，那意味着令牌就可随时获取，数据被丢失。适用于产品都是企业内部的，用户名密码共享不要紧。如果是第三方这种不太适合，也适用手机APP提交用户名密码。

## 3.1、配置密码模式

在安全配置类 `SpringSecurityConfig` 重写 `authenticationManagerBean()`方法，将 `AuthenticationManager`到容器中。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200605224405613.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)

在认证服务器中`AuthorizationServerConfig`覆写 `configure(AuthorizationServerEndpointsConfigurer endpoints)`方法，将`AuthenticationManager`认证管理器注入。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200605224959774.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)

授权类型就不用管了，这里已经设置好了，可以使用密码模式的。

```java
  // 授权类型, 可同时支持多种授权类型
                .authorizedGrantTypes("authorization_code", "password",
                        "implicit","client_credentials","refresh_token")
```

## 3.2、获取token

获取的令牌端点也是一样的，都是`/oauth/token`。访问地址如下：

```http
http://localhost:8090/auth/oauth/token
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200605225628358.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200605225657110.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200605225829524.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)

<font  color="red">**注意要将grant_type更改为password**</font>

当前版本号：`7583d883ffd76f0cc95797498c854f0a1538f0f7`

# 四、简化和客服端授权模式

## 4.1、简化模式

不通过第三方应用程序的服务器，直接在浏览器中向认证服务器申请令牌 ，不需要先获取授权码。直接可以一次请求就可得到令牌，在 `redirect_uri` 指定的回调地址中传递令牌（ `access_token` ）。该模式适合直接运行在浏览器上的应用，不用后端支持（例如 `Javascript` 应用） 。

注意：只要客户端id即可 ，客户端密码都不需要。

### 配置客户端授权类型

实现简化模式需要在认证服务器 `AuthorizationServerConfig`的`authorizedGrantTypes`中配置授权类型为`implicit`，简化模式才可以生效。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200606101300103.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)

### 获取`token`

获取`token`的令牌端点`/oauth/authorize` ，访问地址如下：

```http
http://localhost:8090/auth/oauth/authorize?client_id=sse-pc&response_type=token
```

> `auth`：服务器名称，在yml中自己配置的
>
> `client_id`：客服端id
>
> `response_type`：相应类型是`token`

访问这个地址后，会跳转到登录页面进行登录。登录成功跳转到重定向的`URL`地址，后面就带上token了。

![在这里插入图片描述](https://img-blog.csdnimg.cn/2020060610202266.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200606102142890.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200606102209631.png)

```http
https://www.baidu.com/
#access_token=92d70c3f-2172-4a9e-9ba0-39428659e057
&token_type=bearer
&expires_in=43199
&scope=all
```

> `access_token`：令牌
>
> `token_type`：令牌类型，返回都是这个值
>
> `expires_in`：令牌的时差，一般是12个小时
>
> `scope`：访问范围，在 `.scopes("all")`这里配置的。

## 4.2、客户端模式

客户端模式（Client Credentials Grant）指客户端以自己的名义，而不是以用户的名义，向服务提供商（认证服务器）进行认证。在这种模式中，用户直接向客户端注册，客户端以自己的名义要求服务提供商（认证服务器）提供服务，**其实不存在授权问题**。客户端向认证服务器进行身份认证，并要求一个访问令牌。认证服务器确认无误后，向客户端提供访问令牌。

### 配置客服端授权类型

在认证服务器中`AuthorizationServerConfig`通过`authorizedGrantTypes`指定客服端模式。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200606103312216.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)

### 获取token

既然是以客服端为名义，而不是用户的名义去获取令牌。那么用户登录这个步骤就没有了，并且这个模式是没有刷新令牌的。使用的令牌端点`/oauth/token`，访问地址如下：

```http
http://localhost:8090/auth/oauth/token
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200606104022877.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200606104154255.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)

当前版本号：`eeda16403ee90bf8fd76883cd189d66aa500b02f`