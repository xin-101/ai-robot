<template>
  <div class="chat-container">
    <!--聊天区-->
    <div class="chat-messages" ref="messagesContainer">
      <!-- 遍历聊天记录 -->
      <template v-for="(chat, index) in chatList" :key="index">
        <!--用户内容居于右侧 -->
        <div v-if="chat.role==='user'" class="message-item user-message">
          <div class="message-content">
            {{ chat.content }}
          </div>
          <div class="avatar user-avatar">
            <svg-icon name="user"></svg-icon>
          </div>
        </div>
        <!--AI内容居于左侧 -->
        <div v-else class="message-item ai-message">
          <div class="avatar ai-avatar">
            <svg-icon name="ai"></svg-icon>
          </div>
          <div class="message-content">
            <markdown-render :content="chat.content"></markdown-render>
          </div>
        </div>
      </template>
    </div>

    <!--提问区-->
    <div class="input-area">
      <div class="textarea-container">
        <textarea
            v-model="inputMessage"
            class="message-input"
            placeholder="请输入给AI提问的内容..."
            rows="1"
            ref="textareaRef"
            @input="autoResize"
            @keydown="handleKeydown"
        ></textarea>
      </div>

      <!--发送按钮-->
      <div class="send-button-container">
        <button
            class="send-button"
            :disabled="!inputMessage.trim()"
            @click="sendMessage"
        >
          <svg-icon name="send" custom-css="w-5 h-5"></svg-icon>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import {ref, nextTick, onUnmounted, onBeforeUnmount, onMounted} from "vue";
import SvgIcon from '@/components/Svgicon/Index.vue';
import MarkdownRender from '@/components/MarkdownRender/Index.vue';

// 定义SSE连接
let eventSource = null;

// textarea自动高度引用定义
const textareaRef = ref(null);
const messagesContainer = ref(null);

//定义消息提交的变量
const inputMessage = ref('');

// 消息列表
const messages = ref([]);

// 消息记录
const chatList = ref([
  {role:'user',content:'你好'},
  {role:'assistant',content:'欢迎使用AI技术，你可以向我提问任何问题。'}
]);

// 自动调整多行文本框高度
const autoResize = () => {
  const textarea = textareaRef.value;
  if (textarea) {
    textarea.style.height = 'auto';
    textarea.style.height = textarea.scrollHeight + 'px';
  }
};

// 处理键盘事件
const handleKeydown = (event) => {
  if (event.key === 'Enter' && !event.shiftKey) {
    event.preventDefault();
    sendMessage();
  }
};

// 关闭SSE连接
const closeSSE = () => {
  if (eventSource) {
    eventSource.close();
    eventSource = null;
  }
};

// 页面销毁时关闭SSE连接
onBeforeUnmount(() => {
  closeSSE();
});

// 滚动到底部方法
const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight;
    }
  });
};

// 发送消息
const sendMessage = async() => {
  // 1.检验发送的消息，不能为空
  if (!inputMessage.value.trim()) return;

  // 2.发送出去的消息先添加到chatList的聊天中
  const userMessage = inputMessage.value.trim()
  chatList.value.push({role:'user',content:inputMessage.value});

  // 发送消息后滚动到底部
  scrollToBottom();

  // 3.清空输入框
  inputMessage.value = '';

  // 4.重置输入框的高度
  if (textareaRef.value){
    textareaRef.value.style.height = 'auto';
  }

  // 5.发送消息给后端
  // 5.1创建sse连接
  chatList.value.push({role:'assistant',content:''});

  // 定义响应的回答
  let responseText= '';

  try {
    closeSSE()
    // 建立SSE连接
    eventSource = new EventSource(`http://localhost:8888/test/chatFluxSSEAIResponse?msg=${encodeURIComponent(userMessage)}`);

    // 处理接收到的数据
    eventSource.onmessage = (event) => {
      // console.log('event.data:', event.data)
      if (event.data){
        // 接收数据解析
        const data = JSON.parse(event.data)

        // 流式追加
        responseText += data.content;
        chatList.value[chatList.value.length-1].content=responseText;

        // 实时滚动到底部以显示最新内容
        scrollToBottom();
      }
    }

    // 处理错误消息
    eventSource.onerror = (error) => {
      if (error.eventPhase===EventSource.CLOSED){
        console.info('SSE连接已关闭');
      }else {
        chatList.value[chatList.value.length-1].content='我无法回答你的问题，请重新提问';
      }
      closeSSE();
    }
  }catch (e){
    console.error('SSE连接错误:',e);
    chatList.value[chatList.value.length-1].content='我无法回答你的问题，请重新提问';
    closeSSE();
  }
};

// 组件挂载后滚动到底部
onMounted(() => {
  scrollToBottom();
});
</script>

<style scoped>
.chat-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  max-width: 800px;
  margin: 0 auto;
  position: relative;
  background-color: #f8f9fa;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px 15px;
  display: flex;
  flex-direction: column;
}

.message-item {
  display: flex;
  margin-bottom: 15px;
  animation: fadeIn 0.3s ease-in;
}

.user-message {
  justify-content: flex-end;
}

.ai-message {
  justify-content: flex-start;
}

.avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  margin: 0 8px;
}

.user-avatar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.ai-avatar {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  color: white;
}

.message-content {
  max-width: 70%;
  padding: 12px 16px;
  border-radius: 18px;
  font-size: 14px;
  line-height: 1.4;
  position: relative;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
  word-wrap: break-word;
  overflow-wrap: break-word;
  white-space: pre-wrap;
}

.user-message .message-content {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-bottom-right-radius: 4px;
}

.ai-message .message-content {
  background: #ffffff;
  color: #333;
  border: 1px solid #e1e5e9;
  border-bottom-left-radius: 4px;
}

.input-area {
  padding: 15px;
  background: white;
  border-top: 1px solid #e1e5e9;
  position: relative;
}

.textarea-container {
  position: relative;
  border: 1px solid #e1e5e9;
  border-radius: 20px;
  background: #f8f9fa;
  transition: border-color 0.2s ease;
}

.textarea-container:focus-within {
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.1);
}

.message-input {
  width: 100%;
  padding: 12px 50px 12px 16px;
  background: transparent;
  border: none;
  outline: none;
  resize: none;
  font-size: 14px;
  line-height: 1.4;
  max-height: 120px;
  min-height: 20px;
  box-sizing: border-box;
}

.message-input::placeholder {
  color: #999;
}

.send-button-container {
  position: absolute;
  right: 25px;
  bottom: 25px;
}

.send-button {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: #667eea;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 2px 5px rgba(102, 126, 234, 0.3);
}

.send-button:hover:not(:disabled) {
  background: #5a6fd8;
  transform: scale(1.05);
}

.send-button:disabled {
  background: #ccc;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.send-icon {
  fill: white;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 滚动条样式 */
.chat-messages::-webkit-scrollbar {
  width: 6px;
}

.chat-messages::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 10px;
}

.chat-messages::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 10px;
}

.chat-messages::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}
</style>
