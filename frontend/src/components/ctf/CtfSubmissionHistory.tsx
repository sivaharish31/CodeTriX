import React, { useEffect, useState } from 'react';
import { ctfApi } from '../../services/ctfApi';
import type { CtfSubmissionListResponse } from '../../types/ctf';
import { CATEGORY_LABELS, CATEGORY_COLORS } from '../../types/ctf';

interface CtfSubmissionHistoryProps {
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

export const CtfSubmissionHistory: React.FC<CtfSubmissionHistoryProps> = ({
  refreshTrigger,
}) => {
  const [data, setData] = useState<CtfSubmissionListResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    loadSubmissions();
  }, [refreshTrigger]);

  const loadSubmissions = async () => {
    try {
      setLoading(true);
      const result = await ctfApi.getSubmissions();
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
      <div className="px-4 py-3 border-b border-gray-200 bg-teal-50">
        <div className="flex items-center justify-between">
          <h2 className="text-lg font-semibold text-gray-800">CTF Submissions</h2>
          <div className="flex items-center gap-4 text-sm">
            <span className="text-gray-600">
              Solved: <span className="font-semibold text-green-600">{data.challengesSolved}/{data.totalChallenges}</span>
            </span>
            <span className="text-gray-600">
              Points: <span className="font-semibold text-teal-600">{data.totalPoints}</span>
            </span>
          </div>
        </div>
      </div>

      {data.submissions.length === 0 ? (
        <div className="p-8 text-center text-gray-500">
          No submissions yet. Start capturing flags!
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
                  Challenge
                </th>
                <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Category
                </th>
                <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Result
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
                    {submission.challengeTitle}
                  </td>
                  <td className="px-4 py-3 whitespace-nowrap">
                    <span className={`px-2 py-1 rounded text-xs font-medium ${CATEGORY_COLORS[submission.challengeCategory]}`}>
                      {CATEGORY_LABELS[submission.challengeCategory]}
                    </span>
                  </td>
                  <td className="px-4 py-3 whitespace-nowrap">
                    {submission.correct ? (
                      <span className="inline-flex items-center gap-1 text-green-600 font-medium text-sm">
                        <span>✓</span> Correct
                      </span>
                    ) : (
                      <span className="text-red-500 text-sm">✗ Incorrect</span>
                    )}
                  </td>
                  <td className="px-4 py-3 whitespace-nowrap">
                    {submission.correct ? (
                      <span className="text-sm font-semibold text-teal-600">
                        +{submission.pointsAwarded}
                      </span>
                    ) : (
                      <span className="text-sm text-gray-400">0</span>
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

export default CtfSubmissionHistory;
