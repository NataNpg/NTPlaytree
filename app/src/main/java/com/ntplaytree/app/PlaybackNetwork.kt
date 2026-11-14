package com.ntplaytree.app

import okhttp3.Dns
import okhttp3.OkHttpClient
import okhttp3.dnsoverhttps.DnsOverHttps
import okhttp3.HttpUrl.Companion.toHttpUrl
import java.net.InetAddress
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit

/** Shared HTTP transport with separate timeout policies for playback and metadata requests. */
internal object PlaybackNetwork {
    private object FallbackDns : Dns {
        private val bootstrapClient: OkHttpClient by lazy {
            OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .callTimeout(8, TimeUnit.SECONDS)
                .build()
        }

        private val cloudflare: DnsOverHttps by lazy {
            DnsOverHttps.Builder()
                .client(bootstrapClient)
                .url("https://cloudflare-dns.com/dns-query".toHttpUrl())
                .bootstrapDnsHosts(InetAddress.getByName("1.1.1.1"), InetAddress.getByName("1.0.0.1"))
                .includeIPv6(true)
                .build()
        }

        private val google: DnsOverHttps by lazy {
            DnsOverHttps.Builder()
                .client(bootstrapClient)
                .url("https://dns.google/dns-query".toHttpUrl())
                .bootstrapDnsHosts(InetAddress.getByName("8.8.8.8"), InetAddress.getByName("8.8.4.4"))
                .includeIPv6(true)
                .build()
        }

        override fun lookup(hostname: String): List<InetAddress> {
            try {
                return Dns.SYSTEM.lookup(hostname)
            } catch (systemFailure: UnknownHostException) {
                val failures = mutableListOf<Throwable>(systemFailure)
                for (resolver in listOf(cloudflare, google)) {
                    try {
                        val addresses = resolver.lookup(hostname)
                        if (addresses.isNotEmpty()) return addresses
                    } catch (failure: Exception) {
                        failures += failure
                    }
                }
                val error = UnknownHostException(
                    "Android DNS, Cloudflare DNS e Google DNS não conseguiram resolver $hostname"
                )
                error.initCause(failures.lastOrNull() ?: systemFailure)
                failures.dropLast(1).forEach(error::addSuppressed)
                throw error
            }
        }
    }

    /** Long-lived playback streams must not have a total call deadline. */
    val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .dns(FallbackDns)
            .connectTimeout(12, TimeUnit.SECONDS)
            .readTimeout(25, TimeUnit.SECONDS)
            .callTimeout(0, TimeUnit.MILLISECONDS)
            .followRedirects(true)
            .followSslRedirects(true)
            .retryOnConnectionFailure(true)
            .build()
    }

    /** Short API requests (Xtream metadata) have a finite total deadline. */
    val apiClient: OkHttpClient by lazy {
        client.newBuilder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(18, TimeUnit.SECONDS)
            .callTimeout(28, TimeUnit.SECONDS)
            .build()
    }

    /**
     * Playlist downloads are streamed and parsed while the response is being read.
     * A total call deadline incorrectly rejects large but healthy provider playlists;
     * the read timeout still aborts a connection that stops sending bytes.
     */
    val playlistClient: OkHttpClient by lazy {
        client.newBuilder()
            .connectTimeout(25, TimeUnit.SECONDS)
            .readTimeout(90, TimeUnit.SECONDS)
            .callTimeout(0, TimeUnit.MILLISECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    /** EPG is optional and must never delay the main channel catalog. */
    val epgClient: OkHttpClient by lazy {
        client.newBuilder()
            .connectTimeout(8, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .callTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    /** Run the same Android -> Cloudflare -> Google DNS sequence used by playback. */
    fun resolveHost(hostname: String): List<InetAddress> = FallbackDns.lookup(hostname)
}
