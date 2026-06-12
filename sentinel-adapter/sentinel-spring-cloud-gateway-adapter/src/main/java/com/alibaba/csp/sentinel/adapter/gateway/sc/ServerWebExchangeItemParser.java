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
package com.alibaba.csp.sentinel.adapter.gateway.sc;

import com.alibaba.csp.sentinel.adapter.gateway.common.param.RequestItemParser;
import org.springframework.http.HttpCookie;
import org.springframework.web.server.ServerWebExchange;

import java.net.InetSocketAddress;
import java.util.Optional;

/**
 * ServerWebExchange 请求项解析器 - 将 ServerWebExchange 转换为流控参数
 *
 * 该类实现了 RequestItemParser<ServerWebExchange> 接口，
 * 负责从 Spring Cloud Gateway 的 ServerWebExchange 对象中提取各种请求信息，
 * 以便在 Sentinel 流控规则中使用这些信息作为流控参数。
 *
 * 支持提取的请求信息包括：
 * - 请求路径（Path）
 * - 远程客户端地址（Remote Address）
 * - HTTP 请求头（Header）
 * - URL 查询参数（URL Param）
 * - HTTP Cookie 值
 *
 * 这些提取的信息可以用于：
 * - 按来源IP进行流控
 * - 按请求参数进行流控
 * - 按Cookie值进行流控
 * - 自定义参数组合流控
 * 
 * @author Eric Zhao
 * @since 1.6.0
 */
public class ServerWebExchangeItemParser implements RequestItemParser<ServerWebExchange> {

    /**
     * 从 ServerWebExchange 中提取请求路径
     *
     * @param exchange ServerWebExchange 对象
     * @return 请求的路径部分（例如：/api/users）
     */
    @Override
    public String getPath(ServerWebExchange exchange) {
        return exchange.getRequest().getPath().value();
    }

    /**
     * 从 ServerWebExchange 中提取远程客户端的 IP 地址
     *
     * @param exchange ServerWebExchange 对象
     * @return 远程客户端的 IP 地址，如果无法获取则返回 null
     */
    @Override
    public String getRemoteAddress(ServerWebExchange exchange) {
        InetSocketAddress remoteAddress = exchange.getRequest().getRemoteAddress();
        if (remoteAddress == null) {
            return null;
        }
        return remoteAddress.getAddress().getHostAddress();
    }

    /**
     * 从 ServerWebExchange 中提取指定名称的 HTTP 请求头
     *
     * @param exchange ServerWebExchange 对象
     * @param key      请求头的名称
     * @return 请求头的值，如果不存在则返回 null
     */
    @Override
    public String getHeader(ServerWebExchange exchange, String key) {
        return exchange.getRequest().getHeaders().getFirst(key);
    }

    /**
     * 从 ServerWebExchange 中提取指定名称的 URL 查询参数
     *
     * @param exchange  ServerWebExchange 对象
     * @param paramName 参数名称
     * @return 参数的值，如果不存在则返回 null
     */
    @Override
    public String getUrlParam(ServerWebExchange exchange, String paramName) {
        return exchange.getRequest().getQueryParams().getFirst(paramName);
    }

    /**
     * 从 ServerWebExchange 中提取指定名称的 Cookie 值
     *
     * @param exchange   ServerWebExchange 对象
     * @param cookieName Cookie 的名称
     * @return Cookie 的值，如果不存在则返回 null
     */
    @Override
    public String getCookieValue(ServerWebExchange exchange, String cookieName) {
        return Optional.ofNullable(exchange.getRequest().getCookies().getFirst(cookieName))
            .map(HttpCookie::getValue)
            .orElse(null);
    }
}
