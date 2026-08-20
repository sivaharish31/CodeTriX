import React, { useState } from 'react';
import type { CtfChallenge } from '../../types/ctf';
import { CATEGORY_LABELS, CATEGORY_COLORS, DIFFICULTY_LABELS, DIFFICULTY_COLORS } from '../../types/ctf';
import { ctfApi } from '../../services/ctfApi';

interface CtfChallengeDetailProps {
  challenge: CtfChallenge;
  onSolved?: () => void;
}

export const CtfChallengeDetail: React.FC<CtfChallengeDetailProps> = ({
  challenge,
  onSolved,
}) => {
  const [flag, setFlag] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [result, setResult] = useState<{ correct: boolean; message: string } | null>(null);
  const [downloading, setDownloading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!flag.trim() || submitting) return;

    setSubmitting(true);
    setResult(null);

    try {
      const response = await ctfApi.submitFlag(challenge.id, flag.trim());
      setResult({
        correct: response.correct,
        message: response.message,
      });

      if (response.correct) {
        setFlag('');
        onSolved?.();
      }
    } catch (error: any) {
      const message = error.response?.data?.message || 'Failed to submit flag';
      setResult({
        correct: false,
        message,
      });
    } finally {
      setSubmitting(false);
    }
  };

  const handleDownload = async () => {
    if (!challenge.hasAttachment || downloading) return;

    setDownloading(true);
    try {
      await ctfApi.downloadAttachment(challenge.id, challenge.attachmentFilename || 'attachment');
    } catch (error) {
      console.error('Download failed:', error);
    } finally {
      setDownloading(false);
    }
  };

  return (
    <div className="bg-white rounded-lg shadow overflow-hidden">
      {/* Header */}
      <div className="px-6 py-4 border-b border-gray-200 bg-gradient-to-r from-teal-50 to-cyan-50">
        <div className="flex items-start justify-between">
          <div>
            <div className="flex items-center gap-2 mb-2">
              {challenge.solved && (
                <span className="text-green-500 text-xl" title="Solved">✓</span>
              )}
              <h2 className="text-xl font-bold text-gray-900">{challenge.title}</h2>
            </div>
            <div className="flex items-center gap-3">
              <span className={`px-2 py-1 rounded text-sm font-medium ${CATEGORY_COLORS[challenge.category]}`}>
                {CATEGORY_LABELS[challenge.category]}
              </span>
              <span className={`text-sm font-medium ${DIFFICULTY_COLORS[challenge.difficulty]}`}>
                {DIFFICULTY_LABELS[challenge.difficulty]}
              </span>
            </div>
          </div>
          <div className="text-right">
            <span className={`text-2xl font-bold ${challenge.solved ? 'text-green-600' : 'text-teal-600'}`}>
              {challenge.points}
            </span>
            <span className="text-sm text-gray-500 block">points</span>
          </div>
        </div>
      </div>

      {/* Description */}
      <div className="px-6 py-4">
        <h3 className="text-sm font-semibold text-gray-500 uppercase tracking-wider mb-2">
          Description
        </h3>
        <div className="prose prose-sm max-w-none text-gray-700 whitespace-pre-wrap">
          {challenge.description}
        </div>
      </div>

      {/* Attachment */}
      {challenge.hasAttachment && (
        <div className="px-6 py-3 border-t border-gray-100 bg-gray-50">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2 text-sm text-gray-600">
              <span>📎</span>
              <span>{challenge.attachmentFilename}</span>
            </div>
            <button
              onClick={handleDownload}
              disabled={downloading}
              className="px-3 py-1 text-sm bg-teal-100 text-teal-700 rounded hover:bg-teal-200 disabled:opacity-50 transition-colors"
            >
              {downloading ? 'Downloading...' : 'Download'}
            </button>
          </div>
        </div>
      )}

      {/* Flag Submission */}
      <div className="px-6 py-4 border-t border-gray-200">
        <h3 className="text-sm font-semibold text-gray-500 uppercase tracking-wider mb-3">
          Submit Flag
        </h3>

        {challenge.solved ? (
          <div className="bg-green-50 text-green-700 px-4 py-3 rounded-lg text-sm">
            You have already solved this challenge!
          </div>
        ) : (
          <form onSubmit={handleSubmit}>
            <div className="flex gap-2">
              <input
                type="text"
                value={flag}
                onChange={(e) => setFlag(e.target.value)}
                placeholder="Enter flag..."
                className="flex-1 px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-teal-500 focus:border-transparent font-mono"
                disabled={submitting}
              />
              <button
                type="submit"
                disabled={submitting || !flag.trim()}
                className="px-6 py-2 bg-teal-600 text-white font-medium rounded-lg hover:bg-teal-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
              >
                {submitting ? 'Checking...' : 'Submit'}
              </button>
            </div>

            {result && (
              <div className={`mt-3 px-4 py-3 rounded-lg text-sm ${
                result.correct
                  ? 'bg-green-50 text-green-700 border border-green-200'
                  : 'bg-red-50 text-red-700 border border-red-200'
              }`}>
                {result.message}
              </div>
            )}
          </form>
        )}
      </div>
    </div>
  );
};

export default CtfChallengeDetail;
