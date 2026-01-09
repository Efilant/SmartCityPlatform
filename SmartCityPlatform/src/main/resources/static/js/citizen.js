// Load categories for dropdown
let categoriesLoading = false; // Yükleme işlemi devam ediyor mu?
let categoriesLoaded = false; // Kategoriler yüklendi mi?

async function loadCategories() {
    const categorySelect = document.getElementById('issueCategory');
    if (!categorySelect) {
        return; // Element yoksa çık
    }
    
    if (categoriesLoading) {
        return;
    }
    
    if (categoriesLoaded && categorySelect.options.length > 1) {
        return;
    }
    
    categoriesLoading = true;
    
    while (categorySelect.firstChild) {
        categorySelect.removeChild(categorySelect.firstChild);
    }
    categorySelect.innerHTML = '';
    
    try {
        const response = await CategoriesAPI.getAll();
        const categoriesList = response.categories || [];
        
        const placeholderOption = document.createElement('option');
        placeholderOption.value = '';
        placeholderOption.textContent = 'Kategori seçiniz...';
        categorySelect.appendChild(placeholderOption);
        
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
                    categorySelect.appendChild(option);
                    addedCategoryIds.add(categoryId);
                    addedCategoryNames.add(categoryName);
                }
            }
        });
        
        categories = Array.from(addedCategoryIds).map(id => {
            return categoriesList.find(cat => (cat.categoryId || cat.category_id) === id);
        }).filter(Boolean);
        
        categoriesLoaded = true;
        categoriesLoading = false;
    } catch (error) {
        categorySelect.innerHTML = '';
        
        const defaultCategories = [
            { value: '', text: 'Kategori seçiniz...' },
            { value: '1', text: 'Ulaşım' },
            { value: '2', text: 'Çevre ve Temizlik' },
            { value: '3', text: 'Altyapı' },
            { value: '4', text: 'Sosyal Hizmetler' }
        ];
        
        defaultCategories.forEach(cat => {
            const option = document.createElement('option');
            option.value = cat.value;
            option.textContent = cat.text;
            categorySelect.appendChild(option);
        });
        
        categoriesLoaded = true;
        categoriesLoading = false;
    }
}

// Create issue form handler
document.getElementById('createIssueForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const title = document.getElementById('issueTitle').value;
    const description = document.getElementById('issueDescription').value;
    const categoryId = parseInt(document.getElementById('issueCategory').value);
    
    if (!categoryId) {
        showMessage('Lütfen bir kategori seçiniz!', 'error');
        return;
    }
    
    try {
        await IssuesAPI.create(currentUser.userId, title, description, categoryId);
        showMessage('Şikayetiniz başarıyla gönderildi!', 'success');
        document.getElementById('createIssueForm').reset();
        loadMyIssues();
    } catch (error) {
        showMessage(error.message || 'Şikayet gönderilirken bir hata oluştu!', 'error');
    }
});

// Load my issues
async function loadMyIssues() {
    const issuesList = document.getElementById('issuesList');
    if (!issuesList) {
        return;
    }
    
    if (!currentUser || !currentUser.userId) {
        issuesList.innerHTML = '<p class="error">Kullanıcı bilgisi bulunamadı. Lütfen tekrar giriş yapın.</p>';
        return;
    }
    
    issuesList.innerHTML = '<p>Şikayetler yükleniyor...</p>';
    
    try {
        const statusFilter = document.getElementById('statusFilter')?.value || '';
        
        let response;
        if (statusFilter) {
            response = await IssuesAPI.getMyIssuesByStatus(currentUser.userId, statusFilter);
        } else {
            response = await IssuesAPI.getMyIssues(currentUser.userId);
        }
        
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
        
        if (issues.length === 0) {
            issuesList.innerHTML = '<p>Henüz şikayetiniz bulunmamaktadır.</p>';
            return;
        }
        
        issuesList.innerHTML = issues.map(issue => {
            return `
            <div class="card">
                <h3>${escapeHtml(issue.title || 'Başlıksız')}</h3>
                <p><strong>Durum:</strong> <span class="status ${issue.status || 'Yeni'}">${issue.status || 'Yeni'}</span></p>
                ${issue.description ? `<p><strong>Açıklama:</strong> ${escapeHtml(issue.description)}</p>` : ''}
                ${issue.createdAt ? `<p><strong>Oluşturulma:</strong> ${formatDate(issue.createdAt)}</p>` : ''}
                ${issue.priority ? `<p><strong>Öncelik:</strong> ${escapeHtml(issue.priority)}</p>` : ''}
            </div>
        `;
        }).join('');
        
        console.log('✅ Şikayetler başarıyla gösterildi:', issues.length, 'şikayet');
    } catch (error) {
        console.error('❌ Error loading issues:', error);
        console.error('❌ Error details:', error.message);
        console.error('❌ Error stack:', error.stack);
        issuesList.innerHTML = '<p class="error">Şikayetler yüklenirken bir hata oluştu. Lütfen sayfayı yenileyin.</p>';
        showMessage('Şikayetler yüklenirken bir hata oluştu: ' + (error.message || 'Bilinmeyen hata'), 'error');
    }
}

