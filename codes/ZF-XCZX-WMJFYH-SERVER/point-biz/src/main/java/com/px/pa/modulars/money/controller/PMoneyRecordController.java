package com.px.pa.modulars.money.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pig4cloud.pig.common.core.util.R;
import com.px.basic.alone.security.util.SecurityUtils;
import com.px.pa.modulars.money.entity.PMoneyRecord;
import com.px.pa.modulars.money.service.PMoneyRecordService;
import com.px.pa.modulars.resource.entity.vo.ResourceDeptVo;
import com.px.pa.modulars.upms.entity.SysDept;
import com.px.pa.modulars.upms.entity.SysDeptSon;
import com.px.pa.modulars.upms.entity.SysUser;
import com.px.pa.modulars.upms.service.SysDeptService;
import com.px.pa.modulars.upms.service.SysDeptSonService;
import com.px.pa.modulars.upms.service.SysUserService;
import com.px.pa.utils.ExcelUtil.ExcelBaseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * 资金上传记录
 *
 * @author 品讯科技
 * @date 2024-08
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/money/pmoneyrecord")
@Api(value = "pmoneyrecord", tags = "资金上传记录管理")
public class PMoneyRecordController {

    private final PMoneyRecordService pmoneyrecordService;
    @Autowired
    private SysDeptService sysDeptService;
    @Autowired
    private SysDeptSonService sysDeptSonService;
    @Autowired
    private SysUserService sysUserService;

