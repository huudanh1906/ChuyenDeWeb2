import React, { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import axios from 'axios';

const ContactDetail = () => {
    const { id } = useParams();
    const [contact, setContact] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchContact = async () => {
            try {
                const response = await axios.get(`http://localhost:8000/admin/contact/show/${id}`);
                setContact(response.data);
            } catch (error) {
                console.error('Error fetching contact details:', error);
            } finally {
                setLoading(false);
            }
        };

        fetchContact();
    }, [id]);

    if (loading) {
        return <div>Loading...</div>;
    }

    if (!contact) {
        return <div>Contact not found.</div>;
    }

    return (
        <div className="container mx-60 my-4">
            <div className="card">
                <div className="card-header flex justify-between items-center p-4">
                    <h2 className="text-xl font-semibold">Chi tiết liên hệ</h2>
                    <Link to="/admin/contact" className="btn btn-success">
                        <i className="fas fa-arrow-left mr-2"></i> Về Danh sách
                    </Link>
                </div>
                <div className="p-4">
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                        <div className="bg-white p-4 rounded shadow">
                            <p className="font-semibold">ID: <span className="font-normal">{contact.id}</span></p>
                            <p className="font-semibold">Tên khách hàng: <span className="font-normal">{contact.username}</span></p>
                            <p className="font-semibold">Tên người liên hệ: <span className="font-normal">{contact.name}</span></p>
                            <p className="font-semibold">Email: <span className="font-normal">{contact.email}</span></p>
                            <p className="font-semibold">Điện thoại: <span className="font-normal">{contact.phone}</span></p>
                            <p className="font-semibold">Nội dung liên hệ: <span className="font-normal">{contact.content}</span></p>
                            <p className="font-semibold">Ngày liên hệ: <span className="font-normal">{new Date(contact.created_at).toLocaleString()}</span></p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default ContactDetail;
