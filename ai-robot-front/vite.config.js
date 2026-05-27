import {fileURLToPath, URL} from 'node:url'

import {defineConfig} from 'vite'
import vue from '@vitejs/plugin-vue'
// import vueDevTools from 'vite-plugin-vue-devtools'
// 样式库配置
import tailwindcss from '@tailwindcss/vite';
// Ant 按需引入
import Components from 'unplugin-vue-components/vite';
import {AntDesignVueResolver} from 'unplugin-vue-components/resolvers';
// SVG图标
import {createSvgIconsPlugin} from 'vite-plugin-svg-icons';
import path from 'path';



// https://vite.dev/config/
export default defineConfig({
    plugins: [
        vue(),
        // vueDevTools(),
        tailwindcss(),
        Components({
            resolvers: [
                AntDesignVueResolver({
                    importStyle: false,
                }),
            ],
        }),
        createSvgIconsPlugin({
            //指定svg图标存放的目录
            iconDirs: [path.resolve(process.cwd(), 'src/assets/icons')],
            // 指定symbolId格式
            symbolId: 'icon-[dir]-[name]',

            //自定义SVG的插入位置
            inject:'body-last',
            //自定义dom id
            customDomId: '__svg__icons__dom__',
        }),
    ],
    resolve: {
        alias: {
            '@': fileURLToPath(new URL('./src', import.meta.url))
        },
    },
})
