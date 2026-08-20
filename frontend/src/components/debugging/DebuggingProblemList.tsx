import React, { useEffect, useState } from 'react';
import { debuggingApi } from '../../services/debuggingApi';
import type { DebuggingProblemListItem } from '../../types/debugging';
import { LANGUAGE_CONFIG } from '../../types/coding';

interface DebuggingProblemListProps {
  onSelectProblem: (problemId: number) => void;
  selectedProblemId?: number;
}

export const DebuggingProblemList: React.FC<DebuggingProblemListProps> = ({
  onSelectProblem,
  selectedProblemId,
}) => {
  const [problems, setProblems] = useState<DebuggingProblemListItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    loadProblems();
  }, []);

  const loadProblems = async () => {
    try {
      setLoading(true);
      const data = await debuggingApi.getProblems();
      setProblems(data);
      setError(null);
    } catch (err) {
      setError('Failed to load problems');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="p-4 text-center text-gray-500">Loading problems...</div>
    );
  }

  if (error) {
    return (
      <div className="p-4 text-center text-red-500">{error}</div>
    );
  }

  return (
    <div className="bg-white rounded-lg shadow">
      <div className="px-4 py-3 border-b border-gray-200">
        <h2 className="text-lg font-semibold text-gray-800">Debug Challenges</h2>
      </div>
      <div className="divide-y divide-gray-200">
        {problems.map((problem) => (
          <button
            key={problem.id}
            onClick={() => onSelectProblem(problem.id)}
            className={`w-full px-4 py-3 text-left hover:bg-gray-50 transition-colors ${
              selectedProblemId === problem.id ? 'bg-orange-50 border-l-4 border-orange-500' : ''
            }`}
          >
            <div className="flex items-center justify-between">
              <div>
                <span className="font-medium text-gray-900">
                  {problem.displayOrder + 1}. {problem.title}
                </span>
                <div className="flex items-center gap-2 mt-1">
                  <span className="px-2 py-0.5 text-xs rounded-full bg-gray-100 text-gray-700">
                    {LANGUAGE_CONFIG[problem.language]?.name || problem.language}
                  </span>
                </div>
              </div>
              <span className="text-sm font-semibold text-orange-600">
                {problem.points} pts
              </span>
            </div>
          </button>
        ))}
      </div>
    </div>
  );
};

export default DebuggingProblemList;
