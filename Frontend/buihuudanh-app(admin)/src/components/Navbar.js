import React from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

const Navbar = () => {
  const navigate = useNavigate();

  // Handle logout functionality
  const handleLogout = () => {
    const token = localStorage.getItem('authToken');
    console.log('Token retrieved for logout:', token);

    if (token) {
      // Perform logout action, such as deleting token from localStorage
      localStorage.removeItem('authToken');
      localStorage.removeItem('user'); // Optional: Remove user data too

      // Optionally, redirect user to login page or update state
      window.location.href = '/';  // Redirect to login page
    } else {
      console.log('No token found for logout');
    }
  };


  return (
    <nav className="bg-white shadow p-4 flex justify-between items-center">
      <div className="flex space-x-4">
        <button className="text-gray-500 hover:text-gray-700">
          <i className="fas fa-bars"></i>
        </button>
        <a href="/" className="text-gray-700 hover:text-gray-900">Home</a>
        <a href="#" className="text-gray-700 hover:text-gray-900">Contact</a>
      </div>
      <div className="flex space-x-4">
        <a href="#" className="text-gray-700 hover:text-gray-900">
          <i className="far fa-user"></i> Quản lý
        </a>
        <button onClick={handleLogout} className="hover:text-red-600">Đăng xuất</button>
      </div>
    </nav>
  );
};

export default Navbar;
