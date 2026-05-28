import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

const apiClient = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

export const PostService = {
    getAll: async (params) => {
        const response = await apiClient.get('/posts', { params });
        return response.data;
    },

    getById: async (id) => {
        const response = await apiClient.get(`/posts/${id}`);
        return response.data;
    },

    create: async (postData) => {
        const response = await apiClient.post('/posts', postData);
        return response.data;
    },

    update: async (id, postData) => {
        const response = await apiClient.put(`/posts/${id}`, postData);
        return response.data;
    },

    delete: async (id) => {
        await apiClient.delete(`/posts/${id}`);
    }
};

export const SpaceService = {
    getAll: async () => (await apiClient.get('/spaces')).data
};

export const TagService = {
    getAll: async () => (await apiClient.get('/tags')).data
};
