console.log("Website loaded successfully!");

// ===== MOBILE MENU =====
function toggleMenu() {
    const nav = document.getElementById("mobileNav");
    if (nav) nav.classList.toggle("open");
}

// ===== CART COUNT (GLOBAL) =====
function updateCartCount() {
    const desk = document.getElementById("cart-count");
    const mob = document.getElementById("cart-count-mobile");
    const mobMenu = document.getElementById("cart-count-mobile-menu");

    fetch("/cart/count")
        .then(res => res.json())
        .then(data => {
            if (desk) desk.innerText = data.count;
            if (mob) mob.innerText = data.count;
            if (mobMenu) mobMenu.innerText = data.count;
        })
        .catch(err => console.error("Cart count error", err));
}

// Auto-load safely
document.addEventListener("DOMContentLoaded", () => {
    updateCartCount();
});

// ===== SAFE FALLBACK =====
if (typeof window.openProductCard !== "function") {
    window.openProductCard = function () {};
}
