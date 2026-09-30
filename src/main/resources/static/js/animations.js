/* =====================================================
   ANIMATIONS ENGINE — Shree Ganesh Enterprises
   Gmail-palette particle system + full UI animations
===================================================== */

(function () {
    "use strict";

    /* ── PAGE LOAD FADE-IN ── */
    document.documentElement.style.opacity = "0";
    document.documentElement.style.transition = "opacity 0.45s ease";
    window.addEventListener("load", () => {
        document.documentElement.style.opacity = "1";
    });

    /* ── NAVBAR SHRINK ON SCROLL ── */
    const header = document.querySelector("header");
    let lastScroll = 0;

    window.addEventListener("scroll", () => {
        const y = window.scrollY;
        if (y > 60) {
            header && header.classList.add("header-compact");
        } else {
            header && header.classList.remove("header-compact");
        }
        if (y > lastScroll && y > 120) {
            header && header.classList.add("header-hidden");
        } else {
            header && header.classList.remove("header-hidden");
        }
        lastScroll = y <= 0 ? 0 : y;
    }, { passive: true });

    /* ── ACTIVE NAV LINK ── */
    const path = window.location.pathname.split("?")[0];
    document.querySelectorAll(".header-nav .nav-link, .mobile-nav-panel a").forEach(a => {
        const href = a.getAttribute("href");
        if (href === path || (path === "/" && href === "/") || (path !== "/" && href !== "/" && path.startsWith(href))) {
            a.classList.add("active");
        }
    });

    /* ── SCROLL REVEAL (staggered) ── */
    const revealObserver = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (!entry.isIntersecting) return;
            const el = entry.target;
            const siblings = el.parentElement ? [...el.parentElement.children] : [el];
            const idx = siblings.indexOf(el);
            setTimeout(() => {
                el.classList.add("visible");
            }, idx * 90);
            revealObserver.unobserve(el);
        });
    }, { threshold: 0.1, rootMargin: "0px 0px -40px 0px" });

    function attachReveal() {
        document.querySelectorAll(
            ".reveal, .category-card, .why-card, .mvv-card, .contact-card, .product-card, .stat-item"
        ).forEach(el => {
            if (!el.classList.contains("visible")) {
                el.classList.add("reveal");
                revealObserver.observe(el);
            }
        });
    }

    /* ── COUNTER ANIMATION ── */
    function animateCounter(el) {
        const target = parseInt(el.getAttribute("data-count"), 10);
        if (isNaN(target)) return;
        const suffix = el.getAttribute("data-suffix") || "";
        const duration = 1800;
        const start = performance.now();
        function step(now) {
            const progress = Math.min((now - start) / duration, 1);
            const eased = 1 - Math.pow(1 - progress, 3);
            el.textContent = Math.round(eased * target) + suffix;
            if (progress < 1) requestAnimationFrame(step);
        }
        requestAnimationFrame(step);
    }

    const counterObserver = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (!entry.isIntersecting) return;
            animateCounter(entry.target);
            counterObserver.unobserve(entry.target);
        });
    }, { threshold: 0.5 });

    function attachCounters() {
        document.querySelectorAll("[data-count]").forEach(el => counterObserver.observe(el));
    }

    /* ── BUTTON RIPPLE ── */
    document.addEventListener("click", (e) => {
        const btn = e.target.closest(
            ".btn, .btn-primary, .btn-secondary, .about-btn, .add-to-cart-btn, .enquire-btn," +
            ".auth-submit, .cart-checkout-btn, .enquiry-form button[type='submit']," +
            ".search-box button, .logout-btn, .btn-signup, .btn-login, .whatsapp-btn, .category-btn"
        );
        if (!btn) return;
        const ripple = document.createElement("span");
        ripple.className = "ripple-fx";
        const rect = btn.getBoundingClientRect();
        const size = Math.max(rect.width, rect.height);
        ripple.style.cssText = `
            width:${size}px; height:${size}px;
            left:${e.clientX - rect.left - size / 2}px;
            top:${e.clientY - rect.top - size / 2}px;
        `;
        btn.style.position = btn.style.position || "relative";
        btn.style.overflow = "hidden";
        btn.appendChild(ripple);
        setTimeout(() => ripple.remove(), 600);
    });

    /* ── PARALLAX HERO ── */
    const heroSlider = document.querySelector(".hero-section");
    if (heroSlider) {
        window.addEventListener("scroll", () => {
            const y = window.scrollY;
            if (y < 600) {
                heroSlider.style.setProperty("--parallax-y", y * 0.3 + "px");
            }
        }, { passive: true });
    }

    /* ── SMOOTH IMAGE LOAD ── */
    document.querySelectorAll("img").forEach(img => {
        if (img.complete) return;
        img.style.opacity = "0";
        img.style.transition = "opacity 0.5s ease";
        img.addEventListener("load", () => { img.style.opacity = "1"; });
    });

    /* ── FLOATING LABEL INPUTS ── */
    document.querySelectorAll(".enquiry-form input, .enquiry-form textarea, .enquiry-form select").forEach(input => {
        const wrap = input.parentElement;
        if (!wrap.classList.contains("field-wrap")) return;
        input.addEventListener("focus", () => wrap.classList.add("active"));
        input.addEventListener("blur", () => {
            if (!input.value) wrap.classList.remove("active");
        });
        if (input.value) wrap.classList.add("active");
    });

    /* ── MOBILE OVERLAY (close on outside click) ── */
    document.addEventListener("click", (e) => {
        const panel = document.getElementById("mobileNav");
        const btn = document.querySelector(".mobile-menu-btn");
        if (panel && panel.classList.contains("open") && !panel.contains(e.target) && !btn?.contains(e.target)) {
            panel.classList.remove("open");
        }
    });

    /* ============================================================
       GMAIL-PALETTE PARTICLE SYSTEM
       Colors: Red #EA4335 | Blue #4285F4 | Yellow #FBBC05 | Green #34A853
    ============================================================ */

    const GMAIL_COLORS = [
        { r: 234, g: 67,  b: 53  },   // Gmail Red
        { r: 66,  g: 133, b: 244 },   // Gmail Blue
        { r: 251, g: 188, b: 5   },   // Gmail Yellow
        { r: 52,  g: 168, b: 83  },   // Gmail Green
    ];

    function gmailColor(idx, alpha) {
        const c = GMAIL_COLORS[idx % GMAIL_COLORS.length];
        return `rgba(${c.r},${c.g},${c.b},${alpha})`;
    }

    function createGmailParticleSystem(container, opts) {
        opts = opts || {};
        const COUNT        = opts.count        || 80;
        const DIST         = opts.dist         || 100;
        const MOUSE_RADIUS = opts.mouseRadius  || 110;
        const Z_INDEX      = opts.zIndex       || 2;
        const OPACITY      = opts.opacity      || 1;
        const SPEED        = opts.speed        || 1;

        /* ── Canvas setup ── */
        const canvas = document.createElement("canvas");
        canvas.className = "gmail-particle-canvas";
        canvas.style.cssText =
            "position:absolute;top:0;left:0;width:100%;height:100%;" +
            "pointer-events:none;z-index:" + Z_INDEX + ";opacity:" + OPACITY + ";";

        const pos = window.getComputedStyle(container).position;
        if (pos === "static") container.style.position = "relative";
        container.insertBefore(canvas, container.firstChild);

        const ctx = canvas.getContext("2d");
        let particles = [];
        let mouse = { x: -9999, y: -9999 };
        let animRunning = true;

        /* ── Size ── */
        function resize() {
            canvas.width  = container.offsetWidth;
            canvas.height = container.offsetHeight;
        }

        function rand(a, b) { return a + Math.random() * (b - a); }

        /* ── Particle factory ── */
        function makeParticle() {
            const colorIdx = Math.floor(Math.random() * 4);
            return {
                x:      rand(0, canvas.width),
                y:      rand(0, canvas.height),
                r:      rand(2, 4.5),
                dx:     rand(-0.35, 0.35) * SPEED,
                dy:     rand(-0.6, -0.1)  * SPEED,
                alpha:  rand(0.55, 0.92),
                colorIdx,
                pulse:  rand(0, Math.PI * 2),   // for gentle size pulsing
                ox: 0, oy: 0
            };
        }

        function resetParticle(p) {
            p.x      = rand(0, canvas.width);
            p.y      = canvas.height + 10;
            p.dx     = rand(-0.35, 0.35) * SPEED;
            p.dy     = rand(-0.6, -0.1)  * SPEED;
            p.alpha  = rand(0.55, 0.92);
            p.colorIdx = Math.floor(Math.random() * 4);
            p.ox = 0; p.oy = 0;
        }

        resize();
        window.addEventListener("resize", resize, { passive: true });
        for (let i = 0; i < COUNT; i++) particles.push(makeParticle());

        /* ── Mouse tracking (relative to canvas) ── */
        container.addEventListener("mousemove", function(e) {
            const rect = canvas.getBoundingClientRect();
            mouse.x = e.clientX - rect.left;
            mouse.y = e.clientY - rect.top;
        }, { passive: true });

        container.addEventListener("mouseleave", function() {
            mouse.x = -9999; mouse.y = -9999;
        }, { passive: true });

        /* ── Touch support ── */
        container.addEventListener("touchmove", function(e) {
            const rect = canvas.getBoundingClientRect();
            const t = e.touches[0];
            mouse.x = t.clientX - rect.left;
            mouse.y = t.clientY - rect.top;
        }, { passive: true });

        container.addEventListener("touchend", function() {
            mouse.x = -9999; mouse.y = -9999;
        }, { passive: true });

        /* ── Draw loop ── */
        let frame = 0;
        function draw() {
            if (!animRunning) return;
            ctx.clearRect(0, 0, canvas.width, canvas.height);
            frame++;

            /* Connection lines between nearby particles */
            for (let i = 0; i < particles.length; i++) {
                for (let j = i + 1; j < particles.length; j++) {
                    const dx = particles[i].x - particles[j].x;
                    const dy = particles[i].y - particles[j].y;
                    const d  = Math.sqrt(dx * dx + dy * dy);
                    if (d < DIST) {
                        const lineAlpha = 0.18 * (1 - d / DIST);
                        /* Use average color of the two particles for the line */
                        const ci = particles[i].colorIdx;
                        const cj = particles[j].colorIdx;
                        const c1 = GMAIL_COLORS[ci];
                        const c2 = GMAIL_COLORS[cj];
                        const mr = (c1.r + c2.r) / 2;
                        const mg = (c1.g + c2.g) / 2;
                        const mb = (c1.b + c2.b) / 2;

                        ctx.beginPath();
                        ctx.strokeStyle = `rgba(${mr},${mg},${mb},${lineAlpha})`;
                        ctx.lineWidth = 0.8;
                        ctx.moveTo(particles[i].x, particles[i].y);
                        ctx.lineTo(particles[j].x, particles[j].y);
                        ctx.stroke();
                    }
                }
            }

            /* Dots + mouse repulsion */
            particles.forEach(p => {
                /* Mouse repulsion */
                const mdx = p.x - mouse.x;
                const mdy = p.y - mouse.y;
                const md  = Math.sqrt(mdx * mdx + mdy * mdy);
                if (md < MOUSE_RADIUS && md > 0) {
                    const force = (MOUSE_RADIUS - md) / MOUSE_RADIUS;
                    p.ox = (mdx / md) * force * 3.5;
                    p.oy = (mdy / md) * force * 3.5;
                } else {
                    p.ox *= 0.85;
                    p.oy *= 0.85;
                }

                /* Gentle pulse on radius */
                p.pulse += 0.025;
                const pulseR = p.r + Math.sin(p.pulse) * 0.6;

                /* Glow shadow */
                ctx.shadowBlur  = 8;
                ctx.shadowColor = gmailColor(p.colorIdx, 0.5);

                /* Draw dot */
                ctx.beginPath();
                ctx.arc(p.x, p.y, pulseR, 0, Math.PI * 2);
                ctx.fillStyle = gmailColor(p.colorIdx, p.alpha);
                ctx.fill();

                ctx.shadowBlur = 0;

                /* Move */
                p.x += p.dx + p.ox;
                p.y += p.dy + p.oy;

                /* Wrap / reset */
                if (p.y < -10) resetParticle(p);
                if (p.x < -8)  p.x = canvas.width + 8;
                if (p.x > canvas.width + 8) p.x = -8;
            });

            requestAnimationFrame(draw);
        }

        /* Pause when tab hidden to save CPU */
        document.addEventListener("visibilitychange", () => {
            animRunning = !document.hidden;
            if (animRunning) draw();
        });

        draw();
    }

    /* ── HERO PARTICLES ── */
    function initHeroParticles() {
        const hero =
            document.querySelector(".hero-section") ||
            document.querySelector(".about-hero") ||
            document.querySelector(".contact-header") ||
            document.querySelector(".category-page-hero") ||
            document.querySelector(".category-banner");
        if (!hero) return;
        createGmailParticleSystem(hero, {
            count: 85,
            dist: 105,
            mouseRadius: 120,
            zIndex: 2,
            opacity: 0.85,
            speed: 0.95
        });
    }

    /* ── FOOTER PARTICLES ── */
    function initFooterParticles() {
        const footer = document.querySelector("footer.footer");
        if (!footer) return;
        createGmailParticleSystem(footer, {
            count: 65,
            dist: 90,
            mouseRadius: 100,
            zIndex: 0,
            opacity: 0.55,
            speed: 0.7
        });
    }

    /* ── GLOW CURSOR (desktop only) ── */
    if (!window.matchMedia("(pointer: coarse)").matches) {
        const glow = document.createElement("div");
        glow.id = "cursor-glow";
        document.body.appendChild(glow);

        let cx = -200, cy = -200, tx = -200, ty = -200;

        document.addEventListener("mousemove", (e) => {
            tx = e.clientX;
            ty = e.clientY;
            if (glow.style.opacity === "0" || !glow.style.opacity) {
                glow.style.opacity = "1";
                cx = tx; cy = ty;
            }
        }, { passive: true });

        const hoverTargets = "a, button, input, textarea, select, [data-url], .category-card, .product-card";
        document.addEventListener("mouseover", (e) => {
            if (e.target.closest(hoverTargets)) glow.classList.add("cursor-hover");
        }, { passive: true });
        document.addEventListener("mouseout", (e) => {
            if (e.target.closest(hoverTargets)) glow.classList.remove("cursor-hover");
        }, { passive: true });

        document.addEventListener("mouseleave", () => { glow.style.opacity = "0"; }, { passive: true });

        function animateCursor() {
            cx += (tx - cx) * 0.14;
            cy += (ty - cy) * 0.14;
            glow.style.left = cx + "px";
            glow.style.top  = cy + "px";
            requestAnimationFrame(animateCursor);
        }
        animateCursor();
    }

    /* ── PAGE LOAD HERO INTRO ── */
    function initPageLoadIntro() {
        const heroContent = document.querySelector(".hero-content");
        if (heroContent) {
            heroContent.querySelectorAll("h1, p, a, button").forEach((el, i) => {
                el.classList.add("hero-intro-animate");
                el.style.animationDelay = (0.15 + i * 0.14) + "s";
            });
        }
        const altHero = document.querySelector(".about-hero-content, .contact-header");
        if (altHero) {
            altHero.querySelectorAll("h1, h2, p").forEach((el, i) => {
                el.classList.add("hero-intro-animate");
                el.style.animationDelay = (0.12 + i * 0.12) + "s";
            });
        }
    }

    /* ── INIT ── */
    document.addEventListener("DOMContentLoaded", () => {
        attachReveal();
        attachCounters();
    });

    window.addEventListener("load", () => {
        attachReveal();
        attachCounters();
        initHeroParticles();
        initFooterParticles();
        initPageLoadIntro();
    });

})();