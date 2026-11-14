# NTPlaytree — 1.1.0-alpha22

Projeto Android nativo em Kotlin + Jetpack Compose para Android TV e dispositivos Android.

## Alterações alpha22

- Corrige o transporte de listas M3U para negociar compressão HTTP automaticamente com OkHttp, em vez de forçar `Accept-Encoding: identity`.
- Usa um User-Agent de navegador baseado na versão Chromium/WebView disponível no próprio Android, com fallback compatível.
- Remove o prazo total fixo de 120 segundos do download M3U. A conexão continua protegida por timeout de conexão de 25 segundos e timeout de leitura ociosa de 90 segundos.
- Mostra etapas do download e do parsing, incluindo quantidade de itens processados a cada mil entradas.
- Em respostas HTTP 200 que não sejam M3U válida, informa tipo de conteúdo, tamanho declarado e host final do redirecionamento, sem expor usuário, senha ou URL completa.
- Mantém Media3 como player principal e LibVLC como alternativa. A troca de player não substitui a correção do transporte nem resolve falhas no servidor de origem.

## O que ainda precisa ser validado

O teste do usuário confirmou que o Chrome inicia o download do endereço M3U. Isso prova que o endereço responde ao navegador, mas ainda não confirma que o arquivo foi baixado por completo nem que o corpo começa com `#EXTM3U`. A alpha22 adapta a requisição do aplicativo para se aproximar do comportamento de um navegador e elimina o limite total fixo de 120 segundos.

A compilação Android e o teste real de rede ainda precisam ser executados no Android Studio e no dispositivo. Nenhum APK é declarado como validado nesta entrega.

## Compilar

Abra a pasta `NTPlaytree-alpha22` no Android Studio, instale o Android SDK API 36, configure Java 17, sincronize o Gradle, execute os testes e use **Build > Build APK(s)**.
