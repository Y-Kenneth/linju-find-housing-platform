/* ── Property marquee: always-scrolling + draggable, never stops ──────────
   A rAF loop drives the transform (instead of a CSS animation) so hovering
   never pauses it. Dragging adds an offset, but the constant drift keeps
   going — the user can nudge it left/right yet cannot halt it.
   Shared by the login & register pages. */
(function () {
    var marquee = document.getElementById('lfMarquee');
    var track   = document.getElementById('lfMarqueeTrack');
    if (!marquee || !track) return;

    var reduce = window.matchMedia &&
                 window.matchMedia('(prefers-reduced-motion: reduce)').matches;

    var SPEED = reduce ? 0 : 36;   // px/sec constant drift (0 = respect reduced-motion)
    var pos   = 0;                 // virtual scroll position (px)
    var half  = 0;                 // width of one copy of the cards
    var dragging = false, lastX = 0;

    function measure() {
        // track holds two identical copies, so one loop = half the scroll width
        half = track.scrollWidth / 2;
    }
    measure();
    window.addEventListener('resize', measure);
    window.addEventListener('load', measure);   // images change width after first paint

    function wrap(x) {
        if (half <= 0) return 0;
        return ((x % half) + half) % half;       // keep within [0, half) for a seamless loop
    }

    var last = performance.now();
    function frame(now) {
        var dt = (now - last) / 1000;
        last = now;
        pos += SPEED * dt;                        // never stops
        track.style.transform = 'translateX(' + (-wrap(pos)) + 'px)';
        requestAnimationFrame(frame);
    }
    requestAnimationFrame(frame);

    /* Drag to nudge left/right (pointer events cover mouse + touch) */
    marquee.addEventListener('pointerdown', function (e) {
        dragging = true;
        lastX = e.clientX;
        marquee.classList.add('is-dragging');
        if (marquee.setPointerCapture) {
            try { marquee.setPointerCapture(e.pointerId); } catch (err) {}
        }
    });
    marquee.addEventListener('pointermove', function (e) {
        if (!dragging) return;
        var dx = e.clientX - lastX;
        lastX = e.clientX;
        pos -= dx;                                // drag right → content moves right
    });
    function endDrag(e) {
        if (!dragging) return;
        dragging = false;
        marquee.classList.remove('is-dragging');
        if (marquee.releasePointerCapture && e && e.pointerId != null) {
            try { marquee.releasePointerCapture(e.pointerId); } catch (err) {}
        }
    }
    marquee.addEventListener('pointerup', endDrag);
    marquee.addEventListener('pointercancel', endDrag);
    marquee.addEventListener('pointerleave', endDrag);
})();
