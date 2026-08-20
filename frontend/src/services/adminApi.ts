import axios from 'axios';
import type {
  EventStatus,
  TeamInfo,
  TeamDetail,
  SubmissionSummary,
  ProblemSummary,
  CtfChallengeSummary,
  DashboardStats,
} from '../types/admin';

const API_BASE = '/api';

const api = axios.create({
  baseURL: API_BASE,
  headers: { 'Content-Type': 'application/json' },
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export const adminApi = {
  // Event Management
  getEventStatus: async (): Promise<EventStatus> => {
    const response = await api.get<EventStatus>('/event/status');
    return response.data;
  },

  startEvent: async (): Promise<EventStatus> => {
    const response = await api.post<EventStatus>('/admin/event/start');
    return response.data;
  },

  // Teams
  getTeams: async (): Promise<TeamInfo[]> => {
    const response = await api.get<TeamInfo[]>('/admin/teams');
    return response.data;
  },

  getTeamDetail: async (teamId: number): Promise<TeamDetail> => {
    const response = await api.get<TeamDetail>(`/admin/teams/${teamId}`);
    return response.data;
  },

  deleteTeam: async (teamId: number): Promise<void> => {
    await api.delete(`/admin/teams/${teamId}`);
  },

  // Coding Problems
  getCodingProblems: async (): Promise<ProblemSummary[]> => {
    const response = await api.get<ProblemSummary[]>('/admin/coding/problems');
    return response.data;
  },

  createCodingProblem: async (data: any): Promise<any> => {
    const response = await api.post('/admin/coding/problems', data);
    return response.data;
  },

  updateCodingProblem: async (id: number, data: any): Promise<any> => {
    const response = await api.put(`/admin/coding/problems/${id}`, data);
    return response.data;
  },

  deleteCodingProblem: async (id: number): Promise<void> => {
    await api.delete(`/admin/coding/problems/${id}`);
  },

  // Debugging Problems
  getDebuggingProblems: async (): Promise<ProblemSummary[]> => {
    const response = await api.get<ProblemSummary[]>('/admin/debugging/problems');
    return response.data;
  },

  createDebuggingProblem: async (data: any): Promise<any> => {
    const response = await api.post('/admin/debugging/problems', data);
    return response.data;
  },

  updateDebuggingProblem: async (id: number, data: any): Promise<any> => {
    const response = await api.put(`/admin/debugging/problems/${id}`, data);
    return response.data;
  },

  deleteDebuggingProblem: async (id: number): Promise<void> => {
    await api.delete(`/admin/debugging/problems/${id}`);
  },

  // CTF Challenges
  getCtfChallenges: async (): Promise<CtfChallengeSummary[]> => {
    const response = await api.get<CtfChallengeSummary[]>('/admin/ctf/challenges');
    return response.data;
  },

  // Submissions
  getCodingSubmissions: async (limit?: number): Promise<SubmissionSummary[]> => {
    const response = await api.get<any>(`/admin/coding/submissions${limit ? `?limit=${limit}` : ''}`);
    return response.data.submissions || response.data;
  },

  getDebuggingSubmissions: async (limit?: number): Promise<SubmissionSummary[]> => {
    const response = await api.get<any>(`/admin/debugging/submissions${limit ? `?limit=${limit}` : ''}`);
    return response.data.submissions || response.data;
  },

  // Dashboard Stats
  getDashboardStats: async (): Promise<DashboardStats> => {
    try {
      const [teams, codingSubs, debuggingSubs, violations] = await Promise.all([
        api.get('/admin/teams'),
        api.get('/admin/coding/submissions?limit=1000').catch(() => ({ data: { submissions: [] } })),
        api.get('/admin/debugging/submissions?limit=1000').catch(() => ({ data: { submissions: [] } })),
        api.get('/admin/proctoring/violations?limit=1').catch(() => ({ data: { totalViolations: 0, teamsWithViolations: 0 } })),
      ]);

      const teamList = teams.data as TeamInfo[];
      const codingList = codingSubs.data.submissions || [];
      const debuggingList = debuggingSubs.data.submissions || [];
      const violationData = violations.data;

      return {
        teamsRegistered: teamList.length,
        maxTeams: 27,
        activeTeams: teamList.filter((t: TeamInfo) => t.status === 'ACTIVE').length,
        totalSubmissions: codingList.length + debuggingList.length,
        codingSubmissions: codingList.length,
        debuggingSubmissions: debuggingList.length,
        ctfSubmissions: 0,
        totalViolations: violationData.totalViolations || 0,
        teamsWithViolations: violationData.teamsWithViolations || 0,
      };
    } catch (error) {
      console.error('Failed to get dashboard stats:', error);
      return {
        teamsRegistered: 0,
        maxTeams: 27,
        activeTeams: 0,
        totalSubmissions: 0,
        codingSubmissions: 0,
        debuggingSubmissions: 0,
        ctfSubmissions: 0,
        totalViolations: 0,
        teamsWithViolations: 0,
      };
    }
  },
};

export default adminApi;
