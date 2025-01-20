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

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pig4cloud.pig.common.core.util.R;
import com.px.basic.alone.security.annotation.Inner;
import com.px.basic.alone.security.util.SecurityUtils;
import com.px.pa.modulars.core.entity.SzManUser;
import com.px.pa.modulars.core.service.SzManUserService;
import com.px.pa.modulars.upms.dto.UserDTO;
import com.px.pa.modulars.upms.entity.SysDept;
import com.px.pa.modulars.upms.entity.SysDeptSon;
import com.px.pa.modulars.upms.entity.SysUser;
import com.px.pa.modulars.upms.mapper.SysUserMapper;
import com.px.pa.modulars.upms.service.SysDeptService;
import com.px.pa.modulars.upms.service.SysDeptSonService;
import com.px.pa.modulars.upms.service.SysUserService;
import com.px.pa.modulars.upms.vo.UserVO;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author 品讯科技
 * @date 2024-08
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Api(value = "user", tags = "用户管理模块")
public class UserController {

    private final SysUserService userService;
    private final SysUserMapper userMapper;
    private final SysDeptService sysDeptService;
    private final SysDeptSonService sysDeptSonService;
    private final SzManUserService szUserService;

    /**
     * 获取当前用户全部信息
     *
     * @return 用户信息
     */
    @GetMapping(value = {"/info"})
    public R info() {
        String username = SecurityUtils.getUser().getUsername();
        SysUser user = userService.getOne(Wrappers.<SysUser>query().lambda().eq(SysUser::getUsername, username));
        if (user == null) {
            return R.result(401, "获取当前用户信息失败");
        }
        return R.ok(userService.getUserInfo(user));
    }

    /**
     * 获取指定用户全部信息
     *
     * @return 用户信息
     */
    @Inner
    @GetMapping("/info/{username}")
    public R info(@PathVariable String username) {
        SysUser user = userService.getOne(Wrappers.<SysUser>query().lambda().eq(SysUser::getUsername, username));
        if (user == null) {
            return R.result(401, String.format("用户信息为空 %s", username));
        }
        return R.ok(userService.getUserInfo(user));
    }

    /**
     * 通过ID查询用户信息
     *
     * @param id ID
     * @return 用户信息
     */
    @GetMapping("/{id}")
    public R user(@PathVariable Integer id) {
        return R.ok(userService.getUserVoById(id));
    }

    /**
     * 根据用户名查询用户信息
     *
     * @param username 用户名
     * @return
     */
    @GetMapping("/details/{username}")
    public R user(@PathVariable String username) {
        SysUser condition = new SysUser();
        condition.setUsername(username);
        return R.ok(userService.getOne(new QueryWrapper<>(condition)));
    }

    @GetMapping("/details1/{username}")
    public R user1(@PathVariable String username) {
        SysUser condition = new SysUser();
        condition.setName(username);
        return R.ok(userService.getOne(new QueryWrapper<>(condition)));
    }

    /**
     * 删除用户信息
     *
     * @param id ID
     * @return R
     */
//	@SysLog("删除用户信息")
    @DeleteMapping("/{id}")
    @PreAuthorize("@pms.hasPermission('sys_user_del')")
    public R userDel(@PathVariable Integer id) {
        SysUser sysUser = userService.getById(id);
        return R.ok(userService.removeUserById(sysUser));
    }

    /**
     * 添加用户
     *
     * @param userDto 用户信息
     * @return success/false
     */
//	@SysLog("添加用户")
    @PostMapping
    @PreAuthorize("@pms.hasPermission('sys_user_add')")
    public R user(@RequestBody UserDTO userDto) {
        return R.ok(userService.saveUser(userDto));
    }

