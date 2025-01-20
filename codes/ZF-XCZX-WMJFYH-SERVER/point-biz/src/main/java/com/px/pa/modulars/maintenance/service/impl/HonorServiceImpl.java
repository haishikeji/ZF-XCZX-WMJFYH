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
package com.px.pa.modulars.maintenance.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pig4cloud.pig.common.core.util.R;
import com.px.pa.modulars.core.entity.SzManUser;
import com.px.pa.modulars.core.entity.SzPoints;
import com.px.pa.modulars.core.mapper.SzPointsMapper;
import com.px.pa.modulars.core.mapper.SzTaskRecordMapper;
import com.px.pa.modulars.core.mapper.SzUserMapper;
import com.px.pa.modulars.core.service.SzPointsService;
import com.px.pa.modulars.core.service.SzUserService;
import com.px.pa.modulars.core.vo.TotalSroceVo;
import com.px.pa.modulars.maintenance.param.HonorParam;
import com.px.pa.modulars.maintenance.param.result.HonorResult;
import com.px.pa.modulars.maintenance.param.result.HonorUserResult;
import com.px.pa.modulars.maintenance.service.HonorService;
import com.px.pa.modulars.points.entity.PointsLevel;
import com.px.pa.modulars.points.mapper.PointsLevelMapper;
import com.px.pa.modulars.points.service.PointsLevelService;
import com.px.pa.modulars.upms.entity.SysDept;
import com.px.pa.modulars.upms.service.SysDeptService;
import com.px.pa.utils.bean.BaseQueryToPageUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * 荣誉榜
 *
 * @author 品讯科技
 * @date 2024-08
 */
@Service
@Transactional
public class HonorServiceImpl extends ServiceImpl<PointsLevelMapper, PointsLevel> implements HonorService {

    @Autowired
    private SzUserService userService;
    @Autowired
    private PointsLevelService pointsLevelService;
    @Autowired
    private SysDeptService deptService;
    @Autowired
    private SzTaskRecordMapper recordMapper;
    @Autowired
    private SzUserMapper userMapper;
    @Autowired
    private SzPointsService pointsService;
    @Autowired
    private SzPointsMapper szPointsMapper;

    @Override
    public HonorResult getUserHonorList(HonorParam honorParam) {
        /*switch (honorParam.getType()) {
            case 1:*/
        return this.queryUser(honorParam);
           /* case 2:
//                return this.queryVillageCadres(param);
            case 3:
//                return this.queryVillage(param);
            case 4:
//                return this.queryCadres(param);
            case 5:
//                return this.queryCompany(param);
            case 6:
                return this.queryUserList(honorParam);

        }
        return null;*/
    }

