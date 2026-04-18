/**
 * logo-helper.js
 * Xử lý logo company: ẩn/hiện img + placeholder đúng cách.
 * Đặt file này vào: src/main/resources/static/js/logo-helper.js
 * Include trong layout: <script th:src="@{/js/logo-helper.js}"></script>
 */
(function () {
    'use strict';

    /**
     * Gọi khi img lỗi (onerror).
     * Ẩn img, hiện placeholder anh em kế tiếp.
     *
     * HTML pattern:
     *   <div class="logo-wrap">
     *     <img class="lf-img" onerror="lfErr(this)" .../>
     *     <span class="lf-ph">F</span>   ← kế tiếp img
     *   </div>
     */
    window.lfErr = function (img) {
        img.style.display = 'none';
        var ph = img.nextElementSibling;
        if (ph && ph.classList.contains('lf-ph')) {
            ph.style.display = 'flex';
        }
    };

    /**
     * Khởi động khi DOM load xong — kiểm tra tất cả .lf-img.
     * Trường hợp img src rỗng hoặc null → hiện placeholder luôn.
     */
    function initLogos() {
        var imgs = document.querySelectorAll('.lf-img');
        imgs.forEach(function (img) {
            var src = img.getAttribute('src');
            // src rỗng, null, hoặc chứa "null" literal (Thymeleaf render null obj)
            if (!src || src === '' || src === 'null') {
                lfErr(img);
            }
        });
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', initLogos);
    } else {
        initLogos();
    }
})();