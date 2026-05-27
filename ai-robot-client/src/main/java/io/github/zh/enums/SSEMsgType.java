package io.github.zh.enums;

public enum SSEMsgType {

    MESSAGE("message", "单次去发送的普通类型消息"),
    ADD("add", "消息追加，适用于流式消息推送"),
    FINISH("finish", "结束消息推送"),
    CUSTOM("custom", "自定义消息类型");
    public String type;
    public String value;
    SSEMsgType(String type, String value) {
        this.type = type;
        this.value = value;
    }
}
