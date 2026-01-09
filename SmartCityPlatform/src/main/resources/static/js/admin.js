// Global issues storage for filtering
let allIssuesData = [];
let categories = []; // Global categories array

// Load all issues for admin
async function loadAllIssues() {
    const allIssuesList = document.getElementById('allIssuesList');
    if (!allIssuesList) {
        return;
    }
    
    allIssuesList.innerHTML = '<tr><td colspan="8" class="loading-cell">Şikayetler yükleniyor...</td></tr>';
    
    try {
        const categoryFilter = document.getElementById('categoryFilter')?.value || '';
        const statusFilter = document.getElementById('adminStatusFilter')?.value || '';
        
        let categoryId = undefined;
        if (categoryFilter && categoryFilter !== '' && categoryFilter !== 'Tümü') {
            const parsed = parseInt(categoryFilter);
            if (!isNaN(parsed) && parsed > 0) {
                categoryId = parsed;
            }
        }
        
        let status = undefined;
        if (statusFilter && statusFilter !== '' && statusFilter !== 'Tümü') {
            status = statusFilter;
        }
        
        const response = await IssuesAPI.getAllIssues(categoryId, status);
        
        let issues = [];
        if (response) {
            if (Array.isArray(response)) {
                issues = response;
            } else if (response.issues && Array.isArray(response.issues)) {
                issues = response.issues;
            } else if (response.data && Array.isArray(response.data)) {
                issues = response.data;
            } else if (response.success && response.issues) {
                issues = response.issues;
            }
        }
        
        allIssuesData = issues;
        updateIssuesStats(issues);
        
        const issuesCount = document.getElementById('issuesCount');
        if (issuesCount) {
            issuesCount.textContent = `${issues.length} şikayet bulundu`;
        }
        
        renderIssuesTable(issues);
        
    } catch (error) {
        allIssuesList.innerHTML = '<tr><td colspan="8" class="loading-cell">Şikayetler yüklenirken bir hata oluştu.</td></tr>';
        showMessage('Şikayetler yüklenirken bir hata oluştu: ' + (error.message || 'Bilinmeyen hata'), 'error');
    }
}

// Render issues table
function renderIssuesTable(issues) {
    const allIssuesList = document.getElementById('allIssuesList');
    const emptyState = document.getElementById('emptyState');
    
    if (!allIssuesList) {
        return;
    }
    
    if (issues.length === 0) {
        allIssuesList.innerHTML = '';
        if (emptyState) emptyState.style.display = 'block';
        return;
    }
    
    if (emptyState) emptyState.style.display = 'none';
    
    const getCategoryName = (categoryId) => {
        if (!categoryId) return 'Kategori Yok';
        if (!categories || categories.length === 0) {
            return `Kategori #${categoryId}`;
        }
        const category = categories.find(cat => (cat.categoryId || cat.category_id) == categoryId);
        return category ? category.name : `Kategori #${categoryId}`;
    };
    
    const tableRows = issues.map(issue => {
        const issueId = issue.issueId || issue.issue_id;
        const categoryName = getCategoryName(issue.categoryId);
        const priority = issue.priority || 'Orta';
        const status = issue.status || 'Yeni';
        const createdAt = issue.createdAt ? formatDate(issue.createdAt) : '-';
        const description = issue.description ? escapeHtml(issue.description.substring(0, 100)) + (issue.description.length > 100 ? '...' : '') : '-';
        
        return `
            <tr>
                <td class="issue-id">#${issueId}</td>
                <td>
                    <div class="issue-title">${escapeHtml(issue.title || 'Başlıksız')}</div>
                    <div class="issue-description">${description}</div>
                </td>
                <td><span class="category-badge">${escapeHtml(categoryName)}</span></td>
                <td><span class="status ${status}">${status}</span></td>
                <td><span class="priority-badge ${priority}">${priority}</span></td>
                <td><span class="user-badge">Kullanıcı #${issue.userId || '-'}</span></td>
                <td class="date-cell">${createdAt}</td>
                <td>
                    <div class="action-buttons">
                        <select onchange="updateIssueStatus(${issueId}, this.value)" class="action-select status-select">
                            <option value="">Durum</option>
                            <option value="Yeni" ${status === 'Yeni' ? 'selected' : ''}>Yeni</option>
                            <option value="İnceleniyor" ${status === 'İnceleniyor' ? 'selected' : ''}>İnceleniyor</option>
                            <option value="Çözüldü" ${status === 'Çözüldü' ? 'selected' : ''}>Çözüldü</option>
                        </select>
                        <select onchange="setIssuePriority(${issueId}, this.value)" class="action-select priority-select">
                            <option value="">Öncelik</option>
                            <option value="Yüksek" ${priority === 'Yüksek' ? 'selected' : ''}>Yüksek</option>
                            <option value="Orta" ${priority === 'Orta' ? 'selected' : ''}>Orta</option>
                            <option value="Düşük" ${priority === 'Düşük' ? 'selected' : ''}>Düşük</option>
                        </select>
                    </div>
                </td>
            </tr>
        `;
    }).join('');
    
    allIssuesList.innerHTML = tableRows;
}

