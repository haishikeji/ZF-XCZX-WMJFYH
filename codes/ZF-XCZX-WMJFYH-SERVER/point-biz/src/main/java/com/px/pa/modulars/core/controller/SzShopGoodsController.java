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

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pig4cloud.pig.common.core.util.R;
import com.px.basic.alone.security.util.SecurityUtils;
import com.px.pa.modulars.core.entity.SzShopGoods;
import com.px.pa.modulars.core.mapper.SzShopGoodsMapper;
import com.px.pa.modulars.core.service.SzShopGoodsService;
import com.px.pa.modulars.upms.entity.SysUser;
import com.px.pa.modulars.upms.entity.SysUserRole;
import com.px.pa.modulars.upms.service.SysDeptService;
import com.px.pa.modulars.upms.service.SysUserRoleService;
import com.px.pa.modulars.upms.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


/**
 * 积分商品
 *
 * @author 品讯科技
 * @date 2024-08
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/szshopgoods")
@Api(value = "szshopgoods", tags = "积分商品管理")
public class SzShopGoodsController {

    private final SzShopGoodsService szShopGoodsService;
    private final SzShopGoodsMapper szShopGoodsMapper;
    private final SysUserService sysUserService;
    private final SysUserRoleService userRoleService;
    private final SysDeptService sysDeptService;

    /**
     * 分页查询
     *
     * @param page        分页对象
     * @param szShopGoods 积分商品
     * @return
     */
    @ApiOperation(value = "分页查询", notes = "分页查询")
    @GetMapping("/page")
    @PreAuthorize("@pms.hasPermission('core_szshopgoods_get')")
    public R getSzShopGoodsPage(Page page, SzShopGoods szShopGoods) {
        /*szShopGoodsService.lambdaQuery()
                .select(SzShopGoods::getId,SzShopGoods::getName,SzShopGoods::getImage,SzShopGoods::getState,
                        SzShopGoods::getSort,SzShopGoods::getCid,SzShopGoods::getStock,SzShopGoods::getCreateTime,
                        SzShopGoods::getPoints,SzShopGoods::getViews,SzShopGoods::getBuys,SzShopGoods::getFinish,SzShopGoods::getShopId)
                .eq(szShopGoods.getState()!=null, SzShopGoods::getState,szShopGoods.getState())
                .eq(szShopGoods.getCid()!=null, SzShopGoods::getCid,szShopGoods.getCid())
                .like(StrUtil.isNotEmpty(szShopGoods.getName()),SzShopGoods::getName,szShopGoods.getName())
                .eq(szShopGoods.getShopId()!=null, SzShopGoods::getShopId,szShopGoods.getShopId())
                .orderByDesc(SzShopGoods::getCreateTime)
                .page(page)*/
        SysUser sysUser = sysUserService.getById(SecurityUtils.getUser().getId());
        List<Integer> permission = new ArrayList<>();
        Integer roleId = userRoleService.lambdaQuery().eq(SysUserRole::getUserId, sysUser.getUserId()).select(SysUserRole::getRoleId).one().getRoleId();
        if (roleId != 1 && sysUser.getDeptId() != null) { //村干部区域权限
            if (sysUser.getDeptId().equals(sysDeptService.getpid(sysUser.getDeptId()))) {
                permission = sysDeptService.getidsbypid(sysUser.getDeptId());
            } else {
                permission.add(sysUser.getDeptId());
            }
        } else {
            permission = null;
        }
        QueryWrapper<SzShopGoods> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("s.del_flag", "0");
        queryWrapper.eq(szShopGoods.getShopId() != null, "s.shop_id", szShopGoods.getShopId());
        queryWrapper.like(StrUtil.isNotBlank(szShopGoods.getName()), "s.name", szShopGoods.getName());
        List<Integer> finalPermission = permission;
        queryWrapper.and(finalPermission != null && permission.size() > 0, wrapper -> wrapper.in("u.cdid", finalPermission)
                .or(wrapper1 -> wrapper1.in("u.did", finalPermission)));
        queryWrapper.orderByAsc("s.sort");
        queryWrapper.orderByDesc("s.update_time", "s.create_time");
        Page<SzShopGoods> page1 = this.szShopGoodsMapper.selectPage(page, queryWrapper);
        return R.ok(page1);
    }


    /**
     * 通过id查询积分商品
     *
     * @param id id
     * @return R
     */
    @ApiOperation(value = "通过id查询", notes = "通过id查询")
    @GetMapping("/{id}")
    @PreAuthorize("@pms.hasPermission('core_szshopgoods_get')")
    public R getById(@PathVariable("id") Integer id) {
        return R.ok(szShopGoodsService.getById(id));
    }

    /**
     * 新增积分商品
     *
     * @param szShopGoods 积分商品
     * @return R
     */
    @ApiOperation(value = "新增积分商品", notes = "新增积分商品")
    @PostMapping
    @PreAuthorize("@pms.hasPermission('core_szshopgoods_add')")
    public R save(@RequestBody SzShopGoods szShopGoods) {
        Integer userid = SecurityUtils.getUser().getId();
        szShopGoods.setCreateBy(userid);
        return R.ok(szShopGoodsService.save(szShopGoods));
    }

    /**
     * 修改积分商品
     *
     * @param szShopGoods 积分商品
     * @return R
     */
    @ApiOperation(value = "修改积分商品", notes = "修改积分商品")
    @PutMapping
    @PreAuthorize("@pms.hasPermission('core_szshopgoods_edit')")
    public R updateById(@RequestBody SzShopGoods szShopGoods) {
        return R.ok(szShopGoodsService.updateById(szShopGoods));
    }

    /**
     * 通过id删除积分商品
     *
     * @param id id
     * @return R
     */
    @ApiOperation(value = "通过id删除积分商品", notes = "通过id删除积分商品")
    @DeleteMapping("/{id}")
    @PreAuthorize("@pms.hasPermission('core_szshopgoods_del')")
    public R removeById(@PathVariable Integer id) {
        return R.ok(szShopGoodsService.removeById(id));
    }

}
