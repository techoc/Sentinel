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
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiDefinitionChangeObserver;

import java.util.Set;

/**
 * Spring Cloud Gateway API 定义变更观察者
 * <p>
 * 该类实现了 ApiDefinitionChangeObserver 接口，是 Sentinel API 定义变更的观察者。
 * <p>
 * 在 Sentinel 的架构中：
 * - ApiDefinitionChangeObserver 是 API 定义变更的观察者接口
 * - 当 API 定义发生变更时（例如通过 Sentinel Dashboard 动态配置），会通知所有观察者
 * - 该类接收到通知后，会更新 GatewayApiMatcherManager 中的匹配器映射
 * <p>
 * 工作流程：
 * 1. Sentinel 动态配置发生变化时，触发 ApiDefinition 变更
 * 2. Sentinel 调用所有已注册的 ApiDefinitionChangeObserver 的 onChange 方法
 * 3. 该类的 onChange 方法被调用，接收新的 API 定义集合
 * 4. 调用 GatewayApiMatcherManager.loadApiDefinitions 更新匹配器
 * <p>
 * 使用 SPI 机制：
 * 该类通过 Java SPI（Service Provider Interface）机制自动注册为观察者。
 * 在 META-INF/services 目录下配置 com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiDefinitionChangeObserver
 * 文件，指定该类的全限定名即可自动加载。
 *
 * @author Eric Zhao
 * @since 1.6.0
 */
public class SpringCloudGatewayApiDefinitionChangeObserver implements ApiDefinitionChangeObserver {

    /**
     * 当 API 定义发生变更时调用
     * <p>
     * 将接收到的新的 API 定义集合传递给 GatewayApiMatcherManager 进行更新。
     *
     * @param apiDefinitions 新的 API 定义集合
     */
    @Override
    public void onChange(Set<ApiDefinition> apiDefinitions) {
        GatewayApiMatcherManager.loadApiDefinitions(apiDefinitions);
    }
}