// Update issues statistics
function updateIssuesStats(issues) {
    const statNew = document.getElementById('statNew');
    const statInProgress = document.getElementById('statInProgress');
    const statResolved = document.getElementById('statResolved');
    
    const stats = {
        Yeni: issues.filter(i => i.status === 'Yeni').length,
        İnceleniyor: issues.filter(i => i.status === 'İnceleniyor').length,
        Çözüldü: issues.filter(i => i.status === 'Çözüldü').length
    };
    
    if (statNew) statNew.textContent = stats.Yeni || 0;
    if (statInProgress) statInProgress.textContent = stats.İnceleniyor || 0;
    if (statResolved) statResolved.textContent = stats.Çözüldü || 0;
}

// Filter issues by search and priority
function filterIssues() {
    const searchInput = document.getElementById('searchInput')?.value.toLowerCase() || '';
    const priorityFilter = document.getElementById('priorityFilter')?.value || '';
    
    let filtered = [...allIssuesData];
    
    // Arama filtresi
    if (searchInput) {
        filtered = filtered.filter(issue => {
            const title = (issue.title || '').toLowerCase();
            const description = (issue.description || '').toLowerCase();
            return title.includes(searchInput) || description.includes(searchInput);
        });
    }
    
    // Öncelik filtresi
    if (priorityFilter) {
        filtered = filtered.filter(issue => (issue.priority || 'Orta') === priorityFilter);
    }
    
    // İstatistikleri güncelle
    updateIssuesStats(filtered);
    
    // Şikayet sayısını güncelle
    const issuesCount = document.getElementById('issuesCount');
    if (issuesCount) {
        issuesCount.textContent = `${filtered.length} şikayet bulundu`;
    }
    
    // Tabloyu yeniden render et
    renderIssuesTable(filtered);
}

// Update issue status
async function updateIssueStatus(issueId, status) {
    if (!status) return;
    
    try {
        await IssuesAPI.updateStatus(issueId, status);
        showMessage('Şikayet durumu güncellendi!', 'success');
        loadAllIssues(); // Tabloyu yenile
    } catch (error) {
        showMessage(error.message || 'Durum güncellenirken bir hata oluştu!', 'error');
    }
}

// Set issue priority
async function setIssuePriority(issueId, priority) {
    if (!priority) return;
    
    try {
        await IssuesAPI.setPriority(issueId, priority);
        showMessage('Şikayet önceliği güncellendi!', 'success');
        loadAllIssues(); // Tabloyu yenile
    } catch (error) {
        showMessage(error.message || 'Öncelik güncellenirken bir hata oluştu!', 'error');
    }
}

// Create project form handler
document.getElementById('createProjectForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const title = document.getElementById('projectTitle').value;
    const description = document.getElementById('projectDescription').value;
    const startDate = document.getElementById('projectStartDate').value;
    const endDate = document.getElementById('projectEndDate').value;
    
    try {
        await ProjectsAPI.create(title, description, startDate, endDate);
        showMessage('Proje başarıyla oluşturuldu!', 'success');
        document.getElementById('createProjectForm').reset();
        loadProjects();
        // Also refresh projects list if we're on that section
        if (document.getElementById('projects') && document.getElementById('projects').classList.contains('active')) {
            loadAllProjects();
        }
    } catch (error) {
        showMessage(error.message || 'Proje oluşturulurken bir hata oluştu!', 'error');
    }
});

// Load projects for dropdown
async function loadProjects() {
    try {
        const response = await ProjectsAPI.getAll();
        const projects = response.projects || [];
        const projectSelect = document.getElementById('projectSelect');
        
        if (!projectSelect) return;
        
        projectSelect.innerHTML = '<option value="">Proje seçiniz...</option>';
        projects.forEach((project) => {
            const option = document.createElement('option');
            option.value = project.projectId || project.project_id;
            option.textContent = project.title || project;
            projectSelect.appendChild(option);
        });
    } catch (error) {
        // Silent fail
    }
}

