import React from 'react';
import type { TeamScoreResponse } from '../../types/leaderboard';

interface TeamScoreCardProps {
  score: TeamScoreResponse;
}

export const TeamScoreCard: React.FC<TeamScoreCardProps> = ({ score }) => {
  const getRankDisplay = () => {
    if (score.rank === 1) return { icon: '🥇', color: 'text-yellow-500' };
    if (score.rank === 2) return { icon: '🥈', color: 'text-gray-500' };
    if (score.rank === 3) return { icon: '🥉', color: 'text-orange-500' };
    return { icon: `#${score.rank}`, color: 'text-indigo-600' };
  };

  const rankDisplay = getRankDisplay();

  return (
    <div className="bg-white rounded-xl shadow-lg overflow-hidden">
      {/* Header */}
      <div className="bg-gradient-to-r from-indigo-500 to-purple-600 px-6 py-4">
        <div className="flex items-center justify-between">
          <div>
            <h2 className="text-xl font-bold text-white">{score.teamName}</h2>
            <p className="text-indigo-200 text-sm">Your Team Score</p>
          </div>
          <div className="text-center">
            <div className={`text-4xl ${typeof rankDisplay.icon === 'string' && rankDisplay.icon.startsWith('#') ? 'font-bold text-white' : ''}`}>
              {rankDisplay.icon}
            </div>
            <p className="text-indigo-200 text-xs">of {score.totalTeams} teams</p>
          </div>
        </div>
      </div>

      {/* Total Score */}
      <div className="px-6 py-4 bg-gradient-to-r from-purple-50 to-indigo-50 border-b">
        <div className="text-center">
          <span className="text-5xl font-bold text-purple-700">{score.totalScore}</span>
          <span className="text-lg text-gray-500 ml-2">points</span>
        </div>
      </div>

      {/* Score Breakdown */}
      <div className="px-6 py-4">
        <h3 className="text-sm font-semibold text-gray-500 uppercase tracking-wider mb-3">
          Score Breakdown
        </h3>
        <div className="space-y-3">
          {/* Coding */}
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <div className="w-3 h-3 rounded-full bg-blue-500"></div>
              <span className="text-gray-700">Coding Round</span>
            </div>
            <div className="text-right">
              <span className="text-lg font-semibold text-blue-600">{score.codingScore}</span>
              <span className="text-xs text-gray-400 ml-2">
                ({score.codingProblemsSolved} solved)
              </span>
            </div>
          </div>

          {/* Debugging */}
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <div className="w-3 h-3 rounded-full bg-orange-500"></div>
              <span className="text-gray-700">Debugging Round</span>
            </div>
            <div className="text-right">
              <span className="text-lg font-semibold text-orange-600">{score.debuggingScore}</span>
              <span className="text-xs text-gray-400 ml-2">
                ({score.debuggingProblemsSolved} solved)
              </span>
            </div>
          </div>

          {/* CTF */}
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <div className="w-3 h-3 rounded-full bg-teal-500"></div>
              <span className="text-gray-700">CTF Round</span>
            </div>
            <div className="text-right">
              <span className="text-lg font-semibold text-teal-600">{score.ctfScore}</span>
              <span className="text-xs text-gray-400 ml-2">
                ({score.ctfChallengesSolved} captured)
              </span>
            </div>
          </div>
        </div>
      </div>

      {/* Footer */}
      {score.lastSubmissionTime && (
        <div className="px-6 py-3 bg-gray-50 text-center text-xs text-gray-500">
          Last submission: {new Date(score.lastSubmissionTime).toLocaleString()}
        </div>
      )}
    </div>
  );
};

export default TeamScoreCard;
