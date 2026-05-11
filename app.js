/* ================================================================
   WaterSave – app.js
   Slide navigation logic
   ================================================================ */

let currentSlide = 1;
const TOTAL = 5;

function goTo(n) {
  if (n < 1 || n > TOTAL) return;

  document.getElementById('slide-' + currentSlide).classList.remove('active');
  document.querySelectorAll('.nav-dot')[currentSlide - 1].classList.remove('active');

  currentSlide = n;

  document.getElementById('slide-' + currentSlide).classList.add('active');
  document.querySelectorAll('.nav-dot')[currentSlide - 1].classList.add('active');

  document.getElementById('page-counter').textContent = currentSlide + ' de ' + TOTAL;

  const prev = document.getElementById('btn-prev');
  const next = document.getElementById('btn-next');

  prev.disabled = currentSlide === 1;
  next.disabled = currentSlide === TOTAL;
  next.textContent = currentSlide === TOTAL ? 'Contatar →' : 'Próximo →';

  window.scrollTo({ top: 0, behavior: 'smooth' });
}

// Keyboard navigation
document.addEventListener('keydown', function (e) {
  if (e.key === 'ArrowRight' || e.key === 'ArrowDown') goTo(currentSlide + 1);
  if (e.key === 'ArrowLeft'  || e.key === 'ArrowUp')   goTo(currentSlide - 1);
});

// Init
goTo(1);
