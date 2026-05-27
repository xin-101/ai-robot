package io.github.zh.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.ai.tool.annotation.ToolParam;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EmailRequest {

    @ToolParam(description = "收件人邮箱")
    private String email;

    @ToolParam(description = "邮件标题")
    private String subject;

    @ToolParam(description = "邮件内容")
    private String message;

}
