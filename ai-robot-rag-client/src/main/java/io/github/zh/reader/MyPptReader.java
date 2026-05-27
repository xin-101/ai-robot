package io.github.zh.reader;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MyPptReader {
    @Value("classpath:/document/xiyouji.ppt")
    private Resource resource;

    /**
     * 读取PPT文件
     * @return
     */
    public List<Document> readPPT(){

        TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(resource);
        List<Document> documents = tikaDocumentReader.read();

        // 文档分块
        TokenTextSplitter tokenTextSplitter = new TokenTextSplitter();


        return tokenTextSplitter.apply(documents);
    }
}
