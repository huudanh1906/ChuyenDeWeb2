import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import axios from 'axios';

const CategoryTrash = () => {
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  // URL cơ sở cho API backend
  const API_URL = "http://localhost:8080/api";
  // URL cho hình ảnh
  const IMAGE_BASE_URL = "http://localhost:8080/uploads/categories/";

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
      localStorage.removeItem('authToken');
      window.location.href = '/login';
    } else {
      setError(`Lỗi: ${error.response?.data?.message || error.message || 'Không xác định'}`);
    }
    setLoading(false);
  };

  // Kiểm tra và chuyển hướng nếu không có token
  useEffect(() => {
    const token = getAuthToken();
    if (!token) {
      navigate('/login');
      return;
    }
  }, [navigate]);

  useEffect(() => {
    const fetchTrashedCategories = async () => {
      // Kiểm tra token trước khi gọi API
      const token = getAuthToken();
      if (!token) {
        setError('Bạn cần đăng nhập để xem danh mục đã xóa.');
        setLoading(false);
        navigate('/login');
        return;
      }

      try {
        setLoading(true);
        // Fetch các danh mục đã xóa (status = 0)
        const response = await axios.get(`${API_URL}/categories/trash`, getAuthHeaders());
        console.log("Trashed categories data:", response.data);
        setCategories(response.data);
        setLoading(false);
      } catch (error) {
        console.error('Error fetching trashed categories:', error);
        handleError(error);
      }
    };

    fetchTrashedCategories();
  }, [navigate]);

  const handleRestore = async (id) => {
    // Kiểm tra token trước khi gọi API
    const token = getAuthToken();
    if (!token) {
      alert('Bạn cần đăng nhập để khôi phục danh mục.');
      navigate('/login');
      return;
    }

    try {
      // Gọi API khôi phục danh mục từ thùng rác
      await axios.put(`${API_URL}/categories/${id}/restore`, {}, getAuthHeaders());
      // Cập nhật UI bằng cách loại bỏ danh mục đã khôi phục khỏi danh sách
      setCategories(categories.filter(category => category.id !== id));
      alert('Khôi phục danh mục thành công');
    } catch (error) {
      console.error('Error restoring category:', error);
      if (error.response && error.response.status === 401) {
        alert('Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại.');
        localStorage.removeItem('authToken');
        navigate('/login');
      } else {
        alert(`Lỗi khi khôi phục danh mục: ${error.response?.data?.message || error.message || 'Không xác định'}`);
      }
    }
  };

  const handleDestroy = async (id) => {
    // Xác nhận trước khi xóa vĩnh viễn
    if (!window.confirm('Bạn có chắc chắn muốn xóa vĩnh viễn danh mục này? Thao tác này không thể hoàn tác.')) {
      return;
    }

    // Kiểm tra token trước khi gọi API
    const token = getAuthToken();
    if (!token) {
      alert('Bạn cần đăng nhập để xóa vĩnh viễn danh mục.');
      navigate('/login');
      return;
    }

    try {
      // Gọi API xóa vĩnh viễn danh mục
      await axios.delete(`${API_URL}/categories/${id}`, getAuthHeaders());
      // Cập nhật UI bằng cách loại bỏ danh mục đã xóa khỏi danh sách
      setCategories(categories.filter(category => category.id !== id));
      alert('Xóa vĩnh viễn danh mục thành công');
    } catch (error) {
      console.error('Error permanently deleting category:', error);
      if (error.response && error.response.status === 401) {
        alert('Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại.');
        localStorage.removeItem('authToken');
        navigate('/login');
      } else {
        alert(`Lỗi khi xóa vĩnh viễn danh mục: ${error.response?.data?.message || error.message || 'Không xác định'}`);
      }
    }
  };

  if (loading) return <div className="text-center p-4">Đang tải danh sách danh mục đã xóa...</div>;
  if (error) return <div className="text-center p-4 text-red-500">{error}</div>;

  return (
    <div className="container mx-60 py-8">
      <h1 className="text-2xl font-bold mb-4">Danh mục trong thùng rác</h1>

      <div className="flex justify-end mb-4">
        <Link to="/admin/categories" className="bg-green-500 hover:bg-green-600 text-white font-bold py-2 px-4 rounded">
          <i className="fas fa-arrow-left mr-2"></i>Về Danh sách
        </Link>
      </div>

      {categories.length === 0 ? (
        <div className="bg-white shadow-md rounded-lg p-6 text-center">
          <p className="text-gray-500">Không có danh mục nào trong thùng rác</p>
        </div>
      ) : (
        <div className="bg-white shadow-md rounded-lg overflow-hidden">
          <table className="min-w-full table-auto">
            <thead className="bg-gray-200">
              <tr>
                <th className="px-4 py-2 text-left">#</th>
                <th className="px-4 py-2 text-left">Hình</th>
                <th className="px-4 py-2 text-left">Tên danh mục</th>
                <th className="px-4 py-2 text-center">Hoạt động</th>
                <th className="px-4 py-2 text-left">ID</th>
              </tr>
            </thead>
            <tbody>
              {categories.map((category, index) => (
                <tr key={category.id} className="border-b hover:bg-gray-50">
                  <td className="px-4 py-2">
                    <input type="checkbox" />
                  </td>
                  <td className="px-4 py-2">
                    <img
                      src={`${IMAGE_BASE_URL}${category.image}`}
                      alt={category.name}
                      className="w-12 h-12 object-cover rounded"
                      onError={(e) => {
                        e.target.onerror = null;
                        e.target.src = 'https://via.placeholder.com/150?text=No+Image';
                      }}
                    />
                  </td>
                  <td className="px-4 py-2">{category.name}</td>
                  <td className="px-4 py-2 text-center space-x-2">
                    <button
                      onClick={() => handleRestore(category.id)}
                      className="bg-blue-500 hover:bg-blue-600 text-white py-1 px-3 rounded"
                    >
                      <i className="fas fa-trash-restore-alt mr-1"></i>Khôi phục
                    </button>
                    <Link
                      to={`/admin/categories/show/${category.id}`}
                      className="bg-green-500 hover:bg-green-600 text-white py-1 px-3 rounded"
                    >
                      <i className="fas fa-eye mr-1"></i>Xem
                    </Link>
                    <button
                      onClick={() => handleDestroy(category.id)}
                      className="bg-red-500 hover:bg-red-600 text-white py-1 px-3 rounded"
                    >
                      <i className="fas fa-trash mr-1"></i>Xóa vĩnh viễn
                    </button>
                  </td>
                  <td className="px-4 py-2">{category.id}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
};

export default CategoryTrash;
