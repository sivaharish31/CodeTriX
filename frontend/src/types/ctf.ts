export type CtfCategory = 'WEB' | 'CRYPTOGRAPHY' | 'FORENSICS' | 'ENCODING' | 'STEGANOGRAPHY';

export type CtfDifficulty = 'EASY' | 'MEDIUM' | 'HARD' | 'EXPERT';

export interface CtfChallenge {
  id: number;
  title: string;
  description: string;
  category: CtfCategory;
  difficulty: CtfDifficulty;
  points: number;
  hasAttachment: boolean;
  attachmentFilename?: string;
  solved: boolean;
}

export interface CtfChallengeAdmin extends CtfChallenge {
  flag: string;
  active: boolean;
  attachmentContentType?: string;
  createdAt: string;
  updatedAt: string;
}

export interface CtfSubmitRequest {
  challengeId: number;
  flag: string;
}

export interface CtfSubmitResponse {
  submissionId?: number;
  challengeId: number;
  challengeTitle: string;
  correct: boolean;
  pointsAwarded: number;
  message: string;
}

export interface CtfSubmission {
  id: number;
  challengeId: number;
  challengeTitle: string;
  challengeCategory: CtfCategory;
  challengePoints: number;
  correct: boolean;
  pointsAwarded: number;
  submissionTime: string;
}

export interface CtfSubmissionListResponse {
  submissions: CtfSubmission[];
  totalPoints: number;
  challengesSolved: number;
  totalChallenges: number;
}

export interface CreateCtfChallengeRequest {
  title: string;
  description: string;
  category: CtfCategory;
  difficulty: CtfDifficulty;
  points: number;
  flag: string;
  active?: boolean;
}

export interface UpdateCtfChallengeRequest {
  title?: string;
  description?: string;
  category?: CtfCategory;
  difficulty?: CtfDifficulty;
  points?: number;
  flag?: string;
  active?: boolean;
}

export const CATEGORY_LABELS: Record<CtfCategory, string> = {
  WEB: 'Web',
  CRYPTOGRAPHY: 'Cryptography',
  FORENSICS: 'Forensics',
  ENCODING: 'Encoding',
  STEGANOGRAPHY: 'Steganography',
};

export const CATEGORY_COLORS: Record<CtfCategory, string> = {
  WEB: 'bg-blue-100 text-blue-800',
  CRYPTOGRAPHY: 'bg-purple-100 text-purple-800',
  FORENSICS: 'bg-amber-100 text-amber-800',
  ENCODING: 'bg-green-100 text-green-800',
  STEGANOGRAPHY: 'bg-pink-100 text-pink-800',
};

export const DIFFICULTY_LABELS: Record<CtfDifficulty, string> = {
  EASY: 'Easy',
  MEDIUM: 'Medium',
  HARD: 'Hard',
  EXPERT: 'Expert',
};

export const DIFFICULTY_COLORS: Record<CtfDifficulty, string> = {
  EASY: 'text-green-600',
  MEDIUM: 'text-yellow-600',
  HARD: 'text-orange-600',
  EXPERT: 'text-red-600',
};
