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
package com.alibaba.csp.sentinel.adapter.gateway.common.api;

import java.util.Set;

/**
 * API 定义变更观察者接口 - 定义 API 定义变更的回调契约
 *
 * 该接口用于监听 API 定义的变更事件。当 GatewayApiDefinitionManager 中的 API 定义发生变化时，
 * 会通知所有已注册的观察者。
 *
 * 使用场景：
 * - 各个网关适配器（如 Spring Cloud Gateway、Zuul）需要监听 API 定义变化
 * - 当 API 定义更新时，同步更新本地的匹配器缓存
 *
 * 实现方式：
 * 通过 SPI 机制自动注册观察者，在 META-INF/services 目录下配置实现类。
 * 
 * @author Eric Zhao
 * @since 1.6.0
 */
public interface ApiDefinitionChangeObserver {

    /**
     * 当 API 定义发生变更时调用
     *
     * @param apiDefinitions 新的 API 定义集合
     */
    void onChange(Set<ApiDefinition> apiDefinitions);
}
