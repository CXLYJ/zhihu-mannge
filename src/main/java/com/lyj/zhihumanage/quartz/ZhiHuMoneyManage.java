package com.lyj.zhihumanage.quartz;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lyj.zhihumanage.send.PushWxMessage;
import com.lyj.zhihumanage.util.Constant;
import com.lyj.zhihumanage.util.JdSignUtil;
import com.lyj.zhihumanage.send.MailUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ：lyqiang & lyj
 * @email: : iclyj@iclyj.cn
 * @date ：2020/12/13 11:37
 */
@Slf4j
@Component
public class ZhiHuMoneyManage {

    @Autowired
    private MailUtil mailUtil;
    @Autowired
    private PushWxMessage pushWxMessage;

    @Value("${app.key}")
    private String jdKey;
    @Value("${app.secret}")
    private String jdSecret;
    @Value("${open.mail}")
    private Boolean openMail;
    @Value("${open.wx}")
    private Boolean openWx;
    @Value("${open.filterPaid}")
    private Boolean openFilterPaid;


    @Scheduled(cron = "${lyj.quartz.task}")
    public void query() {
        LocalDateTime nowTime = LocalDateTime.now();
        LocalDateTime fiveMinBefore = nowTime.minusMinutes(5);
        queryFromJd(fiveMinBefore, nowTime);
    }


    public void queryFromJd(LocalDateTime startTime, LocalDateTime endTime) {
        Map<String, Object> paramMap = packageParam(startTime, endTime);
        String httpResp = HttpUtil.get(Constant.JD_API_URL, paramMap);
        JSONObject respJson = JSON.parseObject(httpResp);
        JSONObject queryResponse = respJson.getJSONObject("jd_union_open_order_row_query_response");
        String result = queryResponse.getString("result");
        JSONObject resultJson = JSON.parseObject(result);
        JSONArray dataArr = resultJson.getJSONArray("data");
        if (dataArr == null || dataArr.isEmpty()) {
            log.info("没有获取到订单数据,继续努力~");
            return;
        }
        packageContentAndSendMail(dataArr);
    }

    /**
     * 发送消息封装
     *
     * @param dataArr
     */
    private void packageContentAndSendMail(JSONArray dataArr) {



        String title = "老板出单了啊,金额:";
        BigDecimal totalMoney = BigDecimal.ZERO;
        StringBuilder contentBuilder = new StringBuilder();
        for (int i = 0; i < dataArr.size(); i++) {
            JSONObject data = dataArr.getJSONObject(i);

            // 过滤未付款
            // validCode = 16 是已付款 如果不是 已付款直接返回
           if (openFilterPaid){
               if (!data.getInteger("validCode").equals(16)) {
                   return;
               }
           }

            BigDecimal estimateFee = data.getBigDecimal("estimateFee");
            totalMoney = totalMoney.add(estimateFee);
            String skuName = data.getString("skuName");
            log.info("老板您有新出单子了，商品名称：{}, 金额：{}", skuName, estimateFee);
            contentBuilder
                    .append(" 商品名称： ")
                    .append(skuName)
                    .append(", **** 金额 ****：")
                    .append(estimateFee);
        }

        // 是否开启邮件通知
        if (openMail){
            log.info("#### 开始发送邮件信息 ####");
            mailUtil.sendMailMessage(title + totalMoney.toString(), contentBuilder.toString());
        }

        // 是否开启微信通知
        if (openWx){
            log.info("#### 开始发送微信信息 ####");
            pushWxMessage.sendWxMessage(title + totalMoney.toString(), contentBuilder.toString());
        }

    }


    /**
     * 参数封装
     *
     * @param startTime
     * @param endTime
     * @return
     */
    private Map<String, Object> packageParam(LocalDateTime startTime, LocalDateTime endTime) {
        LocalDateTime nowTime = LocalDateTime.now();
        String nowTimeStr = nowTime.format(DateTimeFormatter.ofPattern(Constant.DATE_TIME_FORMAT));

        String startTimeStr = startTime.format(DateTimeFormatter.ofPattern(Constant.DATE_TIME_FORMAT));
        String endTimeStr = endTime.format(DateTimeFormatter.ofPattern(Constant.DATE_TIME_FORMAT));

        Map<String, Object> bizParam = new HashMap<>(2);
        Map<String, Object> orderReqParam = new HashMap<>(8);

        orderReqParam.put("startTime", startTimeStr);
        orderReqParam.put("endTime", endTimeStr);
        orderReqParam.put("type", 1);
        orderReqParam.put("pageIndex", 1);
        orderReqParam.put("pageSize", 10);
        bizParam.put("orderReq", orderReqParam);

        Map<String, Object> paramMap = new HashMap<>(8);
        paramMap.put("app_key", jdKey);
        paramMap.put("sign_method", Constant.SIGN_METHOD);
        paramMap.put("format", Constant.PARAM_FORMAT);
        paramMap.put("v", Constant.VERSION);
        paramMap.put("timestamp", nowTimeStr);
        paramMap.put("method", Constant.JD_QUERY_METHOD);
        String paramJson = JSON.toJSONString(bizParam);
        paramMap.put("param_json", paramJson);
        paramMap.put(
                "sign",
                JdSignUtil.buildSign(nowTimeStr,
                paramJson, jdKey, jdSecret)
        );
        return paramMap;
    }


}
