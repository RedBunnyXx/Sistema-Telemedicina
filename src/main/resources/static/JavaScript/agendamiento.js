// --- Estado de la Aplicación ---
let appState = {
    selectedDoctor: null,
    selectedDate: null, // Default por diseño imagen
    selectedTime: "14:00" // Default por diseño imagen
};

// --- Inicialización ---
document.addEventListener("DOMContentLoaded", () => {
    const searchInput = document.querySelector(".search-input input");
    if (searchInput) {
        searchInput.addEventListener("input", () => {
            filterDoctors();
        });
    }

    const dateInput = document.getElementById("datePicker");
    if (dateInput) {
        inicializarSelectorFecha(dateInput);
    }

    // Los doctores ya vienen renderizados por Thymeleaf en el HTML.
});

// --- Funciones Paso 1: Doctores ---

function selectDoctorCard(cardElement) {
    if (!cardElement) return;

    const cards = document.querySelectorAll(".doctor-card");
    cards.forEach(c => c.classList.remove("selected"));

    cardElement.classList.add("selected");

    appState.selectedDoctor = {
        id: parseInt(cardElement.dataset.id, 10),
        name: cardElement.dataset.name,
        specialty: cardElement.dataset.specialty,
        cmp: cardElement.dataset.cmp
    };

    const btnNext = document.getElementById("btn-next-1");
    if (btnNext) {
        btnNext.disabled = false;
    }

    const doctorNameRef = document.getElementById("doctorNameRef");
    if (doctorNameRef) {
        doctorNameRef.innerText = appState.selectedDoctor.name;
    }
}

function filterDoctors() {
    const specialtySelect = document.getElementById("specialtyFilter");
    const searchInput = document.querySelector(".search-input input");
    const specialtyValue = specialtySelect ? specialtySelect.value : "all";
    const searchText = searchInput ? searchInput.value.toLowerCase().trim() : "";

    const cards = document.querySelectorAll(".doctor-card");
    cards.forEach(card => {
        const cardSpec = (card.dataset.specialty || "").toLowerCase();
        const cardName = (card.dataset.name || "").toLowerCase();

        const matchSpec = specialtyValue === "all" || cardSpec === specialtyValue.toLowerCase();
        const matchSearch =
            !searchText ||
            cardName.includes(searchText) ||
            cardSpec.includes(searchText);

        if (matchSpec && matchSearch) {
            card.style.display = "";
        } else {
            card.style.display = "none";
        }
    });

    appState.selectedDoctor = null;
    const btnNext = document.getElementById("btn-next-1");
    if (btnNext) {
        btnNext.disabled = true;
    }
}

const DIAS_SEMANA = ["domingo", "lunes", "martes", "miércoles", "jueves", "viernes", "sábado"];
const MESES = ["enero", "febrero", "marzo", "abril", "mayo", "junio", "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre"];

function formatearFechaLarga(date) {
    const diaNombre = DIAS_SEMANA[date.getDay()];
    const dia = date.getDate();
    const mesNombre = MESES[date.getMonth()];
    const anio = date.getFullYear();
    return `${diaNombre}, ${dia} de ${mesNombre} de ${anio}`;
}

function formatearFechaCorta(date) {
    const dia = date.getDate();
    const mesNombre = MESES[date.getMonth()];
    return `${dia} de ${mesNombre}`;
}

function inicializarSelectorFecha(dateInput) {
    const hoy = new Date();
    const yyyy = hoy.getFullYear();
    const mm = String(hoy.getMonth() + 1).padStart(2, "0");
    const dd = String(hoy.getDate()).padStart(2, "0");
    const isoHoy = `${yyyy}-${mm}-${dd}`;

    dateInput.min = isoHoy; // Bloquea fechas anteriores a hoy
    dateInput.value = isoHoy;

    const fechaLarga = formatearFechaLarga(hoy);
    const fechaCorta = formatearFechaCorta(hoy);

    appState.selectedDate = fechaLarga;
    actualizarSeleccionFechaUI(fechaLarga, fechaCorta);
    checkStep2Validity();

    dateInput.addEventListener("change", () => {
        if (dateInput.value) {
            const [year, month, day] = dateInput.value.split("-").map(Number);
            const seleccionada = new Date(year, month - 1, day);
            const fechaLargaSel = formatearFechaLarga(seleccionada);
            const fechaCortaSel = formatearFechaCorta(seleccionada);
            appState.selectedDate = fechaLargaSel;
            actualizarSeleccionFechaUI(fechaLargaSel, fechaCortaSel);
        } else {
            appState.selectedDate = null;
            actualizarSeleccionFechaUI(null, null);
        }
        checkStep2Validity();
    });
}

