import React, { useEffect, useState } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import axios from 'axios';

const UserDetail = () => {
  const { id } = useParams();
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  // URL cơ sở cho API backend
  const API_BASE_URL = 'http://localhost:8080/api';
  // URL cho hình ảnh
  const IMAGE_BASE_URL = 'http://localhost:8080/uploads/users/';

  // Lấy token xác thực từ localStorage
  const getAuthToken = () => {
    return localStorage.getItem('authToken');
  };

  // Cấu hình headers cho request với token xác thực
  const getAuthHeaders = () => {
    const token = getAuthToken();
    return {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    };
  };

  useEffect(() => {
    const fetchUser = async () => {
      const token = getAuthToken();
      if (!token) {
        setError('Bạn cần đăng nhập để xem thông tin người dùng.');
        setLoading(false);
        return;
      }

      try {
        setLoading(true);
        const response = await axios.get(`${API_BASE_URL}/users/${id}`, getAuthHeaders());
        setUser(response.data);
        setLoading(false);
      } catch (error) {
        console.error('Error fetching user data:', error);
        setLoading(false);

        if (error.response && error.response.status === 401) {
          alert('Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại.');
          localStorage.removeItem('authToken');
          setTimeout(() => navigate('/login'), 2000);
        } else if (error.response && error.response.status === 404) {
          setError('Không tìm thấy người dùng.');
        } else {
          setError('Không thể tải thông tin người dùng. Vui lòng thử lại sau.');
        }
      }
    };

    fetchUser();
  }, [id, navigate]);

  // Hàm kiểm tra và trả về URL hình ảnh hợp lệ
  const getImageUrl = (imageName) => {
    if (!imageName) return null;
    return `${IMAGE_BASE_URL}${imageName}`;
  };

  if (loading) {
    return <div className="text-center p-4">Đang tải thông tin người dùng...</div>;
  }

  if (error) {
    return <div className="text-center p-4 text-red-500">{error}</div>;
  }

  if (!user) {
    return <div className="text-center p-4">Không tìm thấy người dùng</div>;
  }

  return (
    <div className="container mx-60 p-4">
      <div className="card bg-white shadow-lg rounded-lg overflow-hidden">
        <div className="card-header flex justify-between items-center p-4 border-b">
          <h2 className="text-xl font-bold">Tên người dùng: {user.name}</h2>
          <Link to="/admin/user" className="btn bg-green-500 text-white px-4 py-2 rounded">
            <i className="fas fa-arrow-left mr-2"></i>Về Danh sách
          </Link>
        </div>
        <div className="flex m-4">
          <div className="w-1/3">
            {user.image ? (
              <img
                src={getImageUrl(user.image)}
                alt={user.name}
                className="img-fluid w-36 h-36 object-cover rounded-full"
                onError={(e) => {
                  e.target.onerror = null;
                  e.target.src = 'https://via.placeholder.com/150?text=No+Image';
                }}
              />
            ) : (
              <div className="w-36 h-36 bg-gray-200 rounded-full flex items-center justify-center">
                <span className="text-gray-500 text-2xl">{user.name?.charAt(0).toUpperCase() || 'U'}</span>
              </div>
            )}
          </div>
          <div className="w-2/3 pl-4">
            <p className="mb-2"><span className="font-bold">ID:</span> {user.id}</p>
            <p className="mb-2"><span className="font-bold">Tên:</span> {user.name}</p>
            <p className="mb-2"><span className="font-bold">Tên đăng nhập:</span> {user.username}</p>
            <p className="mb-2"><span className="font-bold">Email:</span> {user.email}</p>
            <p className="mb-2"><span className="font-bold">Số điện thoại:</span> {user.phone}</p>
            <p className="mb-2"><span className="font-bold">Địa chỉ:</span> {user.address}</p>
            <p className="mb-2"><span className="font-bold">Vai trò:</span> {user.roles}</p>
            <p className="mb-2"><span className="font-bold">Giới tính:</span> {user.gender === 1 ? 'Nam' : 'Nữ'}</p>
            <p className="mb-2"><span className="font-bold">Trạng thái:</span> {user.status === 1 ? 'Hoạt động' : (user.status === 0 ? 'Đã xóa' : 'Không hoạt động')}</p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default UserDetail;
