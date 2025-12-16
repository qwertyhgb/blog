<script setup lang="ts">
import { ref, onMounted, h } from "vue";
import { useRouter } from "vue-router";
import {
  NButton,
  NInput,
  NDataTable,
  NPagination,
  NTag,
  NSpace,
  useMessage,
  useDialog,
  type DataTableColumns,
} from "naive-ui";
import {
  SearchOutline,
  AddOutline,
  EyeOutline,
  CreateOutline,
  TrashOutline,
} from "@vicons/ionicons5";

const router = useRouter();
const message = useMessage();
const dialog = useDialog();

const loading = ref(false);
const searchKeyword = ref("");
const currentPage = ref(1);
const pageSize = ref(10);

const posts = ref<any[]>([]);
const total = ref(0);

// Columns definition
const columns: DataTableColumns = [
  {
    title: "ID",
    key: "id",
    width: 80,
  },
  {
    title: "标题",
    key: "title",
    width: 200,
    ellipsis: {
      tooltip: true,
    },
  },
  {
    title: "分类",
    key: "category.name",
    width: 120,
    render(row: any) {
      return row.category?.name || "-";
    },
  },
  {
    title: "标签",
    key: "tags",
    width: 200,
    render(row: any) {
      const tags = row.tags || [];
      return h(
        NSpace,
        { size: 4 },
        {
          default: () =>
            tags.map((tag: any) =>
              h(NTag, { size: "small", bordered: false }, { default: () => tag.name })
            ),
        }
      );
    },
  },
  {
    title: "浏览量",
    key: "viewCount",
    width: 100,
  },
  {
    title: "创建时间",
    key: "createTime",
    width: 150,
    render(row: any) {
      return formatDate(row.createTime);
    },
  },
  {
    title: "操作",
    key: "actions",
    width: 200,
    render(row: any) {
      return h(
        NSpace,
        { size: 8 },
        {
          default: () => [
            h(
              NButton,
              {
                size: "small",
                type: "info",
                secondary: true,
                onClick: () => viewPost(row.id),
              },
              { icon: () => h(EyeOutline), default: () => "查看" }
            ),
            h(
              NButton,
              {
                size: "small",
                type: "primary",
                secondary: true,
                onClick: () => editPost(row.id),
              },
              { icon: () => h(CreateOutline), default: () => "编辑" }
            ),
            h(
              NButton,
              {
                size: "small",
                type: "error",
                secondary: true,
                onClick: () => deletePost(row.id),
              },
              { icon: () => h(TrashOutline), default: () => "删除" }
            ),
          ],
        }
      );
    },
  },
];

// 方法
const fetchPosts = async () => {
  try {
    loading.value = true;
    const { postApi } = await import("@/api");
    const res = await postApi.getAdminPosts({
      page: currentPage.value,
      size: pageSize.value,
      keyword: searchKeyword.value || undefined,
    });
    if (res.data) {
      posts.value = res.data.records || [];
      total.value = res.data.total || 0;
    }
  } catch (error) {
    console.error("获取文章列表失败:", error);
    message.error("获取文章列表失败");
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  currentPage.value = 1;
  fetchPosts();
};

const handlePageChange = (page: number) => {
  currentPage.value = page;
  fetchPosts();
};

const viewPost = (id: number) => {
  router.push(`/post/${id}`);
};

const editPost = (id: number) => {
  router.push(`/post/edit/${id}`);
};

const deletePost = async (id: number) => {
  dialog.warning({
    title: "提示",
    content: "确定要删除这篇文章吗？",
    positiveText: "确定",
    negativeText: "取消",
    onPositiveClick: async () => {
      try {
        const { postApi } = await import("@/api");
        await postApi.deletePost(id);
        message.success("删除成功");
        fetchPosts();
      } catch (error: any) {
        message.error(error.message || "删除失败");
      }
    },
  });
};

const createPost = () => {
  router.push("/post/edit");
};

const formatDate = (dateString: string) => {
  const date = new Date(dateString);
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, "0")}-${String(date.getDate()).padStart(2, "0")}`;
};

// 初始化
onMounted(() => {
  fetchPosts();
});
</script>

<template>
  <div class="post-management">
    <div class="page-header">
      <h2>文章管理</h2>
      <n-button type="primary" @click="createPost">
        <template #icon>
          <n-icon :component="AddOutline" />
        </template>
        新建文章
      </n-button>
    </div>

    <div class="search-bar">
      <n-input
        v-model:value="searchKeyword"
        placeholder="搜索文章标题"
        class="search-input"
        @keyup.enter="handleSearch"
      >
        <template #suffix>
          <n-icon :component="SearchOutline" class="search-icon" @click="handleSearch" />
        </template>
      </n-input>
    </div>

    <div class="post-table">
      <n-data-table
        :columns="columns"
        :data="posts"
        :loading="loading"
        :bordered="false"
        :single-line="false"
      />

      <div class="pagination">
        <n-pagination
          v-model:page="currentPage"
          :page-count="Math.ceil(total / pageSize)"
          :page-size="pageSize"
          @update:page="handlePageChange"
        />
      </div>
    </div>
  </div>
</template>

<style scoped>
.post-management {
  width: 100%;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  font-size: 20px;
  color: var(--n-text-color);
}

.search-bar {
  margin-bottom: 20px;
}

.search-input {
  width: 300px;
}

.search-icon {
  cursor: pointer;
}

.post-table {
  background-color: var(--n-color);
  border-radius: 8px;
  padding: 20px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>
