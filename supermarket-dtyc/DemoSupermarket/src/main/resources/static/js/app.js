// ===== Tien ich chung =====
const fmt = n => new Intl.NumberFormat('vi-VN').format(Math.round(n || 0)) + ' ₫';

// Dong bang co data-href -> bam vao de mo chi tiet
document.addEventListener('click', e => {
    const tr = e.target.closest('tr[data-href]');
    if (tr && !e.target.closest('a,button,input,select,form')) location.href = tr.dataset.href;
});

// Tu an thong bao thanh cong sau 6 giay
setTimeout(() => document.querySelectorAll('.alert-success.flash').forEach(a => {
    try { bootstrap.Alert.getOrCreateInstance(a).close(); } catch (_) {}
}), 6000);

/**
 * Bang chi tiet dong (them / xoa dong, tu tinh tien).
 * <table id="lines" data-mode="nhap|ban"> ... <template id="line-tpl"> ...
 * - mode "nhap": don gia do nguoi dung nhap
 * - mode "ban" : don gia lay tu gia ban cua san pham (data-gia), kiem tra so luong co the ban (data-coban)
 */
function initLineItems() {
    const table = document.getElementById('lines');
    if (!table) return;
    const mode = table.dataset.mode;
    const tbody = table.querySelector('tbody');
    const tpl = document.getElementById('line-tpl');

    function reindex() {
        tbody.querySelectorAll('tr.line').forEach((tr, i) => {
            tr.querySelector('.stt').textContent = i + 1;
            tr.querySelectorAll('[name]').forEach(el =>
                el.name = el.name.replace(/chiTiet\[\d+\]/, 'chiTiet[' + i + ']'));
        });
    }

    function recalc() {
        let total = 0, totalQty = 0;
        const chosen = {};
        tbody.querySelectorAll('tr.line').forEach(tr => {
            const sel = tr.querySelector('select.sp');
            const opt = sel.selectedOptions[0];
            const qtyEl = tr.querySelector('input.qty');
            const priceEl = tr.querySelector('input.price');
            const hint = tr.querySelector('.stock-hint');
            const qty = parseInt(qtyEl.value) || 0;

            if (mode === 'ban' && opt && opt.value) priceEl.value = opt.dataset.gia;
            const price = parseFloat(priceEl.value) || 0;
            const amount = qty * price;
            tr.querySelector('.amount').textContent = fmt(amount);
            total += amount; totalQty += qty;

            // goi y ton kho
            sel.classList.remove('is-invalid'); qtyEl.classList.remove('is-invalid');
            if (opt && opt.value) {
                if (chosen[opt.value]) sel.classList.add('is-invalid');
                chosen[opt.value] = true;
                const dvt = opt.dataset.dvt || '';
                if (mode === 'ban') {
                    const coBan = parseInt(opt.dataset.coban) || 0;
                    hint.innerHTML = 'Có thể bán: <b>' + coBan + '</b> ' + dvt;
                    hint.className = 'stock-hint ' + (qty > coBan ? 'text-danger fw-semibold' : 'text-muted');
                    if (qty > coBan) qtyEl.classList.add('is-invalid');
                } else {
                    hint.innerHTML = 'Tồn hiện tại: <b>' + opt.dataset.ton + '</b> ' + dvt +
                        (qty > 0 ? ' → sau nhập: <b class="text-success">' + ((parseInt(opt.dataset.ton) || 0) + qty) + '</b>' : '');
                    hint.className = 'stock-hint text-muted';
                }
            } else hint.textContent = '';
        });
        document.getElementById('total').textContent = fmt(total);
        const tq = document.getElementById('total-qty');
        if (tq) tq.textContent = totalQty;
        const dupe = tbody.querySelector('select.sp.is-invalid');
        const warn = document.getElementById('dupe-warning');
        if (warn) warn.classList.toggle('d-none', !dupe);
    }

    document.getElementById('add-line').addEventListener('click', () => {
        tbody.insertAdjacentHTML('beforeend', tpl.innerHTML.replace(/__i__/g, tbody.children.length));
        reindex(); recalc();
        tbody.lastElementChild.querySelector('select').focus();
    });
    tbody.addEventListener('click', e => {
        const btn = e.target.closest('.remove-line');
        if (!btn) return;
        if (tbody.querySelectorAll('tr.line').length === 1) {
            btn.closest('tr').querySelectorAll('input,select').forEach(el => el.value = '');
        } else btn.closest('tr').remove();
        reindex(); recalc();
    });
    tbody.addEventListener('input', recalc);
    tbody.addEventListener('change', e => {
        // Nhap kho: chon SP -> goi y don gia nhap = 75% gia ban (nguoi dung sua lai duoc)
        if (mode === 'nhap' && e.target.matches('select.sp')) {
            const price = e.target.closest('tr').querySelector('input.price');
            const opt = e.target.selectedOptions[0];
            if (!price.value && opt && opt.value) price.value = Math.round(opt.dataset.gia * 0.75 / 100) * 100;
        }
        recalc();
    });
    recalc();
}
document.addEventListener('DOMContentLoaded', initLineItems);
