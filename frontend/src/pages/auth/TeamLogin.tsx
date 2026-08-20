import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

export const TeamLogin: React.FC = () => {
  const [teamCode, setTeamCode] = useState('');
  const [loginPin, setLoginPin] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const response = await axios.post('/api/auth/team/login', {
        teamCode,
        loginPin,
      });

      const { token, team } = response.data;

      localStorage.setItem('token', token);
      localStorage.setItem('role', 'TEAM');
      localStorage.setItem('team', JSON.stringify(team));

      navigate('/team/dashboard');
    } catch (err: any) {
      setError(err.response?.data?.message || 'Login failed. Please check your credentials.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-900 via-purple-900 to-gray-900 flex items-center justify-center p-4">
      <div className="w-full max-w-md">
        {/* Logo */}
        <div className="text-center mb-8">
          <h1 className="text-4xl font-bold text-white mb-2">CodeTriX</h1>
          <p className="text-purple-300">Team Portal</p>
        </div>

        {/* Login Form */}
        <div className="bg-white rounded-xl shadow-2xl p-8">
          <h2 className="text-2xl font-bold text-gray-800 mb-6 text-center">
            Team Login
          </h2>

          {error && (
            <div className="mb-4 p-3 bg-red-50 border border-red-200 text-red-700 rounded-lg text-sm">
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label htmlFor="teamCode" className="block text-sm font-medium text-gray-700 mb-1">
                Team Code
              </label>
              <input
                id="teamCode"
                type="text"
                value={teamCode}
                onChange={(e) => setTeamCode(e.target.value.toUpperCase())}
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent outline-none transition font-mono text-lg tracking-wider"
                placeholder="TEAM-XXXX"
                required
              />
            </div>

            <div>
              <label htmlFor="loginPin" className="block text-sm font-medium text-gray-700 mb-1">
                Login PIN
              </label>
              <input
                id="loginPin"
                type="password"
                value={loginPin}
                onChange={(e) => setLoginPin(e.target.value)}
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent outline-none transition font-mono text-lg tracking-wider text-center"
                placeholder="••••••"
                maxLength={6}
                required
              />
            </div>

            <button
              type="submit"
              disabled={loading}
              className="w-full py-3 bg-gradient-to-r from-purple-600 to-purple-700 text-white font-semibold rounded-lg hover:from-purple-700 hover:to-purple-800 transition-all disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {loading ? 'Signing in...' : 'Enter Competition'}
            </button>
          </form>

          <div className="mt-6 text-center">
            <a
              href="/admin/login"
              className="text-sm text-purple-600 hover:text-purple-800"
            >
              Admin Login →
            </a>
          </div>
        </div>

        {/* Info */}
        <div className="mt-4 text-center text-sm text-gray-400">
          Use the Team Code and PIN provided by your administrator
        </div>
      </div>
    </div>
  );
};

export default TeamLogin;
