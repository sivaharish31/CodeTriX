import React from 'react';
import type { LeaderboardResponse } from '../../../types/leaderboard';

interface QuickLeaderboardProps {
  leaderboard: LeaderboardResponse | null;
  onViewFull: () => void;
}

export const QuickLeaderboard: React.FC<QuickLeaderboardProps> = ({
  leaderboard,
  onViewFull,
}) => {
  if (!leaderboard || leaderboard.entries.length === 0) {
    return (
      <div className="bg-white rounded-lg shadow p-6">
        <h3 className="text-lg font-semibold text-gray-800 mb-4">Leaderboard</h3>
        <div className="text-center text-gray-500 py-8">
          No scores yet. Waiting for submissions...
        </div>
      </div>
    );
  }

  const top10 = leaderboard.entries.slice(0, 10);

  return (
    <div className="bg-white rounded-lg shadow overflow-hidden">
      <div className="px-4 py-3 bg-gradient-to-r from-purple-500 to-indigo-500 flex items-center justify-between">
        <h3 className="text-lg font-semibold text-white">🏆 Leaderboard</h3>
        <button
          onClick={onViewFull}
          className="text-sm text-white/80 hover:text-white"
        >
          View Full →
        </button>
      </div>

      <div className="divide-y">
        {top10.map((entry) => (
          <div
            key={entry.teamId}
            className={`px-4 py-3 flex items-center justify-between hover:bg-gray-50 ${
              entry.rank <= 3 ? 'bg-yellow-50/50' : ''
            }`}
          >
            <div className="flex items-center gap-3">
              <span className={`w-8 h-8 flex items-center justify-center rounded-full text-sm font-bold ${
                entry.rank === 1 ? 'bg-yellow-400 text-yellow-900' :
                entry.rank === 2 ? 'bg-gray-300 text-gray-700' :
                entry.rank === 3 ? 'bg-orange-300 text-orange-800' :
                'bg-gray-100 text-gray-600'
              }`}>
                {entry.rank <= 3 ? ['🥇', '🥈', '🥉'][entry.rank - 1] : entry.rank}
              </span>
              <span className="font-medium text-gray-800">{entry.teamName}</span>
            </div>
            <div className="flex items-center gap-4 text-sm">
              <span className="text-blue-600">{entry.codingScore}</span>
              <span className="text-orange-600">{entry.debuggingScore}</span>
              <span className="text-teal-600">{entry.ctfScore}</span>
              <span className="font-bold text-purple-700 w-12 text-right">{entry.totalScore}</span>
            </div>
          </div>
        ))}
      </div>

      <div className="px-4 py-2 bg-gray-50 text-xs text-gray-500 text-center">
        Updated: {new Date(leaderboard.generatedAt).toLocaleTimeString()}
      </div>
    </div>
  );
};

export default QuickLeaderboard;
