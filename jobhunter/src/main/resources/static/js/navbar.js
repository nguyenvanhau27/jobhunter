function jhToggleDrop(btn) {
    const dd = btn.nextElementSibling;
    const open = dd.classList.contains('open');
    document.querySelectorAll('.jh-dropdown.open').forEach(d => d.classList.remove('open'));
    if (!open) dd.classList.add('open');
}
document.addEventListener('click', function(e) {
    if (!e.target.closest('.jh-avatar-wrap'))
        document.querySelectorAll('.jh-dropdown.open').forEach(d => d.classList.remove('open'));
});
function jhToggleDrawer() {
    const open = document.getElementById('jhDrawer').classList.contains('open');
    if (open) jhCloseDrawer();
    else {
        document.getElementById('jhDrawer').classList.add('open');
        document.getElementById('jhBackdrop').classList.add('open');
        document.body.style.overflow = 'hidden';
    }
}
function jhCloseDrawer() {
    document.getElementById('jhDrawer').classList.remove('open');
    document.getElementById('jhBackdrop').classList.remove('open');
    document.body.style.overflow = '';
}
window.addEventListener('resize', () => { if (window.innerWidth >= 769) jhCloseDrawer(); });