<script setup lang="ts">
// 导入Naive UI组件
import { NButton, NIcon, NDropdown, useMessage } from "naive-ui";
// 导入图标组件
import { ShareSocialOutline, LinkOutline, LogoTwitter, LogoFacebook } from "@vicons/ionicons5";

// 使用消息提示
const message = useMessage();

// 定义组件属性
const props = defineProps<{
  title: string; // 分享标题
  url?: string; // 分享链接，默认为当前页面
}>();

// 获取分享链接，如果未提供则使用当前页面URL
const shareUrl = props.url || window.location.href;

// 复制链接到剪贴板
const copyLink = () => {
  navigator.clipboard.writeText(shareUrl);
  message.success("链接已复制到剪贴板");
};

// 分享到Twitter
const shareToTwitter = () => {
  const text = encodeURIComponent(props.title);
  const url = encodeURIComponent(shareUrl);
  window.open(`https://twitter.com/intent/tweet?text=${text}&url=${url}`, "_blank");
};

// 分享到Facebook
const shareToFacebook = () => {
  const url = encodeURIComponent(shareUrl);
  window.open(`https://www.facebook.com/sharer/sharer.php?u=${url}`, "_blank");
};

// 导入Vue的h函数用于渲染组件
import { h } from "vue";

// 下拉菜单选项配置
const options = [
  {
    label: "复制链接",
    key: "copy",
    icon: () => h(NIcon, null, { default: () => h(LinkOutline) }),
  },
  {
    label: "分享到 Twitter",
    key: "twitter",
    icon: () => h(NIcon, null, { default: () => h(LogoTwitter) }),
  },
  {
    label: "分享到 Facebook",
    key: "facebook",
    icon: () => h(NIcon, null, { default: () => h(LogoFacebook) }),
  },
];

// 处理下拉菜单选择事件
const handleSelect = (key: string) => {
  switch (key) {
    case "copy":
      copyLink();
      break;
    case "twitter":
      shareToTwitter();
      break;
    case "facebook":
      shareToFacebook();
      break;
  }
};
</script>

<template>
  <!-- 分享按钮下拉菜单 -->
  <n-dropdown :options="options" @select="handleSelect" trigger="click">
    <!-- 分享按钮 -->
    <n-button quaternary circle>
      <!-- 分享图标 -->
      <template #icon>
        <n-icon :component="ShareSocialOutline" />
      </template>
    </n-button>
  </n-dropdown>
</template>
