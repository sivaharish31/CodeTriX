import axios from 'axios';
import type {
  CtfChallenge,
  CtfChallengeAdmin,
  CtfSubmitRequest,
  CtfSubmitResponse,
  CtfSubmissionListResponse,
  CreateCtfChallengeRequest,
  UpdateCtfChallengeRequest,
} from '../types/ctf';

const API_BASE = '/api';

const api = axios.create({
  baseURL: API_BASE,
  headers: {
    'Content-Type': 'application/json',
  },
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export const ctfApi = {
  getChallenges: async (): Promise<CtfChallenge[]> => {
    const response = await api.get<CtfChallenge[]>('/ctf/challenges');
    return response.data;
  },

  getChallenge: async (id: number): Promise<CtfChallenge> => {
    const response = await api.get<CtfChallenge>(`/ctf/challenges/${id}`);
    return response.data;
  },

  submitFlag: async (challengeId: number, flag: string): Promise<CtfSubmitResponse> => {
    const request: CtfSubmitRequest = { challengeId, flag };
    const response = await api.post<CtfSubmitResponse>(
      `/ctf/challenges/${challengeId}/submit`,
      request
    );
    return response.data;
  },

  getSubmissions: async (): Promise<CtfSubmissionListResponse> => {
    const response = await api.get<CtfSubmissionListResponse>('/ctf/submissions');
    return response.data;
  },

  getAttachmentUrl: (challengeId: number): string => {
    const token = localStorage.getItem('token');
    return `${API_BASE}/ctf/challenges/${challengeId}/attachment?token=${token}`;
  },

  downloadAttachment: async (challengeId: number, filename: string): Promise<void> => {
    const response = await api.get(`/ctf/challenges/${challengeId}/attachment`, {
      responseType: 'blob',
    });

    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', filename);
    document.body.appendChild(link);
    link.click();
    link.remove();
    window.URL.revokeObjectURL(url);
  },

  // Admin APIs
  admin: {
    getChallenges: async (): Promise<CtfChallengeAdmin[]> => {
      const response = await api.get<CtfChallengeAdmin[]>('/admin/ctf/challenges');
      return response.data;
    },

    getChallenge: async (id: number): Promise<CtfChallengeAdmin> => {
      const response = await api.get<CtfChallengeAdmin>(`/admin/ctf/challenges/${id}`);
      return response.data;
    },

    createChallenge: async (
      request: CreateCtfChallengeRequest,
      attachment?: File
    ): Promise<CtfChallengeAdmin> => {
      if (attachment) {
        const formData = new FormData();
        formData.append('challenge', new Blob([JSON.stringify(request)], { type: 'application/json' }));
        formData.append('attachment', attachment);

        const response = await api.post<CtfChallengeAdmin>('/admin/ctf/challenges', formData, {
          headers: { 'Content-Type': 'multipart/form-data' },
        });
        return response.data;
      } else {
        const response = await api.post<CtfChallengeAdmin>('/admin/ctf/challenges', request);
        return response.data;
      }
    },

    updateChallenge: async (
      id: number,
      request: UpdateCtfChallengeRequest,
      attachment?: File
    ): Promise<CtfChallengeAdmin> => {
      if (attachment) {
        const formData = new FormData();
        formData.append('challenge', new Blob([JSON.stringify(request)], { type: 'application/json' }));
        formData.append('attachment', attachment);

        const response = await api.put<CtfChallengeAdmin>(`/admin/ctf/challenges/${id}`, formData, {
          headers: { 'Content-Type': 'multipart/form-data' },
        });
        return response.data;
      } else {
        const response = await api.put<CtfChallengeAdmin>(`/admin/ctf/challenges/${id}`, request);
        return response.data;
      }
    },

    deleteChallenge: async (id: number): Promise<void> => {
      await api.delete(`/admin/ctf/challenges/${id}`);
    },

    removeAttachment: async (id: number): Promise<void> => {
      await api.delete(`/admin/ctf/challenges/${id}/attachment`);
    },
  },
};

export default ctfApi;
