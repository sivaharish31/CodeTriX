import React from 'react';
import type { DashboardStats } from '../../../types/admin';

interface StatsCardsProps {
  stats: DashboardStats;
}

interface StatCardProps {
  title: string;
  value: number | string;
  subtitle?: string;
  icon: string;
  color: string;
  trend?: 'up' | 'down' | 'neutral';
}

const StatCard: React.FC<StatCardProps> = ({ title, value, subtitle, icon, color }) => (
  <div className="bg-white rounded-lg shadow p-4">
    <div className="flex items-center justify-between">
      <div>
        <p className="text-sm text-gray-500">{title}</p>
        <p className={`text-2xl font-bold ${color}`}>{value}</p>
        {subtitle && <p className="text-xs text-gray-400">{subtitle}</p>}
      </div>
      <div className="text-3xl opacity-50">{icon}</div>
    </div>
  </div>
);

export const StatsCards: React.FC<StatsCardsProps> = ({ stats }) => {
  return (
    <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
      <StatCard
        title="Teams Registered"
        value={`${stats.teamsRegistered}/${stats.maxTeams}`}
        subtitle={stats.teamsRegistered === stats.maxTeams ? 'Full' : `${stats.maxTeams - stats.teamsRegistered} slots left`}
        icon="👥"
        color="text-blue-600"
      />
      <StatCard
        title="Active Teams"
        value={stats.activeTeams}
        subtitle="Currently online"
        icon="🟢"
        color="text-green-600"
      />
      <StatCard
        title="Total Submissions"
        value={stats.totalSubmissions}
        subtitle={`C: ${stats.codingSubmissions} | D: ${stats.debuggingSubmissions}`}
        icon="📝"
        color="text-purple-600"
      />
      <StatCard
        title="Violations"
        value={stats.totalViolations}
        subtitle={`${stats.teamsWithViolations} teams`}
        icon="⚠️"
        color={stats.totalViolations > 0 ? 'text-red-600' : 'text-gray-600'}
      />
    </div>
  );
};

export default StatsCards;
