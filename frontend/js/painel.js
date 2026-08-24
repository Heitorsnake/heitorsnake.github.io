const usuario = usuarioLogado();
if (!usuario) {
  window.location.href = "index.html";
} else if (usuario.perfil === "OPERADOR") {
  window.location.href = "caixa.html";
}

const podeDecidir = ["SUPERVISOR", "GERENTE", "ADMINISTRADOR"].includes(usuario?.perfil);
const rotulosAcao = {
  LOGIN: "Login",
  INICIO_ATENDIMENTO: "Início de atendimento",
  FIM_ATENDIMENTO: "Fim de atendimento",
  CAIXA_EM_ESPERA: "Caixa em espera",
  ESPERA_REMOVIDA: "Espera removida",
  SOLICITACAO_CRIADA: "Solicitação enviada",
  ANALISE_INICIADA: "Análise iniciada",
  SOLICITACAO_APROVADA: "Solicitação APROVADA",
  SOLICITACAO_RECUSADA: "Solicitação RECUSADA",
  USUARIO_CRIADO: "Usuário criado",
  USUARIO_ATIVADO: "Usuário ativado",
  USUARIO_DESATIVADO: "Usuário desativado"
};

let idsPendentesAnteriores = new Set();

document.getElementById("nome-usuario").textContent = usuario.nome;
document.getElementById("perfil-usuario").textContent = usuario.perfil;

if (!podeDecidir) {
  document.getElementById("btn-novo-caixa").style.display = "none";
} else {
  const btnNovoCaixa = document.getElementById("btn-novo-caixa");
  const dialogCaixa = document.getElementById("dialog-caixa");
  btnNovoCaixa.addEventListener("click", () => {
    document.getElementById("erro-caixa").style.display = "none";
    dialogCaixa.showModal();
  });
  document.getElementById("form-novo-caixa").addEventListener("submit", async (e) => {
    e.preventDefault();
    try {
      await apiFetch("/caixas", {
        method: "POST",
        body: { numero: Number(document.getElementById("numero-caixa").value) }
      });
      dialogCaixa.close();
      mostrarToast("Caixa cadastrado com sucesso", "verde");
      carregarCaixas();
    } catch (err) {
      const erroCaixa = document.getElementById("erro-caixa");
      erroCaixa.textContent = err.message;
      erroCaixa.style.display = "block";
    }
  });
}

async function carregarResumo() {
  try {
    const resumo = await apiFetch("/relatorios/resumo");
    document.getElementById("res-pendentes").textContent = resumo.pendentes;
    document.getElementById("res-aprovadas").textContent = resumo.aprovadas;
    document.getElementById("res-recusadas").textContent = resumo.recusadas;
    document.getElementById("res-tempo").textContent =
      resumo.tempoMedioDecisaoMinutos > 0 ? `${resumo.tempoMedioDecisaoMinutos} min` : "–";
    const atencao = (resumo.caixasPorStatus.SOLICITACAO || 0) + (resumo.caixasPorStatus.APROVACAO || 0)
      + (resumo.caixasPorStatus.AGUARDANDO || 0);
    document.getElementById("res-atencao").textContent = atencao;
  } catch (e) { /* silencioso */ }
}

async function carregarPendentes() {
  try {
    const pendentes = await apiFetch("/solicitacoes/pendentes");
    const container = document.getElementById("lista-pendentes");

    const idsNovos = new Set(pendentes.map(s => s.id));
    for (const s of pendentes) {
      if (!idsPendentesAnteriores.has(s.id) && idsPendentesAnteriores.size >= 0 && jaCarregouPrimeiraVez) {
        mostrarToast(`Nova solicitação no Caixa ${String(s.caixaNumero).padStart(2, "0")}: ${s.tipo.toLowerCase()} de "${s.produto}"`, "amarelo");
        piscarTitulo(true);
      }
    }
    idsPendentesAnteriores = idsNovos;
    jaCarregouPrimeiraVez = true;
    if (pendentes.length === 0) piscarTitulo(false);

    if (pendentes.length === 0) {
      container.innerHTML = `<div class="aviso-vazio">Nenhuma solicitação pendente. Tudo sob controle.</div>`;
      return;
    }

    container.innerHTML = "";
    for (const s of pendentes) {
      const cartao = document.createElement("div");
      cartao.className = "solicitacao-card";
      cartao.innerHTML = `
        <div class="sol-principal">
          <span class="sol-caixa">Caixa ${String(s.caixaNumero).padStart(2, "0")}</span>
          <span class="sol-tipo">${rotuloTipo(s.tipo)}</span>
          <div class="sol-produto"><strong>${escapar(s.produto)}</strong> — ${formatarMoeda(s.valor)} (${s.quantidade} un)</div>
          <div class="sol-meta">
            Operador: <strong>${escapar(s.operadorNome)}</strong>
            · Solicitado às ${formatarDataHora(s.criadoEm)}
            ${s.motivo ? `· Motivo: ${escapar(s.motivo)}` : ""}
          </div>
        </div>
        <div class="sol-acoes">
          <button class="btn btn-azul btn-pequeno" data-analisar="${s.id}">Analisar</button>
          <button class="btn btn-verde btn-pequeno" data-aprovar="${s.id}">Aprovar</button>
          <button class="btn btn-vermelho btn-pequeno" data-recusar="${s.id}">Recusar</button>
        </div>`;
      container.appendChild(cartao);
    }

    container.querySelectorAll("[data-aprovar]").forEach(b =>
      b.addEventListener("click", () => decidir(b.dataset.aprovar, true)));
    container.querySelectorAll("[data-recusar]").forEach(b =>
      b.addEventListener("click", () => decidir(b.dataset.recusar, false)));
    container.querySelectorAll("[data-analisar]").forEach(b =>
      b.addEventListener("click", () => analisar(b.dataset.analisar)));
  } catch (e) { /* silencioso */ }
}

