package com.lexora.app.ui.navigation

sealed class Screen(val route: String) {
        object Home : Screen("home")
    object Learn : Screen("learn")
    object Add : Screen("add")
    object Statistics : Screen("statistics")
    object Settings : Screen("settings")

        object Vocabulary : Screen("vocabulary")
    object Grammar : Screen("grammar")
    object Notes : Screen("notes")
    object Review : Screen("review")
    object Mistakes : Screen("mistakes")

        object WordDetail : Screen("word/{wordId}") {
        fun createRoute(wordId: Long) = "word/$wordId"
    }
    object GrammarDetail : Screen("grammar/{grammarId}") {
        fun createRoute(grammarId: Long) = "grammar/$grammarId"
    }
    object NoteDetail : Screen("note/{noteId}") {
        fun createRoute(noteId: Long) = "note/$noteId"
    }

        object AddWord : Screen("add_word")
    object EditWord : Screen("edit_word/{wordId}") {
        fun createRoute(wordId: Long) = "edit_word/$wordId"
    }
    object AddGrammar : Screen("add_grammar")
    object EditGrammar : Screen("edit_grammar/{grammarId}") {
        fun createRoute(grammarId: Long) = "edit_grammar/$grammarId"
    }
    object AddNote : Screen("add_note")
    object EditNote : Screen("edit_note/{noteId}") {
        fun createRoute(noteId: Long) = "note/$noteId"
    }

        object Favorites : Screen("favorites")

        object Search : Screen("search")

        object Dictionary : Screen("dictionary")
    object DictionaryDetail : Screen("dictionary_detail/{entryId}") {
        fun createRoute(entryId: Long) = "dictionary_detail/$entryId"
    }

        object LeitnerReview : Screen("leitner_review")

        object Quiz : Screen("quiz")
}
