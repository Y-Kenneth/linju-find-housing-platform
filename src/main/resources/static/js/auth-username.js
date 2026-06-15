/* ──────────────────────────────────────────────────────────────────────
   Username combobox — dropdown of remembered usernames (Remember Me).
   The <li> options are rendered server-side from the rememberedUsers cookie.
   This script makes the list a real dropdown: toggle via chevron, open on
   focus, filter while typing, pick with mouse or keyboard.
   ────────────────────────────────────────────────────────────────────── */
(function () {
    var combo  = document.querySelector('.lf-combo--has-options');
    if (!combo) return;                       // no remembered users → nothing to do

    var input  = combo.querySelector('#username');
    var toggle = combo.querySelector('#lfUserToggle');
    var menu   = combo.querySelector('#lfUserMenu');
    if (!input || !menu) return;

    var options = Array.prototype.slice.call(menu.querySelectorAll('.lf-combo__option'));
    var activeIndex = -1;

    function open() {
        combo.classList.add('is-open');
        input.setAttribute('aria-expanded', 'true');
    }
    function close() {
        combo.classList.remove('is-open');
        input.setAttribute('aria-expanded', 'false');
        setActive(-1);
    }
    function isOpen() {
        return combo.classList.contains('is-open');
    }

    /* Show only the options whose text contains what's typed (case-insensitive). */
    function filter() {
        var q = input.value.trim().toLowerCase();
        var anyVisible = false;
        options.forEach(function (li) {
            var match = li.textContent.toLowerCase().indexOf(q) !== -1;
            li.style.display = match ? '' : 'none';
            if (match) anyVisible = true;
        });
        return anyVisible;
    }

    function visibleOptions() {
        return options.filter(function (li) { return li.style.display !== 'none'; });
    }

    function setActive(i) {
        var vis = visibleOptions();
        vis.forEach(function (li) { li.classList.remove('is-active'); });
        activeIndex = i;
        if (i >= 0 && i < vis.length) {
            vis[i].classList.add('is-active');
            vis[i].scrollIntoView({ block: 'nearest' });
        }
    }

    function choose(li) {
        input.value = li.textContent;
        close();
        input.focus();
    }

    /* ── Mouse ── */
    if (toggle) {
        toggle.addEventListener('click', function () {
            if (isOpen()) {
                close();
            } else {
                filter();
                open();
            }
            input.focus();
        });
    }
    options.forEach(function (li) {
        // mousedown (not click) so it fires before the input's blur closes the menu
        li.addEventListener('mousedown', function (e) {
            e.preventDefault();
            choose(li);
        });
    });

    /* ── Focus / typing ── */
    input.addEventListener('focus', function () {
        if (filter()) open();
    });
    input.addEventListener('input', function () {
        if (filter()) { open(); } else { close(); }
        setActive(-1);
    });

    /* ── Keyboard ── */
    input.addEventListener('keydown', function (e) {
        var vis = visibleOptions();
        if (e.key === 'ArrowDown') {
            e.preventDefault();
            if (!isOpen()) { filter(); open(); }
            setActive(Math.min(activeIndex + 1, vis.length - 1));
        } else if (e.key === 'ArrowUp') {
            e.preventDefault();
            setActive(Math.max(activeIndex - 1, 0));
        } else if (e.key === 'Enter') {
            if (isOpen() && activeIndex >= 0 && vis[activeIndex]) {
                e.preventDefault();          // pick the highlighted name instead of submitting
                choose(vis[activeIndex]);
            }
        } else if (e.key === 'Escape') {
            close();
        }
    });

    /* ── Close when clicking outside ── */
    document.addEventListener('mousedown', function (e) {
        if (!combo.contains(e.target)) close();
    });
})();
