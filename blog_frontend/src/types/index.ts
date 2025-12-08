export interface ApiResponse<T> {
    code: number;
    message: string;
    data: T;
}

export interface PageResult<T> {
    records: T[];
    total: number;
    size: number;
    current: number;
    pages: number;
}

export interface User {
    id: number;
    username: string;
    nickname: string;
    email?: string;
    avatar?: string;
    role?: string;
    createTime: string;
}

export interface Category {
    id: number;
    name: string;
    description?: string;
    postCount?: number;
    createTime?: string;
}

export interface Tag {
    id: number;
    name: string;
    postCount?: number;
    createTime?: string;
}

export interface Post {
    id: number;
    title: string;
    content: string;
    summary?: string;
    coverImage?: string;
    viewCount: number;
    likeCount: number;
    commentCount?: number;
    status: number; // 0: draft, 1: published
    createTime: string;
    updateTime: string;
    authorId: number;
    categoryId: number;
    author?: User;
    category?: Category;
    tags?: Tag[];
}

export interface Comment {
    id: number;
    content: string;
    createTime: string;
    postId: number;
    userId: number;
    parentId?: number;
    status: number; // 0: pending, 1: approved, 2: rejected
    user?: User;
}
