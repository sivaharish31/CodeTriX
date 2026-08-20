import React from 'react';
import type { ViolationResponse } from '../../../types/proctoring';
import { VIOLATION_LABELS, VIOLATION_COLORS } from '../../../types/proctoring';

interface RecentViolationsProps {
  violations: ViolationResponse[];
  onViewAll: () => void;
}

export const RecentViolations: React.FC<RecentViolationsProps> = ({
  violations,
  onViewAll,
}) => {
  const formatTime = (timestamp: string): string => {
    return new Date(timestamp).toLocaleTimeString('en-US', {
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit',
    });
  };

  return (
    <div className="bg-white rounded-lg shadow overflow-hidden">
      <div className="px-4 py-3 bg-gradient-to-r from-red-500 to-red-600 flex items-center justify-between">
        <div className="flex items-center gap-2">
          <h3 className="text-lg font-semibold text-white">⚠️ Proctoring Alerts</h3>
          {violations.length > 0 && (
            <span className="px-2 py-0.5 bg-white/20 rounded-full text-sm text-white">
              {violations.length}
            </span>
          )}
        </div>
        <button
          onClick={onViewAll}
          className="text-sm text-white/80 hover:text-white"
        >
          View All →
        </button>
      </div>

      {violations.length === 0 ? (
        <div className="p-6 text-center text-gray-500">
          <div className="text-3xl mb-2">✓</div>
          No violations detected
        </div>
      ) : (
        <div className="divide-y max-h-64 overflow-y-auto">
          {violations.slice(0, 10).map((v) => (
            <div key={v.id} className="px-4 py-3 hover:bg-red-50">
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-2">
                  <span className={`px-2 py-0.5 rounded text-xs font-medium ${
                    VIOLATION_COLORS[v.violationType]
                  }`}>
                    {VIOLATION_LABELS[v.violationType]}
                  </span>
                  <span className="font-medium text-gray-800">{v.teamName}</span>
                </div>
                <span className="text-xs text-gray-500">{formatTime(v.violationTime)}</span>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default RecentViolations;
