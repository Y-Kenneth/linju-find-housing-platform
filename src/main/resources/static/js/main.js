/* ============================================================
   LINJU FIND — main.js
   Horizontal scroll engine + parallax + scroll reveal
   + star rendering + general interactions
   ============================================================ */

(function () {
  'use strict';

  /* ── Scroll reveal ─────────────────────────────────────────── */
  function initReveal() {
    const els = document.querySelectorAll('.lf-reveal');
    if (!els.length) return;
    const obs = new IntersectionObserver(
      (entries) => {
        entries.forEach((e) => {
          if (e.isIntersecting) {
            e.target.classList.add('is-visible');
            obs.unobserve(e.target);
          }
        });
      },
      { threshold: 0.12 }
    );
    els.forEach((el) => obs.observe(el));
  }

  /* ── Score rings (neighborhood) ────────────────────────────── */
  function initScoreRings() {
    document.querySelectorAll('.lf-score-ring').forEach((ring) => {
      const fill = ring.querySelector('.lf-score-ring__fill');
      const score = parseFloat(ring.dataset.score || '0');
      if (!fill || isNaN(score)) return;
      const circumference = 188.5;
      const pct = Math.min(Math.max(score / 5, 0), 1);
      setTimeout(() => {
        fill.style.strokeDashoffset = circumference * (1 - pct);
      }, 200);
    });
  }

  /* ── Star rendering ─────────────────────────────────────────── */
  function initStars() {
    document.querySelectorAll('[data-stars]').forEach((el) => {
      const rating = parseFloat(el.dataset.stars || '0');
      const max = 5;
      let html = '';
      for (let i = 1; i <= max; i++) {
        if (rating >= i) {
          html += '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" width="14" height="14"><path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/></svg>';
        } else if (rating >= i - 0.5) {
          html += '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" width="14" height="14" style="opacity:0.6"><path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/></svg>';
        } else {
          html += '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" width="14" height="14" style="opacity:0.3"><path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/></svg>';
        }
      }
      el.innerHTML = html;
    });
  }

  /* ── Horizontal scroll (listings browse page) ──────────────── */
  function initHorizontalScroll() {
    const page    = document.querySelector('.lf-hscroll-page');
    const track   = document.querySelector('.lf-hscroll-track');
    const bg      = document.querySelector('.lf-hscroll-bg');
    const panels  = document.querySelectorAll('.lf-hscroll-panel');
    const hint    = document.querySelector('.lf-scroll-hint');

    if (!page || !track || !panels.length) return;

    const panelCount = panels.length;
    let currentPanel = 0;
    let isAnimating  = false;

    function goTo(index) {
      if (index < 0 || index >= panelCount || isAnimating) return;
      isAnimating = true;
      currentPanel = index;

      const offset = index * 100;
      track.style.transform = `translateX(-${offset}vw)`;

      if (bg) {
        // parallax: bg moves at 40% of the panel shift
        const bgShift = index * 40;
        bg.style.transform = `translateX(-${bgShift}vw)`;
      }

      if (hint) {
        hint.style.opacity = index === panelCount - 1 ? '0' : '0.7';
      }

      setTimeout(() => { isAnimating = false; }, 650);
    }

    // Wheel scroll
    let wheelAccum = 0;
    const WHEEL_THRESHOLD = 60;

    page.addEventListener('wheel', (e) => {
      e.preventDefault();
      wheelAccum += Math.abs(e.deltaX) > Math.abs(e.deltaY) ? e.deltaX : e.deltaY;

      if (wheelAccum > WHEEL_THRESHOLD) {
        goTo(currentPanel + 1);
        wheelAccum = 0;
      } else if (wheelAccum < -WHEEL_THRESHOLD) {
        goTo(currentPanel - 1);
        wheelAccum = 0;
      }
    }, { passive: false });

    // Keyboard arrows
    document.addEventListener('keydown', (e) => {
      if (!page.isConnected) return;
      if (e.key === 'ArrowRight' || e.key === 'ArrowDown') {
        e.preventDefault();
        goTo(currentPanel + 1);
      } else if (e.key === 'ArrowLeft' || e.key === 'ArrowUp') {
        e.preventDefault();
        goTo(currentPanel - 1);
      }
    });

    // Touch swipe
    let touchStartX = 0;
    let touchStartY = 0;
    page.addEventListener('touchstart', (e) => {
      touchStartX = e.touches[0].clientX;
      touchStartY = e.touches[0].clientY;
    }, { passive: true });

    page.addEventListener('touchend', (e) => {
      const dx = touchStartX - e.changedTouches[0].clientX;
      const dy = touchStartY - e.changedTouches[0].clientY;
      if (Math.abs(dx) > Math.abs(dy) && Math.abs(dx) > 50) {
        goTo(dx > 0 ? currentPanel + 1 : currentPanel - 1);
      }
    }, { passive: true });

    // Nav dots (if present)
    document.querySelectorAll('[data-panel-dot]').forEach((dot) => {
      dot.addEventListener('click', () => {
        goTo(parseInt(dot.dataset.panelDot, 10));
      });
    });

    // Init
    goTo(0);
  }

  /* ── Confirm dialogs (custom in-page modal) ─────────────────── */
  function showConfirmModal(message, onConfirm) {
    const overlay = document.createElement('div');
    overlay.style.cssText = [
      'position:fixed;inset:0;z-index:9000',
      'background:rgba(0,0,0,0.65)',
      'backdrop-filter:blur(4px)',
      'display:flex;align-items:center;justify-content:center',
      'opacity:0;transition:opacity 180ms ease',
    ].join(';');

    const box = document.createElement('div');
    box.style.cssText = [
      'background:#1c1c1c',
      'border:1px solid rgba(201,168,76,0.35)',
      'border-radius:14px',
      'padding:2rem 2.25rem 1.5rem',
      'max-width:380px;width:90%',
      'box-shadow:0 24px 64px rgba(0,0,0,0.7)',
      'transform:translateY(12px)',
      'transition:transform 180ms ease',
      'font-family:\'Josefin Sans\',sans-serif',
    ].join(';');

    box.innerHTML =
      '<p style="font-size:0.92rem;color:#f0ebe0;line-height:1.6;margin-bottom:1.5rem;">' +
        message +
      '</p>' +
      '<div style="display:flex;gap:0.65rem;justify-content:flex-end;">' +
        '<button class="lf-modal-cancel" style="' +
          'padding:0.5rem 1.1rem;border-radius:7px;border:1px solid rgba(201,168,76,0.25);' +
          'background:transparent;color:#b8a98a;font-family:\'Josefin Sans\',sans-serif;' +
          'font-size:0.78rem;letter-spacing:0.08em;text-transform:uppercase;cursor:pointer;' +
          'transition:border-color 150ms,color 150ms;"' +
        '>Cancel</button>' +
        '<button class="lf-modal-confirm" style="' +
          'padding:0.5rem 1.1rem;border-radius:7px;border:1px solid #b94040;' +
          'background:#b94040;color:#fff;font-family:\'Josefin Sans\',sans-serif;' +
          'font-size:0.78rem;letter-spacing:0.08em;text-transform:uppercase;cursor:pointer;' +
          'transition:background 150ms,border-color 150ms;"' +
        '>Confirm</button>' +
      '</div>';

    overlay.appendChild(box);
    document.body.appendChild(overlay);

    requestAnimationFrame(() => {
      overlay.style.opacity = '1';
      box.style.transform = 'translateY(0)';
    });

    function close() {
      overlay.style.opacity = '0';
      box.style.transform = 'translateY(12px)';
      setTimeout(() => overlay.remove(), 200);
    }

    box.querySelector('.lf-modal-cancel').addEventListener('click', close);
    overlay.addEventListener('click', (e) => { if (e.target === overlay) close(); });
    document.addEventListener('keydown', function esc(e) {
      if (e.key === 'Escape') { close(); document.removeEventListener('keydown', esc); }
    });
    box.querySelector('.lf-modal-confirm').addEventListener('click', () => {
      close();
      onConfirm();
    });
  }

  function initConfirm() {
    document.addEventListener('click', (e) => {
      const btn = e.target.closest('[data-confirm]');
      if (!btn) return;
      e.preventDefault();
      e.stopPropagation();
      const msg = btn.dataset.confirm || 'Are you sure?';
      showConfirmModal(msg, () => {
        const form = btn.closest('form');
        if (form) {
          form.submit();
        } else {
          btn.removeAttribute('data-confirm');
          btn.click();
        }
      });
    });
  }

  /* ── Toast notifications (shown after redirect) ─────────────── */
  function initToast() {
    const url = new URL(window.location.href);
    let msg  = null;
    let type = 'success';

    if (url.searchParams.has('registered')) {
      msg = 'Account created successfully. Welcome!';
    }

    if (!msg) return;

    const toast = document.createElement('div');
    toast.style.cssText = `
      position: fixed; bottom: 2rem; right: 2rem; z-index: 9999;
      background: rgba(20,20,20,0.92); border: 1px solid rgba(201,168,76,0.3);
      backdrop-filter: blur(12px); padding: 1rem 1.5rem;
      border-radius: 10px; font-family: 'Josefin Sans', sans-serif;
      font-size: 0.82rem; color: #e2c97e; letter-spacing: 0.04em;
      box-shadow: 0 8px 32px rgba(0,0,0,0.5);
      transform: translateY(20px); opacity: 0;
      transition: transform 0.3s ease, opacity 0.3s ease;
    `;
    toast.textContent = msg;
    document.body.appendChild(toast);

    requestAnimationFrame(() => {
      toast.style.transform = 'translateY(0)';
      toast.style.opacity   = '1';
    });

    setTimeout(() => {
      toast.style.transform = 'translateY(20px)';
      toast.style.opacity   = '0';
      setTimeout(() => toast.remove(), 400);
    }, 3500);
  }

  /* ── Navbar scroll shadow ────────────────────────────────────── */
  function initNavbar() {
    const nav = document.querySelector('.lf-nav');
    if (!nav) return;

    const onScroll = () => {
      if (window.scrollY > 20) {
        nav.style.background = 'rgba(13, 13, 13, 0.96)';
      } else {
        nav.style.background = 'rgba(13, 13, 13, 0.85)';
      }
    };

    window.addEventListener('scroll', onScroll, { passive: true });

    // Mark active link
    const path = window.location.pathname;
    nav.querySelectorAll('a').forEach((a) => {
      const href = a.getAttribute('href');
      if (href && path.startsWith(href) && href !== '/') {
        a.classList.add('active');
      }
    });
  }

  /* ── Boot ────────────────────────────────────────────────────── */
  function boot() {
    initReveal();
    initScoreRings();
    initStars();
    initHorizontalScroll();
    initConfirm();
    initToast();
    initNavbar();
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', boot);
  } else {
    boot();
  }
})();
