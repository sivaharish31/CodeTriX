import React, { useState } from 'react';
import type { EventStatus, RoundInfo } from '../../../types/admin';

interface EventControlProps {
  eventStatus: EventStatus | null;
  onStartEvent: () => Promise<void>;
  loading?: boolean;
}

const ROUND_LABELS: Record<string, string> = {
  CODING: 'Coding Round',
  DEBUGGING: 'Debugging Round',
  CTF: 'CTF Round',
};

const ROUND_COLORS: Record<string, string> = {
  CODING: 'from-blue-500 to-blue-600',
  DEBUGGING: 'from-orange-500 to-orange-600',
  CTF: 'from-teal-500 to-teal-600',
};

const formatTime = (seconds: number): string => {
  const mins = Math.floor(seconds / 60);
  const secs = seconds % 60;
  return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
};

const formatTotalTime = (seconds: number): string => {
  const mins = Math.floor(seconds / 60);
  const secs = seconds % 60;
  if (mins >= 60) {
    const hours = Math.floor(mins / 60);
    const remainingMins = mins % 60;
    return `${hours}h ${remainingMins}m ${secs}s`;
  }
  return `${mins}m ${secs}s`;
};

export const EventControl: React.FC<EventControlProps> = ({
  eventStatus,
  onStartEvent,
  loading = false,
}) => {
  const [confirming, setConfirming] = useState(false);

  const handleStartClick = () => {
    if (!confirming) {
      setConfirming(true);
      return;
    }
    onStartEvent();
    setConfirming(false);
  };

  const status = eventStatus?.status || 'NOT_STARTED';
  const currentRound = eventStatus?.currentRound;

  return (
    <div className="bg-white rounded-xl shadow-lg overflow-hidden">
      {/* Header */}
      <div className={`px-6 py-4 bg-gradient-to-r ${
        status === 'IN_PROGRESS' && currentRound
          ? ROUND_COLORS[currentRound.roundType]
          : status === 'COMPLETED'
          ? 'from-green-500 to-green-600'
          : 'from-gray-600 to-gray-700'
      }`}>
        <div className="flex items-center justify-between text-white">
          <div>
            <h2 className="text-lg font-semibold">Event Control</h2>
            <p className="text-sm opacity-90">{eventStatus?.eventName || 'CodeTriX Competition'}</p>
          </div>
          <div className={`px-3 py-1 rounded-full text-sm font-medium ${
            status === 'IN_PROGRESS' ? 'bg-white/20' :
            status === 'COMPLETED' ? 'bg-white/20' : 'bg-white/10'
          }`}>
            {status === 'NOT_STARTED' ? 'Not Started' :
             status === 'IN_PROGRESS' ? 'In Progress' : 'Completed'}
          </div>
        </div>
      </div>

      {/* Content */}
      <div className="p-6">
        {status === 'NOT_STARTED' && (
          <div className="text-center">
            <div className="mb-6">
              <p className="text-gray-600 mb-4">
                The event consists of 3 rounds, each lasting 15 minutes:
              </p>
              <div className="grid grid-cols-3 gap-4 text-sm">
                <div className="bg-blue-50 rounded-lg p-3">
                  <div className="font-semibold text-blue-700">Round 1</div>
                  <div className="text-blue-600">Coding</div>
                  <div className="text-blue-500 text-xs">15 minutes</div>
                </div>
                <div className="bg-orange-50 rounded-lg p-3">
                  <div className="font-semibold text-orange-700">Round 2</div>
                  <div className="text-orange-600">Debugging</div>
                  <div className="text-orange-500 text-xs">15 minutes</div>
                </div>
                <div className="bg-teal-50 rounded-lg p-3">
                  <div className="font-semibold text-teal-700">Round 3</div>
                  <div className="text-teal-600">CTF</div>
                  <div className="text-teal-500 text-xs">15 minutes</div>
                </div>
              </div>
            </div>

            <div className="bg-red-50 border border-red-200 rounded-lg p-4 mb-6 text-left">
              <h4 className="font-semibold text-red-800 mb-2">⚠️ Important Warning</h4>
              <ul className="text-sm text-red-700 space-y-1">
                <li>• Once started, the timer <strong>cannot be paused</strong></li>
                <li>• The timer <strong>cannot be stopped</strong></li>
                <li>• The timer <strong>cannot be extended</strong></li>
                <li>• Rounds transition <strong>automatically</strong></li>
                <li>• Event ends automatically after 45 minutes</li>
              </ul>
            </div>

            {confirming ? (
              <div className="space-y-3">
                <p className="text-red-600 font-semibold">
                  Are you sure? This action cannot be undone.
                </p>
                <div className="flex justify-center gap-3">
                  <button
                    onClick={() => setConfirming(false)}
                    className="px-6 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50"
                  >
                    Cancel
                  </button>
                  <button
                    onClick={handleStartClick}
                    disabled={loading}
                    className="px-6 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 disabled:opacity-50"
                  >
                    {loading ? 'Starting...' : 'Yes, Start Event'}
                  </button>
                </div>
              </div>
            ) : (
              <button
                onClick={handleStartClick}
                disabled={loading}
                className="px-8 py-3 bg-gradient-to-r from-green-500 to-green-600 text-white font-semibold rounded-lg hover:from-green-600 hover:to-green-700 shadow-lg transition-all disabled:opacity-50"
              >
                🚀 Start Event
              </button>
            )}
          </div>
        )}

        {status === 'IN_PROGRESS' && currentRound && (
          <div>
            {/* Current Round */}
            <div className="text-center mb-6">
              <div className="text-sm text-gray-500 mb-1">Current Round</div>
              <div className={`text-2xl font-bold ${
                currentRound.roundType === 'CODING' ? 'text-blue-600' :
                currentRound.roundType === 'DEBUGGING' ? 'text-orange-600' : 'text-teal-600'
              }`}>
                {ROUND_LABELS[currentRound.roundType]}
              </div>
              <div className="text-sm text-gray-500">
                Round {currentRound.roundNumber} of 3
              </div>
            </div>

            {/* Timer */}
            <div className="bg-gray-900 rounded-2xl p-6 text-center mb-6">
              <div className="text-sm text-gray-400 mb-2">Time Remaining</div>
              <div className={`text-6xl font-mono font-bold ${
                currentRound.remainingSeconds <= 60 ? 'text-red-500 animate-pulse' :
                currentRound.remainingSeconds <= 180 ? 'text-yellow-500' : 'text-white'
              }`}>
                {formatTime(currentRound.remainingSeconds)}
              </div>
              <div className="text-sm text-gray-500 mt-2">
                Total event time remaining: {formatTotalTime(eventStatus.remainingSeconds)}
              </div>
            </div>

            {/* Progress */}
            <div className="space-y-2">
              <div className="flex justify-between text-sm text-gray-600">
                <span>Event Progress</span>
                <span>{Math.round((eventStatus.elapsedSeconds / eventStatus.totalDurationSeconds) * 100)}%</span>
              </div>
              <div className="h-3 bg-gray-200 rounded-full overflow-hidden">
                <div
                  className="h-full bg-gradient-to-r from-blue-500 via-orange-500 to-teal-500 transition-all duration-1000"
                  style={{ width: `${(eventStatus.elapsedSeconds / eventStatus.totalDurationSeconds) * 100}%` }}
                />
              </div>
              <div className="flex justify-between text-xs text-gray-400">
                <span>Start</span>
                <span>15m</span>
                <span>30m</span>
                <span>45m</span>
              </div>
            </div>
          </div>
        )}

        {status === 'COMPLETED' && (
          <div className="text-center py-8">
            <div className="text-6xl mb-4">🏁</div>
            <h3 className="text-2xl font-bold text-gray-800 mb-2">Event Completed</h3>
            <p className="text-gray-600">
              The competition has ended. Check the leaderboard for final results.
            </p>
            {eventStatus?.endTime && (
              <p className="text-sm text-gray-500 mt-4">
                Ended at: {new Date(eventStatus.endTime).toLocaleString()}
              </p>
            )}
          </div>
        )}
      </div>
    </div>
  );
};

export default EventControl;
