console.log("Website loaded successfully!");

// ===== MOBILE MENU =====
function toggleMenu() {
    const nav = document.getElementById("mobileNav");
    if (nav) nav.classList.toggle("open");
}

// ===== GLOBAL TOAST (FALLBACK) =====
function showToast(msg) {
    const t = document.getElementById("toast");
    if (!t) return;

    t.innerText = msg;
    t.classList.remove("show");
    void t.offsetWidth;
    t.classList.add("show");

    setTimeout(() => t.classList.remove("show"), 2000);
}

// ===== CART COUNT =====
function updateCartCount() {
    const desk = document.getElementById("cart-count");
    const mob = document.getElementById("cart-count-mobile");
    const mobMenu = document.getElementById("cart-count-mobile-menu");

    fetch("/cart/count")
        .then(res => res.json())
        .then(data => {
            if (desk) desk.textContent = data.count;
            if (mob) mob.textContent = data.count;
            if (mobMenu) mobMenu.textContent = data.count;
        })
        .catch(err => console.error("Cart count error", err));
}

// ===== MAIN INIT =====
document.addEventListener("DOMContentLoaded", () => {

    // Update cart count
    updateCartCount();

    // ===== QTY BUTTONS (LIMIT FIX) =====
    document.querySelectorAll(".qty-box").forEach(box => {

        const plus = box.querySelector(".plus");
        const minus = box.querySelector(".minus");
        const input = box.querySelector(".qty-input") || box.querySelector("#qty");

        if (!plus || !minus || !input) return;

        const max = parseInt(input.getAttribute("max")) || 200;

        // 🔹 Typing control
        input.addEventListener("input", () => {
            let val = parseInt(input.value) || 1;

            if (val < 1) val = 1;

            if (val > max) {
                val = max;
                showToast("Maximum " + max + " allowed");
            }

            input.value = val;
        });

        // 🔹 Plus button control
        plus.addEventListener("click", () => {
            let val = parseInt(input.value) || 1;

            if (val >= max) {
                showToast("Max limit reached");
                return;
            }

            input.value = val + 1;
        });

        // 🔹 Minus button control
        minus.addEventListener("click", () => {
            let val = parseInt(input.value) || 1;

            if (val > 1) {
                input.value = val - 1;
            }
        });

    });

    // ===== CSRF =====
    const csrfTokenMeta = document.querySelector('meta[name="_csrf"]');
    const csrfHeaderMeta = document.querySelector('meta[name="_csrf_header"]');

    const csrfToken = csrfTokenMeta ? csrfTokenMeta.getAttribute('content') : null;
    const csrfHeader = csrfHeaderMeta ? csrfHeaderMeta.getAttribute('content') : null;

    // ===== ADD TO CART =====
    document.querySelectorAll(".add-to-cart-btn").forEach(btn => {
        btn.addEventListener("click", () => {

            const productId = btn.getAttribute("data-product-id");

            // Works for BOTH product grid and details page
            const qtyInput =
                btn.closest(".product-card")?.querySelector(".qty-input") ||
                document.querySelector("#qty");

            const qty = qtyInput ? qtyInput.value : 1;

            fetch("/cart/ajax/add", {
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded",
                    "X-Requested-With": "XMLHttpRequest",
                    ...(csrfToken && csrfHeader ? { [csrfHeader]: csrfToken } : {})
                },
                body: "id=" + productId + "&qty=" + qty
            })
            .then(res => res.json())
            .then(data => {

                if (data.error === "not_logged_in") {
                    // ✅ Save current page so user returns here after login
                    const returnUrl = encodeURIComponent(window.location.href);
                    window.location.href = "/login?redirect=" + returnUrl;
                    return;
                }

                showToast("Added to Cart!");
                updateCartCount();
            })
            .catch(err => console.error("Add cart error", err));
        });
    });

});

// ===== SAFE FALLBACK =====
if (typeof window.openProductCard !== "function") {
    window.openProductCard = function () {};
}
// ===== PRODUCT CARD CLICK (SAFE VERSION) =====
document.querySelectorAll(".product-card").forEach(card => {

    card.addEventListener("click", (event) => {

        // Prevent navigation if clicking interactive elements
        if (
            event.target.closest(".qty-box") ||
            event.target.closest(".add-to-cart-btn") ||
            event.target.closest(".view-details-btn") ||
            event.target.closest("button") ||
            event.target.closest("input") ||
            event.target.closest("a")
        ) return;

        const url = card.getAttribute("data-url");
        if (url) window.location.href = url;
    });

});