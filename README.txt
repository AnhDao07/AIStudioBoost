BUILD APK: mở thư mục AIStudioBoost bằng Android Studio -> Build > Build APK(s).
SELECTOR: boost.js dùng ms-chat-turn / textarea (chưa kiểm chứng trên AI Studio hiện tại). Nếu không chạy, sửa TURN_SEL, INPUT_SEL, RUN_SEL ở đầu file.
USERSCRIPT: aistudio-boost.user.js cài vào Tampermonkey/Violentmonkey (Kiwi, Firefox Android, Chrome desktop).

NGUỒN / CREDIT: Một Ngàn Nỗi Nhớ - fb.com/troidongconang

BUILD KHÔNG CẦN MÁY TÍNH CÀI GÌ (GitHub Actions):
1. Tạo repo mới trên github.com, upload TOÀN BỘ nội dung đã giải nén (nhớ cả thư mục .github).
2. Vào tab Actions -> "Build APK" -> chờ chạy xong (~3-5 phút).
3. Mở lần chạy đó -> mục Artifacts -> tải AIStudioBoost-apk (zip chứa app-debug.apk).
4. Giải nén, copy app-debug.apk vào điện thoại, bật "Cài đặt từ nguồn không rõ" rồi cài.
