<template>
<div class="markdown-container">
  <div v-html="html">

  </div>
</div>
</template>

<script setup>
import MarkdownIt from "markdown-it";
import {ref, watch} from "vue";
import highlight from "highlight.js";
import "highlight.js/styles/github.css";
import markdownItHighlightJs from 'markdown-it-highlightjs';
// 定义一个字段，用于传入markdown的文本
const props = defineProps({
  content: {
    type: String,
    default: ''
  }
})

// 定义解析后的html
const html = ref('')

// 初始化markdown解析器
const md = new MarkdownIt({
  html: true,// 允许html标签
  xhtmlOut: true, // 输出xhtml <br />
  linkify: true, // 允许自动转换url为链接
  typographer: true, // 排版优化
  breaks: true,// 允许换行
  langPrefix: 'language-',// 添加lang属性
});

//
 // 使用代码插件
md.use(markdownItHighlightJs,{
  highlight,
  auto: true,
  code:true,
});

// 监听 content 属性变化，并解析为html
watch(() => props.content, (newValue) => {
if (newValue){
  //渲染成html
  html.value = md.render(newValue);
}
},{immediate: true})

</script>

<style scoped>
</style>