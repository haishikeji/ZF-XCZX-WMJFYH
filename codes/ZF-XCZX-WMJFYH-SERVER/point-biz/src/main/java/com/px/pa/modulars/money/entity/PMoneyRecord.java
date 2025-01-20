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

/**
 * 资金上传记录
 *
 * @author 品讯科技
 * @date 2024-08
 */
@Data
@TableName("p_money_record")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "资金上传记录")
public class PMoneyRecord extends Model<PMoneyRecord> {
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    @TableId
    @ApiModelProperty(value = "")
    private Integer id;

    /**
     * 信息标题
     */
    @ApiModelProperty(value = "信息标题")
    private String name;
    @ApiModelProperty(value = "村组id")
    private Integer dsid;
    @TableField(exist = false)
    private String dsname;
    @TableField(exist = false)
    private Integer cdid;
    @ApiModelProperty(value = "村id")
    private Integer did;
    @TableField(exist = false)
    private String dname;
    /**
     * 年
     */
    @ApiModelProperty(value = "年")
    private Integer year;
    /**
     * 月
     */
    @ApiModelProperty(value = "月")
    private Integer month;
    /**
     * 日
     */
    @ApiModelProperty(value = "日")
    private Integer day;
    /**
     * 类型
     */
    @ApiModelProperty(value = "类型")
    private Integer type;
    /**
     * 是否显示：1-显示，0-不显示
     */
    @ApiModelProperty(value = "是否显示：1-显示，0-不显示")
    private Integer state;
    /**
     * 内容
     */
    @ApiModelProperty(value = "内容")
    private String content;
    /**
     * 关联id
     */
    @ApiModelProperty(value = "关联id")
    private Integer linkid;
    /**
     * 发布时间
     */
    @ApiModelProperty(value = "发布时间")
    private LocalDateTime publishTime;
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

}
