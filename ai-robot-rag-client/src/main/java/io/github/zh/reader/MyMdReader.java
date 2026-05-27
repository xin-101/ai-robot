package io.github.zh.reader;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MyMdReader {
    @Value("classpath:/document/xiyouji.md")
    private Resource resource;

    /**
     * 读取MD文件
     * @return
     */
    public List<Document> readMd(){

        MarkdownDocumentReaderConfig markdownDocumentReaderConfig= MarkdownDocumentReaderConfig.builder()
                .withHorizontalRuleCreateDocument( true) // 添加分隔符
                .withIncludeBlockquote(true) // 添加引用
                .withIncludeCodeBlock(true) // 添加代码块
                .withAdditionalMetadata("fileName",resource.getFilename())
                .build();

        MarkdownDocumentReader mdReader = new MarkdownDocumentReader(resource,
                markdownDocumentReaderConfig);

        return mdReader.read();
    }
}
