import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'node:path'

const variablesPath = path.resolve(__dirname, 'src/styles/variables.scss')

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  },
  server: {
    port: 4000,
    host: true
  },
  css: {
    preprocessorOptions: {
      scss: {
        additionalData: (source: string, fp: string) => {
          if (fp.endsWith('variables.scss') || fp.includes('node_modules')) {
            return source
          }
          return `@use "${variablesPath}" as *;\n${source}`
        }
      }
    }
  }
})