// Load all projects for management
async function loadAllProjects() {
    const projectsList = document.getElementById('projectsList');
    const projectsEmptyState = document.getElementById('projectsEmptyState');
    const projectsCount = document.getElementById('projectsCount');
    
    if (!projectsList) {
        return;
    }
    
    projectsList.innerHTML = '<tr><td colspan="7" class="loading-cell">Projeler yükleniyor...</td></tr>';
    
    try {
        const response = await ProjectsAPI.getAll();
        const projects = response.projects || [];
        
        if (projectsCount) {
            projectsCount.textContent = `${projects.length} proje bulundu`;
        }
        
        if (projects.length === 0) {
            projectsList.innerHTML = '';
            if (projectsEmptyState) projectsEmptyState.style.display = 'block';
            return;
        }
        
        if (projectsEmptyState) projectsEmptyState.style.display = 'none';
        
        const tableRows = projects.map(project => {
            const projectId = project.projectId || project.project_id;
            const title = escapeHtml(project.title || 'Başlıksız');
            const description = project.description ? escapeHtml(project.description.substring(0, 100)) + (project.description.length > 100 ? '...' : '') : '-';
            const status = project.status || 'Açık';
            const startDate = project.startDate ? formatDate(project.startDate) : '-';
            const endDate = project.endDate ? formatDate(project.endDate) : '-';
            
            return `
                <tr>
                    <td class="issue-id">#${projectId}</td>
                    <td><strong>${title}</strong></td>
                    <td>${description}</td>
                    <td><span class="status ${status}">${status}</span></td>
                    <td class="date-cell">${startDate}</td>
                    <td class="date-cell">${endDate}</td>
                    <td>
                        <div class="action-buttons">
                            <button onclick="editProject(${projectId})" class="btn btn-small btn-primary">✏️ Düzenle</button>
                            <button onclick="deleteProject(${projectId})" class="btn btn-small btn-danger">🗑️ Sil</button>
                        </div>
                    </td>
                </tr>
            `;
        }).join('');
        
        projectsList.innerHTML = tableRows;
    } catch (error) {
        projectsList.innerHTML = '<tr><td colspan="7" class="loading-cell">Projeler yüklenirken bir hata oluştu.</td></tr>';
        showMessage('Projeler yüklenirken bir hata oluştu: ' + (error.message || 'Bilinmeyen hata'), 'error');
    }
}

// Edit project
async function editProject(projectId) {
    try {
        const response = await ProjectsAPI.getAll();
        const projects = response.projects || [];
        const project = projects.find(p => (p.projectId || p.project_id) == projectId);
        
        if (!project) {
            showMessage('Proje bulunamadı!', 'error');
            return;
        }
        
        // Fill modal with project data
        document.getElementById('editProjectId').value = projectId;
        document.getElementById('editProjectTitle').value = project.title || '';
        document.getElementById('editProjectDescription').value = project.description || '';
        
        // Format dates for input (YYYY-MM-DD)
        if (project.startDate) {
            const startDate = new Date(project.startDate);
            document.getElementById('editProjectStartDate').value = startDate.toISOString().split('T')[0];
        }
        if (project.endDate) {
            const endDate = new Date(project.endDate);
            document.getElementById('editProjectEndDate').value = endDate.toISOString().split('T')[0];
        }
        
        document.getElementById('editProjectStatus').value = project.status || 'Açık';
        
        // Show modal
        document.getElementById('projectModal').style.display = 'block';
    } catch (error) {
        showMessage('Proje bilgileri yüklenirken bir hata oluştu: ' + (error.message || 'Bilinmeyen hata'), 'error');
    }
}

// Delete project
async function deleteProject(projectId) {
    if (!confirm('Bu projeyi silmek istediğinizden emin misiniz? Bu işlem geri alınamaz!')) {
        return;
    }
    
    try {
        await ProjectsAPI.delete(projectId);
        showMessage('Proje başarıyla silindi!', 'success');
        loadAllProjects();
    } catch (error) {
        showMessage('Proje silinirken bir hata oluştu: ' + (error.message || 'Bilinmeyen hata'), 'error');
    }
}

// Close project modal
function closeProjectModal() {
    document.getElementById('projectModal').style.display = 'none';
    document.getElementById('editProjectForm').reset();
}

