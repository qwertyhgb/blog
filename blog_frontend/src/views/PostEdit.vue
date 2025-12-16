<script setup lang="ts">
import { ref, reactive, computed, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { usePostStore } from "@/stores/post";
import { useCategoryStore, useTagStore } from "@/stores/category";
import {
  NButton,
  NForm,
  NFormItem,
  NInput,
  NSelect,
  NCard,
  NIcon,
  useMessage,
  type FormInst,
} from "naive-ui";
import { ArrowBackOutline, AddOutline } from "@vicons/ionicons5";

const route = useRoute();
const router = useRouter();
const postStore = usePostStore();
const categoryStore = useCategoryStore();
const tagStore = useTagStore();
const message = useMessage();

const postId = computed(() => Number(route.params.id));
const isEdit = computed(() => !!postId.value);
const categories = computed(() => categoryStore.categories);
const tags = computed(() => tagStore.tags);

const loading = ref(false);
const submitting = ref(false);
const postFormRef = ref<FormInst | null>(null);

const postForm = reactive({
  title: "",
  content: "",
  summary: "",
  categoryId: null as number | null,
  tagIds: [] as number[],
});

const postRules = {
  title: [
    { required: true, message: "请输入文章标题", trigger: "blur" },
    { min: 5, max: 100, message: "长度在 5 到 100 个字符", trigger: "blur" },
  ],
  content: [
    { required: true, message: "请输入文章内容", trigger: "blur" },
    { min: 10, message: "内容至少10个字符", trigger: "blur" },
  ],
  summary: [{ max: 500, message: "摘要最多500个字符", trigger: "blur" }],
  categoryId: [
    { required: true, type: "number" as const, message: "请选择文章分类", trigger: "change" },
  ],
};

const newTagInput = ref("");
const showNewTagInput = ref(false);

// 方法
const fetchPost = async () => {
  if (!isEdit.value) return;

  try {
    loading.value = true;
    await postStore.fetchPostById(postId.value);
    const post = postStore.currentPost;

    if (post) {
      postForm.title = post.title;
      postForm.content = post.content;
      postForm.summary = post.summary || "";
      postForm.categoryId = post.category?.id || null;
      postForm.tagIds = post.tags?.map((tag: any) => tag.id) || [];
    }
  } catch (error: any) {
    message.error(error.message || "获取文章详情失败");
    router.push("/admin/posts");
  } finally {
    loading.value = false;
  }
};

const submitPost = (e: MouseEvent) => {
  e.preventDefault();
  postFormRef.value?.validate(async (errors) => {
    if (!errors) {
      try {
        submitting.value = true;

        const postData = {
          title: postForm.title,
          content: postForm.content,
          summary: postForm.summary,
          categoryId: postForm.categoryId,
          tagIds: postForm.tagIds,
        };

        if (isEdit.value) {
          await postStore.updatePost(postId.value, postData);
          message.success("更新成功");
        } else {
          await postStore.createPost(postData);
          message.success("创建成功");
        }

        router.back();
      } catch (error: any) {
        message.error(error.message || (isEdit.value ? "更新失败" : "创建失败"));
      } finally {
        submitting.value = false;
      }
    }
  });
};

const goBack = () => {
  router.back();
};

const showAddTag = () => {
  showNewTagInput.value = true;
};

const addNewTag = async () => {
  if (!newTagInput.value.trim()) {
    message.warning("请输入标签名称");
    return;
  }

  try {
    await tagStore.createTag({ name: newTagInput.value.trim() });
    message.success("标签添加成功");
    newTagInput.value = "";
    showNewTagInput.value = false;
    await tagStore.fetchTags();
  } catch (error: any) {
    message.error(error.message || "添加标签失败");
  }
};

const cancelAddTag = () => {
  newTagInput.value = "";
  showNewTagInput.value = false;
};

// 初始化
onMounted(async () => {
  await Promise.all([categoryStore.fetchCategories(), tagStore.fetchTags(), fetchPost()]);
});
</script>

<template>
  <div class="post-edit-page">
    <div class="edit-header">
      <n-button text @click="goBack">
        <template #icon>
          <n-icon :component="ArrowBackOutline" />
        </template>
        返回
      </n-button>
      <div class="header-actions">
        <n-button @click="goBack">取消</n-button>
        <n-button type="primary" :loading="submitting" @click="submitPost">
          {{ isEdit ? "更新" : "发布" }}
        </n-button>
      </div>
    </div>

    <div class="edit-container">
      <n-input
        v-model:value="postForm.title"
        placeholder="无标题"
        :bordered="false"
        size="large"
        class="title-input"
      />

      <div class="metadata-section">
        <div class="metadata-item">
          <span class="label">分类</span>
          <n-select
            v-model:value="postForm.categoryId"
            :options="categories.map((c) => ({ label: c.name, value: c.id }))"
            placeholder="选择分类"
            size="small"
            style="width: 200px"
          />
        </div>

        <div class="metadata-item">
          <span class="label">标签</span>
          <n-select
            v-model:value="postForm.tagIds"
            multiple
            :options="tags.map((t) => ({ label: t.name, value: t.id }))"
            placeholder="选择标签"
            size="small"
            style="width: 300px"
          />
        </div>
      </div>

      <n-input
        v-model:value="postForm.summary"
        type="textarea"
        :bordered="false"
        :rows="2"
        placeholder="添加摘要（可选）"
        class="summary-input"
      />

      <n-input
        v-model:value="postForm.content"
        type="textarea"
        :bordered="false"
        :autosize="{ minRows: 20 }"
        placeholder="开始写作... 支持 Markdown 格式"
        class="content-input"
      />
    </div>
  </div>
</template>

<style scoped>
.post-edit-page {
  max-width: 900px;
  margin: 0 auto;
  padding: 24px 32px 80px;
  min-height: 100vh;
}

.edit-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--border-color);
}

.header-actions {
  display: flex;
  gap: 12px;
}

.edit-container {
  background-color: var(--card-bg);
  border: 1px solid var(--border-color);
  border-radius: 8px;
  padding: 48px;
}

.title-input {
  font-size: 40px;
  font-weight: 700;
  margin-bottom: 24px;
}

.title-input :deep(.n-input__input-el) {
  font-size: 40px;
  font-weight: 700;
  color: var(--text-color);
}

.metadata-section {
  display: flex;
  gap: 24px;
  margin-bottom: 24px;
  padding-bottom: 24px;
  border-bottom: 1px solid var(--border-color);
  flex-wrap: wrap;
}

.metadata-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.metadata-item .label {
  font-size: 14px;
  color: var(--text-secondary);
  font-weight: 500;
}

.summary-input {
  margin-bottom: 24px;
  padding-bottom: 24px;
  border-bottom: 1px solid var(--border-color);
}

.summary-input :deep(.n-input__textarea-el) {
  font-size: 16px;
  color: var(--text-secondary);
  line-height: 1.6;
}

.content-input :deep(.n-input__textarea-el) {
  font-size: 16px;
  line-height: 1.7;
  color: var(--text-color);
  font-family: var(--font-mono);
}

@media (max-width: 768px) {
  .post-edit-page {
    padding: 16px;
  }

  .edit-container {
    padding: 24px 16px;
  }

  .title-input,
  .title-input :deep(.n-input__input-el) {
    font-size: 28px;
  }

  .metadata-section {
    flex-direction: column;
    gap: 16px;
  }

  .metadata-item {
    width: 100%;
  }

  .metadata-item .n-select {
    flex: 1;
  }
}
</style>
