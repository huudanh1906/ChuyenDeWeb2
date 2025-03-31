import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useParams, useNavigate } from 'react-router-dom';

const EditUser = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const [user, setUser] = useState({
        name: '',
        phone: '',
        email: '',
        gender: '',
        address: '',
        username: '',
        roles: 'customer',
        status: '1',
        image: null,
    });
    const [errors, setErrors] = useState({});
    const [loading, setLoading] = useState(true);
    const [formSubmitting, setFormSubmitting] = useState(false);
    const [originalImage, setOriginalImage] = useState(null);

    // URL cơ sở cho API backend
    const API_BASE_URL = 'http://localhost:8080/api';

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

    // Kiểm tra và chuyển hướng nếu không có token
    useEffect(() => {
        const token = getAuthToken();
        if (!token) {
            alert('Bạn cần đăng nhập để truy cập trang này.');
            navigate('/login');
        }
    }, [navigate]);

    useEffect(() => {
        // Fetch user data
        const fetchUser = async () => {
            try {
                setLoading(true);
                const response = await axios.get(`${API_BASE_URL}/users/${id}`, getAuthHeaders());
                console.log("User data fetched:", response.data);

                // Chuyển đổi giới tính từ số sang chuỗi để khớp với dropdown
                const genderString = response.data.gender !== null ? response.data.gender.toString() : '1';
                const statusString = response.data.status !== null ? response.data.status.toString() : '1';

                // Lưu ảnh gốc để kiểm tra thay đổi
                setOriginalImage(response.data.image);

                setUser({
                    ...response.data,
                    gender: genderString,
                    status: statusString,
                    password: '',
                    password_confirmation: ''
                });
                setLoading(false);
            } catch (error) {
                console.error('Error fetching user:', error);
                setLoading(false);

                if (error.response && error.response.status === 401) {
                    alert('Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại.');
                    localStorage.removeItem('authToken');
                    navigate('/login');
                } else {
                    alert('Không thể tải thông tin người dùng. Vui lòng thử lại sau.');
                    navigate('/admin/user');
                }
            }
        };

        if (getAuthToken()) {
            fetchUser();
        }
    }, [id, navigate]);

    const handleChange = (e) => {
        const { name, value } = e.target;
        console.log(`Field ${name} changed to ${value}`);
        setUser((prev) => ({
            ...prev,
            [name]: value,
        }));

        // Xóa lỗi khi người dùng thay đổi giá trị
        if (errors[name]) {
            setErrors(prev => ({
                ...prev,
                [name]: null
            }));
        }
    };

    const handleFileChange = (e) => {
        const file = e.target.files[0];
        if (!file) return;

        console.log("File selected:", file.name);

        // Convert the file to Base64
        const reader = new FileReader();
        reader.onloadend = () => {
            setUser((prev) => ({
                ...prev,
                image: reader.result, // Set Base64 image
            }));
            console.log("Image converted to base64");
        };
        reader.readAsDataURL(file);
    };

    const validateForm = () => {
        const newErrors = {};

        if (!user.name.trim()) newErrors.name = ['Tên không được để trống'];
        if (!user.email.trim()) newErrors.email = ['Email không được để trống'];
        if (!user.phone.trim()) newErrors.phone = ['Điện thoại không được để trống'];
        if (user.phone.length !== 10) newErrors.phone = ['Số điện thoại phải có 10 chữ số'];
        if (!user.address.trim()) newErrors.address = ['Địa chỉ không được để trống'];
        if (!user.username.trim()) newErrors.username = ['Tên đăng nhập không được để trống'];
        if (user.password && user.password !== user.password_confirmation) {
            newErrors.password = ['Mật khẩu xác nhận không khớp'];
        }

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!validateForm()) return;

        setFormSubmitting(true);
        console.log("Form submission started with data:", {
            name: user.name,
            username: user.username,
            email: user.email,
            phone: user.phone,
            address: user.address,
            roles: user.roles,
            status: user.status,
            gender: user.gender,
            hasPassword: !!user.password,
            hasImage: user.image !== originalImage
        });

        try {
            // Sử dụng PATCH thay vì PUT để chỉ cập nhật các trường được thay đổi
            const formData = new FormData();

            // Thêm các trường cơ bản
            formData.append('name', user.name);
            formData.append('username', user.username);
            formData.append('email', user.email);
            formData.append('phone', user.phone || '');
            formData.append('address', user.address || '');
            formData.append('roles', user.roles);
            formData.append('status', parseInt(user.status, 10));
            formData.append('gender', parseInt(user.gender, 10));

            // Chỉ thêm password nếu có nhập mới
            if (user.password) {
                formData.append('password', user.password);
            }

            // Chỉ gửi ảnh nếu có thay đổi ảnh
            const isNewImage = user.image && user.image.includes('base64');
            if (isNewImage) {
                formData.append('imageBase64', user.image);
                console.log("Sending new image as base64");
            }

            const token = getAuthToken();
            const config = {
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'multipart/form-data'
                }
            };

            // Log tất cả các key trong formData
            console.log("Form data being sent:");
            for (let pair of formData.entries()) {
                console.log(pair[0] + ': ' + (pair[0] === 'imageBase64' ? 'Base64 Image Data' : pair[1]));
            }

            // Sử dụng PATCH thay vì PUT
            const response = await axios.patch(`${API_BASE_URL}/users/${id}`, formData, config);
            console.log("Update response:", response.data);
            alert('Cập nhật người dùng thành công');
            navigate('/admin/user');
        } catch (error) {
            console.error('Error updating user:', error);
            console.error('Error details:', error.response?.data);

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
                    alert('Đã xảy ra lỗi khi cập nhật người dùng.');
                }
            } else {
                alert('Không thể kết nối đến máy chủ. Vui lòng thử lại sau.');
            }
        } finally {
            setFormSubmitting(false);
        }
    };

    if (loading) {
        return <div className="text-center p-5">Đang tải thông tin người dùng...</div>;
    }

    return (
        <div className="max-w-4xl mx-auto p-5 bg-white rounded shadow-md">
            <h1 className="text-2xl font-bold mb-5">Chỉnh sửa thành viên</h1>
            <form onSubmit={handleSubmit}>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div className="mb-4">
                        <label htmlFor="name" className="block text-sm font-medium text-gray-700">Họ tên <span className="text-red-500">*</span></label>
                        <input
                            type="text"
                            id="name"
                            name="name"
                            value={user.name}
                            onChange={handleChange}
                            className={`mt-1 block w-full p-2 border ${errors.name ? 'border-red-500' : 'border-gray-300'} rounded-md`}
                        />
                        {errors.name && <span className="text-red-500 text-sm">{errors.name[0]}</span>}
                    </div>

                    <div className="mb-4">
                        <label htmlFor="phone" className="block text-sm font-medium text-gray-700">Điện thoại <span className="text-red-500">*</span></label>
                        <input
                            type="text"
                            id="phone"
                            name="phone"
                            value={user.phone || ''}
                            onChange={handleChange}
                            className={`mt-1 block w-full p-2 border ${errors.phone ? 'border-red-500' : 'border-gray-300'} rounded-md`}
                        />
                        {errors.phone && <span className="text-red-500 text-sm">{errors.phone[0]}</span>}
                    </div>

                    <div className="mb-4">
                        <label htmlFor="email" className="block text-sm font-medium text-gray-700">Email <span className="text-red-500">*</span></label>
                        <input
                            type="text"
                            id="email"
                            name="email"
                            value={user.email}
                            onChange={handleChange}
                            className={`mt-1 block w-full p-2 border ${errors.email ? 'border-red-500' : 'border-gray-300'} rounded-md`}
                        />
                        {errors.email && <span className="text-red-500 text-sm">{errors.email[0]}</span>}
                    </div>

                    <div className="mb-4">
                        <label htmlFor="gender" className="block text-sm font-medium text-gray-700">Giới tính</label>
                        <select
                            id="gender"
                            name="gender"
                            value={user.gender}
                            onChange={handleChange}
                            className="mt-1 block w-full p-2 border border-gray-300 rounded-md"
                        >
                            <option value="1">Nam</option>
                            <option value="0">Nữ</option>
                        </select>
                    </div>

                    <div className="mb-4">
                        <label htmlFor="address" className="block text-sm font-medium text-gray-700">Địa chỉ <span className="text-red-500">*</span></label>
                        <input
                            type="text"
                            id="address"
                            name="address"
                            value={user.address || ''}
                            onChange={handleChange}
                            className={`mt-1 block w-full p-2 border ${errors.address ? 'border-red-500' : 'border-gray-300'} rounded-md`}
                        />
                        {errors.address && <span className="text-red-500 text-sm">{errors.address[0]}</span>}
                    </div>

                    <div className="mb-4">
                        <label htmlFor="username" className="block text-sm font-medium text-gray-700">Tên đăng nhập <span className="text-red-500">*</span></label>
                        <input
                            type="text"
                            id="username"
                            name="username"
                            value={user.username}
                            onChange={handleChange}
                            className={`mt-1 block w-full p-2 border ${errors.username ? 'border-red-500' : 'border-gray-300'} rounded-md`}
                        />
                        {errors.username && <span className="text-red-500 text-sm">{errors.username[0]}</span>}
                    </div>

                    <div className="mb-4">
                        <label htmlFor="password" className="block text-sm font-medium text-gray-700">Mật khẩu</label>
                        <input
                            type="password"
                            id="password"
                            name="password"
                            onChange={handleChange}
                            className={`mt-1 block w-full p-2 border ${errors.password ? 'border-red-500' : 'border-gray-300'} rounded-md`}
                            placeholder="Để trống nếu không thay đổi"
                        />
                        {errors.password && <span className="text-red-500 text-sm">{errors.password[0]}</span>}
                    </div>

                    <div className="mb-4">
                        <label htmlFor="password_confirmation" className="block text-sm font-medium text-gray-700">Xác nhận mật khẩu</label>
                        <input
                            type="password"
                            id="password_confirmation"
                            name="password_confirmation"
                            onChange={handleChange}
                            className="mt-1 block w-full p-2 border border-gray-300 rounded-md"
                            placeholder="Để trống nếu không thay đổi"
                        />
                    </div>

                    <div className="mb-4">
                        <label htmlFor="roles" className="block text-sm font-medium text-gray-700">Quyền</label>
                        <select
                            id="roles"
                            name="roles"
                            value={user.roles}
                            onChange={handleChange}
                            className="mt-1 block w-full p-2 border border-gray-300 rounded-md"
                        >
                            <option value="USER">Khách hàng</option>
                            <option value="ADMIN">Quản lý</option>
                        </select>
                    </div>

                    <div className="mb-4">
                        <label htmlFor="image" className="block text-sm font-medium text-gray-700">Hình</label>
                        <input
                            type="file"
                            id="image"
                            name="image"
                            onChange={handleFileChange}
                            className="mt-1 block w-full p-2 border border-gray-300 rounded-md"
                            accept="image/*"
                        />
                        {user.image && (
                            <div className="mt-2">
                                <p className="text-xs text-gray-500 mb-1">Hình ảnh hiện tại:</p>
                                <img
                                    src={user.image.includes('base64') ? user.image : `http://localhost:8080/uploads/users/${user.image}`}
                                    alt="Hình ảnh người dùng"
                                    className="h-20 w-20 object-cover"
                                />
                            </div>
                        )}
                    </div>

                    <div className="mb-4">
                        <label htmlFor="status" className="block text-sm font-medium text-gray-700">Trạng thái</label>
                        <select
                            id="status"
                            name="status"
                            value={user.status}
                            onChange={handleChange}
                            className="mt-1 block w-full p-2 border border-gray-300 rounded-md"
                        >
                            <option value="1">Hoạt động</option>
                            <option value="2">Không hoạt động</option>
                        </select>
                    </div>
                </div>
                <div className="flex justify-end mt-6">
                    <button
                        type="submit"
                        className="mr-2 px-4 py-2 bg-green-600 text-white rounded hover:bg-green-500 disabled:bg-green-300"
                        disabled={formSubmitting}
                    >
                        {formSubmitting ? 'Đang lưu...' : <><i className="fa fa-save"></i> Lưu</>}
                    </button>
                    <button
                        type="button"
                        onClick={() => navigate('/admin/user')}
                        className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-500"
                        disabled={formSubmitting}
                    >
                        <i className="fa fa-arrow-left"></i> Hủy
                    </button>
                </div>
            </form>
        </div>
    );
};

export default EditUser;
