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

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pig4cloud.pig.common.core.util.R;
import com.px.basic.alone.security.util.SecurityUtils;
import com.px.pa.modulars.core.entity.SzFinaceBanner;
import com.px.pa.modulars.core.service.SzFinanceBannerService;
import com.px.pa.modulars.core.service.SzFinanceCateService;
import com.px.pa.modulars.core.service.SzFinanceService;
import com.px.pa.modulars.upms.service.SysUserRoleService;
import com.px.pa.modulars.upms.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


/**
 * 金融超市_轮播图
 *
 * @author 品讯科技
 * @date 2024-08
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/financebanner")
@Api(value = "finance", tags = "金融超市_轮播图管理")
public class SzFinanceBannerController {

    private final SzFinanceService szFinanceService;
    private final SzFinanceCateService szFinanceCateService;
    private final SzFinanceBannerService szFinanceBannerService;
    private final SysUserService sysUserService;
    private final SysUserRoleService userRoleService;

    /**
     * 分页查询
     *
     * @param page           分页对象
     * @param szFinaceBanner
     * @return
     */
    @ApiOperation(value = "分页查询", notes = "分页查询")
    @GetMapping("/page")
    public R getSzVotePage(Page page, SzFinaceBanner szFinaceBanner) {
        /*SysUser sysUser = sysUserService.getById(SecurityUtils.getUser().getId());
        Integer permission;
        Integer roleId = userRoleService.lambdaQuery().eq(SysUserRole::getUserId,sysUser.getUserId()).select(SysUserRole::getRoleId).one().getRoleId();
        if(roleId != 1 && sysUser.getDeptId() != null){ //村干部区域权限
            permission = sysUser.getDeptId();
        }else{
            permission = null;
        }*/
        LocalDateTime LocalTime1 = null;
        LocalDateTime LocalTime2 = null;
        if (szFinaceBanner.getStartTime() != null && szFinaceBanner.getEndTime() != null) {
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalTime1 = LocalDateTime.parse(szFinaceBanner.getStartTime(), df);
            LocalTime2 = LocalDateTime.parse(szFinaceBanner.getEndTime(), df);
        }
        Page<SzFinaceBanner> szFinaceBannerPage = szFinanceBannerService.lambdaQuery()
                .eq(SzFinaceBanner::getDelFlag, 0)
                .eq(szFinaceBanner.getCate() != null, SzFinaceBanner::getCate, szFinaceBanner.getCate())
                .ge(LocalTime1 != null, SzFinaceBanner::getCreateTime, LocalTime1)
                .le(LocalTime2 != null, SzFinaceBanner::getCreateTime, LocalTime2)
                .like(StringUtils.isNotEmpty(szFinaceBanner.getTitle()), SzFinaceBanner::getTitle, szFinaceBanner.getTitle())
                .orderByDesc(SzFinaceBanner::getCreateTime)
                .page(page);


        return R.ok(szFinaceBannerPage);
    }


    /**
     * 通过id查询
     *
     * @param id id
     * @return R
     */
    @ApiOperation(value = "通过id查询", notes = "通过id查询")
    @GetMapping("/{id}")
    public R getById(@PathVariable("id") Integer id) {
        return R.ok(szFinanceBannerService.getById(id));
    }

    /**
     * 新增
     *
     * @param szFinaceBanner
     * @return R
     */
    @ApiOperation(value = "新增", notes = "新增")
    @PostMapping
    public R save(@RequestBody SzFinaceBanner szFinaceBanner) {
        System.out.println(szFinaceBanner);
        Integer userid = SecurityUtils.getUser().getId();
        szFinaceBanner.setCreateBy(userid);
        return R.ok(szFinanceBannerService.save(szFinaceBanner));
    }

    /**
     * 修改
     *
     * @param szFinaceBanner
     * @return R
     */
    @ApiOperation(value = "修改", notes = "修改")
    @PutMapping
    public R updateById(@RequestBody SzFinaceBanner szFinaceBanner) {
        return R.ok(szFinanceBannerService.updateById(szFinaceBanner));
    }

    /**
     * 通过id删除
     *
     * @param id id
     * @return R
     */
    @ApiOperation(value = "通过id删除", notes = "通过id删除")
    @DeleteMapping("/{id}")
    public R removeById(@PathVariable Integer id) {
        return R.ok(szFinanceBannerService.removeById(id));
    }

}
