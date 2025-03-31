import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { Link } from 'react-router-dom';

const Order = () => {
    const [orders, setOrders] = useState([]);

    useEffect(() => {
        axios.get('http://localhost:8000/admin/order')
            .then(response => {
                console.log("API Response:", response.data);
                setOrders(Array.isArray(response.data) ? response.data : []);
            })
            .catch(error => {
                console.error('There was an error!', error);
            });
    }, []);

    // Toggle order status
    const toggleStatus = (id, currentStatus) => {
        axios.get(`http://localhost:8000/admin/order/status/${id}`)
            .then(response => {
                setOrders(orders.map(order =>
                    order.id === id ? { ...order, status: currentStatus === 1 ? 2 : 1 } : order
                ));
            })
            .catch(error => {
                console.error('Error toggling status', error);
            });
    };

    // Delete order
    const deleteOrder = (id) => {
        axios.get(`http://localhost:8000/admin/order/delete/${id}`)
            .then(() => {
                setOrders(orders.filter(order => order.id !== id));
            })
            .catch(error => console.error('Error deleting order', error));
    };

    return (
        <div className="container mx-60 p-4">
            <div className="flex justify-end mb-4">
                <button className="bg-green-500 text-white px-4 py-2 rounded">
                    <Link to="/admin/order/trash" className="flex items-center justify-center h-full w-full">Trash</Link>
                </button>
            </div>
            <div className="overflow-x-auto">
                <table className="table-auto w-full bg-white shadow-md rounded-lg">
                    <thead>
                        <tr className="bg-gray-200 text-left text-sm">
                            <th className="p-3">#</th>
                            <th className="p-3">Customer name</th>
                            <th className="p-3">Order Date</th>
                            <th className="p-3">Status</th>
                            <th className="p-3">Actions</th>
                            <th className="p-3">Order ID</th>
                        </tr>
                    </thead>
                    <tbody>
                        {orders.map((order, index) => (
                            <tr key={order.id} className="border-t">
                                <td className="p-3">{index + 1}</td>
                                <td className="p-3">{order.username}</td>
                                <td className="p-3">{new Date(order.created_at).toLocaleString()}</td>
                                <td className="p-3">
                                    <button
                                        onClick={() => toggleStatus(order.id, order.status)}
                                        className={order.status === 1 ? "bg-green-500 text-white px-2 py-1 rounded" : "bg-red-500 text-white px-2 py-1 rounded"}>
                                        {order.status === 1 ? 'Active' : 'Inactive'}
                                    </button>
                                </td>
                                <td className="p-3 space-x-2">
                                    {/* View button to navigate to OrderDetail */}
                                    <Link to={`/admin/order/show/${order.id}`} className="bg-blue-500 text-white px-2 py-1 rounded">
                                        View
                                    </Link>

                                    <button
                                        onClick={() => deleteOrder(order.id)}
                                        className="bg-red-500 text-white px-2 py-1 rounded"
                                    >
                                        Delete
                                    </button>
                                </td>
                                <td className="p-3">{order.id}</td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
};

export default Order;
