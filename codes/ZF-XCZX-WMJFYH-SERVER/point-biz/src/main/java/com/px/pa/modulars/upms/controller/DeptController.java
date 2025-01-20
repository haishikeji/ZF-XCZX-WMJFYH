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

import cn.binarywang.wx.miniapp.api.WxMaService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pig4cloud.pig.common.core.util.R;
import com.px.basic.alone.security.annotation.Inner;
import com.px.basic.alone.security.util.SecurityUtils;
import com.px.fastfile.service.FastfileService;
import com.px.pa.api.home.service.HomeApiService;
import com.px.pa.modulars.upms.dto.DeptTree;
import com.px.pa.modulars.upms.dto.TreeNode;
import com.px.pa.modulars.upms.entity.SysDept;
import com.px.pa.modulars.upms.entity.SysUser;
import com.px.pa.modulars.upms.entity.SysUserRole;
import com.px.pa.modulars.upms.service.SysDeptService;
import com.px.pa.modulars.upms.service.SysUserRoleService;
import com.px.pa.modulars.upms.service.SysUserService;
import com.px.pa.modulars.upms.vo.TreeUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 部门管理 前端控制器
 * </p>
 *
 * @author 品讯科技
 * @since 2019/2/1
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/dept")
@Api(value = "dept", tags = "部门管理模块")
public class DeptController {

    private final SysDeptService sysDeptService;
    private final HomeApiService homeApiService;
    private final WxMaService wxMaService;
    private final FastfileService fastfileService;
    private final SysUserService sysUserService;
    private final SysUserRoleService userRoleService;
    @Value("${file.page1}")
    private String page0;
    @Value("${file.temporary}")
    private String temporary;

    /**
     * 通过ID查询
     *
     * @param id ID
     * @return SysDept
     */
    @GetMapping("/{id}")
    public R getById(@PathVariable Integer id) {
        return R.ok(sysDeptService.getById(id));
    }

    /**
     * 返回树形菜单集合
     *
     * @return 树形菜单
     */
    @GetMapping(value = "/tree")
    public R listDeptTrees() {
        return R.ok(sysDeptService.listDeptTrees());
    }

    @GetMapping(value = "/tree/cc")
    public R listDeptTreesByCc() {
        List<DeptTree> list = sysDeptService.listDeptTrees();
        List<DeptTree> trees = TreeUtil.getTreeByLevelUp(list, 2);
        return R.ok(trees);
    }

    @ApiOperation(value = "(根据cdid和did)树", notes = "(根据cdid和code)树")
    @GetMapping("/getdidtreesbydepyid")
    public R getdidtreesbydepyid(Integer id) {
        SysDept sysDept = sysDeptService.getById(id);
        List<DeptTree> result = new ArrayList<>();
        if (sysDept == null) {
            return R.ok(result);
        } else if (sysDept.getParentId() == 0) {
            return R.ok(sysDeptService.getVillagesTree(id));
        } else if (sysDept.getParentId() == 1 || sysDept.getParentId() == 700 || sysDept.getParentId() == 701 || sysDept.getParentId() == 702) {
            result = (sysDeptService.getZAreas(Collections.singletonList(sysDept.getDeptId()), null, sysDept.getParentId()));
        } else {
            result = (sysDeptService.getAreas(Collections.singletonList(1), sysDept.getParentId()));
            for (DeptTree deptTree : result) {
                if (deptTree.getId() == sysDept.getParentId()) {
                    List<TreeNode> children = new ArrayList<>();
                    DeptTree treeNode = new DeptTree();
                    treeNode.setId(sysDept.getDeptId());
                    treeNode.setParentId(sysDept.getParentId());
                    treeNode.setName(sysDept.getName());
                    children.add(treeNode);
                    deptTree.setChildren(children);
                    break;
                }
            }
        }
        return R.ok(result);
    }

