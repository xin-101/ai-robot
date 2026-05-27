package io.github.zh.model;


import lombok.Data;
import org.springframework.ai.tool.annotation.ToolParam;

@Data
public class WeatherRequest {
    @ToolParam(description = "城市名称，若果是中文汉字请先转换为汉语拼音，例如北京：beijing")
    private String city;

    @ToolParam(description = "城市的locationId")
    private String locationId;
}
