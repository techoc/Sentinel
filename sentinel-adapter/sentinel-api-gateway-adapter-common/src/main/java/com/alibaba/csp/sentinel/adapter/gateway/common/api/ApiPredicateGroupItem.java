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
package com.alibaba.csp.sentinel.adapter.gateway.common.api;

import com.alibaba.csp.sentinel.util.AssertUtil;

import java.util.HashSet;
import java.util.Set;

/**
 * API 匹配项组类 - 用于组合多个匹配项
 *
 * 该类实现了 ApiPredicateItem 接口，用于将多个匹配项组合在一起。
 *
 * 使用场景：
 * - 当需要将多个匹配条件组合使用时
 * - 支持动态添加匹配项
 * 
 * @author Eric Zhao
 * @since 1.6.0
 */
public class ApiPredicateGroupItem implements ApiPredicateItem {

    /**
     * 匹配项集合
     */
    private final Set<ApiPredicateItem> items = new HashSet<>();

    /**
     * 添加一个匹配项到组中
     *
     * @param item 匹配项
     * @return 当前实例（链式调用）
     */
    public ApiPredicateGroupItem addItem(ApiPredicateItem item) {
        AssertUtil.notNull(item, "item cannot be null");
        items.add(item);
        return this;
    }

    /**
     * 获取所有匹配项
     *
     * @return 匹配项集合
     */
    public Set<ApiPredicateItem> getItems() {
        return items;
    }

    /**
     * 组合匹配项（已注释，预留功能）
     */
    /*@Override
    public ApiPredicateItem and(ApiPredicateItem item) {
        AssertUtil.notNull(item, "item cannot be null");
        return this.addItem(item);
    }*/
}
