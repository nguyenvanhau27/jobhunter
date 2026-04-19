
(function () {
    'use strict';

    window.lfErr = function (img) {
        img.style.display = 'none';
        var ph = img.nextElementSibling;
        if (ph && ph.classList.contains('lf-ph')) {
            ph.style.display = 'flex';
        }
    };


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