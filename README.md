# FILALIVRE

Sistema de Gerenciamento Remoto de Caixas de Supermercado

**Integrantes:** Eduardo Gomes, Maicon Goulart, Heitor Hara e José Leandro
**Disciplina:** Projeto Integrador
**Professor:** André Lobo

---

## 1. INTRODUÇÃO

O setor varejista depende diretamente da eficiência de suas operações de atendimento. Dentro de um supermercado, a frente de caixa representa uma das etapas mais importantes da experiência de compra, pois é nesse momento que o consumidor finaliza sua aquisição e realiza o pagamento.

Apesar dos avanços tecnológicos presentes no varejo, determinadas operações realizadas nos caixas ainda dependem da intervenção presencial de um gerente ou responsável. Cancelamentos, descontos, validação de promoções e outras situações podem exigir uma autorização que, em modelos tradicionais, é realizada diretamente no terminal do caixa.

Essa necessidade cria um problema operacional: sempre que uma situação exige a intervenção do gestor, o atendimento pode ser interrompido até que ele consiga chegar fisicamente ao terminal.

O FilaLivre surge como uma proposta de solução para esse problema. O projeto consiste em um sistema de gerenciamento remoto que conecta os terminais de caixa a uma central de controle utilizada pelo gestor, permitindo o acompanhamento das operações e a autorização remota de determinadas ações.

A proposta utiliza a mobilidade proporcionada pelos smartphones e outros dispositivos conectados para transformar o gestor em uma espécie de central móvel de supervisão dos caixas.

---

## 2. CONCEITO DO FILALIVRE

O FilaLivre é uma solução voltada para estabelecimentos comerciais que operam com múltiplos terminais de atendimento.

Seu conceito fundamental é simples:

> Permitir que o gestor acompanhe e autorize determinadas operações dos caixas sem necessidade de deslocamento físico até o terminal.

O sistema é composto por duas partes principais:

- uma aplicação integrada aos terminais de caixa;
- uma central de gerenciamento utilizada pelo gestor.

Quando uma operação exigir autorização, o operador poderá enviar uma solicitação ao gestor. Essa solicitação será apresentada na central de controle, permitindo que o responsável analise as informações e tome uma decisão.

A aprovação ou recusa será então comunicada ao terminal correspondente, permitindo a continuidade do atendimento.

Dessa forma, o FilaLivre transforma um processo tradicionalmente presencial em um fluxo digital, conectado e gerenciado remotamente.

---

## 3. PROBLEMA IDENTIFICADO

Um dos principais gargalos existentes na frente de caixa é a dependência da presença física do gerente para resolver determinadas ocorrências.

Imagine, por exemplo, que um operador registre incorretamente um produto. Para realizar o cancelamento, pode ser necessária uma autorização superior.

1. O operador chama o gerente.
2. O gerente interrompe sua atividade.
3. Em seguida, desloca-se até o caixa.
4. Realiza a autorização.
5. E retorna à atividade anterior.

Individualmente, esse processo pode parecer simples. Entretanto, em um estabelecimento com dezenas de caixas e grande fluxo diário de clientes, essas ocorrências se repetem diversas vezes.

A soma desses pequenos intervalos de espera pode gerar um impacto significativo na eficiência operacional.

O problema, portanto, não está na necessidade de autorização em si, que é essencial para o controle do processo.

O principal ponto crítico está na necessidade de deslocamento físico para execução dessa autorização.

---

## 4. CONSEQUÊNCIAS DO PROBLEMA

A dependência da presença física do gestor pode gerar diferentes impactos.

### 4.1 Para o consumidor

- Aumento do tempo de espera;
- Formação de filas mais extensas;
- Interrupções no atendimento;
- Experiência de compra prejudicada;
- Possível desistência da compra.

### 4.2 Para o operador

- Interrupções frequentes no atendimento;
- Tempo ocioso durante a espera;
- Maior pressão em períodos de pico;
- Redução da produtividade;
- Dificuldade em manter o fluxo contínuo.

### 4.3 Para o gestor

- Deslocamentos constantes;
- Interrupção de atividades administrativas;
- Sobrecarga operacional;
- Dificuldade de supervisão simultânea;
- Redução do foco em atividades estratégicas.

### 4.4 Para o estabelecimento

A combinação desses fatores pode resultar em menor eficiência operacional, aumento do tempo médio de atendimento e deterioração da experiência do cliente.

---

## 5. JUSTIFICATIVA

A proposta do FilaLivre baseia-se na aplicação da tecnologia para resolver um gargalo operacional específico.

Atualmente, dispositivos móveis possuem capacidade suficiente para atuar como ferramentas de comunicação e gestão em tempo real.

Dessa forma, existe uma oportunidade de utilizar essa infraestrutura já disponível para aproximar o gestor das operações sem exigir sua presença física.

Além disso, a solução aproveita um recurso amplamente difundido: o smartphone.

Em vez de depender de deslocamentos constantes ou de estações fixas de supervisão, o FilaLivre propõe uma central móvel de controle operacional.

---

## 6. SOLUÇÃO PROPOSTA

