(function () {
    'use strict';

    // Sidebar collapse — guard bằng ?. để không crash khi element không có
    var collapseBtn = document.getElementById('sidebarCollapseBtn');
    if (collapseBtn) {
        collapseBtn.addEventListener('click', function () {
            document.getElementById('adminSidebar').classList.toggle('open');
        });
    }

    // Mobile: đóng sidebar khi click ngoài
    document.addEventListener('click', function (e) {
        var sidebar = document.getElementById('adminSidebar');
        if (!sidebar) return;
        if (window.innerWidth <= 768 && !sidebar.contains(e.target)) {
            sidebar.classList.remove('open');
        }
    });
})();