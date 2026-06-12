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
package com.alibaba.csp.sentinel.adapter.gateway.common;

/**
 * Sentinel Gateway 常量定义类 - 定义网关适配器使用的各种常量和枚举值
 *
 * 该类包含以下类型的常量：
 * 1. 应用类型常量
 * 2. 资源模式常量（路由ID模式 vs 自定义API名称模式）
 * 3. 参数解析策略常量（客户端IP、Host、Header、URL参数、Cookie）
 * 4. URL匹配策略常量（精确匹配、前缀匹配、正则匹配）
 * 5. 参数匹配策略常量（精确、前缀、正则、包含）
 * 6. 上下文名称常量
 * 7. 参数值常量（不匹配、默认参数）
 * 
 * @author Eric Zhao
 * @since 1.6.0
 */
public final class SentinelGatewayConstants {

    /**
     * 网关应用类型标识
     */
    public static final int APP_TYPE_GATEWAY = 1;

    /** 资源模式：路由ID模式 - 使用路由ID作为资源名 */
    public static final int RESOURCE_MODE_ROUTE_ID = 0;
    /** 资源模式：自定义API名称模式 - 使用自定义API名称作为资源名 */
    public static final int RESOURCE_MODE_CUSTOM_API_NAME = 1;

    /** 参数解析策略：客户端IP */
    public static final int PARAM_PARSE_STRATEGY_CLIENT_IP = 0;
    /** 参数解析策略：请求头中的Host */
    public static final int PARAM_PARSE_STRATEGY_HOST = 1;
    /** 参数解析策略：请求头 */
    public static final int PARAM_PARSE_STRATEGY_HEADER = 2;
    /** 参数解析策略：URL查询参数 */
    public static final int PARAM_PARSE_STRATEGY_URL_PARAM = 3;
    /** 参数解析策略：Cookie */
    public static final int PARAM_PARSE_STRATEGY_COOKIE = 4;

    /** URL匹配策略：精确匹配 */
    public static final int URL_MATCH_STRATEGY_EXACT = 0;
    /** URL匹配策略：前缀匹配（Ant风格） */
    public static final int URL_MATCH_STRATEGY_PREFIX = 1;
    /** URL匹配策略：正则表达式匹配 */
    public static final int URL_MATCH_STRATEGY_REGEX = 2;

    /** 参数匹配策略：精确匹配 */
    public static final int PARAM_MATCH_STRATEGY_EXACT = 0;
    /** 参数匹配策略：前缀匹配 */
    public static final int PARAM_MATCH_STRATEGY_PREFIX = 1;
    /** 参数匹配策略：正则匹配 */
    public static final int PARAM_MATCH_STRATEGY_REGEX = 2;
    /** 参数匹配策略：包含匹配 */
    public static final int PARAM_MATCH_STRATEGY_CONTAINS = 3;

    /** 默认网关上下文名称 */
    public static final String GATEWAY_CONTEXT_DEFAULT = "sentinel_gateway_context_default";
    /** 网关上下文名称前缀 */
    public static final String GATEWAY_CONTEXT_PREFIX = "sentinel_gateway_context$$";
    /** 路由级网关上下文名称前缀 */
    public static final String GATEWAY_CONTEXT_ROUTE_PREFIX = "sentinel_gateway_context$$route$$";

    /** 不匹配参数标记 */
    public static final String GATEWAY_NOT_MATCH_PARAM = "$NM";
    /** 默认参数标记 */
    public static final String GATEWAY_DEFAULT_PARAM = "$D";

    private SentinelGatewayConstants() {}
}
