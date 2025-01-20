package com.px.pa.modulars.money.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.px.pa.modulars.money.entity.PMoneyContent;
import com.px.pa.modulars.money.entity.PMoneyInfo;
import com.px.pa.modulars.money.mapper.PMoneyContentMapper;
import com.px.pa.modulars.money.service.PMoneyContentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 资金统计记录
 *
 * @author 品讯科技
 * @date 2024-08
 */
@Service
@Transactional
public class PMoneyContentServiceImpl extends ServiceImpl<PMoneyContentMapper, PMoneyContent> implements PMoneyContentService {


    @Override
    public Map<String, Object> tongji(PMoneyInfo pmoneyinfo) {
        Integer lastYear = null;
        if (pmoneyinfo.getYear() != null) {
            lastYear = pmoneyinfo.getYear() - 1;
        }
        Integer lastMonth = null;
        if (pmoneyinfo.getMonth() != null) {
            if (pmoneyinfo.getMonth() == 1) {
                lastMonth = 12;
            } else {
                lastMonth = pmoneyinfo.getMonth() - 1;
            }
        }
        Map<String, Object> result = new HashMap<>();
        List<PMoneyContent> pMoneyContentListUp = this.lambdaQuery()
                .select(PMoneyContent::getMoney)
                .eq(pmoneyinfo.getYear() != null, PMoneyContent::getYear, pmoneyinfo.getYear())
                .eq(pmoneyinfo.getMonth() != null, PMoneyContent::getMonth, pmoneyinfo.getMonth())
                .eq(pmoneyinfo.getDay() != null, PMoneyContent::getDay, pmoneyinfo.getDay())
                .eq(pmoneyinfo.getDid() != null, PMoneyContent::getDid, pmoneyinfo.getDid())
                .eq(pmoneyinfo.getDsid() != null, PMoneyContent::getDsid, pmoneyinfo.getDsid())
                .eq(PMoneyContent::getType, 1)
                .eq(PMoneyContent::getDelFlag, 0)
                .eq(PMoneyContent::getState, 1)
                .list();
        Double moneyUp = pMoneyContentListUp.stream().mapToDouble(PMoneyContent::getMoney).sum();
        List<PMoneyContent> pMoneyContentListDown = this.lambdaQuery()
                .select(PMoneyContent::getMoney)
                .eq(pmoneyinfo.getYear() != null, PMoneyContent::getYear, pmoneyinfo.getYear())
                .eq(pmoneyinfo.getMonth() != null, PMoneyContent::getMonth, pmoneyinfo.getMonth())
                .eq(pmoneyinfo.getDay() != null, PMoneyContent::getDay, pmoneyinfo.getDay())
                .eq(pmoneyinfo.getDid() != null, PMoneyContent::getDid, pmoneyinfo.getDid())
                .eq(pmoneyinfo.getDsid() != null, PMoneyContent::getDsid, pmoneyinfo.getDsid())
                .eq(PMoneyContent::getType, 0)
                .eq(PMoneyContent::getDelFlag, 0)
                .eq(PMoneyContent::getState, 1)
                .list();
        Double moneyDown = pMoneyContentListDown.stream().mapToDouble(PMoneyContent::getMoney).sum();
        if (pmoneyinfo.getMonth() == null) {
            List<Map> monthMaps = new ArrayList<>();
            for (int i = 1; i <= 12; i++) {
                Map monthMap = new HashMap<>();
                List<PMoneyContent> monthpMoneyContentListUp = this.lambdaQuery()
                        .select(PMoneyContent::getMoney)
                        .eq(pmoneyinfo.getYear() != null, PMoneyContent::getYear, pmoneyinfo.getYear())
                        .eq(pmoneyinfo.getDid() != null, PMoneyContent::getDid, pmoneyinfo.getDid())
                        .eq(pmoneyinfo.getDsid() != null, PMoneyContent::getDsid, pmoneyinfo.getDsid())
                        .eq(PMoneyContent::getMonth, i)
                        .eq(PMoneyContent::getType, 1)
                        .eq(PMoneyContent::getDelFlag, 0)
                        .eq(PMoneyContent::getState, 1)
                        .list();
                List<PMoneyContent> monthpMoneyContentListDown = this.lambdaQuery()
                        .select(PMoneyContent::getMoney)
                        .eq(pmoneyinfo.getYear() != null, PMoneyContent::getYear, pmoneyinfo.getYear())
                        .eq(pmoneyinfo.getDid() != null, PMoneyContent::getDid, pmoneyinfo.getDid())
                        .eq(pmoneyinfo.getDsid() != null, PMoneyContent::getDsid, pmoneyinfo.getDsid())
                        .eq(PMoneyContent::getMonth, i)
                        .eq(PMoneyContent::getType, 0)
                        .eq(PMoneyContent::getDelFlag, 0)
                        .eq(PMoneyContent::getState, 1)
                        .list();
                if (monthpMoneyContentListUp.isEmpty() && monthpMoneyContentListDown.isEmpty()) {
                    continue;
                }
                Double monthMoneyUp = monthpMoneyContentListUp.stream().mapToDouble(PMoneyContent::getMoney).sum();
                Double monthMoneyDown = monthpMoneyContentListDown.stream().mapToDouble(PMoneyContent::getMoney).sum();
                monthMap.put("month", i);
                monthMap.put("moneyUp", monthMoneyUp);
                monthMap.put("moneyDown", monthMoneyDown);
                monthMap.put("moneyTotal", monthMoneyUp - monthMoneyDown);
                monthMaps.add(monthMap);

            }
            result.put("monthMaps", monthMaps);
        }
        if (pmoneyinfo.getDay() == null) {
            List<PMoneyContent> lastpMoneyContentListUp = this.lambdaQuery()
                    .select(PMoneyContent::getMoney)
                    .eq(lastYear != null, PMoneyContent::getYear, lastYear)
                    .eq(lastMonth != null, PMoneyContent::getMonth, lastMonth)
                    .eq(pmoneyinfo.getDid() != null, PMoneyContent::getDid, pmoneyinfo.getDid())
                    .eq(pmoneyinfo.getDsid() != null, PMoneyContent::getDsid, pmoneyinfo.getDsid())
                    .eq(PMoneyContent::getType, 1)
                    .eq(PMoneyContent::getDelFlag, 0)
                    .eq(PMoneyContent::getState, 1)
                    .list();
            Double lastMoneyUp = lastpMoneyContentListUp.stream().mapToDouble(PMoneyContent::getMoney).sum();
            List<PMoneyContent> lastpMoneyContentListDown = this.lambdaQuery()
                    .select(PMoneyContent::getMoney)
                    .eq(lastYear != null, PMoneyContent::getYear, lastYear)
                    .eq(lastMonth != null, PMoneyContent::getMonth, lastMonth)
                    .eq(pmoneyinfo.getDid() != null, PMoneyContent::getDid, pmoneyinfo.getDid())
                    .eq(pmoneyinfo.getDsid() != null, PMoneyContent::getDsid, pmoneyinfo.getDsid())
                    .eq(PMoneyContent::getType, 0)
                    .eq(PMoneyContent::getDelFlag, 0)
                    .eq(PMoneyContent::getState, 1)
                    .list();
            Double lastMoneyDown = lastpMoneyContentListDown.stream().mapToDouble(PMoneyContent::getMoney).sum();
            result.put("lastMoneyUp", lastMoneyUp);
            result.put("lastMoneyDown", lastMoneyDown);
            result.put("lastmoneyUp", lastMoneyUp - lastMoneyDown);
            double oldValue = lastMoneyUp - lastMoneyDown;
            double newValue = moneyUp - moneyDown;
            double amplitude = 0;
            if (oldValue != 0) {
                amplitude = (newValue - oldValue) / oldValue * 100;
            }
            amplitude = Math.round(amplitude * 100.0) / 100.0;
            result.put("amplitude", amplitude);
        }
        result.put("moneyUp", moneyUp);
        result.put("moneyDown", moneyDown);
        result.put("moneyTotal", moneyUp - moneyDown);
        return result;
    }
}