O FilaLivre funciona por meio da integração entre os caixas e a central de controle do gestor.

O sistema possui como principais funcionalidades:

- Monitoramento dos caixas;
- Identificação de solicitações;
- Envio de notificações;
- Análise de ocorrências;
- Autorização ou recusa de operações;
- Registro de decisões;
- Geração de histórico;
- Exibição de informações operacionais.

O gestor pode visualizar todos os terminais em uma única interface, identificando rapidamente quais necessitam de atenção.

---

## 7. FUNCIONAMENTO GERAL DO SISTEMA

O funcionamento pode ser dividido em cinco etapas:

1. **Identificação da ocorrência** — O operador identifica uma situação que requer autorização.
2. **Solicitação** — O operador registra a solicitação no sistema.
3. **Notificação** — A central do gestor recebe a solicitação e emite uma notificação.
4. **Decisão** — O gestor analisa as informações e decide entre aprovar ou recusar.
5. **Retorno ao caixa** — A decisão é enviada ao terminal, permitindo a continuidade do atendimento.

Fluxo geral:

```
Operador → Solicitação → Sistema → Gestor → Decisão → Sistema → Caixa → Continuidade do atendimento
```

---

## 8. CENTRAL DE CONTROLE DO GESTOR

A central de controle é um dos principais componentes do FilaLivre.

Por meio dela, o gestor pode visualizar todos os terminais em uma única interface.

Entre as informações disponíveis estão:

- Identificação do caixa;
- Status operacional;
- Valor da compra;
- Quantidade de itens;
- Tempo de atendimento;
- Operador responsável;
- Solicitações pendentes;
- Ações disponíveis.

Essa visão centralizada permite maior agilidade na tomada de decisão.

---

## 9. STATUS DOS CAIXAS

O sistema utiliza indicadores de status para facilitar a interpretação das informações.

| Status | Descrição |
|---|---|
| **Normal** | Funcionamento regular, sem necessidade de intervenção. |
| **Aguardando** | Situação em espera que pode demandar atenção. |
| **Solicitação** | Ocorrência que requer análise do gestor. |
| **Aprovação** | Ação pendente de decisão. |

O uso de indicadores visuais contribui para uma leitura mais rápida e eficiente do painel.

---

## 10. FLUXO DE AUTORIZAÇÃO

O fluxo de autorização ocorre da seguinte forma:

1. Um cliente realiza uma compra no Caixa 03.
2. Durante o atendimento, surge uma situação que exige autorização.
3. O operador registra a solicitação no sistema.
4. O gestor recebe a notificação.
5. Na tela de análise, o gestor visualiza:
   - Caixa;
   - Operador;
   - Produto;
   - Quantidade;
   - Valor;
   - Motivo da solicitação.
6. Com base nessas informações, o gestor pode **APROVAR** ou **RECUSAR** a solicitação.
7. Após a decisão, o sistema registra a ação e envia a resposta ao terminal, permitindo a continuidade do atendimento.

---

## 11. REQUISITOS FUNCIONAIS

| Código | Requisito | Descrição |
|---|---|---|
| RF01 | Cadastro de usuários | Permitir o cadastro de usuários com diferentes perfis e permissões. |
| RF02 | Autenticação | Garantir acesso apenas a usuários autorizados. |
| RF03 | Painel de caixas | Exibir os caixas ativos e suas informações operacionais. |
| RF04 | Cancelamento remoto | Permitir análise e autorização de cancelamentos. |
| RF05 | Validação de descontos e cupons | Permitir análise de promoções, descontos e cupons. |
| RF06 | Notificações | Notificar o gestor sobre novas solicitações. |
| RF07 | Gerenciamento do atendimento | Auxiliar na organização do fluxo dos caixas. |
| RF08 | Relatórios | Gerar informações sobre as operações realizadas. |
| RF09 | Histórico | Registrar todas as ações para auditoria e consulta. |

---

## 12. REQUISITOS NÃO FUNCIONAIS

| Código | Requisito | Descrição |
|---|---|---|
| RNF01 | Disponibilidade | O sistema deve estar disponível durante o horário de operação. |
| RNF02 | Desempenho | As solicitações devem ser processadas com baixa latência. |
| RNF03 | Segurança | As informações devem ser protegidas contra acessos não autorizados. |
| RNF04 | Usabilidade | A interface deve ser simples e intuitiva. |
| RNF05 | Compatibilidade | O sistema deve ser compatível com os dispositivos definidos. |
| RNF06 | Manutenibilidade | Deve permitir atualizações e correções com facilidade. |
| RNF07 | Escalabilidade | Deve suportar crescimento de usuários e caixas. |
| RNF08 | Acessibilidade | Deve considerar boas práticas de acessibilidade. |

---

## 13. SEGURANÇA

A segurança é essencial, pois o sistema envolve operações financeiras.

O FilaLivre deve operar com diferentes níveis de acesso:

- Autenticação individual;
- Controle de permissões;
- Registro de ações;
- Histórico de autorizações;
- Identificação do responsável por cada decisão;
- Controle de sessão;
- Proteção de dados transmitidos.

