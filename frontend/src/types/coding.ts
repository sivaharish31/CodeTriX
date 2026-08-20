export type Language = 'C' | 'CPP' | 'JAVA' | 'PYTHON';

export type SubmissionStatus =
  | 'QUEUED'
  | 'RUNNING'
  | 'ACCEPTED'
  | 'PARTIAL'
  | 'WRONG_ANSWER'
  | 'COMPILE_ERROR'
  | 'RUNTIME_ERROR'
  | 'TIME_LIMIT';

export interface TestCase {
  id: number;
  input: string;
  expectedOutput: string;
  isSample: boolean;
  displayOrder: number;
  explanation?: string;
}

export interface Problem {
  id: number;
  title: string;
  description: string;
  constraints?: string;
  inputFormat?: string;
  outputFormat?: string;
  points: number;
  timeLimitMs: number;
  memoryLimitMb: number;
  difficulty: string;
  displayOrder: number;
  sampleTestCases?: TestCase[];
  totalTestCases?: number;
}

export interface ProblemListItem {
  id: number;
  title: string;
  points: number;
  difficulty: string;
  displayOrder: number;
}

export interface RunCodeRequest {
  problemId: number;
  language: string;
  sourceCode: string;
  customInput: string;
}

export interface SubmitCodeRequest {
  problemId: number;
  language: string;
  sourceCode: string;
}

export interface RunCodeResponse {
  success: boolean;
  status: string;
  output?: string;
  error?: string;
  executionTimeMs?: number;
  memoryUsedKb?: number;
  compileOutput?: string;
}

export interface Submission {
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

export interface SubmissionListResponse {
  submissions: Submission[];
  totalSubmissions: number;
  totalPointsEarned: number;
  problemsSolved: number;
}

export const LANGUAGE_CONFIG: Record<Language, { name: string; extension: string; monacoLang: string }> = {
  C: { name: 'C', extension: 'c', monacoLang: 'c' },
  CPP: { name: 'C++', extension: 'cpp', monacoLang: 'cpp' },
  JAVA: { name: 'Java', extension: 'java', monacoLang: 'java' },
  PYTHON: { name: 'Python', extension: 'py', monacoLang: 'python' },
};

export const DEFAULT_CODE: Record<Language, string> = {
  C: `#include <stdio.h>

int main() {
    // Your code here

    return 0;
}`,
  CPP: `#include <iostream>
using namespace std;

int main() {
    // Your code here

    return 0;
}`,
  JAVA: `import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        // Your code here

    }
}`,
  PYTHON: `# Your code here

`,
};

export const STATUS_COLORS: Record<SubmissionStatus, string> = {
  QUEUED: 'text-gray-500',
  RUNNING: 'text-blue-500',
  ACCEPTED: 'text-green-500',
  PARTIAL: 'text-yellow-500',
  WRONG_ANSWER: 'text-red-500',
  COMPILE_ERROR: 'text-orange-500',
  RUNTIME_ERROR: 'text-red-600',
  TIME_LIMIT: 'text-purple-500',
};
