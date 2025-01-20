package com.px.pa.modulars.home.controller;


import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pig4cloud.pig.common.core.util.R;
import com.px.basic.alone.security.annotation.Inner;
import com.px.basic.alone.security.util.SecurityUtils;
import com.px.pa.constants.OrgTypeEnum;
import com.px.pa.constants.PointsTypeEnum;
import com.px.pa.modulars.core.entity.*;
import com.px.pa.modulars.core.mapper.*;
import com.px.pa.modulars.core.service.*;
import com.px.pa.modulars.core.vo.PagetaskVo;
import com.px.pa.modulars.core.vo.PagetasksVo;
import com.px.pa.modulars.core.vo.RoleVo;
import com.px.pa.modulars.home.service.HomeInfoService;
import com.px.pa.modulars.upms.entity.SysDept;
import com.px.pa.modulars.upms.entity.SysUser;
import com.px.pa.modulars.upms.entity.SysUserRole;
import com.px.pa.modulars.upms.service.SysDeptService;
import com.px.pa.modulars.upms.service.SysUserRoleService;
import com.px.pa.modulars.upms.service.SysUserService;
import com.px.pa.modulars.vo.IndexQueryParam;
import com.px.pa.modulars.vo.TaskRecordParam;
import com.px.pa.modulars.vo.result.ManUserResult;
import com.px.pa.utils.ExcelUtil.ExcelBaseUtil;
import com.px.pa.utils.bean.BaseQueryToPageUtil;
import com.px.pa.vo.result.HomeDataInfoResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * @author 品讯科技
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/home")
@Api(value = "home", tags = "首页服务")
public class HomeController {
    @Autowired
    private HomeInfoService homeInfoService;
    @Autowired
    private SzPointsLogMapper szPointsLogMapper;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysUserRoleService userRoleService;
    @Autowired
    private SysDeptService sysDeptService;
    @Autowired
    private SzManUserMapper szManUserMapper;
    @Autowired
    private SzShopUserService szShopUserService;
    @Autowired
    private SzTaskRecordService szTaskRecordService;
    @Autowired
    private SzShopOrderMapper szShopOrderMapper;
    @Autowired
    private SzTaskService szTaskService;
    @Autowired
    private SzManUserService szManUserService;
    @Autowired
    private SzShopGoodsService szShopGoodsService;
    @Autowired
    private SzTaskRecordMapper szTaskRecordMapper;
    @Autowired
    private SzRoleUserService szRoleUserService;
    @Autowired
    private SzRoleUserMapper szRoleUserMapper;
    @Autowired
    private SzShopGoodsMapper szShopGoodsMapper;
    @Autowired
    private SzFamilyCardMapper szFamilyCardMapper;
    @Autowired
    private SzLoginUserMapper szLoginUserMapper;

    public static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            try {
                Double.parseDouble(str);
                return true;
            } catch (NumberFormatException ex) {
                return false;
            }
        }
    }

    @GetMapping("/read")
    @ApiOperation("首页信息查询")
