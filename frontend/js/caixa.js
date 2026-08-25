const usuario = usuarioLogado();
if (!usuario) {
  window.location.href = "index.html";
} else if (usuario.perfil !== "OPERADOR") {
  window.location.href = "painel.html";
}

document.getElementById("nome-usuario").textContent = usuario.nome;

const selectCaixa = document.getElementById("select-caixa");
const chipStatus = document.getElementById("chip-status");
const totalCompra = document.getElementById("total-compra");
const qtdItensEl = document.getElementById("qtd-itens");
const tempoAtendimento = document.getElementById("tempo-atendimento");
const btnIniciar = document.getElementById("btn-iniciar");
const btnEspera = document.getElementById("btn-espera");
const btnFinalizar = document.getElementById("btn-finalizar");
const btnSolicitar = document.getElementById("btn-solicitar");
const aguardandoGestor = document.getElementById("aguardando-gestor");
const bannerDecisao = document.getElementById("banner-decisao");

let caixas = [];
let caixaAtual = null;
let carrinho = [];
let solicitacaoPendenteId = null;
let ultimaSolicitacaoDecidida = null;

btnIniciar.addEventListener("click", async () => {
  if (!caixaSelecionada()) return;
  try {
    await apiFetch(`/caixas/${caixaAtual.id}/iniciar`, { method: "POST" });
    carrinho = [];
    renderizarCarrinho();
    mostrarToast(`Você assumiu o Caixa ${String(caixaAtual.numero).padStart(2, "0")}`, "verde");
    await atualizarTudo();
  } catch (e) {
    mostrarToast(e.message, "vermelho");
  }
});

btnEspera.addEventListener("click", async () => {
  if (!caixaSelecionada()) return;
  try {
    const resposta = await apiFetch(`/caixas/${caixaAtual.id}/espera`, { method: "POST" });
    await carregarCaixas();
    renderizarStatus(resposta);
  } catch (e) {
    mostrarToast(e.message, "vermelho");
  }
});

btnFinalizar.addEventListener("click", async () => {
  if (!caixaSelecionada()) return;
  try {
    await apiFetch(`/caixas/${caixaAtual.id}/finalizar`, { method: "POST" });
    carrinho = [];
    renderizarCarrinho();
    mostrarToast("Venda finalizada com sucesso", "verde");
    await atualizarTudo();
  } catch (e) {
    mostrarToast(e.message, "vermelho");
  }
});

btnSolicitar.addEventListener("click", () => {
  if (!caixaSelecionada()) return;
  document.getElementById("erro-solicitacao").style.display = "none";
  document.getElementById("dialog-solicitacao").showModal();
});

document.getElementById("form-solicitacao").addEventListener("submit", async (e) => {
  e.preventDefault();
  try {
    await apiFetch("/solicitacoes", {
      method: "POST",
      body: {
        caixaId: caixaAtual.id,
        tipo: document.getElementById("sol-tipo").value,
        produto: document.getElementById("sol-produto").value.trim(),
        quantidade: Number(document.getElementById("sol-qtd").value),
        valor: Number(document.getElementById("sol-valor").value),
        motivo: document.getElementById("sol-motivo").value.trim()
      }
    });
    mostrarToast("Solicitação enviada ao gestor", "azul");
    document.getElementById("form-solicitacao").reset();
    await atualizarTudo();
  } catch (err) {
    const erroSol = document.getElementById("erro-solicitacao");
    erroSol.textContent = err.message;
    erroSol.style.display = "block";
    e.preventDefault();
  }
});

document.getElementById("form-item").addEventListener("submit", async (e) => {
  e.preventDefault();
  if (!atendimentoAtivo()) {
    mostrarToast("Inicie o atendimento antes de registrar produtos", "amarelo");
    return;
  }
  const nome = document.getElementById("item-nome").value.trim();
  const preco = Number(document.getElementById("item-preco").value);
  const qtd = Number(document.getElementById("item-qtd").value);
  carrinho.push({ nome, preco, qtd });
  renderizarCarrinho();
  e.target.reset();
  document.getElementById("item-qtd").value = 1;
  await sincronizarTotais();
});

async function sincronizarTotais() {
  if (!atendimentoAtivo()) return;
  try {
    await apiFetch(`/caixas/${caixaAtual.id}/totais`, {
      method: "PUT",
      body: { valorCompra: totalCarrinho(), qtdItens: itensCarrinho() }
    });
  } catch (e) { /* próxima sincronização tenta novamente */ }
}

function totalCarrinho() {
  return Math.round(carrinho.reduce((soma, item) => soma + item.preco * item.qtd, 0) * 100) / 100;
}

function itensCarrinho() {
  return carrinho.reduce((soma, item) => soma + item.qtd, 0);
}

function renderizarCarrinho() {
  const container = document.getElementById("lista-itens");
  if (carrinho.length === 0) {
    container.innerHTML = `<div class="vazio">Nenhum produto registrado ainda.</div>`;
    return;
  }
  container.innerHTML = "";
  carrinho.forEach((item, indice) => {
    const linha = document.createElement("div");
    linha.className = "item-linha";
    linha.innerHTML = `
      <span class="nome">${escapar(item.nome)}</span>
      <span>${item.qtd} × ${formatarMoeda(item.preco)}</span>
      <strong>${formatarMoeda(item.preco * item.qtd)}</strong>`;
    const botaoRemover = document.createElement("button");
    botaoRemover.className = "btn btn-vermelho btn-pequeno";
    botaoRemover.textContent = "✕";
    botaoRemover.title = "Cancelar item";
    botaoRemover.addEventListener("click", async () => {
      carrinho.splice(indice, 1);
      renderizarCarrinho();
      await sincronizarTotais();
    });
    linha.appendChild(botaoRemover);
    container.appendChild(linha);
  });
}

