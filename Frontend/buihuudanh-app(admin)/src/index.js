// index.js
import React from 'react';
import ReactDOM from 'react-dom';
import { BrowserRouter as Router } from 'react-router-dom';
import App from './App';
import './index.css';
import axios from 'axios';

// Set the CSRF token for Axios
const csrfToken = document.querySelector('meta[name="csrf-token"]').getAttribute('content');
axios.defaults.headers.common['X-CSRF-TOKEN'] = csrfToken;

ReactDOM.render(
  <Router>
    <App />
  </Router>,
  document.getElementById('root')
);
