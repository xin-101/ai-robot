//package io.github.zh.runner;
//
//
//import io.github.zh.util.CustomTextSplitter;
//import jakarta.annotation.Resource;
//import org.springframework.ai.document.Document;
//import org.springframework.ai.reader.TextReader;
//import org.springframework.ai.vectorstore.SearchRequest;
//import org.springframework.ai.vectorstore.VectorStore;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
//
//@Component
//public class InitEmbeddingRunner implements ApplicationRunner {
//
//    @Value("classpath:/document/java.txt")
//    private org.springframework.core.io.Resource resource;
//
//    @Resource
//    private VectorStore vectorStore;
//
//    @Override
//    public void run(ApplicationArguments args) throws Exception {
//        TextReader textReader = new TextReader(resource);
//
//        // 转成Document对象
//        List<Document> documents = textReader.read();
//
//        // 自定义拆分处理
//        CustomTextSplitter customTextSplitter = new CustomTextSplitter();
//        List<Document> list = customTextSplitter.apply(documents);
//
//        // 防止数据重复添加
//        for (Document document : list) {
//            // 首先从向量数据库中查询该文档向量
//            List<Document> documentInDB = vectorStore.similaritySearch(SearchRequest.builder()
//                    .query(document.getText())
//                    .topK(1) // 只查询相似度最高的
//                    .build());
//            if (!documentInDB.isEmpty() && documentInDB.get(0).getScore() > 0.9) {
//                continue;
//            }
//            // 如果没有查询到，则添加
//            vectorStore.add(List.of(document));
//        }
//
//
//    }
//}























package io.github.zh.runner;

import io.github.zh.util.CustomTextSplitter;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InitEmbeddingRunner implements ApplicationRunner {

    @Value("classpath:/document/java.txt")
    private org.springframework.core.io.Resource resource;

    @Resource
    private VectorStore vectorStore;

    @Value("${app.embedding.batch-size:10}")
    private int batchSize;

    @Value("${app.embedding.delay-ms:2000}")
    private long delayMs;

    @Value("${app.embedding.init.enabled:true}")
    private boolean enabled;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!enabled) {
            log.info("Embedding initialization is disabled, skipping...");
            return;
        }

        log.info("Starting embedding initialization...");

        TextReader textReader = new TextReader(resource);
        List<Document> documents = textReader.read();

        CustomTextSplitter customTextSplitter = new CustomTextSplitter();
        List<Document> splitDocuments = customTextSplitter.apply(documents);

        log.info("Total documents to process: {}", splitDocuments.size());

        // 优化1：先批量去重（基于内容哈希）
        List<Document> uniqueDocuments = deduplicateByContent(splitDocuments);
        log.info("After deduplication: {} documents", uniqueDocuments.size());

        // 优化2：分批处理
        List<List<Document>> batches = partitionList(uniqueDocuments, batchSize);

        for (int i = 0; i < batches.size(); i++) {
            List<Document> batch = batches.get(i);
            try {
                // 优化3：批量添加，减少 API 调用次数
                vectorStore.add(batch);
                log.info("Batch {}/{} completed ({} documents)", i + 1, batches.size(), batch.size());
            } catch (Exception e) {
                log.warn("Batch {} failed: {}", i + 1, e.getMessage());
                // 单个文档降级处理
                handleBatchFallback(batch);
            }

            // 优化4：批次间延迟，避免触发 RPM 限制
            if (i < batches.size() - 1) {
                log.info("Waiting {}ms before next batch...", delayMs);
                Thread.sleep(delayMs);
            }
        }

        log.info("Embedding initialization completed!");
    }

    /**
     * 基于内容哈希去重，避免重复文档
     */
    private List<Document> deduplicateByContent(List<Document> documents) {
        return documents.stream()
                .collect(Collectors.toMap(
                        doc -> Objects.hash(doc.getText()),
                        doc -> doc,
                        (existing, replacement) -> existing
                ))
                .values()
                .stream()
                .toList();
    }

    /**
     * 分批处理
     */
    private List<List<Document>> partitionList(List<Document> list, int batchSize) {
        List<List<Document>> batches = new ArrayList<>();
        for (int i = 0; i < list.size(); i += batchSize) {
            batches.add(list.subList(i, Math.min(i + batchSize, list.size())));
        }
        return batches;
    }

    /**
     * 批量失败时的降级处理 - 单个文档重试
     */
    private void handleBatchFallback(List<Document> batch) {
        log.warn("Falling back to single document processing...");
        for (int i = 0; i < batch.size(); i++) {
            try {
                vectorStore.add(List.of(batch.get(i)));
                log.info("Fallback document {}/{} succeeded", i + 1, batch.size());
            } catch (Exception e) {
                log.error("Fallback document {} failed: {}", i + 1, e.getMessage());
            }
            if (i < batch.size() - 1) {
                try {
                    Thread.sleep(delayMs * 2); // 失败后增加延迟
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}