    private HonorResult queryUserList(HonorParam param) {
        HonorResult result = new HonorResult();
        result.setTotalScore(this.gettotalScore(param));
        Page<SzManUser> page = BaseQueryToPageUtil.createPage(param);
        Page<HonorUserResult> itemResult = BaseQueryToPageUtil.createPage(param);
        //TODO 统计当前年份，如果不是当年年份，在查询历史记录
        /*page = this.userService.lambdaQuery()
                .eq(SzUser::getLogin, 1)
                .eq(SzUser::getDelFlag, 0)
                .eq(param.getDeptName()!=null,SzUser::getDid,param.getDeptName())
                .eq(param.getCdidName()!=null,SzUser::getCdid,param.getCdidName())
                .notIn(param.getSkipIds().size() > 0, SzUser::getId, param.getSkipIds())
                .orderByDesc(SzUser::getScore, SzUser::getUpdateTime).list();*/
        QueryWrapper<Integer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("str.type", 1);
        queryWrapper.eq("str.del_flag", 0);
        if (param.getYear() != null) {
            if (param.getStrmonth() == null) {
                queryWrapper.apply("date_format(str.create_time,'%Y')={0}", param.getYear());
            } else {
                if (param.getEndmonth() == null) {
                    param.setEndmonth(12);
                }
                queryWrapper.apply("date_format (str.create_time,'%Y-%m') >= {0}", (param.getYear() == null ? DateUtil.year(new Date()) : param.getYear()) + (param.getStrmonth() < 10 ? "-0" : "-") + param.getStrmonth());
                queryWrapper.apply("date_format (str.create_time,'%Y-%m') <= {0}", (param.getYear() == null ? DateUtil.year(new Date()) : param.getYear()) + (param.getEndmonth() < 10 ? "-0" : "-") + param.getEndmonth());
            }
        }
        //queryWrapper.ge("totalScore",0);
        queryWrapper.eq("u.login", 1);
        queryWrapper.eq("u.del_flag", 0);
        queryWrapper.eq(param.getDeptName() != null, "u.did", param.getDeptName());
        queryWrapper.eq(param.getCdidName() != null, "u.cdid", param.getCdidName());
        queryWrapper.notIn(param.getSkipIds().size() > 0, "u.id", param.getSkipIds());
        queryWrapper.groupBy("str.cdid,str.code");
        queryWrapper.orderByDesc("totalScore");
        queryWrapper.orderByAsc("downtask");
        queryWrapper.orderByDesc("uptask");
        queryWrapper.orderByDesc("time");
        //queryWrapper.last("limit 20");
        page = this.userMapper.usertotalScoreList(page, queryWrapper);
        List<HonorUserResult> list = new ArrayList<>();
//        List<PointsLevel> levels = this.pointsLevelService.lambdaQuery()
//                .eq(PointsLevel::getDelFlag, 0).eq(PointsLevel::getType, 1)
//                .orderByAsc(PointsLevel::getBeginScore).list();
        page.getRecords().forEach(item -> {

            HonorUserResult ri = new HonorUserResult();
            //户
            ri.setArea(1);
            SysDept dept = this.deptService.getById(item.getDid());
            ri.setDeptName(dept != null ? dept.getName() : "");
            SysDept cdept = this.deptService.getById(item.getCdid());
            ri.setCdidName(cdept != null ? cdept.getName() : "");
            ri.setDid(item.getDid());
            ri.setName(item.getName());
            ri.setId(item.getId());
            ri.setPhoto(item.getAvatar());
            ri.setPhone(item.getPhone());
            ri.setSex(item.getSex());
            ri.setCdid(item.getCdid());

            /*QueryWrapper<Integer> queryWrapper=new QueryWrapper<>();
            queryWrapper.eq("str.uid", item.getId());
            queryWrapper.eq("st.area", ri.getArea());
            if(param.getMonth()==null){
                queryWrapper.apply("date_format(str.create_time,'%Y')={0}", param.getYear()==null? DateUtil.year(new Date()):param.getYear());
            }else{
                queryWrapper.apply("date_format(str.create_time,'%Y-%m')={0}", (param.getYear()==null? DateUtil.year(new Date()):param.getYear())
                        +(param.getMonth()<10? "-0":"-")
                        + (param.getMonth()==null? DateUtil.month(new Date()):param.getMonth()));
            }
//            queryWrapper.apply("date_format(create_time,'%Y-%m')={0}", (param.getYear()==null? DateUtil.year(new Date()):param.getYear())+"-"+param.getMonth()==null? DateUtil.month(new Date()):param.getMonth());
//            queryWrapper.apply("date_format(create_time,'%m')={0}", param.getMonth()==null? DateUtil.month(new Date()):param.getMonth());
            queryWrapper.nested(i->i.gt("str.points", 0).eq("str.state", 1).or(q->q.lt("str.points", 0).eq("str.state", 2)));
            ri.setScore(recordMapper.sumPoints(queryWrapper));*/
            //总分
            ri.setScore(item.getTotalScore());
            // 计算星级
//            ri.setNum(this.pointsLevelService.getLevel(ri.getScore(), levels));
            list.add(ri);
        });

        Collections.sort(list, new Comparator<HonorUserResult>() {//此处创建了1个匿名内部类
            @Override
            public int compare(HonorUserResult o1, HonorUserResult o2) {
                return o2.getScore() - o1.getScore();
            }
        });

//        list.stream().sorted(Comparator.comparingInt(HonorUserResult::getId)).collect(Collectors.toList());
        itemResult.setRecords(list);
        result.setRankings(itemResult);
        return result;

    }

