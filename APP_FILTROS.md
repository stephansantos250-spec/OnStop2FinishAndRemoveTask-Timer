# Seleção por tipo de aplicativo — Timer 2

Na parte superior da tela **Selecionar apps**, escolha:

- **Todos:** mostra apps instalados pelo usuário e apps do sistema.
- **Instalados:** mostra apenas apps que não são classificados como apps do sistema pelo Android.
- **Sistema:** mostra apps pré-instalados, inclusive atualizações de apps do sistema.

Depois, use o interruptor ao lado de cada app para incluí-lo ou removê-lo do temporizador. Trocar de categoria **não marca, desmarca nem apaga** a lista de apps selecionados. Os filtros por nome, pacote e “Somente apps selecionados”, disponíveis na lupa, continuam funcionando em conjunto com a categoria.

O tipo do app também é mostrado abaixo do nome do pacote. Os botões de categoria podem quebrar de linha em telas pequenas ou com fontes maiores.

O temporizador de 1 a 5 minutos continua em **Configurações → Temporizador de fechamento**, sem alterar as regras da versão anterior.

## Instalação e atualização

Esta versão usa o mesmo identificador e a mesma chave de assinatura local do primeiro APK Timer fornecido nesta conversa:

- Pacote: `io.github.takusan23.onstop2finishandremovetask.timer`
- Versão: `2.0.6-timer.2`
- Código da versão: `9`

É uma atualização da versão Timer anterior; não é uma atualização do APK oficial do autor original. Ao atualizar a primeira versão Timer, não é necessário desinstalá-la e suas configurações devem ser preservadas pelo Android.

Mantenha o serviço do app oficial original parado para evitar que ele feche tarefas imediatamente e interfira no temporizador. O Shizuku deve estar ativo, e a versão Timer precisa da permissão dele.

## Cuidados com apps do sistema

Evite selecionar componentes essenciais, como a interface do sistema e a tela inicial. Remover suas tarefas pode afetar a navegação ou interromper telas importantes.

O app usa `removeTask`: remove tarefas/Activities, **não força a parada de todos os processos ou serviços** de um pacote. Componentes do sistema sem uma tarefa de interface não passam a ser encerrados apenas por aparecerem na lista.

A compilação e os testes automatizados não substituem testes no celular com Shizuku. Consulte `VALIDACAO_ANDROID.txt`.
