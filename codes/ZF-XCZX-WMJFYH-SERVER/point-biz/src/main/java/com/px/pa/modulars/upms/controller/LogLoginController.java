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
import com.px.pa.modulars.upms.entity.SysLogLogin;
import com.px.pa.modulars.upms.service.SysLogLoginService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * @author 品讯科技
 * @date 2024-08
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/loglogin")
@Api(value = "loglogin", tags = "loglogin")
public class LogLoginController {

    private final SysLogLoginService sysLogLoginService;

    /**
     * 分页log 信息
     *
     * @param sysLogLogin 参数集
     * @return log集合
     */
    @GetMapping("/page")
    public R log(Page page, SysLogLogin sysLogLogin) {
        return R.ok(sysLogLoginService.lambdaQuery()
                .eq(StringUtils.isNotEmpty(sysLogLogin.getType()), SysLogLogin::getType, sysLogLogin.getType())
                .eq(StringUtils.isNotEmpty(sysLogLogin.getDid()), SysLogLogin::getDid, sysLogLogin.getDid())
                .eq(StringUtils.isNotEmpty(sysLogLogin.getCdid()), SysLogLogin::getCdid, sysLogLogin.getCdid())
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
        return R.ok(sysLogLoginService.removeById(id));
    }
}