    //    @Override
    public R exportUserHonorList(HonorParam honorParam, HttpServletResponse response) throws IOException {
//
//        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
//        response.setCharacterEncoding("utf-8");
//        String filename = URLEncoder.encode(honorParam.getYear()+"年"+(honorParam.getMonth()==null?"":(honorParam.getMonth()+"月"))+"积分信息表", "UTF-8").replaceAll("\\+", "%20");
//        response.setHeader("Content-Disposition","attachment;filename*=utf-8''"+filename+".xlsx");


//        EasyExcel.write(response.getOutputStream(), Library.class)
//                .sheet("积分排行")
//                .doWrite(libraryService.list());


//
        return R.ok(true);
    }

    public List<TotalSroceVo> gettotalScore(HonorParam param) {
        QueryWrapper<Integer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("str.type", 1);
        queryWrapper.eq("str.del_flag", 0);
        if (param.getYear() != null) {
            if (param.getStrmonth() == null) {
                queryWrapper.apply("date_format(str.create_time,'%Y')={0}", param.getYear() == null ? DateUtil.year(new Date()) : param.getYear());
            } else {
                if (param.getEndmonth() == null) {
                    param.setEndmonth(12);
                }
                queryWrapper.apply("date_format (str.create_time,'%Y-%m') >= {0}", (param.getYear() == null ? DateUtil.year(new Date()) : param.getYear()) + (param.getStrmonth() < 10 ? "-0" : "-") + param.getStrmonth());
                queryWrapper.apply("date_format (str.create_time,'%Y-%m') <= {0}", (param.getYear() == null ? DateUtil.year(new Date()) : param.getYear()) + (param.getEndmonth() < 10 ? "-0" : "-") + param.getEndmonth());
            }
        }
        queryWrapper.eq("u.login", 1);
        queryWrapper.eq("u.del_flag", 0);
        queryWrapper.eq(param.getDeptName() != null, "u.did", param.getDeptName());
        queryWrapper.eq(param.getCdidName() != null, "u.cdid", param.getCdidName());
        queryWrapper.notIn(param.getSkipIds().size() > 0, "u.id", param.getSkipIds());
        queryWrapper.groupBy("u.did");
        List<TotalSroceVo> totalSroceVos = this.userMapper.totalScore(queryWrapper);
        return totalSroceVos;
    }

