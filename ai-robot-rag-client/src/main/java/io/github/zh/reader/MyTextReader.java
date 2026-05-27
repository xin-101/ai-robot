package io.github.zh.reader;

import io.github.zh.util.CustomTextSplitter;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;


import java.util.List;


@Component
public class MyTextReader {

    @Value("classpath:/document/java.txt")
    private Resource resource;

    /**
     * 读取TEXT文件
     * @return
     */
    public List<Document> readText(){
        TextReader textReader = new TextReader(resource);

        // 添加一些自定义的元数据
        textReader.getCustomMetadata()
                .put("fileName","java.txt");

        return textReader.read();
    }
    /**
     *  读取TEXT文档 进行拆分处理
     */
    public List<Document> readTextAndSplit(){
        TextReader textReader = new TextReader(resource);

        // 转成Document对象
        List<Document> documents = textReader.read();

        // 使用TokenTextSplitter进行拆分处理-默认的（不用）
//        TokenTextSplitter tokenTextSplitter=new TokenTextSplitter();
//        List<Document> list = tokenTextSplitter.apply(documents);
        // 自定义拆分处理
        CustomTextSplitter customTextSplitter=new CustomTextSplitter();
        List<Document> list = customTextSplitter.apply(documents);


        return list;
    }

}
