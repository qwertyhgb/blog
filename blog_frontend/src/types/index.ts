// API响应通用接口
export interface ApiResponse<T> {
    code: number;      // 响应状态码
    message: string;   // 响应消息
    data: T;          // 响应数据
}

// 分页结果通用接口
export interface PageResult<T> {
    records: T[];     // 当前页数据列表
    total: number;     // 总记录数
    size: number;      // 每页大小
    current: number;   // 当前页码
    pages: number;     // 总页数
}

// 用户接口
export interface User {
    id: number;            // 用户ID
    username: string;      // 用户名
    nickname: string;      // 昵称
    email?: string;        // 邮箱（可选）
    avatar?: string;       // 头像（可选）
    role?: string;         // 角色（可选）
    createTime: string;    // 创建时间
}

// 分类接口
export interface Category {
    id: number;            // 分类ID
    name: string;          // 分类名称
    description?: string;  // 分类描述（可选）
    postCount?: number;    // 文章数量（可选）
    createTime?: string;   // 创建时间（可选）
}

// 标签接口
export interface Tag {
    id: number;            // 标签ID
    name: string;          // 标签名称
    postCount?: number;    // 文章数量（可选）
    createTime?: string;   // 创建时间（可选）
}

// 文章接口
export interface Post {
    id: number;            // 文章ID
    title: string;         // 标题
    content: string;       // 内容
    summary?: string;      // 摘要（可选）
    coverImage?: string;   // 封面图片（可选）
    viewCount: number;     // 浏览次数
    likeCount: number;     // 点赞次数
    commentCount?: number; // 评论数量（可选）
    status: number;        // 状态：0-草稿，1-已发布
    createTime: string;    // 创建时间
    updateTime: string;    // 更新时间
    authorId: number;      // 作者ID
    categoryId: number;    // 分类ID
    author?: User;         // 作者信息（可选）
    category?: Category;   // 分类信息（可选）
    tags?: Tag[];          // 标签列表（可选）
}

// 评论接口
export interface Comment {
    id: number;            // 评论ID
    content: string;       // 评论内容
    createTime: string;    // 创建时间
    postId: number;        // 文章ID
    userId: number;        // 用户ID
    parentId?: number;     // 父评论ID（可选，用于回复）
    status: number;        // 状态：0-待审核，1-已通过，2-已拒绝
    user?: User;           // 用户信息（可选）
}