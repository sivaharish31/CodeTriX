import axios from 'axios';
import type {
  ViolationRequest,
  ViolationResponse,
  AdminViolationsResponse,
  TeamViolationSummary,
  UpdateReviewStatusRequest,
} from '../types/proctoring';

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

export const proctoringApi = {
  reportViolation: async (request: ViolationRequest): Promise<ViolationResponse | null> => {
    try {
      const response = await api.post<ViolationResponse>('/proctoring/violations', request);
      return response.data;
    } catch (error) {
      console.error('Failed to report violation:', error);
      return null;
    }
  },

  reportViolationsBatch: async (requests: ViolationRequest[]): Promise<{ recorded: number; total: number }> => {
    try {
      const response = await api.post<{ recorded: number; total: number }>(
        '/proctoring/violations/batch',
        requests
      );
      return response.data;
    } catch (error) {
      console.error('Failed to report violations batch:', error);
      return { recorded: 0, total: requests.length };
    }
  },

  admin: {
    getViolations: async (limit = 100): Promise<AdminViolationsResponse> => {
      const response = await api.get<AdminViolationsResponse>(
        `/admin/proctoring/violations?limit=${limit}`
      );
      return response.data;
    },

    getTeamViolations: async (teamId: number): Promise<TeamViolationSummary> => {
      const response = await api.get<TeamViolationSummary>(`/admin/proctoring/teams/${teamId}`);
      return response.data;
    },

    updateReviewStatus: async (
      teamId: number,
      request: UpdateReviewStatusRequest
    ): Promise<TeamViolationSummary> => {
      const response = await api.put<TeamViolationSummary>(
        `/admin/proctoring/teams/${teamId}/review`,
        request
      );
      return response.data;
    },

    getReviewQueue: async (): Promise<TeamViolationSummary[]> => {
      const response = await api.get<TeamViolationSummary[]>('/admin/proctoring/review-queue');
      return response.data;
    },
  },
};

export default proctoringApi;
