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
import org.springframework.web.server.ServerWebExchange;

import java.util.regex.Pattern;

/**
 * 正则表达式路由路径匹配器 - 使用正则表达式进行路径匹配
 * <p>
 * 该类实现了 Predicate<ServerWebExchange> 接口，
 * 使用 Java 的正则表达式（Pattern）进行路径模式匹配。
 * <p>
 * 正则表达式路径模式示例：
 * - /api/user/\\d+ - 匹配 /api/user/ 后跟数字的路径
 * - /api/.* - 匹配 /api/ 下所有路径
 * - /api/(user|order)/\\d+ - 匹配 /api/user/数字 或 /api/order/数字
 * <p>
 * 特点：
 * - 支持完整的正则表达式语法
 * - 匹配灵活性高
 * - 性能取决于正则表达式的复杂程度
 * <p>
 * 使用场景：
 * 当 Sentinel API 定义使用正则表达式匹配策略时，会创建该类的实例进行路径匹配。
 * <p>
 * 注意：
 * - 正则表达式会编译成 Pattern 对象存储，以提高匹配效率
 * - 使用 matches() 方法进行完全匹配，而非 find() 的部分匹配
 *
 * @author Eric Zhao
 * @since 1.6.0
 */
public class RegexRoutePathMatcher implements Predicate<ServerWebExchange> {

    // 存储的路径模式（正则表达式字符串）
    private final String pattern;
    // 编译后的 Pattern 对象
    private final Pattern regex;

    /**
     * 构造函数
     *
     * @param pattern 正则表达式模式，不能为空
     * @throws IllegalArgumentException 如果 pattern 为空或空白
     */
    public RegexRoutePathMatcher(String pattern) {
        AssertUtil.assertNotBlank(pattern, "pattern cannot be blank");
        this.pattern = pattern;
        // 编译正则表达式为 Pattern 对象
        this.regex = Pattern.compile(pattern);
    }

    /**
     * 测试 ServerWebExchange 是否匹配该正则表达式模式
     * <p>
     * 使用 matches() 方法进行完全匹配，即整个路径字符串必须完全匹配正则表达式。
     *
     * @param exchange ServerWebExchange 对象
     * @return 如果请求路径完全匹配该正则表达式则返回 true
     */
    @Override
    public boolean test(ServerWebExchange exchange) {
        String path = exchange.getRequest().getPath().value();
        return regex.matcher(path).matches();
    }

    /**
     * 获取存储的正则表达式模式
     *
     * @return 正则表达式模式字符串
     */
    public String getPattern() {
        return pattern;
    }
}
