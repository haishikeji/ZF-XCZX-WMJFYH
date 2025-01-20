package com.px.pa.modulars.info.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pig4cloud.pig.common.core.util.R;
import com.px.pa.modulars.info.entity.SzInformationCollect;
import com.px.pa.modulars.info.service.SzInformationCollectService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * 公开信息收藏
 *
 * @author 品讯科技
 * @date 2024-08
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/info/szinformationcollect")
@Api(value = "szinformationcollect", tags = "公开信息收藏管理")
public class SzInformationCollectController {

    private final SzInformationCollectService szInformationCollectService;

    /**
     * 分页查询
     *
     * @param page                 分页对象
     * @param szInformationCollect
     * @return
     */
    @ApiOperation(value = "分页查询", notes = "分页查询")
    @GetMapping("/page")
//    @PreAuthorize("@pms.hasPermission('info_szinformationmoney_get')")
    public R getSzInformationMoneyPage(Page page, SzInformationCollect szInformationCollect) {
        return R.ok(szInformationCollectService.getPage(page, szInformationCollect));
    }


    /**
     * 通过id查询公开信息收藏
     *
     * @param id id
     * @return R
     */
    @ApiOperation(value = "通过id查询", notes = "通过id查询")
    @GetMapping("/{id}")
//    @PreAuthorize("@pms.hasPermission('info_szinformationmoney_get')")
    public R getById(@PathVariable("id") Integer id) {
        return R.ok(szInformationCollectService.getById(id));
    }

    /**
     * 新增公开信息收藏
     *
     * @param szInformationCollect 新增公开信息收藏
     * @return R
     */
    @ApiOperation(value = "新增公开信息收藏", notes = "新增公开信息收藏")
    @PostMapping
//    @PreAuthorize("@pms.hasPermission('info_szinformationmoney_add')")
    public R save(@Validated @RequestBody SzInformationCollect szInformationCollect) {
        szInformationCollectService.save(szInformationCollect);
        return R.ok(true);
    }

    /**
     * 修改公开信息收藏
     *
     * @param szInformationCollect 修改公开信息收藏
     * @return R
     */
    @ApiOperation(value = "修改公开信息收藏", notes = "修改公开信息收藏")
    @PutMapping
//    @PreAuthorize("@pms.hasPermission('info_szinformationmoney_edit')")
    public R updateById(@Validated @RequestBody SzInformationCollect szInformationCollect) {
        szInformationCollectService.updateById(szInformationCollect);
        return R.ok(true);
    }

    /**
     * 通过id删除公开回复
     *
     * @param id id
     * @return R
     */
    @ApiOperation(value = "通过id删除公开信息收藏", notes = "通过id删除公开信息收藏")
    @DeleteMapping("/{id}")
//    @PreAuthorize("@pms.hasPermission('info_szinformationmoney_del')")
    public R removeById(@PathVariable Integer id) {
        return R.ok(szInformationCollectService.removeById(id));
    }

}
