import React from 'react';
import type { LeaderboardEntry } from '../../types/leaderboard';

interface LeaderboardTableProps {
  entries: LeaderboardEntry[];
  highlightTeamId?: number;
  showDetails?: boolean;
}

const formatTime = (timestamp?: string): string => {
  if (!timestamp) return '-';
  const date = new Date(timestamp);
  return date.toLocaleTimeString('en-US', {
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
  });
};

const getRankStyle = (rank: number): string => {
  switch (rank) {
    case 1:
      return 'bg-yellow-100 text-yellow-800 font-bold';
    case 2:
      return 'bg-gray-100 text-gray-800 font-bold';
    case 3:
      return 'bg-orange-100 text-orange-800 font-bold';
    default:
      return 'text-gray-600';
  }
};

const getRankIcon = (rank: number): string => {
  switch (rank) {
    case 1:
      return '🥇';
    case 2:
      return '🥈';
    case 3:
      return '🥉';
    default:
      return '';
  }
};

export const LeaderboardTable: React.FC<LeaderboardTableProps> = ({
  entries,
  highlightTeamId,
  showDetails = true,
}) => {
  return (
    <div className="overflow-x-auto">
      <table className="min-w-full divide-y divide-gray-200">
        <thead className="bg-gray-50">
          <tr>
            <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider w-16">
              Rank
            </th>
            <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              Team
            </th>
            <th className="px-4 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">
              <span className="text-blue-600">Coding</span>
            </th>
            <th className="px-4 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">
              <span className="text-orange-600">Debugging</span>
            </th>
            <th className="px-4 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">
              <span className="text-teal-600">CTF</span>
            </th>
            <th className="px-4 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">
              <span className="text-purple-600 font-bold">Total</span>
            </th>
            {showDetails && (
              <th className="px-4 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">
                Last Submit
              </th>
            )}
          </tr>
        </thead>
        <tbody className="bg-white divide-y divide-gray-200">
          {entries.map((entry) => (
            <tr
              key={entry.teamId}
              className={`transition-colors ${
                highlightTeamId === entry.teamId
                  ? 'bg-indigo-50 border-l-4 border-indigo-500'
                  : 'hover:bg-gray-50'
              }`}
            >
              <td className="px-4 py-3 whitespace-nowrap">
                <span className={`inline-flex items-center justify-center w-8 h-8 rounded-full ${getRankStyle(entry.rank)}`}>
                  {getRankIcon(entry.rank) || entry.rank}
                </span>
              </td>
              <td className="px-4 py-3 whitespace-nowrap">
                <div className="flex items-center">
                  <div>
                    <div className={`text-sm font-medium ${
                      highlightTeamId === entry.teamId ? 'text-indigo-900' : 'text-gray-900'
                    }`}>
                      {entry.teamName}
                    </div>
                    {showDetails && (
                      <div className="text-xs text-gray-500">
                        Solved: {entry.codingProblemsSolved + entry.debuggingProblemsSolved + entry.ctfChallengesSolved}
                      </div>
                    )}
                  </div>
                </div>
              </td>
              <td className="px-4 py-3 whitespace-nowrap text-center">
                <span className="text-sm font-medium text-blue-600">{entry.codingScore}</span>
                {showDetails && (
                  <span className="text-xs text-gray-400 ml-1">({entry.codingProblemsSolved})</span>
                )}
              </td>
              <td className="px-4 py-3 whitespace-nowrap text-center">
                <span className="text-sm font-medium text-orange-600">{entry.debuggingScore}</span>
                {showDetails && (
                  <span className="text-xs text-gray-400 ml-1">({entry.debuggingProblemsSolved})</span>
                )}
              </td>
              <td className="px-4 py-3 whitespace-nowrap text-center">
                <span className="text-sm font-medium text-teal-600">{entry.ctfScore}</span>
                {showDetails && (
                  <span className="text-xs text-gray-400 ml-1">({entry.ctfChallengesSolved})</span>
                )}
              </td>
              <td className="px-4 py-3 whitespace-nowrap text-center">
                <span className="text-lg font-bold text-purple-700">{entry.totalScore}</span>
              </td>
              {showDetails && (
                <td className="px-4 py-3 whitespace-nowrap text-center text-xs text-gray-500">
                  {formatTime(entry.lastSubmissionTime)}
                </td>
              )}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default LeaderboardTable;
