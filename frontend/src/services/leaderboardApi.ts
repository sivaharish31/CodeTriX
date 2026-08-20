import axios from 'axios';
import type {
  LeaderboardResponse,
  TeamScoreResponse,
  AdminScoresResponse,
} from '../types/leaderboard';

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

export const leaderboardApi = {
  getLeaderboard: async (): Promise<LeaderboardResponse> => {
    const response = await api.get<LeaderboardResponse>('/leaderboard');
    return response.data;
  },

  getTeamScore: async (teamId: number): Promise<TeamScoreResponse> => {
    const response = await api.get<TeamScoreResponse>(`/leaderboard/team/${teamId}`);
    return response.data;
  },

  getMyScore: async (): Promise<TeamScoreResponse> => {
    const response = await api.get<TeamScoreResponse>('/leaderboard/my-score');
    return response.data;
  },

  admin: {
    getScores: async (): Promise<AdminScoresResponse> => {
      const response = await api.get<AdminScoresResponse>('/admin/scores');
      return response.data;
    },

    forceBroadcast: async (): Promise<void> => {
      await api.post('/admin/scores/broadcast');
    },
  },
};

export default leaderboardApi;
