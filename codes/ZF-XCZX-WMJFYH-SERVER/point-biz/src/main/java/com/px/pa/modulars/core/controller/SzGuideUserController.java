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
import com.px.pa.modulars.core.entity.SzGuideUser;
import com.px.pa.modulars.core.service.SzGuideUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


/**
 * 办事指南审核人列表
 *
 * @author 品讯科技
 * @date 2024-08
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/szguideuser")
@Api(value = "szguideuser", tags = "办事指南审核人列表管理")
public class SzGuideUserController {

    private final SzGuideUserService szGuideUserService;

    /**
     * 分页查询
     *
     * @param page        分页对象
     * @param szGuideUser 办事指南审核人列表
     * @return
     */
    @ApiOperation(value = "分页查询", notes = "分页查询")
    @GetMapping("/page")
    @PreAuthorize("@pms.hasPermission('core_szguideuser_get')")
    public R getSzGuideUserPage(Page page, SzGuideUser szGuideUser) {
        return R.ok(szGuideUserService.page(page, Wrappers.query(szGuideUser)));
    }


    /**
     * 通过id查询办事指南审核人列表
     *
     * @param id id
     * @return R
     */
    @ApiOperation(value = "通过id查询", notes = "通过id查询")
    @GetMapping("/{id}")
    @PreAuthorize("@pms.hasPermission('core_szguideuser_get')")
    public R getById(@PathVariable("id") Integer id) {
        return R.ok(szGuideUserService.getById(id));
    }

    /**
     * 新增办事指南审核人列表
     *
     * @param szGuideUser 办事指南审核人列表
     * @return R
     */
    @ApiOperation(value = "新增办事指南审核人列表", notes = "新增办事指南审核人列表")
    @PostMapping
    @PreAuthorize("@pms.hasPermission('core_szguideuser_add')")
    public R save(@RequestBody SzGuideUser szGuideUser) {
        Integer userid = SecurityUtils.getUser().getId();
        szGuideUser.setCreateBy(userid);
        return R.ok(szGuideUserService.save(szGuideUser));
    }

    /**
     * 修改办事指南审核人列表
     *
     * @param szGuideUser 办事指南审核人列表
     * @return R
     */
    @ApiOperation(value = "修改办事指南审核人列表", notes = "修改办事指南审核人列表")
    @PutMapping
    @PreAuthorize("@pms.hasPermission('core_szguideuser_edit')")
    public R updateById(@RequestBody SzGuideUser szGuideUser) {
        return R.ok(szGuideUserService.updateById(szGuideUser));
    }

    /**
     * 通过id删除办事指南审核人列表
     *
     * @param id id
     * @return R
     */
    @ApiOperation(value = "通过id删除办事指南审核人列表", notes = "通过id删除办事指南审核人列表")
    @DeleteMapping("/{id}")
    @PreAuthorize("@pms.hasPermission('core_szguideuser_del')")
    public R removeById(@PathVariable Integer id) {
        return R.ok(szGuideUserService.removeById(id));
    }

}
