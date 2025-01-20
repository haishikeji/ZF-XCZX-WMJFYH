package com.px.pa.modulars.complain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.px.pa.modulars.complain.entity.Complain;
import com.px.pa.modulars.complain.mapper.ComplainMapper;
import com.px.pa.modulars.complain.service.ComplainService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 投诉信息
 *
 * @author 品讯科技
 * @date 2024-08
 */
@Service
@Transactional
public class ComplainServiceImpl extends ServiceImpl<ComplainMapper, Complain> implements ComplainService {


}