selectCaixa.addEventListener("change", async () => {
  localStorage.setItem("filalivre_caixa_id", selectCaixa.value);
  carrinho = [];
  renderizarCarrinho();
  await atualizarTudo();
});

function caixaSelecionada() {
  if (!caixaAtual) {
    mostrarToast("Selecione um caixa primeiro", "amarelo");
    return false;
  }
  return true;
}

function atendimentoAtivo() {
  return Boolean(caixaAtual && caixaAtual.operadorNome === usuario.nome && caixaAtual.inicioAtendimento);
}

async function carregarCaixas() {
  caixas = await apiFetch("/caixas");
  const idSalvo = localStorage.getItem("filalivre_caixa_id");

  selectCaixa.innerHTML = "";
  for (const c of caixas) {
    const opcao = document.createElement("option");
    opcao.value = c.id;
    const ocupado = c.operadorNome && c.operadorNome !== usuario.nome;
    opcao.textContent = `Caixa ${String(c.numero).padStart(2, "0")}` +
      (c.operadorNome ? (ocupado ? ` — ${c.operadorNome}` : " — você") : " — livre");
    selectCaixa.appendChild(opcao);
  }

  let escolhido = caixas.find(c => String(c.id) === idSalvo);
  if (!escolhido) {
    escolhido = caixas.find(c => c.operadorNome === usuario.nome) || caixas[0];
  }
  if (escolhido) {
    selectCaixa.value = String(escolhido.id);
    caixaAtual = escolhido;
  }

  renderizarStatus(caixaAtual);

  const minhasPendentes = await apiFetch("/solicitacoes/minhas");
  const pendenteDoCaixa = minhasPendentes.find(s =>
    s.status === "PENDENTE" && caixaAtual && s.caixaId === caixaAtual.id);

  if (pendenteDoCaixa) {
    solicitacaoPendenteId = pendenteDoCaixa.id;
    aguardandoGestor.style.display = "flex";
  } else {
    aguardandoGestor.style.display = "none";
    if (solicitacaoPendenteId) {
      const decidida = minhasPendentes.find(s => s.id === solicitacaoPendenteId)
        || minhasPendentes[0];
      if (decidida) {
        exibirDecisao(decidida);
      }
      solicitacaoPendenteId = null;
    }
  }
}

function renderizarStatus(caixa) {
  if (!caixa) return;

  chipStatus.className = `status-chip status-${caixa.status}`;
  const rotulos = {
    NORMAL: caixa.operadorNome ? "EM ATENDIMENTO" : "LIVRE",
    AGUARDANDO: "AGUARDANDO",
    SOLICITACAO: "SOLICITAÇÃO EM ANÁLISE",
    APROVACAO: "APROVAÇÃO PENDENTE"
  };
  chipStatus.textContent = rotulos[caixa.status] || caixa.status;

  totalCompra.textContent = formatarMoeda(caixa.valorCompra);
  qtdItensEl.textContent = caixa.qtdItens;
  tempoAtendimento.textContent = tempoDecorrido(caixa.inicioAtendimento);

  const meuAtendimento = caixa.operadorNome === usuario.nome;
  const bloqueadoPorSolicitacao = caixa.status === "SOLICITACAO" || caixa.status === "APROVACAO";

  btnEspera.disabled = !meuAtendimento || bloqueadoPorSolicitacao;
  btnFinalizar.disabled = !meuAtendimento || bloqueadoPorSolicitacao;
  btnSolicitar.disabled = !meuAtendimento || bloqueadoPorSolicitacao;
  btnIniciar.disabled = meuAtendimento || bloqueadoPorSolicitacao;
}

function exibirDecisao(solicitacao) {
  const aprovada = solicitacao.status === "APROVADA";
  bannerDecisao.className = `banner-decisao ${aprovada ? "banner-aprovada" : "banner-recusada"}`;
  bannerDecisao.style.display = "flex";
  bannerDecisao.innerHTML = `
    <span style="font-size:1.6rem">${aprovada ? "✔" : "✖"}</span>
    <div>
      Solicitação de "${escapar(solicitacao.produto)}" foi
      ${aprovada ? "<u>APROVADA</u>" : "<u>RECUSADA</u>"} pelo gestor
      ${solicitacao.decididoPorNome ? `(${escapar(solicitacao.decididoPorNome)})` : ""}.
      ${aprovada ? "Você já pode concluir a operação." : "A operação não será autorizada."}
      <div><button class="btn btn-cinza btn-pequeno" id="fechar-banner">Fechar</button></div>
    </div>`;
  document.getElementById("fechar-banner").addEventListener("click", () => {
    bannerDecisao.style.display = "none";
  });
  setTimeout(() => { bannerDecisao.style.display = "none"; }, 15000);
  window.scrollTo({ top: 0, behavior: "smooth" });
}

async function atualizarTudo() {
  await carregarCaixas();
}

(async function iniciar() {
  try {
    await carregarCaixas();
  } catch (e) {
    mostrarToast(e.message, "vermelho");
  }
})();

setInterval(carregarCaixas, 3000);
