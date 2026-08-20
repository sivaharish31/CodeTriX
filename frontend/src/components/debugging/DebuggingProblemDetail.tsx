import React from 'react';
import type { DebuggingProblem } from '../../types/debugging';
import { LANGUAGE_CONFIG } from '../../types/coding';

interface DebuggingProblemDetailProps {
  problem: DebuggingProblem;
}

export const DebuggingProblemDetail: React.FC<DebuggingProblemDetailProps> = ({ problem }) => {
  return (
    <div className="bg-white rounded-lg shadow overflow-hidden">
      <div className="px-6 py-4 border-b border-gray-200 bg-orange-50">
        <div className="flex items-center justify-between">
          <h1 className="text-xl font-bold text-gray-900">{problem.title}</h1>
          <div className="flex items-center gap-3">
            <span className="px-2 py-1 bg-gray-200 text-gray-700 rounded text-sm font-medium">
              {LANGUAGE_CONFIG[problem.language]?.name || problem.language}
            </span>
            <span className="px-3 py-1 bg-orange-100 text-orange-800 rounded-full text-sm font-semibold">
              {problem.points} points
            </span>
          </div>
        </div>
        <div className="flex items-center gap-4 mt-2 text-sm text-gray-500">
          <span>Time Limit: {problem.timeLimitMs}ms</span>
          <span>Memory Limit: {problem.memoryLimitMb}MB</span>
        </div>
      </div>

      <div className="px-6 py-4 space-y-6 max-h-[calc(100vh-300px)] overflow-y-auto">
        <section>
          <h3 className="text-sm font-semibold text-gray-700 uppercase tracking-wide mb-2">
            Problem Description
          </h3>
          <div className="prose prose-sm max-w-none text-gray-600 whitespace-pre-wrap">
            {problem.description}
          </div>
        </section>

        {problem.hint && (
          <section>
            <h3 className="text-sm font-semibold text-amber-700 uppercase tracking-wide mb-2">
              Hint
            </h3>
            <div className="bg-amber-50 border border-amber-200 rounded-lg p-4 text-sm text-amber-800">
              {problem.hint}
            </div>
          </section>
        )}

        {problem.sampleTestCases && problem.sampleTestCases.length > 0 && (
          <section>
            <h3 className="text-sm font-semibold text-gray-700 uppercase tracking-wide mb-2">
              Expected Behavior
            </h3>
            <div className="space-y-4">
              {problem.sampleTestCases.map((tc, index) => (
                <div key={tc.id} className="border border-gray-200 rounded-lg overflow-hidden">
                  <div className="bg-gray-50 px-4 py-2 text-sm font-medium text-gray-700">
                    Test Case {index + 1}
                  </div>
                  <div className="grid grid-cols-2 divide-x divide-gray-200">
                    <div className="p-4">
                      <div className="text-xs font-semibold text-gray-500 uppercase mb-1">
                        Input
                      </div>
                      <pre className="text-sm text-gray-800 bg-gray-50 p-2 rounded overflow-x-auto">
                        {tc.input}
                      </pre>
                    </div>
                    <div className="p-4">
                      <div className="text-xs font-semibold text-gray-500 uppercase mb-1">
                        Expected Output
                      </div>
                      <pre className="text-sm text-gray-800 bg-gray-50 p-2 rounded overflow-x-auto">
                        {tc.expectedOutput}
                      </pre>
                    </div>
                  </div>
                  {tc.explanation && (
                    <div className="px-4 py-3 bg-blue-50 text-sm text-blue-800">
                      <span className="font-medium">Note: </span>
                      {tc.explanation}
                    </div>
                  )}
                </div>
              ))}
            </div>
          </section>
        )}
      </div>
    </div>
  );
};

export default DebuggingProblemDetail;
