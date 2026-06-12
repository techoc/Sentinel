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
package com.alibaba.csp.sentinel.adapter.gateway.sc.route;


import com.alibaba.csp.sentinel.util.function.Predicate;

import org.springframework.web.server.ServerWebExchange;

/**
 * 路由路径匹配器工厂 - 提供各种路径匹配方式的 Predicate 创建方法
 * <p>
 * 该类是一个工具类（Utils），采用单例模式的变体实现（通过私有构造函数），
 * 负责创建不同类型的路径匹配器（Predicate<ServerWebExchange>）。
 * <p>
 * 支持的匹配类型：
 * 1. all() - 匹配所有请求
 * 2. exactPath() - 精确路径匹配
 * 3. antPath() - Ant 风格路径匹配（如 /api/**, /api/user/*）
 * 4. regexPath() - 正则表达式路径匹配
 * <p>
 * 使用方式：
 * 在 WebExchangeApiMatcher 中，根据不同的匹配策略调用相应的方法创建匹配器。
 *
 * @author Eric Zhao
 * @since 1.6.0
 */
public final class RouteMatchers {

    // 私有构造函数，防止外部实例化
    private RouteMatchers() {
    }

    /**
     * 创建一个匹配所有请求的 Predicate
     *
     * @return 始终返回 true 的 Predicate
     */
    public static Predicate<ServerWebExchange> all() {
        return exchange -> true;
    }

    /**
     * 创建一个 Ant 风格路径匹配器
     *
     * @param pathPattern Ant 风格的路径模式（如 /api/**, /api/user/*）
     * @return AntRoutePathMatcher 实例
     */
    public static Predicate<ServerWebExchange> antPath(String pathPattern) {
        return new AntRoutePathMatcher(pathPattern);
    }

    /**
     * 创建一个精确路径匹配器
     *
     * @param path 精确的路径字符串
     * @return Predicate，匹配给定路径的请求
     */
    public static Predicate<ServerWebExchange> exactPath(final String path) {
        return exchange -> exchange.getRequest().getPath().value().equals(path);
    }

    /**
     * 创建一个正则表达式路径匹配器
     *
     * @param pathPattern 正则表达式模式
     * @return RegexRoutePathMatcher 实例
     */
    public static Predicate<ServerWebExchange> regexPath(String pathPattern) {
        return new RegexRoutePathMatcher(pathPattern);
    }
}
