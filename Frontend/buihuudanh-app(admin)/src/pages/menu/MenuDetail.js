import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useParams, useNavigate } from 'react-router-dom';

const MenuDetail = () => {
    const { id } = useParams(); // Get the menu ID from URL parameters
    const [menu, setMenu] = useState(null);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchMenuDetail = async () => {
            try {
                const response = await axios.get(`http://localhost:8000/admin/menu/show/${id}`);
                setMenu(response.data);
            } catch (error) {
                console.error('Error fetching menu detail:', error);
                // Optionally handle error (e.g., redirect to menu list)
                navigate('/admin/menu'); // Redirect to the menu list on error
            }
        };

        fetchMenuDetail();
    }, [id, navigate]);

    if (!menu) {
        return <div>Loading...</div>; // Loading state while fetching data
    }

    return (
        <div className="container mx-60 p-5">
            <div className="bg-white shadow-md rounded-lg p-5">
                <div className="flex justify-between items-center mb-5">
                    <h2 className="text-2xl font-bold">Tên chủ đề: {menu.name}</h2>
                    <a
                        href="/admin/menu"
                        className="bg-green-500 text-white px-4 py-2 rounded flex items-center"
                    >
                        <i className="fas fa-arrow-left mr-2"></i> Về Danh sách
                    </a>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div className="bg-gray-100 p-4 rounded shadow">
                        <p className="font-semibold">ID: <span className="font-normal">{menu.id}</span></p>
                        <p className="font-semibold">Vị trí: <span className="font-normal">{menu.position}</span></p>
                        <p className="font-semibold">Kiểu Menu: <span className="font-normal">{menu.type}</span></p>
                        <p className="font-semibold">Created At: <span className="font-normal">{new Date(menu.created_at).toLocaleString()}</span></p>
                        <p className="font-semibold">Updated At: <span className="font-normal">{new Date(menu.updated_at).toLocaleString()}</span></p>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default MenuDetail;
