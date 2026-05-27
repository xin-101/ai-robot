<template>
  <svg class="customCss" aria-hidden="true">
    <use :xlink:href="symbolId"/>
  </svg>
</template>

<script setup>
import {defineProps, computed} from 'vue';


/**定义props
 * prefix:图标前缀
 * name:图标名称
 * customCss:自定义样式
 */
const props = defineProps({
  prefix: {
    type: String,
    default: 'icon'
  },
  name: {
    type: String,
    required: true
  },
  customCss: {
    type: String,
    default: ''
  }
})




// 计算SymbolId
const symbolId = computed(() => `#${props.prefix}-${props.name}`)


</script>


<style scoped>

</style>