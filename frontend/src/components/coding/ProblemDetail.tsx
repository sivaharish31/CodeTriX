import React from 'react';
import type { Problem } from '../../types/coding';

interface ProblemDetailProps {
  problem: Problem;
}

export const ProblemDetail: React.FC<ProblemDetailProps> = ({ problem }) => {
  return (
    <div className="bg-white rounded-lg shadow overflow-hidden">
      <div className="px-6 py-4 border-b border-gray-200">
        <div className="flex items-center justify-between">
          <h1 className="text-xl font-bold text-gray-900">{problem.title}</h1>
          <span className="px-3 py-1 bg-blue-100 text-blue-800 rounded-full text-sm font-semibold">
            {problem.points} points
          </span>
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

        {problem.constraints && (
          <section>
            <h3 className="text-sm font-semibold text-gray-700 uppercase tracking-wide mb-2">
              Constraints
            </h3>
            <div className="bg-gray-50 rounded-lg p-4 text-sm text-gray-600 whitespace-pre-wrap font-mono">
              {problem.constraints}
            </div>
          </section>
        )}

        {problem.inputFormat && (
          <section>
            <h3 className="text-sm font-semibold text-gray-700 uppercase tracking-wide mb-2">
              Input Format
            </h3>
            <div className="text-gray-600 text-sm whitespace-pre-wrap">
              {problem.inputFormat}
            </div>
          </section>
        )}

        {problem.outputFormat && (
          <section>
            <h3 className="text-sm font-semibold text-gray-700 uppercase tracking-wide mb-2">
              Output Format
            </h3>
            <div className="text-gray-600 text-sm whitespace-pre-wrap">
              {problem.outputFormat}
            </div>
          </section>
        )}

        {problem.sampleTestCases && problem.sampleTestCases.length > 0 && (
          <section>
            <h3 className="text-sm font-semibold text-gray-700 uppercase tracking-wide mb-2">
              Examples
            </h3>
            <div className="space-y-4">
              {problem.sampleTestCases.map((tc, index) => (
                <div key={tc.id} className="border border-gray-200 rounded-lg overflow-hidden">
                  <div className="bg-gray-50 px-4 py-2 text-sm font-medium text-gray-700">
                    Example {index + 1}
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
                        Output
                      </div>
                      <pre className="text-sm text-gray-800 bg-gray-50 p-2 rounded overflow-x-auto">
                        {tc.expectedOutput}
                      </pre>
                    </div>
                  </div>
                  {tc.explanation && (
                    <div className="px-4 py-3 bg-blue-50 text-sm text-blue-800">
                      <span className="font-medium">Explanation: </span>
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

export default ProblemDetail;
