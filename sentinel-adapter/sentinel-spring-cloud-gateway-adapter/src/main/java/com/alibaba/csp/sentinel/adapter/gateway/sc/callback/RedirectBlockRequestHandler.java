/*
 * Copyright 1999-2019 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.csp.sentinel.adapter.gateway.sc.callback;

import com.alibaba.csp.sentinel.util.AssertUtil;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

/**
 * 重定向限流处理器 - 将被限流的请求重定向到指定 URL
 *
 * 该类是 BlockRequestHandler 接口的一个实现，用于在被 Sentinel 拦截请求时，
 * 将客户端重定向到预先配置的 URL（通常是错误提示页面）。
 *
 * 工作原理：
 * 使用 HTTP 302 临时重定向响应，将请求重定向到构造时指定的 URL。
 *
 * 使用场景：
 * - 将限流页面托管在独立的静态资源服务器上
 * - 使用 CDN 或专门的错误页面服务
 * - 需要统一的错误提示页面
 *
 * 示例：
 * ```java
 * // 创建重定向到 /static/429.html 的处理器
 * RedirectBlockRequestHandler handler = new RedirectBlockRequestHandler("/static/429.html");
 * GatewayCallbackManager.setBlockHandler(handler);
 * ```
 * 
 * @author Eric Zhao
 * @since 1.6.0
 */
public class RedirectBlockRequestHandler implements BlockRequestHandler {

    // 重定向的目标 URL
    private final URI uri;

    /**
     * 创建重定向处理器
     *
     * @param url 重定向目标 URL，必须非空
     * @throws IllegalArgumentException 如果 url 为空或空白
     */
    public RedirectBlockRequestHandler(String url) {
        AssertUtil.assertNotBlank(url, "url cannot be blank");
        this.uri = URI.create(url);
    }

    /**
     * 处理被限流的请求 - 返回重定向响应
     * <p>
     * 生成一个 HTTP 302 临时重定向响应，将客户端重定向到预配置的 URL。
     *
     * @param exchange ServerWebExchange 对象
     * @param t        BlockException 异常对象（在此实现中未使用）
     * @return 302 重定向响应的 ServerResponse
     */
    @Override
    public Mono<ServerResponse> handleRequest(ServerWebExchange exchange, Throwable t) {
        // 返回 302 临时重定向响应
        return ServerResponse.temporaryRedirect(uri).build();
    }
}
