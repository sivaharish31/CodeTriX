import axios from 'axios';
import type {
  DebuggingProblem,
  DebuggingProblemListItem,
  DebuggingRunRequest,
  DebuggingRunResponse,
  DebuggingSubmitRequest,
  DebuggingSubmission,
  DebuggingSubmissionListResponse,
} from '../types/debugging';

const API_BASE = '/api';

const api = axios.create({
  baseURL: API_BASE,
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export const debuggingApi = {
  getProblems: async (): Promise<DebuggingProblemListItem[]> => {
    const response = await api.get<DebuggingProblemListItem[]>('/debugging/problems');
    return response.data;
  },

  getProblem: async (id: number): Promise<DebuggingProblem> => {
    const response = await api.get<DebuggingProblem>(`/debugging/problems/${id}`);
    return response.data;
  },

  runCode: async (request: DebuggingRunRequest): Promise<DebuggingRunResponse> => {
    const response = await api.post<DebuggingRunResponse>('/debugging/run', request);
    return response.data;
  },

  submitCode: async (request: DebuggingSubmitRequest): Promise<DebuggingSubmission> => {
    const response = await api.post<DebuggingSubmission>('/debugging/submit', request);
    return response.data;
  },

  getSubmissions: async (): Promise<DebuggingSubmissionListResponse> => {
    const response = await api.get<DebuggingSubmissionListResponse>('/debugging/submissions');
    return response.data;
  },

  getSubmission: async (id: number): Promise<DebuggingSubmission> => {
    const response = await api.get<DebuggingSubmission>(`/debugging/submissions/${id}`);
    return response.data;
  },
};

export default debuggingApi;
