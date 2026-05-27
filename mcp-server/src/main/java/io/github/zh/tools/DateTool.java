package io.github.zh.tools;

import io.github.zh.model.DateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component
@Slf4j
public class DateTool {


    // http://localhost:9999/sse

    /**
     * 获取当前时间 - 我自己的服务器是 北京（北京时间）
     * 服务器-洛杉矶（美国） - 洛杉矶的当前时间
     */
    @Tool(name = "getCurrentTime", description = "获取当前时间")
    public String getCurrentTime() {
        log.info("调用MCP------获取当前时间");
        LocalDateTime now = LocalDateTime.now();
        return now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

    }


    /**
     * 提问：我想知道 美国洛杉矶的当前时间
     * 获取当前时间 （根据城市名所在时区的id来获取当前时间）
     */
    @Tool(name = "getCurrentTimeByZoneId", description = "根据城市名所在时区的id来获取当前时间")
    public String getCurrentTimeByZoneId(DateRequest dateRequest) {
        log.info("调用MCP------根据城市名所在时区的id来获取当前时间");
        ZoneId zoneId = ZoneId.of(dateRequest.getZoneId());

        // 获取当前时区对应的时间
        LocalDateTime now = LocalDateTime.now(zoneId);
        return now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
