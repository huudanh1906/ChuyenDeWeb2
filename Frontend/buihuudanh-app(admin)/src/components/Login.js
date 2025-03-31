import React, { useState } from 'react';
import axios from 'axios';
import { Link } from 'react-router-dom';

const Login = ({ setAuth }) => {
  const [usernameOrEmail, setUsernameOrEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState(null);

  const handleLogin = async (e) => {
    e.preventDefault();
    console.log('Submitting login form with:', { usernameOrEmail, password });

    try {
      // Sử dụng API đăng nhập admin từ backend Java với cấu hình CORS
      console.log('Sending request to API endpoint:', 'http://localhost:8080/api/auth/admin/login');

      const response = await axios.post('http://localhost:8080/api/auth/admin/login',
        {
          usernameOrEmail,
          password
        },
        {
          headers: {
            'Content-Type': 'application/json',
          },
          withCredentials: true
        }
      );

      console.log('API response:', response.data);

      // Kiểm tra kết quả từ response
      if (response.data.success) {
        const { token, user } = response.data;

        // Lưu thông tin vào localStorage
        localStorage.setItem('authToken', token);
        localStorage.setItem('user', JSON.stringify(user));

        // Thông báo đăng nhập thành công
        console.log('Login successful!', { token, user });

        // Cập nhật trạng thái xác thực để chuyển đến trang admin
        setAuth(true);
      } else {
        // Nếu backend trả về success=false
        console.error('Login failed:', response.data);
        setError(response.data.message || 'Đăng nhập không thành công');
      }
    } catch (err) {
      // Xử lý lỗi từ API
      console.error('Login error:', err);

      if (err.response) {
        // Lỗi từ server với status code không phải 2xx
        console.error('Error response from server:', err.response.data);
        setError(err.response.data.message || 'Tên đăng nhập hoặc mật khẩu không chính xác');
      } else if (err.request) {
        // Không nhận được phản hồi từ server
        console.error('No response received:', err.request);
        setError('Không thể kết nối đến máy chủ. Vui lòng thử lại sau.');
      } else {
        // Lỗi khác
        console.error('Unexpected error:', err.message);
        setError('Đã xảy ra lỗi. Vui lòng thử lại.');
      }
    }
  };

  return (
    <div className="flex items-center justify-center min-h-screen bg-gray-100">
      <div className="w-full max-w-md p-8 space-y-6 bg-white rounded-lg shadow-md">
        <h2 className="text-2xl font-bold text-center text-gray-800">Đăng nhập quản trị</h2>
        <form onSubmit={handleLogin} className="space-y-4">
          <div>
            <input
              type="text"
              placeholder="Tên đăng nhập hoặc Email"
              value={usernameOrEmail}
              onChange={(e) => setUsernameOrEmail(e.target.value)}
              required
              className="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-yellow-400"
            />
          </div>
          <div>
            <input
              type="password"
              placeholder="Mật khẩu"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              className="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-yellow-400"
            />
          </div>
          {error && <p className="text-sm text-red-600">{error}</p>}
          <button
            type="submit"
            className="w-full px-4 py-2 font-semibold text-white bg-yellow-400 rounded-lg hover:bg-yellow-500 focus:outline-none focus:ring-2 focus:ring-yellow-400 focus:ring-offset-2"
          >
            Đăng nhập
          </button>
          <Link to="/admin/forgot-password" className="block text-center text-cyan-500 hover:text-cyan-900">
            Quên mật khẩu
          </Link>
        </form>
      </div>
    </div>
  );
};

export default Login;