function actualizarSeleccionFechaUI(fechaLarga, fechaCorta) {
    const display = document.getElementById("selectedDateDisplay");
    if (display) {
        if (fechaLarga) {
            display.innerHTML = `Fecha seleccionada: <strong>${fechaLarga}</strong>`;
        } else {
            display.innerHTML = "Fecha seleccionada: <strong>Selecciona una fecha</strong>";
        }
    }

    const doctorDateRef = document.getElementById("doctorDateRef");
    if (doctorDateRef) {
        if (fechaCorta) {
            doctorDateRef.innerText = fechaCorta;
        } else {
            doctorDateRef.innerText = "selecciona una fecha";
        }
    }
}

// --- Funciones Paso 2: Fecha y Hora ---

function selectTime(element) {
    // Remover clase selected de otros horarios
    const times = document.querySelectorAll(".time-slot");
    times.forEach(t => t.classList.remove("selected-time"));
    
    // Agregar al actual
    element.classList.add("selected-time");
    
    appState.selectedTime = element.innerText;
    document.getElementById("selectedTimeDisplay").innerHTML = `Hora seleccionada: <strong>${appState.selectedTime}</strong>`;
    
    checkStep2Validity();
}

function checkStep2Validity() {
    const btn = document.getElementById("btn-next-2");
    if (appState.selectedDate && appState.selectedTime) {
        btn.disabled = false;
    } else {
        btn.disabled = true;
    }
}

// --- Navegación entre Pasos ---

function nextStep(stepNumber) {
    // Ocultar todos
    document.querySelectorAll(".step-section").forEach(s => s.classList.remove("active-section"));
    document.querySelectorAll(".step").forEach(s => s.classList.remove("active"));
    
    // Mostrar actual
    document.getElementById(`step-${stepNumber}`).classList.add("active-section");
    
    // Actualizar Stepper (colorear hasta el paso actual)
    for(let i=1; i<=stepNumber; i++) {
        document.getElementById(`progress-step-${i}`).classList.add("active");
    }

    // Si vamos al paso 3, llenar el resumen
    if (stepNumber === 3) {
        fillSummary();
    }
    
    // Scroll arriba
    window.scrollTo(0, 0);
}

function prevStep(stepNumber) {
    // Ocultar todos
    document.querySelectorAll(".step-section").forEach(s => s.classList.remove("active-section"));
    
    // Mostrar anterior
    document.getElementById(`step-${stepNumber}`).classList.add("active-section");
    
    // Actualizar Stepper (quitar active del paso actual que abandonamos)
    document.getElementById(`progress-step-${stepNumber + 1}`).classList.remove("active");
    
    window.scrollTo(0, 0);
}

// --- Paso 3: Resumen y Confirmación ---

function fillSummary() {
    if (appState.selectedDoctor) {
        const name = appState.selectedDoctor.name || "";
        const trimmed = name.trim();
        const initial = trimmed ? trimmed.charAt(0) : "D";
        document.getElementById("summaryAvatar").innerText = initial;
        document.getElementById("summaryDoctorName").innerText = appState.selectedDoctor.name;
        document.getElementById("summarySpecialty").innerText = appState.selectedDoctor.specialty;
    }
    document.getElementById("summaryDate").innerText = appState.selectedDate;
    document.getElementById("summaryTime").innerText = appState.selectedTime;
}

// --- Modal ---

function showSuccessModal() {
    if (!appState.selectedDoctor) {
        alert("Debes seleccionar un doctor antes de confirmar la cita.");
        return;
    }

    const dateInput = document.getElementById("datePicker");
    if (!dateInput || !dateInput.value) {
        alert("Debes seleccionar una fecha antes de confirmar la cita.");
        return;
    }

    const fechaIso = dateInput.value; // YYYY-MM-DD
    const hora = appState.selectedTime; // HH:mm
    const fechaHoraIso = `${fechaIso}T${hora}`;

    const csrfTokenMeta = document.querySelector('meta[name="_csrf"]');
    const csrfHeaderMeta = document.querySelector('meta[name="_csrf_header"]');
    const csrfToken = csrfTokenMeta ? csrfTokenMeta.getAttribute("content") : null;
    const csrfHeader = csrfHeaderMeta ? csrfHeaderMeta.getAttribute("content") : null;

    const headers = {
        "Content-Type": "application/json"
    };
    if (csrfToken && csrfHeader) {
        headers[csrfHeader] = csrfToken;
    }

    fetch("/api/citas", {
        method: "POST",
        headers: headers,
        body: JSON.stringify({
            medicoId: appState.selectedDoctor.id,
            fechaHoraIso: fechaHoraIso
        })
    }).then(response => {
        if (!response.ok) {
            return response.text().then(text => { throw new Error(text || "Error al crear la cita"); });
        }
        return response.json();
    }).then(() => {
        document.getElementById("modalDoctorName").innerText = appState.selectedDoctor.name;
        document.getElementById("modalDateTime").innerText = `${appState.selectedDate} a las ${appState.selectedTime}`;

        const modal = document.getElementById("successModal");
        modal.style.display = "flex";
    }).catch(err => {
        console.error(err);
        alert("Ocurrió un error al crear la cita. Por favor, inténtalo nuevamente.");
    });
}

function closeModal() {
    document.getElementById("successModal").style.display = "none";
}