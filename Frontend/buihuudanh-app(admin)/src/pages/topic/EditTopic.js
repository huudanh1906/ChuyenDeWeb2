import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useParams, useNavigate } from 'react-router-dom';

const EditTopic = () => {
  const { id } = useParams();
  const navigate = useNavigate(); // Hook for navigation
  const [topic, setTopic] = useState({
    name: '',
    description: '',
    sort_order: 0,
    status: 2,
  });
  const [topicsList, setTopicsList] = useState([]);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchTopic = async () => {
      try {
        const response = await axios.get(`http://localhost:8000/admin/topic/edit/${id}`);
        setTopic(response.data.topic);
        setTopicsList(response.data.list);
      } catch (error) {
        setError('There was an error fetching the topic!');
      }
    };

    fetchTopic();
  }, [id]);

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      // Send PUT request to update the topic
      await axios.put(`http://localhost:8000/admin/topic/update/${id}`, topic);
      // After a successful update, navigate back to the topic list page
      navigate('/admin/topic');
    } catch (error) {
      console.error('There was an error updating the topic!', error);
    }
  };

  if (error) {
    return <div>{error}</div>;
  }

  if (!topic) {
    return <div>Loading...</div>;
  }

  return (
    <div className="container mx-60 p-4">
      <h2 className="text-2xl font-bold mb-4">Edit Topic</h2>
      <form onSubmit={handleSubmit} className="space-y-4">
        <div>
          <label htmlFor="name" className="block text-sm font-medium">Topic Name</label>
          <input
            type="text"
            id="name"
            name="name"
            value={topic.name}
            onChange={(e) => setTopic({ ...topic, name: e.target.value })}
            className="border border-gray-300 p-2 w-full"
          />
        </div>

        <div>
          <label htmlFor="description" className="block text-sm font-medium">Description</label>
          <textarea
            id="description"
            name="description"
            value={topic.description}
            onChange={(e) => setTopic({ ...topic, description: e.target.value })}
            className="border border-gray-300 p-2 w-full"
          />
        </div>

        {/* Sort Order Field */}
        <div>
          <label htmlFor="sort_order" className="block text-sm font-medium">Sort Order</label>
          <select
            id="sort_order"
            name="sort_order"
            value={topic.sort_order}
            onChange={(e) => setTopic({ ...topic, sort_order: e.target.value })}
            className="border border-gray-300 p-2 w-full"
          >
            <option value="0">None</option>
            {topicsList.map((row) => (
              <option key={row.id} value={row.sort_order + 1}>
                After: {row.name}
              </option>
            ))}
          </select>
        </div>

        <div>
          <label htmlFor="status" className="block text-sm font-medium">Status</label>
          <select
            id="status"
            name="status"
            value={topic.status}
            onChange={(e) => setTopic({ ...topic, status: e.target.value })}
            className="border border-gray-300 p-2 w-full"
          >
            <option value="2">Unpublished</option>
            <option value="1">Published</option>
          </select>
        </div>

        <button type="submit" className="bg-green-500 text-white p-2 rounded">Update Topic</button>
      </form>
    </div>
  );
};

export default EditTopic;
