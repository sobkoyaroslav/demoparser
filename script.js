(() => {
    const cards = document.querySelectorAll('.m5k_24');
    const importedReviews = [];

    cards.forEach(card => {
        try {
            const authorEl = card.querySelector('.bq03_8-3-a, .lj0_24'); 
            const author = authorEl ? authorEl.textContent.trim() : "Аноним";
            const textEl = card.querySelector('.o1j_24');
            const text = textEl ? textEl.textContent.trim() : "";
            const ratingEl = card.querySelector('.o0j_24');
            let rating = 5;
            if (ratingEl) {
                const match = ratingEl.textContent.match(/\d+/);
                if (match) rating = parseInt(match[0]);
            }
            if (text.length > 0) {
                importedReviews.push({ author, text, rating, platform: "Ozon" });
            }
        } catch (e) {}
    });

    // Вместо fetch просто выводим JSON в консоль в виде готовой строки!
    console.log("👉 СКОПИРУЙ ТЕКСТ НИЖЕ (БЕЗ КАВЫЧЕК):");
    console.log(JSON.stringify(importedReviews, null, 2));
})();