import React, { useState, useEffect } from 'react';
import Editor from '@monaco-editor/react';
import type { DebuggingProblem, DebuggingRunResponse } from '../../types/debugging';
import { LANGUAGE_CONFIG } from '../../types/coding';
import { debuggingApi } from '../../services/debuggingApi';

interface DebuggingEditorProps {
  problem: DebuggingProblem;
  onSubmissionSuccess?: () => void;
}

export const DebuggingEditor: React.FC<DebuggingEditorProps> = ({
  problem,
  onSubmissionSuccess,
}) => {
  const [code, setCode] = useState<string>('');
  const [customInput, setCustomInput] = useState<string>('');
  const [output, setOutput] = useState<string>('');
  const [isRunning, setIsRunning] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [showInput, setShowInput] = useState(true);

  useEffect(() => {
    setCode(problem.buggyCode);
    setOutput('');
    if (problem.sampleTestCases && problem.sampleTestCases.length > 0) {
      setCustomInput(problem.sampleTestCases[0].input);
    }
  }, [problem]);

  const handleReset = () => {
    setCode(problem.buggyCode);
    setOutput('Code reset to original buggy version');
  };

  const handleRun = async () => {
    if (!customInput.trim()) {
      setOutput('Error: Please provide custom input to run');
      return;
    }

    setIsRunning(true);
    setOutput('Running...');

    try {
      const result: DebuggingRunResponse = await debuggingApi.runCode({
        problemId: problem.id,
        sourceCode: code,
        customInput,
      });

      if (result.success) {
        setOutput(result.output || 'No output');
      } else {
        let errorOutput = `Status: ${result.status}\n`;
        if (result.compileOutput) {
          errorOutput += `\nCompilation Error:\n${result.compileOutput}`;
        }
        if (result.error) {
          errorOutput += `\nError:\n${result.error}`;
        }
        setOutput(errorOutput);
      }
    } catch (error: any) {
      setOutput(`Error: ${error.response?.data?.message || error.message}`);
    } finally {
      setIsRunning(false);
    }
  };

  const handleSubmit = async () => {
    if (!code.trim()) {
      setOutput('Error: Please write some code before submitting');
      return;
    }

    setIsSubmitting(true);
    setOutput('Submitting...');

    try {
      const result = await debuggingApi.submitCode({
        problemId: problem.id,
        sourceCode: code,
      });

      let statusOutput = `Submission ID: ${result.id}\n`;
      statusOutput += `Status: ${result.status}\n`;
      statusOutput += `Tests Passed: ${result.testsPassed}/${result.totalTests}\n`;
      statusOutput += `Points: ${result.pointsEarned}`;

      if (result.compileOutput) {
        statusOutput += `\n\nCompilation Output:\n${result.compileOutput}`;
      }
      if (result.errorMessage) {
        statusOutput += `\n\nError:\n${result.errorMessage}`;
      }

      setOutput(statusOutput);
      onSubmissionSuccess?.();
    } catch (error: any) {
      const message = error.response?.data?.message || error.message;
      setOutput(`Submission failed: ${message}`);
    } finally {
      setIsSubmitting(false);
    }
  };

  const languageConfig = LANGUAGE_CONFIG[problem.language];

  return (
    <div className="bg-white rounded-lg shadow overflow-hidden flex flex-col h-full">
      {/* Toolbar */}
      <div className="flex items-center justify-between px-4 py-2 border-b border-gray-200 bg-gray-50">
        <div className="flex items-center gap-4">
          <span className="px-3 py-1 bg-gray-200 text-gray-700 rounded text-sm font-medium">
            {languageConfig?.name || problem.language}
          </span>
          <button
            onClick={handleReset}
            className="px-3 py-1.5 text-sm text-gray-600 hover:text-gray-800 hover:bg-gray-100 rounded transition-colors"
          >
            Reset Code
          </button>
        </div>

        <div className="flex items-center gap-2">
          <button
            onClick={handleRun}
            disabled={isRunning || isSubmitting}
            className="px-4 py-1.5 bg-gray-600 text-white rounded-md text-sm font-medium hover:bg-gray-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
          >
            {isRunning ? 'Running...' : 'Run'}
          </button>
          <button
            onClick={handleSubmit}
            disabled={isRunning || isSubmitting}
            className="px-4 py-1.5 bg-orange-600 text-white rounded-md text-sm font-medium hover:bg-orange-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
          >
            {isSubmitting ? 'Submitting...' : 'Submit Fix'}
          </button>
        </div>
      </div>

      {/* Editor */}
      <div className="flex-1 min-h-[300px]">
        <Editor
          height="100%"
          language={languageConfig?.monacoLang || 'plaintext'}
          value={code}
          onChange={(value) => setCode(value || '')}
          theme="vs-dark"
          options={{
            minimap: { enabled: false },
            fontSize: 14,
            lineNumbers: 'on',
            scrollBeyondLastLine: false,
            automaticLayout: true,
            tabSize: 4,
            wordWrap: 'on',
          }}
        />
      </div>

      {/* Input/Output Panel */}
      <div className="border-t border-gray-200">
        <div className="flex border-b border-gray-200">
          <button
            onClick={() => setShowInput(true)}
            className={`px-4 py-2 text-sm font-medium ${
              showInput
                ? 'text-orange-600 border-b-2 border-orange-600 bg-white'
                : 'text-gray-500 hover:text-gray-700'
            }`}
          >
            Test Input
          </button>
          <button
            onClick={() => setShowInput(false)}
            className={`px-4 py-2 text-sm font-medium ${
              !showInput
                ? 'text-orange-600 border-b-2 border-orange-600 bg-white'
                : 'text-gray-500 hover:text-gray-700'
            }`}
          >
            Output
          </button>
        </div>

        <div className="h-32">
          {showInput ? (
            <textarea
              value={customInput}
              onChange={(e) => setCustomInput(e.target.value)}
              placeholder="Enter test input..."
              className="w-full h-full p-3 text-sm font-mono resize-none focus:outline-none"
            />
          ) : (
            <pre className="w-full h-full p-3 text-sm font-mono overflow-auto bg-gray-900 text-gray-100">
              {output || 'Output will appear here...'}
            </pre>
          )}
        </div>
      </div>
    </div>
  );
};

export default DebuggingEditor;