Cada ação é registrada com informações como usuário, horário, caixa e operação realizada.

---

## 14. PERFIS DE USUÁRIO

| Perfil | Responsabilidade |
|---|---|
| **Operador** | Executa operações no caixa e envia solicitações. |
| **Gerente** | Analisa e autoriza operações. |
| **Supervisor** | Acompanha múltiplos caixas. |
| **Administrador** | Gerencia usuários, permissões e configurações. |

---

## 15. PESQUISA DE VALIDAÇÃO

A pesquisa busca validar a relevância do problema em três públicos:

- Operadores de caixa;
- Gerentes e supervisores;
- Clientes.

O formulário aborda:

1. Perfil do participante;
2. Experiência com filas;
3. Percepção da solução;
4. Sugestões.

---

## 16. PÚBLICO-ALVO

- Supermercados;
- Atacarejos;
- Lojas de departamento;
- Farmácias;
- Lojas de conveniência;
- Grandes redes varejistas.

---

## 17. PROPOSTA DE VALOR

| Público | Benefício |
|---|---|
| **Gerente** | Maior controle com menor necessidade de deslocamento. |
| **Operador** | Redução do tempo de espera por autorização. |
| **Estabelecimento** | Maior eficiência operacional. |
| **Consumidor** | Atendimento mais rápido e fluido. |

---

## 18. DIFERENCIAL DO FILALIVRE

O diferencial está na digitalização de um processo presencial.

**Modelo tradicional:**

```
Solicitação → deslocamento → autorização → retorno
```

**FilaLivre:**

```
Solicitação → notificação → decisão remota → retorno
```

---

## 19. ARQUITETURA CONCEITUAL

- Terminal de caixa
- Backend
- Banco de dados
- Central do gestor

Fluxo:

```
Caixa → Backend → Gestor → Backend → Caixa
```

---

## 20. TECNOLOGIAS ENVOLVIDAS

- Aplicativo mobile
- Interface web
- API backend
- Banco de dados
- Sistema de autenticação
- Notificações em tempo real
- Infraestrutura em nuvem

---

## 21. MODELO DE NEGÓCIO

Modelo de assinatura mensal, variando conforme:

- Número de caixas;
- Usuários;
- Estabelecimentos;
- Recursos contratados.

---

## 22. VIABILIDADE

O sistema é tecnicamente viável, pois utiliza tecnologias já consolidadas.

O principal desafio está na integração com sistemas de PDV existentes.

---

## 23. RISCOS

- Falhas de conexão;
- Indisponibilidade do servidor;
- Acesso não autorizado;
- Erro humano;
- Dificuldades de integração;
- Resistência dos usuários.

---

## 24. PLANO DE CONTINGÊNCIA

Em caso de falha, o estabelecimento pode retornar ao processo manual de autorização.

---

## 25. MVP — PRODUTO MÍNIMO VIÁVEL

- Cadastro e login;
- Painel de caixas;
- Solicitações;
- Notificações;
- Aprovação/recusa;
- Registro de ações.

---

## 26. PRÓXIMAS ETAPAS

1. Refinamento do protótipo
2. Desenvolvimento do MVP
3. Integração com sistemas
4. Testes
5. Projeto-piloto
6. Análise de resultados
7. Melhorias
8. Expansão

---

## 27. INDICADORES DE DESEMPENHO

- Tempo de autorização;
- Tempo de espera;
- Número de solicitações;
- Taxa de aprovação;
- Intervenções presenciais;
- Produtividade por caixa.

---

## 28. EXPANSÕES FUTURAS

- Integração com ERP;
- Gestão multi-loja;
- Dashboards avançados;
- Inteligência artificial;
- Previsão de filas;
- Detecção de anomalias.

---

## 29. IMPACTO ESPERADO

- Maior eficiência operacional;
- Melhor gestão;
- Redução de custos indiretos;
- Melhoria da experiência do cliente;
- Digitalização do processo de autorização.

---

## 30. CONCLUSÃO

O FilaLivre propõe uma solução para um problema recorrente no varejo: a necessidade de deslocamento físico do gestor para autorizações em caixas.

Embora cada ocorrência individual seja simples, sua repetição impacta diretamente a eficiência operacional.

A proposta consiste em uma plataforma de gerenciamento remoto que conecta caixas a uma central de controle, permitindo decisões rápidas e centralizadas.

O sistema não substitui o gestor, mas amplia sua capacidade de atuação, tornando o processo mais ágil e eficiente.

Além disso, o FilaLivre possui potencial de evolução para se tornar uma plataforma completa de gestão operacional no varejo.

---

## 31. SÍNTESE DO PROJETO

| Item | Descrição |
|---|---|
| **Problema** | Necessidade de deslocamento físico do gerente para autorizações. |
| **Impacto** | Atrasos, filas e sobrecarga operacional. |
| **Solução** | Central de gerenciamento remoto. |
| **Fluxo** | Operador solicita → gestor decide → sistema retorna ao caixa. |
| **Benefício** | Maior agilidade, controle e eficiência operacional. |
| **Visão futura** | Evolução para uma plataforma inteligente de gestão do varejo. |
