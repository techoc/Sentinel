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
package com.alibaba.csp.sentinel.adapter.gateway.common.command;

import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import com.alibaba.csp.sentinel.command.CommandHandler;
import com.alibaba.csp.sentinel.command.CommandRequest;
import com.alibaba.csp.sentinel.command.CommandResponse;
import com.alibaba.csp.sentinel.command.annotation.CommandMapping;
import com.alibaba.fastjson.JSON;

/**
 * 获取网关规则命令处理器 - 用于获取所有网关流控规则
 *
 * 该类实现了 Sentinel 的 CommandHandler 接口，提供获取网关规则的 API。
 *
 * 命令名称：gateway/getRules
 * 功能：返回所有已加载的 GatewayFlowRule 规则的 JSON 字符串
 * 
 * @author Eric Zhao
 * @since 1.6.0
 */
@CommandMapping(name = "gateway/getRules", desc = "获取所有网关流控规则")
public class GetGatewayRuleCommandHandler implements CommandHandler<String> {

    @Override
    public CommandResponse<String> handle(CommandRequest request) {
        return CommandResponse.ofSuccess(JSON.toJSONString(GatewayRuleManager.getRules()));
    }
}
