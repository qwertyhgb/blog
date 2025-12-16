<script setup lang="ts">
import { ref, onMounted, computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import { usePostStore } from "@/stores/post";
import { useCommentStore } from "@/stores/comment";
import { useUserStore } from "@/stores/user";
import {
  NTag,
  NIcon,
  NButton,
  NForm,
  NFormItem,
  NInput,
  NAvatar,
  NEmpty,
  NSkeleton,
  useMessage,
  useDialog,
  NDivider,
  NDropdown,
  type FormInst,
} from "naive-ui";
import {
  CalendarOutline,
  PersonOutline,
  EyeOutline,
  FolderOutline,
  PricetagOutline,
  CreateOutline,
  TrashOutline,
  HeartOutline,
  ChatbubbleOutline,
  ShareSocialOutline,
  BookmarkOutline,
  EllipsisHorizontal,
} from "@vicons/ionicons5";
import MarkdownIt from "markdown-it";
import hljs from "highlight.js";
import "highlight.js/styles/github.css";
import ReadingProgress from "@/components/ReadingProgress.vue";
import TableOfContents from "@/components/TableOfContents.vue";
import BackTop from "@/components/BackTop.vue";
import ShareButton from "@/components/ShareButton.vue";

const route = useRoute();
const router = useRouter();
const postStore = usePostStore();
const commentStore = useCommentStore();
const userStore = useUserStore();
const message = useMessage();
const dialog = useDialog();

const postId = computed(() => Number(route.params.id));
const post = computed(() => postStore.currentPost);
const comments = computed(() => commentStore.comments);
const isLoggedIn = computed(() => userStore.isLoggedIn);
const isAdmin = computed(() => userStore.isAdmin);

const loading = ref(false);

const submittingComment = ref(false);
const showCommentForm = ref(false);
const commentFormRef = ref<FormInst | null>(null);

const commentForm = ref({
  content: "",
});

const commentRules = {
  content: [
    { required: true, message: "请输入评论内容", trigger: "blur" },
    { min: 5, max: 500, message: "长度应在 5 到 500 个字符之间", trigger: "blur" },
  ],
};

// Initialize Markdown renderer
const md = new MarkdownIt({
  html: true,
  linkify: true,
  typographer: true,
  highlight: function (str: string, lang: string) {
    if (lang && hljs.getLanguage(lang)) {
      try {
        return hljs.highlight(str, { language: lang }).value;
      } catch (__) {}
    }
    return "";
  },
});

// Compute rendered HTML content
const renderedContent = computed(() => {
  return post.value ? md.render(post.value.content) : "";
});

// Methods
const fetchPost = async () => {
  try {
    loading.value = true;
    await postStore.fetchPostById(postId.value);

    if (postStore.currentPost) {
      await commentStore.fetchCommentsByPostId(postId.value);
    }
  } catch (error: any) {
    console.error("获取文章失败:", error);
    message.error(error.message || "获取文章失败");
    setTimeout(() => {
      router.push("/");
    }, 1500);
  } finally {
    loading.value = false;
  }
};

const formatDate = (dateString: string) => {
  const date = new Date(dateString);
  return date.toLocaleDateString("zh-CN", { month: "short", day: "numeric", year: "numeric" });
};

const goToCategory = (id: number) => {
  router.push(`/category/${id}`);
};

const goToTag = (id: number) => {
  router.push(`/tag/${id}`);
};

const editPost = () => {
  router.push(`/post/edit/${postId.value}`);
};

const deletePost = async () => {
  dialog.warning({
    title: "删除文章",
    content: "确定要删除这篇文章吗？",
    positiveText: "删除",
    negativeText: "取消",
    onPositiveClick: async () => {
      try {
        await postStore.deletePost(postId.value);
        message.success("删除成功");
        router.push("/");
      } catch (error: any) {
        message.error(error.message || "删除失败");
      }
    },
  });
};

const toggleCommentForm = () => {
  showCommentForm.value = !showCommentForm.value;
};

const submitComment = (e: MouseEvent) => {
  e.preventDefault();
  commentFormRef.value?.validate(async (errors) => {
    if (!errors) {
      try {
        submittingComment.value = true;
        await commentStore.createComment({
          postId: postId.value,
          content: commentForm.value.content,
        });
        message.success("评论已发布");
        commentForm.value.content = "";
        showCommentForm.value = false;
        await commentStore.fetchCommentsByPostId(postId.value);
      } catch (error: any) {
        message.error(error.message || "评论发布失败");
      } finally {
        submittingComment.value = false;
      }
    }
  });
};

const deleteComment = async (id: number) => {
  dialog.warning({
    title: "删除评论",
    content: "确定要删除这条评论吗？",
    positiveText: "删除",
    negativeText: "取消",
    onPositiveClick: async () => {
      try {
        await commentStore.deleteComment(id);
        message.success("删除成功");
        await commentStore.fetchCommentsByPostId(postId.value);
      } catch (error: any) {
        message.error(error.message || "删除失败");
      }
    },
  });
};

const liking = ref(false);
const handleLike = async () => {
  if (liking.value) return;

  try {
    liking.value = true;
    await postStore.likePost(postId.value);
    message.success("点赞成功");
    if (post.value) {
      post.value.likeCount = (post.value.likeCount || 0) + 1;
    }
  } catch (error: any) {
    message.error(error.message || "点赞失败");
  } finally {
    liking.value = false;
  }
};

onMounted(() => {
  fetchPost();
});
</script>

<template>
  <reading-progress />
  <div class="article-page">
    <div v-if="loading" class="loading-container">
      <n-skeleton text style="width: 60%; height: 48px; margin-bottom: 24px" />
      <div class="meta-skeleton">
        <n-skeleton circle width="48px" height="48px" />
        <div class="meta-text">
          <n-skeleton text width="120px" />
          <n-skeleton text width="80px" />
        </div>
      </div>
      <n-skeleton text :repeat="10" style="margin-top: 40px" />
    </div>

    <div v-else-if="post" class="article-layout">
      <article class="article-content">
        <h1 class="article-title serif">{{ post.title }}</h1>

        <div class="article-meta">
          <div class="author-info">
            <n-avatar
              round
              size="medium"
              :src="post.author?.avatar"
              :fallback-src="'https://07akioni.oss-cn-beijing.aliyuncs.com/07akioni.jpeg'"
            />
            <div class="author-details">
              <div class="author-name">
                {{ post.author?.nickname || post.author?.username || "匿名用户" }}
              </div>
              <div class="publish-info">
                <span>{{ formatDate(post.createTime) }}</span>
                <span class="dot">·</span>
                <span>{{ Math.ceil(post.content.length / 500) }} 分钟阅读</span>
              </div>
            </div>
          </div>

          <div class="article-actions">
            <share-button :title="post.title" />

            <n-button quaternary circle>
              <template #icon>
                <n-icon :component="BookmarkOutline" />
              </template>
            </n-button>

            <n-dropdown
              v-if="isAdmin"
              trigger="click"
              :options="[
                { label: '编辑', key: 'edit' },
                { label: '删除', key: 'delete' },
              ]"
              @select="(key) => (key === 'edit' ? editPost() : deletePost())"
            >
              <n-button quaternary circle>
                <template #icon>
                  <n-icon :component="CreateOutline" />
                </template>
              </n-button>
            </n-dropdown>
          </div>
        </div>

        <n-divider />

        <div class="markdown-body serif" v-html="renderedContent"></div>

        <div class="article-tags">
          <n-tag
            v-if="post.category"
            round
            :bordered="false"
            class="tag-pill"
            @click="goToCategory(post.category.id)"
          >
            {{ post.category.name }}
          </n-tag>

          <n-tag
            v-for="tag in post.tags"
            :key="tag.id"
            round
            :bordered="false"
            class="tag-pill"
            @click="goToTag(tag.id)"
          >
            {{ tag.name }}
          </n-tag>
        </div>

        <div class="interaction-bar">
          <div class="left">
            <n-button quaternary class="action-btn" @click="handleLike" :loading="liking">
              <template #icon>
                <n-icon :component="HeartOutline" />
              </template>
              {{ post.likeCount || 0 }}
            </n-button>
            <n-button quaternary class="action-btn" @click="toggleCommentForm">
              <template #icon>
                <n-icon :component="ChatbubbleOutline" />
              </template>
              {{ comments.length }}
            </n-button>
            <n-button quaternary class="action-btn">
              <template #icon>
                <n-icon :component="EyeOutline" />
              </template>
              {{ post.viewCount || 0 }}
            </n-button>
          </div>
          <div class="right">
            <share-button :title="post.title" />
            <n-button quaternary circle>
              <template #icon>
                <n-icon :component="BookmarkOutline" />
              </template>
            </n-button>
          </div>
        </div>

        <div class="comments-section" id="comments">
          <h3>评论 ({{ comments.length }})</h3>

          <div class="comment-form-container" v-if="isLoggedIn">
            <div class="user-avatar-small">
              <n-avatar
                round
                size="small"
                :src="userStore.userInfo?.avatar"
                :fallback-src="'https://07akioni.oss-cn-beijing.aliyuncs.com/07akioni.jpeg'"
              />
            </div>
            <div class="form-wrapper">
              <n-input
                v-model:value="commentForm.content"
                type="textarea"
                :rows="3"
                placeholder="写下你的想法..."
                :bordered="false"
                class="comment-input"
              />
              <div class="form-actions">
                <n-button
                  size="small"
                  type="primary"
                  round
                  color="#1a8917"
                  :disabled="!commentForm.content.trim()"
                  :loading="submittingComment"
                  @click="submitComment"
                >
                  发布
                </n-button>
              </div>
            </div>
          </div>
          <div v-else class="login-prompt">
            <n-button quaternary type="primary" @click="router.push('/login')"
              >登录后发表评论</n-button
            >
          </div>

          <div class="comment-list">
            <div v-for="comment in comments" :key="comment.id" class="comment-item">
              <div class="comment-header">
                <div class="comment-author">
                  <n-avatar
                    round
                    size="small"
                    :src="comment.user?.avatar"
                    :fallback-src="'https://07akioni.oss-cn-beijing.aliyuncs.com/07akioni.jpeg'"
                  />
                  <div class="comment-meta">
                    <span class="name">{{
                      comment.user?.nickname || comment.user?.username || "匿名用户"
                    }}</span>
                    <span class="time">{{ formatDate(comment.createTime) }}</span>
                  </div>
                </div>

                <n-dropdown
                  v-if="isAdmin || (isLoggedIn && userStore.userInfo?.id === comment.user?.id)"
                  trigger="click"
                  :options="[{ label: '删除', key: 'delete' }]"
                  @select="() => deleteComment(comment.id)"
                >
                  <n-button quaternary circle size="tiny">
                    <template #icon>
                      <n-icon :component="EllipsisHorizontal" />
                    </template>
                  </n-button>
                </n-dropdown>
              </div>

              <div class="comment-body serif">
                {{ comment.content }}
              </div>
            </div>
          </div>
        </div>
      </article>

      <aside class="article-sidebar">
        <table-of-contents />
      </aside>
    </div>
  </div>
  <back-top />
