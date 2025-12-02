// --- CONFIGURACIÓN STRIPE (AÑADIDO) ---
const stripe = Stripe("pk_test_51SZwQJRXZrkYvZdtIg2VF2GBGXuEtQyDXHhr1komOXElrprsce1A7BwEg7bziwEeR0vZGCZGfJgUOpUMta6PCeRk00GVD1OPS5");
let elements;

// --- Estado de la Aplicación ---
let appState = {
    selectedDoctor: null,
    selectedDate: null, 
    selectedTime: "14:00",
    selectedPrice: 0 // <--- NUEVO: Para guardar el precio
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
        cmp: cardElement.dataset.cmp,
        // LEEMOS EL COSTO AÑADIDO EN EL HTML
        costo: parseFloat(cardElement.dataset.costo) 
    };
    
    // Guardamos precio en estado y actualizamos la vista si existen los elementos
    appState.selectedPrice = appState.selectedDoctor.costo;
    if(document.getElementById("displayPrecio")) {
        document.getElementById("displayPrecio").innerText = appState.selectedPrice.toFixed(2);
    }
    if(document.getElementById("btnPrecioCita")) {
        document.getElementById("btnPrecioCita").innerText = appState.selectedPrice.toFixed(2);
    }

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

// --- Funciones de Fechas (ORIGINALES) ---
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

    dateInput.min = isoHoy;
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
            appState.selectedDate = formatearFechaLarga(seleccionada);
            actualizarSeleccionFechaUI(appState.selectedDate, formatearFechaCorta(seleccionada));
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

// --- Funciones Paso 2: Fecha y Hora (ORIGINALES) ---

function selectTime(element) {
    const times = document.querySelectorAll(".time-slot");
    times.forEach(t => t.classList.remove("selected-time"));
    
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

// --- Navegación entre Pasos (MODIFICADO LIGERAMENTE PARA INICIAR PAGO) ---

function nextStep(stepNumber) {
    document.querySelectorAll(".step-section").forEach(s => s.classList.remove("active-section"));
    document.querySelectorAll(".step").forEach(s => s.classList.remove("active"));
    
    document.getElementById(`step-${stepNumber}`).classList.add("active-section");
    
    for(let i=1; i<=stepNumber; i++) {
        document.getElementById(`progress-step-${i}`).classList.add("active");
    }

    // Si vamos al paso 3, llenar el resumen e INICIAR STRIPE
    if (stepNumber === 3) {
        fillSummary();
        iniciarStripe(); // <--- Llamada a la función de pago
    }
    
    window.scrollTo(0, 0);
}

function prevStep(stepNumber) {
    document.querySelectorAll(".step-section").forEach(s => s.classList.remove("active-section"));
    document.getElementById(`step-${stepNumber}`).classList.add("active-section");
    document.getElementById(`progress-step-${stepNumber + 1}`).classList.remove("active");
    window.scrollTo(0, 0);
}

// --- Paso 3: Resumen ---

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

// --- MODAL Y PAGOS (INTEGRADO NUEVO CON LÓGICA ORIGINAL) ---

// Esta función ahora la llama el proceso de pago cuando es exitoso
function guardarCitaEnBackend(idTransaccion, metodoPago) {
    // Validaciones originales
    if (!appState.selectedDoctor) {
        alert("Debes seleccionar un doctor antes de confirmar la cita.");
        return;
    }
    const dateInput = document.getElementById("datePicker");
    if (!dateInput || !dateInput.value) {
        alert("Debes seleccionar una fecha antes de confirmar la cita.");
        return;
    }

    const fechaIso = dateInput.value; 
    const hora = appState.selectedTime; 
    const fechaHoraIso = `${fechaIso}T${hora}`;

    // CSRF (Importante para tu seguridad)
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

    // Llamada al endpoint modificado que acepta el ID de transacción
    fetch("/api/citas", {
        method: "POST",
        headers: headers,
        body: JSON.stringify({
            medicoId: appState.selectedDoctor.id,
            fechaHoraIso: fechaHoraIso,
            idTransaccion: idTransaccion, // Nuevo campo
            metodoPago: metodoPago        // Nuevo campo
        })
    }).then(response => {
        if (!response.ok) {
            return response.text().then(text => { throw new Error(text || "Error al crear la cita"); });
        }
        return response.json();
    }).then(() => {
        // Llenar el modal de éxito (Lógica visual original)
        document.getElementById("modalDoctorName").innerText = appState.selectedDoctor.name;
        document.getElementById("modalDateTime").innerText = `${appState.selectedDate} a las ${appState.selectedTime}`;

        const modal = document.getElementById("successModal");
        modal.style.display = "flex";
    }).catch(err => {
        console.error(err);
        alert("El pago se realizó correctamente (ID: "+idTransaccion+"), pero hubo un error guardando la cita. Por favor contacta soporte.");
    });
}

function closeModal() {
    document.getElementById("successModal").style.display = "none";
}

// --- LÓGICA DE STRIPE (NUEVO) ---

async function iniciarStripe() {
    try {
        const response = await fetch("/api/pagos/crear-intento", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ medicoId: appState.selectedDoctor.id })
        });
        
        if (!response.ok) throw new Error("Error backend");

        const { clientSecret } = await response.json();

        const appearance = { theme: 'stripe', labels: 'floating' };
        elements = stripe.elements({ appearance, clientSecret });

        const paymentElement = elements.create("payment");
        paymentElement.mount("#payment-element");
    } catch (error) {
        console.error("Error iniciando Stripe:", error);
        document.getElementById("payment-message").innerText = "Error cargando sistema de pagos.";
        document.getElementById("payment-message").style.display = "block";
    }
}

async function procesarPagoYConfirmar() {
    const btn = document.getElementById("btn-pay-confirm");
    btn.disabled = true;
    btn.innerText = "Procesando pago...";

    const { error, paymentIntent } = await stripe.confirmPayment({
        elements,
        confirmParams: {
            return_url: window.location.href, 
        },
        redirect: "if_required"
    });

    if (error) {
        const msg = document.getElementById("payment-message");
        msg.innerText = error.message;
        msg.style.display = "block";
        btn.disabled = false;
        btn.innerText = "Reintentar Pago";
    } else if (paymentIntent && paymentIntent.status === "succeeded") {
        console.log("Pago Éxitoso:", paymentIntent.id);
        // Aquí llamamos a tu lógica original de guardado, pasándole los datos del pago
        guardarCitaEnBackend(paymentIntent.id, "Tarjeta Stripe");
    }
}