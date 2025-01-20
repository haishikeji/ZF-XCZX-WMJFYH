package com.px.pa.modulars.info.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pig4cloud.pig.common.core.util.R;
import com.px.pa.modulars.info.entity.SzInfomation;
import com.px.pa.modulars.info.entity.SzInformationMoney;
import com.px.pa.modulars.info.service.SzInfomationService;
import com.px.pa.modulars.info.service.SzInformationMoneyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;


/**
 * 资金公开
 *
 * @author 品讯科技
 * @date 2024-08
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/info/szinformationmoney")
@Api(value = "szinformationmoney", tags = "资金公开管理")
public class SzInformationMoneyController {

    private final SzInformationMoneyService szInformationMoneyService;
    private final SzInfomationService infomationService;

    /**
     * 分页查询
     *
     * @param page               分页对象
     * @param szInformationMoney 资金公开
     * @return
     */
    @ApiOperation(value = "分页查询", notes = "分页查询")
    @GetMapping("/page")
//    @PreAuthorize("@pms.hasPermission('info_szinformationmoney_get')")
    public R getSzInformationMoneyPage(Page page, SzInformationMoney szInformationMoney) {
        return R.ok(szInformationMoneyService.getPage(page, szInformationMoney));
    }


    /**
     * 通过id查询资金公开
     *
     * @param id id
     * @return R
     */
    @ApiOperation(value = "通过id查询", notes = "通过id查询")
    @GetMapping("/{id}")
//    @PreAuthorize("@pms.hasPermission('info_szinformationmoney_get')")
    public R getById(@PathVariable("id") Integer id) {
        return R.ok(szInformationMoneyService.getById(id));
    }

    /**
     * 新增资金公开
     *
     * @param szInformationMoney 资金公开
     * @return R
     */
    @ApiOperation(value = "新增资金公开", notes = "新增资金公开")
    @PostMapping
//    @PreAuthorize("@pms.hasPermission('info_szinformationmoney_add')")
    public R save(@Validated @RequestBody SzInformationMoney szInformationMoney) {
        saveUpdate(szInformationMoney);
        return R.ok(true);
    }

    @Transactional
    public void saveUpdate(SzInformationMoney szInformationMoney) {
        infomationService.saveOrUpdateBatch(szInformationMoney.getPays());
        infomationService.saveOrUpdateBatch(szInformationMoney.getIncomes());
        szInformationMoney.setPay(StrUtil.join(",", szInformationMoney.getPays().stream().map(SzInfomation::getId).collect(Collectors.toList())));
        szInformationMoney.setIncome(StrUtil.join(",", szInformationMoney.getIncomes().stream().map(SzInfomation::getId).collect(Collectors.toList())));
        szInformationMoneyService.saveOrUpdate(szInformationMoney);
    }

    /**
     * 修改资金公开
     *
     * @param szInformationMoney 资金公开
     * @return R
     */
    @ApiOperation(value = "修改资金公开", notes = "修改资金公开")
    @PutMapping
//    @PreAuthorize("@pms.hasPermission('info_szinformationmoney_edit')")
    public R updateById(@Validated @RequestBody SzInformationMoney szInformationMoney) {
        saveUpdate(szInformationMoney);
        return R.ok(true);
    }

    /**
     * 通过id删除资金公开
     *
     * @param id id
     * @return R
     */
    @ApiOperation(value = "通过id删除资金公开", notes = "通过id删除资金公开")
    @DeleteMapping("/{id}")
//    @PreAuthorize("@pms.hasPermission('info_szinformationmoney_del')")
    public R removeById(@PathVariable Integer id) {
        return R.ok(szInformationMoneyService.removeById(id));
    }

}
