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

// Şifre validasyonu fonksiyonu
function validatePassword(password) {
    const errors = [];
    
    if (!password || password.length < 8) {
        errors.push('En az 8 karakter');
    }
    
    if (!/[A-Z]/.test(password)) {
        errors.push('En az bir büyük harf (A-Z)');
    }
    
    if (!/[a-z]/.test(password)) {
        errors.push('En az bir küçük harf (a-z)');
    }
    
    if (!/[0-9]/.test(password)) {
        errors.push('En az bir rakam (0-9)');
    }
    
    if (!/[!@#$%^&*()_+\-=\[\]{}|;:,.<>?]/.test(password)) {
        errors.push('En az bir özel karakter (!@#$%^&*()_+-=[]{}|;:,.<>?)');
    }
    
    return errors;
}

// Şifre gücü kontrolü (real-time)
document.addEventListener('DOMContentLoaded', () => {
    const passwordInput = document.getElementById('registerPassword');
    const passwordStrength = document.getElementById('passwordStrength');
    
    if (passwordInput && passwordStrength) {
        passwordInput.addEventListener('input', () => {
            const password = passwordInput.value;
            const errors = validatePassword(password);
            
            if (password.length === 0) {
                passwordStrength.innerHTML = '';
                passwordStrength.style.color = '';
                return;
            }
            
            if (errors.length === 0) {
                passwordStrength.innerHTML = '✅ Şifre gereksinimleri karşılanıyor';
                passwordStrength.style.color = '#28a745';
            } else {
                passwordStrength.innerHTML = '⚠️ Eksik: ' + errors.join(', ');
                passwordStrength.style.color = '#dc3545';
            }
        });
    }
});

// Register form handler
document.getElementById('registerFormElement').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const username = document.getElementById('registerUsername').value;
    const password = document.getElementById('registerPassword').value;
    const fullName = document.getElementById('registerFullName').value;
    
    // Frontend şifre validasyonu
    const passwordErrors = validatePassword(password);
    if (passwordErrors.length > 0) {
        showMessage('Şifre gereksinimleri karşılanmıyor: ' + passwordErrors.join(', '), 'error');
        return;
    }
    
    // Note: All new users are registered as CITIZEN by default
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

