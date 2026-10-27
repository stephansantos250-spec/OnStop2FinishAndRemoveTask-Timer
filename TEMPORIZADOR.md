# Temporizador de fechamento — 1 a 5 minutos

## O que foi adicionado

- Em **Configurações → Temporizador de fechamento**, selecione 1, 2, 3, 4 ou 5 minutos.
- O padrão é **1 minuto**. O valor fica salvo entre aberturas do app.
- O tempo escolhido é global: vale para todos os apps marcados na lista principal.
- Ao trocar de um app selecionado para outro app ou para a tela inicial, começa a contagem da tarefa anterior.
- Cada tarefa pendente tem sua própria contagem, inclusive ao alternar entre dois apps selecionados.
- Reabrir o mesmo app antes do prazo cancela seus fechamentos pendentes. Ao sair novamente, começa um novo prazo completo.
- Desmarcar um app, remover sua tarefa ou parar o serviço cancela o fechamento pendente correspondente.
- Alterar o tempo reinicia as contagens pendentes com o novo valor.
- O seletor e seus textos estão disponíveis em português do Brasil, inglês e japonês. O restante da interface preserva as traduções existentes.

## Como usar / compilar

Este ZIP contém **código-fonte**. O APK assinado é fornecido separadamente como `OnStop2FinishAndRemoveTask-Timer-1-a-5-min.apk`. A modificação não foi publicada no repositório do autor.

1. Extraia o ZIP e abra a pasta do projeto no Android Studio atualizado.
2. Instale o SDK exigido pelo projeto: **API 37**. Preserve o Gradle e as versões de dependências existentes no repositório.
3. Aguarde a sincronização e use **Build → Generate App Bundles or APKs → Generate APKs** (a redação do menu pode variar).
4. A variante **release** usa o identificador `io.github.takusan23.onstop2finishandremovetask.timer` e o nome **OnStop2FinishAndRemoveTask Timer**. Ela é instalada separadamente e não substitui o app oficial. A assinatura é local, não a do autor original. Pare o serviço da versão original antes de usar a versão Timer, para evitar fechamentos imediatos pela versão original.
5. Inicie o Shizuku e conceda a permissão solicitada pelo app.
6. Marque os apps desejados na lista principal. Nas Configurações, escolha o prazo. Mantenha o serviço ativo.

## Limites importantes

O comportamento original usa `removeTask` por meio do Shizuku: **remove a tarefa/Activity, não é “Forçar parada” do pacote**. Serviços, reprodução de áudio e outros processos de um app podem continuar ativos conforme o comportamento dele e do Android.

O temporizador depende dos eventos de mudança de tarefa do Android e do serviço em execução. Não foi adicionado rastreamento de apps que já estavam em segundo plano antes de iniciar o serviço. O objetivo é contar a partir das trocas de app observadas pelo serviço.

As contagens são mantidas em memória: encerrar o serviço/processo ou reiniciar o celular cancela as contagens. Suspensão do dispositivo, Doze e restrições do fabricante podem atrasar a execução; não é um alarme de precisão. Não há nova permissão de alarme exato ou de bloqueio de suspensão.

Multijanela/tela dividida, tela bloqueada e particularidades de fabricantes precisam de validação no dispositivo. Não selecione apps com trabalho não salvo sem testar: a tarefa será removida ao fim do prazo.

## Validação realizada

- Oito testes unitários JVM passaram, com tempo virtual: prazos de 1 a 5 minutos, reabertura, reinício da contagem, tarefas independentes, alteração do prazo, desmarcação, remoção/parada e exclusão de apps não selecionados.
- O núcleo Kotlin `BackgroundTaskTimer` e seu teste foram compilados isoladamente com Kotlin 2.1.20, coroutines 1.10.2 e JUnit 4.13.2.
- XML das traduções validado e `git diff --check` sem erros.
- Compilação integral concluída com sucesso: `:app:testDebugUnitTest :app:assembleRelease`. Nove testes passaram (oito da lógica do timer e um teste existente do projeto).
- APK release alinhado para páginas de 16 KB e assinado com chave local. Assinatura verificada com `apksigner`; integridade ZIP e alinhamento verificados. Permissões equivalentes às do APK original, considerando a mudança de identificador.
- **Não testado em celular/emulador Android.** É necessário validar a integração com Shizuku e o comportamento no dispositivo. Consulte `VALIDACAO_ANDROID.txt`.

No ambiente Android configurado, execute:

```sh
bash gradlew :app:testDebugUnitTest :app:assembleDebug
```

## Teste manual sugerido

1. Selecione dois apps de teste (A e B) e defina 1 minuto.
2. Abra A, troque para B e depois vá para a tela inicial. Confira o fechamento de cada tarefa após seu respectivo prazo.
3. Abra A novamente, saia, aguarde 30 segundos e reabra A: ele não deve fechar ao vencer o prazo antigo.
4. Saia de A outra vez: o novo minuto deve começar do zero.
5. Repita o teste com 2, 3, 4 e 5 minutos.
6. Durante uma contagem, desmarque o app ou pare o serviço: o fechamento deve ser cancelado.
7. Durante uma contagem, altere o prazo: a contagem deve reiniciar com o novo tempo.
8. Confira o comportamento com tela apagada, economia de bateria, perda de conexão Shizuku e multijanela no seu dispositivo.

## Base

Repositório: https://github.com/takusan23/OnStop2FinishAndRemoveTask
Commit-base: ea535cd546a96722cdfda906508b667de7f81ecb
Licença original Apache-2.0 preservada. Esta é uma modificação local, não publicada pelo autor original.