// Edit project form handler
document.addEventListener('DOMContentLoaded', () => {
    const editProjectForm = document.getElementById('editProjectForm');
    if (editProjectForm) {
        editProjectForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            
            const projectId = document.getElementById('editProjectId').value;
            const title = document.getElementById('editProjectTitle').value;
            const description = document.getElementById('editProjectDescription').value;
            const startDate = document.getElementById('editProjectStartDate').value;
            const endDate = document.getElementById('editProjectEndDate').value;
            const status = document.getElementById('editProjectStatus').value;
            
            try {
                // Update project details
                await ProjectsAPI.update(projectId, title, description, startDate, endDate);
                
                // Update status if changed
                // Note: We might need to get the current project status first
                // For now, we'll update status separately
                await ProjectsAPI.updateStatus(projectId, status);
                
                showMessage('Proje başarıyla güncellendi!', 'success');
                closeProjectModal();
                loadAllProjects();
            } catch (error) {
                showMessage('Proje güncellenirken bir hata oluştu: ' + (error.message || 'Bilinmeyen hata'), 'error');
            }
        });
    }
});

// Load project applications
async function loadProjectApplications() {
    const projectId = document.getElementById('projectSelect').value;
    if (!projectId) {
        document.getElementById('applicationsList').innerHTML = '<p>Lütfen bir proje seçiniz.</p>';
        return;
    }
    
    const applicationsList = document.getElementById('applicationsList');
    if (!applicationsList) return;
    
    applicationsList.innerHTML = '<p>Başvurular yükleniyor...</p>';
    
    try {
        const response = await ApplicationsAPI.getProjectApplications(projectId);
        const applications = response.applications || [];
        
        if (applications.length === 0) {
            applicationsList.innerHTML = '<p>Bu projeye henüz başvuru yapılmamış.</p>';
            return;
        }
        
        applicationsList.innerHTML = applications.map(app => {
            const status = app.status || 'Beklemede';
            const isApproved = status === 'Onaylandı';
            const isRejected = status === 'Reddedildi';
            
            return `
            <div class="card">
                <h3>Başvuru #${app.applicationId}</h3>
                <p><strong>Başvuran:</strong> ${escapeHtml(app.applicantName || 'Bilinmiyor')}</p>
                <p><strong>Kullanıcı Adı:</strong> ${escapeHtml(app.applicantUsername || 'Bilinmiyor')}</p>
                <p><strong>Durum:</strong> <span class="status ${status}">${escapeHtml(status)}</span></p>
                <p><strong>Tarih:</strong> ${formatDate(app.applicationDate)}</p>
                ${app.notes ? `<p><strong>Notlar:</strong> ${escapeHtml(app.notes)}</p>` : ''}
                <div class="card-actions">
                    <button onclick="approveApplication(${app.applicationId})" 
                            class="btn btn-success" 
                            ${isApproved ? 'disabled style="opacity: 0.5;"' : ''}>
                        Onayla
                    </button>
                    <button onclick="rejectApplication(${app.applicationId})" 
                            class="btn btn-danger" 
                            ${isRejected ? 'disabled style="opacity: 0.5;"' : ''}>
                        Reddet
                    </button>
                </div>
            </div>
        `;
        }).join('');
        
        showMessage(`${applications.length} başvuru yüklendi.`, 'success');
    } catch (error) {
        applicationsList.innerHTML = '<p class="error">Başvurular yüklenirken bir hata oluştu.</p>';
        showMessage('Başvurular yüklenirken bir hata oluştu: ' + (error.message || 'Bilinmeyen hata'), 'error');
    }
}

// Approve application
async function approveApplication(applicationId) {
    if (!confirm('Bu başvuruyu onaylamak istediğinizden emin misiniz?')) {
        return;
    }
    
    try {
        await ApplicationsAPI.approve(applicationId);
        showMessage('Başvuru onaylandı!', 'success');
        loadProjectApplications(); // Refresh list
    } catch (error) {
        showMessage('Başvuru onaylanırken bir hata oluştu: ' + (error.message || 'Bilinmeyen hata'), 'error');
    }
}

// Reject application
async function rejectApplication(applicationId) {
    if (!confirm('Bu başvuruyu reddetmek istediğinizden emin misiniz?')) {
        return;
    }
    
    try {
        await ApplicationsAPI.reject(applicationId);
        showMessage('Başvuru reddedildi.', 'success');
        loadProjectApplications(); // Refresh list
    } catch (error) {
        showMessage('Başvuru reddedilirken bir hata oluştu: ' + (error.message || 'Bilinmeyen hata'), 'error');
    }
}

