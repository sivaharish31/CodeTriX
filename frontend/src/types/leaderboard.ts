export interface LeaderboardEntry {
  rank: number;
  teamId: number;
  teamName: string;
  codingScore: number;
  debuggingScore: number;
  ctfScore: number;
  totalScore: number;
  codingProblemsSolved: number;
  debuggingProblemsSolved: number;
  ctfChallengesSolved: number;
  lastSubmissionTime?: string;
}

export interface LeaderboardResponse {
  entries: LeaderboardEntry[];
  totalTeams: number;
  generatedAt: string;
  eventStatus?: string;
}

export interface TeamScoreResponse {
  teamId: number;
  teamName: string;
  rank: number;
  totalTeams: number;
  codingScore: number;
  debuggingScore: number;
  ctfScore: number;
  totalScore: number;
  codingProblemsSolved: number;
  debuggingProblemsSolved: number;
  ctfChallengesSolved: number;
  lastSubmissionTime?: string;
}

export interface ScoreRecord {
  id: number;
  teamId: number;
  teamName: string;
  scoreType: 'CODING' | 'DEBUGGING' | 'CTF';
  problemId: number;
  submissionId?: number;
  pointsEarned: number;
  maxPoints: number;
  testsPassed?: number;
  totalTests?: number;
  createdAt: string;
}

export interface ScoreSummary {
  totalTeams: number;
  teamsWithScore: number;
  totalCodingPoints: number;
  totalDebuggingPoints: number;
  totalCtfPoints: number;
  totalPoints: number;
}

export interface AdminScoresResponse {
  teamScores: LeaderboardEntry[];
  recentRecords: ScoreRecord[];
  summary: ScoreSummary;
}
