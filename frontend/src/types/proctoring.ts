export type ViolationType =
  | 'TAB_SWITCH'
  | 'FULLSCREEN_EXIT'
  | 'COPY'
  | 'PASTE'
  | 'CUT'
  | 'KEYBOARD_SHORTCUT'
  | 'VISIBILITY_HIDDEN';

export type ReviewStatus = 'PENDING' | 'UNDER_REVIEW' | 'CLEARED' | 'FLAGGED';

export interface ViolationRequest {
  violationType: ViolationType;
  roundId?: number;
  clientTimestamp?: number;
  details?: string;
}

export interface ViolationResponse {
  id: number;
  teamId: number;
  teamName: string;
  roundId: number;
  violationType: ViolationType;
  violationTime: string;
  details?: string;
  createdAt: string;
}

export interface TeamViolationSummary {
  teamId: number;
  teamName: string;
  totalViolations: number;
  violationsByType: Record<ViolationType, number>;
  recentViolations: ViolationResponse[];
  reviewStatus: ReviewStatus;
  adminNotes?: string;
  lastViolationTime?: string;
}

export interface AdminViolationsResponse {
  violations: ViolationResponse[];
  totalViolations: number;
  teamsWithViolations: number;
  violationsByType: Record<string, number>;
  topOffenders: TeamViolationCount[];
}

export interface TeamViolationCount {
  teamId: number;
  teamName: string;
  count: number;
}

export interface UpdateReviewStatusRequest {
  status: ReviewStatus;
  adminNotes?: string;
}

export const VIOLATION_LABELS: Record<ViolationType, string> = {
  TAB_SWITCH: 'Tab Switch',
  FULLSCREEN_EXIT: 'Fullscreen Exit',
  COPY: 'Copy Attempt',
  PASTE: 'Paste Attempt',
  CUT: 'Cut Attempt',
  KEYBOARD_SHORTCUT: 'Keyboard Shortcut',
  VISIBILITY_HIDDEN: 'Window Hidden',
};

export const VIOLATION_COLORS: Record<ViolationType, string> = {
  TAB_SWITCH: 'bg-red-100 text-red-800',
  FULLSCREEN_EXIT: 'bg-orange-100 text-orange-800',
  COPY: 'bg-yellow-100 text-yellow-800',
  PASTE: 'bg-yellow-100 text-yellow-800',
  CUT: 'bg-yellow-100 text-yellow-800',
  KEYBOARD_SHORTCUT: 'bg-purple-100 text-purple-800',
  VISIBILITY_HIDDEN: 'bg-red-100 text-red-800',
};

export const REVIEW_STATUS_LABELS: Record<ReviewStatus, string> = {
  PENDING: 'Pending Review',
  UNDER_REVIEW: 'Under Review',
  CLEARED: 'Cleared',
  FLAGGED: 'Flagged',
};

export const REVIEW_STATUS_COLORS: Record<ReviewStatus, string> = {
  PENDING: 'bg-gray-100 text-gray-800',
  UNDER_REVIEW: 'bg-blue-100 text-blue-800',
  CLEARED: 'bg-green-100 text-green-800',
  FLAGGED: 'bg-red-100 text-red-800',
};
