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

package com.px.pa.modulars.money.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 资金信息分类
 *
 * @author 品讯科技
 * @date 2024-08
 */
@Data
@TableName("p_money_cate")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "资金信息分类")
public class PMoneyCate extends Model<PMoneyCate> {
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    @TableId
    @ApiModelProperty(value = "")
    private Integer id;

    @ApiModelProperty(value = "上级id")
    private Integer pid;
    /**
     * 分类名称
     */
    @ApiModelProperty(value = "分类名称")
    private String name;
    /**
     * 是否显示：1-显示，0-不显示
     */
    @ApiModelProperty(value = "是否显示：1-显示，0-不显示")
    private Integer state;

    @ApiModelProperty(value = "分类图")
    private String img;
    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
    private Integer sort;
    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;
    /**
     * 修改时间
     */
    @ApiModelProperty(value = "修改时间")
    private LocalDateTime updateTime;
    /**
     * 是否删除  -1：已删除  0：正常
     */
    @ApiModelProperty(value = "是否删除  -1：已删除  0：正常")
    private String delFlag;
    @ApiModelProperty(value = "管理端创建人")
    private Integer createBy;
    @TableField(exist = false)
    private List<PMoneyCate> children;
    @TableField(exist = false)
    private List<PMoneyContent> contents;
    @TableField(exist = false)
    private Double allMoney;
}
