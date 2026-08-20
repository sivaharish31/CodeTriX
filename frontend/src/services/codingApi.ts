import axios from 'axios';
import type {
  Problem,
  ProblemListItem,
  RunCodeRequest,
  RunCodeResponse,
  SubmitCodeRequest,
  Submission,
  SubmissionListResponse,
} from '../types/coding';

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

export const codingApi = {
  getProblems: async (): Promise<ProblemListItem[]> => {
    const response = await api.get<ProblemListItem[]>('/coding/problems');
    return response.data;
  },

  getProblem: async (id: number): Promise<Problem> => {
    const response = await api.get<Problem>(`/coding/problems/${id}`);
    return response.data;
  },

  runCode: async (request: RunCodeRequest): Promise<RunCodeResponse> => {
    const response = await api.post<RunCodeResponse>('/coding/run', request);
    return response.data;
  },

  submitCode: async (request: SubmitCodeRequest): Promise<Submission> => {
    const response = await api.post<Submission>('/coding/submit', request);
    return response.data;
  },

  getSubmissions: async (): Promise<SubmissionListResponse> => {
    const response = await api.get<SubmissionListResponse>('/coding/submissions');
    return response.data;
  },

  getSubmission: async (id: number): Promise<Submission> => {
    const response = await api.get<Submission>(`/coding/submissions/${id}`);
    return response.data;
  },
};

export default codingApi;
