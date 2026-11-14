package com.ntplaytree.app

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Handler
import android.os.Looper
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.Closeable
import java.net.Inet4Address
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.NetworkInterface
import java.net.ServerSocket
import java.net.Socket
import java.util.Collections
import java.util.UUID
import kotlin.concurrent.thread

data class LanImportPayload(
    val name: String,
    val url: String,
    val content: String
)

/** Temporary, token-protected HTTP server available only on the local network. */
class LanImportServer(
    private val context: Context,
    private val onImport: (LanImportPayload) -> Unit
) : Closeable {
    private val token = UUID.randomUUID().toString().replace("-", "")
    @Volatile private var running = false
    @Volatile private var serverSocket: ServerSocket? = null
    @Volatile var address: String? = null
        private set

    fun start(): String? {
        if (address != null) return address
        val ip = localIpv4Address() ?: return null
        return runCatching {
            val socket = ServerSocket().apply {
                reuseAddress = true
                bind(InetSocketAddress(InetAddress.getByName("0.0.0.0"), 0), 8)
            }
            serverSocket = socket
            running = true
            address = "http://$ip:${socket.localPort}/?token=$token"
            thread(name = "NTPlaytree-LAN-Import", isDaemon = true) {
                while (running) {
                    val client = runCatching { socket.accept() }.getOrNull() ?: break
                    runCatching { client.use { handleClient(it) } }
                }
            }
            address
        }.getOrNull()
    }

    private fun handleClient(client: Socket) {
        client.soTimeout = 12_000
        val input = BufferedInputStream(client.getInputStream())
        val headerBytes = ByteArrayOutputStream()
        var matched = 0
        val marker = byteArrayOf(13, 10, 13, 10)
        while (headerBytes.size() < 16_384 && matched < marker.size) {
            val value = input.read()
            if (value < 0) return
            headerBytes.write(value)
            matched = if (value.toByte() == marker[matched]) matched + 1
            else if (value.toByte() == marker[0]) 1 else 0
        }
        val headers = headerBytes.toString(Charsets.ISO_8859_1.name())
        val lines = headers.split("\r\n")
        val request = lines.firstOrNull().orEmpty().split(' ')
        val method = request.getOrNull(0).orEmpty()
        val path = request.getOrNull(1).orEmpty()
        val authorized = path.contains("token=$token")

        if (method == "OPTIONS" && authorized) {
            respond(client, 204, "text/plain; charset=utf-8", "")
            return
        }
        if (method == "GET" && path.startsWith("/health") && authorized) {
            respond(client, 200, "application/json; charset=utf-8", "{\"ok\":true,\"service\":\"NTPlaytree LAN import\"}")
            return
        }
        if (method == "GET" && authorized) {
            respond(client, 200, "text/html; charset=utf-8", transferPage())
            return
        }
        if (method == "POST" && path.startsWith("/import") && authorized) {
            val contentLength = lines.firstOrNull { it.startsWith("Content-Length:", true) }
                ?.substringAfter(':')?.trim()?.toIntOrNull() ?: 0
            if (contentLength !in 1..(12 * 1024 * 1024)) {
                respond(client, 413, "text/plain; charset=utf-8", "Arquivo vazio ou maior que 12 MB.")
                return
            }
            val body = ByteArray(contentLength)
            var offset = 0
            while (offset < contentLength) {
                val count = input.read(body, offset, contentLength - offset)
                if (count < 0) break
                offset += count
            }
            if (offset != contentLength) {
                respond(client, 400, "text/plain; charset=utf-8", "Transferência incompleta.")
                return
            }
            val json = runCatching { JSONObject(String(body, Charsets.UTF_8)) }.getOrNull()
            if (json == null) {
                respond(client, 400, "text/plain; charset=utf-8", "Dados inválidos.")
                return
            }
            val name = json.optString("name", "Lista enviada pelo celular").trim().ifBlank { "Lista enviada pelo celular" }
            val url = json.optString("url", "").trim()
            val content = json.optString("content", "").trim()
            val validUrl = url.startsWith("http://", true) || url.startsWith("https://", true)
            val validM3u = content.trimStart('\uFEFF', ' ', '\n', '\r', '\t').startsWith("#EXTM3U", true)
            if (!validUrl && !validM3u) {
                respond(client, 422, "text/plain; charset=utf-8", "Informe um endereço HTTP/HTTPS ou envie um arquivo M3U válido.")
                return
            }
            Handler(Looper.getMainLooper()).post { onImport(LanImportPayload(name, if (validUrl) url else "", if (validM3u) content else "")) }
            respond(client, 200, "application/json; charset=utf-8", "{\"ok\":true,\"message\":\"Lista recebida. Você já pode voltar à TV.\"}")
            return
        }
        respond(client, 403, "text/plain; charset=utf-8", "Acesso não autorizado. Escaneie novamente o QR Code exibido na TV.")
    }

    private fun transferPage(): String = """
        <!doctype html><html lang="pt-BR"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1">
        <title>Enviar lista para NTPlaytree</title><style>
        *{box-sizing:border-box}body{margin:0;background:#090d1c;color:#f4f6ff;font:16px Arial,sans-serif;padding:24px}
        main{max-width:620px;margin:24px auto;background:#141d35;border:1px solid #293657;border-radius:22px;padding:26px}
        h1{margin:0 0 8px;color:#fff}p{color:#aab6d0;line-height:1.5}label{display:block;margin:18px 0 7px;color:#dce4ff;font-weight:600}
        input,textarea{width:100%;padding:14px;border-radius:10px;border:1px solid #3a4b70;background:#0c1428;color:#fff;font-size:16px}
        textarea{min-height:130px;resize:vertical}button{width:100%;margin-top:20px;padding:15px;border:0;border-radius:12px;background:linear-gradient(100deg,#13cfc9,#8647ed);color:#06101e;font-size:17px;font-weight:700}
        #msg{margin-top:14px;color:#5de2d6;min-height:22px}.hint{font-size:13px}
        </style></head><body><main><h1>NTPlaytree</h1><p>Envie uma lista M3U para a sua TV. O celular e a televisão precisam estar conectados à mesma rede Wi-Fi.</p>
        <label>Nome da lista</label><input id="name" value="Minha lista">
        <label>Endereço M3U (opcional)</label><input id="url" placeholder="https://servidor/lista.m3u" inputmode="url">
        <label>Ou escolha um arquivo M3U</label><input id="file" type="file" accept=".m3u,.m3u8,.txt,audio/x-mpegurl,application/vnd.apple.mpegurl">
        <p class="hint">Você também pode colar o conteúdo da lista no campo abaixo.</p><label>Conteúdo M3U (opcional)</label><textarea id="content" placeholder="#EXTM3U&#10;... "></textarea>
        <button onclick="sendList()">Enviar para a TV</button><div id="msg"></div></main><script>
        async function sendList(){const msg=document.getElementById('msg');msg.textContent='Preparando envio...';let content=document.getElementById('content').value.trim();const file=document.getElementById('file').files[0];if(file)content=await file.text();const payload={name:document.getElementById('name').value,url:document.getElementById('url').value.trim(),content};try{const r=await fetch('/import?token=$token',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify(payload)});const t=await r.text();if(!r.ok)throw new Error(t);msg.textContent='Lista enviada! Volte ao NTPlaytree na televisão.';}catch(e){msg.textContent='Não foi possível enviar: '+e.message;}}</script></body></html>
    """.trimIndent()

    private fun respond(client: Socket, code: Int, contentType: String, body: String) {
        val reason = when (code) { 200 -> "OK"; 204 -> "No Content"; 400 -> "Bad Request"; 403 -> "Forbidden"; 413 -> "Payload Too Large"; 422 -> "Unprocessable Entity"; else -> "Error" }
        val bytes = body.toByteArray(Charsets.UTF_8)
        val header = "HTTP/1.1 $code $reason\r\nContent-Type: $contentType\r\nContent-Length: ${bytes.size}\r\nAccess-Control-Allow-Origin: *\r\nAccess-Control-Allow-Methods: GET, POST, OPTIONS\r\nAccess-Control-Allow-Headers: Content-Type\r\nConnection: close\r\nCache-Control: no-store\r\n\r\n"
        client.getOutputStream().apply { write(header.toByteArray(Charsets.ISO_8859_1)); write(bytes); flush() }
    }

    private fun localIpv4Address(): String? = runCatching {
        val connectivity = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivity.activeNetwork
        val capabilities = activeNetwork?.let { connectivity.getNetworkCapabilities(it) }
        val linkProperties = activeNetwork?.let { connectivity.getLinkProperties(it) }
        val activeAddresses = linkProperties?.linkAddresses
            ?.mapNotNull { it.address as? Inet4Address }
            ?.filter { !it.isLoopbackAddress && !it.isLinkLocalAddress }
            .orEmpty()

        val activeIsLanTransport = capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true ||
            capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) == true
        val activeLanAddress = activeAddresses.firstOrNull { isPrivateIpv4(it.hostAddress.orEmpty()) }
        if (activeIsLanTransport && activeLanAddress != null) return@runCatching activeLanAddress.hostAddress

        // Do not publish a cellular, VPN, loopback or emulator-only address as a scannable LAN URL.
        val interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
        val candidates = interfaces.filter { iface ->
            val name = iface.name.lowercase()
            iface.isUp && !iface.isLoopback &&
                !name.startsWith("tun") && !name.startsWith("ppp") &&
                !name.startsWith("rmnet") && !name.startsWith("dummy") &&
                !name.startsWith("lo") && !name.startsWith("wg") &&
                !name.startsWith("vpn") && !name.startsWith("sit") && !name.startsWith("p2p")
        }.sortedBy { iface ->
            val name = iface.name.lowercase()
            when {
                name.startsWith("wlan") || name.startsWith("wifi") -> 0
                name.startsWith("eth") || name.startsWith("en") -> 1
                name.startsWith("br") -> 2
                else -> 3
            }
        }.flatMap { Collections.list(it.inetAddresses) }
            .filterIsInstance<Inet4Address>()
            .filter { !it.isLoopbackAddress && !it.isLinkLocalAddress && isPrivateIpv4(it.hostAddress.orEmpty()) }

        candidates.firstOrNull()?.hostAddress
    }.getOrNull()

    private fun isPrivateIpv4(value: String): Boolean {
        val parts = value.split('.').mapNotNull { it.toIntOrNull() }
        if (parts.size != 4) return false
        return parts[0] == 10 ||
            (parts[0] == 192 && parts[1] == 168) ||
            (parts[0] == 172 && parts[1] in 16..31)
    }

    override fun close() {
        running = false
        runCatching { serverSocket?.close() }
        serverSocket = null
        address = null
    }
}
