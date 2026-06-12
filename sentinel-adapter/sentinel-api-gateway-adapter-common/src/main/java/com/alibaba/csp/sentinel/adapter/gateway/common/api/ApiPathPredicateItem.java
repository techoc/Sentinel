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
package com.alibaba.csp.sentinel.adapter.gateway.common.api;

import com.alibaba.csp.sentinel.adapter.gateway.common.SentinelGatewayConstants;

import java.util.Objects;

/**
 * 路径匹配项类 - 定义 API 路径模式及其匹配策略
 * <p>
 * 该类实现了 ApiPredicateItem 接口，用于定义具体的路径匹配规则。
 * <p>
 * 核心属性：
 * - pattern: 路径模式字符串（如 /api/user/**）
 * - matchStrategy: 匹配策略（精确匹配、前缀匹配、正则匹配）
 * <p>
 * 匹配策略说明：
 * - URL_MATCH_STRATEGY_EXACT (0): 精确匹配，路径必须完全一致
 * - URL_MATCH_STRATEGY_PREFIX (1): 前缀匹配，使用 Ant 风格路径匹配
 * - URL_MATCH_STRATEGY_REGEX (2): 正则匹配，使用正则表达式匹配
 *
 * @author Eric Zhao
 * @since 1.6.0
 */
public class ApiPathPredicateItem implements ApiPredicateItem {

    /**
     * 路径模式
     */
    private String pattern;
    /**
     * 匹配策略，默认为精确匹配
     */
    private int matchStrategy = SentinelGatewayConstants.URL_MATCH_STRATEGY_EXACT;

    /**
     * 设置路径模式
     *
     * @param pattern 路径模式字符串
     * @return 当前实例（链式调用）
     */
    public ApiPathPredicateItem setPattern(String pattern) {
        this.pattern = pattern;
        return this;
    }

    /**
     * 设置匹配策略
     *
     * @param matchStrategy 匹配策略常量
     * @return 当前实例（链式调用）
     */
    public ApiPathPredicateItem setMatchStrategy(int matchStrategy) {
        this.matchStrategy = matchStrategy;
        return this;
    }

    public String getPattern() {
        return pattern;
    }

    public int getMatchStrategy() {
        return matchStrategy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ApiPathPredicateItem that = (ApiPathPredicateItem) o;

        if (matchStrategy != that.matchStrategy) {
            return false;
        }
        return Objects.equals(pattern, that.pattern);
    }

    @Override
    public int hashCode() {
        int result = pattern != null ? pattern.hashCode() : 0;
        result = 31 * result + matchStrategy;
        return result;
    }

    @Override
    public String toString() {
        return "ApiPathPredicateItem{" +
                "pattern='" + pattern + '\'' +
                ", matchStrategy=" + matchStrategy +
                '}';
    }
}
