# Sentinel Spring Cloud Gateway 适配器

Sentinel 提供了与 Spring Cloud Gateway 的集成模块，该模块基于 Sentinel Reactor Adapter 实现。

在 `pom.xml` 中添加以下依赖（如果使用 Maven）：

```xml
<dependency>
    <groupId>com.alibaba.csp</groupId>
    <artifactId>sentinel-spring-cloud-gateway-adapter</artifactId>
    <version>x.y.z</version>
</dependency>
```

然后只需在 Spring 配置中注入相应的 `SentinelGatewayFilter` 和 `SentinelGatewayBlockExceptionHandler` 实例。例如：

```java
@Configuration
public class GatewayConfiguration {

    private final List<ViewResolver> viewResolvers;
    private final ServerCodecConfigurer serverCodecConfigurer;

    public GatewayConfiguration(ObjectProvider<List<ViewResolver>> viewResolversProvider,
                                ServerCodecConfigurer serverCodecConfigurer) {
        this.viewResolvers = viewResolversProvider.getIfAvailable(Collections::emptyList);
        this.serverCodecConfigurer = serverCodecConfigurer;
    }

    @Bean
    @Order(-1)
    public SentinelGatewayBlockExceptionHandler sentinelGatewayBlockExceptionHandler() {
      // 注册 Spring Cloud Gateway 的限流异常处理器
        return new SentinelGatewayBlockExceptionHandler(viewResolvers, serverCodecConfigurer);
    }

    @Bean
    @Order(-1)
    public GlobalFilter sentinelGatewayFilter() {
        return new SentinelGatewayFilter();
    }
}
```

网关适配器会将所有 `routeId`（在 Spring 属性中定义）和所有自定义的 API 定义（在 `sentinel-api-gateway-adapter-common` 模块的
`GatewayApiDefinitionManager` 中定义）视为资源。

您可以在 `GatewayCallbackManager` 中注册各种自定义回调：

- `setBlockHandler`: 注册自定义的 `BlockRequestHandler` 来处理被限流的请求。默认实现是 `DefaultBlockRequestHandler`，返回类似
  `Blocked by Sentinel: FlowException` 的默认消息。
