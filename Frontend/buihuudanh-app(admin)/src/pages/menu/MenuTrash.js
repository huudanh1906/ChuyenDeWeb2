import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { Link } from 'react-router-dom';

const MenuTrash = () => {
    const [menus, setMenus] = useState([]);

    const fetchMenus = async () => {
        try {
            const response = await axios.get('http://localhost:8000/admin/menu/trash'); // Adjust this URL according to your API route
            setMenus(response.data);
        } catch (error) {
            console.error('Error fetching menus:', error);
        }
    };

    useEffect(() => {
        fetchMenus();
    }, []);

    const handleDelete = async (id) => {
        try {
            await axios.delete(`http://localhost:8000/admin/menu/destroy/${id}`); // Adjust this URL according to your API route
            fetchMenus(); // Refresh the menu list after deletion
        } catch (error) {
            console.error('Error deleting menu:', error);
        }
    };

    const handleRestore = async (id) => {
        try {
            await axios.get(`http://localhost:8000/admin/menu/restore/${id}`); // Adjust this URL according to your API route
            fetchMenus(); // Refresh the menu list after restoring
        } catch (error) {
            console.error('Error restoring menu:', error);
        }
    };

    return (
        <div className="card mx-60">
            <div className="card-header">
                <div className="flex justify-end">
                    <Link to="/admin/menu" className="btn btn-success">
                        <i className="fas fa-arrow-left mr-2"></i>Về Danh sách
                    </Link>
                </div>
            </div>
            <div className="card-body">
                <table className="min-w-full bg-white border border-gray-200">
                    <thead>
                        <tr className="bg-gray-100">
                            <th className="border px-4 py-2">#</th>
                            <th className="border px-4 py-2">Tên Menu</th>
                            <th className="border px-4 py-2 text-center">Hoạt động</th>
                            <th className="border px-4 py-2">ID</th>
                        </tr>
                    </thead>
                    <tbody>
                        {menus.length > 0 ? (
                            menus.map((menu, index) => (
                                <tr key={menu.id} className="hover:bg-gray-100">
                                    <td className="border px-4 py-2">
                                        <input type="checkbox" />
                                    </td>
                                    <td className="border px-4 py-2">{menu.name}</td>
                                    <td className="border px-4 py-2 text-center">
                                        <button onClick={() => handleRestore(menu.id)} className="bg-blue-500 text-white px-2 py-1 rounded ml-2">
                                            <i className="fas fa-trash-restore-alt">Restore</i>
                                        </button>
                                        <Link to={`/admin/menu/show/${menu.id}`} className="bg-green-500 text-white px-2 py-1 rounded ml-2">
                                            <i className="fas fa-eye">View</i>
                                        </Link>
                                        <button onClick={() => handleDelete(menu.id)} className="bg-red-500 text-white px-2 py-1 rounded ml-2">
                                            <i className="fas fa-trash">Permantly Delete</i>
                                        </button>
                                    </td>
                                    <td className="border px-4 py-2">{menu.id}</td>
                                </tr>
                            ))
                        ) : (
                            <tr>
                                <td colSpan="4" className="text-center border px-4 py-2">No deleted menus found.</td>
                            </tr>
                        )}
                    </tbody>
                </table>
            </div>
        </div>
    );
};

export default MenuTrash;
