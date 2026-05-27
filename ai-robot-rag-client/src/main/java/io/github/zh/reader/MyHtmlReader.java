package io.github.zh.reader;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.jsoup.JsoupDocumentReader;
import org.springframework.ai.reader.jsoup.config.JsoupDocumentReaderConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MyHtmlReader {
    @Value("classpath:/document/index.html")
    private Resource resource;

    /**
     * 读取html文件-默认
     */
    public List<Document> readHtml(){
        JsoupDocumentReader jsoupDocumentReader = new JsoupDocumentReader(resource);

        return jsoupDocumentReader.read();
    }
    /**
     * 读取html文件-自定义
     */
    public List<Document> readHtmlAndSplit(){

        JsoupDocumentReaderConfig jsoupDocumentReaderConfig=JsoupDocumentReaderConfig.builder()
                .selector("body div p") // 选择器 选择body下的div标签
                .charset("UTF-8")
                .includeLinkUrls( true) // 是否包含链接
                .metadataTag("author")
                .metadataTag("date")
                .additionalMetadata("filename",resource.getFilename())
                .build();
        JsoupDocumentReader jsoupDocumentReader = new JsoupDocumentReader(resource,jsoupDocumentReaderConfig);

        return jsoupDocumentReader.read();
    }
}
