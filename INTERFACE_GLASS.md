# Interface Glass — decisões e verificação

## Sistema visual

- Base: Material 3 em Jetpack Compose; sem biblioteca visual externa no APK.
- Superfícies com transparência, bordas sutis e gradiente especular leve.
- Paleta clara: canvas #F5F8FD, texto #172334, secundário #4B6076, destaque #185FC6.
- Paleta escura: canvas #0B1422, superfície #172334, texto #E6EDF8, secundário #AFBED0, destaque #A8CAFF.
- Verde/azulado acompanha controles de categoria; avisos usam texto e cor de erro, não só cor.
- Texto da interface a partir de 14 sp; alvos de ação Material com pelo menos 48 dp onde aplicável.
- Layout preguiçoso com rolagem nativa; filtros quebram linha em telas estreitas.

## Movimento e custo

As transições de cores e expansão duram aproximadamente 120–220 ms. A mudança dos números usa AnimatedContent. Não há animação decorativa contínua; a barra de progresso só aparece durante o carregamento de apps. A escala de animação padrão do Android é respeitada pelos componentes Compose.

O vidro é uma composição de superfícies translúcidas e gradientes estáticos, não blur/refração em tempo real. Isso evita acrescentar esse custo gráfico ao app, mas não constitui um benchmark de bateria ou FPS.

## Otimizações de dados

Metadados de pacotes são consultados fora da thread principal e compartilhados por StateFlow. Nome e flags de sistema não são relidos a cada troca de categoria. Ícones são dimensionados e carregados em IO. As gravações de seleção são atômicas; um teste executa 20 inclusões concorrentes e 10 remoções concorrentes, sem perder outras seleções.

## Revisão visual

Foram renderizados e inspecionados individualmente 12 estados, com dados fictícios: lista clara/escura, categoria sistema escura, lista com seleção alterada, configurações clara/escura, estado vazio, busca em painel, conexão Shizuku, permissão escura, licenças e largura de 320 dp com fonte em 125%.

A revisão corrigiu o contraste dos títulos no tema escuro e a composição do painel de pesquisa sobre a tela principal. Os estados finais foram revisados sem sobreposição ou corte indevido; a rolagem vertical normal permanece disponível. Nomes de pacote longos são abreviados com reticências nos cartões.

Pares de contraste base verificados: texto claro 14,87:1; secundário claro 6,10:1; destaque azul sobre branco 6,02:1; texto escuro 13,44:1; secundário escuro 8,37:1. As cores explícitas foram avaliadas nas superfícies correspondentes.

As capturas são renders nativos de Compose sob Robolectric, não capturas de um aparelho com Shizuku. Não houve medição física de desempenho, teste de GPU real nem ensaio de bateria.
