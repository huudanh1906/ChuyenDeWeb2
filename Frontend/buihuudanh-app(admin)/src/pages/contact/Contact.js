// Contact.js
import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { Link } from 'react-router-dom';

const Contact = () => {
    const [contacts, setContacts] = useState([]);
    const [error, setError] = useState(''); // Error state

    useEffect(() => {
        const fetchContacts = async () => {
            try {
                const response = await axios.get('http://localhost:8000/admin/contact');
                setContacts(response.data);
            } catch (error) {
                console.error("Error fetching contacts:", error);
            }
        };
        fetchContacts();
    }, []);

    const handleDelete = async (id) => {
        if (window.confirm("Are you sure you want to delete this contact?")) {
            try {
                await axios.get(`http://localhost:8000/admin/contact/delete/${id}`);
                setContacts(contacts.filter(contact => contact.id !== id)); // Update state to remove deleted contact
                alert("Contact deleted successfully.");
            } catch (error) {
                console.error("Error deleting contact:", error);
                alert("An error occurred while deleting the contact.");
            }
        }
    };

    const changeStatus = async (id) => {
        try {
            await axios.get(`http://localhost:8000/admin/contact/status/${id}`);
            const response = await axios.get('http://localhost:8000/admin/contact');
            setContacts(response.data);
        } catch (error) {
            setError('Error changing status');
        }
    };

    return (
        <div className="container mx-60 px-4 py-6">
            <div className="flex justify-end mb-4">
                <Link to="/admin/contact/trash" className="btn btn-success">
                    <i className="fas fa-trash mr-2"></i> Thùng rác
                </Link>
            </div>
            <div className="bg-white shadow-md rounded-lg">
                <div className="overflow-x-auto">
                    <table className="min-w-full divide-y divide-gray-200">
                        <thead className="bg-gray-50">
                            <tr>
                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">#</th>
                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Tên người liên hệ</th>
                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Tên người quản trị</th>
                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Email</th>
                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Điện thoại</th>
                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Hoạt động</th>
                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">ID</th>
                            </tr>
                        </thead>
                        <tbody className="bg-white divide-y divide-gray-200">
                            {contacts.map((contact, index) => (
                                <tr key={contact.id}>
                                    <td className="px-6 py-4 whitespace-nowrap">
                                        <input type="checkbox" />
                                    </td>
                                    <td className="px-6 py-4 whitespace-nowrap">{contact.name}</td>
                                    <td className="px-6 py-4 whitespace-nowrap">{contact.username}</td>
                                    <td className="px-6 py-4 whitespace-nowrap">{contact.email}</td>
                                    <td className="px-6 py-4 whitespace-nowrap">{contact.phone}</td>
                                    <td className="px-6 py-4 whitespace-nowrap text-center">
                                        <button
                                            onClick={() => changeStatus(contact.id, contact.status)}
                                            className={contact.status === 1 ? "bg-green-500 text-white px-2 py-1 rounded mr-2" : "bg-red-500 text-white px-2 py-1 rounded mr-2"}>
                                            {contact.status === 1 ? 'Active' : 'Inactive'}
                                        </button>
                                        <Link to={`/admin/contact/show/${contact.id}`} className="bg-blue-500 text-white px-2 py-1 rounded mr-2">
                                            <i className="fas fa-edit">View</i>
                                        </Link>
                                        <button onClick={() => handleDelete(contact.id)} className="bg-red-500 text-white px-2 py-1 rounded">
                                            <i className="fas fa-trash">Delete</i>
                                        </button>
                                    </td>
                                    <td className="px-6 py-4 whitespace-nowrap">{contact.id}</td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    );
};

export default Contact;
