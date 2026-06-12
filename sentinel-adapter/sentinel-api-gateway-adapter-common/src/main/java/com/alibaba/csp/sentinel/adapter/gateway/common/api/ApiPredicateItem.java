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

/**
 * API 匹配项接口 - 定义 API 匹配项的基本契约
 *
 * 该接口是所有匹配项类型的基接口，目前主要实现类为 ApiPathPredicateItem。
 *
 * 设计意图：
 * - 提供统一的匹配项抽象，便于扩展其他类型的匹配项
 * - 当前主要支持路径匹配，未来可扩展其他类型（如请求头匹配、参数匹配等）
 * 
 * @author Eric Zhao
 * @since 1.6.0
 */
public interface ApiPredicateItem {

    /**
     * 将两个匹配项组合（已注释，预留功能）
     *
     * @param item 另一个匹配项
     * @return 组合后的匹配项组
     */
    /*default ApiPredicateItem and(ApiPredicateItem item) {
        AssertUtil.notNull(item, "item cannot be null");
        return new ApiPredicateGroupItem()
            .addItem(this).addItem(item);
    }*/
}
