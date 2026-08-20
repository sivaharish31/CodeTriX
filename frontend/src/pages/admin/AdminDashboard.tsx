import React, { useState, useEffect, useCallback } from 'react';
import { EventControl } from '../../components/admin/dashboard/EventControl';
import { StatsCards } from '../../components/admin/dashboard/StatsCards';
import { QuickLeaderboard } from '../../components/admin/dashboard/QuickLeaderboard';
import { RecentViolations } from '../../components/admin/dashboard/RecentViolations';
import { TeamList } from '../../components/admin/teams/TeamList';
import { ProblemManager } from '../../components/admin/problems/ProblemManager';
import { SubmissionMonitor } from '../../components/admin/submissions/SubmissionMonitor';
import { LiveLeaderboard } from '../../components/leaderboard/LiveLeaderboard';
import { AdminViolationsDashboard } from '../../components/proctoring/AdminViolationsDashboard';
import { adminApi } from '../../services/adminApi';
import { useAdminWebSocket } from '../../hooks/useAdminWebSocket';
import type { AdminTab, EventStatus, DashboardStats } from '../../types/admin';
import type { LeaderboardResponse } from '../../types/leaderboard';

const TABS: { key: AdminTab; label: string; icon: string }[] = [
  { key: 'overview', label: 'Overview', icon: '📊' },
  { key: 'teams', label: 'Teams', icon: '👥' },
  { key: 'coding', label: 'Coding', icon: '💻' },
  { key: 'debugging', label: 'Debugging', icon: '🐛' },
  { key: 'ctf', label: 'CTF', icon: '🚩' },
  { key: 'submissions', label: 'Submissions', icon: '📝' },
  { key: 'leaderboard', label: 'Leaderboard', icon: '🏆' },
  { key: 'proctoring', label: 'Proctoring', icon: '👁️' },
];

