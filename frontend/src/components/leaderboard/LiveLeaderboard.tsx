import React, { useState, useEffect, useCallback } from 'react';
import { LeaderboardTable } from './LeaderboardTable';
import { TeamScoreCard } from './TeamScoreCard';
import { leaderboardApi } from '../../services/leaderboardApi';
import { useLeaderboardWebSocket } from '../../hooks/useLeaderboardWebSocket';
import type { LeaderboardResponse, TeamScoreResponse } from '../../types/leaderboard';

interface LiveLeaderboardProps {
  teamId?: number;
  showMyScore?: boolean;
  autoRefresh?: boolean;
  refreshInterval?: number;
}

export const LiveLeaderboard: React.FC<LiveLeaderboardProps> = ({
  teamId,
  showMyScore = false,
  autoRefresh = true,
  refreshInterval = 30000,
}) => {
  const [leaderboard, setLeaderboard] = useState<LeaderboardResponse | null>(null);
  const [myScore, setMyScore] = useState<TeamScoreResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const handleWebSocketUpdate = useCallback((data: LeaderboardResponse) => {
    setLeaderboard(data);
  }, []);

  const { connected, requestRefresh } = useLeaderboardWebSocket({
    onUpdate: handleWebSocketUpdate,
    autoConnect: true,
  });

  const loadLeaderboard = async () => {
    try {
      const data = await leaderboardApi.getLeaderboard();
      setLeaderboard(data);
      setError(null);
    } catch (err) {
      setError('Failed to load leaderboard');
      console.error(err);
    }
  };

  const loadMyScore = async () => {
    if (!showMyScore) return;
    try {
      const data = await leaderboardApi.getMyScore();
      setMyScore(data);
    } catch (err) {
      console.error('Failed to load my score:', err);
    }
  };

  useEffect(() => {
    const loadData = async () => {
      setLoading(true);
      await Promise.all([loadLeaderboard(), loadMyScore()]);
      setLoading(false);
    };
    loadData();
  }, [showMyScore]);

  useEffect(() => {
    if (!autoRefresh || connected) return;

    const interval = setInterval(loadLeaderboard, refreshInterval);
    return () => clearInterval(interval);
  }, [autoRefresh, connected, refreshInterval]);

  if (loading) {
    return (
      <div className="flex items-center justify-center py-12">
        <div className="text-gray-500">Loading leaderboard...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="bg-red-50 text-red-700 px-4 py-3 rounded-lg text-center">
        {error}
        <button
          onClick={() => loadLeaderboard()}
          className="ml-4 text-red-600 underline hover:text-red-800"
        >
          Retry
        </button>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Connection Status */}
      <div className="flex items-center justify-between">
        <h2 className="text-2xl font-bold text-gray-800">Live Leaderboard</h2>
        <div className="flex items-center gap-4">
          <div className={`flex items-center gap-2 text-sm ${connected ? 'text-green-600' : 'text-gray-500'}`}>
            <span className={`w-2 h-2 rounded-full ${connected ? 'bg-green-500 animate-pulse' : 'bg-gray-400'}`}></span>
            {connected ? 'Live' : 'Offline'}
          </div>
          <button
            onClick={() => connected ? requestRefresh() : loadLeaderboard()}
            className="px-3 py-1 text-sm bg-indigo-100 text-indigo-700 rounded hover:bg-indigo-200 transition-colors"
          >
            Refresh
          </button>
        </div>
      </div>

      {/* My Score Card */}
      {showMyScore && myScore && (
        <div className="max-w-md">
          <TeamScoreCard score={myScore} />
        </div>
      )}

      {/* Leaderboard Table */}
      {leaderboard && (
        <div className="bg-white rounded-lg shadow overflow-hidden">
          <div className="px-4 py-3 bg-gray-50 border-b border-gray-200 flex items-center justify-between">
            <div className="text-sm text-gray-500">
              {leaderboard.totalTeams} teams participating
            </div>
            <div className="text-xs text-gray-400">
              Updated: {new Date(leaderboard.generatedAt).toLocaleTimeString()}
            </div>
          </div>
          <LeaderboardTable
            entries={leaderboard.entries}
            highlightTeamId={teamId}
            showDetails={true}
          />
        </div>
      )}
    </div>
  );
};

export default LiveLeaderboard;
