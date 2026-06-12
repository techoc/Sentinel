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
import org.springframework.web.server.ServerWebExchange;

import java.util.function.Function;

/**
 * Gateway 回调管理器 - 管理 Sentinel Gateway 适配器的全局回调配置
 * <p>
 * 该类是一个工具类（Utils），采用单例模式的变体实现（通过私有构造函数），
 * 用于集中管理 Gateway 适配器的各类回调处理器。
 * <p>
 * 主要管理两类回调：
 * 1. BlockRequestHandler：处理被限流请求的处理器
 * - 默认实现：DefaultBlockRequestHandler
 * - 可通过 setBlockHandler 方法自定义
 * <p>
 * 2. RequestOriginParser：请求来源解析器
 * - 用于从 ServerWebExchange 中提取请求来源标识（如 IP、用户ID等）
 * - 默认返回空字符串
 * - 可通过 setRequestOriginParser 方法自定义
 * <p>
 * 这些回调在 SentinelGatewayFilter 和 SentinelGatewayBlockExceptionHandler 中被使用，
 * 实现了流控逻辑与业务处理逻辑的解耦。
 *
 * @author Eric Zhao
 * @since 1.6.0
 */
public final class GatewayCallbackManager {

    // 默认的请求来源解析器，返回空字符串
    private static final Function<ServerWebExchange, String> DEFAULT_ORIGIN_PARSER = (w) -> "";

    /**
     * BlockRequestHandler：处理被限流请求的处理器
     * 使用 volatile 关键字确保多线程环境下的可见性
     */
    private static volatile BlockRequestHandler blockHandler = new DefaultBlockRequestHandler();

    /**
     * RequestOriginParser：请求来源解析器函数
     * 接收 ServerWebExchange，返回请求来源标识
     */
    private static volatile Function<ServerWebExchange, String> requestOriginParser = DEFAULT_ORIGIN_PARSER;

    // 私有构造函数，防止外部实例化
    private GatewayCallbackManager() {
    }

    /**
     * 获取当前的 BlockRequestHandler
     *
     * @return 当前注册的限流请求处理器
     */
    public static /*@NonNull*/ BlockRequestHandler getBlockHandler() {
        return blockHandler;
    }

    /**
     * 设置自定义的 BlockRequestHandler
     *
     * @param blockHandler 新的限流请求处理器，不能为 null
     */
    public static void setBlockHandler(BlockRequestHandler blockHandler) {
        AssertUtil.notNull(blockHandler, "blockHandler cannot be null");
        GatewayCallbackManager.blockHandler = blockHandler;
    }

    /**
     * 重置 BlockRequestHandler 为默认实现
     */
    public static void resetBlockHandler() {
        GatewayCallbackManager.blockHandler = new DefaultBlockRequestHandler();
    }

    /**
     * 获取当前的 RequestOriginParser
     *
     * @return 当前注册的请求来源解析器
     */
    public static /*@NonNull*/ Function<ServerWebExchange, String> getRequestOriginParser() {
        return requestOriginParser;
    }

    /**
     * 设置自定义的 RequestOriginParser
     *
     * @param requestOriginParser 新的请求来源解析器，不能为 null
     */
    public static void setRequestOriginParser(Function<ServerWebExchange, String> requestOriginParser) {
        AssertUtil.notNull(requestOriginParser, "requestOriginParser cannot be null");
        GatewayCallbackManager.requestOriginParser = requestOriginParser;
    }

    /**
     * 重置 RequestOriginParser 为默认实现
     */
    public static void resetRequestOriginParser() {
        GatewayCallbackManager.requestOriginParser = DEFAULT_ORIGIN_PARSER;
    }
}
