# 🔄 Cache Temizleme ve Sorun Giderme

## ⚠️ Değişiklikler Görünmüyorsa

Eğer yaptığımız değişiklikler tarayıcıda görünmüyorsa, muhtemelen **cache** sorunu vardır.

## 🔧 Çözüm Adımları

### 1. Backend'i Yeniden Başlatın

```bash
cd ~/Desktop/SmartCityPlatform/SmartCityPlatform/SmartCityPlatform
mvn spring-boot:run
```

**Önemli:** Backend'i durdurup yeniden başlatın (Ctrl+C ile durdurun, sonra tekrar başlatın).

### 2. Tarayıcı Cache'ini Temizleyin

#### Chrome/Edge (Windows/Linux):
- **Ctrl + Shift + R** (Hard Refresh)
- veya **Ctrl + F5**

#### Chrome/Edge (Mac):
- **Cmd + Shift + R** (Hard Refresh)
- veya **Cmd + Option + R**

#### Firefox:
- **Ctrl + Shift + R** (Windows/Linux)
- **Cmd + Shift + R** (Mac)

#### Safari:
- **Cmd + Option + E** (Empty Caches)
- Sonra **Cmd + R** (Refresh)

### 3. Developer Tools ile Cache Temizleme

1. **F12** tuşuna basın (Developer Tools)
2. **Network** sekmesine gidin
3. **Disable cache** checkbox'ını işaretleyin
4. Sayfayı yenileyin (**F5**)

### 4. Tam Cache Temizleme

#### Chrome:
1. **F12** → **Application** sekmesi
2. Sol menüden **Storage** → **Clear site data**
3. **Clear site data** butonuna tıklayın

#### Firefox:
1. **F12** → **Storage** sekmesi
2. Sağ tık → **Delete All**

### 5. JavaScript Dosyalarını Kontrol Edin

Tarayıcıda şu adresi açın:
```
http://localhost:8080/js/citizen.js
```

Dosyanın en üstünde şu satırı görmelisiniz:
```javascript
// Load categories for dropdown
let categoriesLoaded = false; // Kategorilerin yüklenip yüklenmediğini takip et
```

Eğer görmüyorsanız, cache sorunu var demektir.

### 6. Manuel Cache Bypass

Tarayıcı adres çubuğuna şunu yazın:
```
http://localhost:8080/citizen-dashboard.html?v=2
```

Her yenilemede versiyon numarasını artırın (v=3, v=4, vb.)

## 🐛 Sorun Devam Ediyorsa

### Kategoriler Hala Tekrarlanıyorsa:

1. **Console'u kontrol edin** (F12 → Console)
2. Şu komutu çalıştırın:
   ```javascript
   categoriesLoaded = false;
   loadCategories();
   ```

### Şikayetlerim "Tümü" Çalışmıyorsa:

1. **Network sekmesini kontrol edin** (F12 → Network)
2. `/api/issues/my/{userId}` isteğini bulun
3. Response'u kontrol edin
4. Eğer boşsa, backend log'larını kontrol edin

## 📝 Test Checklist

- [ ] Backend yeniden başlatıldı
- [ ] Hard refresh yapıldı (Ctrl+Shift+R)
- [ ] Developer Tools'da "Disable cache" işaretli
- [ ] JavaScript dosyası güncel (citizen.js kontrol edildi)
- [ ] Console'da hata yok
- [ ] Network sekmesinde istekler başarılı (200 OK)

## 💡 İpucu

Her değişiklikten sonra:
1. Backend'i yeniden başlatın
2. Hard refresh yapın (Ctrl+Shift+R)
3. Console'u kontrol edin

Bu adımları takip ederseniz, cache sorunları çözülecektir.

