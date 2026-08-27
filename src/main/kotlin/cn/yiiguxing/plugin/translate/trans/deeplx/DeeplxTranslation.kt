package cn.yiiguxing.plugin.translate.trans.deeplx

import cn.yiiguxing.plugin.translate.message
import cn.yiiguxing.plugin.translate.trans.Lang
import cn.yiiguxing.plugin.translate.trans.Translation
import cn.yiiguxing.plugin.translate.trans.TranslationAdapter
import cn.yiiguxing.plugin.translate.trans.text.DictionaryDocument
import cn.yiiguxing.plugin.translate.trans.text.DictionaryEntry
import cn.yiiguxing.plugin.translate.trans.text.DictionaryGroup
import com.google.gson.annotations.SerializedName

data class DeeplxTranslations (
    var original: String? = null,
    var targetLang: Lang,
    var srcLang: Lang,
    var data: String? = null,
    var id: Long,
    val code: Int = 0,
    @SerializedName("alternatives" ) var alternatives : ArrayList<String> = arrayListOf()
) : TranslationAdapter {

    val isSuccessful get() = code == 200

    override fun toTranslation(): Translation {
        check(isSuccessful) { "Cannot convert to Translation: errorCode=$code" }
        check(data != null) { "Cannot convert to Translation: trans=null" }
        //如果srcLang 值为空
        if (srcLang == null) {
            srcLang = Lang.ENGLISH_AMERICAN
        }

        val dictDocument = alternatives.takeIf { it.isNotEmpty() }?.let { alts ->
            DictionaryDocument(
                listOf(
                    DictionaryGroup(
                        message("deeplx.alternatives"),
                        alts.map { DictionaryEntry(it) }
                    )
                )
            )
        }

        return Translation(
            original = original!!,
            translation = data,
            srcLang = srcLang,
            targetLang = targetLang,
            sourceLangs = listOf(srcLang),
            dictDocument = dictDocument
        )
    }
}