    /**
     * 分页查询
     *
     * @param page         分页对象
     * @param pmoneyrecord 资金上传记录
     * @return
     */
    @ApiOperation(value = "分页查询", notes = "分页查询")
    @GetMapping("/page")
    @PreAuthorize("@pms.hasPermission('money_pmoneyrecord_get')")
    public R getpmoneyrecordPage(Page page, PMoneyRecord pmoneyrecord) {
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
                pmoneyrecord.setDid(sysDept.getDeptId());
            }
        }
        return R.ok(pmoneyrecordService.lambdaQuery()
                .in(!deptIds.isEmpty(), PMoneyRecord::getDid, deptIds)
                .eq(pmoneyrecord.getDid() != null, PMoneyRecord::getDid, pmoneyrecord.getDid())
                .eq(PMoneyRecord::getDelFlag, 0)
                .eq(pmoneyrecord.getState() != null, PMoneyRecord::getState, pmoneyrecord.getState())
                .orderByDesc(PMoneyRecord::getPublishTime)
                .page(page));
    }


    /**
     * 通过id查询资金上传记录
     *
     * @param id id
     * @return R
     */
    @ApiOperation(value = "通过id查询", notes = "通过id查询")
    @GetMapping("/{id}")
    @PreAuthorize("@pms.hasPermission('money_pmoneyrecord_get')")
    public R getById(@PathVariable("id") Integer id) {
        PMoneyRecord pmoneyrecord = pmoneyrecordService.getById(id);
        if (pmoneyrecord.getDid() != null) {
            SysDept sysDept = sysDeptService.getById(pmoneyrecord.getDid());
            if (sysDept != null) {
                pmoneyrecord.setDname(sysDept.getName());
            }
        }
        if (pmoneyrecord.getDsid() != null) {
            SysDeptSon son = sysDeptSonService.getById(pmoneyrecord.getDsid());
            if (son != null) {
                pmoneyrecord.setDsname(son.getName());
            }
        }
        return R.ok(pmoneyrecord);
    }

    /**
     * 新增资金上传记录
     *
     * @param pmoneyrecord 资金上传记录
     * @return R
     */
    @ApiOperation(value = "新增资金上传记录", notes = "新增资金上传记录")
    @PostMapping
    @PreAuthorize("@pms.hasPermission('money_pmoneyrecord_add')")
    public R save(@Validated @RequestBody PMoneyRecord pmoneyrecord) {
        Integer userid = SecurityUtils.getUser().getId();
        pmoneyrecord.setCreateBy(userid);
        pmoneyrecord.setCreateTime(LocalDateTime.now());
        pmoneyrecord.setState(1);
        pmoneyrecord.setDelFlag("0");
        return R.ok(pmoneyrecordService.save(pmoneyrecord));
    }

    /**
     * 修改资金上传记录
     *
     * @param pmoneyrecord 资金上传记录
     * @return R
     */
    @ApiOperation(value = "修改资金上传记录", notes = "修改资金上传记录")
    @PutMapping
    @PreAuthorize("@pms.hasPermission('money_pmoneyrecord_edit')")
    public R updateById(@Validated @RequestBody PMoneyRecord pmoneyrecord) {
        pmoneyrecord.setUpdateTime(LocalDateTime.now());
        return R.ok(pmoneyrecordService.updateById(pmoneyrecord));
    }

    /**
     * 通过id删除资金上传记录
     *
     * @param id id
     * @return R
     */
    @ApiOperation(value = "通过id删除资金上传记录", notes = "通过id删除资金上传记录")
    @DeleteMapping("/{id}")
    @PreAuthorize("@pms.hasPermission('money_pmoneyrecord_del')")
    public R removeById(@PathVariable Integer id) {
        return R.ok(pmoneyrecordService.removeById(id));
    }

    @ApiOperation(value = "查询未提交资金上传记录的村组", notes = "查询未提交资金上传记录的村组")
    @GetMapping("/ds")
    @PreAuthorize("@pms.hasPermission('money_pmoneyrecord_get')")
    public R getpmoneyrecordrankds(PMoneyRecord pmoneyrecord, HttpServletResponse response, HttpServletRequest request) {
        List<ResourceDeptVo> resourceDeptVos = new ArrayList<>();
        List<SysDeptSon> list = sysDeptSonService.lambdaQuery()
                .eq(SysDeptSon::getParentId, pmoneyrecord.getDid())
                .eq(SysDeptSon::getDelFlag, 0)
                .orderByDesc(SysDeptSon::getSort)
                .list();
        SysDept sysDept = sysDeptService.getById(pmoneyrecord.getDid());
        for (SysDeptSon sysDeptSon : list) {
            List<PMoneyRecord> recordList = pmoneyrecordService.lambdaQuery()
                    .eq(PMoneyRecord::getDid, pmoneyrecord.getDid())
                    .eq(PMoneyRecord::getDsid, sysDeptSon.getSonId())
                    .in(pmoneyrecord.getYear() != null, PMoneyRecord::getYear, pmoneyrecord.getYear())
                    .in(pmoneyrecord.getMonth() != null, PMoneyRecord::getMonth, pmoneyrecord.getMonth())
                    .in(pmoneyrecord.getDay() != null, PMoneyRecord::getDay, pmoneyrecord.getDay())
                    .eq(PMoneyRecord::getDelFlag, 0)
                    .eq(PMoneyRecord::getState, 1)
                    .orderByDesc(PMoneyRecord::getPublishTime)
                    .list();
            if (recordList.isEmpty()) {
                ResourceDeptVo resourceDeptVo = new ResourceDeptVo();
                resourceDeptVo.setDname(sysDept.getName());
                resourceDeptVo.setDsname(sysDeptSon.getName());
                resourceDeptVo.setYear(pmoneyrecord.getYear());
                resourceDeptVo.setMonth(pmoneyrecord.getMonth());
                resourceDeptVo.setDay(pmoneyrecord.getDay());
                resourceDeptVos.add(resourceDeptVo);
            }
        }
        ExcelBaseUtil.exportExcel(resourceDeptVos, "<< 村户列表 >>", "村户列表", ResourceDeptVo.class, "村户列表.xls", response);

        return null;
    }

    @ApiOperation(value = "查询未提交资金上传记录的村", notes = "查询未提交资金上传记录的村")
    @GetMapping("/d")//入参cdid  传镇id
    @PreAuthorize("@pms.hasPermission('money_pmoneyrecord_get')")
    public R getpmoneyrecordrankd(PMoneyRecord pmoneyrecord, HttpServletResponse response, HttpServletRequest request) {
        List<ResourceDeptVo> resourceDeptVos = new ArrayList<>();
        List<SysDept> list = sysDeptService.lambdaQuery()
                .eq(SysDept::getParentId, pmoneyrecord.getCdid())
                .eq(SysDept::getDelFlag, 0)
                .orderByDesc(SysDept::getSort)
                .list();
        for (SysDept sysDept : list) {
            List<SysDeptSon> Sonlist = sysDeptSonService.lambdaQuery()
                    .eq(SysDeptSon::getParentId, sysDept.getDeptId())
                    .eq(SysDeptSon::getDelFlag, 0)
                    .orderByDesc(SysDeptSon::getSort)
                    .list();
            for (SysDeptSon sysDeptSon : Sonlist) {
                List<PMoneyRecord> recordList = pmoneyrecordService.lambdaQuery()
                        .eq(PMoneyRecord::getDid, pmoneyrecord.getDid())
                        .eq(PMoneyRecord::getDsid, sysDeptSon.getSonId())
                        .in(pmoneyrecord.getYear() != null, PMoneyRecord::getYear, pmoneyrecord.getYear())
                        .in(pmoneyrecord.getMonth() != null, PMoneyRecord::getMonth, pmoneyrecord.getMonth())
                        .in(pmoneyrecord.getDay() != null, PMoneyRecord::getDay, pmoneyrecord.getDay())
                        .eq(PMoneyRecord::getDelFlag, 0)
                        .eq(PMoneyRecord::getState, 1)
                        .orderByDesc(PMoneyRecord::getPublishTime)
                        .list();
                if (recordList.isEmpty()) {
                    ResourceDeptVo resourceDeptVo = new ResourceDeptVo();
                    resourceDeptVo.setDname(sysDept.getName());
                    resourceDeptVo.setDsname(sysDeptSon.getName());
                    resourceDeptVo.setYear(pmoneyrecord.getYear());
                    resourceDeptVo.setMonth(pmoneyrecord.getMonth());
                    resourceDeptVo.setDay(pmoneyrecord.getDay());
                    resourceDeptVos.add(resourceDeptVo);
                }
            }
        }
        ExcelBaseUtil.exportExcel(resourceDeptVos, "<< 村列表 >>", "村列表", ResourceDeptVo.class, "村列表.xls", response);

        return null;
    }
}