    @ApiOperation(value = "(根据cdid和did)树", notes = "(根据cdid和code)树")
    @GetMapping("/getdidtrees")
    public R getdidtrees() {
        SysUser sysUser = sysUserService.getById(SecurityUtils.getUser().getId());
        Integer roleId = userRoleService.lambdaQuery().eq(SysUserRole::getUserId, sysUser.getUserId()).select(SysUserRole::getRoleId).one().getRoleId();
        Integer deptId = sysUser.getDeptId();
        SysDept sysDept = sysDeptService.getById(deptId);
        Integer did = 0;
        Integer deptid = 1;
        if (sysDept != null) {
            did = sysDept.getParentId();
            deptid = sysDept.getDeptId();
        }
        if (did == 0) {
            DeptTree deptTree = new DeptTree();
            deptTree.setName(sysDept.getName());
            deptTree.setId(sysDept.getDeptId());
            deptTree.setParentId(sysDept.getParentId());
            List<DeptTree> deptTrees = sysDeptService.getVillagesTree(deptid);
            /*deptTrees.stream()
                    .map(entity1 -> new TreeNode(entity1.getName(), entity1.getId(),entity1.getParentId(),entity1.getChildren()))
                    .collect(Collectors.toList());*/
            List<TreeNode> treeNodes = new ArrayList<>();
            /* deptTree.setChildren(treeNodes);*/
            List<DeptTree> list = TreeUtil.addChildren(deptTrees, deptTree);
            return R.ok(list);
        }
        List<DeptTree> result = new ArrayList<>();
        if (did == 1 || did == 700 || did == 701 || did == 702) {
            SysDept sysdept = sysDeptService.getById(did);
            DeptTree deptTree1 = new DeptTree();
            BeanUtils.copyProperties(sysdept, deptTree1);
            if (roleId != null && roleId != 1 && StringUtils.isNotEmpty(sysUser.getPermission())) {
                String[] strings = sysUser.getPermission().split(",");
                List<SysDept> sysDepts = sysDeptService.lambdaQuery()
                        .eq(SysDept::getDelFlag, 0)
                        .apply("dept_id in (SELECT DISTINCT parent_id FROM sys_dept where dept_id in ( " + sysUser.getPermission() + "))")
                        .list();
                List<Integer> ids = new ArrayList<>();
                for (SysDept dept : sysDepts) {
                    ids.add(dept.getDeptId());
                }
                /*
                String[] dept = sysUser.getPermission().split(",");
                */
                List<DeptTree> list = (sysDeptService.getZAreas(ids, deptId, did));
                for (DeptTree deptTree : list) {
                    List<TreeNode> children = new ArrayList<>();
                    List<SysDept> sysDepts1 = sysDeptService.lambdaQuery()
                            .eq(SysDept::getDelFlag, 0)
                            .eq(SysDept::getParentId, deptTree.getId())
                            .in(SysDept::getDeptId, strings)
                            .list();
                    for (SysDept sysDept1 : sysDepts1) {
                        DeptTree treeNode = new DeptTree();
                        treeNode.setId(sysDept1.getDeptId());
                        treeNode.setParentId(did);
                        treeNode.setName(sysDept1.getName());
                        children.add(treeNode);
                        deptTree.setChildren(children);
                    }
                }
                result = TreeUtil.addChildren(list, deptTree1);
            } else {
                result = (sysDeptService.getZAreas(Collections.singletonList(deptId), null, sysDeptService.getpid(deptId)));
            }
        } else {
            SysDept sysdept = sysDeptService.getById(sysDeptService.getpid(did));
            DeptTree deptTree1 = new DeptTree();
            BeanUtils.copyProperties(sysdept, deptTree1);
            List<DeptTree> list = (sysDeptService.getZAreas(Collections.singletonList(sysDeptService.getpid(did)), did, sysDeptService.getpid(did)));
            for (DeptTree deptTree : list) {
                if (deptTree.getId() == did) {
                    List<TreeNode> children = new ArrayList<>();
                    DeptTree treeNode = new DeptTree();
                    treeNode.setId(deptId);
                    treeNode.setParentId(did);
                    treeNode.setName(sysDept.getName());
                    children.add(treeNode);
                    deptTree.setChildren(children);
                    break;
                }
            }
            result = TreeUtil.addChildren(list, deptTree1);
        }
        return R.ok(result);
    }

    /**
     * 返回当前用户树形菜单集合
     *
     * @return 树形菜单
     */
    @GetMapping(value = "/user-tree")
    public R listCurrentUserDeptTrees() {
        return R.ok(sysDeptService.listCurrentUserDeptTrees());
    }

    /**
     * 添加
     *
     * @param sysDept 实体
     * @return success/false
     */
//	@SysLog("添加部门")
    @PostMapping
    //@PreAuthorize("@pms.hasPermission('sys_dept_add')")
    public R save(@Valid @RequestBody SysDept sysDept) {
        return R.ok(sysDeptService.saveDept(sysDept));
    }

    /**
     * 删除
     *
     * @param id ID
     * @return success/false
     */
//	@SysLog("删除部门")
    @DeleteMapping("/{id}")
    //@PreAuthorize("@pms.hasPermission('sys_dept_del')")
    public R removeById(@PathVariable Integer id) {
        return R.ok(sysDeptService.removeDeptById(id));
    }

    /**
     * 编辑
     *
     * @param sysDept 实体
     * @return success/false
     */
//	@SysLog("编辑部门")
    @PutMapping
    //@PreAuthorize("@pms.hasPermission('sys_dept_edit')")
    public R update(@Valid @RequestBody SysDept sysDept) {
        sysDept.setUpdateTime(LocalDateTime.now());
        return R.ok(sysDeptService.updateDeptById(sysDept));
    }

    /**
     * 根据部门名查询部门信息
     *
     * @param deptname 部门名
     * @return
     */
    @GetMapping("/details/{deptname}")
    public R user(@PathVariable String deptname) {
        SysDept condition = new SysDept();
        condition.setName(deptname);
        return R.ok(sysDeptService.getOne(new QueryWrapper<>(condition)));
    }

    @GetMapping("/generatemanzhuang")
    @Inner(false)
    @ApiOperation("生成村庄二维码")
    public R generatemanzhuang(Integer cdid) throws Exception {
        SysDept sysDept = sysDeptService.getById(cdid);
        if (sysDept == null) {
            return R.result(0, "村庄不存在");
        }
        String json = sysDept.getParentId() + "aa" + cdid;
        /*Map map = new HashMap();
        map.put("cdid",cdid);
        map.put("did",sysDept.getParentId());
        Gson gson = new Gson();
        String json = gson.toJson(map);*/
        File file = wxMaService.getQrcodeService().createWxaCodeUnlimit(json, page0, temporary);
        String url = null;
        try {
            Map<String, Object> upResult = this.fastfileService.uploadFile("szrj-1256675456", file);
            url = (String) upResult.get("kpath");
        } catch (Exception e) {
            R.result(0, "上传二维码异常");
        }
        sysDept.setQrCode(url);
        sysDeptService.updateById(sysDept);
        return R.ok(url);
    }

    @GetMapping(value = "/getpid")
    @Inner(false)
    @ApiOperation("获取pid")
    public R getpid(Integer id) {
        return R.ok(sysDeptService.getpid(id));
    }
}
