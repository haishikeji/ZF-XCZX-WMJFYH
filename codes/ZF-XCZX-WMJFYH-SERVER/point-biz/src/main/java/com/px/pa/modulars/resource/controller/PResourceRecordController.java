package com.px.pa.modulars.resource.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pig4cloud.pig.common.core.util.R;
import com.px.basic.alone.security.util.SecurityUtils;
import com.px.pa.modulars.assets.service.AssetsContentService;
import com.px.pa.modulars.resource.entity.PResourceRecord;
import com.px.pa.modulars.resource.service.PResourceRecordService;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * 资产资源咨询信息
 *
 * @author 品讯科技
 * @date 2024-08
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/resource/presourcerecord")
@Api(value = "ResourceRecord", tags = "资产资源咨询信息管理")
public class PResourceRecordController {

    private final PResourceRecordService pResourceRecordService;
    @Autowired
    private SysDeptService sysDeptService;
    @Autowired
    private SysDeptSonService sysDeptSonService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private AssetsContentService assetsContentService;

    /**
     * 分页查询
     *
     * @param page            分页对象
     * @param presourceRecord 资产资源咨询信息
     * @return
     */
    @ApiOperation(value = "分页查询", notes = "分页查询")
    @GetMapping("/page")
    @PreAuthorize("@pms.hasPermission('resource_presourcerecord_get')")
    public R getPResourceRecordPage(Page page, PResourceRecord presourceRecord) {
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
                presourceRecord.setDid(sysDept.getDeptId());
            }
        }
        List<Integer> aids = new ArrayList<>();
        /*if(presourceRecord.getTType()!=null){
            aids = assetsContentService.lambdaQuery()
                    .select(AssetsContent::getId)
                    .eq(AssetsContent::getCid, presourceRecord.getTType())
                    .eq(AssetsContent::getDelFlag, 0)
                    .list()
                    .stream().map(AssetsContent::getId).collect(Collectors.toList());
        }*/
        return R.ok(pResourceRecordService.lambdaQuery()
                .in(!deptIds.isEmpty(), PResourceRecord::getDid, deptIds)
                .eq(presourceRecord.getDid() != null, PResourceRecord::getDid, presourceRecord.getDid())
                .eq(presourceRecord.getDsid() != null, PResourceRecord::getDsid, presourceRecord.getDsid())
                .eq(presourceRecord.getState() != null, PResourceRecord::getState, presourceRecord.getState())
                .eq(presourceRecord.getIsAccepted() != null, PResourceRecord::getIsAccepted, presourceRecord.getIsAccepted())
                //.in(!aids.isEmpty(),PResourceRecord::getAid, aids)
                .eq(presourceRecord.getTType() != null, PResourceRecord::getAid, presourceRecord.getTType())
                .eq(PResourceRecord::getDelFlag, 0)
                //.eq(PResourceRecord::getState, 1)
                .page(page));
    }


    /**
     * 通过id查询资产资源咨询信息
     *
     * @param id id
     * @return R
     */
    @ApiOperation(value = "通过id查询", notes = "通过id查询")
    @GetMapping("/{id}")
    @PreAuthorize("@pms.hasPermission('resource_presourcerecord_get')")
    public R getById(@PathVariable("id") Integer id) {
        PResourceRecord pResourceRecord = pResourceRecordService.getById(id);
        if (pResourceRecord.getDid() != null) {
            SysDept sysDept = sysDeptService.getById(pResourceRecord.getDid());
            if (sysDept != null) {
                pResourceRecord.setDname(sysDept.getName());
            }
        }
        if (pResourceRecord.getDsid() != null) {
            SysDeptSon son = sysDeptSonService.getById(pResourceRecord.getDsid());
            if (son != null) {
                pResourceRecord.setDsname(son.getName());
            }
        }
        return R.ok(pResourceRecord);
    }

    /**
     * 新增资产资源咨询信息
     *
     * @param PResourceRecord 资产资源咨询信息
     * @return R
     */
    @ApiOperation(value = "新增资产资源咨询信息", notes = "新增资产资源咨询信息")
    @PostMapping
    @PreAuthorize("@pms.hasPermission('resource_presourcerecord_add')")
    public R save(@Validated @RequestBody PResourceRecord PResourceRecord) {
        return R.ok(pResourceRecordService.save(PResourceRecord));
    }

    /**
     * 修改资产资源咨询信息
     *
     * @param PResourceRecord 资产资源咨询信息
     * @return R
     */
    @ApiOperation(value = "修改资产资源咨询信息", notes = "修改资产资源咨询信息")
    @PutMapping
    @PreAuthorize("@pms.hasPermission('resource_presourcerecord_edit')")
    public R updateById(@Validated @RequestBody PResourceRecord PResourceRecord) {
        return R.ok(pResourceRecordService.updateById(PResourceRecord));
    }

    /**
     * 通过id删除资产资源咨询信息
     *
     * @param id id
     * @return R
     */
    @ApiOperation(value = "通过id删除资产资源咨询信息", notes = "通过id删除资产资源咨询信息")
    @DeleteMapping("/{id}")
    @PreAuthorize("@pms.hasPermission('resource_presourcerecord_del')")
    public R removeById(@PathVariable Integer id) {
        return R.ok(pResourceRecordService.removeById(id));
    }

}
