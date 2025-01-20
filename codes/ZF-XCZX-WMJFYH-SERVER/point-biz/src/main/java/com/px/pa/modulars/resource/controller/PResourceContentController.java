package com.px.pa.modulars.resource.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pig4cloud.pig.common.core.util.R;
import com.px.basic.alone.security.util.SecurityUtils;
import com.px.pa.modulars.resource.entity.PResourceContent;
import com.px.pa.modulars.resource.service.PResourceContentService;
import com.px.pa.modulars.upms.entity.SysDept;
import com.px.pa.modulars.upms.entity.SysDeptSon;
import com.px.pa.modulars.upms.service.SysDeptService;
import com.px.pa.modulars.upms.service.SysDeptSonService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;


/**
 * 资产资源成交信息
 *
 * @author 品讯科技
 * @date 2024-08
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/resource/presourcecontent")
@Api(value = "ResourceContent", tags = "资产资源成交信息管理")
public class PResourceContentController {

    private final PResourceContentService pResourceContentService;
    @Autowired
    private SysDeptService sysDeptService;
    @Autowired
    private SysDeptSonService sysDeptSonService;

    /**
     * 分页查询
     *
     * @param page             分页对象
     * @param presourcecontent 资产资源成交信息
     * @return
     */
    @ApiOperation(value = "分页查询", notes = "分页查询")
    @GetMapping("/page")
    @PreAuthorize("@pms.hasPermission('resource_presourcecontent_get')")
    public R getpresourcecontentPage(Page page, PResourceContent presourcecontent) {
        return R.ok(pResourceContentService.lambdaQuery()
                .eq(presourcecontent.getAid() != null, PResourceContent::getAid, presourcecontent.getAid())
                .eq(presourcecontent.getType() != null, PResourceContent::getType, presourcecontent.getType())
                .eq(presourcecontent.getDid() != null, PResourceContent::getDid, presourcecontent.getDid())
                .eq(presourcecontent.getLinkid() != null, PResourceContent::getLinkid, presourcecontent.getLinkid())
                .eq(presourcecontent.getState() != null, PResourceContent::getState, presourcecontent.getState())
                .eq(presourcecontent.getDsid() != null, PResourceContent::getDsid, presourcecontent.getDsid())
                .eq(PResourceContent::getDelFlag, 0)
                //.eq(PResourceContent::getState, 1)
                .orderByAsc(PResourceContent::getSort)
                .page(page));
    }


    /**
     * 通过id查询资产资源成交信息
     *
     * @param id id
     * @return R
     */
    @ApiOperation(value = "通过id查询", notes = "通过id查询")
    @GetMapping("/{id}")
    @PreAuthorize("@pms.hasPermission('resource_presourcecontent_get')")
    public R getById(@PathVariable("id") Integer id) {
        PResourceContent pResourceContent = pResourceContentService.getById(id);
        if (pResourceContent.getDid() != null) {
            SysDept sysDept = sysDeptService.getById(pResourceContent.getDid());
            if (sysDept != null) {
                pResourceContent.setDname(sysDept.getName());
            }
        }
        if (pResourceContent.getDsid() != null) {
            SysDeptSon son = sysDeptSonService.getById(pResourceContent.getDsid());
            if (son != null) {
                pResourceContent.setDsname(son.getName());
            }
        }
        return R.ok(pResourceContent);
    }

    /**
     * 新增资产资源成交信息
     *
     * @param presourcecontent 资产资源成交信息
     * @return R
     */
    @ApiOperation(value = "新增资产资源成交信息", notes = "新增资产资源成交信息")
    @PostMapping
    @PreAuthorize("@pms.hasPermission('resource_presourcecontent_add')")
    public R save(@Validated @RequestBody PResourceContent presourcecontent) {
        presourcecontent.setDelFlag("0");
        presourcecontent.setState(1);
        Integer userid = SecurityUtils.getUser().getId();
        presourcecontent.setCreateBy(userid);
        presourcecontent.setCreateTime(LocalDateTime.now());
        return R.ok(pResourceContentService.save(presourcecontent));
    }

    /**
     * 修改资产资源成交信息
     *
     * @param presourcecontent 资产资源成交信息
     * @return R
     */
    @ApiOperation(value = "修改资产资源成交信息", notes = "修改资产资源成交信息")
    @PutMapping
    @PreAuthorize("@pms.hasPermission('resource_presourcecontent_edit')")
    public R updateById(@Validated @RequestBody PResourceContent presourcecontent) {
        presourcecontent.setUpdateTime(LocalDateTime.now());
        return R.ok(pResourceContentService.updateById(presourcecontent));
    }

    /**
     * 通过id删除资产资源成交信息
     *
     * @param id id
     * @return R
     */
    @ApiOperation(value = "通过id删除资产资源成交信息", notes = "通过id删除资产资源成交信息")
    @DeleteMapping("/{id}")
    @PreAuthorize("@pms.hasPermission('resource_presourcecontent_del')")
    public R removeById(@PathVariable Integer id) {
        return R.ok(pResourceContentService.removeById(id));
    }

}