//    @Inner(false)
    public R readHomeInfo(IndexQueryParam param) {
        HomeDataInfoResult result = this.homeInfoService.readHome(param);

        return R.ok(result);
    }

    /**
     * 近1个月日期list
     *
     * @param
     * @return
     */

    @GetMapping("/activeusers")
    @ApiOperation("活跃用户查询")
    @Inner(false)
    public R readActiveUsers(Integer type, IndexQueryParam param) throws ParseException {
        LocalDate strTime = null;
        LocalDate endTime = null;
        Integer months = null;
        if (StringUtils.isNotEmpty(param.getStrDate())) {
            strTime = LocalDate.parse(param.getStrDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            if (StringUtils.isNotEmpty(param.getEndDate())) {
                endTime = LocalDate.parse(param.getEndDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")).plusMonths(1).minusDays(1);
                YearMonth startYearMonth = YearMonth.from(strTime);
                YearMonth endYearMonth = YearMonth.from(endTime);
                long monthsBetween = startYearMonth.until(endYearMonth, ChronoUnit.MONTHS);
                months = Math.toIntExact(monthsBetween) + 1;
            } else {
                months = 1;
            }
        }
        List<Map> list = new ArrayList<>();
        if (strTime != null) {
            for (int i = 0; i < months; i++) {
                Map map = homeInfoService.getYearsTime(strTime.plusMonths(i));
                list.add(map);
            }
        } else {
            if (type == null) {
                type = 1;
            }
            if (type == 1) {//近1个月
                list = homeInfoService.getlastMonths();
            } else if (type == 2) {//近1个季
                list = homeInfoService.getlastSeasons();
            } else if (type == 3) {//近一年
                list = homeInfoService.getlastYears();
            } else {
                list = homeInfoService.getlastYears();
            }
        }
        if (param.getCdid() != null) {
            if (sysDeptService.getpid(param.getDid()) != 1) {
                param.setZtype(1);
            }
        }
        for (Map map : list) {
            List<DateTime> dates = homeInfoService.getTaskDatetime(map);
            QueryWrapper query = new QueryWrapper();
            query.between("l.create_time", dates.get(0), dates.get(1));
            query.eq("l.login_type", 1);
            if (param.getZtype() != null && param.getZtype() == 1) {
                query.eq(param.getCdid() != null, "m.zcdid", param.getCdid());
            } else {
                query.eq(param.getCdid() != null, "m.cdid", param.getCdid());
            }
            Integer count = szLoginUserMapper.getActiveManUser(query);
            map.put("date", dates.get(1).toString());
            map.put("count", count != null ? count : 0);
        }
        return R.ok(list);
    }

    @GetMapping("/allintegral")
    @ApiOperation("累计积分查询")
    @Inner(false)
    public R allintegral(Integer timeType, IndexQueryParam param) throws ParseException {
        LocalDate strTime = null;
        LocalDate endTime = null;
        Integer months = null;
        if (StringUtils.isNotEmpty(param.getStrDate())) {
            strTime = LocalDate.parse(param.getStrDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            if (StringUtils.isNotEmpty(param.getEndDate())) {
                endTime = LocalDate.parse(param.getEndDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")).plusMonths(1).minusDays(1);
                YearMonth startYearMonth = YearMonth.from(strTime);
                YearMonth endYearMonth = YearMonth.from(endTime);
                long monthsBetween = startYearMonth.until(endYearMonth, ChronoUnit.MONTHS);
                months = Math.toIntExact(monthsBetween) + 1;
            } else {
                months = 1;
            }
        }
        List<Map> list = new ArrayList<>();
        if (strTime != null) {
            for (int i = 0; i < months; i++) {
                Map map = homeInfoService.getYearsTime(strTime.plusMonths(i));
                list.add(map);
            }
        } else {
            if (timeType == null) {
                timeType = 1;
            }
            if (timeType == 1) {//近1个月
                list = homeInfoService.getlastMonths();
            } else if (timeType == 2) {//近1个季
                list = homeInfoService.getlastSeasons();
            } else if (timeType == 3) {//近一年
                list = homeInfoService.getlastYears();
            } else {
                list = homeInfoService.getlastYears();
            }
        }
        for (Map map : list) {
            List<DateTime> dates = homeInfoService.getTaskDatetime(map);
            QueryWrapper query = new QueryWrapper();
            query.between("p.create_time", dates.get(0), dates.get(1));
            query.eq("p.state", 1);
            query.apply(param.getCdid() != null, "FIND_IN_SET ( '" + param.getCdid() + "',t.cdid )");
            Integer count = szPointsLogMapper.allintegral(query);
            map.put("date", dates.get(1).toString());
            map.put("count", count != null ? count : 0);
        }
        return R.ok(list);
    }

    /*public static void main(String[] args) {
        LocalDate startDate = LocalDate.of(2022, 1, 1);
        LocalDate endDate = LocalDate.of(2022, 12, 31);

        YearMonth startYearMonth = YearMonth.from(startDate);
        YearMonth endYearMonth = YearMonth.from(endDate);

        long monthsBetween = startYearMonth.until(endYearMonth, ChronoUnit.MONTHS);

        System.out.println("月数差：" + monthsBetween);

    }*/
    @GetMapping("/avgintegral")
    //@ApiOperation("平均积分查询")
    @ApiOperation("累计兑换")
    @Inner(false)
    public R avgintegral(Integer timeType, IndexQueryParam param) throws ParseException {
        LocalDate strTime = null;
        LocalDate endTime = null;
        Integer months = null;
        if (StringUtils.isNotEmpty(param.getStrDate())) {
            strTime = LocalDate.parse(param.getStrDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            if (StringUtils.isNotEmpty(param.getEndDate())) {
                endTime = LocalDate.parse(param.getEndDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")).plusMonths(1).minusDays(1);
                YearMonth startYearMonth = YearMonth.from(strTime);
                YearMonth endYearMonth = YearMonth.from(endTime);
                long monthsBetween = startYearMonth.until(endYearMonth, ChronoUnit.MONTHS);
                months = Math.toIntExact(monthsBetween) + 1;
            } else {
                months = 1;
            }
        }
        List<Map> list = new ArrayList<>();
        if (strTime != null) {
            for (int i = 0; i < months; i++) {
                Map map = homeInfoService.getYearsTime(strTime.plusMonths(i));
                list.add(map);
            }
        } else {
            if (timeType == null) {
                timeType = 1;
            }
            if (timeType == 1) {//近1个月
                list = homeInfoService.getlastMonths();
            } else if (timeType == 2) {//近1个季
                list = homeInfoService.getlastSeasons();
            } else if (timeType == 3) {//近一年
                list = homeInfoService.getlastYears();
            } else {
                list = homeInfoService.getlastYears();
            }
        }
        for (Map map : list) {
            List<DateTime> dates = homeInfoService.getTaskDatetime(map);
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.between("create_time", dates.get(0), dates.get(1));
            queryWrapper.eq("del_flag", 0);
            queryWrapper.eq("type", OrgTypeEnum.HOUSE.getValue());
            queryWrapper.eq("op_type", 3);
            queryWrapper.eq(param.getCdid() != null, "cdid", param.getCdid());
            Integer count = szPointsLogMapper.avgintegral(queryWrapper);
            /*QueryWrapper query = new QueryWrapper();
            query.between("create_time", dates.get(0), dates.get(1));
            query.eq("type", OrgTypeEnum.HOUSE.getValue());
            query.eq(param.getCdid() != null, "cdid", param.getCdid());
            query.eq("del_flag", 0);
            Integer count = szPointsLogMapper.avgintegral(query);*/
            map.put("date", dates.get(1).toString());
            map.put("count", count != null ? count : 0);
        }
        return R.ok(list);
    }

    @GetMapping("/alltask")
    @ApiOperation("全部任务记录")
    @Inner(false)
    public R alltask(Integer type, IndexQueryParam param) throws ParseException {
        LocalDate strTime = null;
        LocalDate endTime = null;
        Integer months = null;
        if (StringUtils.isNotEmpty(param.getStrDate())) {
            strTime = LocalDate.parse(param.getStrDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            if (StringUtils.isNotEmpty(param.getEndDate())) {
                endTime = LocalDate.parse(param.getEndDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")).plusMonths(1).minusDays(1);
                YearMonth startYearMonth = YearMonth.from(strTime);
                YearMonth endYearMonth = YearMonth.from(endTime);
                long monthsBetween = startYearMonth.until(endYearMonth, ChronoUnit.MONTHS);
                months = Math.toIntExact(monthsBetween) + 1;
            } else {
                months = 1;
            }
        }
        List<Map> list = new ArrayList<>();
        if (strTime != null) {
            for (int i = 0; i < months; i++) {
                Map map = homeInfoService.getYearsTime(strTime.plusMonths(i));
                list.add(map);
            }
        } else {
            if (type == null) {
                type = 1;
            }
            if (type == 1) {//近1个月
                list = homeInfoService.getlastMonths();
            } else if (type == 2) {//近1个季
                list = homeInfoService.getlastSeasons();
            } else if (type == 3) {//近一年
                list = homeInfoService.getlastYears();
            } else {
                list = homeInfoService.getlastYears();
            }
        }
        for (Map map : list) {
            List<DateTime> dates = homeInfoService.getTaskDatetime(map);
            QueryWrapper query = new QueryWrapper();
            query.between("p.create_time", dates.get(0), dates.get(1));
            query.eq(param.getCdid() != null, "t.cdid", param.getCdid());
            query.eq("p.del_flag", 0);
            //query.eq("p.state",1);
            Integer count = szPointsLogMapper.alltask(query);
            map.put("date", dates.get(1).toString());
            map.put("count", count != null ? count : 0);
        }
        return R.ok(list);
    }

    @GetMapping("/allUser")
    @ApiOperation("全部村民")
    @Inner(false)
    public R allUser(Integer type) throws ParseException {
        List<Map> list = new ArrayList<>();
        QueryWrapper<SzUser> query = new QueryWrapper<>();
        //query.eq("a.login",1);
        query.eq("a.del_flag", "0");
        query.groupBy("a.did , a.cdid");
        List<ManUserResult> manUserResults = szManUserMapper.alluserList(query);
        for (ManUserResult manUserResult : manUserResults) {
            String name = manUserResult.getDept() + manUserResult.getArea();
            Map map = new HashMap();
            map.put("date", name);
            map.put("count", manUserResult.getTotal());
            list.add(map);
        }
        return R.ok(list);
    }

    @GetMapping("/allpoints")
    @ApiOperation("全部奖惩")
    @Inner(false)
    public R allpoints(Integer type) throws ParseException {
        List<Map> list = new ArrayList<>();
        QueryWrapper<SzUser> query = new QueryWrapper<>();
        //query.eq("a.login",1);
        query.eq("a.del_flag", "0");
        query.groupBy("a.did , a.cdid");
        List<ManUserResult> manUserResults = szManUserMapper.alluserList(query);
        for (ManUserResult manUserResult : manUserResults) {
            String name = manUserResult.getDept() + manUserResult.getArea();
            QueryWrapper query1 = new QueryWrapper();
            query1.eq("type", OrgTypeEnum.HOUSE.getValue());
            query1.eq("op_type", PointsTypeEnum.TASK.getValue());
            query1.eq("did", manUserResult.getDid());
            query1.eq("cdid", manUserResult.getCdid());
            query1.eq("del_flag", "0");
            QueryWrapper query2 = new QueryWrapper();
            query2.eq("type", OrgTypeEnum.HOUSE.getValue());
            query2.eq("op_type", PointsTypeEnum.TASK.getValue());
            query2.eq("did", manUserResult.getDid());
            query2.eq("cdid", manUserResult.getCdid());
            query2.eq("del_flag", "0");

            query1.eq("operator", "1");
            Integer count1 = szPointsLogMapper.allpoints(query1);

            query2.eq("operator", "0");
            Integer count2 = szPointsLogMapper.allpoints(query2);
            Map map = new HashMap();
            map.put("date", name);
            map.put("count1", count1);
            map.put("count2", count2);
            list.add(map);
        }
        return R.ok(list);
    }

    @GetMapping("/taskrank")
    @ApiOperation("最近任务完成记录")
    @Inner(false)
    public R taskrank(IndexQueryParam param) {
        SysUser sysUser = sysUserService.getById(SecurityUtils.getUser().getId());
        param.setPid(sysDeptService.getpid(sysUser.getDeptId()));
        List<Integer> ids = null;
        if (param.getPid() != null) {
            if (param.getPid() != 1) {
                param.setZtype(1);
            }
            ids = sysDeptService.getidsbypid(param.getPid());
        }
        if (StringUtils.isNotEmpty(param.getStrDate())) {
            param.setStrDate(param.getStrDate() + " 00:00:00");
            if (StringUtils.isNotEmpty(param.getEndDate())) {
                param.setEndDate(param.getEndDate() + " 23:59:59");
            } else {
                param.setEndDate(param.getStrDate() + " 23:59:59");
            }
            LocalDateTime endTime = LocalDateTime.parse(param.getEndDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            param.setEndDate(endTime.plusMonths(1).minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        QueryWrapper<TaskRecordParam> query = new QueryWrapper();
        query.eq("a.del_flag", "0");
        query.eq("b.del_flag", "0");
        query.and(param.getCdid() != null, y -> y.apply(" FIND_IN_SET ( '" + param.getCdid() + "',b.cdid )")
                .or(x -> x.apply("FIND_IN_SET ( '" + param.getCdid() + "',b.did )"))
        );
        query.in(ids != null && ids.size() > 0, "b.did", ids);
        query.apply(StringUtils.isNotEmpty(param.getStrDate()), "a.create_time BETWEEN '" + param.getStrDate() + "' AND '" + param.getEndDate() + "'");
        query.orderByDesc("a.create_time");
        query.last("limit 10");
        List<TaskRecordParam> szTaskRecords = szTaskRecordMapper.getPages(query);
       /* List<SzTaskRecord> szTaskRecords = szTaskRecordService.lambdaQuery()
                .eq(SzTaskRecord::getDelFlag, "0")
                .eq(SzTaskRecord::getState, "1")
                .orderByDesc(SzTaskRecord::getCreateTime)
                .last("limit 10")
                .list();*/
        /*List<SzPointsLog> szPointsLogs = szPointsLogMapper.getLeaderPointsLog(query);
         *//* .eq(SzPointsLog::getType, OrgTypeEnum.HOUSE.getValue())
                .eq(SzPointsLog::getOpType, 4)
                .eq(SzPointsLog::getDelFlag, "0")
                .orderByDesc(SzPointsLog::getCreateTime)
                .last("limit 10")
                .list();*/
        if (param.getCdid() != null) {
            if (sysDeptService.getpid(param.getDid()) != 1) {
                param.setZtype(1);
            }
        }
        for (SzTaskRecord szPointsLog : szTaskRecords) {
            SzTask szTask = szTaskService.getById(szPointsLog.getTime());
            if (szTask != null) {
                szPointsLog.setTname(szTask.getName());
                szPointsLog.setType(szTask.getType());
            }
            SzManUser szManUser = szManUserService.getById(szPointsLog.getUid());
            if (szManUser != null) {
                if (param.getZtype() != null && param.getZtype() == 1) {
                } else {
                    if (StringUtils.isNotEmpty(szManUser.getName())) {
                        szPointsLog.setUserName(szManUser.getName());
                    } else {
                        szPointsLog.setUserName(szManUser.getZname());
                    }
                }
            }
        }
        return R.ok(szTaskRecords);
    }

    @ApiOperation(value = "查询统计数据", notes = "查询统计数据")
    @GetMapping("/read/statistics")
    public R readStatistics(IndexQueryParam param) {
        SysUser sysUser = sysUserService.getById(SecurityUtils.getUser().getId());
        param.setPid(sysDeptService.getpid(sysUser.getDeptId()));
        if (param.getDid() != null) {
            if (sysDeptService.getpid(param.getDid()) != null) {
                param.setPid(sysDeptService.getpid(param.getDid()));
                if (sysDeptService.getpid(param.getDid()) != 1) {
                    param.setZtype(1);
                }
            }
        }
        List<Integer> ids = null;
        if (param.getPid() != null) {
            if (param.getPid() != 1) {
                param.setZtype(1);
            }
            ids = sysDeptService.getidsbypid(param.getPid());
        }
        if (StringUtils.isNotEmpty(param.getStrDate())) {
            param.setStrDate(param.getStrDate() + " 00:00:00");
            if (StringUtils.isNotEmpty(param.getEndDate())) {
                param.setEndDate(param.getEndDate() + " 23:59:59");
            } else {
                param.setEndDate(param.getStrDate() + " 23:59:59");
            }
            LocalDateTime endTime = LocalDateTime.parse(param.getEndDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            param.setEndDate(endTime.plusMonths(1).minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }

        Map result = new HashMap();
        Integer manUserCount = null;
        if (param.getZtype() != null && param.getZtype() == 1) {
            manUserCount = szManUserService.lambdaQuery()
                    .eq(SzManUser::getLogin, 1)
                    .eq(SzManUser::getAuditFlag, 1)
                    .eq(SzManUser::getDelFlag, "0")
                    .and(param.getCdid() != null, y -> y.apply("zcdid = " + param.getCdid() + " ")
                            .or(x -> x.apply("zdid = " + param.getCdid() + " "))
                    )
                    .in(ids != null, SzManUser::getZdid, ids)
                    .apply(StringUtils.isNotEmpty(param.getStrDate()), "create_time BETWEEN '" + param.getStrDate() + "' AND '" + param.getEndDate() + "'")
                    .count();
        } else {
            manUserCount = szManUserService.lambdaQuery()
                    .eq(SzManUser::getLogin, 1)
                    .eq(SzManUser::getAuditFlag, 1)
                    .eq(SzManUser::getDelFlag, "0")
                    .isNotNull(SzManUser::getIdcard)
                    .and(param.getCdid() != null, y -> y.apply("cdid = " + param.getCdid() + " ")
                            .or(x -> x.apply("did = " + param.getCdid() + " "))
                    )
                    .in(ids != null, SzManUser::getDid, ids)
                    .apply(StringUtils.isNotEmpty(param.getStrDate()), "create_time BETWEEN '" + param.getStrDate() + "' AND '" + param.getEndDate() + "'")
                    .count();
        }
        QueryWrapper<SzManUser> query = new QueryWrapper<>();
        query.eq("del_flag", "0");
        query.eq("audit_flag", 1);
        query.eq("login", 1);
        query.isNotNull("idcard");
        query.and(param.getCdid() != null, y -> y.apply("cdid = " + param.getCdid() + " ")
                .or(x -> x.apply("did = " + param.getCdid() + " "))
        );
        query.apply(StringUtils.isNotEmpty(param.getStrDate()), "create_time BETWEEN '" + param.getStrDate() + "' AND '" + param.getEndDate() + "'");
        Integer houseCount = null;
        if (param.getZtype() != null && param.getZtype() == 1) {
            houseCount = manUserCount;
        } else {
            houseCount = szManUserMapper.alluserCount(query);
        }
        Integer shopCount = szShopUserService.lambdaQuery()
                .eq(SzShopUser::getLogin, 1)
                .eq(SzShopUser::getAuditFlag, 1)
                .eq(SzShopUser::getDelFlag, "0")
                .and(param.getCdid() != null, y -> y.apply("cdid = " + param.getCdid() + " ")
                        .or(x -> x.apply("did = " + param.getCdid() + " "))
                )
                .in(ids != null, SzShopUser::getDid, ids)
                .apply(StringUtils.isNotEmpty(param.getStrDate()), "create_time BETWEEN '" + param.getStrDate() + "' AND '" + param.getEndDate() + "'")
                .count();

        QueryWrapper<SzShopUser> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("a.del_flag", "0");
        queryWrapper1.eq("a.state", "1");
        queryWrapper1.and(param.getCdid() != null, y -> y.apply(" FIND_IN_SET ( '" + param.getCdid() + "',b.cdid )")
                .or(x -> x.apply("FIND_IN_SET ( '" + param.getCdid() + "',b.did )"))
        );
        queryWrapper1.in(ids != null, "b.did", ids);
        queryWrapper1.apply(StringUtils.isNotEmpty(param.getStrDate()), "a.create_time BETWEEN '" + param.getStrDate() + "' AND '" + param.getEndDate() + "'");
        Integer taskcount = szTaskRecordMapper.deductListCount(queryWrapper1);
                /*.eq(SzTaskRecord::getDelFlag, "0")
                .eq(SzTaskRecord::getState, 1)
                .and(param.getCdid()!=null,y->y.apply(" FIND_IN_SET ( '"+ param.getCdid() +"',cdid )")
                        .or(x->x.apply("FIND_IN_SET ( '"+ param.getCdid() +"',did )"))
                )
                .apply(StringUtils.isNotEmpty(param.getStrDate()),"create_time BETWEEN '" + param.getStrDate() + "' AND '" + param.getEndDate() + "'")
                .count();*/

        QueryWrapper<SzShopOrder> queryWrapper = new QueryWrapper();
        queryWrapper.eq("s.state", 1);
        queryWrapper.eq("s.del_flag", "0");
        if (param.getZtype() != null && param.getZtype() == 1) {
            queryWrapper.and(param.getCdid() != null, y -> y.apply("b.zcdid = " + param.getCdid() + " ")
                    .or(x -> x.apply("b.zdid = " + param.getCdid() + " "))
            );
            queryWrapper.in(ids != null, "b.zdid", ids);
        } else {
            queryWrapper.and(param.getCdid() != null, y -> y.apply("b.cdid = " + param.getCdid() + " ")
                    .or(x -> x.apply("b.did = " + param.getCdid() + " "))
            );
            queryWrapper.in(ids != null, "b.did", ids);
        }
        queryWrapper.apply(StringUtils.isNotEmpty(param.getStrDate()), "s.create_time BETWEEN '" + param.getStrDate() + "' AND '" + param.getEndDate() + "'");

        Integer shopOderCount = szShopOrderMapper.orderPointCount(queryWrapper);
        //所有居民数量
        result.put("manUserCount", manUserCount);
        //所有户数量
        result.put("houseCount", houseCount);
        //所有商户数量
        result.put("shopCount", shopCount);
        //所有任务完成数量
        result.put("taskcount", taskcount);
        //全部兑换积分数量
        result.put("shopOderCount", shopOderCount);
        return R.ok(result);
    }

    @GetMapping("/shenhetaskactiveline")
    @ApiOperation("积分任务审核列表(选择镇)折线图")
    public R shenhetaskactiveline(Integer type, IndexQueryParam param) throws ParseException {
        if (param.getDid() == null) {
            param.setDid(1);
        }
        List<SysDept> depts = sysDeptService.lambdaQuery()
                .eq(SysDept::getDelFlag, 0)
                .eq(SysDept::getParentId, param.getDid())
                .list();
        LocalDate strTime = null;
        LocalDate endTime = null;
        Integer months = null;
        if (StringUtils.isNotEmpty(param.getStrDate())) {
            strTime = LocalDate.parse(param.getStrDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            if (StringUtils.isNotEmpty(param.getEndDate())) {
                endTime = LocalDate.parse(param.getEndDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")).plusMonths(1).minusDays(1);
                YearMonth startYearMonth = YearMonth.from(strTime);
                YearMonth endYearMonth = YearMonth.from(endTime);
                long monthsBetween = startYearMonth.until(endYearMonth, ChronoUnit.MONTHS);
                months = Math.toIntExact(monthsBetween) + 1;
            } else {
                months = 1;
            }
        }
        for (SysDept sysDept : depts) {
            List<Map> list = new ArrayList<>();
            if (strTime != null) {
                for (int i = 0; i < months; i++) {
                    Map map = homeInfoService.getYearsTime(strTime.plusMonths(i));
                    list.add(map);
                }
            } else {
                if (type == null) {
                    type = 1;
                }
                if (type == 1) {//近1个月
                    list = homeInfoService.getlastMonths();
                } else if (type == 2) {//近1个季
                    list = homeInfoService.getlastSeasons();
                } else if (type == 3) {//近一年
                    list = homeInfoService.getlastYears();
                } else {
                    list = homeInfoService.getlastYears();
                }
            }
            for (Map map : list) {
                List<DateTime> dates = homeInfoService.getTaskDatetime(map);
                QueryWrapper query1 = new QueryWrapper();
                query1.eq("a.del_flag", 0);
                query1.eq("a.state", 1);
                query1.eq("u.del_flag", 0);
                query1.eq("u.login", 1);
                query1.eq("u.audit_flag", 1);
                query1.between("a.create_time", dates.get(0), dates.get(1));
                query1.eq(sysDept.getParentId() == 1, "u.did", sysDept.getDeptId());
                query1.eq(sysDept.getParentId() != 1, "u.cdid", sysDept.getDeptId());
                Integer count = szTaskRecordMapper.usershheistCount(query1);
            /*QueryWrapper query=new QueryWrapper();
            query.between("create_time", dates.get(0),dates.get(1));
            query.eq("type", OrgTypeEnum.HOUSE.getValue());
            query.eq("op_type", PointsTypeEnum.TASK.getValue());
            Integer count = szPointsLogMapper.alltask(query);*/
                map.put("date", dates.get(1).toString());
                map.put("count", count != null ? count : 0);
            }
            sysDept.setMaps(list);
        }
        return R.ok(depts);
    }

    @GetMapping("/shenhetaskactive")
    @ApiOperation("积分任务审核列表(选择镇)")
    public R shenhetaskactive(IndexQueryParam param) {
        Page<IndexQueryParam> page = BaseQueryToPageUtil.createPage(param);
        if (StringUtils.isNotEmpty(param.getStrDate())) {
            param.setStrDate(param.getStrDate() + " 00:00:00");
            if (StringUtils.isNotEmpty(param.getEndDate())) {
                LocalDate endTime = LocalDate.parse(param.getEndDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                param.setEndDate(endTime.plusMonths(1).minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " 23:59:59");
            } else {
                LocalDate strTime = LocalDate.parse(param.getStrDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                param.setEndDate(strTime.plusMonths(1).minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " 23:59:59");
            }
        }
        Page<RoleVo> list = new Page<>();
        if (param.getDid() != null && param.getDid() != 1) {
            QueryWrapper<SzTaskRecord> queryWrapper = new QueryWrapper();
            queryWrapper.eq("t.del_flag", 0);
            queryWrapper.eq("t.state", 1);
            queryWrapper.eq("u.del_flag", 0);
            queryWrapper.eq("u.audit_flag", 1);
            queryWrapper.eq("d.del_flag", 0);
            queryWrapper.apply(StringUtils.isNotEmpty(param.getStrDate()), "t.create_time BETWEEN '" + param.getStrDate() + "' AND '" + param.getEndDate() + "'");
            queryWrapper.eq("u.did", param.getDid());
            //queryWrapper.apply("FIND_IN_SET ( '"+ param.getCdid() +"',u.cdid )");
            queryWrapper.groupBy("u.cdid");
            queryWrapper.orderByDesc("count");
            list = szTaskRecordMapper.getshenheactivecdidList(page, queryWrapper);
            for (RoleVo roleVo : list.getRecords()) {
                QueryWrapper query1 = new QueryWrapper();
                query1.eq("a.del_flag", 0);
                query1.eq("u.del_flag", 0);
                query1.eq("u.login", 1);
                query1.eq("u.audit_flag", 1);
                query1.eq("u.cdid", roleVo.getCdid());
                Integer taskCount = szTaskRecordMapper.usershheistCount(query1);
                        /*szManUserService.lambdaQuery()
                        .eq(SzManUser::getLogin, 1)
                        .eq(SzManUser::getAuditFlag, 1)
                        .eq(SzManUser::getDelFlag, "0")
                        .eq(SzManUser::getCdid, roleVo.getCdid())
                        .apply(StringUtils.isNotEmpty(param.getStrDate()),"create_time BETWEEN '" + param.getStrDate() + "' AND '" + param.getEndDate() + "'")
                        .count();*/
                roleVo.setNumber(taskCount);
                //roleVo.setNumber(roleVo.getCount()/manUserCount*100);
            }
        } else {
            QueryWrapper<SzTaskRecord> queryWrapper = new QueryWrapper();
            queryWrapper.eq("t.del_flag", 0);
            queryWrapper.eq("u.del_flag", 0);
            queryWrapper.eq("t.state", 1);
            queryWrapper.eq("u.audit_flag", 1);
            queryWrapper.eq("d.del_flag", 0);
            queryWrapper.apply(StringUtils.isNotEmpty(param.getStrDate()), "t.create_time BETWEEN '" + param.getStrDate() + "' AND '" + param.getEndDate() + "'");
            //queryWrapper.eq("u.did",param.getDid());
            //queryWrapper.apply(param.getDid()!=null,"FIND_IN_SET ( '"+ param.getDid() +"',u.did )");
            queryWrapper.groupBy("u.did");
            queryWrapper.orderByDesc("count");
            list = szTaskRecordMapper.getshgenheactiveList(page, queryWrapper);
            for (RoleVo roleVo : list.getRecords()) {
                QueryWrapper query1 = new QueryWrapper();
                query1.eq("a.del_flag", 0);
                query1.eq("u.del_flag", 0);
                query1.eq("u.login", 1);
                query1.eq("u.audit_flag", 1);
                query1.eq("u.did", roleVo.getDid());
                Integer taskCount = szTaskRecordMapper.usershheistCount(query1);
                         /*szManUserService.lambdaQuery()
                        .eq(SzManUser::getLogin, 1)
                        .eq(SzManUser::getAuditFlag, 1)
                        .eq(SzManUser::getDelFlag, "0")
                        .eq(SzManUser::getDid, roleVo.getDid())
                        .apply(StringUtils.isNotEmpty(param.getStrDate()),"create_time BETWEEN '" + param.getStrDate() + "' AND '" + param.getEndDate() + "'")
                        .count();*/
                roleVo.setNumber(taskCount);
                //roleVo.setNumber(roleVo.getCount()/manUserCount*100);
            }
        }
        return R.ok(list);
    }

    @GetMapping("/shhetaskactiveExcel")
    @ApiOperation("积分任务审核列表(选择镇)导出")
    public void shhetaskactiveExcel(IndexQueryParam param, HttpServletResponse response) {
        Page<IndexQueryParam> page = BaseQueryToPageUtil.createPage(param);
        if (StringUtils.isNotEmpty(param.getStrDate())) {
            param.setStrDate(param.getStrDate() + " 00:00:00");
            if (StringUtils.isNotEmpty(param.getEndDate())) {
                LocalDate endTime = LocalDate.parse(param.getEndDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                param.setEndDate(endTime.plusMonths(1).minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " 23:59:59");
            } else {
                LocalDate strTime = LocalDate.parse(param.getStrDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                param.setEndDate(strTime.plusMonths(1).minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " 23:59:59");
            }
        }
        List<RoleVo> list = new ArrayList<>();
        if (param.getDid() != null && param.getDid() != 1) {
            QueryWrapper<SzTaskRecord> queryWrapper = new QueryWrapper();
            queryWrapper.eq("t.del_flag", 0);
            queryWrapper.eq("t.state", 1);
            queryWrapper.eq("u.del_flag", 0);
            queryWrapper.eq("u.audit_flag", 1);
            queryWrapper.eq("d.del_flag", 0);
            queryWrapper.apply(StringUtils.isNotEmpty(param.getStrDate()), "t.create_time BETWEEN '" + param.getStrDate() + "' AND '" + param.getEndDate() + "'");
            queryWrapper.eq("u.did", param.getDid());
            //queryWrapper.apply("FIND_IN_SET ( '"+ param.getCdid() +"',u.cdid )");
            queryWrapper.groupBy("u.cdid");
            queryWrapper.orderByDesc("count");
            list = szTaskRecordMapper.getshenheactivecdidLists(queryWrapper);
            for (RoleVo roleVo : list) {
                QueryWrapper query1 = new QueryWrapper();
                query1.eq("a.del_flag", 0);
                query1.eq("u.del_flag", 0);
                query1.eq("u.login", 1);
                query1.eq("u.audit_flag", 1);
                query1.eq("u.cdid", roleVo.getCdid());
                Integer taskCount = szTaskRecordMapper.usershheistCount(query1);
                        /*szManUserService.lambdaQuery()
                        .eq(SzManUser::getLogin, 1)
                        .eq(SzManUser::getAuditFlag, 1)
                        .eq(SzManUser::getDelFlag, "0")
                        .eq(SzManUser::getCdid, roleVo.getCdid())
                        .apply(StringUtils.isNotEmpty(param.getStrDate()),"create_time BETWEEN '" + param.getStrDate() + "' AND '" + param.getEndDate() + "'")
                        .count();*/
                roleVo.setNumber(taskCount);
                //roleVo.setNumber(roleVo.getCount()/manUserCount*100);
            }
        } else {
            QueryWrapper<SzTaskRecord> queryWrapper = new QueryWrapper();
            queryWrapper.eq("t.del_flag", 0);
            queryWrapper.eq("u.del_flag", 0);
            queryWrapper.eq("t.state", 1);
            queryWrapper.eq("u.audit_flag", 1);
            queryWrapper.eq("d.del_flag", 0);
            queryWrapper.apply(StringUtils.isNotEmpty(param.getStrDate()), "t.create_time BETWEEN '" + param.getStrDate() + "' AND '" + param.getEndDate() + "'");
            //queryWrapper.eq("u.did",param.getDid());
            //queryWrapper.apply(param.getDid()!=null,"FIND_IN_SET ( '"+ param.getDid() +"',u.did )");
            queryWrapper.groupBy("u.did");
            queryWrapper.orderByDesc("count");
            list = szTaskRecordMapper.getshenheactivecdidLists(queryWrapper);
            for (RoleVo roleVo : list) {
                QueryWrapper query1 = new QueryWrapper();
                query1.eq("a.del_flag", 0);
                query1.eq("u.del_flag", 0);
                query1.eq("u.login", 1);
                query1.eq("u.audit_flag", 1);
                query1.eq("u.did", roleVo.getDid());
                Integer taskCount = szTaskRecordMapper.usershheistCount(query1);
                         /*szManUserService.lambdaQuery()
                        .eq(SzManUser::getLogin, 1)
                        .eq(SzManUser::getAuditFlag, 1)
                        .eq(SzManUser::getDelFlag, "0")
                        .eq(SzManUser::getDid, roleVo.getDid())
                        .apply(StringUtils.isNotEmpty(param.getStrDate()),"create_time BETWEEN '" + param.getStrDate() + "' AND '" + param.getEndDate() + "'")
                        .count();*/
                roleVo.setNumber(taskCount);
                //roleVo.setNumber(roleVo.getCount()/manUserCount*100);
            }
        }
        List<PagetasksVo> pagetaskVos = new ArrayList<>();
        for (RoleVo roleVo : list) {
            PagetasksVo pagetaskVo = new PagetasksVo();
            BeanUtils.copyProperties(roleVo, pagetaskVo);
            String name = roleVo.getDname();
            if (StringUtils.isNotEmpty(roleVo.getCdname())) {
                name += "-" + roleVo.getCdname();
            }
            pagetaskVo.setName(name);
            pagetaskVos.add(pagetaskVo);
        }
        ExcelBaseUtil.exportExcel(pagetaskVos, "积分任务审核效率", "积分任务审核效率", PagetasksVo.class, "积分任务审核效率.xls", response);
        //return R.ok(list);
    }

    @GetMapping("/taskactiveline")
    @ApiOperation("积分任务活跃列表(选择镇)折线图")
    public R taskactiveline(Integer type, IndexQueryParam param) throws ParseException {
        if (param.getDid() == null) {
            param.setDid(1);
        }
        List<SysDept> depts = sysDeptService.lambdaQuery()
                .eq(SysDept::getDelFlag, 0)
                .eq(SysDept::getParentId, param.getDid())
                .list();
        LocalDate strTime = null;
        LocalDate endTime = null;
        Integer months = null;
        if (StringUtils.isNotEmpty(param.getStrDate())) {
            strTime = LocalDate.parse(param.getStrDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            if (StringUtils.isNotEmpty(param.getEndDate())) {
                endTime = LocalDate.parse(param.getEndDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")).plusMonths(1).minusDays(1);
                YearMonth startYearMonth = YearMonth.from(strTime);
                YearMonth endYearMonth = YearMonth.from(endTime);
                long monthsBetween = startYearMonth.until(endYearMonth, ChronoUnit.MONTHS);
                months = Math.toIntExact(monthsBetween) + 1;
            } else {
                months = 1;
            }
        }
        for (SysDept sysDept : depts) {
            List<Map> list = new ArrayList<>();
            if (strTime != null) {
                for (int i = 0; i < months; i++) {
                    Map map = homeInfoService.getYearsTime(strTime.plusMonths(i));
                    list.add(map);
                }
            } else {
                if (type == null) {
                    type = 1;
                }
                if (type == 1) {//近1个月
                    list = homeInfoService.getlastMonths();
                } else if (type == 2) {//近1个季
                    list = homeInfoService.getlastSeasons();
                } else if (type == 3) {//近一年
                    list = homeInfoService.getlastYears();
                } else {
                    list = homeInfoService.getlastYears();
                }
            }
            for (Map map : list) {
                List<DateTime> dates = homeInfoService.getTaskDatetime(map);
                QueryWrapper query1 = new QueryWrapper();
                query1.eq("a.del_flag", 0);
                query1.eq("u.del_flag", 0);
                query1.eq("u.login", 1);
                query1.eq("u.audit_flag", 1);
                query1.between("a.create_time", dates.get(0), dates.get(1));
                query1.eq(sysDept.getParentId() == 1, "u.did", sysDept.getDeptId());
                query1.eq(sysDept.getParentId() != 1, "u.cdid", sysDept.getDeptId());
                Integer count = szTaskRecordMapper.userListCount(query1);
            /*QueryWrapper query=new QueryWrapper();
            query.between("create_time", dates.get(0),dates.get(1));
            query.eq("type", OrgTypeEnum.HOUSE.getValue());
            query.eq("op_type", PointsTypeEnum.TASK.getValue());
            Integer count = szPointsLogMapper.alltask(query);*/
                map.put("date", dates.get(1).toString());
                map.put("count", count != null ? count : 0);
            }
            sysDept.setMaps(list);
        }
        return R.ok(depts);
    }

    @GetMapping("/taskactive")
    @ApiOperation("积分任务活跃列表(选择镇)")
    public R taskactive(IndexQueryParam param) {
        Page<IndexQueryParam> page = BaseQueryToPageUtil.createPage(param);
        if (StringUtils.isNotEmpty(param.getStrDate())) {
            param.setStrDate(param.getStrDate() + " 00:00:00");
            if (StringUtils.isNotEmpty(param.getEndDate())) {
                LocalDate endTime = LocalDate.parse(param.getEndDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                param.setEndDate(endTime.plusMonths(1).minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " 23:59:59");
            } else {
                LocalDate strTime = LocalDate.parse(param.getStrDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                param.setEndDate(strTime.plusMonths(1).minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " 23:59:59");
            }
        }
        Page<RoleVo> list = new Page<>();
        if (param.getDid() != null && param.getDid() != 1) {
            QueryWrapper<SzTaskRecord> queryWrapper = new QueryWrapper();
            queryWrapper.eq("t.del_flag", 0);
            queryWrapper.eq("u.del_flag", 0);
            queryWrapper.eq("u.audit_flag", 1);
            queryWrapper.eq("d.del_flag", 0);
            queryWrapper.apply(StringUtils.isNotEmpty(param.getStrDate()), "t.create_time BETWEEN '" + param.getStrDate() + "' AND '" + param.getEndDate() + "'");
            queryWrapper.eq("u.did", param.getDid());
            //queryWrapper.apply("FIND_IN_SET ( '"+ param.getCdid() +"',u.cdid )");
            queryWrapper.groupBy("u.cdid");
            queryWrapper.orderByDesc("count");
            list = szTaskRecordMapper.getactivecdidList(page, queryWrapper);
            for (RoleVo roleVo : list.getRecords()) {
                Integer manUserCount = szManUserService.lambdaQuery()
                        .eq(SzManUser::getLogin, 1)
                        .eq(SzManUser::getAuditFlag, 1)
                        .eq(SzManUser::getDelFlag, "0")
                        .eq(SzManUser::getCdid, roleVo.getCdid())
                        //.apply(StringUtils.isNotEmpty(param.getStrDate()),"create_time BETWEEN '" + param.getStrDate() + "' AND '" + param.getEndDate() + "'")
                        .count();
                roleVo.setNumber(manUserCount);
                //roleVo.setNumber(roleVo.getCount()/manUserCount*100);
            }
        } else {
            QueryWrapper<SzTaskRecord> queryWrapper = new QueryWrapper();
            queryWrapper.eq("t.del_flag", 0);
            queryWrapper.eq("u.del_flag", 0);
            queryWrapper.eq("u.audit_flag", 1);
            queryWrapper.eq("d.del_flag", 0);
            queryWrapper.apply(StringUtils.isNotEmpty(param.getStrDate()), "t.create_time BETWEEN '" + param.getStrDate() + "' AND '" + param.getEndDate() + "'");
            //queryWrapper.eq("u.did",param.getDid());
            //queryWrapper.apply(param.getDid()!=null,"FIND_IN_SET ( '"+ param.getDid() +"',u.did )");
            queryWrapper.groupBy("u.did");
            queryWrapper.orderByDesc("count");
            list = szTaskRecordMapper.getactiveList(page, queryWrapper);
            for (RoleVo roleVo : list.getRecords()) {
                Integer manUserCount = szManUserService.lambdaQuery()
                        .eq(SzManUser::getLogin, 1)
                        .eq(SzManUser::getAuditFlag, 1)
                        .eq(SzManUser::getDelFlag, "0")
                        .eq(SzManUser::getDid, roleVo.getDid())
                        //.apply(StringUtils.isNotEmpty(param.getStrDate()),"create_time BETWEEN '" + param.getStrDate() + "' AND '" + param.getEndDate() + "'")
                        .count();
                roleVo.setNumber(manUserCount);
                //roleVo.setNumber(roleVo.getCount()/manUserCount*100);
            }
        }
        return R.ok(list);
    }

    @GetMapping("/taskactiveExcel")
    @ApiOperation("积分任务活跃列表(选择镇)导出")
    public void taskactiveExcel(IndexQueryParam param, HttpServletResponse response) {
        //Page<IndexQueryParam> page = BaseQueryToPageUtil.createPage(param);
        if (StringUtils.isNotEmpty(param.getStrDate())) {
            param.setStrDate(param.getStrDate() + " 00:00:00");
            if (StringUtils.isNotEmpty(param.getEndDate())) {
                LocalDateTime endTime = LocalDateTime.parse(param.getEndDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                param.setEndDate(endTime.plusMonths(1).minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " 23:59:59");
            } else {
                LocalDateTime strTime = LocalDateTime.parse(param.getStrDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                param.setEndDate(strTime.plusMonths(1).minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " 23:59:59");
            }
        }
        List<RoleVo> list = new ArrayList<>();
        if (param.getDid() != null && param.getDid() != 1) {
            QueryWrapper<SzTaskRecord> queryWrapper = new QueryWrapper();
            queryWrapper.eq("t.del_flag", 0);
            queryWrapper.eq("u.del_flag", 0);
            queryWrapper.eq("u.audit_flag", 1);
            queryWrapper.eq("d.del_flag", 0);
            queryWrapper.apply(StringUtils.isNotEmpty(param.getStrDate()), "t.create_time BETWEEN '" + param.getStrDate() + "' AND '" + param.getEndDate() + "'");
            queryWrapper.eq("u.did", param.getDid());
            //queryWrapper.apply("FIND_IN_SET ( '"+ param.getCdid() +"',u.cdid )");
            queryWrapper.groupBy("u.cdid");
            queryWrapper.orderByDesc("count");
            list = szTaskRecordMapper.getactivecdidLists(queryWrapper);
            for (RoleVo roleVo : list) {
                Integer manUserCount = szManUserService.lambdaQuery()
                        .eq(SzManUser::getLogin, 1)
                        .eq(SzManUser::getAuditFlag, 1)
                        .eq(SzManUser::getDelFlag, "0")
                        .eq(SzManUser::getCdid, roleVo.getCdid())
                        //.apply(StringUtils.isNotEmpty(param.getStrDate()),"create_time BETWEEN '" + param.getStrDate() + "' AND '" + param.getEndDate() + "'")
                        .count();
                roleVo.setNumber(manUserCount);
                //roleVo.setNumber(roleVo.getCount()/manUserCount*100);
            }
        } else {
            QueryWrapper<SzTaskRecord> queryWrapper = new QueryWrapper();
            queryWrapper.eq("t.del_flag", 0);
            queryWrapper.eq("u.del_flag", 0);
            queryWrapper.eq("u.audit_flag", 1);
            queryWrapper.eq("d.del_flag", 0);
            queryWrapper.apply(StringUtils.isNotEmpty(param.getStrDate()), "t.create_time BETWEEN '" + param.getStrDate() + "' AND '" + param.getEndDate() + "'");
            //queryWrapper.eq("u.did",param.getDid());
            //queryWrapper.apply(param.getDid()!=null,"FIND_IN_SET ( '"+ param.getDid() +"',u.did )");
            queryWrapper.groupBy("u.did");
            queryWrapper.orderByDesc("count");
            list = szTaskRecordMapper.getactivecdidLists(queryWrapper);
            for (RoleVo roleVo : list) {
                Integer manUserCount = szManUserService.lambdaQuery()
                        .eq(SzManUser::getLogin, 1)
                        .eq(SzManUser::getAuditFlag, 1)
                        .eq(SzManUser::getDelFlag, "0")
                        .eq(SzManUser::getDid, roleVo.getDid())
                        //.apply(StringUtils.isNotEmpty(param.getStrDate()),"create_time BETWEEN '" + param.getStrDate() + "' AND '" + param.getEndDate() + "'")
                        .count();
                roleVo.setNumber(manUserCount);
                //roleVo.setNumber(roleVo.getCount()/manUserCount*100);
            }
        }
        List<PagetaskVo> pagetaskVos = new ArrayList<>();
        for (RoleVo roleVo : list) {
            PagetaskVo pagetaskVo = new PagetaskVo();
            BeanUtils.copyProperties(roleVo, pagetaskVo);
            String name = roleVo.getDname();
            if (StringUtils.isNotEmpty(roleVo.getCdname())) {
                name += "-" + roleVo.getCdname();
            }
            double percent = ((double) roleVo.getCount() / roleVo.getNumber()) * 100;
            BigDecimal bd = new BigDecimal(percent).setScale(2, RoundingMode.UP);
            pagetaskVo.setCountv(bd + "%");
            pagetaskVo.setName(name);
            pagetaskVos.add(pagetaskVo);
        }
        ExcelBaseUtil.exportExcel(pagetaskVos, "积分任务活跃率", "积分任务活跃率", PagetaskVo.class, "积分任务活跃率.xls", response);
        //return R.ok(list);
    }

    /*  public static void main(String[] args) {
          Integer i = 3;
          Integer y = 14;
          double percent = ((double) i / y) * 100;
          BigDecimal bd = new BigDecimal(percent).setScale(2, RoundingMode.UP);
          System.out.println(bd);

      }*/
    @GetMapping("/taskroleopen")
    @ApiOperation("积分任务统计")
    public R taskroleopen() {
        Integer userid = SecurityUtils.getUser().getId();
        SysUser user = sysUserService.getById(userid);
        Integer roleId = userRoleService.lambdaQuery().eq(SysUserRole::getUserId, user.getUserId()).select(SysUserRole::getRoleId).one().getRoleId();
        String[] p = null;
        List<Integer> cdid = new ArrayList<>();
        if (roleId != null && roleId != 1 && user.getDeptId() != null && user.getDeptId() != 0 && StringUtils.isNotEmpty(user.getPermission())) {//&& user.getDeptId() != 1
            p = user.getPermission().split(",");
            for (String s : p) {
                if (isNumeric(s)) {
                    cdid.add(Integer.parseInt(s));
                }
            }
        }
        Integer root = null;
        if (cdid.size() == 1) {
            root = cdid.get(0);
        }
        Integer alltask = szTaskService.lambdaQuery()
                .eq(SzTask::getArea, 1)
                .eq(SzTask::getState, 1)
                //.apply(cdid!=null,"FIND_IN_SET ('" + cdid + "',cdid)")
                //.in(cdid != null&&cdid.size()>0, SzTask::getCdid, cdid)
                .and(root == null && cdid.size() > 0, y -> y.in(SzTask::getDid, cdid)
                        .or(i -> i.in(SzTask::getCdid, cdid))
                )
                .apply(root != null, "FIND_IN_SET ( " + root + ",cdid )")
                .eq(SzTask::getDelFlag, 0).count();
        QueryWrapper<SzTask> query1 = new QueryWrapper<>();
        query1.eq("u.del_flag", 0);
        query1.eq("u.login", 1);
        query1.eq("u.audit_flag", 1);
        //query1.eq(cdid!=null,"u.cdid",cdid);
        //query1.in(cdid != null&&cdid.size()>0, "u.cdid", cdid);
        query1.and(root == null && cdid.size() > 0, y -> y.in("u.cdid", cdid)
                .or(i -> i.in("u.did", cdid))
        );
        query1.apply(root != null, "FIND_IN_SET ( " + root + ",u.cdid )");
        Integer alluser = szTaskRecordMapper.userListCount(query1);
        QueryWrapper<SzTask> query2 = new QueryWrapper<>();
        query2.eq("u.del_flag", 0);
        query2.eq("u.login", 1);
        query2.eq("u.audit_flag", 1);
        //query1.eq(cdid!=null,"u.cdid",cdid);
        query2.in(cdid.size() > 0, "u.zcdid", cdid);
        /*query2.and(cdid.size()>0, y -> y.in("u.cdid",cdid)
                .or(i -> i.in("u.did",cdid))
        );*/
        Integer alluser1 = szTaskRecordMapper.userListCount(query2);
        QueryWrapper<SzTask> query = new QueryWrapper<>();
        query.eq("b.area", 1);
        query.eq("b.state", 1);
        query.eq("b.del_flag", 0);
        //query.apply(cdid!=null,"FIND_IN_SET ('" + cdid + "',b.cdid)");
        //query.in(cdid.size()>0, "b.cdid", cdid);
        query.and(root == null && cdid.size() > 0, y -> y.in("b.cdid", cdid)
                .or(i -> i.in("b.did", cdid))
        );
        query.apply(root != null, "FIND_IN_SET ( " + root + ",b.cdid )");
        /*query.and(cdid.size()>0, y -> y.in("b.cdid",cdid)
                .or(i -> i.in("b.did",cdid))
        );*/
        Integer alltaskcount = szTaskRecordMapper.queryBadListcount(query);
        QueryWrapper<SzTask> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("a.state", 1);
        queryWrapper.eq("b.area", 1);
        queryWrapper.eq("b.state", 1);
        queryWrapper.eq("b.del_flag", 0);
        //queryWrapper.apply(cdid!=null,"FIND_IN_SET ('" + cdid + "',b.cdid)");
        //queryWrapper.in(cdid != null&&cdid.size()>0, "b.cdid", cdid);
        queryWrapper.and(root == null && cdid.size() > 0, y -> y.in("b.cdid", cdid)
                .or(i -> i.in("b.did", cdid))
        );
        queryWrapper.apply(root != null, "FIND_IN_SET ( " + root + ",b.cdid )");
        Integer allovertaskcount = szTaskRecordMapper.queryBadListcount(queryWrapper);
        /*QueryWrapper<SzTask> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("a.state", 1);
        queryWrapper1.eq("b.area", 1);
        queryWrapper1.eq("b.state", 1);
        queryWrapper1.eq("b.del_flag", 0);
        //queryWrapper.apply(cdid!=null,"FIND_IN_SET ('" + cdid + "',b.cdid)");
        //queryWrapper.in(cdid != null&&cdid.size()>0, "b.cdid", cdid);
        queryWrapper1.and(root==null&&cdid.size()>0, y -> y.in("b.cdid",cdid)
                .or(i -> i.in("b.did",cdid))
        );
        queryWrapper1.apply(root!=null,"FIND_IN_SET ( " + root + ",b.cdid )");
        Integer allovertaskcount1 = szTaskRecordMapper.queryBadListcount(queryWrapper1);*/

        QueryWrapper<SzTask> query3 = new QueryWrapper<>();
        query3.eq("st.type", 0);
        query3.eq("str.state", 1);
        query3.eq("str.del_flag", 0);
        //query2.apply(cdid!=null,"FIND_IN_SET ('" + cdid + "',st.cdid)");
        //query3.in(cdid != null&&cdid.size()>0, "st.cdid", cdid);
        query3.and(root == null && cdid.size() > 0, y -> y.in("st.cdid", cdid)
                .or(i -> i.in("st.did", cdid))
        );
        query3.apply(root != null, "FIND_IN_SET ( " + root + ",st.cdid )");
        Integer removepoints = szTaskRecordMapper.sumPoints(query3);
        Map map = new HashMap();
        //总任务数
        map.put("alltask", alltask);
        //总参与用户数
        map.put("alluser", alluser + alluser1);
        //总提交任务数
        map.put("alltaskcount", alltaskcount);
        //总完成任务数
        map.put("allovertaskcount", allovertaskcount);
        //积分消耗数
        map.put("removepoints", removepoints);
        return R.ok(map);
    }

    @GetMapping("/allroleusernum")
    @ApiOperation("各村干部绑定情况")
    public R allroleusernum(Integer did) {
        Integer userid = SecurityUtils.getUser().getId();
        SysUser user = sysUserService.getById(userid);
        if (did == null) {
            did = 1;
        }
        //各村干部数量
        List<RoleVo> roleVos = new ArrayList<>();
        List<SysDept> cdepts = sysDeptService.lambdaQuery()
                .eq(SysDept::getDelFlag, 0)
                .eq(SysDept::getParentId, did)
                .list();
        /*if(did==1){
            for (SysDept sysDept:cdepts) {
                List<SysDept> cdepts1 = sysDeptService.lambdaQuery()
                        .eq(SysDept::getDelFlag, 0)
                        .eq(SysDept::getParentId, sysDept.getDeptId())
                        .list();
                if(cdepts1!=null&&cdepts1.size()>0){
                    depts.addAll(cdepts1);
                }
            }
        }else{
            depts.addAll(cdepts);
        }*/
        //List<SysDept> depts = new ArrayList<>(cdepts);
        for (SysDept sysDept : cdepts) {
            //村已绑定的管理员数量
            QueryWrapper queryWrappers = new QueryWrapper();
            queryWrappers.eq("r.del_flag", 0);
            queryWrappers.eq("r.audit_flag", 1);
            queryWrappers.ne("r.rid", 0);
            queryWrappers.isNotNull("r.rid");
            queryWrappers.eq("r.login", 1);
            queryWrappers.eq(did == 1, "r.did", sysDept.getDeptId());
            queryWrappers.eq(did != 1, "r.cdid", sysDept.getDeptId());
            queryWrappers.eq("u.del_flag", 0);
            Integer deptbangrole = szRoleUserMapper.sbanglist(queryWrappers);
            if (deptbangrole == 0) {
                continue;
            }
            //村所有管理员
            Integer deptalluser = szRoleUserService.lambdaQuery()
                    .eq(SzRoleUser::getDelFlag, 0)
                    .eq(SzRoleUser::getLogin, 1)
                    .apply(did == 1, "FIND_IN_SET ('" + sysDept.getDeptId() + "',dids)")
                    .apply(did != 1, "FIND_IN_SET ('" + sysDept.getDeptId() + "',cdids)")
                    .eq(SzRoleUser::getAuditFlag, 1).count();
            RoleVo roleVo = new RoleVo();
            roleVo.setCount(deptalluser);
            roleVo.setNumber(deptbangrole);
            roleVo.setDid(sysDept.getParentId());
            roleVo.setCdid(sysDept.getDeptId());
            roleVo.setName(sysDept.getName());
            roleVos.add(roleVo);
        }
        return R.ok(roleVos);
    }

    @GetMapping("/roleuseropen")
    @ApiOperation("村干部统计")
    public R roleuseropen() {
        Integer userid = SecurityUtils.getUser().getId();
        SysUser user = sysUserService.getById(userid);
        Integer roleId = userRoleService.lambdaQuery().eq(SysUserRole::getUserId, user.getUserId()).select(SysUserRole::getRoleId).one().getRoleId();
        String[] cdid = null;
        Integer pid = sysDeptService.getpid(user.getDeptId());
        /*if (roleId != null && roleId != 1 && user.getDeptId() != null && user.getDeptId() != 1 && user.getDeptId() != 0) {
            if(StringUtils.isNotEmpty(user.getPermission())){
                cdid = user.getPermission().split(",");
            }else{
                return  R.ok();
            }

        }*/
        if (roleId != null && roleId != 1 && user.getDeptId() != null && user.getDeptId() != 0 && StringUtils.isNotEmpty(user.getPermission())) {
            cdid = user.getPermission().split(",");
        }
       /* List<Integer> permission;
        if (user.getDeptId() != null) { //村干部区域权限//roleId != 1 &&
            permission = sysDeptService.getidsbypid(user.getDeptId());
        } else {
            permission = null;
        }*/
        //总人数
        Integer alluser = 0;
        if (cdid != null) {
            Set set = new HashSet();
            for (String id : cdid) {
                List<SzRoleUser> SzRoleUsers = szRoleUserService.lambdaQuery()
                        .eq(SzRoleUser::getDelFlag, 0)
                        .eq(SzRoleUser::getLogin, 1)
                        .apply("FIND_IN_SET ('" + id + "',cdids)")
                        .eq(pid != null, SzRoleUser::getPid, pid)
                        .eq(SzRoleUser::getAuditFlag, 1).list();
                for (SzRoleUser szRoleUser : SzRoleUsers) {
                    set.add(szRoleUser.getId());
                }
            }
            alluser = set.size();
        } else {
            Integer number = szRoleUserService.lambdaQuery()
                    .eq(SzRoleUser::getDelFlag, 0)
                    .eq(SzRoleUser::getLogin, 1)
                    .eq(pid != null, SzRoleUser::getPid, pid)
                    .eq(SzRoleUser::getAuditFlag, 1).count();
            alluser += number;
        }


       /* //各村人数
       QueryWrapper query1=new QueryWrapper();
        query1.isNotNull("r.relation");
        query1.eq("r.del_flag",0);
        query1.eq("r.audit_flag",1);
        query1.eq("r.login",1);
        query1.groupBy(" r.relation");
        List<RoleVo> deptrole=szRoleUserMapper.rolelist(query1);*/
        //已绑定干部人数
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("r.del_flag", 0);
        queryWrapper.eq("r.audit_flag", 1);
        queryWrapper.eq("r.login", 1);
        queryWrapper.ne("r.rid", 0);
        queryWrapper.in(cdid != null, "r.cdid", cdid);
        queryWrapper.eq("u.del_flag", 0);
        Integer bangrole = szRoleUserMapper.sbanglist(queryWrapper);
        //各性别人数
        Integer mannumber = 0;
        Integer wumannumber = 0;
        Integer notxumber = 0;
        if (cdid != null) {
            for (String id : cdid) {
                QueryWrapper query1 = new QueryWrapper();
                query1.eq("r.del_flag", 0);
                query1.eq("r.audit_flag", 1);
                query1.eq("r.login", 1);
                query1.eq(" r.sex", 1);
                query1.eq(pid != null, "r.pid", pid);
                query1.apply(id != null, "FIND_IN_SET ('" + id + "',r.cdids)");
                Integer sexroles = szRoleUserMapper.shoplistnumber(query1);
                mannumber += sexroles;
                QueryWrapper query2 = new QueryWrapper();
                query2.eq("r.del_flag", 0);
                query2.eq("r.audit_flag", 1);
                query2.eq("r.login", 1);
                query2.eq(" r.sex", 2);
                query2.eq(pid != null, "r.pid", pid);
                query2.apply(id != null, "FIND_IN_SET ('" + id + "',r.cdids)");
                Integer sexroles1 = szRoleUserMapper.shoplistnumber(query2);
                wumannumber += sexroles1;
                QueryWrapper query3 = new QueryWrapper();
                query3.eq("r.del_flag", 0);
                query3.eq("r.audit_flag", 1);
                query3.eq("r.login", 1);
                query3.eq(" r.sex", 3);
                query3.eq(pid != null, "r.pid", pid);
                query3.apply(id != null, "FIND_IN_SET ('" + id + "',r.cdids)");
                Integer sexroles2 = szRoleUserMapper.shoplistnumber(query3);
                notxumber += sexroles2;
            }
        } else {
            QueryWrapper query1 = new QueryWrapper();
            query1.eq("r.del_flag", 0);
            query1.eq("r.audit_flag", 1);
            query1.eq("r.login", 1);
            query1.eq(" r.sex", 1);
            query1.eq(pid != null, "r.pid", pid);
            Integer sexroles = szRoleUserMapper.shoplistnumber(query1);
            mannumber += sexroles;
            QueryWrapper query2 = new QueryWrapper();
            query2.eq("r.del_flag", 0);
            query2.eq("r.audit_flag", 1);
            query2.eq("r.login", 1);
            query2.eq(" r.sex", 2);
            query2.eq(pid != null, "r.pid", pid);
            Integer sexroles1 = szRoleUserMapper.shoplistnumber(query2);
            wumannumber += sexroles1;
            QueryWrapper query3 = new QueryWrapper();
            query3.eq("r.del_flag", 0);
            query3.eq("r.audit_flag", 1);
            query3.eq("r.login", 1);
            query3.eq(" r.sex", 3);
            query3.eq(pid != null, "r.pid", pid);
            Integer sexroles2 = szRoleUserMapper.shoplistnumber(query3);
            notxumber += sexroles2;
        }
        /*QueryWrapper query1=new QueryWrapper();
        query1.eq("r.del_flag",0);
        query1.eq("r.audit_flag",1);
        query1.eq("r.login",1);
        query1.eq(" r.sex",1);
        query1.apply(cdid!=null,"FIND_IN_SET ('" + cdid + "',r.cdids)");
        Integer sexroles=szRoleUserMapper.shoplistnumber(query1);*/
        /*QueryWrapper query1=new QueryWrapper();
        query1.eq("r.del_flag",0);
        query1.eq("r.audit_flag",1);
        query1.eq("r.login",1);
        query1.apply(cdid!=null,"FIND_IN_SET ('" + cdid + "',r.cdids)");
        query1.groupBy(" r.sex");
        List<RoleVo> sexroles=szRoleUserMapper.shoplist(query1);*/
        RoleVo roleVo_1 = new RoleVo();
        roleVo_1.setName("0");
        roleVo_1.setCount(notxumber);
        RoleVo roleVo_2 = new RoleVo();
        roleVo_2.setName("1");
        roleVo_2.setCount(mannumber);
        RoleVo roleVo_3 = new RoleVo();
        roleVo_3.setName("2");
        roleVo_3.setCount(wumannumber);
        /*for (RoleVo roleVo :sexroles ) {
            if (roleVo.getName().equals("0")){
                roleVo_1.setCount(roleVo.getCount());
            } else if (roleVo.getName().equals("1")){
                roleVo_2.setCount(roleVo.getCount());
            } else{
                roleVo_3.setCount(roleVo.getCount());
            }
        }*/
        List<RoleVo> sexrole = new ArrayList<>();
        sexrole.add(roleVo_1);
        sexrole.add(roleVo_2);
        sexrole.add(roleVo_3);

        //各职位人数
        /*QueryWrapper query2=new QueryWrapper();
        query2.isNotNull("r.relation");
        query2.eq("r.del_flag",0);
        query2.eq("r.audit_flag",1);
        query2.eq("r.login",1);
        query2.apply(cdid!=null,"FIND_IN_SET ('" + cdid + "',r.cdids)");
        query2.groupBy("r.relation");
        List<RoleVo> workrole=szRoleUserMapper.rolelist(query2);*/

        //各月人数
        Integer year = LocalDate.now().getYear();
        Map mouths = new HashMap();
        for (int i = 1; i <= 12; i++) {
            QueryWrapper query3 = new QueryWrapper();
            query3.isNotNull("r.help");
            query3.eq("r.del_flag", 0);
            query3.eq("u.del_flag", 0);
            query3.eq("u.audit_flag", 1);
            query3.eq("u.login", 1);
            query3.apply(cdid != null, "FIND_IN_SET ('" + cdid + "',u.cdids)");

            query3.apply("date_format (r.create_time,'%Y-%m') = {0}", year + (i < 10 ? "-0" : "-") + i);
            Integer mouthrole = szRoleUserMapper.mouthrole(query3);
            mouths.put(i, mouthrole);
        }
        Map map = new HashMap();
        //总人数
        map.put("alluser", alluser);
       /* //各村人数
        map.put("deptrole",deptrole);*/
        //各性别人数
        map.put("sexrole", sexrole);
        //已绑定干部人数
        map.put("bangrole", bangrole);
        /*//各职位人数
        map.put("workrole",workrole);*/
        //各月份人数
        map.put("mouthrole", mouths);
        return R.ok(map);
    }

    @GetMapping("/shopuseropen")
    @ApiOperation("商铺统计")
    public R shopuseropen() {
        Integer userid = SecurityUtils.getUser().getId();
        SysUser user = sysUserService.getById(userid);
        Integer roleId = userRoleService.lambdaQuery().eq(SysUserRole::getUserId, user.getUserId()).select(SysUserRole::getRoleId).one().getRoleId();
        String[] cdid = null;
        if (roleId != null && roleId != 1 && user.getDeptId() != null && user.getDeptId() != 1 && user.getDeptId() != 0 && StringUtils.isNotEmpty(user.getPermission())) {
            cdid = user.getPermission().split(",");
        }
        List<Integer> permission;
        if (user.getDeptId() != null) { //村干部区域权限//roleId != 1 &&
            permission = sysDeptService.getidsbypid(user.getDeptId());
        } else {
            permission = null;
        }
        //总商铺数
        Integer allshop = szShopUserService.lambdaQuery()
                .in(cdid != null, SzShopUser::getCdid, cdid)
                .and(permission != null, y -> y.in(SzShopUser::getCdid, permission)
                        .or(i -> i.in(SzShopUser::getDid, permission))
                )
                .eq(SzShopUser::getDelFlag, 0)
                .eq(SzShopUser::getAuditFlag, 1).count();
        //总商品数
        QueryWrapper<SzShopGoods> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("s.del_flag", "0");
        queryWrapper.eq("s.state", 1);
        queryWrapper.in(cdid != null, "u.cdid", cdid);
        queryWrapper.and(permission != null, y -> y.in("u.cdid", permission)
                .or(i -> i.in("u.did", permission)));
        Integer allgood = szShopGoodsMapper.count(queryWrapper);
        //商铺分类统计
        QueryWrapper<RoleVo> query2 = new QueryWrapper<>();
        query2.eq("r.del_flag", 0);
        query2.in(cdid != null, "d.cdid", cdid);
        query2.and(permission != null, m -> m.in("d.cdid", permission)
                .or(n -> n.in("d.did", permission)));
        query2.isNotNull(" r.`gtype`");
        query2.groupBy("r.gtype");
        List<RoleVo> workrole = szShopGoodsMapper.shoplist(query2);
        //总兑换商品数
        QueryWrapper<SzShopGoods> query3 = new QueryWrapper<>();
        query3.eq("s.del_flag", "0");
        query3.eq("s.state", 1);
        query3.in(cdid != null, "u.cdid", cdid);
        query3.and(permission != null, y -> y.in("u.cdid", permission)
                .or(i -> i.in("u.did", permission)));
        Integer shopgoodcount = szShopGoodsMapper.baycount(query3);
        //商铺兑换统计
        Integer year = LocalDate.now().getYear();
        Map mouths = new HashMap();
        for (int i = 1; i <= 12; i++) {
            QueryWrapper<SzShopOrder> query4 = new QueryWrapper<>();
            query4.eq("o.del_flag", 0);
            query4.eq("o.state", 1);
            query4.in(cdid != null, "u.cdid", cdid);
            query4.and(permission != null, y -> y.in("u.cdid", permission)
                    .or(z -> z.in("u.did", permission)));
            query4.apply("date_format (o.create_time,'%Y-%m') = {0}", year + (i < 10 ? "-0" : "-") + i);
            Integer mouthrole = szShopOrderMapper.shopCount(query4);
            mouths.put(i, mouthrole);
        }
        Map map = new HashMap();
        //总商铺数
        map.put("allshop", allshop);
        //总商品数
        map.put("allgood", allgood);
        //商铺分类统计
        map.put("workrole", workrole);
        //总兑换商品数
        map.put("shopgoodcount", shopgoodcount);
        //商铺兑换统计
        map.put("mouthrole", mouths);
        return R.ok(map);
    }

    @GetMapping("/cardopen")
    @ApiOperation("卡统计")
    public R cardopen() {
        Integer userid = SecurityUtils.getUser().getId();
        SysUser user = sysUserService.getById(userid);
        Integer roleId = userRoleService.lambdaQuery().eq(SysUserRole::getUserId, user.getUserId()).select(SysUserRole::getRoleId).one().getRoleId();
        String[] cdid = null;
        if (roleId != null && roleId != 1 && user.getDeptId() != null && user.getDeptId() != 1 && user.getDeptId() != 0 && StringUtils.isNotEmpty(user.getPermission())) {
            cdid = user.getPermission().split(",");
        }
        //今日新增居民卡数量
        QueryWrapper query = new QueryWrapper();
        query.in(cdid != null, "u.cdid", cdid);
        query.apply("DATE(c.create_time) = CURDATE()");
        Integer todayuser = szFamilyCardMapper.countToday(query);
        //所有卡
        QueryWrapper query1 = new QueryWrapper();
        query1.in(cdid != null, "u.cdid", cdid);
        Integer alluser = szFamilyCardMapper.countToday(query1);
        //总人数
        Integer alluserman = szManUserService.lambdaQuery()
                //.eq(cdid!=null,SzManUser::getCdid,cdid)
                .in(cdid != null, SzManUser::getCdid, cdid)
                .eq(SzManUser::getDelFlag, 0)
                .eq(SzManUser::getLogin, 1)
                .eq(SzManUser::getAuditFlag, 1).count();
        //男女比例
        QueryWrapper query2 = new QueryWrapper();
        //query2.eq(cdid!=null,"r.cdid",cdid);
        query2.in(cdid != null, "r.cdid", cdid);
        query2.eq("r.del_flag", 0);
        query2.eq("r.audit_flag", 1);
        query2.eq("r.login", 1);
        query2.isNotNull("d.id");
        query2.groupBy("r.sex");
        List<RoleVo> sexroles = szManUserMapper.shoplist(query2);
        RoleVo roleVo_1 = new RoleVo();
        roleVo_1.setName("0");
        roleVo_1.setCount(0);
        RoleVo roleVo_2 = new RoleVo();
        roleVo_2.setName("1");
        roleVo_2.setCount(0);
        RoleVo roleVo_3 = new RoleVo();
        roleVo_3.setName("2");
        roleVo_3.setCount(0);
        for (RoleVo roleVo : sexroles) {
            if (roleVo.getName().equals("0")) {
                roleVo_1.setCount(roleVo.getCount());
            } else if (roleVo.getName().equals("1")) {
                roleVo_2.setCount(roleVo.getCount());
            } else {
                roleVo_3.setCount(roleVo.getCount());
            }
        }
        List<RoleVo> sexrole = new ArrayList<>();
        sexrole.add(roleVo_1);
        sexrole.add(roleVo_2);
        sexrole.add(roleVo_3);

        //办理统计
        Integer year = LocalDate.now().getYear();
        Map mouths = new HashMap();
        for (int i = 1; i <= 12; i++) {
            QueryWrapper query4 = new QueryWrapper();
            query4.eq("c.del_flag", 0);
            //query4.eq(cdid!=null,"s.cdid",cdid);
            query4.in(cdid != null, "s.cdid", cdid);
            query4.apply("date_format (c.create_time,'%Y-%m') = {0}", year + (i < 10 ? "-0" : "-") + i);
            Integer mouthrole = szFamilyCardMapper.count(query4);
            mouths.put(i, mouthrole);
        }
        Map map = new HashMap();
        //今日新增卡数量
        map.put("todayuser", todayuser);
        //男女比例
        map.put("workrole", sexrole);
        //总人数
        map.put("alluserman", alluserman);
        //所有卡
        map.put("alluser", alluser);
        //办理统计
        map.put("mouths", mouths);

        return R.ok(map);
    }

    @GetMapping("/roleopen")
    @ApiOperation("居民统计")
    public R roleopen() {
        Integer userid = SecurityUtils.getUser().getId();
        SysUser user = sysUserService.getById(userid);
        //Integer roleId = userRoleService.lambdaQuery().eq(SysUserRole::getUserId, user.getUserId()).select(SysUserRole::getRoleId).one().getRoleId();
        String[] cdid = null;
        Integer pid = sysDeptService.getpid(user.getDeptId());
        List<Integer> permission = sysDeptService.getidsbypid(pid);
        /*List<String> permissions = new ArrayList<>();
         *//*BeanUtils.copyProperties(permission,permissions);*//*
        permissions = permission.stream()
                .map(Object::toString)  // 将Integer对象转换为String
                .collect(Collectors.toList());  // 收集结果到新的List*/
        if (user.getDeptId() != null && user.getDeptId() != 0 && user.getPermission() != null && StringUtils.isNotEmpty(user.getPermission())) {//&& user.getDeptId() != 1
            cdid = user.getPermission().split(",");
        }
        //总人数
        Integer alluser = szManUserService.lambdaQuery()
                .in(cdid != null, SzManUser::getCdid, cdid)
                .isNotNull(pid != null && pid == 1, SzManUser::getIdcard)
                .in(permission != null && permission.size() > 0 && pid != null && pid == 1, SzManUser::getDid, permission)
                .in(permission != null && permission.size() > 0 && pid != null && pid != 1, SzManUser::getZdid, permission)
                .eq(SzManUser::getDelFlag, 0)
                .eq(SzManUser::getLogin, 1)
                .eq(SzManUser::getAuditFlag, 1).count();
        //总户数
        QueryWrapper query = new QueryWrapper();
        query.eq("del_flag", 0);
        query.eq("audit_flag", 1);
        query.eq("login", 1);
        query.in(cdid != null, "cdid", cdid);
        query.isNotNull(pid != null && pid == 1, "idcard");
        query.in(permission != null && permission.size() > 0 && pid != null && pid == 1, "did", permission);
        query.in(permission != null && permission.size() > 0 && pid != null && pid != 1, "zdid", permission);
        query.isNotNull("code");
        Integer allcode = szManUserMapper.alluserCount(query);
        //各镇人数
        QueryWrapper query1 = new QueryWrapper();
        query1.eq("r.del_flag", 0);
        query1.eq("r.audit_flag", 1);
        query1.eq("r.login", 1);
        query1.groupBy("r.did");
        query1.isNotNull("t.name");
        query1.isNotNull(pid != null && pid == 1, "idcard");
        List<RoleVo> deptrole = szManUserMapper.repmanlist(query1);
        //各性别人数
        QueryWrapper query2 = new QueryWrapper();
        query2.eq("r.del_flag", 0);
        query2.eq("r.audit_flag", 1);
        query2.eq("r.login", 1);
        query2.in(cdid != null, "r.cdid", cdid);
        query2.isNotNull(pid != null && pid == 1, "idcard");
        query2.in(permission != null && permission.size() > 0 && pid != null && pid == 1, "did", permission);
        query2.in(permission != null && permission.size() > 0 && pid != null && pid != 1, "zdid", permission);
        query2.groupBy("r.sex");
        List<RoleVo> sexroles = szManUserMapper.sexlist(query2);
        //List<RoleVo> sexroles=szRoleUserMapper.shoplist(query1);
        RoleVo roleVo_1 = new RoleVo();
        roleVo_1.setName("0");
        roleVo_1.setCount(0);
        RoleVo roleVo_2 = new RoleVo();
        roleVo_2.setName("1");
        roleVo_2.setCount(0);
        RoleVo roleVo_3 = new RoleVo();
        roleVo_3.setName("2");
        roleVo_3.setCount(0);
        for (RoleVo roleVo : sexroles) {
            if (roleVo.getName().equals("0")) {
                roleVo_1.setCount(roleVo.getCount());
            } else if (roleVo.getName().equals("1")) {
                roleVo_2.setCount(roleVo.getCount());
            } else if (roleVo.getName().equals("2")) {
                roleVo_3.setCount(roleVo.getCount());
            }
        }
        List<RoleVo> sexrole = new ArrayList<>();
        sexrole.add(roleVo_1);
        sexrole.add(roleVo_2);
        sexrole.add(roleVo_3);

        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.apply("del_flag = 0 ");
        queryWrapper.apply("audit_flag = 1 ");
        queryWrapper.apply("login = 1 ");
        //queryWrapper.apply(cdid!=null,"cdid = "+cdid+" ");
        queryWrapper.apply(cdid != null, "cdid in (" + user.getPermission() + ")");
        //queryWrapper.in(permissions!=null&&permissions.size()>0&&pid!=null&&pid==1, "did", permissions);
        Integer year = LocalDate.now().getYear();
        //0-20 人数
        QueryWrapper query3 = new QueryWrapper();
        query3.le("a.age", year);
        query3.ge("a.age", year - 20);
        RoleVo roleVo1 = szManUserMapper.agelist(query3, queryWrapper);
        //20-40 人数
        QueryWrapper query4 = new QueryWrapper();
        query4.le("a.age", year - 20);
        query4.ge("a.age", year - 40);
        RoleVo roleVo2 = szManUserMapper.agelist(query4, queryWrapper);
        //40-60 人数
        QueryWrapper query5 = new QueryWrapper();
        query5.le("a.age", year - 40);
        query5.ge("a.age", year - 60);
        RoleVo roleVo3 = szManUserMapper.agelist(query5, queryWrapper);
        //60-80 人数
        QueryWrapper query6 = new QueryWrapper();
        query6.le("a.age", year - 60);
        query6.ge("a.age", year - 80);
        RoleVo roleVo4 = szManUserMapper.agelist(query6, queryWrapper);
        //80-100 人数
        QueryWrapper query7 = new QueryWrapper();
        query7.le("a.age", year - 80);
        query7.ge("a.age", year - 100);
        RoleVo roleVo5 = szManUserMapper.agelist(query7, queryWrapper);
        //100以上 人数
        QueryWrapper query8 = new QueryWrapper();
        query8.le("a.age", year - 100);
        RoleVo roleVo6 = szManUserMapper.agelist(query8, queryWrapper);

        Map map = new HashMap();
        //总人数
        map.put("alluser", alluser);
        //总户数
        map.put("allcode", allcode);
        //各镇人数
        map.put("deptrole", deptrole);
        //各性别人数
        map.put("workrole", sexrole);
        //0-20 人数
        map.put("roleVo1", roleVo1);
        //20-40 人数
        map.put("roleVo2", roleVo2);
        //40-60 人数
        map.put("roleVo3", roleVo3);
        //60-80 人数
        map.put("roleVo4", roleVo4);
        //80-100 人数
        map.put("roleVo5", roleVo5);
        //100以上 人数
        map.put("roleVo6", roleVo6);
        return R.ok(map);
    }

    @ApiOperation(value = "获取三自", notes = "获取三自")
    @GetMapping("/villagesrole")
    public R villagesrole(String name) {
        List<SysDept> sysDepts = sysDeptService.lambdaQuery()
                .like(StringUtils.isNotEmpty(name), SysDept::getName, name)
                .eq(SysDept::getParentId, 0)
                .eq(SysDept::getDelFlag, 0)
                .ne(SysDept::getDeptId, 1)
                .orderByAsc(SysDept::getSort)
                .list();
        return R.ok(sysDepts);
    }
}
