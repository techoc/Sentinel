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
package com.alibaba.csp.sentinel.adapter.gateway.sc.route;

import com.alibaba.csp.sentinel.util.AssertUtil;
import com.alibaba.csp.sentinel.util.function.Predicate;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.server.ServerWebExchange;

/**
 * Ant 风格路由路径匹配器 - 使用 Ant 风格的路径模式进行匹配
 *
 * 该类实现了 Predicate<ServerWebExchange> 接口，
 * 使用 Spring 的 AntPathMatcher 进行路径模式匹配。
 *
 * Ant 风格路径模式示例：
 * - /api/** - 匹配 /api 下的所有路径（包括子目录）
 * - /api/* - 匹配 /api 下的一个路径段（不包括子目录）
 * - /api/user?name=* - 匹配参数值
 * - /api/user?.html - 匹配单个字符
 *
 * 特点：
 * - 支持 ** 通配符匹配多个目录
 * - 支持 * 通配符匹配一个路径段
 * - 支持 ? 通配符匹配单个字符
 *
 * 使用场景：
 * 当 Sentinel API 定义使用前缀匹配策略时，会创建该类的实例进行路径匹配。
 * 
 * @author Eric Zhao
 * @since 1.6.0
 */
public class AntRoutePathMatcher implements Predicate<ServerWebExchange> {

    // 存储的路径模式
    private final String pattern;
    // Spring 的 AntPathMatcher 实例
    private final PathMatcher pathMatcher;
    // 标记该模式是否为一个有效的 Ant 模式
    private final boolean canMatch;

    /**
     * 构造函数
     *
     * @param pattern Ant 风格的路径模式，不能为空
     * @throws IllegalArgumentException 如果 pattern 为空或空白
     */
    public AntRoutePathMatcher(String pattern) {
        AssertUtil.assertNotBlank(pattern, "pattern cannot be blank");
        this.pattern = pattern;
        this.pathMatcher = new AntPathMatcher();
        // 判断是否为有效的 Ant 模式
        this.canMatch = pathMatcher.isPattern(pattern);
    }

    /**
     * 测试 ServerWebExchange 是否匹配该 Ant 模式
     *
     * @param exchange ServerWebExchange 对象
     * @return 如果请求路径匹配该模式则返回 true
     */
    @Override
    public boolean test(ServerWebExchange exchange) {
        String path = exchange.getRequest().getPath().value();
        if (canMatch) {
            // 使用 AntPathMatcher 进行匹配
            return pathMatcher.match(pattern, path);
        }
        return false;
    }

    /**
     * 获取存储的路径模式
     *
     * @return Ant 风格的路径模式
     */
    public String getPattern() {
        return pattern;
    }
}