</template>

<style scoped>
.article-page {
  max-width: 1400px;
  margin: 0 auto;
  padding: 24px 32px 60px;
  min-height: 100vh;
}

.article-layout {
  display: flex;
  gap: 48px;
  align-items: flex-start;
}

.loading-container,
.article-content {
  flex: 1;
  max-width: 900px;
}

.article-sidebar {
  width: 280px;
  flex-shrink: 0;
  display: none;
}

@media (min-width: 1200px) {
  .article-sidebar {
    display: block;
  }
}

.meta-skeleton {
  display: flex;
  gap: 16px;
  align-items: center;
}

.article-title {
  font-size: 40px;
  line-height: 1.2;
  margin-bottom: 24px;
  font-weight: 700;
  color: var(--text-color);
  letter-spacing: -0.5px;
}

.article-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.author-details {
  display: flex;
  flex-direction: column;
}

.author-name {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-color);
}

.publish-info {
  font-size: 13px;
  color: var(--text-secondary);
}

.dot {
  margin: 0 4px;
}

.article-actions {
  display: flex;
  gap: 8px;
}

.markdown-body {
  font-size: 16px;
  line-height: 1.7;
  color: var(--text-color);
  margin-top: 32px;
  margin-bottom: 48px;
}

.markdown-body :deep(p) {
  margin-bottom: 16px;
}

