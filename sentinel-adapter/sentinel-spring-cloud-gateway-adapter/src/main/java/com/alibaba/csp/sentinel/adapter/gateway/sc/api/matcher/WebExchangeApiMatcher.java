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
package com.alibaba.csp.sentinel.adapter.gateway.sc.api.matcher;

import com.alibaba.csp.sentinel.adapter.gateway.common.SentinelGatewayConstants;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiDefinition;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPathPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.matcher.AbstractApiMatcher;
import com.alibaba.csp.sentinel.adapter.gateway.sc.route.RouteMatchers;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.alibaba.csp.sentinel.util.function.Predicate;
import org.springframework.web.server.ServerWebExchange;

import java.util.Optional;

/**
 * WebExchange API 匹配器 - 根据 API 定义判断请求是否匹配
 * <p>
 * 该类继承自 AbstractApiMatcher<ServerWebExchange>，
 * 负责根据自定义 API 定义的匹配规则，判断传入的 ServerWebExchange 是否匹配该 API。
 * <p>
 * 支持的匹配策略：
 * 1. 精确匹配（exact）：精确匹配请求路径
 * 2. 前缀匹配（prefix/ant）：使用 Ant 风格路径匹配（如 /api/**）
 * 3. 正则匹配（regex）：使用正则表达式进行路径匹配
 * <p>
 * 工作流程：
 * 1. 构造函数接收一个 ApiDefinition 对象
 * 2. initializeMatchers() 方法遍历 ApiDefinition 中的所有匹配项
 * 3. 根据匹配项的类型（ApiPathPredicateItem）和策略，创建对应的 Predicate
 * 4. test() 方法测试 ServerWebExchange 是否匹配该 API 定义
 * <p>
 * API 定义示例：
 * - API 名称：custom_api
 * - 匹配规则：/api/user/**, /api/order/**
 * - 匹配策略：前缀匹配
 *
 * @author Eric Zhao
 * @since 1.6.0
 */
public class WebExchangeApiMatcher extends AbstractApiMatcher<ServerWebExchange> {

    /**
     * 构造函数
     *
     * @param apiDefinition API 定义对象，包含 API 名称和匹配规则
     */
    public WebExchangeApiMatcher(ApiDefinition apiDefinition) {
        super(apiDefinition);
    }

    /**
     * 初始化匹配器集合
     * <p>
     * 遍历 ApiDefinition 中的所有匹配项，为每个匹配项创建对应的 Predicate。
     */
    @Override
    protected void initializeMatchers() {
        if (apiDefinition.getPredicateItems() != null) {
            apiDefinition.getPredicateItems().forEach(item ->
                    fromApiPredicate(item).ifPresent(matchers::add));
        }
    }

    /**
     * 根据 API 匹配项创建对应的 Predicate
     *
     * @param item API 匹配项
     * @return Optional<Predicate<ServerWebExchange>>，包含对应的 Predicate
     */
    private Optional<Predicate<ServerWebExchange>> fromApiPredicate(/*@NonNull*/ ApiPredicateItem item) {
        // 目前仅支持路径匹配项
        if (item instanceof ApiPathPredicateItem) {
            return fromApiPathPredicate((ApiPathPredicateItem) item);
        }
        return Optional.empty();
    }

    /**
     * 根据路径匹配项创建对应的 Predicate
     * <p>
     * 根据匹配策略（正则、前缀、精确）选择不同的路径匹配方式。
     *
     * @param item 路径匹配项
     * @return Optional<Predicate<ServerWebExchange>>，包含对应的 Predicate
     */
    private Optional<Predicate<ServerWebExchange>> fromApiPathPredicate(/*@Valid*/ ApiPathPredicateItem item) {
        String pattern = item.getPattern();
        if (StringUtil.isBlank(pattern)) {
            return Optional.empty();
        }
        // 根据匹配策略选择对应的路径匹配器
        switch (item.getMatchStrategy()) {
            case SentinelGatewayConstants.URL_MATCH_STRATEGY_REGEX:
                // 正则表达式匹配
                return Optional.of(RouteMatchers.regexPath(pattern));
            case SentinelGatewayConstants.URL_MATCH_STRATEGY_PREFIX:
                // Ant 风格前缀匹配
                return Optional.of(RouteMatchers.antPath(pattern));
            default:
                // 默认使用精确匹配
                return Optional.of(RouteMatchers.exactPath(pattern));
        }
    }
}
