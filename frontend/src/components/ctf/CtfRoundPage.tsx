import React, { useState, useEffect, useCallback } from 'react';
import { CtfChallengeList } from './CtfChallengeList';
import { CtfChallengeDetail } from './CtfChallengeDetail';
import { CtfSubmissionHistory } from './CtfSubmissionHistory';
import { ctfApi } from '../../services/ctfApi';
import type { CtfChallenge } from '../../types/ctf';

interface CtfRoundPageProps {
  roundRemainingSeconds?: number;
}

export const CtfRoundPage: React.FC<CtfRoundPageProps> = ({
  roundRemainingSeconds,
}) => {
  const [selectedChallengeId, setSelectedChallengeId] = useState<number | undefined>();
  const [selectedChallenge, setSelectedChallenge] = useState<CtfChallenge | null>(null);
  const [loadingChallenge, setLoadingChallenge] = useState(false);
  const [submissionRefresh, setSubmissionRefresh] = useState(0);
  const [challengeRefresh, setChallengeRefresh] = useState(0);
  const [activeTab, setActiveTab] = useState<'challenges' | 'submissions'>('challenges');

  useEffect(() => {
    if (selectedChallengeId) {
      loadChallenge(selectedChallengeId);
    }
  }, [selectedChallengeId, challengeRefresh]);

  const loadChallenge = async (id: number) => {
    setLoadingChallenge(true);
    try {
      const challenge = await ctfApi.getChallenge(id);
      setSelectedChallenge(challenge);
    } catch (error) {
      console.error('Failed to load challenge:', error);
      setSelectedChallenge(null);
    } finally {
      setLoadingChallenge(false);
    }
  };

  const handleSolved = useCallback(() => {
    setSubmissionRefresh((prev) => prev + 1);
    setChallengeRefresh((prev) => prev + 1);
  }, []);

  const formatTime = (seconds: number): string => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
  };

  return (
    <div className="min-h-screen bg-gray-100">
      {/* Header */}
      <header className="bg-white shadow-sm border-b border-teal-200">
        <div className="max-w-full mx-auto px-4 py-3">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-4">
              <h1 className="text-xl font-bold text-gray-900">
                Round 3: Capture The Flag
              </h1>
              <div className="flex gap-2">
                <button
                  onClick={() => setActiveTab('challenges')}
                  className={`px-3 py-1 rounded-md text-sm font-medium ${
                    activeTab === 'challenges'
                      ? 'bg-teal-100 text-teal-700'
                      : 'text-gray-500 hover:text-gray-700'
                  }`}
                >
                  Challenges
                </button>
                <button
                  onClick={() => setActiveTab('submissions')}
                  className={`px-3 py-1 rounded-md text-sm font-medium ${
                    activeTab === 'submissions'
                      ? 'bg-teal-100 text-teal-700'
                      : 'text-gray-500 hover:text-gray-700'
                  }`}
                >
                  Submissions
                </button>
              </div>
            </div>

            {roundRemainingSeconds !== undefined && (
              <div className={`text-2xl font-mono font-bold ${
                roundRemainingSeconds <= 60 ? 'text-red-600' : 'text-gray-900'
              }`}>
                {formatTime(roundRemainingSeconds)}
              </div>
            )}
          </div>
        </div>
      </header>

      {/* Main Content */}
      <div className="flex h-[calc(100vh-64px)]">
        {/* Challenge List Sidebar */}
        <div className="w-72 flex-shrink-0 p-4 overflow-y-auto border-r border-gray-200 bg-white">
          <CtfChallengeList
            onSelectChallenge={setSelectedChallengeId}
            selectedChallengeId={selectedChallengeId}
            refreshTrigger={challengeRefresh}
          />
        </div>

        {/* Main Area */}
        <div className="flex-1 p-4 overflow-y-auto">
          {activeTab === 'challenges' ? (
            <div className="max-w-4xl mx-auto">
              {loadingChallenge ? (
                <div className="bg-white rounded-lg shadow p-8 text-center text-gray-500">
                  Loading challenge...
                </div>
              ) : selectedChallenge ? (
                <CtfChallengeDetail
                  challenge={selectedChallenge}
                  onSolved={handleSolved}
                />
              ) : (
                <div className="bg-white rounded-lg shadow p-8 text-center text-gray-500">
                  <div className="text-4xl mb-4">🚩</div>
                  <p>Select a challenge from the list to start capturing flags</p>
                </div>
              )}
            </div>
          ) : (
            <div className="max-w-5xl mx-auto">
              <CtfSubmissionHistory refreshTrigger={submissionRefresh} />
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default CtfRoundPage;
