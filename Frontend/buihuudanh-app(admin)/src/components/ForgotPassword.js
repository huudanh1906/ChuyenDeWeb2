import React, { useState } from 'react';
import axios from 'axios';

const ForgotPassword = () => {
    const [email, setEmail] = useState('');
    const [message, setMessage] = useState('');
    const [error, setError] = useState('');

    const handleForgotPassword = async (e) => {
        e.preventDefault();

        try {
            const response = await axios.post('http://localhost:8000/admin/forgot-password', { email });
            setMessage(response.data.message);
            setError('');
        } catch (err) {
            setError(err.response?.data?.error || 'An error occurred.');
            setMessage('');
        }
    };

    return (
        <div className="flex items-center justify-center min-h-screen bg-gray-100">
            <div className="w-full max-w-md p-8 space-y-6 bg-white rounded-lg shadow-lg">
                <h2 className="text-2xl font-bold text-center text-gray-800">Forgot Password</h2>
                <form onSubmit={handleForgotPassword} className="space-y-4">
                    <div>
                        <label htmlFor="email" className="block text-sm font-medium text-gray-700">
                            Email Address
                        </label>
                        <input
                            type="email"
                            id="email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            required
                            className="w-full px-4 py-2 mt-1 border rounded-lg focus:outline-none focus:ring-2 focus:ring-yellow-400"
                        />
                    </div>
                    <button
                        type="submit"
                        className="w-full px-4 py-2 font-semibold text-white bg-yellow-400 rounded-lg hover:bg-yellow-500 focus:outline-none focus:ring-2 focus:ring-yellow-400 focus:ring-offset-2"
                    >
                        Send Password Reset Link
                    </button>
                </form>
                {message && <p className="mt-4 text-sm font-semibold text-green-600">{message}</p>}
                {error && <p className="mt-4 text-sm font-semibold text-red-600">{error}</p>}
            </div>
        </div>
    );
};

export default ForgotPassword;
