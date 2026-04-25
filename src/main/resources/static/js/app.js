/* DocApp – client-side helpers */

// Auto-dismiss Bootstrap alerts after 4 s
document.addEventListener('DOMContentLoaded', () => {
  document.querySelectorAll('.alert[data-bs-dismiss]').forEach(el => {
    setTimeout(() => {
      const alert = bootstrap.Alert.getOrCreateInstance(el);
      alert && alert.close();
    }, 4000);
  });
});
