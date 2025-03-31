import React, { useState, useEffect } from 'react';
import axios from 'axios';

const UserTrash = () => {
    const [deletedUsers, setDeletedUsers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // URL cơ sở cho API backend
    const API_BASE_URL = 'http://localhost:8080/api';
    // URL cho hình ảnh
    const IMAGE_BASE_URL = 'http://localhost:8080/uploads/users/';

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

    // Fetch the list of deleted users from the server
    const fetchDeletedUsers = () => {
        // Kiểm tra xem đã đăng nhập chưa
        const token = getAuthToken();
        if (!token) {
            setError('Bạn cần đăng nhập để xem danh sách người dùng đã xóa.');
            setLoading(false);
            return;
        }

        setLoading(true);
        axios.get(`${API_BASE_URL}/users/trash`, getAuthHeaders())
            .then(response => {
                console.log("Deleted Users Response:", response.data);
                setDeletedUsers(Array.isArray(response.data) ? response.data : []);
                setLoading(false);
            })
            .catch(error => {
                console.error('Error fetching deleted users!', error);
                if (error.response && error.response.status === 401) {
                    setError('Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại.');
                    localStorage.removeItem('authToken');
                    window.location.href = '/login';
                } else {
                    setError('Không thể tải danh sách người dùng đã xóa. Vui lòng thử lại sau.');
                }
                setLoading(false);
            });
    };

    useEffect(() => {
        fetchDeletedUsers(); // Fetch Users on component mount
    }, []);

    // Hàm kiểm tra và trả về URL hình ảnh hợp lệ
    const getImageUrl = (imageName) => {
        if (!imageName) return null;
        return `${IMAGE_BASE_URL}${imageName}`;
    };

    // Restore User
    const restoreUser = (id) => {
        axios.put(`${API_BASE_URL}/users/${id}/restore`, {}, getAuthHeaders())
            .then(response => {
                // Filter the user out of the list without re-fetching
                setDeletedUsers(deletedUsers.filter(user => user.id !== id));
            })
            .catch(error => {
                console.error('Error restoring user', error);
                if (error.response && error.response.status === 401) {
                    alert('Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại.');
                    localStorage.removeItem('authToken');
                    window.location.href = '/login';
                } else {
                    alert('Không thể khôi phục người dùng. Vui lòng thử lại.');
                }
            });
    };

    // Permanently delete user and re-fetch the list
    const permanentlyDeleteUser = (id) => {
        axios.delete(`${API_BASE_URL}/users/${id}`, getAuthHeaders())
            .then(() => {
                // Re-fetch the deleted users list after permanent deletion
                fetchDeletedUsers();
            })
            .catch(error => {
                console.error('Error deleting user', error);
                if (error.response && error.response.status === 401) {
                    alert('Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại.');
                    localStorage.removeItem('authToken');
                    window.location.href = '/login';
                } else {
                    alert('Không thể xóa người dùng. Vui lòng thử lại.');
                }
            });
    };

    if (loading) return <div className="text-center p-4">Loading deleted users...</div>;
    if (error) return <div className="text-center p-4 text-red-500">{error}</div>;

    return (
        <div className="container mx-60 p-6">
            <div className="flex justify-end mb-4">
                <a href="/admin/user" className="bg-green-500 text-white px-4 py-2 rounded shadow hover:bg-green-600 transition">
                    <i className="fas fa-arrow-left mr-2"></i>
                    Về Danh sách
                </a>
            </div>
            {deletedUsers.length === 0 ? (
                <div className="text-center p-4">Không có người dùng nào trong thùng rác</div>
            ) : (
                <table className="table-auto w-full bg-white shadow-md rounded-lg overflow-hidden">
                    <thead className="bg-gray-200">
                        <tr>
                            <th className="px-4 py-2 text-left">#</th>
                            <th className="px-4 py-2 text-left">Hình ảnh</th>
                            <th className="px-4 py-2 text-left">Tên người dùng</th>
                            <th className="px-4 py-2 text-center">Thao tác</th>
                            <th className="px-4 py-2 text-left">ID</th>
                        </tr>
                    </thead>
                    <tbody>
                        {deletedUsers.map((user, index) => (
                            <tr key={user.id} className="border-b hover:bg-gray-100">
                                <td className="px-4 py-2"><input type="checkbox" /></td>
                                <td className="px-4 py-2">
                                    {user.image ? (
                                        <img
                                            src={getImageUrl(user.image)}
                                            alt={user.name}
                                            className="w-12 h-12 object-cover rounded-full"
                                            onError={(e) => {
                                                // Nếu hình ảnh không tải được, hiển thị placeholder
                                                e.target.onerror = null;
                                                e.target.src = 'https://via.placeholder.com/150?text=No+Image';
                                            }}
                                        />
                                    ) : (
                                        <div className="w-12 h-12 bg-gray-200 rounded-full flex items-center justify-center">
                                            <span className="text-gray-500">{user.name?.charAt(0).toUpperCase() || 'U'}</span>
                                        </div>
                                    )}
                                </td>
                                <td className="px-4 py-2">{user.name}</td>
                                <td className="px-4 py-2 text-center">
                                    <button
                                        onClick={() => restoreUser(user.id)}
                                        className="bg-blue-500 text-white px-3 py-1 rounded shadow hover:bg-blue-600 transition mr-2"
                                    >
                                        <i className="fas fa-trash-restore-alt mr-1"></i>
                                        Khôi phục
                                    </button>
                                    <button
                                        onClick={() => permanentlyDeleteUser(user.id)}
                                        className="bg-red-500 text-white px-3 py-1 rounded shadow hover:bg-red-600 transition"
                                    >
                                        <i className="fas fa-trash mr-1"></i>
                                        Xóa vĩnh viễn
                                    </button>
                                </td>
                                <td className="px-4 py-2">{user.id}</td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            )}
        </div>
    );
};

export default UserTrash;
