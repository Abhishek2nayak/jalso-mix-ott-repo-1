package com.jalsomix.ott.data

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import java.io.File

/** Offline downloads using the system DownloadManager (files live in the app's own storage). */
object Downloads {

    data class Status(val state: Int, val progress: Float, val bytes: Long) {
        val done get() = state == DownloadManager.STATUS_SUCCESSFUL
        val failed get() = state == DownloadManager.STATUS_FAILED
    }

    private fun manager(ctx: Context) = ctx.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    fun fileFor(ctx: Context, titleId: String, ep: Int): File =
        File(ctx.getExternalFilesDir(Environment.DIRECTORY_MOVIES), "jm_${titleId}_$ep.mp4")

    fun enqueue(ctx: Context, t: Title, ep: Episode): Long {
        val file = fileFor(ctx, t.id, ep.index)
        if (file.exists()) file.delete()
        val req = DownloadManager.Request(Uri.parse(ep.videoUrl))
            .setTitle(t.name)
            .setDescription("JalsomixOTT • Episode ${ep.index + 1}")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            .setAllowedOverMetered(true)
            .setDestinationUri(Uri.fromFile(file))
        return manager(ctx).enqueue(req)
    }

    fun remove(ctx: Context, rec: DownloadRecord) {
        runCatching { manager(ctx).remove(rec.downloadId) }
        fileFor(ctx, rec.titleId, rec.episode).delete()
    }

    fun status(ctx: Context, id: Long): Status {
        val q = DownloadManager.Query().setFilterById(id)
        manager(ctx).query(q)?.use { c ->
            if (c.moveToFirst()) {
                val st = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))
                val got = c.getLong(c.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                val total = c.getLong(c.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                val p = if (total > 0) (got.toFloat() / total).coerceIn(0f, 1f) else 0f
                return Status(st, if (st == DownloadManager.STATUS_SUCCESSFUL) 1f else p, got)
            }
        }
        return Status(DownloadManager.STATUS_FAILED, 0f, 0L)
    }
}
