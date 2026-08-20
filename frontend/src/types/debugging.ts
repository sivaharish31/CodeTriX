import type { Language, SubmissionStatus } from './coding';

export interface DebuggingTestCase {
  id: number;
  input: string;
  expectedOutput: string;
  isSample: boolean;
  displayOrder: number;
  explanation?: string;
}

export interface DebuggingProblem {
  id: number;
  title: string;
  description: string;
  buggyCode: string;
  language: Language;
  points: number;
  timeLimitMs: number;
  memoryLimitMb: number;
  hint?: string;
  displayOrder: number;
  sampleTestCases?: DebuggingTestCase[];
  totalTestCases?: number;
}

export interface DebuggingProblemListItem {
  id: number;
  title: string;
  language: Language;
  points: number;
  displayOrder: number;
}

export interface DebuggingRunRequest {
  problemId: number;
  sourceCode: string;
  customInput: string;
}

export interface DebuggingSubmitRequest {
  problemId: number;
  sourceCode: string;
}

export interface DebuggingRunResponse {
  success: boolean;
  status: string;
  output?: string;
  error?: string;
  executionTimeMs?: number;
  memoryUsedKb?: number;
  compileOutput?: string;
}

export interface DebuggingSubmission {
  id: number;
  problemId: number;
  problemTitle?: string;
  language: Language;
  status: SubmissionStatus;
  testsPassed: number;
  totalTests: number;
  pointsEarned: number;
  maxPoints?: number;
  executionTimeMs?: number;
  memoryUsedKb?: number;
  compileOutput?: string;
  errorMessage?: string;
  submissionTime: string;
}

export interface DebuggingSubmissionListResponse {
  submissions: DebuggingSubmission[];
  totalSubmissions: number;
  totalPointsEarned: number;
  problemsSolved: number;
}
