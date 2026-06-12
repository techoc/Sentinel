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
package com.alibaba.csp.sentinel.adapter.gateway.common.api.matcher;

import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiDefinition;
import com.alibaba.csp.sentinel.log.RecordLog;
import com.alibaba.csp.sentinel.util.AssertUtil;
import com.alibaba.csp.sentinel.util.function.Predicate;

import java.util.HashSet;
import java.util.Set;

/**
 * API 匹配器抽象基类 - 提供 API 匹配的通用逻辑
 *
 * 该类是所有 API 匹配器的基类，实现了 Predicate<T> 接口。
 * 子类需要实现 initializeMatchers() 方法来初始化具体的匹配器。
 *
 * 核心属性：
 * - apiName: API 名称
 * - apiDefinition: API 定义对象
 * - matchers: 匹配器集合（使用 Sentinel 自定义的 Predicate，兼容 JDK 1.7）
 *
 * @param <T> 请求类型（如 ServerWebExchange）
 * @author Eric Zhao
 * @since 1.6.0
 */
public abstract class AbstractApiMatcher<T> implements Predicate<T> {

    protected final String apiName;
    protected final ApiDefinition apiDefinition;
    /**
     * We use {@link com.alibaba.csp.sentinel.util.function.Predicate} here as the min JDK version is 1.7.
     */
    protected final Set<Predicate<T>> matchers = new HashSet<>();

    public AbstractApiMatcher(ApiDefinition apiDefinition) {
        AssertUtil.notNull(apiDefinition, "apiDefinition cannot be null");
        AssertUtil.assertNotBlank(apiDefinition.getApiName(), "apiName cannot be empty");
        this.apiName = apiDefinition.getApiName();
        this.apiDefinition = apiDefinition;

        try {
            initializeMatchers();
        } catch (Exception ex) {
            RecordLog.warn("[GatewayApiMatcher] Failed to initialize internal matchers", ex);
        }
    }

    /**
     * Initialize the matchers.
     */
    protected abstract void initializeMatchers();

    @Override
    public boolean test(T t) {
        for (Predicate<T> matcher : matchers) {
            if (matcher.test(t)) {
                return true;
            }
        }
        return false;
    }

    public String getApiName() {
        return apiName;
    }

    public ApiDefinition getApiDefinition() {
        return apiDefinition;
    }
}
