<script setup lang="ts">
const currentYear = new Date().getFullYear()
</script>

<template>
  <footer class="footer">
    <div class="footer-container">
      <div class="footer-content">
        <div class="footer-section">
          <h3>关于博客</h3>
          <p>这是一个基于Spring Boot + Vue的个人博客系统，分享技术心得与生活感悟。</p>
        </div>
        
        <div class="footer-section">
          <h3>快速链接</h3>
          <ul>
            <li><router-link to="/">首页</router-link></li>
            <li><router-link to="/category">分类</router-link></li>
            <li><router-link to="/tag">标签</router-link></li>
            <li><router-link to="/archive">归档</router-link></li>
          </ul>
        </div>
        
        <div class="footer-section">
          <h3>联系方式</h3>
          <ul>
            <li>邮箱：example@blog.com</li>
            <li>微信：example_wechat</li>
          </ul>
        </div>
      </div>
      
      <div class="footer-bottom">
        <p>&copy; {{ currentYear }} 个人博客. All Rights Reserved.</p>
      </div>
    </div>
  </footer>
</template>

<style scoped>
.footer {
  background-color: #f5f5f5;
  color: #333;
  padding: 40px 0 20px;
  margin-top: auto;
}

.footer-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.footer-content {
  display: flex;
  justify-content: space-between;
  margin-bottom: 30px;
}

.footer-section {
  flex: 1;
  margin-right: 20px;
}

.footer-section:last-child {
  margin-right: 0;
}

.footer-section h3 {
  font-size: 18px;
  margin-bottom: 15px;
  color: #409eff;
}

.footer-section p {
  font-size: 14px;
  line-height: 1.6;
}

.footer-section ul {
  list-style: none;
  padding: 0;
}

.footer-section ul li {
  margin-bottom: 8px;
  font-size: 14px;
}

.footer-section ul li a {
  color: #333;
  text-decoration: none;
}

.footer-section ul li a:hover {
  color: #409eff;
}

.footer-bottom {
  text-align: center;
  padding-top: 20px;
  border-top: 1px solid #ddd;
  font-size: 14px;
}
</style>