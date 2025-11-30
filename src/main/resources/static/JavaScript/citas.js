/**
 * Función para alternar entre pestañas (Tabs)
 * @param {Event} evt - El evento del click
 * @param {string} tabName - El ID del contenedor que queremos mostrar ('proximas' o 'historial')
 */
function openTab(evt, tabName) {
    var i, tabContent, tabLinks;

    // 1. Ocultar todos los contenidos de las pestañas
    // Buscamos todos los elementos con la clase "tab-content"
    tabContent = document.getElementsByClassName("tab-content");
    for (i = 0; i < tabContent.length; i++) {
        tabContent[i].style.display = "none";
        // Opcional: Remover la clase active-content si se usó para estilos iniciales
        tabContent[i].classList.remove("active-content");
    }

    // 2. Desactivar todos los botones de las pestañas
    // Buscamos todos los botones con la clase "tab-btn"
    tabLinks = document.getElementsByClassName("tab-btn");
    for (i = 0; i < tabLinks.length; i++) {
        // Reemplazamos la clase "active" por vacío para quitar el estilo azul
        tabLinks[i].className = tabLinks[i].className.replace(" active", "");
    }

    // 3. Mostrar la pestaña actual y activar el botón presionado
    document.getElementById(tabName).style.display = "block";
    
    // Añadimos la clase "active" al botón que disparó el evento
    evt.currentTarget.className += " active";
}

// Inicialización opcional: Asegurar que la primera pestaña esté abierta al cargar
document.addEventListener("DOMContentLoaded", function() {
    // Si por defecto el HTML no tiene el estilo inline 'display: block', esto asegura que se vea la primera
    const defaultTab = document.getElementById("proximas");
    if (defaultTab && getComputedStyle(defaultTab).display === "none") {
        defaultTab.style.display = "block";
    }
});