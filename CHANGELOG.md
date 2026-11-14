# NTPlaytree alpha22

- Corrige a requisição M3U: remove `Accept-Encoding: identity` para permitir negociação e descompressão gzip transparentes do OkHttp.
- Substitui o User-Agent proprietário da requisição M3U por um User-Agent de navegador derivado do Android, com fallback.
- Remove o timeout total de 120 segundos do download M3U; mantém timeout de conexão de 25 segundos e timeout de leitura ociosa de 90 segundos.
- Adiciona progresso de conexão, resposta HTTP e parsing do catálogo, atualizado a cada mil itens.
- Melhora o diagnóstico de resposta HTTP 200 com corpo inválido, informando Content-Type, tamanho declarado e host final sem mostrar credenciais.
- Mantém a classificação M3U e os motores Media3/LibVLC da alpha21 sem alterações nesta etapa.
- Atualiza `versionCode` para 22 e `versionName` para `1.1.0-alpha22`.
