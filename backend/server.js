const path = require('path');
const fs = require('fs');
const express = require('express');
const Database = require('better-sqlite3');

const app = express();
const PORT = process.env.PORT || 3000;
const root = path.join(__dirname, '..');
const databaseDirectory = path.join(root, 'database');
const database = new Database(path.join(databaseDirectory, 'filalivre.sqlite'));

database.pragma('journal_mode = WAL');
database.exec(fs.readFileSync(path.join(databaseDirectory, 'schema.sql'), 'utf8'));

function seedDatabase() {
  if (database.prepare('SELECT COUNT(*) AS count FROM cash_registers').get().count > 0) return;
  const registers = [
    ['01', 'ok', 84260, 14, null], ['02', 'ok', 120418, 27, null],
    ['03', 'request', 38690, 9, 'Cancelamento pendente'], ['04', 'ok', 65422, 18, null],
    ['05', 'ok', 9240, 4, null], ['06', 'waiting', 189000, 35, 'Cupom para validar'],
    ['07', 'ok', 47812, 11, null], ['08', 'ok', 21570, 7, null], ['09', 'ok', 73044, 22, null]
  ];
  const insertRegister = database.prepare('INSERT INTO cash_registers (code, status, total_cents, item_count, alert) VALUES (?, ?, ?, ?, ?)');
  const insertRequest = database.prepare('INSERT INTO requests (register_id, operator_name, operator_initials, type, product, value_cents, reason) VALUES (?, ?, ?, ?, ?, ?, ?)');
  const insertActivity = database.prepare('INSERT INTO activity (message, is_danger) VALUES (?, ?)');
  const seed = database.transaction(() => {
    registers.forEach(register => insertRegister.run(...register));
    insertRequest.run(3, 'Mariana Costa', 'MC', 'Cancelamento de item', 'Café Torrado 500g', 2490, 'Produto registrado incorretamente. O cliente solicitou a retirada do item antes do pagamento.');
    insertRequest.run(6, 'Rafael Souza', 'RS', 'Validação de cupom', 'Cupom de desconto #4921', 18900, 'Cupom apresentado pelo cliente não foi reconhecido automaticamente pelo sistema.');
    insertRequest.run(2, 'Júlia Martins', 'JM', 'Desconto especial', 'Fralda Confort M - pacote', 4250, 'Desconto de campanha solicitado para produto em oferta de encarte.');
    insertActivity.run('Você aprovou um desconto no Caixa 04', 0);
    insertActivity.run('Mariana enviou uma solicitação no Caixa 03', 1);
    insertActivity.run('Cupom validado no Caixa 01', 0);
  });
  seed();
}
seedDatabase();

app.use(express.json());
app.use(express.static(path.join(root, 'frontend')));

const centsToMoney = cents => (cents / 100).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
const registerView = row => ({ ...row, total: centsToMoney(row.total_cents), statusLabel: row.status === 'ok' ? 'Em operação' : row.status === 'request' ? 'Solicitação' : 'Aguardando' });
const requestView = row => ({ ...row, value: centsToMoney(row.value_cents), register: `Caixa ${row.register_code}` });

app.get('/api/dashboard', (_request, response) => {
  const registers = database.prepare('SELECT * FROM cash_registers ORDER BY code').all().map(registerView);
  const requests = database.prepare(`SELECT requests.*, cash_registers.code AS register_code FROM requests JOIN cash_registers ON cash_registers.id = requests.register_id WHERE requests.status = 'pending' ORDER BY requests.created_at`).all().map(requestView);
  const activity = database.prepare('SELECT * FROM activity ORDER BY id DESC LIMIT 4').all();
  const approved = database.prepare("SELECT COUNT(*) AS count FROM requests WHERE status = 'approved'").get().count;
  response.json({ registers, requests, activity, metrics: { active: registers.filter(register => register.status === 'ok').length, pending: requests.length, approved: approved + 27 } });
});

app.patch('/api/requests/:id', (request, response) => {
  const approved = request.body.status === 'approved';
  if (!approved && request.body.status !== 'denied') return response.status(400).json({ error: 'Status inválido.' });
  const item = database.prepare(`SELECT requests.*, cash_registers.code AS register_code FROM requests JOIN cash_registers ON cash_registers.id = requests.register_id WHERE requests.id = ? AND requests.status = 'pending'`).get(request.params.id);
  if (!item) return response.status(404).json({ error: 'Solicitação não encontrada ou já decidida.' });
  const update = database.transaction(() => {
    database.prepare('UPDATE requests SET status = ?, decided_at = CURRENT_TIMESTAMP WHERE id = ?').run(request.body.status, item.id);
    database.prepare('UPDATE cash_registers SET status = ?, alert = NULL, last_update = CURRENT_TIMESTAMP WHERE id = ?').run('ok', item.register_id);
    database.prepare('INSERT INTO activity (message, is_danger) VALUES (?, ?)').run(`Você ${approved ? 'aprovou' : 'recusou'} ${item.type.toLowerCase()} no Caixa ${item.register_code}`, approved ? 0 : 1);
  });
  update();
  response.json({ ok: true });
});

app.listen(PORT, () => console.log(`FilaLivre API disponível em http://localhost:${PORT}`));
