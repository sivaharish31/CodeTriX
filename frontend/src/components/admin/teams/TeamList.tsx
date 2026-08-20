import React, { useState, useEffect } from 'react';
import { adminApi } from '../../../services/adminApi';
import type { TeamInfo, TeamDetail } from '../../../types/admin';

export const TeamList: React.FC = () => {
  const [teams, setTeams] = useState<TeamInfo[]>([]);
  const [selectedTeam, setSelectedTeam] = useState<TeamDetail | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    loadTeams();
  }, []);

  const loadTeams = async () => {
    try {
      setLoading(true);
      const data = await adminApi.getTeams();
      setTeams(data);
      setError(null);
    } catch (err) {
      setError('Failed to load teams');
    } finally {
      setLoading(false);
    }
  };

  const loadTeamDetail = async (teamId: number) => {
    try {
      const detail = await adminApi.getTeamDetail(teamId);
      setSelectedTeam(detail);
    } catch (err) {
      console.error('Failed to load team details:', err);
    }
  };

  const handleDeleteTeam = async (teamId: number) => {
    if (!confirm('Are you sure you want to delete this team?')) return;
    try {
      await adminApi.deleteTeam(teamId);
      loadTeams();
      if (selectedTeam?.id === teamId) {
        setSelectedTeam(null);
      }
    } catch (err) {
      alert('Failed to delete team');
    }
  };

  if (loading) {
    return <div className="p-8 text-center text-gray-500">Loading teams...</div>;
  }

  if (error) {
    return <div className="p-8 text-center text-red-500">{error}</div>;
  }

  return (
    <div className="grid grid-cols-3 gap-6">
      {/* Team List */}
      <div className="col-span-2 bg-white rounded-lg shadow overflow-hidden">
        <div className="px-4 py-3 bg-gray-50 border-b flex items-center justify-between">
          <h3 className="font-semibold text-gray-800">Teams ({teams.length}/27)</h3>
          <button
            onClick={loadTeams}
            className="text-sm text-blue-600 hover:text-blue-800"
          >
            Refresh
          </button>
        </div>

        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Code</th>
                <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Name</th>
                <th className="px-4 py-3 text-center text-xs font-medium text-gray-500 uppercase">Members</th>
                <th className="px-4 py-3 text-center text-xs font-medium text-gray-500 uppercase">Status</th>
                <th className="px-4 py-3 text-right text-xs font-medium text-gray-500 uppercase">Actions</th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {teams.map((team) => (
                <tr
                  key={team.id}
                  className={`hover:bg-gray-50 cursor-pointer ${
                    selectedTeam?.id === team.id ? 'bg-blue-50' : ''
                  }`}
                  onClick={() => loadTeamDetail(team.id)}
                >
                  <td className="px-4 py-3 whitespace-nowrap">
                    <span className="font-mono text-sm bg-gray-100 px-2 py-1 rounded">
                      {team.teamCode}
                    </span>
                  </td>
                  <td className="px-4 py-3 whitespace-nowrap font-medium text-gray-800">
                    {team.teamName}
                  </td>
                  <td className="px-4 py-3 whitespace-nowrap text-center text-gray-600">
                    {team.memberCount}
                  </td>
                  <td className="px-4 py-3 whitespace-nowrap text-center">
                    <span className={`px-2 py-1 rounded-full text-xs font-medium ${
                      team.status === 'ACTIVE' ? 'bg-green-100 text-green-800' :
                      team.status === 'REGISTERED' ? 'bg-blue-100 text-blue-800' :
                      'bg-red-100 text-red-800'
                    }`}>
                      {team.status}
                    </span>
                  </td>
                  <td className="px-4 py-3 whitespace-nowrap text-right">
                    <button
                      onClick={(e) => {
                        e.stopPropagation();
                        handleDeleteTeam(team.id);
                      }}
                      className="text-red-600 hover:text-red-800 text-sm"
                    >
                      Delete
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Team Detail */}
      <div className="col-span-1">
        {selectedTeam ? (
          <div className="bg-white rounded-lg shadow overflow-hidden">
            <div className="px-4 py-3 bg-blue-50 border-b">
              <h3 className="font-semibold text-gray-800">{selectedTeam.teamName}</h3>
              <p className="text-sm text-gray-500">Code: {selectedTeam.teamCode}</p>
            </div>

            {/* Scores */}
            <div className="p-4 border-b">
              <h4 className="text-sm font-semibold text-gray-500 mb-2">Scores</h4>
              <div className="grid grid-cols-2 gap-2 text-sm">
                <div>Coding: <span className="font-semibold text-blue-600">{selectedTeam.codingScore}</span></div>
                <div>Debugging: <span className="font-semibold text-orange-600">{selectedTeam.debuggingScore}</span></div>
                <div>CTF: <span className="font-semibold text-teal-600">{selectedTeam.ctfScore}</span></div>
                <div>Total: <span className="font-bold text-purple-700">{selectedTeam.totalScore}</span></div>
              </div>
              {selectedTeam.violationCount > 0 && (
                <div className="mt-2 text-sm text-red-600">
                  ⚠️ {selectedTeam.violationCount} violations
                </div>
              )}
            </div>

            {/* Members */}
            <div className="p-4">
              <h4 className="text-sm font-semibold text-gray-500 mb-2">Members</h4>
              <div className="space-y-3">
                {selectedTeam.members.map((member) => (
                  <div key={member.id} className="text-sm">
                    <div className="font-medium text-gray-800">{member.name}</div>
                    <div className="text-gray-500">{member.rollNumber}</div>
                    <div className="text-gray-400 text-xs">{member.college}</div>
                  </div>
                ))}
              </div>
            </div>
          </div>
        ) : (
          <div className="bg-white rounded-lg shadow p-8 text-center text-gray-500">
            Select a team to view details
          </div>
        )}
      </div>
    </div>
  );
};

export default TeamList;
