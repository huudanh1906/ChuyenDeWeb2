import React, { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import axios from 'axios';

const CategoryDetail = () => {
  const { id } = useParams(); // Get the category ID from URL params
  const [category, setCategory] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // URL cơ sở cho API backend
  const API_BASE_URL = 'http://localhost:8080/api';
  // URL cho hình ảnh
  const IMAGE_BASE_URL = 'http://localhost:8080/uploads/categories/';

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
      window.location.href = '/login';
    } else if (error.response && error.response.status === 404) {
      setError('Không tìm thấy danh mục này.');
    } else {
      setError('Không thể tải thông tin danh mục. Vui lòng thử lại sau.');
    }
    setLoading(false);
  };

  useEffect(() => {
    const fetchCategoryDetail = async () => {
      // Kiểm tra xem đã đăng nhập chưa
      const token = getAuthToken();
      if (!token) {
        setError('Bạn cần đăng nhập để xem thông tin danh mục.');
        setLoading(false);
        return;
      }

      setLoading(true);
      try {
        const response = await axios.get(`${API_BASE_URL}/categories/${id}`, getAuthHeaders());
        setCategory(response.data);
        setLoading(false);
      } catch (err) {
        console.error('Error details:', err);
        handleError(err);
      }
    };

    fetchCategoryDetail();
  }, [id]);

  if (loading) return <div className="text-center p-4">Loading category details...</div>;
  if (error) return <div className="text-center p-4 text-red-500">{error}</div>;
  if (!category) return <div className="text-center p-4">Không tìm thấy danh mục.</div>;

  // Hiển thị ngày tháng dạng locale
  const formatDate = (dateString) => {
    if (!dateString) return 'N/A';
    return new Date(dateString).toLocaleString();
  };

  return (
    <div className="container mx-60 py-8 px-8">
      <div className="flex justify-between items-center mb-4">
        <h2 className="text-2xl font-semibold">Tên danh mục: {category.name}</h2>
        <Link to="/admin/categories" className="bg-green-500 hover:bg-green-600 text-white font-bold py-2 px-4 rounded">
          <i className="fas fa-arrow-left mr-2"></i>Về Danh sách
        </Link>
      </div>

      <div className="bg-white shadow-md rounded-lg p-4">
        <div className="flex">
          <div className="w-1/4">
            {category.image ? (
              <img
                src={`${IMAGE_BASE_URL}${category.image}`}
                alt={category.name}
                className="w-32 h-32 object-cover rounded"
                onError={(e) => {
                  e.target.onerror = null;
                  e.target.src = 'http://localhost:8080/uploads/default-category.jpg';
                }}
              />
            ) : (
              <div className="w-32 h-32 bg-gray-200 flex items-center justify-center rounded">
                <span className="text-gray-500">No Image</span>
              </div>
            )}
          </div>
          <div className="w-3/4 ml-4">
            <p className="text-lg"><strong>ID:</strong> {category.id}</p>
            <p className="text-lg"><strong>Miêu tả:</strong> {category.description || 'Không có mô tả'}</p>
            <p className="text-lg"><strong>Slug:</strong> {category.slug}</p>
            <p className="text-lg"><strong>Parent ID:</strong> {category.parentId}</p>
            <p className="text-lg"><strong>Sort Order:</strong> {category.sortOrder}</p>
            <p className="text-lg"><strong>Status:</strong> {category.status === 1 ? 'Active' : category.status === 2 ? 'Inactive' : 'Trashed'}</p>
            <p className="text-lg"><strong>Created At:</strong> {formatDate(category.createdAt)}</p>
            <p className="text-lg"><strong>Updated At:</strong> {formatDate(category.updatedAt)}</p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default CategoryDetail;
