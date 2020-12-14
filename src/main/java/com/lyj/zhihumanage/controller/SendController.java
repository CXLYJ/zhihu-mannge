package com.lyj.zhihumanage.controller;

import com.lyj.zhihumanage.quartz.ZhiHuMoneyManage;
import com.lyj.zhihumanage.util.Constant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author ：lyj & lyqiang
 * @email: : iclyj@iclyj.cn
 * @date ：2020/12/13 11:25
 */
@RestController
@RequestMapping("/api")
public class SendController {

    @Autowired
    ZhiHuMoneyManage zhiHuMoneyManage;

    @GetMapping(value = "/send")
    public Object Send(@RequestParam(required = false) String orderTimeStr) {
        if (StringUtils.isBlank(orderTimeStr)) {
            return "没有输入时间";
        }
        LocalDateTime orderTime = LocalDateTime.parse(orderTimeStr, DateTimeFormatter.ofPattern(Constant.DATE_TIME_FORMAT));
        LocalDateTime startTime = orderTime.minusMinutes(10);
        LocalDateTime endTime = orderTime.plusMinutes(10);
        zhiHuMoneyManage.queryFromJd(startTime, endTime);
        return "请检查检查邮件或微信消息";
    }

}
