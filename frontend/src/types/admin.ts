export interface EventStatus {
  eventId: number;
  eventName: string;
  status: 'NOT_STARTED' | 'IN_PROGRESS' | 'COMPLETED';
  currentRound: RoundInfo | null;
  totalDurationSeconds: number;
  elapsedSeconds: number;
  remainingSeconds: number;
  startTime?: string;
  endTime?: string;
}

export interface RoundInfo {
  roundNumber: number;
  roundType: 'CODING' | 'DEBUGGING' | 'CTF';
  status: 'LOCKED' | 'ACTIVE' | 'COMPLETED';
  durationSeconds: number;
  remainingSeconds: number;
  startTime?: string;
  endTime?: string;
}

export interface DashboardStats {
  teamsRegistered: number;
  maxTeams: number;
  activeTeams: number;
  totalSubmissions: number;
  codingSubmissions: number;
  debuggingSubmissions: number;
  ctfSubmissions: number;
  totalViolations: number;
  teamsWithViolations: number;
}

export interface TeamInfo {
  id: number;
  teamCode: string;
  teamName: string;
  status: 'REGISTERED' | 'ACTIVE' | 'DISQUALIFIED';
  memberCount: number;
  createdAt: string;
}

export interface TeamMember {
  id: number;
  name: string;
  rollNumber: string;
  college: string;
  email?: string;
}

export interface TeamDetail extends TeamInfo {
  members: TeamMember[];
  codingScore: number;
  debuggingScore: number;
  ctfScore: number;
  totalScore: number;
  violationCount: number;
}

export interface SubmissionSummary {
  id: number;
  teamId: number;
  teamName: string;
  problemId: number;
  problemTitle: string;
  type: 'CODING' | 'DEBUGGING';
  language: string;
  status: string;
  testsPassed: number;
  totalTests: number;
  submissionTime: string;
}

export interface ProblemSummary {
  id: number;
  title: string;
  points: number;
  difficulty?: string;
  enabled: boolean;
  submissionCount?: number;
  solvedCount?: number;
}

export interface CtfChallengeSummary {
  id: number;
  title: string;
  category: string;
  difficulty: string;
  points: number;
  active: boolean;
  solvedCount?: number;
}

export type AdminTab =
  | 'overview'
  | 'teams'
  | 'coding'
  | 'debugging'
  | 'ctf'
  | 'submissions'
  | 'leaderboard'
  | 'proctoring';
