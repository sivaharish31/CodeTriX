import React, { useState, useCallback } from 'react';
import Editor from '@monaco-editor/react';
import type { Language, RunCodeResponse } from '../../types/coding';
import { LANGUAGE_CONFIG, DEFAULT_CODE } from '../../types/coding';
import { codingApi } from '../../services/codingApi';

interface CodeEditorProps {
  problemId: number;
  onSubmissionSuccess?: () => void;
}

export const CodeEditor: React.FC<CodeEditorProps> = ({
  problemId,
  onSubmissionSuccess,
}) => {
  const [language, setLanguage] = useState<Language>('CPP');
  const [code, setCode] = useState<string>(DEFAULT_CODE.CPP);
  const [customInput, setCustomInput] = useState<string>('');
  const [output, setOutput] = useState<string>('');
  const [isRunning, setIsRunning] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [showInput, setShowInput] = useState(true);

  const handleLanguageChange = useCallback((newLang: Language) => {
    setLanguage(newLang);
    setCode(DEFAULT_CODE[newLang]);
  }, []);

  const handleRun = async () => {
    if (!customInput.trim()) {
      setOutput('Error: Please provide custom input to run');
      return;
    }

    setIsRunning(true);
    setOutput('Running...');

    try {
      const result: RunCodeResponse = await codingApi.runCode({
        problemId,
        language: language.toLowerCase(),
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
      const result = await codingApi.submitCode({
        problemId,
        language: language.toLowerCase(),
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

  return (
    <div className="bg-white rounded-lg shadow overflow-hidden flex flex-col h-full">
      {/* Toolbar */}
      <div className="flex items-center justify-between px-4 py-2 border-b border-gray-200 bg-gray-50">
        <div className="flex items-center gap-4">
          <select
            value={language}
            onChange={(e) => handleLanguageChange(e.target.value as Language)}
            className="px-3 py-1.5 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            {Object.entries(LANGUAGE_CONFIG).map(([key, config]) => (
              <option key={key} value={key}>
                {config.name}
              </option>
            ))}
          </select>
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
            className="px-4 py-1.5 bg-green-600 text-white rounded-md text-sm font-medium hover:bg-green-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
          >
            {isSubmitting ? 'Submitting...' : 'Submit'}
          </button>
        </div>
      </div>

      {/* Editor */}
      <div className="flex-1 min-h-[300px]">
        <Editor
          height="100%"
          language={LANGUAGE_CONFIG[language].monacoLang}
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
                ? 'text-blue-600 border-b-2 border-blue-600 bg-white'
                : 'text-gray-500 hover:text-gray-700'
            }`}
          >
            Custom Input
          </button>
          <button
            onClick={() => setShowInput(false)}
            className={`px-4 py-2 text-sm font-medium ${
              !showInput
                ? 'text-blue-600 border-b-2 border-blue-600 bg-white'
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
              placeholder="Enter custom input for testing..."
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

export default CodeEditor;
