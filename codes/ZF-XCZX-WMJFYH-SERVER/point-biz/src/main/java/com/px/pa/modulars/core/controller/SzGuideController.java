/*
 *    Copyright (c) 2018-2025, lengleng All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * Neither the name of the pig4cloud.com developer nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * @author 品讯科技
 */

package com.px.pa.modulars.core.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pig4cloud.pig.common.core.util.R;
import com.px.basic.alone.security.util.SecurityUtils;
import com.px.pa.modulars.core.entity.SzGuide;
import com.px.pa.modulars.core.service.SzGuideService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


/**
 * 办事指南
 *
 * @author 品讯科技
 * @date 2024-08
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/szguide")
@Api(value = "szguide", tags = "办事指南管理")
public class SzGuideController {

    private final SzGuideService szGuideService;

    /**
     * 分页查询
     *
     * @param page    分页对象
     * @param szGuide 办事指南
     * @return
     */
    @ApiOperation(value = "分页查询", notes = "分页查询")
    @GetMapping("/page")
    @PreAuthorize("@pms.hasPermission('core_szguide_get')")
    public R getSzGuidePage(Page page, SzGuide szGuide) {
        return R.ok(szGuideService.page(page, Wrappers.query(szGuide)));
    }


    /**
     * 通过id查询办事指南
     *
     * @param id id
     * @return R
     */
    @ApiOperation(value = "通过id查询", notes = "通过id查询")
    @GetMapping("/{id}")
    @PreAuthorize("@pms.hasPermission('core_szguide_get')")
    public R getById(@PathVariable("id") Integer id) {
        return R.ok(szGuideService.getById(id));
    }

    /**
     * 新增办事指南
     *
     * @param szGuide 办事指南
     * @return R
     */
    @ApiOperation(value = "新增办事指南", notes = "新增办事指南")
    @PostMapping
    @PreAuthorize("@pms.hasPermission('core_szguide_add')")
    public R save(@RequestBody SzGuide szGuide) {
        Integer userid = SecurityUtils.getUser().getId();
        szGuide.setCreateBy(userid);
        return R.ok(szGuideService.save(szGuide));
    }

    /**
     * 修改办事指南
     *
     * @param szGuide 办事指南
     * @return R
     */
    @ApiOperation(value = "修改办事指南", notes = "修改办事指南")
    @PutMapping
    @PreAuthorize("@pms.hasPermission('core_szguide_edit')")
    public R updateById(@RequestBody SzGuide szGuide) {
        return R.ok(szGuideService.updateById(szGuide));
    }

    /**
     * 通过id删除办事指南
     *
     * @param id id
     * @return R
     */
    @ApiOperation(value = "通过id删除办事指南", notes = "通过id删除办事指南")
    @DeleteMapping("/{id}")
    @PreAuthorize("@pms.hasPermission('core_szguide_del')")
    public R removeById(@PathVariable Integer id) {
        return R.ok(szGuideService.removeById(id));
    }

}