// Load open projects
async function loadProjects() {
    try {
        const response = await ProjectsAPI.getOpenProjects();
        const projects = response.projects || [];
        const projectsList = document.getElementById('projectsList');
        
        if (projects.length === 0) {
            projectsList.innerHTML = '<p>Şu anda açık proje bulunmamaktadır.</p>';
            return;
        }
        
        projectsList.innerHTML = projects.map(project => `
            <div class="card">
                <h3>${escapeHtml(project.title || project)}</h3>
                <p>${escapeHtml(project.description || 'Proje detayları için başvuru yapabilirsiniz.')}</p>
                ${project.startDate ? `<p><strong>Başlangıç:</strong> ${formatDate(project.startDate)}</p>` : ''}
                ${project.endDate ? `<p><strong>Bitiş:</strong> ${formatDate(project.endDate)}</p>` : ''}
                <div class="card-actions">
                    <button onclick="showApplicationModal(${project.projectId || project.project_id || 0}, '${escapeHtml(project.title || project)}')" class="btn btn-primary">Başvur</button>
                </div>
            </div>
        `).join('');
    } catch (error) {
        console.error('Error loading projects:', error);
        showMessage('Projeler yüklenirken bir hata oluştu!', 'error');
    }
}

// Show application modal
function showApplicationModal(projectId, projectTitle) {
    const modal = document.getElementById('applicationModal');
    const projectTitleSpan = document.getElementById('modalProjectTitle');
    const notesInput = document.getElementById('applicationNotes');
    
    projectTitleSpan.textContent = projectTitle;
    notesInput.value = '';
    modal.style.display = 'block';
    
    // Store projectId for form submission
    modal.dataset.projectId = projectId;
}

// Close application modal
function closeApplicationModal() {
    document.getElementById('applicationModal').style.display = 'none';
}

// Apply to project (from modal)
async function submitApplication() {
    const modal = document.getElementById('applicationModal');
    const projectId = parseInt(modal.dataset.projectId);
    const notes = document.getElementById('applicationNotes').value;
    
    if (!projectId) {
        showMessage('Proje ID bulunamadı!', 'error');
        return;
    }
    
    try {
        await ApplicationsAPI.create(projectId, currentUser.userId, notes || '');
        showMessage('Başvurunuz alındı! Onay bekleniyor...', 'success');
        closeApplicationModal();
        loadApplications();
        loadProjects(); // Refresh projects list
    } catch (error) {
        showMessage(error.message || 'Başvuru yapılırken bir hata oluştu!', 'error');
    }
}

// Load applications
async function loadApplications() {
    try {
        const response = await ApplicationsAPI.getMyApplications(currentUser.userId);
        const applicationsList = document.getElementById('applicationsList');
        
        const applications = response.applications || [];
        
        if (applications.length === 0) {
            applicationsList.innerHTML = '<p>Henüz başvurunuz bulunmamaktadır.</p>';
            return;
        }
        
        applicationsList.innerHTML = applications.map(app => `
            <div class="card">
                <h3>${app.projectTitle ? escapeHtml(app.projectTitle) : 'Başvuru #' + app.applicationId}</h3>
                <p><strong>Durum:</strong> <span class="status ${app.status}">${app.status}</span></p>
                ${app.notes ? `<p><strong>Notlar:</strong> ${escapeHtml(app.notes)}</p>` : ''}
                ${app.applicationDate ? `<p><strong>Başvuru Tarihi:</strong> ${formatDate(app.applicationDate)}</p>` : ''}
            </div>
        `).join('');
    } catch (error) {
        console.error('Error loading applications:', error);
        const applicationsList = document.getElementById('applicationsList');
        applicationsList.innerHTML = '<p>Başvurularınız yüklenirken bir hata oluştu.</p>';
    }
}

// Helper functions
function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

function formatDate(dateString) {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return date.toLocaleDateString('tr-TR');
}