.markdown-body :deep(h1),
.markdown-body :deep(h2),
.markdown-body :deep(h3) {
  font-weight: 600;
  margin-top: 32px;
  margin-bottom: 12px;
  color: var(--text-color);
}

.markdown-body :deep(h1) {
  font-size: 32px;
}

.markdown-body :deep(h2) {
  font-size: 24px;
}

.markdown-body :deep(h3) {
  font-size: 20px;
}

.markdown-body :deep(blockquote) {
  border-left: 3px solid var(--border-color);
  padding-left: 16px;
  margin: 16px 0;
  color: var(--text-secondary);
  font-style: italic;
}

.markdown-body :deep(img) {
  max-width: 100%;
  height: auto;
  margin: 24px 0;
  border-radius: 6px;
}

.markdown-body :deep(pre) {
  background-color: var(--bg-secondary);
  padding: 16px;
  border-radius: 6px;
  overflow-x: auto;
  font-size: 14px;
  font-family: var(--font-mono);
  margin: 16px 0;
  border: 1px solid var(--border-color);
}

.markdown-body :deep(code) {
  background-color: var(--bg-secondary);
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 14px;
  font-family: var(--font-mono);
}

.article-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 40px;
}

.tag-pill {
  background-color: var(--bg-secondary);
  color: var(--text-secondary);
  cursor: pointer;
  padding: 6px 12px;
  font-size: 13px;
  transition: all 0.15s ease;
}

