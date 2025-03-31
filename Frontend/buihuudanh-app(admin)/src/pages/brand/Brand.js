import React, { useEffect, useState } from "react";
import axios from "axios";
import { Link } from "react-router-dom"; // Import Link from react-router-dom

const Brands = () => {
  const [brands, setBrands] = useState([]);
  const [form, setForm] = useState({
    name: "",
    description: "",
    sortOrder: 0,
    image: null,
    status: 1,
  });
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(true);

  // URL cơ sở cho API backend
  const API_URL = "http://localhost:8080/api"; // New Java API base URL
  // URL cho hình ảnh
  const IMAGE_BASE_URL = "http://localhost:8080/uploads/brands/";

  useEffect(() => {
    loadBrands();
  }, []);

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
      setError('Không thể tải dữ liệu. Vui lòng thử lại sau.');
    }
    setLoading(false);
  };

  // Hàm load danh sách thương hiệu
  const loadBrands = () => {
    // Kiểm tra xem đã đăng nhập chưa
    const token = getAuthToken();
    if (!token) {
      setError('Bạn cần đăng nhập để xem danh sách thương hiệu.');
      setLoading(false);
      return;
    }

    // Fetch brands từ backend Java với token xác thực
    setLoading(true);

    // Lấy thương hiệu đang active và inactive (không bao gồm trashed)
    axios.get(`${API_URL}/brands`, getAuthHeaders())
      .then(response => {
        setBrands(response.data);
        setLoading(false);
      })
      .catch(error => {
        console.error('There was an error fetching the brands!', error);
        handleError(error);
      });
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm({
      ...form,
      [name]: value,
    });
  };

  const handleFileChange = (e) => {
    setForm({
      ...form,
      image: e.target.files[0],
    });
  };

  const handleStatusChange = (brandId, currentStatus) => {
    // Kiểm tra xem đã đăng nhập chưa
    const token = getAuthToken();
    if (!token) {
      setError('Bạn cần đăng nhập để thực hiện thao tác này.');
      return;
    }

    // Trong Java API, chúng ta sử dụng PUT request với token xác thực
    axios.put(`${API_URL}/brands/${brandId}/status`, {}, getAuthHeaders())
      .then(response => {
        if (response.data.status) {
          // Nếu có status trong response, sử dụng nó
          const newStatus = parseInt(response.data.status);
          setBrands(brands.map(brand =>
            brand.id === brandId ? { ...brand, status: newStatus } : brand
          ));
        } else {
          // Ngược lại, giả định status được toggle giữa 1 (active) và 2 (inactive)
          const newStatus = currentStatus === 1 ? 2 : 1;
          setBrands(brands.map(brand =>
            brand.id === brandId ? { ...brand, status: newStatus } : brand
          ));
        }
      })
      .catch(error => {
        console.error('There was an error updating the status!', error);
        if (error.response && error.response.status === 401) {
          alert('Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại.');
          // Có thể chuyển hướng về trang đăng nhập ở đây
          localStorage.removeItem('authToken');
          window.location.href = '/login';
        } else {
          alert('Không thể cập nhật trạng thái. Vui lòng thử lại.');
        }
      });
  };

  const handleDelete = (brandId) => {
    // Kiểm tra xem đã đăng nhập chưa
    const token = getAuthToken();
    if (!token) {
      setError('Bạn cần đăng nhập để thực hiện thao tác này.');
      return;
    }

    // Soft delete trong Java API với token xác thực
    axios.put(`${API_URL}/brands/${brandId}/delete`, {}, getAuthHeaders())
      .then(response => {
        // Sau khi xóa mềm, loại bỏ brand khỏi danh sách hiển thị
        setBrands(brands.filter(brand => brand.id !== brandId));
      })
      .catch(error => {
        console.error('There was an error deleting the brand!', error);
        if (error.response && error.response.status === 401) {
          alert('Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại.');
          // Có thể chuyển hướng về trang đăng nhập ở đây
          localStorage.removeItem('authToken');
          window.location.href = '/login';
        } else {
          alert('Không thể xóa thương hiệu. Vui lòng thử lại.');
        }
      });
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    // Kiểm tra xem đã đăng nhập chưa
    const token = getAuthToken();
    if (!token) {
      setError('Bạn cần đăng nhập để thực hiện thao tác này.');
      return;
    }

    if (!form.name) {
      setError("Tên thương hiệu không được để trống");
      return;
    }

    const formData = new FormData();
    formData.append("name", form.name);
    formData.append("description", form.description || "");
    formData.append("sort_order", form.sortOrder);
    if (form.image) {
      formData.append("image", form.image);
    }
    formData.append("status", form.status);

    // New Java API endpoint for brand creation
    axios.post(`${API_URL}/brands`, formData, getFormDataAuthHeaders())
      .then(response => {
        // Reset form after successful submission
        setForm({
          name: "",
          description: "",
          sortOrder: 0,
          image: null,
          status: 1,
        });

        loadBrands(); // Refresh the brand list
        setError(null); // Clear any previous errors
      })
      .catch(error => {
        console.error('There was an error creating the brand!', error);
        if (error.response && error.response.status === 401) {
          alert('Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại.');
          // Có thể chuyển hướng về trang đăng nhập ở đây
          localStorage.removeItem('authToken');
          window.location.href = '/login';
        } else {
          alert('Không thể tạo thương hiệu. Vui lòng thử lại.');
        }
      });
  };

  // Hàm kiểm tra và trả về trạng thái hiển thị của brand
  const getBrandStatusText = (status) => {
    switch (status) {
      case 0: return "Trashed";
      case 1: return "Xuất bản";
      case 2: return "Chưa xuất bản";
      default: return "Unknown";
    }
  };

  // Hàm kiểm tra và trả về màu sắc cho nút trạng thái
  const getBrandStatusClass = (status) => {
    switch (status) {
      case 0: return "bg-gray-500 hover:bg-gray-700";
      case 1: return "bg-green-500 hover:bg-green-700";
      case 2: return "bg-red-500 hover:bg-red-700";
      default: return "bg-gray-500 hover:bg-gray-700";
    }
  };

  if (loading) return <div className="text-center p-4">Loading brands...</div>;
  if (error) return <div className="text-center p-4 text-red-500">{error}</div>;

  return (
    <div className="container mx-60 p-6">
      <h2 className="text-2xl font-bold mb-4">Quản lí thương hiệu</h2>
      <div className="flex justify-end mb-4">
        <Link to="/admin/brands/trash" className="bg-green-500 text-white px-4 py-2 rounded flex items-center justify-center">
          <i className="fas fa-trash mr-2"></i>Thùng rác
        </Link>
      </div>

      <div className="grid grid-cols-3 gap-6">
        {/* Brand Form */}
        <div className="bg-gray-100 p-6 rounded shadow-md">
          <form onSubmit={handleSubmit}>
            <div className="mb-4">
              <label className="block text-gray-700" htmlFor="name">
                Tên thương hiệu <span className="text-red-500">*</span>
              </label>
              <input
                type="text"
                id="name"
                name="name"
                value={form.name}
                onChange={handleChange}
                className="w-full p-2 mt-2 border rounded"
                required
              />
            </div>
            <div className="mb-4">
              <label className="block text-gray-700" htmlFor="description">
                Mô tả
              </label>
              <textarea
                id="description"
                name="description"
                value={form.description}
                onChange={handleChange}
                className="w-full p-2 mt-2 border rounded"
              />
            </div>
            <div className="mb-4">
              <label className="block text-gray-700" htmlFor="sortOrder">
                Sắp xếp
              </label>
              <select
                id="sortOrder"
                name="sortOrder"
                value={form.sortOrder}
                onChange={handleChange}
                className="w-full p-2 mt-2 border rounded"
              >
                <option value="0">None</option>
                {brands.map((brand) => (
                  <option key={brand.id} value={brand.id + 1}>
                    Sau: {brand.name}
                  </option>
                ))}
              </select>
            </div>
            <div className="mb-4">
              <label className="block text-gray-700" htmlFor="image">
                Hình
              </label>
              <input
                type="file"
                id="image"
                name="image"
                onChange={handleFileChange}
                className="w-full p-2 mt-2 border rounded"
              />
            </div>
            <div className="mb-4">
              <label className="block text-gray-700" htmlFor="status">
                Trạng thái
              </label>
              <select
                id="status"
                name="status"
                value={form.status}
                onChange={handleChange}
                className="w-full p-2 mt-2 border rounded"
              >
                <option value="1">Xuất bản</option>
                <option value="2">Chưa xuất bản</option>
              </select>
            </div>
            <button
              type="submit"
              className="bg-green-500 text-white py-2 px-4 rounded"
            >
              Thêm thương hiệu
            </button>
          </form>
        </div>

        {/* Brand List */}
        <div className="col-span-2">
          {brands.length > 0 ? (
            <table className="min-w-full bg-white border">
              <thead>
                <tr>
                  <th className="py-2 px-4 border">#</th>
                  <th className="py-2 px-4 border">Hình</th>
                  <th className="py-2 px-4 border">Tên thương hiệu</th>
                  <th className="py-2 px-4 border">Hoạt động</th>
                  <th className="py-2 px-4 border">ID</th>
                </tr>
              </thead>
              <tbody>
                {brands.map((brand) => (
                  <tr key={brand.id}>
                    <td className="py-2 px-4 border">
                      <input type="checkbox" />
                    </td>
                    <td className="py-2 px-4 border">
                      {brand.image ? (
                        <img
                          src={`${IMAGE_BASE_URL}${brand.image}`}
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
                    <td className="py-2 px-4 border">{brand.name}</td>
                    <td className="py-2 px-4 border text-center">
                      <button
                        onClick={() => handleStatusChange(brand.id, brand.status)}
                        className={`px-3 py-2 rounded ${getBrandStatusClass(brand.status)} text-white`}
                      >
                        {getBrandStatusText(brand.status)}
                      </button>
                      <Link to={`/admin/brands/show/${brand.id}`} className="px-4 py-2 bg-green-500 hover:bg-green-700 text-white rounded ml-2">
                        <i className="fas fa-eye"></i> Xem
                      </Link>
                      <Link to={`/admin/brands/edit/${brand.id}`} className="px-4 py-2 bg-blue-500 hover:bg-blue-700 text-white rounded ml-2">
                        <i className="fas fa-edit"></i> Sửa
                      </Link>
                      <button
                        onClick={() => handleDelete(brand.id)}
                        className="px-4 py-2 bg-red-500 hover:bg-red-700 text-white rounded ml-2"
                      >
                        <i className="fas fa-trash"></i> Xóa
                      </button>
                    </td>
                    <td className="py-2 px-4 border">{brand.id}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          ) : (
            <div className="text-center p-4 bg-gray-100 rounded">
              {error ? "Error loading brands" : "No brands found"}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default Brands;
