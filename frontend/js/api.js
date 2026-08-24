const API = "/api";

async function apiFetch(path, options = {}) {
  const config = {
    method: options.method || "GET",
    credentials: "same-origin",
    headers: {}
  };
  if (options.body !== undefined) {
    config.headers["Content-Type"] = "application/json";
    config.body = JSON.stringify(options.body);
  }
  const resposta = await fetch(API + path, config);

  if (resposta.status === 401 && !path.startsWith("/auth/")) {
    localStorage.removeItem("filalivre_usuario");
    window.location.href = "index.html";
    throw new Error("Sessão expirada. Faça login novamente.");
  }
  if (!resposta.ok) {
    let mensagem = "Erro " + resposta.status;
    try {
      const dados = await resposta.json();
      if (dados.erro) mensagem = dados.erro;
    } catch (e) { /* corpo sem JSON */ }
    throw new Error(mensagem);
  }
  if (resposta.status === 204) return null;
  return resposta.json();
}

function usuarioLogado() {
  try {
    return JSON.parse(localStorage.getItem("filalivre_usuario"));
  } catch (e) {
    return null;
  }
}

function salvarUsuario(usuario) {
  localStorage.setItem("filalivre_usuario", JSON.stringify(usuario));
}

function destinoPorPerfil(perfil) {
  return perfil === "OPERADOR" ? "caixa.html" : "painel.html";
}

function sair() {
  fetch(API + "/auth/logout", { method: "POST", credentials: "same-origin" }).finally(() => {
    localStorage.removeItem("filalivre_usuario");
    window.location.href = "index.html";
  });
}

function formatarMoeda(valor) {
  return Number(valor).toLocaleString("pt-BR", { style: "currency", currency: "BRL" });
}

function formatarDataHora(iso) {
  if (!iso) return "-";
  return new Date(iso).toLocaleString("pt-BR", {
    day: "2-digit", month: "2-digit",
    hour: "2-digit", minute: "2-digit"
  });
}

function tempoDecorrido(inicioIso) {
  if (!inicioIso) return "-";
  const segundos = Math.max(0, Math.floor((Date.now() - new Date(inicioIso)) / 1000));
  const m = String(Math.floor(segundos / 60)).padStart(2, "0");
  const s = String(segundos % 60).padStart(2, "0");
  return `${m}:${s}`;
}

function mostrarToast(mensagem, tipo = "azul") {
  let container = document.getElementById("toasts");
  if (!container) {
    container = document.createElement("div");
    container.id = "toasts";
    document.body.appendChild(container);
  }
  const toast = document.createElement("div");
  toast.className = `toast toast-${tipo}`;
  toast.textContent = mensagem;
  container.appendChild(toast);
  setTimeout(() => toast.remove(), 6000);
}
