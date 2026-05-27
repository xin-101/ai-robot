package io.github.zh.tools;

import cn.hutool.http.HttpResponse;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import io.github.zh.config.WeatherProperties;
import io.github.zh.model.WeatherRequest;
import io.github.zh.model.WeatherResponse;
import jakarta.annotation.Resource;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class WeatherTool {

    @Resource
    private WeatherProperties weatherProperties;

    // 城市搜索路径 /geo/v2/city/lookup
    private static final String CITY_SEARCH_PATH = "/geo/v2/city/lookup";

    /**
     * 城市搜索 获得 locationId
     */
    @Tool(name = "citySearch", description = "城市搜索")
    public String citySearch(WeatherRequest weatherRequest) {
        String city = weatherRequest.getCity();

        log.info("开始获取天气，城市搜索：{}", city);
        // 先调用城市搜索接口，查询到该城市的locationId
        String citySearchApiUrl = weatherProperties.getApiUrl() + CITY_SEARCH_PATH;
        @Cleanup
        HttpResponse citySearchHttpResponse = HttpUtil.createGet(citySearchApiUrl)
                .header("Content-Type", "application/json")
                .header("X-QW-Api-Key", weatherProperties.getApiKey())
                .form("location", city)
                .execute();

        String cityResponseBody = citySearchHttpResponse.body();
        log.info("城市搜索接口返回：{}", cityResponseBody);
        String locationId = JSONUtil.getByPath(JSONUtil.parseObj(cityResponseBody), "$.location[0].id",
                null);
        log.info("城市的locationId为：{}", locationId);

        return locationId;
    }

    @Tool(name = "getWeather", description = "获取天气")
    public WeatherResponse getWeather(WeatherRequest weatherRequest) {
        String locationId = weatherRequest.getLocationId();
        log.info("城市的locationId为：{}", locationId);
        // 再调用天气接口，获得天气信息
        String baseWeatherApiUrl = weatherProperties.getApiUrl() + "/v7/weather/now";
        @Cleanup
        HttpResponse weatherHttpResponse = HttpUtil.createGet(baseWeatherApiUrl)
                .header("Content-Type", "application/json")
                .header("X-QW-Api-Key", weatherProperties.getApiKey())
                .form("location", locationId)
                .form("lang", "zh")
                .execute();

        String body = weatherHttpResponse.body();
        log.info("天气接口返回结果：{}", body);
        JSONObject jsonObject = JSONUtil.parseObj(body);
        return jsonObject.getJSONObject("now").toBean(WeatherResponse.class);


    }
}
