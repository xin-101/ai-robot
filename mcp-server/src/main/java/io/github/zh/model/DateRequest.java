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
public class DateRequest {

    @ToolParam(description = "城市名")
    private String cityName;

    @ToolParam(description = "时区Id")
    private String zoneId;


}
