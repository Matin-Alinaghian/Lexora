package com.lexora.app.utils

object EnglishPhonetics {

    fun toIPA(word: String): String {
        if (word.isBlank()) return ""
        val w = word.lowercase().trim()
        if (w.length == 1) return handleSingleChar(w)

        IRREGULAR[w]?.let { return it }

        val sb = StringBuilder()
        var i = 0
        val chars = w.toCharArray()

        while (i < chars.size) {
            val ch = chars[i]
            val prev = if (i > 0) chars[i - 1] else '\u0000'
            val next = if (i < chars.size - 1) chars[i + 1] else '\u0000'
            val next2 = if (i < chars.size - 2) chars[i + 2] else '\u0000'

            when {
                i + 2 < chars.size && "${ch}${chars[i+1]}${chars[i+2]}" in TRIGRAPHS -> {
                    sb.append(TRIGRAPHS["${ch}${chars[i+1]}${chars[i+2]}"])
                    i += 3
                }
                i + 1 < chars.size && "${ch}${chars[i+1]}" in DIGRAPHS -> {
                    sb.append(DIGRAPHS["${ch}${chars[i+1]}"])
                    i += 2
                }
                ch == 'e' && i == chars.size - 1 && isVowel(prev) -> {
                    i++
                }
                ch == 'e' && i == chars.size - 1 && !isVowel(prev) && prev != 'l' -> {
                    i++
                }
                ch == 'e' && i == chars.size - 1 -> {
                    i++
                }
                else -> {
                    sb.append(phonemeFor(ch, prev, next, next2, i, chars.size))
                    i++
                }
            }
        }

        val result = sb.toString().replace(Regex("\\s+"), " ").trim()
        return if (result.isNotEmpty()) "/$result/" else "/$w/"
    }

    private fun phonemeFor(
        ch: Char, prev: Char, next: Char, next2: Char,
        pos: Int, len: Int
    ): String {
        return when (ch) {
            'a' -> when {
                next == 'l' && pos + 2 < len && next2 == 'l' -> "/ɔː/"
                next == 'r' || next == 'w' -> "/ɔː/"
                next == 'i' && (pos + 2 >= len || next2 != 'n') -> "/eɪ/"
                next == 'y' -> "/eɪ/"
                next in "aeiou" -> "/æ/"
                isConsonant(next) && pos + 1 < len - 1 && isConsonant(next2) -> "/æ/"
                pos == 0 && next == 'n' && pos + 1 == len - 1 -> "/æ/"
                else -> "/æ/"
            }
            'b' -> {
                if (prev == 'm' && pos == len - 1) ""
                else "/b/"
            }
            'c' -> when {
                next == 'e' || next == 'i' || next == 'y' -> "/s/"
                else -> "/k/"
            }
            'd' -> "/d/"
            'e' -> when {
                pos == 0 && next == 'u' -> "/juː/"
                pos == 0 && (next == 'x' || next == 'n') -> "/ɪ/"
                pos > 0 && pos == len - 1 -> ""
                next in "aeiou" -> "/ɛ/"
                else -> "/ɛ/"
            }
            'f' -> "/f/"
            'g' -> when {
                next == 'e' || next == 'i' || next == 'y' -> "/dʒ/"
                next == 'h' && pos + 2 < len -> "/ɡ/"
                else -> "/ɡ/"
            }
            'h' -> when {
                pos == 0 && next in "aeiou" -> "/h/"
                pos > 0 && prev in "cgsrt" -> ""
                next == 'u' -> "/hjuː/"
                else -> "/h/"
            }
            'i' -> when {
                pos == 0 && next == 'e' -> "/aɪ/"
                pos == 0 && next == 'o' -> "/aɪo/"
                next in "aeiou" -> "/ɪ/"
                pos == len - 1 || (pos == len - 2 && next == 'n') -> "/aɪ/"
                else -> "/ɪ/"
            }
            'j' -> "/dʒ/"
            'k' -> {
                if (prev == 'n') ""
                else "/k/"
            }
            'l' -> when {
                pos > 0 && prev in "aeiou" && pos == len - 1 -> "/l/"
                pos > 0 && prev in "aeiou" && isConsonant(next) -> "/l/"
                else -> "/l/"
            }
            'm' -> "/m/"
            'n' -> "/n/"
            'o' -> when {
                next == 'o' -> "/uː/"
                next == 'w' -> "/oʊ/"
                pos == 0 && next == 'h' -> "/oʊ/"
                next in "aeiou" && pos == 0 -> "/ɒ/"
                pos == len - 1 || (pos == len - 2 && next == 'n') -> "/oʊ/"
                else -> "/ɒ/"
            }
            'p' -> "/p/"
            'q' -> "/kw/"
            'r' -> "/ɹ/"
            's' -> when {
                next == 'h' -> "/ʃ/"
                prev in "aeiou" && next in "aeiou" -> "/z/"
                pos > 0 && prev in "aeiou" && next == 'e' && pos == len - 2 -> "/s/"
                else -> "/s/"
            }
            't' -> when {
                next == 'i' && pos + 2 < len && isConsonant(next2) -> "/ʃ/"
                next == 'i' && pos + 1 == len - 1 -> "/tʃ/"
                next == 'h' -> "/θ/"
                next == 'i' -> "/ʃ/"
                else -> "/t/"
            }
            'u' -> when {
                pos == 0 && next == 'n' -> "/ʌ/"
                pos == 0 && next == 's' -> "/ʌ/"
                next in "aeiou" -> "/ʌ/"
                pos == len - 1 || (pos == len - 2 && next in "lnrst") -> "/uː/"
                else -> "/ʌ/"
            }
            'v' -> "/v/"
            'w' -> "/w/"
            'x' -> "/ks/"
            'y' -> when {
                pos == 0 && next in "aeiou" -> "/j/"
                else -> "/aɪ/"
            }
            'z' -> "/z/"
            else -> ch.toString()
        }
    }

