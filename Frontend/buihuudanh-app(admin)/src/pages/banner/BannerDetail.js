import React, { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import axios from "axios";

const BannerDetail = () => {
    const { id } = useParams();
    const [banner, setBanner] = useState(null); // Initialize with null

    useEffect(() => {
        const fetchBanner = async () => {
            try {
                const response = await axios.get(`http://localhost:8000/admin/banner/show/${id}`);
                console.log("API Response: ", response.data); // Ensure this logs correct data
                setBanner(response.data); // Set the banner data
            } catch (error) {
                console.error("Error fetching banner details", error);
            }
        };

        fetchBanner();
    }, [id]);

    // Change the loading condition to check if banner is null
    if (!banner) {
        return <p className="text-center">Loading...</p>;
    }

    return (
        <div className="container mx-60 p-6">
            <div className="bg-white shadow-md rounded-lg">
                <div className="flex justify-between items-center border-b px-6 py-4">
                    <h2 className="text-2xl font-semibold">Banner Name: {banner.name}</h2>
                    <Link
                        to="/admin/banners"
                        className="bg-green-500 text-white py-2 px-4 rounded hover:bg-green-600 flex items-center"
                    >
                        <i className="fas fa-arrow-left mr-2"></i>Back to List
                    </Link>
                </div>

                <div className="flex p-6 space-x-4">
                    <div className="w-1/4">
                        <img
                            src={`http://localhost:8000/imgs/banners/${banner.image}`}
                            alt={banner.image}
                            className="w-40 h-auto rounded-lg"
                        />
                    </div>
                    <div className="w-3/4">
                        <p className="text-lg mb-2">ID: {banner.id}</p>
                        <p className="text-lg mb-2">Position: {banner.position}</p>
                        <p className="text-lg mb-2">Description: {banner.description || "No description available"}</p>
                        <p className="text-lg mb-2">Created At: {new Date(banner.created_at).toLocaleString()}</p>
                        <p className="text-lg">Updated At: {new Date(banner.updated_at).toLocaleString()}</p>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default BannerDetail;
