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
package com.alibaba.csp.sentinel.adapter.gateway.common.rule;

import com.alibaba.csp.sentinel.adapter.gateway.common.SentinelGatewayConstants;

/**
 * Gateway 参数流控项 - 定义参数级别的流控配置
 *
 * 该类用于配置网关流控规则中的参数级流控，支持根据请求参数进行精细化流控。
 *
 * 核心属性：
 * - index: 参数索引（在参数数组中的位置）
 * - parseStrategy: 参数解析策略（IP、Host、Header、URL参数、Cookie）
 * - fieldName: 字段名称（用于 Header、URL参数、Cookie 策略）
 * - pattern: 匹配模式（可选，用于过滤参数值）
 * - matchStrategy: 参数匹配策略（精确、前缀、正则、包含）
 * 
 * @author Eric Zhao
 * @since 1.6.0
 */
public class GatewayParamFlowItem {

    /**
     * Should be set when applying to parameter flow rules.
     */
    private Integer index;

    /**
     * Strategy for parsing item (e.g. client IP, arbitrary headers and URL parameters).
     */
    private int parseStrategy;
    /**
     * Field to get (only required for arbitrary headers or URL parameters mode).
     */
    private String fieldName;
    /**
     * Matching pattern. If not set, all values will be kept in LRU map.
     */
    private String pattern;
    /**
     * Matching strategy for item value.
     */
    private int matchStrategy = SentinelGatewayConstants.PARAM_MATCH_STRATEGY_EXACT;

    public Integer getIndex() {
        return index;
    }

    GatewayParamFlowItem setIndex(Integer index) {
        this.index = index;
        return this;
    }

    public int getParseStrategy() {
        return parseStrategy;
    }

    public GatewayParamFlowItem setParseStrategy(int parseStrategy) {
        this.parseStrategy = parseStrategy;
        return this;
    }

    public String getFieldName() {
        return fieldName;
    }

    public GatewayParamFlowItem setFieldName(String fieldName) {
        this.fieldName = fieldName;
        return this;
    }

    public String getPattern() {
        return pattern;
    }

    public GatewayParamFlowItem setPattern(String pattern) {
        this.pattern = pattern;
        return this;
    }

    public int getMatchStrategy() {
        return matchStrategy;
    }

    public GatewayParamFlowItem setMatchStrategy(int matchStrategy) {
        this.matchStrategy = matchStrategy;
        return this;
    }

    @Override
    public String toString() {
        return "GatewayParamFlowItem{" +
            "index=" + index +
            ", parseStrategy=" + parseStrategy +
            ", fieldName='" + fieldName + '\'' +
            ", pattern='" + pattern + '\'' +
            ", matchStrategy=" + matchStrategy +
            '}';
    }
}
