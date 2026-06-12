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
package com.alibaba.csp.sentinel.adapter.gateway.common.param;

/**
 * 请求项解析器接口 - 定义从请求中提取各项信息的契约
 *
 * 该接口用于从请求对象中提取各种信息，供 Sentinel 的参数流控使用。
 * 不同的网关适配器（如 Spring Cloud Gateway、Zuul）需要提供各自的实现。
 *
 * 支持提取的信息：
 * - 路径（Path）
 * - 远程地址（Remote Address）
 * - 请求头（Header）
 * - URL 参数（URL Param）
 * - Cookie 值
 *
 * @param <T> 请求类型
 * @author Eric Zhao
 * @since 1.6.0
 */
public interface RequestItemParser<T> {

    /**
     * Get API path from the request.
     *
     * @param request valid request
     * @return API path
     */
    String getPath(T request);

    /**
     * Get remote address from the request.
     *
     * @param request valid request
     * @return remote address
     */
    String getRemoteAddress(T request);

    /**
     * Get the header associated with the header key.
     *
     * @param request valid request
     * @param key     valid header key
     * @return the header
     */
    String getHeader(T request, String key);

    /**
     * Get the parameter value associated with the parameter name.
     *
     * @param request   valid request
     * @param paramName valid parameter name
     * @return the parameter value
     */
    String getUrlParam(T request, String paramName);

    /**
     * Get the cookie value associated with the cookie name.
     *
     * @param request    valid request
     * @param cookieName valid cookie name
     * @return the cookie value
     * @since 1.7.0
     */
    String getCookieValue(T request, String cookieName);
}
