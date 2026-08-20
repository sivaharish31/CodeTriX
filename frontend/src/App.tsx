import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AdminLogin } from './pages/auth/AdminLogin';
import { TeamLogin } from './pages/auth/TeamLogin';
import { AdminDashboard } from './pages/admin/AdminDashboard';

const ProtectedRoute: React.FC<{ children: React.ReactNode; requiredRole: 'ADMIN' | 'TEAM' }> = ({
  children,
  requiredRole,
}) => {
  const token = localStorage.getItem('token');
  const role = localStorage.getItem('role');

  if (!token) {
    return <Navigate to={requiredRole === 'ADMIN' ? '/admin/login' : '/team/login'} replace />;
  }

  if (role !== requiredRole) {
    return <Navigate to="/" replace />;
  }

  return <>{children}</>;
};

const App: React.FC = () => {
  return (
    <BrowserRouter>
      <Routes>
        {/* Public Routes */}
        <Route path="/" element={<Navigate to="/admin/login" replace />} />
        <Route path="/admin/login" element={<AdminLogin />} />
        <Route path="/team/login" element={<TeamLogin />} />

        {/* Admin Routes */}
        <Route
          path="/admin/dashboard"
          element={
            <ProtectedRoute requiredRole="ADMIN">
              <AdminDashboard />
            </ProtectedRoute>
          }
        />

        {/* Catch all */}
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  );
};

export default App;
