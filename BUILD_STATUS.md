# Estado verificável — NTPlaytree alpha22

- Fonte de trabalho criada a partir da alpha21; o ZIP original foi preservado.
- Requisição M3U revisada: removido `Accept-Encoding: identity`, permitindo negociação e descompressão gzip automáticas do OkHttp.
- User-Agent M3U substituído por um User-Agent de navegador derivado do Android, com fallback.
- Removido o timeout total fixo de 120 segundos do download M3U; mantidos timeout de conexão de 25 segundos e timeout de leitura ociosa de 90 segundos.
- Progresso de conexão, resposta HTTP e parsing exibido durante a atualização; parsing informa a contagem a cada mil itens.
- Diagnóstico de corpo inválido após HTTP 200 agora informa Content-Type, tamanho declarado e host final, sem expor a URL completa ou credenciais.
- O usuário confirmou que o Chrome inicia o download da M3U. Ainda não foi confirmado se o download termina nem se o conteúdo baixado é uma M3U válida.
- `versionCode=22`; `versionName=1.1.0-alpha22`.
- **Compilação Android não executada neste ambiente**: Android SDK e cache Gradle não estão disponíveis. Nenhum APK é declarado como compilado ou validado.
- Próxima validação obrigatória: sincronizar e compilar no Android Studio; instalar a alpha22; atualizar a lista e registrar o resultado HTTP/progresso; comparar a contagem do catálogo com o arquivo baixado pelo Chrome.
