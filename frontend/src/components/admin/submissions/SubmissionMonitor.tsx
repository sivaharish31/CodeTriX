import React, { useState, useEffect } from 'react';
import { adminApi } from '../../../services/adminApi';
import type { SubmissionSummary } from '../../../types/admin';

export const SubmissionMonitor: React.FC = () => {
  const [submissions, setSubmissions] = useState<SubmissionSummary[]>([]);
  const [filter, setFilter] = useState<'all' | 'coding' | 'debugging'>('all');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadSubmissions();
    const interval = setInterval(loadSubmissions, 5000);
    return () => clearInterval(interval);
  }, [filter]);

  const loadSubmissions = async () => {
    try {
      let data: SubmissionSummary[] = [];

      if (filter === 'all' || filter === 'coding') {
        const coding = await adminApi.getCodingSubmissions(50);
        data = [...data, ...coding.map(s => ({ ...s, type: 'CODING' as const }))];
      }

      if (filter === 'all' || filter === 'debugging') {
        const debugging = await adminApi.getDebuggingSubmissions(50);
        data = [...data, ...debugging.map(s => ({ ...s, type: 'DEBUGGING' as const }))];
      }

      data.sort((a, b) =>
        new Date(b.submissionTime).getTime() - new Date(a.submissionTime).getTime()
      );

      setSubmissions(data.slice(0, 100));
    } catch (err) {
      console.error('Failed to load submissions:', err);
    } finally {
      setLoading(false);
    }
  };

  const formatTime = (timestamp: string): string => {
    return new Date(timestamp).toLocaleTimeString('en-US', {
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit',
    });
  };

  const getStatusColor = (status: string): string => {
    switch (status) {
      case 'ACCEPTED': return 'bg-green-100 text-green-800';
      case 'WRONG_ANSWER': return 'bg-red-100 text-red-800';
      case 'PARTIAL': return 'bg-yellow-100 text-yellow-800';
      case 'COMPILATION_ERROR': return 'bg-orange-100 text-orange-800';
      case 'RUNTIME_ERROR': return 'bg-red-100 text-red-800';
      case 'TIME_LIMIT_EXCEEDED': return 'bg-purple-100 text-purple-800';
      default: return 'bg-gray-100 text-gray-800';
    }
  };

  return (
    <div className="bg-white rounded-lg shadow overflow-hidden">
      <div className="px-4 py-3 bg-gray-50 border-b flex items-center justify-between">
        <h3 className="font-semibold text-gray-800">Submission Monitor</h3>
        <div className="flex items-center gap-2">
          <select
            value={filter}
            onChange={(e) => setFilter(e.target.value as any)}
            className="px-3 py-1 border border-gray-300 rounded text-sm"
          >
            <option value="all">All</option>
            <option value="coding">Coding Only</option>
            <option value="debugging">Debugging Only</option>
          </select>
          <button
            onClick={loadSubmissions}
            className="px-3 py-1 bg-blue-100 text-blue-700 rounded text-sm hover:bg-blue-200"
          >
            Refresh
          </button>
        </div>
      </div>

      {loading ? (
        <div className="p-8 text-center text-gray-500">Loading submissions...</div>
      ) : submissions.length === 0 ? (
        <div className="p-8 text-center text-gray-500">No submissions yet</div>
      ) : (
        <div className="overflow-x-auto max-h-[600px] overflow-y-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50 sticky top-0">
              <tr>
                <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Time</th>
                <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Team</th>
                <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Type</th>
                <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Problem</th>
                <th className="px-4 py-3 text-center text-xs font-medium text-gray-500 uppercase">Lang</th>
                <th className="px-4 py-3 text-center text-xs font-medium text-gray-500 uppercase">Status</th>
                <th className="px-4 py-3 text-center text-xs font-medium text-gray-500 uppercase">Tests</th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {submissions.map((sub, idx) => (
                <tr key={`${sub.type}-${sub.id}-${idx}`} className="hover:bg-gray-50">
                  <td className="px-4 py-2 whitespace-nowrap text-sm text-gray-500">
                    {formatTime(sub.submissionTime)}
                  </td>
                  <td className="px-4 py-2 whitespace-nowrap text-sm font-medium text-gray-800">
                    {sub.teamName}
                  </td>
                  <td className="px-4 py-2 whitespace-nowrap">
                    <span className={`px-2 py-0.5 rounded text-xs font-medium ${
                      sub.type === 'CODING' ? 'bg-blue-100 text-blue-800' : 'bg-orange-100 text-orange-800'
                    }`}>
                      {sub.type}
                    </span>
                  </td>
                  <td className="px-4 py-2 whitespace-nowrap text-sm text-gray-600">
                    {sub.problemTitle || `#${sub.problemId}`}
                  </td>
                  <td className="px-4 py-2 whitespace-nowrap text-center text-xs text-gray-500">
                    {sub.language}
                  </td>
                  <td className="px-4 py-2 whitespace-nowrap text-center">
                    <span className={`px-2 py-0.5 rounded text-xs font-medium ${getStatusColor(sub.status)}`}>
                      {sub.status.replace('_', ' ')}
                    </span>
                  </td>
                  <td className="px-4 py-2 whitespace-nowrap text-center text-sm">
                    <span className={sub.testsPassed === sub.totalTests ? 'text-green-600' : 'text-gray-600'}>
                      {sub.testsPassed}/{sub.totalTests}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
};

export default SubmissionMonitor;
