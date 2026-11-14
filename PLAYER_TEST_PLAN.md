# Plano de validação — NTPlaytree alpha22

## Importação da lista

1. Abra `NTPlaytree-alpha22` no Android Studio e sincronize o Gradle.
2. Execute os testes unitários de `M3uClassifierTest`.
3. Execute `Build > Make Project` e depois `Build > Build APK(s)`.
4. No mesmo Android, use no Chrome o mesmo endereço M3U cadastrado no app e confirme que o download termina. Não compartilhe o endereço completo, pois ele pode conter credenciais.
5. Instale a alpha22 e atualize a fonte. Observe as etapas: conexão, resposta HTTP e leitura do catálogo.
6. Confirme que a quantidade de itens carregados é maior que zero e compare com o arquivo baixado pelo Chrome.
7. Se falhar, registre o código HTTP, Content-Type, tamanho informado e host final exibidos no diagnóstico. Não envie URL completa, usuário ou senha.

## Catálogo e reprodução

8. Confirme que emissoras, filmes e episódios são separados corretamente; A&E, AMC, AXN e Discovery devem permanecer em TV ao vivo mesmo quando a URL usar `/movie/`, salvo tipo explícito diferente na M3U.
9. Teste TV ao vivo, filmes, episódios, favoritos, QR Code e importação local.
10. Teste Media3 e LibVLC com links válidos e formatos suportados. Uma falha HTTP/DNS do servidor deve ser diagnosticada separadamente de erro de codec.

## Limites da validação

A compilação e a reprodução reais precisam ser executadas no Android Studio e no dispositivo. A alteração do transporte M3U não garante disponibilidade do servidor, validade dos links individuais ou compatibilidade de todos os codecs.