// Load analytics dashboard
async function loadAnalytics() {
    try {
        const response = await AnalyticsAPI.getDashboard();
        const systemStats = response.systemStats || {};
        const categoryReport = response.categoryReport || [];
        
        // Update dashboard stats
        document.getElementById('totalIssues').textContent = systemStats.totalIssues || 0;
        document.getElementById('activeProjects').textContent = systemStats.activeProjects || 0;
        document.getElementById('totalApplications').textContent = systemStats.totalApplications || 0;
        
        // Calculate pending issues (total - resolved)
        const resolvedCount = categoryReport.reduce((sum, cat) => sum + (cat.resolvedIssues || 0), 0);
        const pendingIssues = (systemStats.totalIssues || 0) - resolvedCount;
        document.getElementById('pendingIssues').textContent = pendingIssues > 0 ? pendingIssues : 0;
        
        showMessage('Dashboard verileri yüklendi.', 'success');
    } catch (error) {
        showMessage('Dashboard verileri yüklenirken bir hata oluştu!', 'error');
    }
}

// Load category report
async function loadCategoryReport() {
    const reportsContent = document.getElementById('reportsContent');
    if (!reportsContent) return;
    
    reportsContent.innerHTML = '<p>Rapor yükleniyor...</p>';
    
    try {
        const response = await AnalyticsAPI.getDashboard();
        const categoryReport = response.categoryReport || [];
        
        if (categoryReport.length === 0) {
            reportsContent.innerHTML = '<p>Henüz kategori raporu bulunmamaktadır.</p>';
            return;
        }
        
        reportsContent.innerHTML = `
            <h3>Kategori Başarı Raporu</h3>
            <table class="report-table">
                <thead>
                    <tr>
                        <th>Kategori</th>
                        <th>Toplam Şikayet</th>
                        <th>Çözülen</th>
                        <th>Başarı Oranı</th>
                    </tr>
                </thead>
                <tbody>
                    ${categoryReport.map(cat => `
                        <tr>
                            <td>${escapeHtml(cat.category || 'Bilinmiyor')}</td>
                            <td>${cat.totalIssues || 0}</td>
                            <td>${cat.resolvedIssues || 0}</td>
                            <td>${(cat.successRate || 0).toFixed(2)}%</td>
                        </tr>
                    `).join('')}
                </tbody>
            </table>
        `;
        
        showMessage('Kategori raporu yüklendi.', 'success');
    } catch (error) {
        reportsContent.innerHTML = '<p class="error">Rapor yüklenirken bir hata oluştu.</p>';
        showMessage('Kategori raporu yüklenirken bir hata oluştu!', 'error');
    }
}

// Load top categories
async function loadTopCategories() {
    const reportsContent = document.getElementById('reportsContent');
    if (!reportsContent) return;
    
    reportsContent.innerHTML = '<p>Rapor yükleniyor...</p>';
    
    try {
        const response = await AnalyticsAPI.getTopCategories(5);
        const categories = response.categories || [];
        
        if (categories.length === 0) {
            reportsContent.innerHTML = '<p>Henüz kategori verisi bulunmamaktadır.</p>';
            return;
        }
        
        reportsContent.innerHTML = `
            <h3>En Çok Şikayet Alan Kategoriler (Top 5)</h3>
            <table class="report-table">
                <thead>
                    <tr>
                        <th>Kategori</th>
                        <th>Toplam Şikayet</th>
                        <th>Çözülen</th>
                        <th>Sorumlu Birim</th>
                    </tr>
                </thead>
                <tbody>
                    ${categories.map(cat => `
                        <tr>
                            <td>${escapeHtml(cat.categoryName || 'Bilinmiyor')}</td>
                            <td>${cat.totalIssues || 0}</td>
                            <td>${cat.resolvedIssues || 0}</td>
                            <td>${escapeHtml(cat.responsibleUnit || 'Belirtilmemiş')}</td>
                        </tr>
                    `).join('')}
                </tbody>
            </table>
        `;
        
        showMessage('Top kategoriler yüklendi.', 'success');
    } catch (error) {
        reportsContent.innerHTML = '<p class="error">Rapor yüklenirken bir hata oluştu.</p>';
        showMessage('Top kategoriler yüklenirken bir hata oluştu!', 'error');
    }
}

