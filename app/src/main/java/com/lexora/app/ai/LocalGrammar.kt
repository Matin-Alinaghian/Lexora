package com.lexora.app.ai

import com.lexora.app.ai.model.GrammarAiResult
import com.lexora.app.ai.model.GrammarExample

object LocalGrammar {

    private val grammarRules: Map<String, GrammarAiResult> = mapOf(
        "present simple" to GrammarAiResult(
            title = "Present Simple",
            explanation = "The present simple tense is used for habits, general truths, and daily routines.",
            positiveForm = "Subject + Verb (add -s/-es for he/she/it) + Object",
            negativeForm = "Subject + do/does + not + Verb",
            questionForm = "Do/Does + Subject + Verb?",
            examples = listOf(
                GrammarExample("I play football every weekend.", "من هر هفته فوتبال بازی می‌کنم."),
                GrammarExample("She works in an office.", "او در یک دفتر کار می‌کند."),
                GrammarExample("They don't like coffee.", "آن‌ها قهوه را دوست ندارند."),
                GrammarExample("Does he speak English?", "آیا او انگلیسی صحبت می‌کند؟")
            ),
            tips = "Add -s or -es to the verb for he/she/it in positive sentences. Use 'do/does' for negative and question forms.",
            category = "Tenses",
            usedProvider = "local-grammar"
        ),
        "present continuous" to GrammarAiResult(
            title = "Present Continuous",
            explanation = "The present continuous tense is used for actions happening right now or temporary situations.",
            positiveForm = "Subject + am/is/are + Verb-ing",
            negativeForm = "Subject + am/is/are + not + Verb-ing",
            questionForm = "Am/Is/Are + Subject + Verb-ing?",
            examples = listOf(
                GrammarExample("I am studying English now.", "من امروز انگلیسی در حال مطالعه‌ام."),
                GrammarExample("She is reading a book.", "او در حال خواندن کتاب است."),
                GrammarExample("They are not watching TV.", "آن‌ها در حال تماشای تلویزیون نیستند."),
                GrammarExample("Are you coming?", "آیا تو می‌آیی؟")
            ),
            tips = "Use the -ing form of the verb. Remember: I am, You/We/They are, He/She/It is.",
            category = "Tenses",
            usedProvider = "local-grammar"
        ),
        "past simple" to GrammarAiResult(
            title = "Past Simple",
            explanation = "The past simple tense is used for completed actions in the past.",
            positiveForm = "Subject + Verb-ed (or irregular form)",
            negativeForm = "Subject + did + not + Verb (base form)",
            questionForm = "Did + Subject + Verb (base form)?",
            examples = listOf(
                GrammarExample("I watched a movie yesterday.", "من دیروز یک فیلم دیدم."),
                GrammarExample("She went to Tehran last week.", "او هفته‌ی گذشته به تهران رفت."),
                GrammarExample("They didn't like the food.", "آن‌ها غذای را دوست نداشتند."),
                GrammarExample("Did you call him?", "آیا تو او را تماس گرفتی؟")
            ),
            tips = "Use the past form of the verb (regular: +ed; irregular: memorize). Use 'did' for negative and question.",
            category = "Tenses",
            usedProvider = "local-grammar"
        ),
        "future simple" to GrammarAiResult(
            title = "Future Simple",
            explanation = "The future simple tense is used for predictions, promises, and decisions made now.",
            positiveForm = "Subject + will + Verb (base form)",
            negativeForm = "Subject + will + not + Verb (base form)",
            questionForm = "Will + Subject + Verb (base form)?",
            examples = listOf(
                GrammarExample("I will travel to Paris next year.", "من سال آینده به پاریس سفر خواهم کرد."),
                GrammarExample("She will call you tomorrow.", "او فردا به تو تلفن می‌خواند."),
                GrammarExample("They won't be late.", "آن‌ها دیر نخواهند رسید."),
                GrammarExample("Will it rain?", "آیا بار 인도네시아؟")
            ),
            tips = "Use 'will' + base verb. 'Going to' is also used for plans and predictions with evidence.",
            category = "Tenses",
            usedProvider = "local-grammar"
        ),
        "present perfect" to GrammarAiResult(
            title = "Present Perfect",
            explanation = "The present perfect tense is used for actions that happened at an unspecified time or have relevance to the present.",
            positiveForm = "Subject + have/has + Past Participle",
            negativeForm = "Subject + have/has + not + Past Participle",
            questionForm = "Have/Has + Subject + Past Participle?",
            examples = listOf(
                GrammarExample("I have finished my homework.", "من تکالیفم را تمام کرده‌ام."),
                GrammarExample("She has visited Paris.", "او به پاریس سفر کرده است."),
                GrammarExample("They haven't seen that movie.", "آن‌ها آن فیلم را ندیده‌اند."),
                GrammarExample("Have you eaten lunch?", "آیا ناهار خوردی؟")
            ),
            tips = "Use have/has + past participle. The exact time is not important. Use 'since' and 'for' to talk about duration.",
            category = "Tenses",
            usedProvider = "local-grammar"
        ),
        "present perfect continuous" to GrammarAiResult(
            title = "Present Perfect Continuous",
            explanation = "The present perfect continuous tense is used for actions that started in the past and continue to the present, or have just finished with a visible result.",
            positiveForm = "Subject + have/has + been + Verb-ing",
            negativeForm = "Subject + have/has + not + been + Verb-ing",
            questionForm = "Have/Has + Subject + been + Verb-ing?",
            examples = listOf(
                GrammarExample("I have been studying for two hours.", "من دو ساعتбя در حال مطالعه‌ام."),
                GrammarExample("She has been working here since January.", "او از ژانویه کار می‌کند."),
                GrammarExample("They haven't been sleeping well.", "آن‌ها خواب خوبی نداشته‌اند."),
                GrammarExample("Have you been waiting long?", "آیا lâu出ا صبر کرده‌ای؟")
            ),
            tips = "Use have/has + been + verb-ing. Focus on the duration or recent activity.",
            category = "Tenses",
            usedProvider = "local-grammar"
        ),
        "past continuous" to GrammarAiResult(
            title = "Past Continuous",
            explanation = "The past continuous tense is used for actions that were happening at a specific time in the past or were interrupted by another action.",
            positiveForm = "Subject + was/were + Verb-ing",
            negativeForm = "Subject + was/were + not + Verb-ing",
            questionForm = "Was/Were + Subject + Verb-ing?",
            examples = listOf(
                GrammarExample("I was reading when you called.", "وقتی تماس گرفتی من در حال خواندن کتاب بودم."),
                GrammarExample("They were playing football at 5 PM.", "آن‌ها ساعت 5 بعدازظهر فوتبال بازی می‌کردند."),
                GrammarExample("She wasn't listening.", "او در حال گوش دادن نبود."),
                GrammarExample("What were you doing?", "تو کار می‌کردی؟")
            ),
            tips = "Use was/were + verb-ing. Often used with 'when' and 'while'.",
            category = "Tenses",
            usedProvider = "local-grammar"
        ),
        "future continuous" to GrammarAiResult(
            title = "Future Continuous",
            explanation = "The future continuous tense is used for actions that will be in progress at a specific time in the future.",
            positiveForm = "Subject + will + be + Verb-ing",
            negativeForm = "Subject + will + not + be + Verb-ing",
            questionForm = "Will + Subject + be + Verb-ing?",
            examples = listOf(
                GrammarExample("I will be studying at 8 PM.", "من ساعت 8 ش am夜在 در حال مطالعه‌ام."),
                GrammarExample("She will be flying to London tomorrow.", "او فردا به لندن پرواز می‌کند."),
                GrammarExample("They won't be waiting long.", "آن‌ها برای مدت طولانی صبر نخواهند کرد."),
                GrammarExample("Will you be coming?", "آیا تو می‌آیی؟")
            ),
            tips = "Use will + be + verb-ing. Think of an action in progress in the future.",
            category = "Tenses",
            usedProvider = "local-grammar"
        ),
        "modals" to GrammarAiResult(
            title = "Modal Verbs",
            explanation = "Modal verbs express ability, permission, obligation, possibility, and advice. They don't change form and are followed by the base verb.",
            positiveForm = "Subject + Modal + Base Verb",
            negativeForm = "Subject + Modal + not + Base Verb",
            questionForm = "Modal + Subject + Base Verb?",
            examples = listOf(
                GrammarExample("I can speak English.", "من می‌توانم انگلیسی صحبت کنم."),
                GrammarExample("You must study for the exam.", "بخواهی برای امتحان مطالعه کنی."),
                GrammarExample("She should eat healthier.", "بهتر است او سالم‌تری بخورد."),
                GrammarExample("May I leave?", "آیا می‌توانم بروم؟")
            ),
            tips = "Common modals: can, could, may, might, must, should, will, would. No -s, -ed, -ing. Always use base verb after modal.",
            category = "Modal Verbs",
            usedProvider = "local-grammar"
        ),
        "conditionals" to GrammarAiResult(
            title = "Conditionals",
            explanation = "Conditionals express real or hypothetical situations and their results. There are four main types.",
            positiveForm = "If + Condition, Result",
            negativeForm = "If + Negative Condition, Negative Result",
            questionForm = "If + Condition, Result? (in context)",
            examples = listOf(
                GrammarExample("If it rains, I will stay home.", "اگر بارش ببار انگلیس، من در خانه خواهم ماند."),
                GrammarExample("If I had money, I would buy a car.", "اگر پول داشتم، ماشین می‌خرမ적이었다."),
                GrammarExample("If she studies, she will pass.", "اگر او مطالعه کند، موفق خواهد شد."),
                GrammarExample("If they had left earlier, they wouldn't have missed the bus.", "اگر زودتر رفته بودند، اتوبوس را از دست نخواهند داد.")
            ),
            tips = "First conditional = real possibility. Second = hypothetical present/future. Third = hypothetical past. Zero = general truth.",
            category = "Conditionals",
            usedProvider = "local-grammar"
        ),
        "prepositions" to GrammarAiResult(
            title = "Prepositions",
            explanation = "Prepositions show relationships between words — time, place, direction, etc.",
            positiveForm = "Noun + Preposition + Noun",
            negativeForm = "Not applicable in the same way",
            questionForm = "Where/When + Subject + Verb + Preposition?",
            examples = listOf(
                GrammarExample("The book is on the table.", "کتاب روی میز است."),
                GrammarExample("I will see you at 5 PM.", "من تو ساعت 5 بعدازظهر می‌بینمت."),
                GrammarExample("She is from Tehran.", "او از تهران است."),
                GrammarExample("We talked about the exam.", "ما درباره امتحان صحبت کردیم.")
            ),
            tips = "Common prepositions: in, on, at, for, since, to, from, with, without, about, of, by, into, onto, over, under, between, among.",
            category = "Prepositions",
            usedProvider = "local-grammar"
        ),
        "articles" to GrammarAiResult(
            title = "Articles",
            explanation = "Articles (a, an, the) are used to specify nouns. 'A' and 'an' are indefinite, 'the' is definite.",
            positiveForm = "Article + Noun",
            negativeForm = "No article (for plural/uncountable general nouns)",
            questionForm = "Which article should I use?",
            examples = listOf(
                GrammarExample("I saw a dog.", "من یک سگ دیدم."),
                GrammarExample("An apple a day keeps the doctor away.", "هر روز یک سیب Dispensery را دور بماند."),
                GrammarExample("The car is fast.", "ماشین سریع است."),
                GrammarExample("Water is essential.", "آب ضروری است.")
            ),
            tips = "Use 'a' before consonant sounds, 'an' before vowel sounds. Use 'the' for specific nouns. No article for general plural/uncountable nouns.",
            category = "Articles",
            usedProvider = "local-grammar"
        ),
        "countable uncountable" to GrammarAiResult(
            title = "Countable vs Uncountable Nouns",
            explanation = "Countable nouns can be counted (one, two, three...) and can be singular or plural. Uncountable nouns cannot be counted individually.",
            positiveForm = "Countable: a/an + singular, numbers + plural | Uncountable: some/any + noun",
            negativeForm = "Countable: not + a/an + singular | Uncountable: not + any + noun",
            questionForm = "Is/Are + Noun + countable/uncountable?",
            examples = listOf(
                GrammarExample("I have a book. (countable)", "من یک کتاب دارم."),
                GrammarExample("I have some water. (uncountable)", "من کمی آب دارم."),
                GrammarExample("How many apples do you have?", "چند سیب داری؟"),
                GrammarExample("How much milk do you drink?", "چقدر شیر می‌خوری؟")
            ),
            tips = "Countable: a, an, one, two, many, few, number. Uncountable: some, any, much, little, amount. 'How many' for countable, 'How much' for uncountable.",
            category = "Nouns",
            usedProvider = "local-grammar"
        ),
        "comparatives" to GrammarAiResult(
            title = "Comparatives and Superlatives",
            explanation = "Comparatives compare two things, superlatives compare one thing to a group.",
            positiveForm = "Comparative: Adjective + -er / more + adjective | Superlative: the + -est / the most + adjective",
            negativeForm = "Not + comparative / Not + superlative",
            questionForm = "Which is + comparative/superlative?",
            examples = listOf(
                GrammarExample("She is taller than me.", "او از من بلندتر است."),
                GrammarExample("This is the best book.", "این بهترین کتاب است."),
                GrammarExample("He runs faster than me.", "او از من سریع‌تر می‌دوید."),
                GrammarExample("She is the most beautiful.", "او زیباترین است.")
            ),
            tips = "Short adjectives (1-2 syllables): add -er/-est. Long adjectives: use more/most. Irregular: good/better/best, bad/worse/worst.",
            category = "Adjectives",
            usedProvider = "local-grammar"
        ),
        "reflexive pronouns" to GrammarAiResult(
            title = "Reflexive Pronouns",
            explanation = "Reflexive pronouns (myself, yourself, himself, herself, itself, ourselves, yourselves, themselves) refer back to the subject of the sentence.",
            positiveForm = "Subject + Verb + Reflexive Pronoun",
            negativeForm = "Not applicable",
            questionForm = "Did + Subject + Verb + Reflexive Pronoun?",
            examples = listOf(
                GrammarExample("I taught myself English.", "من خودم انگلیسی را یاد گرفتم."),
                GrammarExample("She lives by herself.", "او به تنهایی زندگی می‌کند."),
                GrammarExample("They enjoyed themselves.", "آن‌ها از خودشان لذت بردند."),
                GrammarExample("He hurt himself.", "او به خودش آسیب زد.")
            ),
            tips = "Use reflexive pronouns when the subject and object are the same. 'By myself' = alone. 'Enjoy myself' = have fun.",
            category = "Pronouns",
            usedProvider = "local-grammar"
        ),
        "passive voice" to GrammarAiResult(
            title = "Passive Voice",
            explanation = "The passive voice is used when the focus is on the action, not the doer. It's formed with be + past participle.",
            positiveForm = "Subject + be (in correct tense) + Past Participle (+ by + agent)",
            negativeForm = "Subject + be (in correct tense) + not + Past Participle",
            questionForm = "Be (in correct tense) + Subject + Past Participle?",
            examples = listOf(
                GrammarExample("The book was written by him.", "کتاب توسط او نوشته شد."),
                GrammarExample("English is spoken here.", "اینجا انگلیسی صحبت می‌شود."),
                GrammarExample("The food is being cooked.", "غذا در حال پخت است."),
                GrammarExample("Has the letter been sent?", "آیا نامه ارسال شده است؟")
            ),
            tips = "Use be + past participle. The tense of 'be' changes. The agent (by...) is optional. Use passive when the doer is unknown or unimportant.",
            category = "Voice",
            usedProvider = "local-grammar"
        )
    )

    fun getGrammar(title: String): GrammarAiResult? = grammarRules[title.lowercase()]
}
