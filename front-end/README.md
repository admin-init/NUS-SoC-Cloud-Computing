# README

这个项目已经构建完毕，已经把它push到我们的仓库了。

dist是vue3打包后的文件夹，初始的项目文件夹过大没办法push上仓库。

有一些需要交代的问题：

- 这些代码与后端交互的path，因为我之前在本机测试留的是静态的localhost:50080在实际投入的使用的时候需要我在我的项目文件夹中修改，然后重新打包成dist。
- 后面数据库中数据格式（例如站名）如果还需要修改，我的代码仍需配套修改并重新打包。



一则deepseek问答：

## Q:

我有一个vue3项目。我听说vue3框架可以方便的将某个项目文件夹进行打包或者说封装，使其更容易作为镜像部署在服务器上。我的听说是正确的吗？该怎么做？

## A:

是的，你的理解是正确的。Vue 3 项目可以方便地打包成静态文件，并容器化为 Docker 镜像进行部署。以下是详细步骤：

### 核心步骤概览
1. **打包 Vue 项目** → 生成静态文件
2. **创建 Docker 镜像** → 基于 Nginx 托管静态文件
3. **部署镜像** → 运行容器化应用

---

### 详细操作指南

#### 1. 打包 Vue 项目
```bash
# 安装依赖
npm install

# 构建生产包 (生成 dist 文件夹)
npm run build
```

#### 2. 准备 Docker 相关文件
在项目根目录创建以下文件：

**Dockerfile** (无后缀)
```dockerfile
# 阶段1: 构建环境
FROM node:18-alpine as build-stage
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# 阶段2: 生产环境
FROM nginx:stable-alpine as production-stage
# 复制构建产物
COPY --from=build-stage /app/dist /usr/share/nginx/html
# 复制自定义 Nginx 配置 (可选)
# COPY nginx.conf /etc/nginx/conf.d/default.conf
# 暴露端口
EXPOSE 80
# 启动 Nginx
CMD ["nginx", "-g", "daemon off;"]
```

**.dockerignore** (防止复制无用文件)
```gitignore
node_modules
.git
dist
```

**（可选）自定义 Nginx 配置**  
创建 `nginx.conf` 解决 Vue Router 的 history 模式问题：
```nginx
server {
    listen 80;
    location / {
        root /usr/share/nginx/html;
        index index.html;
        try_files $uri $uri/ /index.html;
    }
}
```

#### 3. 构建 Docker 镜像
```bash
# 在项目根目录执行 (注意最后的点)
docker build -t vue3-app:latest .
```

#### 4. 运行容器
```bash
# 映射宿主机 8080 端口到容器 80 端口
docker run -d -p 8080:80 --name my-vue-app vue3-app:latest
```

#### 5. 访问应用
打开浏览器访问：  
http://localhost:8080

---

### 关键优化建议

1. **镜像体积优化**
   - 使用多阶段构建（如示例所示）
   - 选择 Alpine 基础镜像（减少 70% 体积）

2. **部署路由问题**
   - 使用 `nginx.conf` 配置解决 History 模式 404 错误

3. **容器安全**
   ```dockerfile
   # 在 Dockerfile 生产阶段添加
   RUN chown -R nginx:nginx /usr/share/nginx/html && \
       chmod -R 755 /usr/share/nginx/html
   ```

4. **环境变量处理**
   ```dockerfile
   # 构建时传入变量
   ARG VUE_APP_API_BASE
   ENV VUE_APP_API_BASE=$VUE_APP_API_BASE
   ```
   构建命令：
   ```bash
   docker build --build-arg VUE_APP_API_BASE=https://api.example.com -t vue3-app .
   ```

---

### 部署到服务器的两种方式

**方案1：直接部署静态文件**
```bash
# 1. 本地打包
npm run build

# 2. 上传 dist 文件夹到服务器
scp -r dist user@server:/path/to/deploy

# 3. 服务器安装 Nginx
sudo apt install nginx

# 4. 配置 Nginx 指向 dist 目录
```

**方案2：容器化部署（推荐）**
```bash
# 1. 构建镜像
docker build -t vue3-app .

# 2. 推送到容器仓库
docker tag vue3-app your-registry/vue3-app
docker push your-registry/vue3-app

# 3. 服务器拉取并运行
docker pull your-registry/vue3-app
docker run -d -p 80:80 vue3-app
```

---

### 常见问题解决

**问题：页面刷新出现 404**  
👉 解决方案：确保已配置 Nginx 的 `try_files` 规则

**问题：跨域请求失败**  
👉 解决方案：
```nginx
# 在 nginx.conf 中添加
location /api {
    proxy_pass http://backend-service;
    add_header 'Access-Control-Allow-Origin' '*';
}
```

**问题：静态资源加载路径错误**  
👉 解决方案：在 `vite.config.js` 或 `vue.config.js` 中设置：
```js
// Vite 项目
export default defineConfig({
  base: '/', // 改为相对路径 './'
});

// Vue CLI 项目
module.exports = {
  publicPath: process.env.NODE_ENV === 'production'
    ? './'
    : '/'
}
```

---

### 完整部署流程图
```
本地开发 → npm run build → 生成 dist
       ↓
创建 Dockerfile + nginx.conf
       ↓
docker build → 生成镜像
       ↓
docker run → 容器运行
       ↓
Nginx 服务静态文件 → 用户访问
```

通过这种方式，你可以获得：
- 独立运行的环境
- 版本控制的镜像
- 秒级回滚能力
- 资源隔离的容器
- 跨平台一致性

建议使用容器化部署，这是目前最主流的 Vue 应用部署方案，特别适合 CI/CD 自动化流程。