package org.koitharu.kotatsu.reader.ui.config

import org.koitharu.kotatsu.parsers.MangaLoaderContext
import org.koitharu.kotatsu.parsers.model.Manga
import org.koitharu.kotatsu.parsers.model.MangaChapter
import org.koitharu.kotatsu.parsers.network.OkHttpWebClient
import org.koitharu.kotatsu.parsers.network.WebClient
import org.koitharu.kotatsu.parsers.util.parseHtml
import javax.inject.Inject
import javax.inject.Singleton


class ChapterFindException(message: String) : Exception(message)

@Singleton
class ChapterUrlFinder @Inject constructor(
	private val loaderContext: MangaLoaderContext
) {

	suspend fun getChapterUrl(manga: Manga, chapter: MangaChapter): String {

		if (chapter.url.startsWith("http")) {
			return chapter.url
		}

		val webClient: WebClient = OkHttpWebClient(loaderContext.httpClient, manga.source)
		val root = webClient.httpGet(manga.publicUrl).parseHtml()
		val element = root.selectFirst("[href*=${chapter.url}]")
			?: throw ChapterFindException("Couldn't find element")

		val finalUrl = element.absUrl("href")
		return finalUrl
	}
}
