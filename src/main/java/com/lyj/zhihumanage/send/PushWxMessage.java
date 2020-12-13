package com.lyj.zhihumanage.send;

import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lyqiang & lyj
 */
@Slf4j
@Component
public class PushWxMessage {

    @Value("${wx.send.url}")
    private String wxSender;

    /**
     * text：消息标题，最长为256，必填。
     * desp：消息内容，最长64Kb，可空，支持MarkDown。
     */
    public void sendWxMessage(String title, String content) {
        Map<String, Object> paramMap = new HashMap<>(2);
        paramMap.put("text", title);
        paramMap.put("desp", content);
        String result = HttpUtil.get(wxSender, paramMap);
        log.info("调用 server 酱发送消息，返回：{}", result);
    }

}
