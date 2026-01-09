// API Base URL
const API_BASE_URL = 'http://localhost:8080/api';

// Helper function to show messages
function showMessage(message, type = 'success') {
    const messageDiv = document.getElementById('message');
    messageDiv.textContent = message;
    messageDiv.className = `message ${type}`;
    setTimeout(() => {
        messageDiv.className = 'message';
    }, 5000);
}

// API Request Helper
async function apiRequest(endpoint, options = {}) {
    try {
        const response = await fetch(`${API_BASE_URL}${endpoint}`, {
            headers: {
                'Content-Type': 'application/json',
                ...options.headers
            },
            ...options
        });

        const data = await response.json();
        
        if (!response.ok) {
            throw new Error(data.message || 'Bir hata oluştu');
        }
        
        return data;
    } catch (error) {
        console.error('API Error:', error);
        throw error;
    }
}

// Auth API
const AuthAPI = {
    login: async (username, password) => {
        return apiRequest('/auth/login', {
            method: 'POST',
            body: JSON.stringify({ username, password })
        });
    },
    
    register: async (username, password, fullName) => {
        return apiRequest('/auth/register', {
            method: 'POST',
            body: JSON.stringify({ username, password, fullName })
        });
    }
};

// Issues API
const IssuesAPI = {
    create: async (userId, title, description, categoryId) => {
        return apiRequest('/issues', {
            method: 'POST',
            body: JSON.stringify({ userId, title, description, categoryId })
        });
    },
    
    getMyIssues: async (userId) => {
        return apiRequest(`/issues/my/${userId}`);
    },
    
    getMyIssuesByStatus: async (userId, status) => {
        return apiRequest(`/issues/my/${userId}/status/${status}`);
    },
    
    getAllIssues: async (categoryId, status) => {
        let url = '/admin/issues';
        const params = [];
        
        // Sadece tanımlı ve geçerli değerleri ekle
        if (categoryId !== null && categoryId !== undefined && categoryId !== '') {
            params.push(`categoryId=${categoryId}`);
        }
        if (status !== null && status !== undefined && status !== '' && status !== 'Tümü') {
            params.push(`status=${encodeURIComponent(status)}`);
        }
        
        if (params.length > 0) {
            url += '?' + params.join('&');
        }
        
        console.log('🌐 IssuesAPI.getAllIssues - URL:', url);
        console.log('🌐 IssuesAPI.getAllIssues - categoryId:', categoryId, ', status:', status);
        return apiRequest(url);
    },
    
    updateStatus: async (issueId, status) => {
        return apiRequest(`/admin/issues/${issueId}/status`, {
            method: 'PUT',
            body: JSON.stringify({ status })
        });
    },
    
    setPriority: async (issueId, priority) => {
        return apiRequest(`/admin/issues/${issueId}/priority`, {
            method: 'PUT',
            body: JSON.stringify({ priority })
        });
    }
};

// Projects API
const ProjectsAPI = {
    getOpenProjects: async () => {
        return apiRequest('/projects/open');
    },
    
    getAll: async () => {
        return apiRequest('/admin/projects');
    },
    
    create: async (title, description, startDate, endDate) => {
        return apiRequest('/admin/projects', {
            method: 'POST',
            body: JSON.stringify({ title, description, startDate, endDate })
        });
    },
    
    update: async (projectId, title, description, startDate, endDate) => {
        return apiRequest(`/admin/projects/${projectId}`, {
            method: 'PUT',
            body: JSON.stringify({ title, description, startDate, endDate })
        });
    },
    
    delete: async (projectId) => {
        return apiRequest(`/admin/projects/${projectId}`, {
            method: 'DELETE'
        });
    },
    
    updateStatus: async (projectId, status) => {
        return apiRequest(`/admin/projects/${projectId}/status`, {
            method: 'PUT',
            body: JSON.stringify({ status })
        });
    }
};

// Applications API
const ApplicationsAPI = {
    create: async (projectId, userId, notes) => {
        return apiRequest('/applications', {
            method: 'POST',
            body: JSON.stringify({ projectId, userId, notes })
        });
    },
    
    getMyApplications: async (userId) => {
        return apiRequest(`/applications/my/${userId}`);
    },
    
    getProjectApplications: async (projectId) => {
        return apiRequest(`/admin/applications/project/${projectId}`);
    },
    
    approve: async (applicationId) => {
        return apiRequest(`/admin/applications/${applicationId}/approve`, {
            method: 'PUT'
        });
    },
    
    reject: async (applicationId) => {
        return apiRequest(`/admin/applications/${applicationId}/reject`, {
            method: 'PUT'
        });
    }
};

// Categories API
const CategoriesAPI = {
    getAll: async () => {
        return apiRequest('/categories');
    }
};

// Analytics API
const AnalyticsAPI = {
    getDashboard: async () => {
        return apiRequest('/admin/dashboard/analytics');
    },
    
    getCategoryReport: async () => {
        return apiRequest('/admin/dashboard/analytics');
    },
    
    getTopCategories: async (limit) => {
        return apiRequest(`/admin/categories/top/${limit}`);
    },
    
    getMonthlyStats: async () => {
        return apiRequest('/admin/stats/monthly');
    }
};

