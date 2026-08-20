import React, { useState, useEffect, useCallback } from 'react';
import { useProctoring } from '../../hooks/useProctoring';
import type { ViolationType } from '../../types/proctoring';
import { VIOLATION_LABELS } from '../../types/proctoring';

interface ProctoringOverlayProps {
  enabled: boolean;
  roundId?: number;
  requireFullscreen?: boolean;
  children: React.ReactNode;
}

export const ProctoringOverlay: React.FC<ProctoringOverlayProps> = ({
  enabled,
  roundId,
  requireFullscreen = true,
  children,
}) => {
  const [warning, setWarning] = useState<string | null>(null);
  const [showFullscreenPrompt, setShowFullscreenPrompt] = useState(false);

  const handleViolation = useCallback((type: ViolationType) => {
    console.log('Violation:', type);
  }, []);

  const handleWarning = useCallback((message: string) => {
    setWarning(message);
    setTimeout(() => setWarning(null), 5000);
  }, []);

  const {
    isActive,
    isFullscreen,
    violationCount,
    enterFullscreen,
  } = useProctoring({
    enabled,
    roundId,
    onViolation: handleViolation,
    onWarning: handleWarning,
    requireFullscreen,
    blockCopyPaste: true,
    blockKeyboardShortcuts: true,
  });

  useEffect(() => {
    if (enabled && requireFullscreen && !isFullscreen) {
      setShowFullscreenPrompt(true);
    } else {
      setShowFullscreenPrompt(false);
    }
  }, [enabled, requireFullscreen, isFullscreen]);

  const handleEnterFullscreen = async () => {
    await enterFullscreen();
    setShowFullscreenPrompt(false);
  };

  return (
    <div className="relative min-h-screen">
      {/* Proctoring Status Bar */}
      {isActive && (
        <div className="fixed top-0 left-0 right-0 z-50 bg-gradient-to-r from-red-600 to-red-700 text-white px-4 py-2 text-sm">
          <div className="flex items-center justify-between max-w-7xl mx-auto">
            <div className="flex items-center gap-4">
              <span className="flex items-center gap-2">
                <span className="w-2 h-2 bg-white rounded-full animate-pulse"></span>
                Proctoring Active
              </span>
              {requireFullscreen && (
                <span className={`px-2 py-0.5 rounded text-xs ${
                  isFullscreen ? 'bg-green-500' : 'bg-yellow-500'
                }`}>
                  {isFullscreen ? 'Fullscreen' : 'Not Fullscreen'}
                </span>
              )}
            </div>
            {violationCount > 0 && (
              <span className="text-yellow-200">
                Violations: {violationCount}
              </span>
            )}
          </div>
        </div>
      )}

      {/* Warning Toast */}
      {warning && (
        <div className="fixed top-16 left-1/2 transform -translate-x-1/2 z-50 animate-bounce">
          <div className="bg-yellow-500 text-white px-6 py-3 rounded-lg shadow-lg flex items-center gap-3">
            <span className="text-2xl">⚠️</span>
            <span className="font-medium">{warning}</span>
          </div>
        </div>
      )}

      {/* Fullscreen Prompt Modal */}
      {showFullscreenPrompt && (
        <div className="fixed inset-0 z-50 bg-black bg-opacity-90 flex items-center justify-center">
          <div className="bg-white rounded-xl shadow-2xl max-w-md w-full mx-4 overflow-hidden">
            <div className="bg-red-600 text-white px-6 py-4">
              <h2 className="text-xl font-bold">Fullscreen Required</h2>
            </div>
            <div className="p-6">
              <p className="text-gray-700 mb-4">
                This competition requires fullscreen mode for proctoring purposes.
                Please click the button below to enter fullscreen mode.
              </p>
              <div className="bg-yellow-50 border border-yellow-200 rounded-lg p-4 mb-6">
                <h3 className="font-semibold text-yellow-800 mb-2">Important:</h3>
                <ul className="text-sm text-yellow-700 space-y-1">
                  <li>• Do not switch tabs during the competition</li>
                  <li>• Do not exit fullscreen mode</li>
                  <li>• Copy/paste is disabled</li>
                  <li>• All violations are recorded</li>
                </ul>
              </div>
              <button
                onClick={handleEnterFullscreen}
                className="w-full py-3 bg-red-600 text-white font-semibold rounded-lg hover:bg-red-700 transition-colors"
              >
                Enter Fullscreen & Start
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Main Content */}
      <div className={isActive ? 'pt-12' : ''}>
        {children}
      </div>
    </div>
  );
};

export default ProctoringOverlay;
