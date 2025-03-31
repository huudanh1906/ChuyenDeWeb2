import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useParams, useNavigate } from 'react-router-dom';

const EditCategory = () => {
    const { id } = useParams();
    const navigate = useNavigate();

    const [category, setCategory] = useState({
        name: '',
        description: '',
        parentId: 0,
        sortOrder: 0,
        status: 1,
        slug: '',
        oldImage: '',  // Lưu tên file ảnh cũ
    });
    const [parentCategories, setParentCategories] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [formErrors, setFormErrors] = useState({});
    const [selectedFile, setSelectedFile] = useState(null);
    const [previewImage, setPreviewImage] = useState(null);
    const [formSubmitting, setFormSubmitting] = useState(false);

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
        } else if (error.response && error.response.status === 404) {
            setError('Không tìm thấy danh mục này.');
        } else if (error.response && error.response.status === 400) {
            const message = error.response.data?.message ||
                error.response.data?.error ||
                'Dữ liệu không hợp lệ';
            setError(`Lỗi: ${message}`);
        } else {
            setError(`Lỗi: ${error.response?.data?.message || error.message || 'Không xác định'}`);
        }
        setLoading(false);
        setFormSubmitting(false);
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
        const fetchData = async () => {
            // Kiểm tra lại token mỗi khi gọi API
            const token = getAuthToken();
            if (!token) {
                setError('Bạn cần đăng nhập để chỉnh sửa danh mục.');
                setLoading(false);
                navigate('/login');
                return;
            }

            try {
                setLoading(true);
                console.log(`Fetching category with ID: ${id}`);

                // Fetch category detail
                const categoryResponse = await axios.get(`${API_URL}/categories/${id}`, getAuthHeaders());
                console.log("Category data fetched:", categoryResponse.data);

                // Cập nhật state từ dữ liệu API Java
                const categoryData = categoryResponse.data;
                setCategory({
                    name: categoryData.name || '',
                    description: categoryData.description || '',
                    parentId: categoryData.parentId || 0,
                    sortOrder: categoryData.sortOrder || 0,
                    status: categoryData.status || 1,
                    slug: categoryData.slug || '',
                    oldImage: categoryData.image || ''
                });

                if (categoryData.image) {
                    setPreviewImage(`${IMAGE_BASE_URL}${categoryData.image}`);
                }

                // Fetch parent categories for dropdown
                const parentResponse = await axios.get(`${API_URL}/categories`, getAuthHeaders());
                // Lọc ra các danh mục để làm parent (loại bỏ danh mục hiện tại nếu đang edit)
                const parentCats = parentResponse.data
                    .filter(cat => cat.id !== parseInt(id) && cat.status !== 0)
                    .filter(cat => cat.parentId === 0);

                setParentCategories(parentCats);
                setLoading(false);
            } catch (error) {
                console.error('Error fetching data:', error);
                handleError(error);
            }
        };

        if (id) {
            fetchData();
        }
    }, [id, navigate]);

    const handleChange = (e) => {
        const { name, value } = e.target;
        console.log(`Field ${name} changed to ${value}`);
        setCategory({ ...category, [name]: value });

        // Xóa lỗi khi người dùng thay đổi giá trị
        if (formErrors[name]) {
            setFormErrors(prev => ({
                ...prev,
                [name]: null
            }));
        }
    };

    const handleFileChange = (e) => {
        const file = e.target.files[0];
        if (file) {
            setSelectedFile(file);
            console.log("File selected:", file.name);

            // Tạo preview cho file hình ảnh
            const reader = new FileReader();
            reader.onloadend = () => {
                setPreviewImage(reader.result);
            };
            reader.readAsDataURL(file);
        }
    };

    const validateForm = () => {
        const errors = {};
        if (!category.name.trim()) {
            errors.name = 'Tên danh mục không được để trống';
        }

        setFormErrors(errors);
        return Object.keys(errors).length === 0;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!validateForm()) {
            return;
        }

        // Kiểm tra xem đã đăng nhập chưa
        const token = getAuthToken();
        if (!token) {
            setError('Bạn cần đăng nhập để thực hiện thao tác này.');
            navigate('/login');
            return;
        }

        try {
            setFormSubmitting(true);
            console.log("Form submission started with data:", {
                name: category.name,
                description: category.description || '',
                parentId: category.parentId,
                sortOrder: category.sortOrder,
                status: category.status,
                slug: category.slug || '',
                hasNewImage: selectedFile !== null
            });

            // Tạo FormData để gửi dữ liệu và file
            const formData = new FormData();
            formData.append('name', category.name);
            formData.append('description', category.description || '');
            formData.append('parent_id', category.parentId);
            formData.append('sort_order', category.sortOrder);
            formData.append('status', category.status);

            // Chỉ gửi slug nếu không trống
            if (category.slug && category.slug.trim() !== '') {
                formData.append('slug', category.slug);
            }

            // Gửi tên file hình ảnh cũ để server có thể xóa khi cập nhật hình mới
            if (category.oldImage) {
                formData.append('old_image', category.oldImage);
            }

            // Chỉ append file nếu có chọn file mới
            if (selectedFile) {
                formData.append('image', selectedFile);
            }

            // Log tất cả các key trong formData
            console.log("Form data being sent:");
            for (let pair of formData.entries()) {
                console.log(pair[0] + ': ' + (pair[0] === 'image' ? 'File Data' : pair[1]));
            }

            // Gửi request cập nhật (PUT) với FormData
            const response = await axios.put(`${API_URL}/categories/${id}`, formData, {
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'multipart/form-data'
                }
            });

            console.log("Update response:", response.data);
            alert('Cập nhật danh mục thành công');

            setFormSubmitting(false);
            // Chuyển về trang danh sách danh mục sau khi cập nhật thành công
            navigate('/admin/categories');
        } catch (error) {
            console.error('Error updating category:', error);

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
                    alert('Đã xảy ra lỗi khi cập nhật danh mục.');
                }
            } else {
                alert('Không thể kết nối đến máy chủ. Vui lòng thử lại sau.');
            }

            setFormSubmitting(false);
        }
    };

    if (loading) return <div className="text-center p-4">Đang tải thông tin danh mục...</div>;
    if (error) return <div className="text-center p-4 text-red-500">{error}</div>;

    return (
        <div className="container mx-60 p-4">
            <h1 className="text-2xl font-bold mb-4">Chỉnh sửa danh mục: {category.name}</h1>
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
                            className={`border ${formErrors.name ? 'border-red-500' : 'border-gray-300'} rounded w-full p-2`}
                            required
                        />
                        {formErrors.name && <p className="text-red-500 text-sm mt-1">{formErrors.name}</p>}
                    </div>

                    <div className="mb-4">
                        <label className="block text-gray-700 mb-2" htmlFor="description">Mô tả</label>
                        <textarea
                            name="description"
                            id="description"
                            value={category.description || ''}
                            onChange={handleChange}
                            className="border border-gray-300 rounded w-full p-2"
                            rows="4"
                        />
                    </div>

                    <div className="mb-4">
                        <label className="block text-gray-700 mb-2" htmlFor="slug">Slug</label>
                        <input
                            type="text"
                            name="slug"
                            id="slug"
                            value={category.slug || ''}
                            onChange={handleChange}
                            className="border border-gray-300 rounded w-full p-2"
                            placeholder="Để trống để tự động tạo từ tên"
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
                        {(previewImage || category.oldImage) && (
                            <div className="mt-2">
                                <p className="text-sm text-gray-600 mb-1">Hình ảnh hiện tại:</p>
                                <img
                                    src={previewImage || `${IMAGE_BASE_URL}${category.oldImage}`}
                                    alt="Preview"
                                    className="w-32 h-32 object-cover border rounded"
                                    onError={(e) => {
                                        e.target.onerror = null;
                                        e.target.src = 'https://via.placeholder.com/150?text=No+Image';
                                    }}
                                />
                                <p className="text-sm text-gray-500 mt-1">Để trống nếu không muốn thay đổi hình ảnh.</p>
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
                            disabled={formSubmitting}
                        >
                            {formSubmitting ? 'Đang xử lý...' : 'Cập nhật danh mục'}
                        </button>
                        <button
                            type="button"
                            className="ml-4 bg-gray-500 hover:bg-gray-700 text-white py-2 px-4 rounded flex items-center"
                            onClick={() => navigate('/admin/categories')}
                            disabled={formSubmitting}
                        >
                            Về Danh sách
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default EditCategory;
