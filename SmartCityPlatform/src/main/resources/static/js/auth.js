// Show login form
function showLogin() {
    document.getElementById('loginForm').classList.add('active');
    document.getElementById('registerForm').classList.remove('active');
}

// Show register form
function showRegister() {
    document.getElementById('loginForm').classList.remove('active');
    document.getElementById('registerForm').classList.add('active');
}

// Login form handler
document.getElementById('loginFormElement').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const username = document.getElementById('loginUsername').value;
    const password = document.getElementById('loginPassword').value;
    
    try {
        const response = await AuthAPI.login(username, password);
        
        if (response.success && response.user) {
            // Store user in localStorage
            localStorage.setItem('user', JSON.stringify(response.user));
            
            // Redirect based on role
            if (response.user.role === 'ADMIN') {
                window.location.href = 'admin-dashboard.html';
            } else {
                window.location.href = 'citizen-dashboard.html';
            }
        } else {
            showMessage(response.message || 'Giriş başarısız!', 'error');
        }
    } catch (error) {
        showMessage(error.message || 'Giriş yapılırken bir hata oluştu!', 'error');
    }
});

// Register form handler
document.getElementById('registerFormElement').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const username = document.getElementById('registerUsername').value;
    const password = document.getElementById('registerPassword').value;
    const fullName = document.getElementById('registerFullName').value;
    const role = document.getElementById('registerRole').value;
    
    // Note: Role selection is for UI only, backend will set it to CITIZEN by default
    // Admin users should be created directly in database
    
    try {
        const response = await AuthAPI.register(username, password, fullName);
        
        if (response.success) {
            showMessage('Kayıt başarılı! Giriş yapabilirsiniz.', 'success');
            setTimeout(() => {
                showLogin();
            }, 2000);
        } else {
            showMessage(response.message || 'Kayıt başarısız!', 'error');
        }
    } catch (error) {
        showMessage(error.message || 'Kayıt yapılırken bir hata oluştu!', 'error');
    }
});

