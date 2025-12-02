<script setup lang="ts">
import { NButton, NIcon, NDropdown, useMessage } from 'naive-ui'
import { ShareSocialOutline, LinkOutline, LogoTwitter, LogoFacebook } from '@vicons/ionicons5'

const message = useMessage()

const props = defineProps<{
  title: string
  url?: string
}>()

const shareUrl = props.url || window.location.href

const copyLink = () => {
  navigator.clipboard.writeText(shareUrl)
  message.success('链接已复制到剪贴板')
}

const shareToTwitter = () => {
  const text = encodeURIComponent(props.title)
  const url = encodeURIComponent(shareUrl)
  window.open(`https://twitter.com/intent/tweet?text=${text}&url=${url}`, '_blank')
}

const shareToFacebook = () => {
  const url = encodeURIComponent(shareUrl)
  window.open(`https://www.facebook.com/sharer/sharer.php?u=${url}`, '_blank')
}

import { h } from 'vue'

const options = [
  {
    label: '复制链接',
    key: 'copy',
    icon: () => h(NIcon, null, { default: () => h(LinkOutline) })
  },
  {
    label: '分享到 Twitter',
    key: 'twitter',
    icon: () => h(NIcon, null, { default: () => h(LogoTwitter) })
  },
  {
    label: '分享到 Facebook',
    key: 'facebook',
    icon: () => h(NIcon, null, { default: () => h(LogoFacebook) })
  }
]

const handleSelect = (key: string) => {
  switch (key) {
    case 'copy':
      copyLink()
      break
    case 'twitter':
      shareToTwitter()
      break
    case 'facebook':
      shareToFacebook()
      break
  }
}
</script>

<template>
  <n-dropdown :options="options" @select="handleSelect" trigger="click">
    <n-button quaternary circle>
      <template #icon>
        <n-icon :component="ShareSocialOutline" />
      </template>
    </n-button>
  </n-dropdown>
</template>
