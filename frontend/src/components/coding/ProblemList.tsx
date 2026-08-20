import React, { useEffect, useState } from 'react';
import { codingApi } from '../../services/codingApi';
import type { ProblemListItem } from '../../types/coding';

interface ProblemListProps {
  onSelectProblem: (problemId: number) => void;
  selectedProblemId?: number;
}

const difficultyColors: Record<string, string> = {
  EASY: 'bg-green-100 text-green-800',
  MEDIUM: 'bg-yellow-100 text-yellow-800',
  HARD: 'bg-red-100 text-red-800',
};

export const ProblemList: React.FC<ProblemListProps> = ({
  onSelectProblem,
  selectedProblemId,
}) => {
  const [problems, setProblems] = useState<ProblemListItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    loadProblems();
  }, []);

  const loadProblems = async () => {
    try {
      setLoading(true);
      const data = await codingApi.getProblems();
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
        <h2 className="text-lg font-semibold text-gray-800">Problems</h2>
      </div>
      <div className="divide-y divide-gray-200">
        {problems.map((problem) => (
          <button
            key={problem.id}
            onClick={() => onSelectProblem(problem.id)}
            className={`w-full px-4 py-3 text-left hover:bg-gray-50 transition-colors ${
              selectedProblemId === problem.id ? 'bg-blue-50 border-l-4 border-blue-500' : ''
            }`}
          >
            <div className="flex items-center justify-between">
              <div>
                <span className="font-medium text-gray-900">
                  {problem.displayOrder + 1}. {problem.title}
                </span>
                <div className="flex items-center gap-2 mt-1">
                  <span className={`px-2 py-0.5 text-xs rounded-full ${
                    difficultyColors[problem.difficulty] || 'bg-gray-100 text-gray-800'
                  }`}>
                    {problem.difficulty}
                  </span>
                </div>
              </div>
              <span className="text-sm font-semibold text-blue-600">
                {problem.points} pts
              </span>
            </div>
          </button>
        ))}
      </div>
    </div>
  );
};

export default ProblemList;
