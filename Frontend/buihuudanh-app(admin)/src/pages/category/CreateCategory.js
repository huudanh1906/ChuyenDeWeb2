import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate, Link } from 'react-router-dom';

const CreateCategory = () => {
    const [category, setCategory] = useState({
        name: '',
        description: '',
        parentId: 0,
        sortOrder: 0,
        status: 1,
        slug: '',
    });
    const [selectedFile, setSelectedFile] = useState(null);
    const [parentCategories, setParentCategories] = useState([]);
    const [loading, setLoading] = useState(false);
    const [submitting, setSubmitting] = useState(false);
    const [error, setError] = useState(null);
    const [previewImage, setPreviewImage] = useState(null);
    const navigate = useNavigate();

    // URL cơ sở cho API backend
    const API_URL = "http://localhost:8080/api";

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
        setSubmitting(false);
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
        const fetchParentCategories = async () => {
            // Kiểm tra token trước khi gọi API
            const token = getAuthToken();
            if (!token) {
                setError('Bạn cần đăng nhập để tạo danh mục mới.');
                navigate('/login');
                return;
            }

            try {
                setLoading(true);
                // Fetch danh mục cho dropdown
                const response = await axios.get(`${API_URL}/categories`, getAuthHeaders());
                console.log("Categories data:", response.data);

                // Lọc ra chỉ các parent categories (có parentId = 0 và status != 0)
                const parentCats = response.data
                    .filter(cat => cat.parentId === 0 && cat.status !== 0);

                setParentCategories(parentCats);
                setLoading(false);
            } catch (error) {
                console.error('Error fetching parent categories:', error);
                handleError(error);
            }
        };

        fetchParentCategories();
    }, [navigate]);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setCategory({
            ...category,
            [name]: value
        });
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

    const generateSlug = (name) => {
        return name
            .toLowerCase()
            .replace(/[^\w ]+/g, '')
            .replace(/ +/g, '-');
    };

    const validateForm = () => {
        if (!category.name.trim()) {
            setError('Tên danh mục không được để trống');
            return false;
        }
        return true;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!validateForm()) {
            return;
        }

        // Kiểm tra xem đã đăng nhập chưa
        const token = getAuthToken();
        if (!token) {
            setError('Bạn cần đăng nhập để tạo danh mục mới.');
            navigate('/login');
            return;
        }

        try {
            setSubmitting(true);
            // Tự động tạo slug nếu người dùng không nhập
            const slug = category.slug || generateSlug(category.name);

            // Tạo FormData để gửi dữ liệu và file
            const formData = new FormData();
            formData.append('name', category.name);
            formData.append('description', category.description || '');
            formData.append('parent_id', category.parentId);
            formData.append('sort_order', category.sortOrder);
            formData.append('status', category.status);
            formData.append('slug', slug);

            // Chỉ gửi file nếu có chọn
            if (selectedFile) {
                formData.append('image', selectedFile);
            }

            // Log tất cả các key trong formData
            console.log("Form data being sent:");
            for (let pair of formData.entries()) {
                console.log(pair[0] + ': ' + (pair[0] === 'image' ? 'File Data' : pair[1]));
            }

            // Gọi API tạo danh mục mới
            const response = await axios.post(`${API_URL}/categories`, formData, {
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'multipart/form-data'
                }
            });

            console.log("Create response:", response.data);
            alert('Tạo danh mục thành công!');

            setSubmitting(false);
            // Chuyển về trang danh sách danh mục sau khi tạo thành công
            navigate('/admin/categories');
        } catch (error) {
            console.error('Error creating category:', error);

            if (error.response) {
                if (error.response.status === 401) {
                    alert('Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại.');
                    localStorage.removeItem('authToken');
                    navigate('/login');
                } else if (error.response.data && error.response.data.message) {
                    alert(`Lỗi: ${error.response.data.message}`);
                } else if (error.response.data && error.response.data.error) {
                    alert(`Lỗi: ${error.response.data.error}`);
                } else {
                    alert('Đã xảy ra lỗi khi tạo danh mục.');
                }
            } else {
                alert('Không thể kết nối đến máy chủ. Vui lòng thử lại sau.');
            }

            setSubmitting(false);
        }
    };

    if (loading) return <div className="text-center p-4">Đang tải dữ liệu...</div>;

    return (
        <div className="container mx-60 p-4">
            <h1 className="text-2xl font-bold mb-4">Tạo danh mục mới</h1>

            <div className="flex justify-end mb-4">
                <Link to="/admin/categories" className="bg-green-500 hover:bg-green-600 text-white font-bold py-2 px-4 rounded">
                    <i className="fas fa-arrow-left mr-2"></i>Về Danh sách
                </Link>
            </div>

            {error && (
                <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
                    {error}
                </div>
            )}

            <div className="bg-white shadow-md rounded p-6">
                <form onSubmit={handleSubmit}>
                    <div className="mb-4">
                        <label className="block text-gray-700 mb-2" htmlFor="name">Tên danh mục <span className="text-red-500">*</span></label>
                        <input
                            type="text"
                            name="name"
                            id="name"
                            value={category.name}
                            onChange={handleChange}
                            className="border border-gray-300 rounded w-full p-2"
                            required
                        />
                    </div>

                    <div className="mb-4">
                        <label className="block text-gray-700 mb-2" htmlFor="slug">Slug</label>
                        <input
                            type="text"
                            name="slug"
                            id="slug"
                            value={category.slug}
                            onChange={handleChange}
                            className="border border-gray-300 rounded w-full p-2"
                            placeholder="Để trống để tự động tạo từ tên"
                        />
                    </div>

                    <div className="mb-4">
                        <label className="block text-gray-700 mb-2" htmlFor="description">Mô tả</label>
                        <textarea
                            name="description"
                            id="description"
                            value={category.description}
                            onChange={handleChange}
                            className="border border-gray-300 rounded w-full p-2"
                            rows="4"
                        />
                    </div>

                    <div className="mb-4">
                        <label className="block text-gray-700 mb-2" htmlFor="parentId">Danh mục cha</label>
                        <select
                            name="parentId"
                            id="parentId"
                            value={category.parentId}
                            onChange={handleChange}
                            className="border border-gray-300 rounded w-full p-2"
                        >
                            <option value="0">Không có danh mục cha</option>
                            {parentCategories.map((parent) => (
                                <option key={parent.id} value={parent.id}>
                                    {parent.name}
                                </option>
                            ))}
                        </select>
                    </div>

                    <div className="mb-4">
                        <label className="block text-gray-700 mb-2" htmlFor="sortOrder">Thứ tự sắp xếp</label>
                        <input
                            type="number"
                            name="sortOrder"
                            id="sortOrder"
                            value={category.sortOrder}
                            onChange={handleChange}
                            className="border border-gray-300 rounded w-full p-2"
                            min="0"
                        />
                    </div>

                    <div className="mb-4">
                        <label className="block text-gray-700 mb-2" htmlFor="image">Hình ảnh</label>
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
                                <p className="text-sm text-gray-600 mb-1">Hình ảnh đã chọn:</p>
                                <img
                                    src={previewImage}
                                    alt="Preview"
                                    className="w-32 h-32 object-cover border rounded"
                                />
                            </div>
                        )}
                    </div>

                    <div className="mb-4">
                        <label className="block text-gray-700 mb-2" htmlFor="status">Trạng thái</label>
                        <select
                            name="status"
                            id="status"
                            value={category.status}
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
                            className="bg-blue-500 hover:bg-blue-700 text-white py-2 px-4 rounded flex items-center"
                            disabled={submitting}
                        >
                            {submitting ? 'Đang xử lý...' : 'Tạo danh mục'}
                        </button>
                        <button
                            type="button"
                            className="ml-4 bg-gray-500 hover:bg-gray-700 text-white py-2 px-4 rounded flex items-center"
                            onClick={() => navigate('/admin/categories')}
                            disabled={submitting}
                        >
                            Hủy bỏ
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default CreateCategory;