// Load monthly stats
async function loadMonthlyStats() {
    const reportsContent = document.getElementById('reportsContent');
    if (!reportsContent) return;
    
    reportsContent.innerHTML = '<p>Rapor yükleniyor...</p>';
    
    try {
        const response = await AnalyticsAPI.getMonthlyStats();
        const stats = response.stats || [];
        
        if (stats.length === 0) {
            reportsContent.innerHTML = '<p>Son 30 gün için istatistik bulunmamaktadır.</p>';
            return;
        }
        
        reportsContent.innerHTML = `
            <h3>Son 30 Günün İstatistikleri</h3>
            <table class="report-table">
                <thead>
                    <tr>
                        <th>Tarih</th>
                        <th>Günlük Şikayet</th>
                        <th>Çözülen</th>
                    </tr>
                </thead>
                <tbody>
                    ${stats.map(day => `
                        <tr>
                            <td>${formatDate(day.date)}</td>
                            <td>${day.dailyIssues || 0}</td>
                            <td>${day.resolvedCount || 0}</td>
                        </tr>
                    `).join('')}
                </tbody>
            </table>
        `;
        
        showMessage('Aylık istatistikler yüklendi.', 'success');
    } catch (error) {
        reportsContent.innerHTML = '<p class="error">Rapor yüklenirken bir hata oluştu.</p>';
        showMessage('Aylık istatistikler yüklenirken bir hata oluştu!', 'error');
    }
}

// Load categories for filter
let adminCategoriesLoaded = false;

async function loadCategories() {
    const categoryFilter = document.getElementById('categoryFilter');
    if (!categoryFilter) {
        return;
    }
    
    if (adminCategoriesLoaded && categoryFilter.options.length > 1) {
        return;
    }
    
    while (categoryFilter.firstChild) {
        categoryFilter.removeChild(categoryFilter.firstChild);
    }
    categoryFilter.innerHTML = '';
    
    try {
        const response = await CategoriesAPI.getAll();
        const categoriesList = response.categories || [];
        
        const placeholderOption = document.createElement('option');
        placeholderOption.value = '';
        placeholderOption.textContent = 'Tümü';
        categoryFilter.appendChild(placeholderOption);
        
        const addedCategoryIds = new Set();
        const addedCategoryNames = new Set();
        
        categoriesList.forEach(category => {
            const categoryId = category.categoryId || category.category_id;
            const categoryName = category.name;
            
            if (categoryId && categoryName) {
                if (!addedCategoryIds.has(categoryId) && !addedCategoryNames.has(categoryName)) {
                    const option = document.createElement('option');
                    option.value = categoryId;
                    option.textContent = categoryName;
                    categoryFilter.appendChild(option);
                    addedCategoryIds.add(categoryId);
                    addedCategoryNames.add(categoryName);
                }
            }
        });
        
        categories = categoriesList;
        adminCategoriesLoaded = true;
    } catch (error) {
        categoryFilter.innerHTML = '<option value="">Tümü</option>';
        adminCategoriesLoaded = true;
    }
}

// Helper functions
function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

function formatDate(dateString) {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return date.toLocaleDateString('tr-TR');
}

function closeIssueModal() {
    document.getElementById('issueModal').style.display = 'none';
}

// Show section function - Global scope'ta tanımlı (onclick için)
function showSection(sectionId, event) {
    // Hide all sections
    document.querySelectorAll('.content-section').forEach(section => {
        section.classList.remove('active');
    });
    
    // Show selected section
    const selectedSection = document.getElementById(sectionId);
    if (selectedSection) {
        selectedSection.classList.add('active');
    }
    
    // Update menu items
    document.querySelectorAll('.menu-item').forEach(item => {
        item.classList.remove('active');
    });
    
    // Event target'ı aktif yap
    if (event && event.target) {
        event.target.classList.add('active');
    } else if (window.event && window.event.target) {
        window.event.target.classList.add('active');
    }
    
    // Load data based on section
    if (sectionId === 'allIssues') {
        loadCategories().then(() => {
            loadAllIssues();
        });
    } else if (sectionId === 'projects') {
        loadAllProjects();
    } else if (sectionId === 'applications') {
        loadProjects();
    } else if (sectionId === 'dashboard') {
        loadAnalytics();
    }
}

// Initialize on page load
document.addEventListener('DOMContentLoaded', async () => {
    await loadCategories();
    
    const allIssuesSection = document.getElementById('allIssues');
    if (allIssuesSection && allIssuesSection.classList.contains('active')) {
        await loadAllIssues();
    }
});