    private fun handleSingleChar(ch: String): String = when (ch) {
        "a" -> "/eɪ/"
        "i" -> "/aɪ/"
        "o" -> "/oʊ/"
        else -> "/$ch/"
    }

    private fun isVowel(ch: Char): Boolean = ch in "aeiou"
    private fun isConsonant(ch: Char): Boolean = ch.isLetter() && ch !in "aeiou"

    private val DIGRAPHS = mapOf(
        "sh" to "/ʃ/", "ch" to "/tʃ/", "th" to "/θ/", "ph" to "/f/",
        "ck" to "/k/", "ng" to "/ŋ/", "wh" to "/w/", "gh" to "/ɡ/",
        "qu" to "/kw/", "wr" to "/ɹ/", "kn" to "/n/", "gn" to "/n/",
        "mb" to "/m/", "ai" to "/eɪ/", "ay" to "/eɪ/", "ea" to "/iː/",
        "ee" to "/iː/", "oo" to "/uː/", "ou" to "/aʊ/", "ow" to "/oʊ/",
        "oi" to "/ɔɪ/", "oy" to "/ɔɪ/", "au" to "/ɔː/", "aw" to "/ɔː/",
        "ie" to "/iː/", "ue" to "/uː/", "ei" to "/eɪ/", "ey" to "/eɪ/",
        "oa" to "/oʊ/", "oe" to "/oʊ/", "ui" to "/uː/", "ew" to "/juː/",
        "tion" to "/ʃən/", "sion" to "/ʃən/", "ture" to "/tʃər/",
        "ous" to "/əs/", "ious" to "/iəs/", "eous" to "/iəs/",
        "ble" to "/bəl/", "cle" to "/kəl/", "dle" to "/dəl/",
        "tle" to "/təl/", "gle" to "/ɡəl/", "ple" to "/pəl/",
    )