export const AdminDashboard: React.FC = () => {
  const [activeTab, setActiveTab] = useState<AdminTab>('overview');
  const [eventStatus, setEventStatus] = useState<EventStatus | null>(null);
  const [stats, setStats] = useState<DashboardStats | null>(null);
  const [leaderboard, setLeaderboard] = useState<LeaderboardResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [startingEvent, setStartingEvent] = useState(false);

  const handleEventUpdate = useCallback((status: EventStatus) => {
    setEventStatus(status);
  }, []);

  const handleLeaderboardUpdate = useCallback((data: LeaderboardResponse) => {
    setLeaderboard(data);
  }, []);

  const {
    connected,
    recentViolations,
    eventStatus: wsEventStatus,
    leaderboard: wsLeaderboard,
  } = useAdminWebSocket({
    onEventUpdate: handleEventUpdate,
    onLeaderboardUpdate: handleLeaderboardUpdate,
  });

  useEffect(() => {
    if (wsEventStatus) setEventStatus(wsEventStatus);
  }, [wsEventStatus]);

  useEffect(() => {
    if (wsLeaderboard) setLeaderboard(wsLeaderboard);
  }, [wsLeaderboard]);

  useEffect(() => {
    loadInitialData();
  }, []);

  const loadInitialData = async () => {
    try {
      setLoading(true);
      const [eventData, statsData] = await Promise.all([
        adminApi.getEventStatus(),
        adminApi.getDashboardStats(),
      ]);
      setEventStatus(eventData);
      setStats(statsData);
    } catch (err) {
      console.error('Failed to load initial data:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleStartEvent = async () => {
    try {
      setStartingEvent(true);
      const status = await adminApi.startEvent();
      setEventStatus(status);
    } catch (err) {
      alert('Failed to start event');
    } finally {
      setStartingEvent(false);
    }
  };

  const refreshStats = async () => {
    const statsData = await adminApi.getDashboardStats();
    setStats(statsData);
  };

  useEffect(() => {
    if (activeTab === 'overview') {
      refreshStats();
      const interval = setInterval(refreshStats, 10000);
      return () => clearInterval(interval);
    }
  }, [activeTab]);

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-100 flex items-center justify-center">
        <div className="text-gray-500">Loading dashboard...</div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-100">
      {/* Header */}
      <header className="bg-white shadow-sm border-b">
        <div className="max-w-7xl mx-auto px-4 py-3">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-4">
              <h1 className="text-xl font-bold text-gray-900">CodeTriX Admin</h1>
              <div className={`flex items-center gap-2 text-sm ${
                connected ? 'text-green-600' : 'text-gray-500'
              }`}>
                <span className={`w-2 h-2 rounded-full ${
                  connected ? 'bg-green-500 animate-pulse' : 'bg-gray-400'
                }`}></span>
                {connected ? 'Live' : 'Offline'}
              </div>
            </div>

            {/* Event Status Badge */}
            {eventStatus && (
              <div className="flex items-center gap-4">
                {eventStatus.status === 'IN_PROGRESS' && eventStatus.currentRound && (
                  <>
                    <span className={`px-3 py-1 rounded-full text-sm font-medium ${
                      eventStatus.currentRound.roundType === 'CODING' ? 'bg-blue-100 text-blue-800' :
                      eventStatus.currentRound.roundType === 'DEBUGGING' ? 'bg-orange-100 text-orange-800' :
                      'bg-teal-100 text-teal-800'
                    }`}>
                      Round {eventStatus.currentRound.roundNumber}: {eventStatus.currentRound.roundType}
                    </span>
                    <span className={`text-xl font-mono font-bold ${
                      eventStatus.currentRound.remainingSeconds <= 60 ? 'text-red-600' : 'text-gray-900'
                    }`}>
                      {Math.floor(eventStatus.currentRound.remainingSeconds / 60)}:
                      {(eventStatus.currentRound.remainingSeconds % 60).toString().padStart(2, '0')}
                    </span>
                  </>
                )}
                {eventStatus.status === 'COMPLETED' && (
                  <span className="px-3 py-1 bg-green-100 text-green-800 rounded-full text-sm font-medium">
                    Event Completed
                  </span>
                )}
              </div>
            )}
          </div>
        </div>
      </header>

      {/* Navigation */}
      <nav className="bg-white border-b">
        <div className="max-w-7xl mx-auto px-4">
          <div className="flex gap-1 overflow-x-auto">
            {TABS.map((tab) => (
              <button
                key={tab.key}
                onClick={() => setActiveTab(tab.key)}
                className={`px-4 py-3 text-sm font-medium whitespace-nowrap transition-colors ${
                  activeTab === tab.key
                    ? 'text-blue-600 border-b-2 border-blue-600'
                    : 'text-gray-500 hover:text-gray-700'
                }`}
              >
                <span className="mr-1">{tab.icon}</span>
                {tab.label}
              </button>
            ))}
          </div>
        </div>
      </nav>

      {/* Main Content */}
      <main className="max-w-7xl mx-auto px-4 py-6">
        {activeTab === 'overview' && (
          <div className="space-y-6">
            {/* Stats Cards */}
            {stats && <StatsCards stats={stats} />}

            {/* Event Control + Quick Views */}
            <div className="grid grid-cols-2 gap-6">
              <EventControl
                eventStatus={eventStatus}
                onStartEvent={handleStartEvent}
                loading={startingEvent}
              />
              <QuickLeaderboard
                leaderboard={leaderboard}
                onViewFull={() => setActiveTab('leaderboard')}
              />
            </div>

            {/* Violations */}
            <RecentViolations
              violations={recentViolations}
              onViewAll={() => setActiveTab('proctoring')}
            />
          </div>
        )}

        {activeTab === 'teams' && <TeamList />}

        {activeTab === 'coding' && <ProblemManager type="coding" />}

        {activeTab === 'debugging' && <ProblemManager type="debugging" />}

        {activeTab === 'ctf' && <ProblemManager type="ctf" />}

        {activeTab === 'submissions' && <SubmissionMonitor />}

        {activeTab === 'leaderboard' && <LiveLeaderboard autoRefresh={true} />}

        {activeTab === 'proctoring' && <AdminViolationsDashboard />}
      </main>
    </div>
  );
};

export default AdminDashboard;
