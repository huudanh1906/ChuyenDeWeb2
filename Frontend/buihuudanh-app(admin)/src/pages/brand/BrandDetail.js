import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';

const BrandDetail = () => {
    const { id } = useParams();
    const [brand, setBrand] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const navigate = useNavigate();

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

    // Xử lý lỗi chung
    const handleError = (error) => {
        console.error('Error:', error);
        if (error.response && error.response.status === 401) {
            setError('Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại.');
            // Có thể chuyển hướng về trang đăng nhập ở đây
            localStorage.removeItem('authToken');
            window.location.href = '/login';
        } else {
            setError('Không thể tải dữ liệu thương hiệu. Vui lòng thử lại sau.');
        }
        setLoading(false);
    };

    useEffect(() => {
        // Kiểm tra xem đã đăng nhập chưa
        const token = getAuthToken();
        if (!token) {
            setError('Bạn cần đăng nhập để xem chi tiết thương hiệu.');
            setLoading(false);
            navigate('/login');
            return;
        }

        // Fetch brand detail từ backend Java với token xác thực
        setLoading(true);

        axios.get(`${API_URL}/brands/${id}`, getAuthHeaders())
            .then(response => {
                setBrand(response.data);
                setLoading(false);
            })
            .catch(error => {
                console.error('There was an error fetching the brand details!', error);
                handleError(error);
                if (error.response && error.response.status === 404) {
                    navigate('/admin/brands'); // Redirect to brand list if brand not found
                }
            });
    }, [id, navigate]);

    if (loading) return <div className="text-center p-4">Loading brand details...</div>;
    if (error) return <div className="text-center p-4 text-red-500">{error}</div>;
    if (!brand) return <div className="text-center p-4">Không tìm thấy thương hiệu</div>;

    // Format date from ISO string or timestamp
    const formatDate = (dateString) => {
        if (!dateString) return 'N/A';
        try {
            return new Date(dateString).toLocaleDateString('vi-VN', {
                year: 'numeric',
                month: '2-digit',
                day: '2-digit',
                hour: '2-digit',
                minute: '2-digit'
            });
        } catch (e) {
            return 'Invalid Date';
        }
    };

    return (
        <div className="container mx-60 p-4">
            <div className="bg-white shadow-md rounded-lg p-6">
                <div className="flex justify-between items-center mb-4">
                    <h2 className="text-xl font-semibold">Tên thương hiệu: {brand.name}</h2>
                    <button
                        onClick={() => navigate('/admin/brands')}
                        className="bg-green-500 hover:bg-green-700 text-white py-2 px-4 rounded flex items-center"
                    >
                        <i className="fas fa-arrow-left mr-2"></i>
                        Về Danh sách
                    </button>
                </div>

                <div className="flex m-4">
                    <div className="w-1/3">
                        {brand.image ? (
                            <img
                                src={`${IMAGE_BASE_URL}${brand.image}`}
                                alt={brand.name}
                                className="w-36 h-36 object-cover border rounded"
                                onError={(e) => {
                                    // Nếu hình ảnh không tải được, hiển thị placeholder
                                    e.target.onerror = null;
                                    e.target.src = 'https://via.placeholder.com/150?text=No+Image';
                                }}
                            />
                        ) : (
                            <div className="w-36 h-36 bg-gray-200 flex items-center justify-center border rounded">
                                <span className="text-3xl text-gray-500">{brand.name?.charAt(0).toUpperCase() || 'B'}</span>
                            </div>
                        )}
                    </div>
                    <div className="w-2/3 pl-4">
                        <p className="py-1"><span className="font-semibold">ID:</span> {brand.id}</p>
                        <p className="py-1"><span className="font-semibold">Slug:</span> {brand.slug || 'N/A'}</p>
                        <p className="py-1"><span className="font-semibold">Miêu tả:</span> {brand.description || 'Không có miêu tả'}</p>
                        <p className="py-1"><span className="font-semibold">Trạng thái:</span> {brand.status === 1 ? 'Xuất bản' : 'Chưa xuất bản'}</p>
                        <p className="py-1"><span className="font-semibold">Ngày tạo:</span> {formatDate(brand.createdAt)}</p>
                        <p className="py-1"><span className="font-semibold">Cập nhật lần cuối:</span> {formatDate(brand.updatedAt)}</p>
                        <p className="py-1"><span className="font-semibold">Tạo bởi:</span> {brand.createdBy || 'N/A'}</p>
                        <p className="py-1"><span className="font-semibold">Cập nhật bởi:</span> {brand.updatedBy || 'N/A'}</p>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default BrandDetail;
