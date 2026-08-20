import React, { useEffect, useState } from 'react';
import { ctfApi } from '../../services/ctfApi';
import type { CtfChallenge, CtfCategory } from '../../types/ctf';
import { CATEGORY_LABELS, CATEGORY_COLORS, DIFFICULTY_LABELS, DIFFICULTY_COLORS } from '../../types/ctf';

interface CtfChallengeListProps {
  onSelectChallenge: (id: number) => void;
  selectedChallengeId?: number;
  refreshTrigger?: number;
}

export const CtfChallengeList: React.FC<CtfChallengeListProps> = ({
  onSelectChallenge,
  selectedChallengeId,
  refreshTrigger,
}) => {
  const [challenges, setChallenges] = useState<CtfChallenge[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [filterCategory, setFilterCategory] = useState<CtfCategory | 'ALL'>('ALL');

  useEffect(() => {
    loadChallenges();
  }, [refreshTrigger]);

  const loadChallenges = async () => {
    try {
      setLoading(true);
      const data = await ctfApi.getChallenges();
      setChallenges(data);
      setError(null);
    } catch (err) {
      setError('Failed to load challenges');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const filteredChallenges = filterCategory === 'ALL'
    ? challenges
    : challenges.filter(c => c.category === filterCategory);

  const categories: (CtfCategory | 'ALL')[] = ['ALL', 'WEB', 'CRYPTOGRAPHY', 'FORENSICS', 'ENCODING', 'STEGANOGRAPHY'];

  if (loading) {
    return <div className="text-gray-500 text-center py-4">Loading challenges...</div>;
  }

  if (error) {
    return <div className="text-red-500 text-center py-4">{error}</div>;
  }

  return (
    <div>
      <h2 className="text-lg font-semibold text-gray-800 mb-3">CTF Challenges</h2>

      {/* Category Filter */}
      <div className="mb-4">
        <select
          value={filterCategory}
          onChange={(e) => setFilterCategory(e.target.value as CtfCategory | 'ALL')}
          className="w-full px-3 py-2 text-sm border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-teal-500"
        >
          {categories.map(cat => (
            <option key={cat} value={cat}>
              {cat === 'ALL' ? 'All Categories' : CATEGORY_LABELS[cat]}
            </option>
          ))}
        </select>
      </div>

      {/* Challenge List */}
      <div className="space-y-2">
        {filteredChallenges.length === 0 ? (
          <div className="text-gray-500 text-sm text-center py-4">
            No challenges available
          </div>
        ) : (
          filteredChallenges.map((challenge) => (
            <button
              key={challenge.id}
              onClick={() => onSelectChallenge(challenge.id)}
              className={`w-full text-left p-3 rounded-lg border transition-all ${
                selectedChallengeId === challenge.id
                  ? 'border-teal-500 bg-teal-50 shadow-sm'
                  : 'border-gray-200 hover:border-teal-300 hover:bg-gray-50'
              }`}
            >
              <div className="flex items-start justify-between gap-2">
                <div className="flex-1 min-w-0">
                  <div className="flex items-center gap-2 mb-1">
                    {challenge.solved && (
                      <span className="text-green-500 text-sm" title="Solved">
                        ✓
                      </span>
                    )}
                    <span className={`font-medium text-sm truncate ${
                      challenge.solved ? 'text-green-700' : 'text-gray-800'
                    }`}>
                      {challenge.title}
                    </span>
                  </div>
                  <div className="flex items-center gap-2 flex-wrap">
                    <span className={`px-2 py-0.5 rounded text-xs font-medium ${CATEGORY_COLORS[challenge.category]}`}>
                      {CATEGORY_LABELS[challenge.category]}
                    </span>
                    <span className={`text-xs font-medium ${DIFFICULTY_COLORS[challenge.difficulty]}`}>
                      {DIFFICULTY_LABELS[challenge.difficulty]}
                    </span>
                  </div>
                </div>
                <div className="text-right flex-shrink-0">
                  <span className={`text-sm font-bold ${challenge.solved ? 'text-green-600' : 'text-teal-600'}`}>
                    {challenge.points}
                  </span>
                  <span className="text-xs text-gray-500 block">pts</span>
                </div>
              </div>
              {challenge.hasAttachment && (
                <div className="mt-1 text-xs text-gray-500 flex items-center gap-1">
                  <span>📎</span>
                  <span>{challenge.attachmentFilename}</span>
                </div>
              )}
            </button>
          ))
        )}
      </div>

      {/* Stats */}
      <div className="mt-4 pt-3 border-t border-gray-200 text-xs text-gray-500">
        <div className="flex justify-between">
          <span>Solved:</span>
          <span className="font-medium text-green-600">
            {challenges.filter(c => c.solved).length}/{challenges.length}
          </span>
        </div>
      </div>
    </div>
  );
};

export default CtfChallengeList;
