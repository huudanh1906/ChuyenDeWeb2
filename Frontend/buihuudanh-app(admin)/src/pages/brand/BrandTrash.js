import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { Link, useNavigate } from 'react-router-dom';

const BrandTrash = () => {
  const [brands, setBrands] = useState([]);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  // API base URL
  const API_BASE_URL = 'http://localhost:8080/api';
  // URL cho hình ảnh
  const IMAGE_BASE_URL = 'http://localhost:8080/uploads/brands/';

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

  // Xử lý lỗi chung
  const handleError = (error) => {
    console.error('Error:', error);
    if (error.response && error.response.status === 401) {
      setError('Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại.');
      // Có thể chuyển hướng về trang đăng nhập ở đây
      localStorage.removeItem('authToken');
      setTimeout(() => navigate('/login'), 2000);
    } else if (error.response && error.response.status === 403) {
      setError('Bạn không có quyền truy cập trang này. Cần quyền ADMIN.');
      setTimeout(() => navigate('/admin/dashboard'), 2000);
    } else {
      setError('Không thể tải dữ liệu. Vui lòng thử lại sau.');
    }
    setLoading(false);
  };

  // Hàm kiểm tra và trả về URL hình ảnh hợp lệ
  const getImageUrl = (imageName) => {
    if (!imageName) return null;
    return `${IMAGE_BASE_URL}${imageName}`;
  };

  useEffect(() => {
    const fetchBrands = async () => {
      try {
        // Clear any previous messages or errors
        setMessage('');
        setError('');
        setLoading(true);

        // Kiểm tra xem đã đăng nhập chưa
        const token = getAuthToken();
        if (!token) {
          setError('Bạn cần đăng nhập để xem danh sách thương hiệu trong thùng rác.');
          setLoading(false);
          setTimeout(() => navigate('/login'), 2000);
          return;
        }

        // Fetch trashed brands
        const response = await axios.get(`${API_BASE_URL}/brands/trash`, getAuthHeaders());
        setBrands(response.data);
        setLoading(false);
      } catch (error) {
        console.error('Error fetching trashed brands:', error);
        handleError(error);
      }
    };

    fetchBrands();
  }, [navigate]);

  const handleRestore = async (id) => {
    try {
      // Clear any previous messages or errors
      setMessage('');
      setError('');

      // Kiểm tra xem đã đăng nhập chưa
      const token = getAuthToken();
      if (!token) {
        setError('Bạn cần đăng nhập để khôi phục thương hiệu.');
        setTimeout(() => navigate('/login'), 2000);
        return;
      }

      await axios.put(`${API_BASE_URL}/brands/${id}/restore`, {}, getAuthHeaders());

      setBrands(brands.filter(brand => brand.id !== id));
      setMessage('Thương hiệu đã được khôi phục thành công!');
    } catch (error) {
      console.error('Error restoring brand:', error);
      handleError(error);
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Bạn có chắc chắn muốn xóa vĩnh viễn thương hiệu này?')) {
      try {
        // Clear any previous messages or errors
        setMessage('');
        setError('');

        // Kiểm tra xem đã đăng nhập chưa
        const token = getAuthToken();
        if (!token) {
          setError('Bạn cần đăng nhập để xóa vĩnh viễn thương hiệu.');
          setTimeout(() => navigate('/login'), 2000);
          return;
        }

        await axios.delete(`${API_BASE_URL}/brands/${id}`, getAuthHeaders());

        setBrands(brands.filter(brand => brand.id !== id));
        setMessage('Thương hiệu đã được xóa vĩnh viễn!');
      } catch (error) {
        console.error('Error permanently deleting brand:', error);
        handleError(error);
      }
    }
  };

  return (
    <div className="container mx-60 p-6">
      <div className="mb-4 text-right">
        <Link to="/admin/brands" className="bg-green-500 text-white px-4 py-2 rounded">
          <i className="fas fa-arrow-left mr-2"></i>Về Danh sách
        </Link>
      </div>

      {/* Display success message */}
      {message && (
        <div className="bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded mb-4">
          {message}
        </div>
      )}

      {/* Display error message */}
      {error && (
        <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
          {error}
        </div>
      )}

      {loading ? (
        <div className="text-center p-4">Đang tải danh sách thương hiệu...</div>
      ) : (
        <div className="overflow-x-auto">
          <table className="min-w-full bg-white border border-gray-200">
            <thead>
              <tr className="bg-gray-100">
                <th className="px-4 py-2 border-b">#</th>
                <th className="px-4 py-2 border-b">Hình</th>
                <th className="px-4 py-2 border-b">Tên thương hiệu</th>
                <th className="px-4 py-2 border-b text-center">Hoạt động</th>
                <th className="px-4 py-2 border-b">ID</th>
              </tr>
            </thead>
            <tbody>
              {brands.length > 0 ? (
                brands.map((brand, index) => (
                  <tr key={brand.id}>
                    <td className="px-4 py-2 border-b">
                      <input type="checkbox" />
                    </td>
                    <td className="px-4 py-2 border-b">
                      {brand.image ? (
                        <img
                          src={getImageUrl(brand.image)}
                          alt={brand.name}
                          className="w-12 h-12 object-cover"
                          onError={(e) => {
                            // Nếu hình ảnh không tải được, hiển thị placeholder
                            e.target.onerror = null;
                            e.target.src = 'https://via.placeholder.com/48?text=No+Image';
                          }}
                        />
                      ) : (
                        <div className="w-12 h-12 bg-gray-200 flex items-center justify-center">
                          <span className="text-gray-500">{brand.name?.charAt(0).toUpperCase() || 'B'}</span>
                        </div>
                      )}
                    </td>
                    <td className="px-4 py-2 border-b">{brand.name}</td>
                    <td className="px-4 py-2 border-b text-center">
                      <button
                        onClick={() => handleRestore(brand.id)}
                        className="bg-blue-500 text-white px-2 py-1 rounded mr-1"
                      >
                        <i className="fas fa-trash-restore-alt mr-1"></i>Khôi phục
                      </button>
                      <Link
                        to={`/admin/brands/show/${brand.id}`}
                        className="bg-green-500 text-white px-2 py-1 rounded mx-1"
                      >
                        <i className="fas fa-eye mr-1"></i>Xem
                      </Link>
                      <button
                        onClick={() => handleDelete(brand.id)}
                        className="bg-red-500 text-white px-2 py-1 rounded ml-1"
                      >
                        <i className="fas fa-trash mr-1"></i>Xóa vĩnh viễn
                      </button>
                    </td>
                    <td className="px-4 py-2 border-b">{brand.id}</td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan="5" className="px-4 py-2 text-center border-b">
                    Không có thương hiệu nào trong thùng rác
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
};

export default BrandTrash;