    /**
     * 更新用户信息
     *
     * @param userDto 用户信息
     * @return R
     */
//	@SysLog("更新用户信息")
    @PutMapping
    @PreAuthorize("@pms.hasPermission('sys_user_edit')")
    public R updateUser(@Valid @RequestBody UserDTO userDto) {
        if (userDto.getMid() == null) {
            // 创建一个 UpdateWrapper 对象，用于构建更新条件
            UpdateWrapper<SysUser> updateWrapper = new UpdateWrapper<>();
            // 设置更新条件，例如：根据 id 更新
            updateWrapper.eq("user_id", userDto.getUserId());
            // 设置需要更新的字段为 null
            updateWrapper.set("mid", null);
            //updateWrapper.set("sid", null);
            // 执行更新操作
            userMapper.update(null, updateWrapper);
        }
        if (userDto.getSid() == null) {
            // 创建一个 UpdateWrapper 对象，用于构建更新条件
            UpdateWrapper<SysUser> updateWrapper = new UpdateWrapper<>();
            // 设置更新条件，例如：根据 id 更新
            updateWrapper.eq("user_id", userDto.getUserId());
            // 设置需要更新的字段为 null
            //updateWrapper.set("mid", null);
            updateWrapper.set("sid", null);
            // 执行更新操作
            userMapper.update(null, updateWrapper);
        }
        return R.ok(userService.updateUser(userDto));
    }

    @PutMapping("/permission")
    @PreAuthorize("@pms.hasPermission('sys_user_edit')")
    public R updateUserOfPermission(@Valid @RequestBody SysUser userDto) {
        SysUser user = new SysUser();
        user.setUserId(userDto.getUserId());
        user.setPermission(userDto.getPermission());
        return R.ok(userService.updateById(user));
    }

    /**
     * 分页查询用户
     *
     * @param page    参数集
     * @param userDTO 查询参数列表
     * @return 用户集合
     */
    @GetMapping("/page")
    public R getUserPage(Page page, UserDTO userDTO) {
        Page<UserVO> Page = userService.getUserWithRolePage(page, userDTO);
        for (UserVO role : Page.getRecords()) {
            Integer pid = sysDeptService.getpid(role.getDeptId());
            SysDept sysDept = sysDeptService.getById(pid);
            role.setPid(pid);
            role.setPname(sysDept.getName());
        }
        for (UserVO userVO : Page.getRecords()) {
            Integer pid = sysDeptService.getpid(userVO.getDeptId());
            SysDept sysDept = sysDeptService.getById(pid);
            userVO.setPid(pid);
            userVO.setPname(sysDept.getName());
            if (userVO.getSid() != null) {
                SysDeptSon son = sysDeptSonService.getById(userVO.getSid());
                if (son != null) {
                    userVO.setSname(son.getName());
                }
            }
            if (userVO.getMid() != null) {
                SzManUser manUser = szUserService.getById(userVO.getMid());
                if (manUser != null) {
                    userVO.setMname(manUser.getName());
                }
            }
        }
        return R.ok(Page);
    }

    /**
     * 修改个人信息
     *
     * @param userDto userDto
     * @return success/false
     */
//	@SysLog("修改个人信息")
    @PutMapping("/edit")
    public R updateUserInfo(@Valid @RequestBody UserDTO userDto) {
        if (userDto.getMid() == null) {
            // 创建一个 UpdateWrapper 对象，用于构建更新条件
            UpdateWrapper<SysUser> updateWrapper = new UpdateWrapper<>();
            // 设置更新条件，例如：根据 id 更新
            updateWrapper.eq("user_id", userDto.getUserId());
            // 设置需要更新的字段为 null
            updateWrapper.set("mid", null);
            //updateWrapper.set("sid", null);
            // 执行更新操作
            userMapper.update(null, updateWrapper);
        }
        if (userDto.getSid() == null) {
            // 创建一个 UpdateWrapper 对象，用于构建更新条件
            UpdateWrapper<SysUser> updateWrapper = new UpdateWrapper<>();
            // 设置更新条件，例如：根据 id 更新
            updateWrapper.eq("user_id", userDto.getUserId());
            // 设置需要更新的字段为 null
            //updateWrapper.set("mid", null);
            updateWrapper.set("sid", null);
            // 执行更新操作
            userMapper.update(null, updateWrapper);
        }
        return userService.updateUserInfo(userDto);
    }

    /**
     * @param username 用户名称
     * @return 上级部门用户列表
     */
    @GetMapping("/ancestor/{username}")
    public R listAncestorUsers(@PathVariable String username) {
        return R.ok(userService.listAncestorUsersByUsername(username));
    }

}
