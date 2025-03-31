import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { Link } from 'react-router-dom';

const OrderTrash = () => {
    const [deletedOrders, setDeletedOrders] = useState([]);

    // Fetch the list of deleted orders from the server
    const fetchDeletedOrders = () => {
        axios.get('http://localhost:8000/admin/order/trash')
            .then(response => {
                console.log("Deleted Orders Response:", response.data);
                setDeletedOrders(Array.isArray(response.data) ? response.data : []);
            })
            .catch(error => {
                console.error('Error fetching deleted orders!', error);
            });
    };

    useEffect(() => {
        fetchDeletedOrders(); // Fetch orders on component mount
    }, []);

    // Restore order
    const restoreOrder = (id) => {
        axios.get(`http://localhost:8000/admin/order/restore/${id}`)
            .then(() => {
                // Filter the order out of the list without re-fetching
                setDeletedOrders(deletedOrders.filter(order => order.id !== id));
            })
            .catch(error => {
                console.error('Error restoring order', error);
            });
    };

    // Permanently delete order and re-fetch the list
    const permanentlyDeleteOrder = (id) => {
        axios.delete(`http://localhost:8000/admin/order/destroy/${id}`)
            .then(() => {
                // Re-fetch the deleted orders list after permanent deletion
                fetchDeletedOrders();
            })
            .catch(error => {
                console.error('Error deleting order', error);
            });
    };

    return (
        <div className="container mx-60 p-6">
            <div className="flex justify-end mb-4">
                <a href="/admin/order" className="bg-green-500 text-white px-4 py-2 rounded shadow hover:bg-green-600 transition">
                    <i className="fas fa-arrow-left mr-2"></i>
                    Back to Orders
                </a>
            </div>
            <table className="table-auto w-full bg-white shadow-md rounded-lg overflow-hidden">
                <thead className="bg-gray-200">
                    <tr>
                        <th className="px-4 py-2 text-left">#</th>
                        <th className="px-4 py-2 text-left">Order ID</th>
                        <th className="px-4 py-2 text-left">Username</th>
                        <th className="px-4 py-2 text-left">Date</th>
                        <th className="px-4 py-2 text-center">Actions</th>
                    </tr>
                </thead>
                <tbody>
                    {deletedOrders.map((order, index) => (
                        <tr key={order.id} className="border-b hover:bg-gray-100">
                            <td className="px-4 py-2"><input type="checkbox" /></td>
                            <td className="px-4 py-2">{order.id}</td>
                            <td className="px-4 py-2">{order.username}</td>
                            <td className="px-4 py-2">{new Date(order.created_at).toLocaleDateString()}</td>
                            <td className="px-4 py-2 text-center">
                                <button
                                    onClick={() => restoreOrder(order.id)}
                                    className="bg-blue-500 text-white px-3 py-1 rounded shadow hover:bg-blue-600 transition mr-2"
                                >
                                    <i className="fas fa-trash-restore-alt mr-1"></i>
                                    Restore
                                </button>
                                <button
                                    onClick={() => permanentlyDeleteOrder(order.id)}
                                    className="bg-red-500 text-white px-3 py-1 rounded shadow hover:bg-red-600 transition"
                                >
                                    <i className="fas fa-trash mr-1"></i>
                                    Permanently Delete
                                </button>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
};

export default OrderTrash;
