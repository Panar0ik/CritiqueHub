import axios from 'axios';

// Рекомендуется выносить URL в `.env` переменные окружения
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

const apiClient = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

export const PostService = {
    // Получение всех постов с возможностью фильтрации
    getAll: async (params) => {
        const response = await apiClient.get('/posts', { params });
        return response.data;
    },

    getById: async (id) => {
        const response = await apiClient.get(`/posts/${id}`);
        return response.data;
    },

    // CRUD: Create
    create: async (postData) => {
        // postData должен содержать: title, content, spaceId (OneToMany), tagIds (ManyToMany)
        const response = await apiClient.post('/posts', postData);
        return response.data;
    },

    // CRUD: Update
    update: async (id, postData) => {
        const response = await apiClient.put(`/posts/${id}`, postData);
        return response.data;
    },

    // CRUD: Delete
    delete: async (id) => {
        await apiClient.delete(`/posts/${id}`);
    }
};

// Вспомогательные сервисы для получения списков связей в формах
export const SpaceService = {
    getAll: async () => (await apiClient.get('/spaces')).data
};

export const TagService = {
    getAll: async () => (await apiClient.get('/tags')).data
};
