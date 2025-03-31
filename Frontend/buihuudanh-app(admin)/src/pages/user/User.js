import React, { useState, useEffect } from 'react';
import axios from 'axios';

const User = () => {
  const [users, setUsers] = useState([]);
  // Thêm state để xử lý lỗi và loading
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // URL cơ sở cho API backend
  const API_BASE_URL = 'http://localhost:8080/api';
  // URL cho hình ảnh - chỉ sử dụng đường dẫn uploads
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

  // Hàm load danh sách users
  const loadUsers = () => {
    // Kiểm tra xem đã đăng nhập chưa
    const token = getAuthToken();
    if (!token) {
      setError('Bạn cần đăng nhập để xem danh sách người dùng.');
      setLoading(false);
      return;
    }

    // Fetch users từ backend Java với token xác thực
    setLoading(true);

    // Lấy users đang active và inactive (không bao gồm trashed)
    axios.get(`${API_BASE_URL}/users`, getAuthHeaders())
      .then(response => {
        setUsers(response.data);
        setLoading(false);
      })
      .catch(error => {
        console.error('There was an error fetching the users!', error);
        handleError(error);
      });
  };

  // Xử lý lỗi chung
  const handleError = (error) => {
    console.error('Error:', error);
    if (error.response && error.response.status === 401) {
      setError('Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại.');
      // Có thể chuyển hướng về trang đăng nhập ở đây
      localStorage.removeItem('authToken');
      window.location.href = '/login';
    } else {
      setError('Không thể tải danh sách người dùng. Vui lòng thử lại sau.');
    }
    setLoading(false);
  };

  useEffect(() => {
    loadUsers();
  }, []);

  // Hàm kiểm tra và trả về URL hình ảnh hợp lệ
  const getImageUrl = (imageName) => {
    if (!imageName) return null;
    // Chỉ sử dụng đường dẫn uploads
    return `${IMAGE_BASE_URL}${imageName}`;
  };

  // Xử lý toggle status
  const toggleStatus = (id, currentStatus) => {
    // Trong Java API, chúng ta sử dụng PUT request với token xác thực
    axios.put(`${API_BASE_URL}/users/${id}/status`, {}, getAuthHeaders())
      .then(response => {
        if (response.data.status) {
          // Nếu có status trong response, sử dụng nó
          const newStatus = parseInt(response.data.status);
          setUsers(users.map(user =>
            user.id === id ? { ...user, status: newStatus } : user
          ));
        } else {
          // Ngược lại, giả định status được toggle giữa 1 (active) và 2 (inactive)
          const newStatus = currentStatus === 1 ? 2 : 1;
          setUsers(users.map(user =>
            user.id === id ? { ...user, status: newStatus } : user
          ));
        }
      })
      .catch(error => {
        console.error('There was an error updating the status!', error);
        if (error.response && error.response.status === 401) {
          alert('Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại.');
          // Có thể chuyển hướng về trang đăng nhập ở đây
          window.location.href = '/login';
        } else {
          alert('Không thể cập nhật trạng thái. Vui lòng thử lại.');
        }
      });
  };

  // Xử lý soft delete
  const deleteUser = (id) => {
    // Soft delete trong Java API với token xác thực
    axios.put(`${API_BASE_URL}/users/${id}/delete`, {}, getAuthHeaders())
      .then(response => {
        // Sau khi xóa mềm, loại bỏ user khỏi danh sách hiển thị
        setUsers(users.filter(user => user.id !== id));
      })
      .catch(error => {
        console.error('There was an error deleting the user!', error);
        if (error.response && error.response.status === 401) {
          alert('Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại.');
          // Có thể chuyển hướng về trang đăng nhập ở đây
          window.location.href = '/login';
        } else {
          alert('Không thể xóa người dùng. Vui lòng thử lại.');
        }
      });
  };

  // Hàm kiểm tra và trả về trạng thái hiển thị của user
  const getUserStatusText = (status) => {
    switch (status) {
      case 0: return "Trashed";
      case 1: return "Active";
      case 2: return "Inactive";
      default: return "Unknown";
    }
  };

  // Hàm kiểm tra và trả về màu sắc cho nút trạng thái
  const getUserStatusClass = (status) => {
    switch (status) {
      case 0: return "bg-gray-500 hover:bg-gray-700";
      case 1: return "bg-green-500 hover:bg-green-700";
      case 2: return "bg-red-500 hover:bg-red-700";
      default: return "bg-gray-500 hover:bg-gray-700";
    }
  };

  if (loading) return <div className="text-center p-4">Loading users...</div>;
  if (error) return <div className="text-center p-4 text-red-500">{error}</div>;

  return (
    <div className="container mx-60 p-4">
      <div className="flex justify-end mb-4">
        <a href="/admin/user/trash" className="btn btn-success bg-green-500 hover:bg-green-700 text-white py-2 px-4 rounded">
          <i className="fas fa-trash mr-2"></i>Thùng rác
        </a>
        <a href="/admin/user/create" className="btn btn-primary bg-blue-500 hover:bg-blue-700 text-white py-2 px-4 ml-4 rounded">
          <i className="fas fa-plus mr-2"></i>Thêm
        </a>
      </div>
      <table className="min-w-full bg-white">
        <thead>
          <tr>
            <th className="py-2 px-4 border">#</th>
            <th className="py-2 px-4 border">Hình</th>
            <th className="py-2 px-4 border">Tên người dùng</th>
            <th className="py-2 px-4 border">Giới tính</th>
            <th className="py-2 px-4 border">Điện thoại</th>
            <th className="py-2 px-4 border">Email</th>
            <th className="py-2 px-4 border">Địa chỉ</th>
            <th className="py-2 px-4 border">Hoạt động</th>
            <th className="py-2 px-4 border">ID</th>
          </tr>
        </thead>
        <tbody>
          {users.map(user => (
            <tr key={user.id} className={user.status === 2 ? 'bg-gray-100' : ''}>
              <td className="py-2 px-4 border"><input type="checkbox" /></td>
              <td className="py-2 px-4 border">
                {user.image ? (
                  <img
                    src={getImageUrl(user.image)}
                    alt={user.name}
                    className="w-12 h-12 object-cover rounded-full"
                    onError={(e) => {
                      // Nếu hình ảnh không tải được, hiển thị placeholder
                      e.target.onerror = null;
                      e.target.src = 'https://via.placeholder.com/150?text=No+Image';
                    }}
                  />
                ) : (
                  <div className="w-12 h-12 bg-gray-200 rounded-full flex items-center justify-center">
                    <span className="text-gray-500">{user.name?.charAt(0).toUpperCase() || 'U'}</span>
                  </div>
                )}
              </td>
              <td className="py-2 px-4 border">{user.name}</td>
              <td className="py-2 px-4 border">{user.gender === 1 ? 'Nam' : 'Nữ'}</td>
              <td className="py-2 px-4 border">{user.phone}</td>
              <td className="py-2 px-4 border">{user.email}</td>
              <td className="py-2 px-4 border">{user.address}</td>
              <td className="py-2 px-4 border text-center">
                <button
                  onClick={() => toggleStatus(user.id, user.status)}
                  className={`px-3 py-2 rounded ${getUserStatusClass(user.status)} text-white`}
                >
                  {getUserStatusText(user.status)}
                </button>
                <a href={`/admin/user/show/${user.id}`} className="px-4 py-2 bg-green-500 hover:bg-green-700 text-white rounded ml-2">
                  <i className="fas fa-eye"></i> View
                </a>
                <a href={`/admin/user/edit/${user.id}`} className="px-4 py-2 bg-blue-500 hover:bg-blue-700 text-white rounded ml-2">
                  <i className="fas fa-edit"></i> Edit
                </a>
                <button
                  onClick={() => deleteUser(user.id)}
                  className="px-4 py-2 bg-red-500 hover:bg-red-700 text-white rounded ml-2"
                >
                  <i className="fas fa-trash"></i> Delete
                </button>
              </td>
              <td className="py-2 px-4 border">{user.id}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default User;