let jaCarregouPrimeiraVez = false;

function rotuloTipo(tipo) {
  return { CANCELAMENTO: "Cancelamento", DESCONTO: "Desconto", CUPOM: "Cupom" }[tipo] || tipo;
}

function escapar(texto) {
  const div = document.createElement("div");
  div.textContent = texto ?? "";
  return div.innerHTML;
}

async function analisar(id) {
  try {
    await apiFetch(`/solicitacoes/${id}/analisar`, { method: "POST" });
    mostrarToast("Análise registrada. O caixa está em aprovação.", "azul");
    Promise.all([carregarPendentes(), carregarCaixas()]);
  } catch (e) {
    mostrarToast(e.message, "vermelho");
  }
}

async function decidir(id, aprovar) {
  try {
    await apiFetch(`/solicitacoes/${id}/decidir`, { method: "POST", body: { aprovar } });
    mostrarToast(aprovar ? "Solicitação aprovada e comunicada ao caixa." : "Solicitação recusada.",
      aprovar ? "verde" : "vermelho");
    Promise.all([carregarPendentes(), carregarCaixas(), carregarHistorico(), carregarResumo()]);
  } catch (e) {
    mostrarToast(e.message, "vermelho");
  }
}

async function carregarCaixas() {
  try {
    const caixas = await apiFetch("/caixas");
    const grid = document.getElementById("grid-caixas");
    grid.innerHTML = "";
    for (const c of caixas) {
      const cartao = document.createElement("div");
      cartao.className = `caixa-card borda-${c.status}`;
      cartao.innerHTML = `
        <div class="caixa-numero">Caixa ${String(c.numero).padStart(2, "0")}</div>
        <span class="status-chip status-${c.status}">${c.solicitacaoPendente ? c.status + " • PENDENTE" : c.status}</span>
        <div class="caixa-detalhe"><span>Valor da compra</span><strong>${formatarMoeda(c.valorCompra)}</strong></div>
        <div class="caixa-detalhe"><span>Itens</span><strong>${c.qtdItens}</strong></div>
        <div class="caixa-detalhe"><span>Operador</span><strong>${c.operadorNome ? escapar(c.operadorNome) : "—"}</strong></div>
        <div class="caixa-detalhe"><span>Tempo de atendimento</span><strong>${tempoDecorrido(c.inicioAtendimento)}</strong></div>`;
      grid.appendChild(cartao);
    }
  } catch (e) { /* silencioso */ }
}

async function carregarHistorico() {
  try {
    const registros = await apiFetch("/historico");
    const corpo = document.getElementById("corpo-historico");
    corpo.innerHTML = registros.slice(0, 30).map(r => `
      <tr>
        <td>${formatarDataHora(r.momento)}</td>
        <td>${escapar(r.usuarioNome)}</td>
        <td>${r.caixaNumero ? "Caixa " + String(r.caixaNumero).padStart(2, "0") : "—"}</td>
        <td>${rotulosAcao[r.acao] || r.acao}</td>
        <td>${escapar(r.detalhes)}</td>
      </tr>`).join("");
  } catch (e) { /* silencioso */ }
}

let tituloOriginal = document.title;
let intervaloPiscar = null;

function piscarTitulo(ativar) {
  if (ativar && !intervaloPiscar) {
    let alternar = false;
    intervaloPiscar = setInterval(() => {
      document.title = alternar ? "(1) Solicitação! — FILALIVRE" : tituloOriginal;
      alternar = !alternar;
    }, 1000);
  } else if (!ativar && intervaloPiscar) {
    clearInterval(intervaloPiscar);
    intervaloPiscar = null;
    document.title = tituloOriginal;
  }
}

carregarResumo();
carregarPendentes().then(carregarCaixas).then(carregarHistorico);

setInterval(() => { carregarPendentes(); carregarCaixas(); }, 3000);
setInterval(() => { carregarResumo(); carregarHistorico(); }, 8000);
