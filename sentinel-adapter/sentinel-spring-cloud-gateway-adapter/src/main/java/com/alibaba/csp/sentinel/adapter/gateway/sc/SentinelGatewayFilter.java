/*
 * Copyright 1999-2019 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.csp.sentinel.adapter.gateway.sc;

import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.ResourceTypeConstants;
import com.alibaba.csp.sentinel.adapter.gateway.common.SentinelGatewayConstants;
import com.alibaba.csp.sentinel.adapter.gateway.common.param.GatewayParamParser;
import com.alibaba.csp.sentinel.adapter.gateway.common.param.RequestItemParser;
import com.alibaba.csp.sentinel.adapter.gateway.sc.api.GatewayApiMatcherManager;
import com.alibaba.csp.sentinel.adapter.gateway.sc.api.matcher.WebExchangeApiMatcher;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.alibaba.csp.sentinel.adapter.reactor.ContextConfig;
import com.alibaba.csp.sentinel.adapter.reactor.EntryConfig;
import com.alibaba.csp.sentinel.adapter.reactor.SentinelReactorTransformer;
import com.alibaba.csp.sentinel.util.AssertUtil;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Sentinel Gateway Filter - Spring Cloud Gateway 的 Sentinel 集成过滤器
 * <p>
 * 该过滤器实现了 GatewayFilter 和 GlobalFilter 接口，作为 Spring Cloud Gateway 的全局过滤器运行。
 * 它将 Spring Cloud Gateway 的路由和自定义 API 定义与 Sentinel 的流控规则进行集成。
 * <p>
 * 工作原理：
 * 1. 在请求进入时，尝试从 ServerWebExchange 中获取路由信息
 * 2. 如果存在路由，为该路由创建一个 Sentinel Entry，并应用流控规则
 * 3. 检查请求是否匹配任何自定义 API 定义，如果匹配则创建对应的 Sentinel Entry
 * 4. 使用 Reactor 响应式编程模型，通过 SentinelReactorTransformer 将流控逻辑嵌入到响应式调用链中
 * <p>
 * 过滤器会为每个匹配的路由/API 创建独立的 Entry，实现独立的流控统计。
 *
 * @author Eric Zhao
 * @since 1.6.0
 */
public class SentinelGatewayFilter implements GatewayFilter, GlobalFilter, Ordered {

    private final int order;

    private final GatewayParamParser<ServerWebExchange> paramParser;

    /**
     * 使用默认顺序（最高优先级）创建过滤器实例
     */
    public SentinelGatewayFilter() {
        this(Ordered.HIGHEST_PRECEDENCE);
    }

    /**
     * 使用指定顺序创建过滤器实例
     *
     * @param order 过滤器执行顺序，数值越小越早执行
     */
    public SentinelGatewayFilter(int order) {
        this(order, new ServerWebExchangeItemParser());
    }

    /**
     * 使用自定义请求解析器创建过滤器实例（使用默认顺序）
     *
     * @param serverWebExchangeItemParser 请求参数解析器
     */
    public SentinelGatewayFilter(RequestItemParser<ServerWebExchange> serverWebExchangeItemParser) {
        this(Ordered.HIGHEST_PRECEDENCE, serverWebExchangeItemParser);
    }

    /**
     * 使用指定顺序和自定义请求解析器创建过滤器实例
     *
     * @param order             过滤器执行顺序，数值越小越早执行
     * @param requestItemParser 请求参数解析器，用于提取流控参数
     */
    public SentinelGatewayFilter(int order, RequestItemParser<ServerWebExchange> requestItemParser) {
        AssertUtil.notNull(requestItemParser, "requestItemParser cannot be null");
        this.order = order;
        this.paramParser = new GatewayParamParser<>(requestItemParser);
    }