    /**
     * 查询村民荣誉榜
     *
     * @return 村民集合
     */
    private HonorResult queryUser(HonorParam param) {
        HonorResult result = new HonorResult();
        result.setTotalScore(this.gettotalScore(param));
        Page<SzManUser> page = BaseQueryToPageUtil.createPage(param);
        Page<HonorUserResult> itemResult = BaseQueryToPageUtil.createPage(param);
        //TODO 统计当前年份，如果不是当年年份，在查询历史记录

        QueryWrapper<Integer> queryWrapper = new QueryWrapper<>();
        if (param.getPid() != 1) {
            queryWrapper.eq("str.type", 4);
            if (param.getDeptName() != null) {
                queryWrapper.eq("u.zcdid", param.getDeptName());
            } else {
                queryWrapper.in("u.zdid", deptService.getidsbypid(param.getPid()));
            }
            queryWrapper.eq(param.getCdidName() != null, "u.zcdid", param.getCdidName());
        } else {
            queryWrapper.eq("str.type", 1);
            queryWrapper.eq("u.relation", 1);

            if (param.getDeptName() != null) {
                queryWrapper.eq("u.cdid", param.getDeptName());
            } else {
                queryWrapper.in("u.did", deptService.getidsbypid(param.getPid()));
            }
            queryWrapper.eq(param.getCdidName() != null, "u.cdid", param.getCdidName());
        }
        queryWrapper.eq("str.del_flag", 0);
        if (param.getYear() != null) {
            if (param.getStrmonth() == null) {
                queryWrapper.apply("date_format(str.create_time,'%Y')={0}", param.getYear());
            } else {
                if (param.getEndmonth() == null) {
                    param.setEndmonth(12);
                }
                queryWrapper.apply("date_format (str.create_time,'%Y-%m') >= {0}", (param.getYear() == null ? DateUtil.year(new Date()) : param.getYear()) + (param.getStrmonth() < 10 ? "-0" : "-") + param.getStrmonth());
                queryWrapper.apply("date_format (str.create_time,'%Y-%m') <= {0}", (param.getYear() == null ? DateUtil.year(new Date()) : param.getYear()) + (param.getEndmonth() < 10 ? "-0" : "-") + param.getEndmonth());
            }
        }
        //queryWrapper.ge("totalScore",0);
        queryWrapper.like(StringUtils.isNotEmpty(param.getName()), "u.name", param.getName());
        queryWrapper.eq("u.login", 1);
        queryWrapper.eq("u.del_flag", 0);

        //queryWrapper.notIn(param.getSkipIds().size() > 0,"u.id", param.getSkipIds());
        //queryWrapper.and(param.getRole()!=null,wrapper -> wrapper.apply("FIND_IN_SET ( '"+ param.getRole() +"',u.cdid )")
        //.or( wrapper1 -> wrapper1.apply("FIND_IN_SET ( '"+ param.getRole() +"',u.did )")));
        queryWrapper.groupBy("str.cdid,str.code");
        queryWrapper.orderByDesc("totalScore");
        queryWrapper.orderByAsc("downtask");
        queryWrapper.orderByDesc("uptask");
        queryWrapper.orderByDesc("time");
        //queryWrapper.last("limit 20");
        if (param.getPid() != 1) {
            page = this.userMapper.zusertotalScoreList(page, queryWrapper);
            for (SzManUser item : page.getRecords()) {
                item.setName(item.getZname());
                item.setDid(item.getZdid());
                item.setCdid(item.getZcdid());
                item.setCode(item.getZname());
            }
        } else {
            page = this.userMapper.usertotalScoreList(page, queryWrapper);

        }
        itemResult.setTotal(page.getTotal());
        List<HonorUserResult> list = new ArrayList<>();
        List<PointsLevel> levels = this.pointsLevelService.lambdaQuery()
                .eq(PointsLevel::getDelFlag, 0).eq(PointsLevel::getType, 1)
                .orderByAsc(PointsLevel::getBeginScore).list();
        Integer rank = 0;
        for (SzManUser item : page.getRecords()) {
            rank += 1;
            SzPoints points = null;
            if (param.getPid() != 1) {
                points = this.pointsService.lambdaQuery()
                        .eq(SzPoints::getDid, item.getDid())
                        .eq(SzPoints::getCdid, item.getCdid())
                        .eq(SzPoints::getCode, item.getCode())
                        .eq(SzPoints::getType, 4)
                        .one();
            } else {
                points = this.pointsService.lambdaQuery()
                        .eq(SzPoints::getDid, item.getDid())
                        .eq(SzPoints::getCdid, item.getCdid())
                        .eq(SzPoints::getCode, item.getCode())
                        .eq(SzPoints::getType, 1)
                        .one();
            }


            /*if (points == null || points.getPoints() > 0) {
                QueryWrapper query1 = new QueryWrapper();
                query1.apply("str.cdid = " + item.getCdid());
                if(param.getPid()!=1){
                    query1.apply("str.type = " + OrgTypeEnum.ZHOUSE.getValue());

                }else{
                    query1.apply("str.type = " + OrgTypeEnum.HOUSE.getValue());

                }
                query1.apply("str.del_flag = " + 0);
                query1.apply("str.op_type != " + PointsTypeEnum.ORDER.getValue());
                //queryWrapper.apply("date_format(str.create_time,'%Y') = "+ year);
                if (param.getYear() != null) {
                    if (param.getStrmonth() == null && param.getEndmonth() == null) {
                        query1.apply("date_format(str.create_time,'%Y') = '" + param.getYear() + "'");
                    } else {
                        if (param.getEndmonth() == null) {
                            param.setEndmonth(12);
                        }
                        if (param.getStrmonth() == null) {
                            param.setStrmonth(1);
                        }
                        String str = "";
                        if (param.getStrmonth() < 10) {
                            str = param.getYear() + "-0" + param.getStrmonth();
                        } else {
                            str = param.getYear() + "-" + param.getStrmonth();
                        }
                        String end = "";
                        if (param.getEndmonth() < 10) {
                            end = param.getYear() + "-0" + param.getEndmonth();
                        } else {
                            end = param.getYear() + "-" + param.getEndmonth();
                        }
                        query1.apply("date_format (str.create_time,'%Y-%m') >= '" + str + "'");
                        query1.apply("date_format (str.create_time,'%Y-%m') <= '" + end + "'");
                    }
                }
                query1.isNotNull("p.id");
                query1.groupBy("str.cdid,str.code");
                QueryWrapper query = new QueryWrapper();
                query.eq("a.cdid", item.getCdid());
                query.eq("a.code", item.getCode());
                query.last("limit 1");

                rank = szPointsMapper.getTimeSrank(query, query1, query1);
            }
*/
            HonorUserResult ri = new HonorUserResult();
            //户
            ri.setArea(1);
            SysDept dept = this.deptService.getById(item.getDid());
            ri.setDeptName(dept != null ? dept.getName() : "");
            SysDept cdept = this.deptService.getById(item.getCdid());
            ri.setCdidName(cdept != null ? cdept.getName() : "");
            ri.setDid(item.getDid());
            ri.setCode(item.getCode());
            ri.setName(item.getName());
            ri.setId(item.getId());
            ri.setPhoto(item.getAvatar());
            ri.setPhone(item.getPhone());
            ri.setSex(item.getSex());
            ri.setCdid(item.getCdid());
            ri.setRank(rank);
            ri.setRemainScore(points == null ? 0 : points.getShopPoints());
            ri.setScore(item.getTotalScore());
            // 计算星级
            ri.setNum(this.pointsLevelService.getLevel(ri.getScore(), levels));
            list.add(ri);
        }
        Collections.sort(list, new Comparator<HonorUserResult>() {//此处创建了1个匿名内部类
            @Override
            public int compare(HonorUserResult o1, HonorUserResult o2) {
                return o2.getScore() - o1.getScore();
            }
        });

//        list.stream().sorted(Comparator.comparingInt(HonorUserResult::getId)).collect(Collectors.toList());
        itemResult.setRecords(list);
        result.setRankings(itemResult);

        //村庄信息
        List<SysDept> depts = this.deptService.lambdaQuery()
                .eq(SysDept::getParentId, 1)
                .eq(SysDept::getDelFlag, 0)
                .orderByDesc(SysDept::getSort, SysDept::getUpdateTime, SysDept::getCreateTime)
                .list();
//        depts.forEach(dept->{
//            List<SysDept> cdepts = this.deptService.lambdaQuery()
//                    .eq(SysDept::getParentId,dept.getDeptId())
//                    .eq(SysDept::getDelFlag, 0)
//                    .orderByDesc(SysDept::getSort, SysDept::getUpdateTime, SysDept::getCreateTime)
//                    .list();
//            dept.setCdept(cdepts);
//        });
//        System.out.println(depts);
        result.setDepts(depts);
        return result;
    }


}
