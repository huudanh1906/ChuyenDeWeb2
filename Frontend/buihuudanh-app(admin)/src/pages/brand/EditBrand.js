import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useParams, useNavigate } from 'react-router-dom';

const EditBrand = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [brand, setBrand] = useState({
    name: '',
    description: '',
    sortOrder: 0,
    image: null,
    status: 1,
    oldImage: '',
  });
  const [sortOptions, setSortOptions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [previewImage, setPreviewImage] = useState(null);
  const [selectedFile, setSelectedFile] = useState(null);

  // URL cơ sở cho API backend
  const API_URL = "http://localhost:8080/api";
  // URL cho hình ảnh
  const IMAGE_BASE_URL = "http://localhost:8080/uploads/brands/";

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

  // Cấu hình headers cho request multipart/form-data với token xác thực
  const getFormDataAuthHeaders = () => {
    const token = getAuthToken();
    return {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'multipart/form-data'
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
    } else {
      setError(`Lỗi: ${error.response?.data?.message || error.message || 'Không xác định'}`);
    }
    setLoading(false);
  };

  useEffect(() => {
    // Kiểm tra xem đã đăng nhập chưa
    const token = getAuthToken();
    if (!token) {
      setError('Bạn cần đăng nhập để chỉnh sửa thương hiệu.');
      setLoading(false);
      navigate('/login');
      return;
    }

    const fetchData = async () => {
      try {
        setLoading(true);

        // Fetch brand detail
        const brandResponse = await axios.get(`${API_URL}/brands/${id}`, getAuthHeaders());

        // Cập nhật state từ dữ liệu API Java
        const brandData = brandResponse.data;
        setBrand({
          name: brandData.name || '',
          description: brandData.description || '',
          sortOrder: brandData.sortOrder || 0,
          status: brandData.status || 1,
          // Lưu lại tên file hình ảnh cũ để có thể gửi lên server khi cập nhật
          oldImage: brandData.image || ''
        });

        if (brandData.image) {
          setPreviewImage(`${IMAGE_BASE_URL}${brandData.image}`);
        }

        // Fetch sort options (all brands for sorting)
        const brandsResponse = await axios.get(`${API_URL}/brands`, getAuthHeaders());
        setSortOptions(brandsResponse.data);

        setLoading(false);
      } catch (error) {
        console.error('Error fetching data:', error);
        handleError(error);
      }
    };

    fetchData();
  }, [id, navigate]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setBrand({ ...brand, [name]: value });
  };

  const handleFileChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      setSelectedFile(file);

      // Tạo preview cho file hình ảnh
      const reader = new FileReader();
      reader.onloadend = () => {
        setPreviewImage(reader.result);
      };
      reader.readAsDataURL(file);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    // Kiểm tra xem đã đăng nhập chưa
    const token = getAuthToken();
    if (!token) {
      setError('Bạn cần đăng nhập để thực hiện thao tác này.');
      return;
    }

    try {
      setLoading(true);

      // Tạo FormData để gửi dữ liệu và file
      const formData = new FormData();
      formData.append('name', brand.name);
      formData.append('description', brand.description || '');
      formData.append('sort_order', brand.sortOrder);
      formData.append('status', brand.status);

      // Gửi tên file hình ảnh cũ để server có thể xóa khi cập nhật hình mới
      if (brand.oldImage) {
        formData.append('old_image', brand.oldImage);
      }

      // Chỉ append file nếu có chọn file mới
      if (selectedFile) {
        formData.append('image', selectedFile);
      }

      // Thêm log để debug
      console.log("Submitting form with data:", {
        name: brand.name,
        description: brand.description || '',
        sort_order: brand.sortOrder,
        status: brand.status,
        old_image: brand.oldImage,
        new_image: selectedFile ? selectedFile.name : 'No new image'
      });

      // Gửi request cập nhật (PUT) với FormData
      const response = await axios.put(`${API_URL}/brands/${id}`, formData, {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'multipart/form-data'
        }
      });

      console.log("Update response:", response.data);

      setLoading(false);
      // Chuyển về trang danh sách thương hiệu sau khi cập nhật thành công
      navigate('/admin/brands');
    } catch (error) {
      console.error('Error updating brand:', error);
      handleError(error);
    }
  };

  if (loading) return <div className="text-center p-4">Loading...</div>;
  if (error) return <div className="text-center p-4 text-red-500">{error}</div>;

  return (
    <div className="container mx-60 p-4">
      <h1 className="text-2xl font-bold mb-4">Chỉnh sửa thương hiệu</h1>
      <div className="bg-white shadow-md rounded p-6">
        <form onSubmit={handleSubmit}>
          <div className="mb-4">
            <label className="block text-gray-700 mb-2" htmlFor="name">Tên thương hiệu <span className="text-red-500">*</span></label>
            <input
              type="text"
              name="name"
              id="name"
              value={brand.name}
              onChange={handleChange}
              className="border border-gray-300 rounded w-full p-2"
              required
            />
          </div>

          <div className="mb-4">
            <label className="block text-gray-700 mb-2" htmlFor="description">Mô tả</label>
            <textarea
              name="description"
              id="description"
              value={brand.description}
              onChange={handleChange}
              className="border border-gray-300 rounded w-full p-2"
            />
          </div>

          <div className="mb-4">
            <label className="block text-gray-700 mb-2" htmlFor="sortOrder">Sắp xếp</label>
            <select
              name="sortOrder"
              id="sortOrder"
              value={brand.sortOrder}
              onChange={handleChange}
              className="border border-gray-300 rounded w-full p-2"
            >
              <option value="0">none</option>
              {sortOptions.map(option => (
                <option key={option.id} value={option.id + 1}>
                  Sau: {option.name}
                </option>
              ))}
            </select>
          </div>

          <div className="mb-4">
            <label className="block text-gray-700 mb-2" htmlFor="image">Hình</label>
            <input
              type="file"
              name="image"
              id="image"
              onChange={handleFileChange}
              className="border border-gray-300 rounded w-full p-2"
              accept="image/*"
            />
            {previewImage && (
              <div className="mt-2">
                <p className="text-sm text-gray-600 mb-1">Hình ảnh hiện tại:</p>
                <img
                  src={previewImage}
                  alt="Preview"
                  className="w-32 h-32 object-cover border rounded"
                  onError={(e) => {
                    e.target.onerror = null;
                    e.target.src = 'https://via.placeholder.com/150?text=No+Image';
                  }}
                />
              </div>
            )}
          </div>

          <div className="mb-4">
            <label className="block text-gray-700 mb-2" htmlFor="status">Trạng thái</label>
            <select
              name="status"
              id="status"
              value={brand.status}
              onChange={handleChange}
              className="border border-gray-300 rounded w-full p-2"
            >
              <option value="1">Xuất bản</option>
              <option value="2">Chưa xuất bản</option>
            </select>
          </div>

          <div className="mb-4 flex">
            <button
              type="submit"
              className="bg-green-500 hover:bg-green-700 text-white py-2 px-4 rounded flex items-center"
              disabled={loading}
            >
              {loading ? 'Đang xử lý...' : 'Cập nhật thương hiệu'}
            </button>
            <button
              type="button"
              className="ml-4 bg-gray-500 hover:bg-gray-700 text-white py-2 px-4 rounded flex items-center"
              onClick={() => navigate('/admin/brands')}
            >
              Về Danh sách
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default EditBrand;
