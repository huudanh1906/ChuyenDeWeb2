import React, { useState, useEffect } from 'react';
import { Routes, Route } from 'react-router-dom';
import Navbar from './components/Navbar';
import Sidebar from './components/Sidebar';
import Footer from './components/Footer';
import Main from './components/Main';
import Login from './components/Login';
import ForgotPassword from './components/ForgotPassword';

const App = () => {
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  useEffect(() => {
    const token = localStorage.getItem('authToken');
    if (token) {
      setIsAuthenticated(true);
    }
  }, []);

  return (
    <Routes>
      {/* Public Routes */}
      <Route path="/admin/forgot-password" element={<ForgotPassword />} />
      <Route path="/login" element={<Login setAuth={setIsAuthenticated} />} />

      {/* Protected Routes */}
      {isAuthenticated ? (
        <Route
          path="*"
          element={
            <div className="flex">
              <Sidebar />
              <div className="flex-1 flex flex-col">
                <Navbar />
                <div className="flex-1">
                  <Main />
                </div>
                <Footer />
              </div>
            </div>
          }
        />
      ) : (
        <Route path="*" element={<Login setAuth={setIsAuthenticated} />} />
      )}
    </Routes>
  );
};

export default App;
