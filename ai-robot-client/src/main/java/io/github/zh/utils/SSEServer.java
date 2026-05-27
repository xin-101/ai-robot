package io.github.zh.utils;

import io.github.zh.enums.SSEMsgType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Slf4j
public class SSEServer {
    // 存放用户信息的map集合
    public static final Map<String,SseEmitter> sseClients=new ConcurrentHashMap<>();
    public static SseEmitter connect(String userId) {
        // 设置超时时间0表示永不超时
        SseEmitter sseEmitter = new SseEmitter(0L);
        // 注册超时方法
        sseEmitter.onTimeout(timeout(userId));
        // 完成
        sseEmitter.onCompletion(completion(userId));
        // 注册错误方法
        sseEmitter.onError(error(userId));

        sseClients.put(userId, sseEmitter);
        return sseEmitter;
    }
    //  超时方法
    public static Runnable timeout(String userId) {
        return () -> {
                log.error("SSE连接超时异常");
                remove(userId);
        };
    }
    //完成 方法
    public static Runnable completion(String userId) {
        return () -> {
            log.error("SSE推送完成");
            remove(userId);
        };
    }
    // 错误
    public static Consumer<Throwable> error(String userId) {
        return throwable -> {
            log.error("SSE推送错误");
            remove(userId);
        };
    }
    // 删除用户信息
    public static void remove(String userId) {
        log.info("用户{}已从SSE客户端中移除",userId);
        sseClients.remove(userId);
    }
    // 单人->消息推送

    public static void sendMessage(String userId,
                                   String message,
                                   SSEMsgType sseMsgType) {

        if (CollectionUtils.isEmpty(sseClients)){
            return;
        }
        if (sseClients.containsKey(userId)){
            SseEmitter sseEmitter = sseClients.get(userId);
            sendMessage(sseEmitter, userId, message, sseMsgType);
        }
        sendMessage(userId, message, SSEMsgType.MESSAGE);
    }
    // 多人->消息推送
    public static void sendMessageAll(String message, SSEMsgType sseMsgType) {
        if (CollectionUtils.isEmpty(sseClients)){
            return;
        }
        sseClients.forEach((userId, sseEmitter) -> {
            sendMessage(sseEmitter, userId, message, sseMsgType);
        });
    }

    public static void sendMessage(
            SseEmitter sseEmitter,
            String userId,
            String message,
            SSEMsgType sseMsgType) {

        SseEmitter.SseEventBuilder event = SseEmitter.event()
                .id(String.valueOf(System.currentTimeMillis()))
                .data(message)
                .name(sseMsgType.value);
        try {
            sseEmitter.send(event);
        } catch (Exception e) {
            log.error("SSE推送错误",e);
            remove(userId);
        }
    }

}
