import React, { useState, useEffect } from 'react';
import { adminApi } from '../../../services/adminApi';
import type { ProblemSummary, CtfChallengeSummary } from '../../../types/admin';

interface ProblemManagerProps {
  type: 'coding' | 'debugging' | 'ctf';
}

export const ProblemManager: React.FC<ProblemManagerProps> = ({ type }) => {
  const [problems, setProblems] = useState<(ProblemSummary | CtfChallengeSummary)[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    loadProblems();
  }, [type]);

  const loadProblems = async () => {
    try {
      setLoading(true);
      let data;
      switch (type) {
        case 'coding':
          data = await adminApi.getCodingProblems();
          break;
        case 'debugging':
          data = await adminApi.getDebuggingProblems();
          break;
        case 'ctf':
          data = await adminApi.getCtfChallenges();
          break;
      }
      setProblems(data);
      setError(null);
    } catch (err) {
      setError('Failed to load problems');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id: number) => {
    if (!confirm('Are you sure you want to delete this problem?')) return;
    try {
      switch (type) {
        case 'coding':
          await adminApi.deleteCodingProblem(id);
          break;
        case 'debugging':
          await adminApi.deleteDebuggingProblem(id);
          break;
      }
      loadProblems();
    } catch (err) {
      alert('Failed to delete problem');
    }
  };

  const getTypeLabel = () => {
    switch (type) {
      case 'coding': return 'Coding Problems';
      case 'debugging': return 'Debugging Problems';
      case 'ctf': return 'CTF Challenges';
    }
  };

  const getTypeColor = () => {
    switch (type) {
      case 'coding': return 'from-blue-500 to-blue-600';
      case 'debugging': return 'from-orange-500 to-orange-600';
      case 'ctf': return 'from-teal-500 to-teal-600';
    }
  };

  if (loading) {
    return <div className="p-8 text-center text-gray-500">Loading problems...</div>;
  }

  if (error) {
    return <div className="p-8 text-center text-red-500">{error}</div>;
  }

  return (
    <div className="bg-white rounded-lg shadow overflow-hidden">
      <div className={`px-4 py-3 bg-gradient-to-r ${getTypeColor()} flex items-center justify-between`}>
        <h3 className="text-lg font-semibold text-white">{getTypeLabel()}</h3>
        <div className="flex items-center gap-2">
          <span className="text-white/80 text-sm">{problems.length} items</span>
          <button
            onClick={loadProblems}
            className="px-3 py-1 bg-white/20 text-white rounded text-sm hover:bg-white/30"
          >
            Refresh
          </button>
        </div>
      </div>

      {problems.length === 0 ? (
        <div className="p-8 text-center text-gray-500">
          No {type} problems created yet.
        </div>
      ) : (
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">ID</th>
                <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Title</th>
                {type === 'ctf' && (
                  <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Category</th>
                )}
                <th className="px-4 py-3 text-center text-xs font-medium text-gray-500 uppercase">Points</th>
                <th className="px-4 py-3 text-center text-xs font-medium text-gray-500 uppercase">
                  {type === 'ctf' ? 'Difficulty' : 'Status'}
                </th>
                <th className="px-4 py-3 text-right text-xs font-medium text-gray-500 uppercase">Actions</th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {problems.map((problem) => (
                <tr key={problem.id} className="hover:bg-gray-50">
                  <td className="px-4 py-3 whitespace-nowrap text-sm text-gray-500">
                    #{problem.id}
                  </td>
                  <td className="px-4 py-3 whitespace-nowrap">
                    <span className="font-medium text-gray-800">{problem.title}</span>
                  </td>
                  {type === 'ctf' && 'category' in problem && (
                    <td className="px-4 py-3 whitespace-nowrap">
                      <span className="px-2 py-1 bg-gray-100 text-gray-700 rounded text-xs">
                        {problem.category}
                      </span>
                    </td>
                  )}
                  <td className="px-4 py-3 whitespace-nowrap text-center">
                    <span className={`font-semibold ${
                      type === 'coding' ? 'text-blue-600' :
                      type === 'debugging' ? 'text-orange-600' : 'text-teal-600'
                    }`}>
                      {problem.points}
                    </span>
                  </td>
                  <td className="px-4 py-3 whitespace-nowrap text-center">
                    {type === 'ctf' && 'difficulty' in problem ? (
                      <span className={`px-2 py-1 rounded text-xs font-medium ${
                        problem.difficulty === 'EASY' ? 'bg-green-100 text-green-800' :
                        problem.difficulty === 'MEDIUM' ? 'bg-yellow-100 text-yellow-800' :
                        problem.difficulty === 'HARD' ? 'bg-orange-100 text-orange-800' :
                        'bg-red-100 text-red-800'
                      }`}>
                        {problem.difficulty}
                      </span>
                    ) : (
                      <span className={`px-2 py-1 rounded-full text-xs font-medium ${
                        problem.enabled || ('active' in problem && problem.active)
                          ? 'bg-green-100 text-green-800'
                          : 'bg-gray-100 text-gray-800'
                      }`}>
                        {problem.enabled || ('active' in problem && problem.active) ? 'Active' : 'Inactive'}
                      </span>
                    )}
                  </td>
                  <td className="px-4 py-3 whitespace-nowrap text-right">
                    <button className="text-blue-600 hover:text-blue-800 text-sm mr-3">
                      Edit
                    </button>
                    {type !== 'ctf' && (
                      <button
                        onClick={() => handleDelete(problem.id)}
                        className="text-red-600 hover:text-red-800 text-sm"
                      >
                        Delete
                      </button>
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

export default ProblemManager;
