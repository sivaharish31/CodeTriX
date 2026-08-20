import React, { useEffect, useState } from 'react';
import { codingApi } from '../../services/codingApi';
import type { Submission, SubmissionListResponse } from '../../types/coding';
import { STATUS_COLORS } from '../../types/coding';

interface SubmissionHistoryProps {
  refreshTrigger?: number;
}

const formatTime = (timestamp: string): string => {
  const date = new Date(timestamp);
  return date.toLocaleTimeString('en-US', {
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
  });
};

export const SubmissionHistory: React.FC<SubmissionHistoryProps> = ({
  refreshTrigger,
}) => {
  const [data, setData] = useState<SubmissionListResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    loadSubmissions();
  }, [refreshTrigger]);

  const loadSubmissions = async () => {
    try {
      setLoading(true);
      const result = await codingApi.getSubmissions();
      setData(result);
      setError(null);
    } catch (err) {
      setError('Failed to load submissions');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="bg-white rounded-lg shadow p-4 text-center text-gray-500">
        Loading submissions...
      </div>
    );
  }

  if (error) {
    return (
      <div className="bg-white rounded-lg shadow p-4 text-center text-red-500">
        {error}
      </div>
    );
  }

  if (!data) {
    return null;
  }

  return (
    <div className="bg-white rounded-lg shadow overflow-hidden">
      <div className="px-4 py-3 border-b border-gray-200 bg-gray-50">
        <div className="flex items-center justify-between">
          <h2 className="text-lg font-semibold text-gray-800">Submissions</h2>
          <div className="flex items-center gap-4 text-sm">
            <span className="text-gray-600">
              Solved: <span className="font-semibold text-green-600">{data.problemsSolved}</span>
            </span>
            <span className="text-gray-600">
              Points: <span className="font-semibold text-blue-600">{data.totalPointsEarned}</span>
            </span>
          </div>
        </div>
      </div>

      {data.submissions.length === 0 ? (
        <div className="p-8 text-center text-gray-500">
          No submissions yet. Start coding!
        </div>
      ) : (
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Time
                </th>
                <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Problem
                </th>
                <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Language
                </th>
                <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Status
                </th>
                <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Tests
                </th>
                <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Points
                </th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {data.submissions.map((submission) => (
                <tr key={submission.id} className="hover:bg-gray-50">
                  <td className="px-4 py-3 whitespace-nowrap text-sm text-gray-500">
                    {formatTime(submission.submissionTime)}
                  </td>
                  <td className="px-4 py-3 whitespace-nowrap text-sm font-medium text-gray-900">
                    {submission.problemTitle || `Problem #${submission.problemId}`}
                  </td>
                  <td className="px-4 py-3 whitespace-nowrap text-sm text-gray-500">
                    {submission.language}
                  </td>
                  <td className="px-4 py-3 whitespace-nowrap">
                    <span className={`text-sm font-medium ${STATUS_COLORS[submission.status]}`}>
                      {submission.status.replace('_', ' ')}
                    </span>
                  </td>
                  <td className="px-4 py-3 whitespace-nowrap text-sm text-gray-500">
                    {submission.testsPassed}/{submission.totalTests}
                  </td>
                  <td className="px-4 py-3 whitespace-nowrap text-sm font-semibold text-blue-600">
                    {submission.pointsEarned}
                    {submission.maxPoints && (
                      <span className="text-gray-400 font-normal">
                        /{submission.maxPoints}
                      </span>
                    )}
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

export default SubmissionHistory;
