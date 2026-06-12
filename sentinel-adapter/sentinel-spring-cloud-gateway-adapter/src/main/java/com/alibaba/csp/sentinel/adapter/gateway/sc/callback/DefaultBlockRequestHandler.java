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
package com.alibaba.csp.sentinel.adapter.gateway.sc.callback;

import org.springframework.http.HttpStatus;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;

/**
 * 默认的限流请求处理器 - BlockRequestHandler 的默认实现
 *
 * 该类是 BlockRequestHandler 接口的默认实现，提供了被 Sentinel 拦截后的标准响应逻辑。
 *
 * 响应逻辑：
 * 1. 根据客户端 Accept 请求头判断客户端类型
 * 2. 如果客户端接受 HTML（如浏览器访问），返回纯文本格式的错误信息
 * 3. 否则（默认情况），返回 JSON 格式的错误信息
 *
 * 错误信息格式：
 * - 默认前缀："Blocked by Sentinel: "
 * - 异常类型名：例如 "FlowException"、"DegradeException" 等
 *
 * 响应示例：
 * - HTML: "Blocked by Sentinel: FlowException" (HTTP 429)
 * - JSON: {"code":429,"message":"Blocked by Sentinel: FlowException"} (HTTP 429)
 *
 * 该处理器兼容 Spring WebFlux 和 Spring Cloud Gateway 环境。
 * 
 * @author Eric Zhao
 */
public class DefaultBlockRequestHandler implements BlockRequestHandler {

    // 默认的限流错误信息前缀
    private static final String DEFAULT_BLOCK_MSG_PREFIX = "Blocked by Sentinel: ";

    /**
     * 处理被限流的请求
     * <p>
     * 根据客户端的 Accept 请求头决定返回 HTML 还是 JSON 格式的错误响应。
     *
     * @param exchange ServerWebExchange 对象
     * @param ex       BlockException 异常对象
     * @return ServerResponse 响应对象
     */
    @Override
    public Mono<ServerResponse> handleRequest(ServerWebExchange exchange, Throwable ex) {
        if (acceptsHtml(exchange)) {
            // 如果客户端接受 HTML，返回纯文本响应
            return htmlErrorResponse(ex);
        }
        // 默认返回 JSON 格式响应
        return ServerResponse.status(HttpStatus.TOO_MANY_REQUESTS)
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .body(fromObject(buildErrorResult(ex)));
    }

    /**
     * 生成 HTML 格式的错误响应
     *
     * @param ex BlockException 异常对象
     * @return HTML 格式的 ServerResponse
     */
    private Mono<ServerResponse> htmlErrorResponse(Throwable ex) {
        return ServerResponse.status(HttpStatus.TOO_MANY_REQUESTS)
            .contentType(MediaType.TEXT_PLAIN)
            .syncBody(DEFAULT_BLOCK_MSG_PREFIX + ex.getClass().getSimpleName());
    }

    /**
     * 构建错误结果对象
     *
     * @param ex BlockException 异常对象
     * @return ErrorResult 错误结果对象
     */
    private ErrorResult buildErrorResult(Throwable ex) {
        return new ErrorResult(HttpStatus.TOO_MANY_REQUESTS.value(),
            DEFAULT_BLOCK_MSG_PREFIX + ex.getClass().getSimpleName());
    }

    /**
     * 判断客户端是否接受 HTML 响应
     *
     * 参考 Spring Boot 的 DefaultErrorWebExceptionHandler 实现，
     * 检查 Accept 请求头中是否包含 text/html。
     *
     * @param exchange ServerWebExchange 对象
     * @return 如果接受 HTML 则返回 true
     */
    private boolean acceptsHtml(ServerWebExchange exchange) {
        try {
            List<MediaType> acceptedMediaTypes = exchange.getRequest().getHeaders().getAccept();
            acceptedMediaTypes.remove(MediaType.ALL);
            MediaType.sortBySpecificityAndQuality(acceptedMediaTypes);
            return acceptedMediaTypes.stream()
                .anyMatch(MediaType.TEXT_HTML::isCompatibleWith);
        } catch (InvalidMediaTypeException ex) {
            return false;
        }
    }

    /**
     * 错误结果内部类 - 用于 JSON 格式响应
     */
    private static class ErrorResult {
        private final int code;    // HTTP 状态码
        private final String message;  // 错误信息

        ErrorResult(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }
}
