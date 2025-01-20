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

package com.px.pa.modulars.complain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 投诉信息
 *
 * @author 品讯科技
 * @date 2024-08
 */
@Data
@TableName("p_complain")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "投诉信息")
public class Complain extends Model<Complain> {
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    @TableId
    @ApiModelProperty(value = "")
    private Integer id;

    @ApiModelProperty(value = "是否受理(0待受理 1已受理）")
    private Integer isAccepted;
    @ApiModelProperty(value = "是否实名(0匿名 1实名）")
    private Integer isRealName;

    @ApiModelProperty(value = "名字")
    private String name;
    @ApiModelProperty(value = "电话")
    private String phone;
    @ApiModelProperty(value = "地址")
    private String address;
    @ApiModelProperty(value = "内容")
    private String content;
    @ApiModelProperty(value = "文件")
    private String file;
    /**
     * 是否显示：1-显示，0-不显示
     */
    @ApiModelProperty(value = "是否显示：1-显示，0-不显示")
    private Integer state;

    @ApiModelProperty(value = "村id")
    private Integer did;
    @TableField(exist = false)
    private String dname;
    @ApiModelProperty(value = "村组id")
    private Integer dsid;
    @TableField(exist = false)
    private String dsname;

    @ApiModelProperty(value = "投诉类型")
    private Integer type;
    @ApiModelProperty(value = "关联id")
    private Integer linkid;
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
    @ApiModelProperty(value = "发布时间")
    private LocalDateTime publishTime;
    @ApiModelProperty(value = "回复")
    private String returnContent;
    @ApiModelProperty(value = "回复时间")
    private LocalDateTime returnTime;
}
