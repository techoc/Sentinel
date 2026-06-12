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
package com.alibaba.csp.sentinel.adapter.gateway.common.slot;

import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import com.alibaba.csp.sentinel.context.Context;
import com.alibaba.csp.sentinel.node.DefaultNode;
import com.alibaba.csp.sentinel.slotchain.AbstractLinkedProcessorSlot;
import com.alibaba.csp.sentinel.slotchain.ResourceWrapper;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowChecker;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParameterMetricStorage;
import com.alibaba.csp.sentinel.spi.Spi;

import java.util.List;

/**
 * Gateway 流控 Slot - 在 Sentinel 调用链中执行网关层的参数流控检查
 *
 * 该类是 Sentinel Slot Chain 的一部分，负责在网关层进行参数级流控检查。
 *
 * 工作流程：
 * 1. 从 GatewayRuleManager 获取转换后的参数流控规则
 * 2. 初始化参数指标
 * 3. 使用 ParamFlowChecker 进行流控检查
 * 4. 如果检查不通过，抛出 ParamFlowException
 *
 * SPI 配置：
 * - 使用 @Spi(order = -4000) 注解，确保在其他 Slot 之前执行
 * 
 * @author Eric Zhao
 * @since 1.6.1
 */
@Spi(order = -4000)
public class GatewayFlowSlot extends AbstractLinkedProcessorSlot<DefaultNode> {

    @Override
    public void entry(Context context, ResourceWrapper resource, DefaultNode node, int count,
                      boolean prioritized, Object... args) throws Throwable {
        checkGatewayParamFlow(resource, count, args);

        fireEntry(context, resource, node, count, prioritized, args);
    }

    private void checkGatewayParamFlow(ResourceWrapper resourceWrapper, int count, Object... args)
        throws BlockException {
        if (args == null) {
            return;
        }

        List<ParamFlowRule> rules = GatewayRuleManager.getConvertedParamRules(resourceWrapper.getName());
        if (rules == null || rules.isEmpty()) {
            return;
        }

        for (ParamFlowRule rule : rules) {
            // Initialize the parameter metrics.
            ParameterMetricStorage.initParamMetricsFor(resourceWrapper, rule);

            if (!ParamFlowChecker.passCheck(resourceWrapper, rule, count, args)) {
                String triggeredParam = "";
                if (args.length > rule.getParamIdx()) {
                    Object value = args[rule.getParamIdx()];
                    triggeredParam = String.valueOf(value);
                }
                throw new ParamFlowException(resourceWrapper.getName(), triggeredParam, rule);
            }
        }
    }

    @Override
    public void exit(Context context, ResourceWrapper resourceWrapper, int count, Object... args) {
        fireExit(context, resourceWrapper, count, args);
    }
}
