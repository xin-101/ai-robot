package io.github.zh.controller;

import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesis;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisParam;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisResult;
import com.alibaba.dashscope.aigc.videosynthesis.VideoSynthesis;
import com.alibaba.dashscope.aigc.videosynthesis.VideoSynthesisParam;
import com.alibaba.dashscope.aigc.videosynthesis.VideoSynthesisResult;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.utils.JsonUtils;
import io.github.zh.service.ChatService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import com.alibaba.dashscope.audio.ttsv2.SpeechSynthesisParam;
import com.alibaba.dashscope.audio.ttsv2.SpeechSynthesizer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

@RestController
@RequestMapping("/multiModel")
public class MultiModelController {
    @Resource
    private ChatService chatService;
    @Resource
    private ChatClient chatClient;
    @Value("${alibaba.ai.text2image}")
    private String text2ImageApiKey;

    /**
     * 视觉模型
     */
    @GetMapping(value = "/viewChat")
    public Flux<String> viewChat(String msg,
                                 HttpServletResponse response) {
        // 1.设置媒体资源
        Media media = Media.builder()
                .mimeType(MediaType.IMAGE_PNG)
                .data(new ClassPathResource("/images/1.png"))
                .build();

        // 2.构建多模态消息
        UserMessage userMessage = UserMessage.builder()
                .media(media)
                .text(msg)
                .build();
        // 3.构建Prompt
        Prompt prompt = Prompt.builder()
                .messages(userMessage)
                .build();
        response.setCharacterEncoding("utf-8");
        return chatService.chatFlux(prompt);
    }

    /**
     * 文生图
     */
    @GetMapping(value = "/text2Img", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public String text2Img(String msg, HttpServletResponse response) {
        // 1.构建文生图的参数
        ImageSynthesisParam param =
                ImageSynthesisParam.builder()
                        .apiKey(text2ImageApiKey)
                        .model("wan2.7-image")
                        .prompt(msg)
                        .n(1)
                        .size("1024*1024")
                        .build();


        // 2.同步调用
        ImageSynthesis imageSynthesis = new ImageSynthesis();
        ImageSynthesisResult result = null;
        try {
            System.out.println("---sync call, please wait a moment----");
            result = imageSynthesis.call(param);
        } catch (ApiException | NoApiKeyException e) {
            throw new RuntimeException(e.getMessage());
        }
        return JsonUtils.toJson(result);
    }

    /**
     * 文生音频
     */
    @GetMapping(value = "/text2Audio")
    public void text2Audio(String msg, HttpServletResponse response) {
        // 1.构建请求参数
        SpeechSynthesisParam param =
                SpeechSynthesisParam.builder()
                        .apiKey(text2ImageApiKey)
                        .model("cosyvoice-v3.5-plus") // 模型
                        .voice("longxiaochun_v2") // 音色
                        .build();

        // 2.同步模式：禁用回调（第二个参数为null）
        SpeechSynthesizer synthesizer = new SpeechSynthesizer(param, null);
        // 3.阻塞直至音频返回
        ByteBuffer audio = synthesizer.call(msg);

        // 4.将音频数据保存到本地文件“output.mp3”中
        File file = new File("D:\\JavaProjects\\ai-robot\\output.mp3");
        // 首次发送文本时需建立 WebSocket 连接，因此首包延迟会包含连接建立的耗时
        System.out.println(
                "[Metric] requestId为："
                        + synthesizer.getLastRequestId()
                        + "首包延迟（毫秒）为："
                        + synthesizer.getFirstPackageDelay());
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(audio.array());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * 文生视频
     *
     *
     */
    @GetMapping(value = "/text2Video")
    @SneakyThrows
    public String text2Video(String msg, HttpServletResponse response) {
        // 1.构建参数
        VideoSynthesis vs = new VideoSynthesis();
        VideoSynthesisParam param =
                VideoSynthesisParam.builder()
                        .apiKey(text2ImageApiKey)
                        .model("wan2.7-t2v")
                        .prompt(msg)
                        .size("1920*1080")
                        .build();

        // 2.同步调用
        VideoSynthesisResult result = vs.call(param);
        return JsonUtils.toJson(result);

    }
}
