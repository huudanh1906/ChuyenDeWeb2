import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { Link } from 'react-router-dom';

const Categories = () => {
    const [categories, setCategories] = useState([]);
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
        } else {
            setError('Không thể tải danh sách danh mục. Vui lòng thử lại sau.');
        }
        setLoading(false);
    };

    useEffect(() => {
        const fetchCategories = async () => {
            // Kiểm tra xem đã đăng nhập chưa
            const token = getAuthToken();
            if (!token) {
                setError('Bạn cần đăng nhập để xem danh sách danh mục.');
                setLoading(false);
                return;
            }

            setLoading(true);
            try {
                const response = await axios.get(`${API_BASE_URL}/categories`, getAuthHeaders());
                setCategories(response.data);
                setLoading(false);
            } catch (err) {
                console.error('Error details:', err);
                handleError(err);
            }
        };

        fetchCategories();
    }, []);

    const deleteCategory = async (id) => {
        try {
            await axios.put(`${API_BASE_URL}/categories/${id}/delete`, {}, getAuthHeaders());
            setCategories(categories.filter(category => category.id !== id));
        } catch (err) {
            console.error('Error deleting category', err);
            if (err.response && err.response.status === 401) {
                alert('Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại.');
                window.location.href = '/login';
            } else {
                alert('Không thể xóa danh mục. Vui lòng thử lại.');
            }
        }
    };

    const toggleStatus = async (id, currentStatus) => {
        try {
            const response = await axios.put(`${API_BASE_URL}/categories/${id}/status`, {}, getAuthHeaders());

            if (response.data && response.data.status) {
                const newStatus = parseInt(response.data.status);
                setCategories(categories.map(category =>
                    category.id === id ? { ...category, status: newStatus } : category
                ));
            }
        } catch (err) {
            console.error('Error toggling category status', err);
            if (err.response && err.response.status === 401) {
                alert('Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại.');
                window.location.href = '/login';
            } else {
                alert('Không thể cập nhật trạng thái. Vui lòng thử lại.');
            }
        }
    };

    // Hàm kiểm tra và trả về trạng thái hiển thị của category
    const getCategoryStatusText = (status) => {
        switch (status) {
            case 0: return "Trashed";
            case 1: return "Active";
            case 2: return "Inactive";
            default: return "Unknown";
        }
    };

    if (loading) return <div className="text-center p-4">Loading categories...</div>;
    if (error) return <div className="text-center p-4 text-red-500">{error}</div>;

    return (
        <div className="container mx-60 p-4">
            <h2 className="text-2xl font-bold mb-4">Quản lí danh mục</h2>
            <div className="flex justify-end mb-4">
                <button className="bg-green-500 text-white px-4 py-2 rounded mx-2">
                    <Link to="/admin/categories/trash" className="flex items-center justify-center h-full w-full">Trash</Link>
                </button>
                <button className="bg-green-500 text-white px-4 py-2 rounded">
                    <Link to="/admin/categories/create" className="flex items-center justify-center h-full w-full">Add</Link>
                </button>
            </div>
            <div className="overflow-x-auto">
                <table className="table-auto w-full bg-white shadow-md rounded-lg">
                    <thead>
                        <tr className="bg-gray-200 text-left text-sm">
                            <th className="p-3">#</th>
                            <th className="p-3">Image</th>
                            <th className="p-3">Category Name</th>
                            <th className="p-3">Actions</th>
                            <th className="p-3">ID</th>
                        </tr>
                    </thead>
                    <tbody>
                        {categories.map((category, index) => (
                            <tr key={category.id} className="border-t">
                                <td className="p-3"><input type="checkbox" /></td>
                                <td className="p-3">
                                    <img
                                        src={`${IMAGE_BASE_URL}${category.image}`}
                                        alt={category.name}
                                        className="w-16 h-16 object-cover rounded"
                                        onError={(e) => {
                                            e.target.onerror = null;
                                            e.target.src = 'http://localhost:8080/uploads/default-category.jpg';
                                        }}
                                    />
                                </td>
                                <td className="p-3">{category.name}</td>
                                <td className="p-3 space-x-2">
                                    <button
                                        onClick={() => toggleStatus(category.id, category.status)}
                                        className={category.status === 1 ? "bg-green-500 text-white px-2 py-1 rounded" : "bg-red-500 text-white px-2 py-1 rounded"}>
                                        {getCategoryStatusText(category.status)}
                                    </button>
                                    <Link to={`/admin/categories/show/${category.id}`} className="bg-blue-500 text-white px-2 py-1 rounded">View</Link>
                                    <Link to={`/admin/categories/edit/${category.id}`} className="bg-blue-500 text-white px-2 py-1 rounded">Edit</Link>
                                    <button
                                        onClick={() => deleteCategory(category.id)}
                                        className="bg-red-500 text-white px-2 py-1 rounded">
                                        Delete
                                    </button>
                                </td>
                                <td className="p-3">{category.id}</td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
};

export default Categories;
