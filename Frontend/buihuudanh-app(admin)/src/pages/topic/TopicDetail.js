import React, { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import axios from "axios";

const TopicDetail = () => {
  const { id } = useParams(); // Get topic ID from route params
  const [topic, setTopic] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    // Fetch the topic details from the API
    const fetchTopic = async () => {
      try {
        const response = await axios.get(`http://localhost:8000/admin/topic/show/${id}`);
        setTopic(response.data);
        setLoading(false);
      } catch (err) {
        setError("Error fetching topic details.");
        setLoading(false);
      }
    };

    fetchTopic();
  }, [id]);

  if (loading) return <div className="text-center mt-10">Loading...</div>;
  if (error) return <div className="text-center mt-10 text-red-500">{error}</div>;

  return (
    <div className="container mx-60 p-6">
      <div className="bg-white shadow-md rounded-lg overflow-hidden">
        <div className="px-6 py-4 flex justify-between items-center">
          <h2 className="text-2xl font-bold text-gray-800">Tên chủ đề: {topic.name}</h2>
          <Link
            to="/admin/topic"
            className="bg-green-500 hover:bg-green-700 text-white font-bold py-2 px-4 rounded"
          >
            <i className="fas fa-arrow-left mr-2"></i> Về Danh sách
          </Link>
        </div>
        <div className="p-6">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div className="text-lg">
              <p><strong>ID:</strong> {topic.id}</p>
              <p><strong>Miêu tả:</strong> {topic.description}</p>
              <p><strong>Created At:</strong> {new Date(topic.created_at).toLocaleString()}</p>
              <p><strong>Updated At:</strong> {new Date(topic.updated_at).toLocaleString()}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default TopicDetail;
