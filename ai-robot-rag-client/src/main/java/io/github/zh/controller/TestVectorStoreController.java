package io.github.zh.controller;

import io.github.zh.reader.*;
import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/test")
public class TestVectorStoreController {

    @Resource
    private MyTextReader myTextReader;
    @Resource
    private MyJsonReader myJsonReader;
    @Resource
    private MyMdReader myMdReader;
    @Resource
    private MyHtmlReader myHtmlReader;
    @Resource
    private MyPdfReader myPdfReader;

    @RequestMapping("/txt1")
    public List<Document> txt1() {

        return myTextReader.readText();
    }

    @RequestMapping("/txt2")
    public List<Document> txt2() {

        return myTextReader.readTextAndSplit();
    }

    @RequestMapping("/json1")
    public List<Document> json1() {

        return myJsonReader.readJson();
    }

    @RequestMapping("/json2")
    public List<Document> json2() {

        return myJsonReader.readJsonAndSplit();
    }

    @RequestMapping("/md")
    public List<Document> md() {
        // 读取MD文档
        return myMdReader.readMd();
    }

    @RequestMapping("/html1")
    public List<Document> html1() {

        return myHtmlReader.readHtml();
    }

    @RequestMapping("/html2")
    public List<Document> html2() {

        return myHtmlReader.readHtmlAndSplit();
    }

    @RequestMapping("/pdf")
    public List<Document> pdf() {

        return myPdfReader.readPdf();
    }
}
