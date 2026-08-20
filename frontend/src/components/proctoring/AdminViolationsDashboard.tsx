import React, { useState, useEffect, useCallback } from 'react';
import { proctoringApi } from '../../services/proctoringApi';
import type {
  AdminViolationsResponse,
  TeamViolationSummary,
  ViolationResponse,
  ReviewStatus,
} from '../../types/proctoring';
import {
  VIOLATION_LABELS,
  VIOLATION_COLORS,
  REVIEW_STATUS_LABELS,
  REVIEW_STATUS_COLORS,
} from '../../types/proctoring';

export const AdminViolationsDashboard: React.FC = () => {
  const [data, setData] = useState<AdminViolationsResponse | null>(null);
  const [selectedTeam, setSelectedTeam] = useState<TeamViolationSummary | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [activeTab, setActiveTab] = useState<'live' | 'review'>('live');

  const loadViolations = useCallback(async () => {
    try {
      const result = await proctoringApi.admin.getViolations(100);
      setData(result);
      setError(null);
    } catch (err) {
      setError('Failed to load violations');
    } finally {
      setLoading(false);
    }
  }, []);

  const loadTeamDetails = async (teamId: number) => {
    try {
      const details = await proctoringApi.admin.getTeamViolations(teamId);
      setSelectedTeam(details);
    } catch (err) {
      console.error('Failed to load team details:', err);
    }
  };

  const updateReviewStatus = async (teamId: number, status: ReviewStatus, notes?: string) => {
    try {
      const updated = await proctoringApi.admin.updateReviewStatus(teamId, { status, adminNotes: notes });
      setSelectedTeam(updated);
      loadViolations();
    } catch (err) {
      console.error('Failed to update status:', err);
    }
  };

  useEffect(() => {
    loadViolations();
    const interval = setInterval(loadViolations, 10000);
    return () => clearInterval(interval);
  }, [loadViolations]);

  const formatTime = (timestamp: string): string => {
    return new Date(timestamp).toLocaleTimeString('en-US', {
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit',
    });
  };

  if (loading) {
    return <div className="p-8 text-center text-gray-500">Loading violations...</div>;
  }

  if (error) {
    return <div className="p-8 text-center text-red-500">{error}</div>;
  }

  return (
    <div className="min-h-screen bg-gray-100">
      {/* Header */}
      <div className="bg-white shadow">
        <div className="max-w-7xl mx-auto px-4 py-4">
          <div className="flex items-center justify-between">
            <h1 className="text-2xl font-bold text-gray-900">Proctoring Dashboard</h1>
            <div className="flex items-center gap-4">
              <span className="text-sm text-gray-500">
                Total Violations: <span className="font-bold text-red-600">{data?.totalViolations || 0}</span>
              </span>
              <span className="text-sm text-gray-500">
                Teams: <span className="font-bold">{data?.teamsWithViolations || 0}</span>
              </span>
            </div>
          </div>

          {/* Tabs */}
          <div className="mt-4 flex gap-2">
            <button
              onClick={() => setActiveTab('live')}
              className={`px-4 py-2 rounded-lg text-sm font-medium ${
                activeTab === 'live'
                  ? 'bg-red-100 text-red-700'
                  : 'text-gray-500 hover:bg-gray-100'
              }`}
            >
              Live Feed
            </button>
            <button
              onClick={() => setActiveTab('review')}
              className={`px-4 py-2 rounded-lg text-sm font-medium ${
                activeTab === 'review'
                  ? 'bg-red-100 text-red-700'
                  : 'text-gray-500 hover:bg-gray-100'
              }`}
            >
              Top Offenders
            </button>
          </div>
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-4 py-6">
        <div className="grid grid-cols-3 gap-6">
          {/* Left: Violations List */}
          <div className="col-span-2">
            {activeTab === 'live' ? (
              <div className="bg-white rounded-lg shadow overflow-hidden">
                <div className="px-4 py-3 bg-gray-50 border-b flex items-center justify-between">
                  <h2 className="font-semibold text-gray-800">Recent Violations</h2>
                  <button
                    onClick={loadViolations}
                    className="text-sm text-gray-500 hover:text-gray-700"
                  >
                    Refresh
                  </button>
                </div>
                <div className="divide-y max-h-[600px] overflow-y-auto">
                  {data?.violations.map((v) => (
                    <div
                      key={v.id}
                      className="px-4 py-3 hover:bg-gray-50 cursor-pointer"
                      onClick={() => loadTeamDetails(v.teamId)}
                    >
                      <div className="flex items-center justify-between">
                        <div className="flex items-center gap-3">
                          <span className={`px-2 py-1 rounded text-xs font-medium ${
                            VIOLATION_COLORS[v.violationType]
                          }`}>
                            {VIOLATION_LABELS[v.violationType]}
                          </span>
                          <span className="font-medium text-gray-800">{v.teamName}</span>
                        </div>
                        <span className="text-sm text-gray-500">{formatTime(v.violationTime)}</span>
                      </div>
                      {v.details && (
                        <p className="text-xs text-gray-500 mt-1">{v.details}</p>
                      )}
                    </div>
                  ))}
                </div>
              </div>
            ) : (
              <div className="bg-white rounded-lg shadow overflow-hidden">
                <div className="px-4 py-3 bg-gray-50 border-b">
                  <h2 className="font-semibold text-gray-800">Top Offenders</h2>
                </div>
                <div className="divide-y">
                  {data?.topOffenders.map((team, index) => (
                    <div
                      key={team.teamId}
                      className="px-4 py-3 hover:bg-gray-50 cursor-pointer flex items-center justify-between"
                      onClick={() => loadTeamDetails(team.teamId)}
                    >
                      <div className="flex items-center gap-3">
                        <span className={`w-8 h-8 rounded-full flex items-center justify-center text-sm font-bold ${
                          index < 3 ? 'bg-red-100 text-red-700' : 'bg-gray-100 text-gray-600'
                        }`}>
                          {index + 1}
                        </span>
                        <span className="font-medium text-gray-800">{team.teamName}</span>
                      </div>
                      <span className="text-lg font-bold text-red-600">{team.count}</span>
                    </div>
                  ))}
                </div>
              </div>
            )}
          </div>

          {/* Right: Team Details */}
          <div className="col-span-1">
            {selectedTeam ? (
              <div className="bg-white rounded-lg shadow overflow-hidden">
                <div className="px-4 py-3 bg-red-50 border-b">
                  <h2 className="font-semibold text-gray-800">{selectedTeam.teamName}</h2>
                  <p className="text-sm text-gray-500">Team ID: {selectedTeam.teamId}</p>
                </div>

                {/* Stats */}
                <div className="p-4 border-b">
                  <div className="text-center mb-4">
                    <span className="text-4xl font-bold text-red-600">
                      {selectedTeam.totalViolations}
                    </span>
                    <p className="text-sm text-gray-500">Total Violations</p>
                  </div>

                  <div className="space-y-2">
                    {Object.entries(selectedTeam.violationsByType).map(([type, count]) => (
                      <div key={type} className="flex justify-between text-sm">
                        <span className="text-gray-600">
                          {VIOLATION_LABELS[type as keyof typeof VIOLATION_LABELS]}
                        </span>
                        <span className="font-medium">{count}</span>
                      </div>
                    ))}
                  </div>
                </div>

                {/* Review Status */}
                <div className="p-4 border-b">
                  <h3 className="text-sm font-semibold text-gray-500 mb-2">Review Status</h3>
                  <span className={`px-3 py-1 rounded-full text-sm font-medium ${
                    REVIEW_STATUS_COLORS[selectedTeam.reviewStatus]
                  }`}>
                    {REVIEW_STATUS_LABELS[selectedTeam.reviewStatus]}
                  </span>

                  <div className="mt-3 flex flex-wrap gap-2">
                    {(['UNDER_REVIEW', 'CLEARED', 'FLAGGED'] as ReviewStatus[]).map((status) => (
                      <button
                        key={status}
                        onClick={() => updateReviewStatus(selectedTeam.teamId, status)}
                        className={`px-3 py-1 rounded text-xs font-medium transition-colors ${
                          selectedTeam.reviewStatus === status
                            ? REVIEW_STATUS_COLORS[status]
                            : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
                        }`}
                      >
                        {REVIEW_STATUS_LABELS[status]}
                      </button>
                    ))}
                  </div>
                </div>

                {/* Recent Violations */}
                <div className="p-4">
                  <h3 className="text-sm font-semibold text-gray-500 mb-2">Recent Violations</h3>
                  <div className="space-y-2 max-h-48 overflow-y-auto">
                    {selectedTeam.recentViolations.slice(0, 10).map((v) => (
                      <div key={v.id} className="text-sm">
                        <span className={`px-2 py-0.5 rounded text-xs ${
                          VIOLATION_COLORS[v.violationType]
                        }`}>
                          {VIOLATION_LABELS[v.violationType]}
                        </span>
                        <span className="text-gray-500 ml-2">{formatTime(v.violationTime)}</span>
                      </div>
                    ))}
                  </div>
                </div>
              </div>
            ) : (
              <div className="bg-white rounded-lg shadow p-8 text-center text-gray-500">
                Select a team to view details
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default AdminViolationsDashboard;
