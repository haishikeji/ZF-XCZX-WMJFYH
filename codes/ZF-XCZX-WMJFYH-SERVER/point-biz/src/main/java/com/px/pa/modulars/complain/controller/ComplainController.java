package com.px.pa.modulars.complain.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pig4cloud.pig.common.core.util.R;
import com.px.basic.alone.security.util.SecurityUtils;
import com.px.pa.modulars.complain.entity.Complain;
import com.px.pa.modulars.complain.service.ComplainService;
import com.px.pa.modulars.upms.entity.SysDept;
import com.px.pa.modulars.upms.entity.SysDeptSon;
import com.px.pa.modulars.upms.entity.SysUser;
import com.px.pa.modulars.upms.service.SysDeptService;
import com.px.pa.modulars.upms.service.SysDeptSonService;
import com.px.pa.modulars.upms.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * 投诉信息
 *
 * @author 品讯科技
 * @date 2024-08
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/complain")
@Api(value = "complain", tags = "投诉信息管理")
public class ComplainController {

    private final ComplainService complainService;
    @Autowired
    private SysDeptService sysDeptService;
    @Autowired
    private SysDeptSonService sysDeptSonService;
    @Autowired
    private SysUserService sysUserService;

    /**
     * 分页查询
     *
     * @param page     分页对象
     * @param complain 投诉信息
     * @return
     */
    @ApiOperation(value = "分页查询", notes = "分页查询")
    @GetMapping("/page")
    @PreAuthorize("@pms.hasPermission('complain_get')")
    public R getcomplainPage(Page page, Complain complain) {
        Integer userId = SecurityUtils.getUser().getId();
        SysUser user = sysUserService.getById(userId);
        Integer pid = sysDeptService.getpid(user.getDeptId());
        List<Integer> deptIds = new ArrayList<>();
        if (!Objects.equals(pid, user.getDeptId())) {
            SysDept sysDept = sysDeptService.getById(user.getDeptId());
            if (Objects.equals(pid, sysDept.getParentId())) {
                List<SysDept> sysDepts = sysDeptService.lambdaQuery()
                        .select(SysDept::getDeptId)
                        .eq(SysDept::getParentId, user.getDeptId())
                        .eq(SysDept::getDelFlag, 0)
                        .list();
                deptIds = sysDepts.stream().map(SysDept::getDeptId).collect(Collectors.toList());
            } else {
                complain.setDid(sysDept.getDeptId());
            }
        }
        Page<Complain> complainPage = complainService.lambdaQuery()
                .in(!deptIds.isEmpty(), Complain::getDid, deptIds)
                .eq(complain.getDid() != null, Complain::getDid, complain.getDid())
                .eq(complain.getDsid() != null, Complain::getDsid, complain.getDsid())
                .eq(complain.getIsAccepted() != null, Complain::getIsAccepted, complain.getIsAccepted())
                .eq(complain.getIsRealName() != null, Complain::getIsRealName, complain.getIsRealName())
                .eq(complain.getType() != null, Complain::getType, complain.getType())
                .like(StrUtil.isNotEmpty(complain.getName()), Complain::getName, complain.getName())
                .like(StrUtil.isNotEmpty(complain.getPhone()), Complain::getPhone, complain.getPhone())
                .like(StrUtil.isNotEmpty(complain.getContent()), Complain::getContent, complain.getContent())
                .like(StrUtil.isNotEmpty(complain.getAddress()), Complain::getAddress, complain.getAddress())
                .like(StrUtil.isNotEmpty(complain.getReturnContent()), Complain::getReturnContent, complain.getReturnContent())
                .eq(Complain::getDelFlag, 0)
                .eq(complain.getState() != null, Complain::getState, complain.getState())
                .orderByAsc(Complain::getSort)
                .page(page);
        for (Complain record : complainPage.getRecords()) {
            if (record.getDid() != null) {
                SysDept sysDept = sysDeptService.getById(record.getDid());
                if (sysDept != null) {
                    record.setDname(sysDept.getName());
                }
            }
            if (record.getDsid() != null) {
                SysDeptSon son = sysDeptSonService.getById(record.getDsid());
                if (son != null) {
                    record.setDsname(son.getName());
                }
            }
        }
        return R.ok(complainPage);
    }


    /**
     * 通过id查询投诉信息
     *
     * @param id id
     * @return R
     */
    @ApiOperation(value = "通过id查询", notes = "通过id查询")
    @GetMapping("/{id}")
    @PreAuthorize("@pms.hasPermission('complain_get')")
    public R getById(@PathVariable("id") Integer id) {
        Complain complain = complainService.getById(id);
        if (complain.getDid() != null) {
            SysDept sysDept = sysDeptService.getById(complain.getDid());
            if (sysDept != null) {
                complain.setDname(sysDept.getName());
            }
        }
        if (complain.getDsid() != null) {
            SysDeptSon son = sysDeptSonService.getById(complain.getDsid());
            if (son != null) {
                complain.setDsname(son.getName());
            }
        }
        return R.ok(complain);
    }

    /**
     * 新增投诉信息
     *
     * @param complain 投诉信息
     * @return R
     */
    @ApiOperation(value = "新增投诉信息", notes = "新增投诉信息")
    @PostMapping
    @PreAuthorize("@pms.hasPermission('complain_add')")
    public R save(@Validated @RequestBody Complain complain) {
        Integer userid = SecurityUtils.getUser().getId();
        complain.setCreateBy(userid);
        complain.setDelFlag("0");
        complain.setState(1);
        complain.setCreateTime(LocalDateTime.now());
        return R.ok(complainService.save(complain));
    }

    /**
     * 修改投诉信息
     *
     * @param complain 投诉信息
     * @return R
     */
    @ApiOperation(value = "修改投诉信息", notes = "修改投诉信息")
    @PutMapping
    @PreAuthorize("@pms.hasPermission('complain_edit')")
    public R updateById(@Validated @RequestBody Complain complain) {
        complain.setUpdateTime(LocalDateTime.now());
        return R.ok(complainService.updateById(complain));
    }

    /**
     * 通过id删除投诉信息
     *
     * @param id id
     * @return R
     */
    @ApiOperation(value = "通过id删除投诉信息", notes = "通过id删除投诉信息")
    @DeleteMapping("/{id}")
    @PreAuthorize("@pms.hasPermission('complain_del')")
    public R removeById(@PathVariable Integer id) {
        return R.ok(complainService.removeById(id));
    }

}
