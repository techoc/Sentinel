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
package com.alibaba.csp.sentinel.adapter.gateway.sc.exception;

import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.util.function.Supplier;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.result.view.ViewResolver;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Sentinel Gateway 限流异常处理器 - 处理 Sentinel 拦截的请求异常
 * <p>
 * 该类实现了 Spring WebFlux 的 WebExceptionHandler 接口，
 * 作为 Spring Cloud Gateway 的全局异常处理器，专门处理 Sentinel 产生的流控异常。
 * <p>
 * 工作流程：
 * 1. 当 SentinelGatewayFilter 检测到请求违反流控规则时，会抛出 BlockException
 * 2. Spring Cloud Gateway 的异常处理机制捕获该异常
 * 3. 该处理器判断异常是否为 BlockException
 * 4. 如果是，调用 BlockRequestHandler 生成限流响应
 * 5. 如果不是，则重新抛出异常，让其他处理器处理
 * <p>
 * 响应处理：
 * - 使用 GatewayCallbackManager.getBlockHandler() 获取注册的 BlockRequestHandler
 * - 调用 handleRequest 方法生成 ServerResponse
 * - 将响应写入 ServerWebExchange
 * <p>
 * 注意：
 * 该处理器只处理 Sentinel 的 BlockException，其他异常会被重新抛出。
 * 这确保了 Sentinel 的流控逻辑与其他异常处理逻辑的正确分离。
 *
 * @author Eric Zhao
 * @since 1.6.0
 */
public class SentinelGatewayBlockExceptionHandler implements WebExceptionHandler {

    // 视图解析器列表，用于渲染模板
    private List<ViewResolver> viewResolvers;
    // HTTP 消息写入器列表，用于序列化响应
    private List<HttpMessageWriter<?>> messageWriters;
    /**
     * ServerResponse.Context 的实现
     * <p>
     * 提供响应序列化所需的视图解析器和消息写入器。
     */
    private final Supplier<ServerResponse.Context> contextSupplier = () -> new ServerResponse.Context() {
        @Override
        public List<HttpMessageWriter<?>> messageWriters() {
            return SentinelGatewayBlockExceptionHandler.this.messageWriters;
        }

        @Override
        public List<ViewResolver> viewResolvers() {
            return SentinelGatewayBlockExceptionHandler.this.viewResolvers;
        }
    };

    /**
     * 构造函数
     *
     * @param viewResolvers         视图解析器列表（可以为空的 List）
     * @param serverCodecConfigurer ServerCodecConfigurer，用于获取消息写入器
     */
    public SentinelGatewayBlockExceptionHandler(List<ViewResolver> viewResolvers, ServerCodecConfigurer serverCodecConfigurer) {
        this.viewResolvers = viewResolvers;
        this.messageWriters = serverCodecConfigurer.getWriters();
    }

    /**
     * 将响应写入 ServerWebExchange
     *
     * @param response ServerResponse 响应对象
     * @param exchange ServerWebExchange 对象
     * @return Mono<Void> 表示写入完成
     */
    private Mono<Void> writeResponse(ServerResponse response, ServerWebExchange exchange) {
        return response.writeTo(exchange, contextSupplier.get());
    }

    /**
     * 处理异常的核心方法
     * <p>
     * 判断异常类型并调用相应的处理逻辑。
     *
     * @param exchange ServerWebExchange 对象
     * @param ex       抛出的异常
     * @return Mono<Void> 表示处理完成
     */
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        // 如果响应已经提交，直接抛出异常
        if (exchange.getResponse().isCommitted()) {
            return Mono.error(ex);
        }
        // 仅处理 Sentinel 的 BlockException
        if (!BlockException.isBlockException(ex)) {
            return Mono.error(ex);
        }
        // 处理被限流的请求
        return handleBlockedRequest(exchange, ex)
                .flatMap(response -> writeResponse(response, exchange));
    }

    /**
     * 处理被限流的请求
     * <p>
     * 调用注册的 BlockRequestHandler 生成限流响应。
     *
     * @param exchange  ServerWebExchange 对象
     * @param throwable BlockException 异常
     * @return ServerResponse 响应对象
     */
    private Mono<ServerResponse> handleBlockedRequest(ServerWebExchange exchange, Throwable throwable) {
        return GatewayCallbackManager.getBlockHandler().handleRequest(exchange, throwable);
    }
}
