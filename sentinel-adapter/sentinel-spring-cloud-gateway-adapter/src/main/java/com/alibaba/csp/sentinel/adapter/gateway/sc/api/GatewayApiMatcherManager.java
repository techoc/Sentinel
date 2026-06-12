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
package com.alibaba.csp.sentinel.adapter.gateway.sc.api;

import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiDefinition;
import com.alibaba.csp.sentinel.adapter.gateway.sc.api.matcher.WebExchangeApiMatcher;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Gateway API 匹配器管理器 - 管理自定义 API 定义的匹配器
 * <p>
 * 该类是一个工具类（Utils），采用单例模式的变体实现（通过私有构造函数），
 * 负责管理 Sentinel Gateway 适配器中的自定义 API 定义及其对应的匹配器。
 * <p>
 * 主要功能：
 * 1. 维护 API 名称到 WebExchangeApiMatcher 的映射关系
 * 2. 提供获取所有 API 匹配器的只读视图
 * 3. 管理 API 定义的加载和更新
 * <p>
 * 工作流程：
 * 1. SpringCloudGatewayApiDefinitionChangeObserver 监听 API 定义的变化
 * 2. 当 API 定义变化时，调用 loadApiDefinitions 方法更新匹配器映射
 * 3. SentinelGatewayFilter 使用 getApiMatcherMap() 获取所有匹配器
 * 4. 对每个请求，遍历匹配器判断是否匹配某个 API 定义
 * <p>
 * 线程安全：
 * 使用 volatile 和 synchronized 关键字确保多线程环境下的安全访问。
 *
 * @author Eric Zhao
 * @since 1.6.0
 */
public final class GatewayApiMatcherManager {

    // API 名称到匹配器的映射，使用 volatile 确保可见性
    private static volatile Map<String, WebExchangeApiMatcher> API_MATCHER_MAP = new HashMap<>();

    // 私有构造函数，防止外部实例化
    private GatewayApiMatcherManager() {
    }

    /**
     * 获取所有 API 匹配器的只读视图
     *
     * @return 不可修改的 API 匹配器映射
     */
    public static Map<String, WebExchangeApiMatcher> getApiMatcherMap() {
        return Collections.unmodifiableMap(API_MATCHER_MAP);
    }

    /**
     * 根据 API 名称获取对应的匹配器
     *
     * @param apiName API 名称
     * @return Optional<WebExchangeApiMatcher>，包含匹配器如果存在
     */
    public static Optional<WebExchangeApiMatcher> getMatcher(final String apiName) {
        return Optional.ofNullable(apiName)
                .map(e -> API_MATCHER_MAP.get(apiName));
    }

    /**
     * 获取所有 API 定义的集合
     *
     * @return ApiDefinition 的集合
     */
    public static Set<ApiDefinition> getApiDefinitionSet() {
        return API_MATCHER_MAP.values()
                .stream()
                .map(WebExchangeApiMatcher::getApiDefinition)
                .collect(Collectors.toSet());
    }

    /**
     * 加载/更新 API 定义集合
     * <p>
     * 该方法是同步的，确保在并发情况下的线程安全。
     * 会清空旧的映射关系，并用新的 API 定义创建新的匹配器映射。
     *
     * @param definitions 新的 API 定义集合
     */
    static synchronized void loadApiDefinitions(/*@Valid*/ Set<ApiDefinition> definitions) {
        Map<String, WebExchangeApiMatcher> apiMatcherMap = new HashMap<>();
        for (ApiDefinition definition : definitions) {
            apiMatcherMap.put(definition.getApiName(), new WebExchangeApiMatcher(definition));
        }

        API_MATCHER_MAP = apiMatcherMap;
    }
}
