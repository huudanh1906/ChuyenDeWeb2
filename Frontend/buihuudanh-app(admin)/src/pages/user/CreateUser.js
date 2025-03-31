import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

const CreateUser = () => {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    name: '',
    phone: '',
    email: '',
    gender: '1', // Default to male
    address: '',
    username: '',
    password: '',
    password_confirmation: '',
    roles: 'USER', // Default to "USER"
    image: null,
    status: '1', // Default to active
  });

  const [errors, setErrors] = useState({});
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [imagePreview, setImagePreview] = useState(null);

  // URL cơ sở cho API backend
  const API_BASE_URL = 'http://localhost:8080/api';

  // Lấy token xác thực từ localStorage
  const getAuthToken = () => {
    return localStorage.getItem('authToken');
  };

  // Kiểm tra và chuyển hướng nếu không có token
  useEffect(() => {
    const token = getAuthToken();
    if (!token) {
      alert('Bạn cần đăng nhập để truy cập trang này.');
      navigate('/login');
    }
  }, [navigate]);

  const handleChange = (e) => {
    const { name, value, type, files } = e.target;

    if (type === 'file') {
      if (files[0]) {
        const file = files[0];
        console.log("Selected file:", file.name, file.type, file.size);

        // Hiển thị ảnh xem trước
        const reader = new FileReader();
        reader.onloadend = () => {
          const base64String = reader.result;
          console.log("Image converted to base64, length:", base64String.length);
          setImagePreview(base64String);
          setFormData(prev => ({
            ...prev,
            image: files[0] // Lưu trữ file đối tượng, không phải base64
          }));
        };
        reader.readAsDataURL(file);
      }
    } else {
      setFormData(prev => ({
        ...prev,
        [name]: value
      }));
    }

    // Clear error when field is changed
    if (errors[name]) {
      setErrors(prev => ({
        ...prev,
        [name]: null
      }));
    }
  };

  const validateForm = () => {
    const newErrors = {};

    if (!formData.name.trim()) newErrors.name = ['Tên không được để trống'];
    if (!formData.email.trim()) newErrors.email = ['Email không được để trống'];
    if (!formData.phone.trim()) newErrors.phone = ['Điện thoại không được để trống'];
    if (!formData.address.trim()) newErrors.address = ['Địa chỉ không được để trống'];
    if (formData.phone.length !== 10) newErrors.phone = ['Số điện thoại phải có 10 chữ số'];
    if (!formData.username.trim()) newErrors.username = ['Tên đăng nhập không được để trống'];
    if (!formData.password.trim()) newErrors.password = ['Mật khẩu không được để trống'];
    if (!formData.password.trim()) newErrors.password_confirmation = ['Xác nhận mật khẩu không được để trống'];
    if (formData.password !== formData.password_confirmation) {
      newErrors.password = ['Mật khẩu xác nhận không khớp'];
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!validateForm()) return;

    setIsSubmitting(true);
    console.log("Starting form submission with data:", {
      name: formData.name,
      email: formData.email,
      roles: formData.roles,
      hasImage: !!formData.image
    });

    try {
      // Create FormData object for multipart/form-data (file upload)
      const form = new FormData();
      form.append('name', formData.name);
      form.append('phone', formData.phone || '');
      form.append('email', formData.email);
      form.append('address', formData.address || '');
      form.append('username', formData.username);
      form.append('password', formData.password);
      form.append('roles', formData.roles);
      form.append('status', parseInt(formData.status, 10));
      form.append('gender', parseInt(formData.gender, 10));

      // Append image file if exists - send as MultipartFile
      if (formData.image) {
        form.append('image', formData.image);
        console.log("Appending image file:", formData.image.name, formData.image.type);
      }

      // Get the auth token for authorization
      const token = getAuthToken();

      // Log FormData entries
      console.log("FormData entries:");
      for (let pair of form.entries()) {
        if (pair[0] === 'image') {
          console.log(pair[0], ":", pair[1].name, pair[1].type, pair[1].size);
        } else {
          console.log(pair[0], ":", pair[1]);
        }
      }

      const response = await axios.post(`${API_BASE_URL}/users`, form, {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'multipart/form-data'
        }
      });

      console.log("API response:", response.data);
      alert('Thêm người dùng thành công!');
      navigate('/admin/user'); // Redirect to user list
    } catch (error) {
      console.error('Error creating user:', error);
      console.error('Error response:', error.response?.data);

      if (error.response) {
        if (error.response.status === 401) {
          alert('Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại.');
          localStorage.removeItem('authToken');
          navigate('/login');
        } else if (error.response.data && error.response.data.error) {
          // Backend returns errors in error field
          alert(`Lỗi: ${error.response.data.error}`);
        } else if (error.response.data && error.response.data.message) {
          // Backend returns errors in message field
          alert(`Lỗi: ${error.response.data.message}`);
        } else if (error.response.data && error.response.data.errors) {
          // Backend returns multiple errors in errors object
          setErrors(error.response.data.errors);
        } else {
          alert('Có lỗi xảy ra khi tạo người dùng');
        }
      } else {
        alert('Không thể kết nối đến máy chủ');
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="max-w-3xl mx-auto p-4">
      <h2 className="text-2xl font-semibold mb-4">Thêm thành viên</h2>
      <form onSubmit={handleSubmit} encType="multipart/form-data">
        <div className="bg-white shadow-md rounded px-8 pt-6 pb-8 mb-4">
          <div className="flex flex-wrap -mx-3 mb-6">
            <div className="w-full md:w-1/2 px-3 mb-6 md:mb-0">
              <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="name">
                Họ tên <span className="text-red-500">*</span>
              </label>
              <input
                type="text"
                name="name"
                id="name"
                value={formData.name}
                onChange={handleChange}
                className={`shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline ${errors.name ? 'border-red-500' : ''}`}
              />
              {errors.name && <span className="text-red-500 text-xs">{errors.name[0]}</span>}
            </div>

            <div className="w-full md:w-1/2 px-3 mb-6 md:mb-0">
              <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="phone">
                Điện thoại <span className="text-red-500">*</span>
              </label>
              <input
                type="text"
                name="phone"
                id="phone"
                value={formData.phone}
                onChange={handleChange}
                className={`shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline ${errors.phone ? 'border-red-500' : ''}`}
              />
              {errors.phone && <span className="text-red-500 text-xs">{errors.phone[0]}</span>}
            </div>
          </div>

          <div className="flex flex-wrap -mx-3 mb-6">
            <div className="w-full md:w-1/2 px-3 mb-6 md:mb-0">
              <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="email">
                Email <span className="text-red-500">*</span>
              </label>
              <input
                type="text"
                name="email"
                id="email"
                value={formData.email}
                onChange={handleChange}
                className={`shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline ${errors.email ? 'border-red-500' : ''}`}
              />
              {errors.email && <span className="text-red-500 text-xs">{errors.email[0]}</span>}
            </div>

            <div className="w-full md:w-1/2 px-3 mb-6 md:mb-0">
              <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="gender">
                Giới tính
              </label>
              <select
                name="gender"
                id="gender"
                value={formData.gender}
                onChange={handleChange}
                className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
              >
                <option value="1">Nam</option>
                <option value="0">Nữ</option>
              </select>
            </div>
          </div>

          <div className="flex flex-wrap -mx-3 mb-6">
            <div className="w-full md:w-1/2 px-3 mb-6 md:mb-0">
              <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="address">
                Địa chỉ <span className="text-red-500">*</span>
              </label>
              <input
                type="text"
                name="address"
                id="address"
                value={formData.address}
                onChange={handleChange}
                className={`shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline ${errors.address ? 'border-red-500' : ''}`}
              />
              {errors.address && <span className="text-red-500 text-xs">{errors.address[0]}</span>}
            </div>

            <div className="w-full md:w-1/2 px-3 mb-6 md:mb-0">
              <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="username">
                Tên đăng nhập <span className="text-red-500">*</span>
              </label>
              <input
                type="text"
                name="username"
                id="username"
                value={formData.username}
                onChange={handleChange}
                className={`shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline ${errors.username ? 'border-red-500' : ''}`}
              />
              {errors.username && <span className="text-red-500 text-xs">{errors.username[0]}</span>}
            </div>
          </div>

          <div className="flex flex-wrap -mx-3 mb-6">
            <div className="w-full md:w-1/2 px-3 mb-6 md:mb-0">
              <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="password">
                Mật khẩu <span className="text-red-500">*</span>
              </label>
              <input
                type="password"
                name="password"
                id="password"
                value={formData.password}
                onChange={handleChange}
                className={`shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline ${errors.password ? 'border-red-500' : ''}`}
              />
              {errors.password && <span className="text-red-500 text-xs">{errors.password[0]}</span>}
            </div>

            <div className="w-full md:w-1/2 px-3 mb-6 md:mb-0">
              <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="password_confirmation">
                Xác nhận mật khẩu <span className="text-red-500">*</span>
              </label>
              <input
                type="password"
                name="password_confirmation"
                id="password_confirmation"
                value={formData.password_confirmation}
                onChange={handleChange}
                className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
              />
            </div>
          </div>

          <div className="flex flex-wrap -mx-3 mb-6">
            <div className="w-full md:w-1/2 px-3 mb-6 md:mb-0">
              <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="roles">
                Quyền
              </label>
              <select
                name="roles"
                id="roles"
                value={formData.roles}
                onChange={handleChange}
                className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
              >
                <option value="USER">Khách hàng</option>
                <option value="ADMIN">Quản lý</option>
              </select>
            </div>

            <div className="w-full md:w-1/2 px-3 mb-6 md:mb-0">
              <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="image">
                Hình
              </label>
              <input
                type="file"
                name="image"
                id="image"
                onChange={handleChange}
                className={`shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline ${errors.image ? 'border-red-500' : ''}`}
                accept="image/*"
              />
              {errors.image && <span className="text-red-500 text-xs">{errors.image[0]}</span>}

              {/* Hiển thị ảnh xem trước nếu có */}
              {imagePreview && (
                <div className="mt-2">
                  <img
                    src={imagePreview}
                    alt="Preview"
                    className="w-20 h-20 object-cover"
                  />
                </div>
              )}
            </div>
          </div>

          <div className="flex flex-wrap -mx-3 mb-6">
            <div className="w-full px-3 mb-6">
              <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="status">
                Trạng thái
              </label>
              <select
                name="status"
                id="status"
                value={formData.status}
                onChange={handleChange}
                className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
              >
                <option value="1">Hoạt động</option>
                <option value="2">Không hoạt động</option>
              </select>
            </div>
          </div>

          <div className="flex items-center justify-between">
            <button
              type="submit"
              className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline disabled:bg-blue-300"
              disabled={isSubmitting}
            >
              {isSubmitting ? 'Đang xử lý...' : 'Thêm'}
            </button>
            <button
              type="button"
              onClick={() => navigate('/admin/user')}
              className="text-blue-500 hover:text-blue-800 font-bold"
              disabled={isSubmitting}
            >
              Quay lại
            </button>
          </div>
        </div>
      </form>
    </div>
  );
};

export default CreateUser;
