package io.github.zh.reader;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MyPdfReader {
    @Value("classpath:/document/juejin.pdf")
    private Resource resource;

    /**
     * 读取pdf文件
     * @return
     */
    public List<Document> readPdf(){

        PdfDocumentReaderConfig pdfDocumentReaderConfig = PdfDocumentReaderConfig.builder()
                .withPageTopMargin(0) // 设置页眉和页脚的边距
                .withPageBottomMargin(0)
                .withPageExtractedTextFormatter(ExtractedTextFormatter.builder()
                        .withNumberOfTopTextLinesToDelete(0)
                        .withNumberOfBottomTextLinesToDelete(0)
                        .build())
                .withPagesPerDocument(1)

                .build();

        PagePdfDocumentReader pagePdfDocumentReader = new PagePdfDocumentReader(resource,pdfDocumentReaderConfig);


        return pagePdfDocumentReader.read();
    }
}
