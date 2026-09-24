package com.lexora.app.ai

import com.lexora.app.ai.model.WordAiResult
import com.lexora.app.ai.model.WordMeaning

object LocalDictionary {
    private val dictionary: Map<String, WordAiResult> = mapOf(
        "improve" to WordAiResult(
            word = "improve",
            meanings = listOf(
                WordMeaning("to make better", "en"),
                WordMeaning("to enhance", "en")
            ),
            pronunciation = "/ɪmˈpruːv/",
            wordType = "Verb",
            level = "B1",
            synonyms = listOf("enhance", "develop", "upgrade", "better"),
            antonyms = listOf("worsen", "damage", "deteriorate"),
            wordFamily = listOf("improve", "improvement", "improved", "improving"),
            example = "I want to improve my English.",
            exampleTranslation = "من می‌خواهم انگلیسی‌ام را بهتر کنم.",
            tags = listOf("common", "verb", "B1"),
            personalNote = "",
            usedProvider = "local-dictionary"
        ),
        "practice" to WordAiResult(
            word = "practice",
            meanings = listOf(
                WordMeaning("to do something repeatedly to improve", "en"),
                WordMeaning("exercise", "en")
            ),
            pronunciation = "/ˈpræktɪs/",
            wordType = "Verb",
            level = "A2",
            synonyms = listOf("exercise", "rehearse", "train"),
            antonyms = listOf("neglect", "ignore"),
            wordFamily = listOf("practice", "practise", "practiced", "practicing"),
            example = "I practice English every day.",
            exampleTranslation = "من هر روز انگلیسی تمرین می‌کنم.",
            tags = listOf("common", "verb", "A2"),
            personalNote = "",
            usedProvider = "local-dictionary"
        ),
        "learn" to WordAiResult(
            word = "learn",
            meanings = listOf(
                WordMeaning("to gain knowledge or skill", "en"),
                WordMeaning("to study", "en")
            ),
            pronunciation = "/lɜːrn/",
            wordType = "Verb",
            level = "A1",
            synonyms = listOf("study", "master", "acquire"),
            antonyms = listOf("forget", "unlearn"),
            wordFamily = listOf("learn", "learning", "learned", "learner"),
            example = "I am learning English.",
            exampleTranslation = "من انگلیسی یاد می‌گیرم.",
            tags = listOf("common", "verb", "A1"),
            personalNote = "",
            usedProvider = "local-dictionary"
        ),
        "speak" to WordAiResult(
            word = "speak",
            meanings = listOf(
                WordMeaning("to say words", "en"),
                WordMeaning("to talk", "en")
            ),
            pronunciation = "/spiːk/",
            wordType = "Verb",
            level = "A1",
            synonyms = listOf("talk", "say", "utter"),
            antonyms = listOf("listen", "be silent"),
            wordFamily = listOf("speak", "speaking", "spoken", "speaker"),
            example = "Can you speak English?",
            exampleTranslation = "آیا می‌توانی انگلیسی صحبت کنی؟",
            tags = listOf("common", "verb", "A1"),
            personalNote = "",
            usedProvider = "local-dictionary"
        ),
        "read" to WordAiResult(
            word = "read",
            meanings = listOf(
                WordMeaning("to look at and understand written text", "en"),
                WordMeaning("to study", "en")
            ),
            pronunciation = "/riːd/",
            wordType = "Verb",
            level = "A1",
            synonyms = listOf("study", "scan", "interpret"),
            antonyms = listOf("write", "ignore"),
            wordFamily = listOf("read", "reading", "readable"),
            example = "I read a book every week.",
            exampleTranslation = "من هر هفته یک کتاب می‌خوانم.",
            tags = listOf("common", "verb", "A1"),
            personalNote = "",
            usedProvider = "local-dictionary"
        ),
        "write" to WordAiResult(
            word = "write",
            meanings = listOf(
                WordMeaning("to put words on paper or a screen", "en"),
                WordMeaning("to compose text", "en")
            ),
            pronunciation = "/raɪt/",
            wordType = "Verb",
            level = "A1",
            synonyms = listOf("compose", "pen", "draft"),
            antonyms = listOf("read", "erase"),
            wordFamily = listOf("write", "writing", "written", "writer"),
            example = "I write in my journal every day.",
            exampleTranslation = "من هر روز در دفترچه‌ی خودم می‌نویسم.",
            tags = listOf("common", "verb", "A1"),
            personalNote = "",
            usedProvider = "local-dictionary"
        ),
        "happy" to WordAiResult(
            word = "happy",
            meanings = listOf(
                WordMeaning("feeling or showing pleasure", "en"),
                WordMeaning("glad", "en")
            ),
            pronunciation = "/ˈhæpi/",
            wordType = "Adjective",
            level = "A1",
            synonyms = listOf("joyful", "cheerful", "content", "glad"),
            antonyms = listOf("sad", "unhappy", "miserable"),
            wordFamily = listOf("happy", "happiness", "happily", "unhappy"),
            example = "I am very happy today!",
            exampleTranslation = "من امروز بسیار خوشحالم!",
            tags = listOf("common", "adjective", "A1"),
            personalNote = "",
            usedProvider = "local-dictionary"
        ),
        "sad" to WordAiResult(
            word = "sad",
            meanings = listOf(
                WordMeaning("feeling or showing sorrow", "en"),
                WordMeaning("unhappy", "en")
            ),
            pronunciation = "/sæd/",
            wordType = "Adjective",
            level = "A1",
            synonyms = listOf("unhappy", "sorrowful", "gloomy"),
            antonyms = listOf("happy", "joyful", "cheerful"),
            wordFamily = listOf("sad", "sadness", "sadden"),
            example = "She felt sad when she left.",
            exampleTranslation = "او وقتی رفتنش احساس غم کرد.",
            tags = listOf("common", "adjective", "A1"),
            personalNote = "",
            usedProvider = "local-dictionary"
        ),
        "big" to WordAiResult(
            word = "big",
            meanings = listOf(
                WordMeaning("large in size", "en"),
                WordMeaning("important", "en")
            ),
            pronunciation = "/bɪɡ/",
            wordType = "Adjective",
            level = "A1",
            synonyms = listOf("large", "huge", "great", "grand"),
            antonyms = listOf("small", "tiny", "little"),
            wordFamily = listOf("big", "bigger", "biggest", "bigness"),
            example = "That is a big house.",
            exampleTranslation = "خانه‌ی آن بزرگ است.",
            tags = listOf("common", "adjective", "A1"),
            personalNote = "",
            usedProvider = "local-dictionary"
        ),
        "small" to WordAiResult(
            word = "small",
            meanings = listOf(
                WordMeaning("little in size", "en"),
                WordMeaning("not big", "en")
            ),
            pronunciation = "/smɔːl/",
            wordType = "Adjective",
            level = "A1",
            synonyms = listOf("tiny", "little", "mini", "petite"),
            antonyms = listOf("big", "large", "huge"),
            wordFamily = listOf("small", "smaller", "smallest", "smallness"),
            example = "This is a small apple.",
            exampleTranslation = "این یک سیب کوچک است.",
            tags = listOf("common", "adjective", "A1"),
            personalNote = "",
            usedProvider = "local-dictionary"
        ),
        "good" to WordAiResult(
            word = "good",
            meanings = listOf(
                WordMeaning("of high quality", "en"),
                WordMeaning("acceptable", "en"),
                WordMeaning("beneficial", "en")
            ),
            pronunciation = "/ɡʊd/",
            wordType = "Adjective",
            level = "A1",
            synonyms = listOf("excellent", "great", "fine", "nice"),
            antonyms = listOf("bad", "poor", "terrible"),
            wordFamily = listOf("good", "better", "best", "goodness"),
            example = "This is a good book.",
            exampleTranslation = "این کتاب خوبی است.",
            tags = listOf("common", "adjective", "A1"),
            personalNote = "",
            usedProvider = "local-dictionary"
        ),
        "bad" to WordAiResult(
            word = "bad",
            meanings = listOf(
                WordMeaning("of poor quality", "en"),
                WordMeaning("harmful", "en"),
                WordMeaning("wrong", "en")
            ),
            pronunciation = "/bæd/",
            wordType = "Adjective",
            level = "A1",
            synonyms = listOf("poor", "terrible", "awful", "harmful"),
            antonyms = listOf("good", "excellent", "great"),
            wordFamily = listOf("bad", "worse", "worst", "badness"),
            example = "That was a bad mistake.",
            exampleTranslation = "آن یک اشتباه بد بود.",
            tags = listOf("common", "adjective", "A1"),
            personalNote = "",
            usedProvider = "local-dictionary"
        ),
        "quick" to WordAiResult(
            word = "quick",
            meanings = listOf(
                WordMeaning("fast", "en"),
                WordMeaning("speedy", "en")
            ),
            pronunciation = "/kwɪk/",
            wordType = "Adjective",
            level = "A2",
            synonyms = listOf("fast", "rapid", "speedy", "swift"),
            antonyms = listOf("slow", "sluggish", "gradual"),
            wordFamily = listOf("quick", "quicker", "quickest", "quickly", "quickness"),
            example = "He is a quick learner.",
            exampleTranslation = "او یک یادگیرنده سریع است.",
            tags = listOf("common", "adjective", "A2"),
            personalNote = "",
            usedProvider = "local-dictionary"
        ),
        "slow" to WordAiResult(
            word = "slow",
            meanings = listOf(
                WordMeaning("not fast", "en"),
                WordMeaning("sluggish", "en")
            ),
            pronunciation = "/sloʊ/",
            wordType = "Adjective",
            level = "A2",
            synonyms = listOf("sluggish", "lethargic", "gradual", "unhurried"),
            antonyms = listOf("fast", "quick", "rapid", "swift"),
            wordFamily = listOf("slow", "slower", "slowest", "slowly", "slowness"),
            example = "The traffic is very slow today.",
            exampleTranslation = "ترافیک امروز بسیار آهسته است.",
            tags = listOf("common", "adjective", "A2"),
            personalNote = "",
            usedProvider = "local-dictionary"
        ),
        "love" to WordAiResult(
            word = "love",
            meanings = listOf(
                WordMeaning("deep affection", "en"),
                WordMeaning("to like very much", "en")
            ),
            pronunciation = "/lʌv/",
            wordType = "Noun",
            level = "A1",
            synonyms = listOf("affection", "adoration", "devotion"),
            antonyms = listOf("hate", "loathing", "dislike"),
            wordFamily = listOf("love", "loving", "loved", "lover", "lovely"),
            example = "I love reading books.",
            exampleTranslation = "من کتاب خواندن را دوست دارم.",
            tags = listOf("common", "noun", "A1"),
            personalNote = "",
            usedProvider = "local-dictionary"
        ),
        "hopeful" to WordAiResult(
            word = "hopeful",
            meanings = listOf(
                WordMeaning("feeling or showing hope", "en"),
                WordMeaning("optimistic", "en")
            ),
            pronunciation = "/ˈhoʊpfl/",
            wordType = "Adjective",
            level = "B1",
            synonyms = listOf("optimistic", "promising", "confident"),
            antonyms = listOf("hopeless", "pessimistic", "despairing"),
            wordFamily = listOf("hopeful", "hopeless", "hopefully", "hopelessly"),
            example = "She is hopeful about the future.",
            exampleTranslation = "او نسبت به آینده امیدوار است.",
            tags = listOf("common", "adjective", "B1"),
            personalNote = "",
            usedProvider = "local-dictionary"
        ),
        "hopeless" to WordAiResult(
            word = "hopeless",
            meanings = listOf(
                WordMeaning("without hope", "en"),
                WordMeaning("desperate", "en")
            ),
            pronunciation = "/ˈhoʊpləs/",
            wordType = "Adjective",
            level = "B1",
            synonyms = listOf("desperate", "pessimistic", "despairing"),
            antonyms = listOf("hopeful", "optimistic", "promising"),
            wordFamily = listOf("hopeless", "hopelessly", "hopelessness"),
            example = "The situation seemed hopeless.",
            exampleTranslation = "وضعیت بی‌امید به نظر می‌رسید.",
            tags = listOf("common", "adjective", "B1"),
            personalNote = "",
            usedProvider = "local-dictionary"
        ),
        "fluent" to WordAiResult(
            word = "fluent",
            meanings = listOf(
                WordMeaning("able to speak or write smoothly and easily", "en"),
                WordMeaning("flowing", "en")
            ),
            pronunciation = "/ˈfluːənt/",
            wordType = "Adjective",
            level = "B2",
            synonyms = listOf("smooth", "eloquent", "articulate", "silver-tongued"),
            antonyms = listOf("halting", "broken", "inarticulate"),
            wordFamily = listOf("fluent", "fluently", "fluency", "fluentness"),
            example = "She speaks fluent English.",
            exampleTranslation = "او انگلیسی روان صحبت می‌کند.",
            tags = listOf("language", "adjective", "B2"),
            personalNote = "",
            usedProvider = "local-dictionary"
        ),
        "bored" to WordAiResult(
            word = "bored",
            meanings = listOf(
                WordMeaning("feeling weary and restless through lack of interest", "en"),
                WordMeaning("uninterested", "en")
            ),
            pronunciation = "/bɔːrd/",
            wordType = "Adjective",
            level = "A2",
            synonyms = listOf("uninterested", "bored stiff", "tired"),
            antonyms = listOf("interested", "engaged", "excited"),
            wordFamily = listOf("bored", "boring", "bore"),
            example = "I am bored. Let's go for a walk.",
            exampleTranslation = "من خسته‌ام. بروید پیاده‌روی کنیم.",
            tags = listOf("common", "adjective", "A2"),
            personalNote = "",
            usedProvider = "local-dictionary"
        ),
        "excited" to WordAiResult(
            word = "excited",
            meanings = listOf(
                WordMeaning("very enthusiastic and eager", "en"),
                WordMeaning("thrilled", "en")
            ),
            pronunciation = "/ɪkˈsaɪtɪd/",
            wordType = "Adjective",
            level = "A2",
            synonyms = listOf("thrilled", "delighted", "elated", "overjoyed"),
            antonyms = listOf("bored", "uninterested", "apathetic"),
            wordFamily = listOf("excited", "exciting", "excitement", "excite"),
            example = "I am excited about the trip.",
            exampleTranslation = "من برای سفر هیجان‌زده‌ام.",
            tags = listOf("common", "adjective", "A2"),
            personalNote = "",
            usedProvider = "local-dictionary"
        ),
        "tired" to WordAiResult(
            word = "tired",
            meanings = listOf(
                WordMeaning("in need of rest", "en"),
                WordMeaning("weary", "en")
            ),
            pronunciation = "/taɪrd/",
            wordType = "Adjective",
            level = "A1",
            synonyms = listOf("weary", "exhausted", "fatigued", "drained"),
            antonyms = listOf("rested", "energetic", "refreshed"),
            wordFamily = listOf("tired", "tire", "tiring", "tiredness"),
            example = "I am very tired. I need to sleep.",
            exampleTranslation = "من بسیار خسته‌ام. باید بخوابم.",
            tags = listOf("common", "adjective", "A1"),
            personalNote = "",
            usedProvider = "local-dictionary"
        ),
        "angry" to WordAiResult(
            word = "angry",
            meanings = listOf(
                WordMeaning("feeling or showing strong displeasure", "en"),
                WordMeaning("furious", "en")
            ),
            pronunciation = "/ˈæŋɡri/",
            wordType = "Adjective",
            level = "A2",
            synonyms = listOf("furious", "irate", "livid", "enraged"),
            antonyms = listOf("calm", "peaceful", "serene"),
            wordFamily = listOf("angry", "anger", "angrily", "angryness"),
            example = "He was angry about the mistake.",
            exampleTranslation = "او از اشتباه عصبانی بود.",
            tags = listOf("common", "adjective", "A2"),
            personalNote = "",
            usedProvider = "local-dictionary"
        ),
        "nervous" to WordAiResult(
            word = "nervous",
            meanings = listOf(
                WordMeaning("easily agitated or anxious", "en"),
                WordMeaning("apprehensive", "en")
            ),
            pronunciation = "/ˈnɜːrvəs/",
            wordType = "Adjective",
            level = "B1",
            synonyms = listOf("anxious", "apprehensive", "tense", "jittery"),
            antonyms = listOf("calm", "relaxed", "composed"),
            wordFamily = listOf("nervous", "nervously", "nervousness", "nerves"),
            example = "I feel nervous before exams.",
            exampleTranslation = "من قبل از امتحان‌ها می‌لرزم.",
            tags = listOf("common", "adjective", "B1"),
            personalNote = "",
            usedProvider = "local-dictionary"
        ),
        "proud" to WordAiResult(
            word = "proud",
            meanings = listOf(
                WordMeaning("feeling deep pleasure from achievements", "en"),
                WordMeaning("dignified", "en")
            ),
            pronunciation = "/praʊd/",
            wordType = "Adjective",
            level = "A2",
            synonyms = listOf("triumphant", "accomplished", "self-satisfied"),
            antonyms = listOf("ashamed", "unhappy", "mortified"),
            wordFamily = listOf("proud", "proudly", "proudness", "pride"),
            example = "She is proud of her achievements.",
            exampleTranslation = "او از موفقیت‌هایش غرور دارد.",
            tags = listOf("common", "adjective", "A2"),
            personalNote = "",
            usedProvider = "local-dictionary"
        ),
        "grateful" to WordAiResult(
            word = "grateful",
            meanings = listOf(
                WordMeaning("feeling or showing appreciation", "en"),
                WordMeaning("thankful", "en")
            ),
            pronunciation = "/ˈɡrætəfəl/",
            wordType = "Adjective",
            level = "B1",
            synonyms = listOf("thankful", "appreciative", "indebted"),
            antonyms = listOf("ungrateful", "thankless"),
            wordFamily = listOf("grateful", "gratefully", "gratefulness", "gratitude"),
            example = "I am grateful for your help.",
            exampleTranslation = "من از کمک شما سپاسگزارم.",
            tags = listOf("common", "adjective", "B1"),
            personalNote = "",
            usedProvider = "local-dictionary"
        ),
        "frustrated" to WordAiResult(
            word = "frustrated",
            meanings = listOf(
                WordMeaning("feeling annoyed or thwarted", "en"),
                WordMeaning("discontented", "en")
            ),
            pronunciation = "/frʌˈstreɪtɪd/",
            wordType = "Adjective",
            level = "B1",
            synonyms = listOf("annoyed", "irritated", "disappointed", "strained"),
            antonyms = listOf("satisfied", "content", "relieved"),
            wordFamily = listOf("frustrated", "frustrating", "frustration", "frustrate"),
            example = "He felt frustrated with the problem.",
            exampleTranslation = "او با مشکل ناامید بود.",
            tags = listOf("common", "adjective", "B1"),
            personalNote = "",
            usedProvider = "local-dictionary"
        ),
        "confused" to WordAiResult(
            word = "confused",
            meanings = listOf(
                WordMeaning("unable to think clearly", "en"),
                WordMeaning("bewildered", "en")
            ),
            pronunciation = "/kənˈfjuːzd/",
            wordType = "Adjective",
            level = "A2",
            synonyms = listOf("perplexed", "baffled", "puzzled", "mystified"),
            antonyms = listOf("clear", "certain", "sure"),
            wordFamily = listOf("confused", "confusing", "confusion", "confuse"),
            example = "I am confused by this grammar rule.",
            exampleTranslation = "من با این قوانین گرامری سردرگم هستم.",
            tags = listOf("common", "adjective", "A2"),
            personalNote = "",
            usedProvider = "local-dictionary"
        ),
        "surprised" to WordAiResult(
            word = "surprised",
            meanings = listOf(
                WordMeaning("feeling wonder or astonishment", "en"),
                WordMeaning("startled", "en")
            ),
            pronunciation = "/sərˈpraɪzd/",
            wordType = "Adjective",
            level = "A2",
            synonyms = listOf("astonished", "amazed", "startled", "shocked"),
            antonyms = listOf("unperturbed", "unimpressed", "indifferent"),
            wordFamily = listOf("surprised", "surprising", "surprise", "surprisingly"),
            example = "I was surprised by the result.",
            exampleTranslation = "من از نتیجه شگفت‌زده شدم.",
            tags = listOf("common", "adjective", "A2"),
            personalNote = "",
            usedProvider = "local-dictionary"
        ),
        "relaxed" to WordAiResult(
            word = "relaxed",
            meanings = listOf(
                WordMeaning("free from tension and anxiety", "en"),
                WordMeaning("calm", "en")
            ),
            pronunciation = "/rɪˈlækst/",
            wordType = "Adjective",
            level = "A2",
            synonyms = listOf("calm", "unworried", "composed", "at ease"),
            antonyms = listOf("tense", "anxious", "stressed"),
            wordFamily = listOf("relaxed", "relaxing", "relaxation", "relax"),
            example = "I feel relaxed after a walk.",
            exampleTranslation = "من بعد از پیاده‌روی آرام احساس می‌کنم.",
            tags = listOf("common", "adjective", "A2"),
            personalNote = "",
            usedProvider = "local-dictionary"
        ),
        "stressed" to WordAiResult(
            word = "stressed",
            meanings = listOf(
                WordMeaning("suffering from mental or emotional strain", "en"),
                WordMeaning("anxious", "en")
            ),
            pronunciation = "/strɛst/",
            wordType = "Adjective",
            level = "B1",
            synonyms = listOf("anxious", "tense", "overwhelmed", "strained"),
            antonyms = listOf("relaxed", "calm", "composed"),
            wordFamily = listOf("stressed", "stressful", "stress", "stressfully"),
            example = "I am stressed about the deadline.",
            exampleTranslation = "من از سرmartingale نگرانم.",
            tags = listOf("common", "adjective", "B1"),
            personalNote = "",
            usedProvider = "local-dictionary"
        ),
        "grateful" to WordAiResult(
            word = "grateful",
            meanings = listOf(
                WordMeaning("feeling or showing appreciation", "en"),
                WordMeaning("thankful", "en")
            ),
            pronunciation = "/ˈɡrætəfəl/",
            wordType = "Adjective",
            level = "B1",
            synonyms = listOf("thankful", "appreciative", "indebted"),
            antonyms = listOf("ungrateful", "thankless"),
            wordFamily = listOf("grateful", "gratefully", "gratefulness", "gratitude"),
            example = "I am grateful for your help.",
            exampleTranslation = "من از کمک شما سپاسگزارم.",
            tags = listOf("common", "adjective", "B1"),
            personalNote = "",
            usedProvider = "local-dictionary"
        ),
        "curious" to WordAiResult(
            word = "curious",
            meanings = listOf(
                WordMeaning("eager to know or learn something", "en"),
                WordMeaning("inquisitive", "en")
            ),
            pronunciation = "/ˈkjʊriəs/",
            wordType = "Adjective",
            level = "B1",
            synonyms = listOf("inquisitive", "interested", "questioning", "probing"),
            antonyms = listOf("indifferent", "uninterested", "apathetic"),
            wordFamily = listOf("curious", "curiosity", "curiously", "curiousness"),
            example = "I am curious about your culture.",
            exampleTranslation = "من درباره فرهنگ شما کنجکاو هستم.",
            tags = listOf("common", "adjective", "B1"),
            personalNote = "",
            usedProvider = "local-dictionary"
        ),
        "interested" to WordAiResult(
            word = "interested",
            meanings = listOf(
                WordMeaning("showing curiosity or concern", "en"),
                WordMeaning("engaged", "en")
            ),
            pronunciation = "/ˈɪntrəstɪd/",
            wordType = "Adjective",
            level = "A2",
            synonyms = listOf("engaged", "involved", "concerned", "curious"),
            antonyms = listOf("uninterested", "disinterested", "bored"),
            wordFamily = listOf("interested", "interesting", "interest", "interestingly"),
            example = "I am interested in learning English.",
            exampleTranslation = "من علاقه‌مند به یادگیری انگلیسی هستم.",
            tags = listOf("common", "adjective", "A2"),
            personalNote = "",
            usedProvider = "local-dictionary"
        ),
        "motivated" to WordAiResult(
            word = "motivated",
            meanings = listOf(
                WordMeaning("enthusiastic and determined", "en"),
                WordMeaning("driven", "en")
            ),
            pronunciation = "/ˈmoʊtɪveɪtɪd/",
            wordType = "Adjective",
            level = "B1",
            synonyms = listOf("driven", "determined", "ambitious", "eager"),
            antonyms = listOf("unmotivated", "unenthused", "apathetic"),
            wordFamily = listOf("motivated", "motivating", "motivation", "motivate"),
            example = "I am motivated to learn English.",
            exampleTranslation = "من برای یادگیری انگلیسی انگیزه‌مندم.",
            tags = listOf("common", "adjective", "B1"),
            personalNote = "",
            usedProvider = "local-dictionary"
        ),
        "prepared" to WordAiResult(
            word = "prepared",
            meanings = listOf(
                WordMeaning("made ready", "en"),
                WordMeaning("ready", "en")
            ),
            pronunciation = "/prɪˈpɛrd/",
            wordType = "Adjective",
            level = "A2",
            synonyms = listOf("ready", "set", "primed", "prequalified"),
            antonyms = listOf("unprepared", "unready", "ill-prepared"),
            wordFamily = listOf("prepared", "preparing", "preparation", "prepare"),
            example = "I am prepared for the test.",
            exampleTranslation = "من برای آزمون آماده‌ام.",
            tags = listOf("common", "adjective", "A2"),
            personalNote = "",
            usedProvider = "local-dictionary"
        ),
        "confident" to WordAiResult(
            word = "confident",
            meanings = listOf(
                WordMeaning("having strong belief in oneself", "en"),
                WordMeaning("assured", "en")
            ),
            pronunciation = "/ˈkɒnfɪdənt/",
            wordType = "Adjective",
            level = "B1",
            synonyms = listOf("assured", "self-assured", "certain", "positive"),
            antonyms = listOf("diffident", "insecure", "uncertain"),
            wordFamily = listOf("confident", "confidently", "confidence", "confidentness"),
            example = "She is confident about the exam.",
            exampleTranslation = "او برای امتحان اطمینان دارد.",
            tags = listOf("common", "adjective", "B1"),
            personalNote = "",
            usedProvider = "local-dictionary"
        ),
        "determined" to WordAiResult(
            word = "determined",
            meanings = listOf(
                WordMeaning("having made a firm decision", "en"),
                WordMeaning("resolute", "en")
            ),
            pronunciation = "/dɪˈtɜːrmɪnd/",
            wordType = "Adjective",
            level = "B1",
            synonyms = listOf("resolute", " steadfast", "unwavering", "firm"),
            antonyms = listOf("undetermined", "indecisive", "wavering"),
            wordFamily = listOf("determined", "determining", "determination", "determine"),
            example = "She is determined to succeed.",
            exampleTranslation = "او تعیین شده که موفق شود.",
            tags = listOf("common", "adjective", "B1"),
            personalNote = "",
            usedProvider = "local-dictionary"
        ),
        "bilingual" to WordAiResult(
            word = "bilingual",
            meanings = listOf(
                WordMeaning("able to speak two languages", "en"),
                WordMeaning("two-language", "en")
            ),
            pronunciation = "/baɪˈlɪŋɡwəl/",
            wordType = "Adjective",
            level = "B2",
            synonyms = listOf("two-lingual", "dual-language", "bicultural"),
            antonyms = listOf("monolingual"),
            wordFamily = listOf("bilingual", "bilingualism", "bilingualist"),
            example = "She is bilingual in English and Persian.",
            exampleTranslation = "او دوزبانه انگلیسی و فارسی است.",
            tags = listOf("language", "adjective", "B2"),
            personalNote = "",
            usedProvider = "local-dictionary"
        ),
        "vocabulary" to WordAiResult(
            word = "vocabulary",
            meanings = listOf(
                WordMeaning("the set of words known to a person", "en"),
                WordMeaning("lexicon", "en")
            ),
            pronunciation = "/vəˈkæbjələri/",
            wordType = "Noun",
            level = "A2",
            synonyms = listOf("lexicon", "word bank", "language inventory"),
            antonyms = listOf("ignorance", "illiteracy"),
            wordFamily = listOf("vocabulary", "vocabularies", "vocabularies"),
            example = "I am building my vocabulary.",
            exampleTranslation = "من دایرکت واژگانم را بنا می‌زنم.",
            tags = listOf("language", "noun", "A2"),
            personalNote = "",
            usedProvider = "local-dictionary"
        ),
        "pronounce" to WordAiResult(
            word = "pronounce",
            meanings = listOf(
                WordMeaning("to articulate a word", "en"),
                WordMeaning("to speak aloud", "en")
            ),
            pronunciation = "/prəˈnaʊns/",
            wordType = "Verb",
            level = "A2",
            synonyms = listOf("say", "speak", "utter", "voice"),
            antonyms = listOf("write", "spell", "mumble"),
            wordFamily = listOf("pronounce", "pronouncing", "pronounced", "pronunciation"),
            example = "How do you pronounce this word?",
            exampleTranslation = "این کلمه را چطور صحبت می‌کنید؟",
            tags = listOf("language", "verb", "A2"),
            personalNote = "",
            usedProvider = "local-dictionary"
        ),
        "define" to WordAiResult(
            word = "define",
            meanings = listOf(
                WordMeaning("to state the meaning of", "en"),
                WordMeaning("to explain", "en")
            ),
            pronunciation = "/dɪˈfaɪn/",
            wordType = "Verb",
            level = "A2",
            synonyms = listOf("explain", "describe", "clarify", "interpret"),
            antonyms = listOf("confuse", "obscure", "mislead"),
            wordFamily = listOf("define", "defining", "defined", "definition"),
            example = "Can you define this word?",
            exampleTranslation = "آیا می‌توانی این کلمه را تعریف کنی؟",
            tags = listOf("language", "verb", "A2"),
            personalNote = "",
            usedProvider = "local-dictionary"
        ),
        "translate" to WordAiResult(
            word = "translate",
            meanings = listOf(
                WordMeaning("to convert text from one language to another", "en"),
                WordMeaning("to interpret", "en")
            ),
            pronunciation = "/trænzˈleɪt/",
            wordType = "Verb",
            level = "A2",
            synonyms = listOf("interpret", "transcribe", "render", "transliterate"),
            antonyms = listOf("write", "speak"),
            wordFamily = listOf("translate", "translating", "translated", "translation"),
            example = "Can you translate this sentence?",
            exampleTranslation = "آیا می‌توانی این جمله را ترجمه کنی؟",
            tags = listOf("language", "verb", "A2"),
            personalNote = "",
            usedProvider = "local-dictionary"
        ),
        "study" to WordAiResult(
            word = "study",
            meanings = listOf(
                WordMeaning("to learn about a subject", "en"),
                WordMeaning("to examine", "en")
            ),
            pronunciation = "/ˈstʌdi/",
            wordType = "Verb",
            level = "A1",
            synonyms = listOf("learn", "examine", "research", "read"),
            antonyms = listOf("ignore", "neglect", "forgo"),
            wordFamily = listOf("study", "studying", "studied", "studious"),
            example = "I study English every day.",
            exampleTranslation = "من هر روز انگلیسی مطالعه می‌کنم.",
            tags = listOf("common", "verb", "A1"),
            personalNote = "",
            usedProvider = "local-dictionary"
        ),
        "understand" to WordAiResult(
            word = "understand",
            meanings = listOf(
                WordMeaning("to know the meaning", "en"),
                WordMeaning("to comprehend", "en")
            ),
            pronunciation = "/ˌʌndərˈstænd/",
            wordType = "Verb",
            level = "A1",
            synonyms = listOf("comprehend", "grasp", "realize", "apprehend"),
            antonyms = listOf("misunderstand", "confuse", "perplex"),
            wordFamily = listOf("understand", "understanding", "understood", "understandable"),
            example = "I understand the lesson.",
            exampleTranslation = "من درس را می‌فهمم.",
            tags = listOf("common", "verb", "A1"),
            personalNote = "",
            usedProvider = "local-dictionary"
        ),
        "remember" to WordAiResult(
            word = "remember",
            meanings = listOf(
                WordMeaning("to retain knowledge", "en"),
                WordMeaning("to recall", "en")
            ),
            pronunciation = "/rɪˈmɛmbər/",
            wordType = "Verb",
            level = "A1",
            synonyms = listOf("recall", "recollect", "remind", "mind"),
            antonyms = listOf("forget", "ignore", "overlook"),
            wordFamily = listOf("remember", "remembering", "remembered", "memorable"),
            example = "I remember the word now.",
            exampleTranslation = "من حالا کلمه را به یاد می‌آورم.",
            tags = listOf("common", "verb", "A1"),
            personalNote = "",
            usedProvider = "local-dictionary"
        ),
        "forget" to WordAiResult(
            word = "forget",
            meanings = listOf(
                WordMeaning("to fail to remember", "en"),
                WordMeaning("to overlook", "en")
            ),
            pronunciation = "/fərˈɡɛt/",
            wordType = "Verb",
            level = "A1",
            synonyms = listOf("overlook", "neglect", "ignore"),
            antonyms = listOf("remember", "recall", "recollect"),
            wordFamily = listOf("forget", "forgetting", "forgotten", "forgetful"),
            example = "I forget the meaning sometimes.",
            exampleTranslation = "من گاهی معنی را فراموش می‌کنم.",
            tags = listOf("common", "verb", "A1"),
            personalNote = "",
            usedProvider = "local-dictionary"
        )
    )

    fun getWord(word: String): WordAiResult? = dictionary[word.lowercase()]
}