    /**
     * 过滤器核心方法 - 处理每个经过网关的请求
     * <p>
     * 该方法实现了 Spring Cloud Gateway 的过滤器逻辑，将 Sentinel 流控集成到响应式调用链中。
     * <p>
     * 处理流程：
     * 1. 从 ServerWebExchange 中获取路由信息（通过 GATEWAY_ROUTE_ATTR 属性）
     * 2. 为路由创建 Sentinel Entry（如果存在路由），应用基于路由ID的流控规则
     * 3. 查找所有匹配的自定义 API 定义，为每个匹配的API创建 Sentinel Entry
     * 4. 通过 SentinelReactorTransformer 将流控检查嵌入到 Mono<Void> 响应式调用链中
     * <p>
     * 注意：流控检查发生在 chain.filter(exchange) 执行之后，这是为了确保请求已经被正确路由。
     * 如果在请求被路由之前进行流控检查，可能会导致一些依赖路由信息的参数解析失败。
     *
     * @param exchange ServerWebExchange 对象，包含请求和响应信息
     * @param chain    过滤器链，用于将请求传递给下一个过滤器
     * @return Mono<Void> 响应式结果，包含流控逻辑
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);

        Mono<Void> asyncResult = chain.filter(exchange);
        if (route != null) {
            String routeId = route.getId();
            Object[] params = paramParser.parseParameterFor(routeId, exchange,
                    r -> r.getResourceMode() == SentinelGatewayConstants.RESOURCE_MODE_ROUTE_ID);
            String origin = Optional.ofNullable(GatewayCallbackManager.getRequestOriginParser())
                    .map(f -> f.apply(exchange))
                    .orElse("");
            asyncResult = asyncResult.transform(
                    new SentinelReactorTransformer<>(new EntryConfig(routeId, ResourceTypeConstants.COMMON_API_GATEWAY,
                            EntryType.IN, 1, params, new ContextConfig(contextName(routeId), origin)))
            );
        }

        Set<String> matchingApis = pickMatchingApiDefinitions(exchange);
        for (String apiName : matchingApis) {
            Object[] params = paramParser.parseParameterFor(apiName, exchange,
                    r -> r.getResourceMode() == SentinelGatewayConstants.RESOURCE_MODE_CUSTOM_API_NAME);
            asyncResult = asyncResult.transform(
                    new SentinelReactorTransformer<>(new EntryConfig(apiName, ResourceTypeConstants.COMMON_API_GATEWAY,
                            EntryType.IN, 1, params))
            );
        }

        return asyncResult;
    }

    /**
     * 生成 Sentinel 上下文的名称
     * <p>
     * 上下文名称用于标识不同的流控上下文，格式为 "gateway:" + 路由ID。
     * 同一个上下文中的资源可以共享统计信息。
     *
     * @param route 路由ID
     * @return 上下文名称
     */
    private String contextName(String route) {
        return SentinelGatewayConstants.GATEWAY_CONTEXT_ROUTE_PREFIX + route;
    }

    /**
     * 从 ServerWebExchange 中查找所有匹配的自定义 API 定义
     * <p>
     * 该方法遍历所有已注册的 API 定义（通过 GatewayApiMatcherManager），
     * 使用每个 matcher 测试当前请求是否匹配该 API 的匹配规则。
     * 返回所有匹配的 API 名称集合。
     *
     * @param exchange ServerWebExchange 对象
     * @return 匹配的 API 名称集合
     */
    Set<String> pickMatchingApiDefinitions(ServerWebExchange exchange) {
        return GatewayApiMatcherManager.getApiMatcherMap().values()
                .stream()
                .filter(m -> m.test(exchange))
                .map(WebExchangeApiMatcher::getApiName)
                .collect(Collectors.toSet());
    }

    /**
     * 获取过滤器的执行顺序
     * <p>
     * 实现 Ordered 接口，返回过滤器的执行优先级。
     * 默认情况下，过滤器使用最高优先级（HIGHEST_PRECEDENCE），
     * 这意味着它会在其他过滤器之前执行，从而确保流控逻辑在任何业务逻辑之前被应用。
     *
     * @return 过滤器顺序值
     */
    @Override
    public int getOrder() {
        return order;
    }
}
