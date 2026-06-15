/* ============================================================
   LINJU FIND — three-room.js
   360° equirectangular room viewer using Three.js CDN
   Drag to look around. Auto-rotates when idle.
   ============================================================ */

(function () {
  'use strict';

  const CANVAS_ID   = 'lf-room-canvas';
  const IMAGE_PATH  = '/images/listing detail page.png';
  const AUTO_SPEED  = 0.0004; // radians per frame

  function init() {
    const wrap  = document.querySelector('.lf-room-canvas-wrap');
    const canvas = document.getElementById(CANVAS_ID);
    if (!wrap || !canvas) return;

    // Load Three.js from CDN dynamically
    const script = document.createElement('script');
    script.src = 'https://cdn.jsdelivr.net/npm/three@0.160.1/build/three.min.js';
    script.onload = () => buildScene(wrap, canvas);
    script.onerror = () => {
      wrap.innerHTML = '<div style="display:flex;align-items:center;justify-content:center;height:100%;color:#6b6356;font-size:0.8rem;letter-spacing:0.1em;">360° viewer unavailable</div>';
    };
    document.head.appendChild(script);
  }

  function buildScene(wrap, canvas) {
    const W = wrap.clientWidth;
    const H = wrap.clientHeight;

    /* Scene */
    const scene    = new THREE.Scene();
    const camera   = new THREE.PerspectiveCamera(75, W / H, 0.1, 1000);
    camera.position.set(0, 0, 0.01);

    const renderer = new THREE.WebGLRenderer({ canvas, antialias: true });
    renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2));
    renderer.setSize(W, H);

    /* Sphere geometry — inside faces */
    const geometry = new THREE.SphereGeometry(500, 64, 40);
    geometry.scale(-1, 1, 1); // flip normals to render inside

    /* Texture */
    const loader  = new THREE.TextureLoader();
    const texture = loader.load(IMAGE_PATH);
    texture.colorSpace = THREE.SRGBColorSpace;

    const material = new THREE.MeshBasicMaterial({ map: texture });
    const sphere   = new THREE.Mesh(geometry, material);
    scene.add(sphere);

    /* Orbit state */
    let isDown    = false;
    let lastX     = 0;
    let lastY     = 0;
    let lon       = 0;   // horizontal angle (degrees)
    let lat       = 0;   // vertical angle (degrees)
    let targetLon = 0;
    let targetLat = 0;
    let idleTimer = null;
    let autoRotate = false;

    const LAT_MAX = 75;

    function resetIdle() {
      autoRotate = false;
      clearTimeout(idleTimer);
      idleTimer = setTimeout(() => { autoRotate = true; }, 4000);
    }

    resetIdle();

    /* Pointer events */
    canvas.addEventListener('pointerdown', (e) => {
      isDown = true;
      lastX  = e.clientX;
      lastY  = e.clientY;
      resetIdle();
      canvas.setPointerCapture(e.pointerId);
    });

    canvas.addEventListener('pointermove', (e) => {
      if (!isDown) return;
      const dx = e.clientX - lastX;
      const dy = e.clientY - lastY;
      lastX = e.clientX;
      lastY = e.clientY;
      targetLon -= dx * 0.18;
      targetLat  = Math.max(-LAT_MAX, Math.min(LAT_MAX, targetLat + dy * 0.18));
      resetIdle();
    });

    canvas.addEventListener('pointerup',   () => { isDown = false; });
    canvas.addEventListener('pointerleave', () => { isDown = false; });

    /* Touch */
    let touchLast = null;
    canvas.addEventListener('touchstart', (e) => {
      touchLast = { x: e.touches[0].clientX, y: e.touches[0].clientY };
      resetIdle();
    }, { passive: true });

    canvas.addEventListener('touchmove', (e) => {
      if (!touchLast) return;
      const dx = e.touches[0].clientX - touchLast.x;
      const dy = e.touches[0].clientY - touchLast.y;
      touchLast = { x: e.touches[0].clientX, y: e.touches[0].clientY };
      targetLon -= dx * 0.18;
      targetLat  = Math.max(-LAT_MAX, Math.min(LAT_MAX, targetLat + dy * 0.18));
      resetIdle();
    }, { passive: true });

    /* Resize */
    const ro = new ResizeObserver(() => {
      const nw = wrap.clientWidth;
      const nh = wrap.clientHeight;
      renderer.setSize(nw, nh);
      camera.aspect = nw / nh;
      camera.updateProjectionMatrix();
    });
    ro.observe(wrap);

    /* Hint fade */
    const hint = wrap.querySelector('.lf-room-hint');
    if (hint) {
      setTimeout(() => { hint.style.opacity = '0'; }, 3000);
    }

    /* Render loop */
    function animate() {
      requestAnimationFrame(animate);

      if (autoRotate) {
        targetLon += AUTO_SPEED * (180 / Math.PI);
      }

      // Smooth lerp
      lon += (targetLon - lon) * 0.08;
      lat += (targetLat - lat) * 0.08;

      const phi   = THREE.MathUtils.degToRad(90 - lat);
      const theta = THREE.MathUtils.degToRad(lon);

      camera.lookAt(
        500 * Math.sin(phi) * Math.cos(theta),
        500 * Math.cos(phi),
        500 * Math.sin(phi) * Math.sin(theta)
      );

      renderer.render(scene, camera);
    }

    animate();
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', init);
  } else {
    init();
  }
})();
