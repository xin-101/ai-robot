package io.github.zh.reader;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.JsonReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class MyJsonReader {

    @Value("classpath:/document/java.json")
    private Resource resource;

    /**
     * 读取Json文件
     * @return
     */
    public List<Document> readJson(){
        JsonReader jsonReader = new JsonReader(resource);
        return jsonReader.read();
    }
    /**
     * 读取Json文件并进行拆分处理
     */
    public List<Document> readJsonAndSplit(){
        JsonReader jsonReader = new JsonReader(resource,"title","content");
        return jsonReader.read();
    }

}
