/*
 * Copyright (c) 2020 pig4cloud Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.px.pa.modulars.upms.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pig4cloud.pig.common.core.util.R;
import com.px.pa.modulars.upms.entity.SysLog;
import com.px.pa.modulars.upms.service.SysLogService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * @author 品讯科技
 * @date 2024-08
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/log")
@Api(value = "log", tags = "log")
public class LogController {

    private final SysLogService sysLogService;

    /**
     * 分页log 信息
     *
     * @param sysLog 参数集
     * @return log集合
     */
    @GetMapping("/page")
    public R log(Page page, SysLog sysLog) {
        return R.ok(sysLogService.lambdaQuery()
                .eq(sysLog.getType() != null, SysLog::getType, sysLog.getType())
                .page(page));
    }

    /**
     * 删除
     *
     * @param id ID
     * @return success/false
     */
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable String id) {
        return R.ok(sysLogService.removeById(id));
    }
}