.tag-pill:hover {
  background-color: var(--bg-hover);
  transform: translateY(-1px);
}

.interaction-bar {
  display: flex;
  justify-content: space-between;
  padding: 16px 0;
  border-top: 1px solid var(--border-color);
  border-bottom: 1px solid var(--border-color);
  margin-bottom: 40px;
}

.left,
.right {
  display: flex;
  gap: 16px;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 8px;
}

.comments-section h3 {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 24px;
  color: var(--text-color);
}

.comment-form-container {
  display: flex;
  gap: 12px;
  margin-bottom: 32px;
  padding: 16px;
  background-color: var(--bg-secondary);
  border-radius: 8px;
  border: 1px solid var(--border-color);
}

.form-wrapper {
  flex: 1;
}

.comment-input {
  margin-bottom: 16px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
}

.comment-item {
  padding: 20px 0;
  border-bottom: 1px solid var(--border-color);
}

.comment-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
}

.comment-author {
  display: flex;
  align-items: center;
  gap: 12px;
}

.comment-meta {
  display: flex;
  flex-direction: column;
}

.comment-meta .name {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-color);
}

.comment-meta .time {
  font-size: 12px;
  color: var(--text-tertiary);
}

.comment-body {
  font-size: 14px;
  line-height: 1.6;
  color: var(--text-color);
  margin-left: 40px;
}

.login-prompt {
  text-align: center;
  padding: 32px;
  background-color: var(--bg-secondary);
  border-radius: 8px;
  margin-bottom: 32px;
  border: 1px solid var(--border-color);
}

@media (max-width: 768px) {
  .article-page {
    padding: 24px 16px 60px;
  }

  .article-layout {
    flex-direction: column;
  }

  .article-title {
    font-size: 28px;
  }

  .markdown-body {
    font-size: 15px;
  }

  .article-content {
    max-width: 100%;
  }
}
</style>
