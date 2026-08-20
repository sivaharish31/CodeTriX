import React, { useState, useEffect, useCallback } from 'react';
import { ProblemList } from './ProblemList';
import { ProblemDetail } from './ProblemDetail';
import { CodeEditor } from './CodeEditor';
import { SubmissionHistory } from './SubmissionHistory';
import { codingApi } from '../../services/codingApi';
import type { Problem } from '../../types/coding';

interface CodingRoundPageProps {
  roundRemainingSeconds?: number;
}

export const CodingRoundPage: React.FC<CodingRoundPageProps> = ({
  roundRemainingSeconds,
}) => {
  const [selectedProblemId, setSelectedProblemId] = useState<number | undefined>();
  const [selectedProblem, setSelectedProblem] = useState<Problem | null>(null);
  const [loadingProblem, setLoadingProblem] = useState(false);
  const [submissionRefresh, setSubmissionRefresh] = useState(0);
  const [activeTab, setActiveTab] = useState<'problem' | 'submissions'>('problem');

  useEffect(() => {
    if (selectedProblemId) {
      loadProblem(selectedProblemId);
    }
  }, [selectedProblemId]);

  const loadProblem = async (id: number) => {
    setLoadingProblem(true);
    try {
      const problem = await codingApi.getProblem(id);
      setSelectedProblem(problem);
    } catch (error) {
      console.error('Failed to load problem:', error);
      setSelectedProblem(null);
    } finally {
      setLoadingProblem(false);
    }
  };

  const handleSubmissionSuccess = useCallback(() => {
    setSubmissionRefresh((prev) => prev + 1);
  }, []);

  const formatTime = (seconds: number): string => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
  };

  return (
    <div className="min-h-screen bg-gray-100">
      {/* Header */}
      <header className="bg-white shadow-sm border-b border-gray-200">
        <div className="max-w-full mx-auto px-4 py-3">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-4">
              <h1 className="text-xl font-bold text-gray-900">
                Round 1: Coding
              </h1>
              <div className="flex gap-2">
                <button
                  onClick={() => setActiveTab('problem')}
                  className={`px-3 py-1 rounded-md text-sm font-medium ${
                    activeTab === 'problem'
                      ? 'bg-blue-100 text-blue-700'
                      : 'text-gray-500 hover:text-gray-700'
                  }`}
                >
                  Problem
                </button>
                <button
                  onClick={() => setActiveTab('submissions')}
                  className={`px-3 py-1 rounded-md text-sm font-medium ${
                    activeTab === 'submissions'
                      ? 'bg-blue-100 text-blue-700'
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
        {/* Problem List Sidebar */}
        <div className="w-64 flex-shrink-0 p-4 overflow-y-auto border-r border-gray-200 bg-white">
          <ProblemList
            onSelectProblem={setSelectedProblemId}
            selectedProblemId={selectedProblemId}
          />
        </div>

        {/* Main Area */}
        <div className="flex-1 p-4 overflow-hidden">
          {activeTab === 'problem' ? (
            <div className="grid grid-cols-2 gap-4 h-full">
              {/* Problem Detail */}
              <div className="overflow-y-auto">
                {loadingProblem ? (
                  <div className="bg-white rounded-lg shadow p-8 text-center text-gray-500">
                    Loading problem...
                  </div>
                ) : selectedProblem ? (
                  <ProblemDetail problem={selectedProblem} />
                ) : (
                  <div className="bg-white rounded-lg shadow p-8 text-center text-gray-500">
                    Select a problem from the list to get started
                  </div>
                )}
              </div>

              {/* Code Editor */}
              <div className="h-full">
                {selectedProblem ? (
                  <CodeEditor
                    problemId={selectedProblem.id}
                    onSubmissionSuccess={handleSubmissionSuccess}
                  />
                ) : (
                  <div className="bg-white rounded-lg shadow p-8 text-center text-gray-500 h-full flex items-center justify-center">
                    Select a problem to start coding
                  </div>
                )}
              </div>
            </div>
          ) : (
            <SubmissionHistory refreshTrigger={submissionRefresh} />
          )}
        </div>
      </div>
    </div>
  );
};

export default CodingRoundPage;
