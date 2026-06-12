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
package com.alibaba.csp.sentinel.adapter.gateway.sc.callback;

import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 限流请求处理器接口 - 定义如何处理被 Sentinel 拦截的请求
 * <p>
 * 这是一个函数式接口（FunctionalInterface），用于定义当请求被 Sentinel 流控规则拦截时的处理逻辑。
 * <p>
 * 实现该接口的类需要提供 handleRequest 方法的实现，该方法接收：
 * 1. ServerWebExchange 对象 - 包含被拦截请求的上下文信息
 * 2. Throwable 对象 - 具体的 BlockException，指示被拦截的原因（如流控、熔断等）
 * <p>
 * 返回值是一个 Mono<ServerResponse>，这是 Spring WebFlux 的响应式返回类型。
 * <p>
 * 常见的实现包括：
 * - DefaultBlockRequestHandler：返回默认的 JSON 或 HTML 错误信息
 * - RedirectBlockRequestHandler：将请求重定向到指定的 URL
 * - 自定义实现：根据业务需求返回定制化的响应
 *
 * @author Eric Zhao
 */
@FunctionalInterface
public interface BlockRequestHandler {

    /**
     * 处理被限流的请求
     * <p>
     * 当 Sentinel 检测到请求违反流控规则时，会调用此方法处理该请求。
     *
     * @param exchange ServerWebExchange 对象，包含被拦截请求的上下文
     * @param t        BlockException，表示被拦截的具体原因
     * @return ServerResponse，返回给客户端的响应
     */
    Mono<ServerResponse> handleRequest(ServerWebExchange exchange, Throwable t);
}