    private val TRIGRAPHS = mapOf(
        "igh" to "/aɪ/", "ough" to "/ʌf/", "eigh" to "/eɪ/",
        "tch" to "/tʃ/", "sch" to "/sk/", "thr" to "/θɹ/",
        "str" to "/stɹ/", "spr" to "/spɹ/", "squ" to "/skw/",
    )

    private val IRREGULAR = mapOf(
        "the" to "/ðə/", "this" to "/ðɪs/", "that" to "/ðæt/",
        "those" to "/ðoʊz/", "these" to "/ðiːz/", "there" to "/ðɛɹ/",
        "their" to "/ðɛɹ/", "they" to "/ðeɪ/", "them" to "/ðɛm/",
        "then" to "/ðɛn/", "than" to "/ðæn/", "though" to "/ðoʊ/",
        "through" to "/θɹuː/", "three" to "/θɹiː/", "think" to "/θɪŋk/",
        "what" to "/wʌt/", "when" to "/wɛn/", "where" to "/wɛɹ/",
        "which" to "/wɪtʃ/", "while" to "/waɪl/", "white" to "/waɪt/",
        "who" to "/huː/", "whom" to "/huːm/", "whose" to "/huːz/",
        "could" to "/kʊd/", "would" to "/wʊd/", "should" to "/ʃʊd/",
        "does" to "/dʌz/", "done" to "/dʌn/", "gone" to "/ɡɒn/",
        "come" to "/kʌm/", "some" to "/sʌm/", "love" to "/lʌv/",
        "have" to "/hæv/", "give" to "/ɡɪv/", "live" to "/lɪv/",
        "move" to "/muːv/", "prove" to "/pɹuːv/",
        "were" to "/wɜːɹ/", "here" to "/hɪɹ/",
        "your" to "/jɔːɹ/", "you" to "/juː/", "our" to "/aʊɹ/",
        "about" to "/əbaʊt/", "again" to "/əɡɛn/", "been" to "/biːn/",
        "being" to "/biːɪŋ/", "every" to "/ɛvɹi/", "very" to "/vɛɹi/",
        "many" to "/mɛni/", "only" to "/oʊnli/", "also" to "/ɔːlsoʊ/",
        "just" to "/dʒʌst/", "such" to "/sʌtʃ/",
        "much" to "/mʌtʃ/", "must" to "/mʌst/", "most" to "/moʊst/",
        "more" to "/mɔːɹ/", "before" to "/bɪfɔːɹ/",
        "after" to "/æftəɹ/", "other" to "/ʌðəɹ/", "another" to "/ənʌðəɹ/",
        "mother" to "/mʌðəɹ/", "father" to "/fɑːðəɹ/", "brother" to "/bɹʌðəɹ/",
        "sister" to "/sɪstəɹ/", "water" to "/wɔːtəɹ/", "better" to "/bɛtəɹ/",
        "little" to "/lɪtəl/", "people" to "/piːpəl/", "apple" to "/æpəl/",
        "simple" to "/sɪmpəl/", "example" to "/ɪɡzæmpəl/",
        "world" to "/wɜːɹld/",
        "first" to "/fɜːɹst/", "last" to "/læst/", "next" to "/nɛkst/",
        "right" to "/ɹaɪt/", "left" to "/lɛft/", "good" to "/ɡʊd/",
        "back" to "/bæk/", "made" to "/meɪd/", "make" to "/meɪk/",
        "take" to "/teɪk/", "like" to "/laɪk/", "look" to "/lʊk/",
        "book" to "/bʊk/", "food" to "/fuːd/", "cool" to "/kuːl/",
        "school" to "/skuːl/", "great" to "/ɡɹeɪt/", "place" to "/pleɪs/",
        "house" to "/haʊs/", "horse" to "/hɔːɹs/", "nurse" to "/nɜːɹs/",
        "bird" to "/bɜːɹd/", "girl" to "/ɡɜːɹl/", "turn" to "/tɜːɹn/",
        "learn" to "/lɜːɹn/", "word" to "/wɜːɹd/", "work" to "/wɜːɹk/",
    )
}
