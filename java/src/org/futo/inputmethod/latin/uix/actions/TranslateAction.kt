package org.futo.inputmethod.latin.uix.actions

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.futo.inputmethod.latin.R
import org.futo.inputmethod.latin.uix.Action
import org.futo.inputmethod.latin.uix.ActionTextEditor
import org.futo.inputmethod.latin.uix.ActionWindow
import org.futo.inputmethod.latin.uix.LocalKeyboardScheme

@Composable
internal fun TranslateContents(
    sourceText: MutableState<String>,
    translatedText: MutableState<String>,
    isDeToEn: MutableState<Boolean>,
    statusMessage: MutableState<String>,
    onInsert: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(sourceText.value, isDeToEn.value) {
        val text = sourceText.value
        if (text.isBlank()) {
            translatedText.value = ""
            return@LaunchedEffect
        }

        statusMessage.value = context.getString(R.string.action_translate_translating)
        delay(300L) // debounce typing

        if (sourceText.value != text) return@LaunchedEffect

        val options = TranslatorOptions.Builder()
            .setSourceLanguage(
                if (isDeToEn.value) TranslateLanguage.GERMAN else TranslateLanguage.ENGLISH
            )
            .setTargetLanguage(
                if (isDeToEn.value) TranslateLanguage.ENGLISH else TranslateLanguage.GERMAN
            )
            .build()

        val translator = Translation.getClient(options)

        try {
            withContext(Dispatchers.IO) {
                val conditions = DownloadConditions.Builder().build()
                com.google.android.gms.tasks.Tasks.await(
                    translator.downloadModelIfNeeded(conditions)
                )
            }
        } catch (_: Exception) {
            statusMessage.value = context.getString(R.string.action_translate_error_model)
            translator.close()
            return@LaunchedEffect
        }

        if (sourceText.value != text) {
            translator.close()
            return@LaunchedEffect
        }

        try {
            val result = withContext(Dispatchers.IO) {
                com.google.android.gms.tasks.Tasks.await(translator.translate(text))
            }
            if (sourceText.value == text) {
                translatedText.value = result
                statusMessage.value = ""
            }
        } catch (_: Exception) {
            statusMessage.value = context.getString(R.string.action_translate_error_model)
        } finally {
            translator.close()
        }
    }

    Column(Modifier.padding(8.dp)) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isDeToEn.value) "DE → EN" else "EN → DE",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.weight(1f)
            )
            OutlinedButton(
                onClick = { isDeToEn.value = !isDeToEn.value },
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text("⇄", fontSize = 18.sp)
            }
        }

        Spacer(Modifier.height(4.dp))

        Surface(
            color = LocalKeyboardScheme.current.surface,
            border = BorderStroke(1.dp, SolidColor(LocalKeyboardScheme.current.outline)),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(
                    (with(LocalDensity.current) {
                        (18.sp.toDp()) * (sourceText.value.count { it == '\n' } + 1)
                    } + 16.dp).coerceIn(48.dp, 96.dp)
                ),
        ) {
            androidx.compose.foundation.layout.Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(8.dp)
            ) {
                ActionTextEditor(sourceText, multiline = true)
            }
        }

        Spacer(Modifier.height(4.dp))

        if (statusMessage.value.isNotEmpty()) {
            Text(
                text = statusMessage.value,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }

        if (translatedText.value.isNotEmpty()) {
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = translatedText.value,
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            Spacer(Modifier.height(4.dp))

            Row(Modifier.fillMaxWidth()) {
                Spacer(Modifier.weight(1f))
                Button(
                    onClick = onInsert,
                    enabled = translatedText.value.isNotBlank(),
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                    modifier = Modifier.height(40.dp)
                ) {
                    Icon(
                        painterResource(R.drawable.check),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(stringResource(R.string.action_translate_insert))
                }
            }
        } else if (sourceText.value.isBlank()) {
            Text(
                text = stringResource(R.string.action_translate_no_text),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}

val TranslateAction = Action(
    icon = R.drawable.translate,
    name = R.string.action_translate_title,
    simplePressImpl = null,
    canShowKeyboard = true,
    windowImpl = { manager, _ ->
        val sourceText = mutableStateOf("")
        val translatedText = mutableStateOf("")
        val isDeToEn = mutableStateOf(true)
        val statusMessage = mutableStateOf("")

        object : ActionWindow() {
            override val showCloseButton: Boolean get() = true

            override val positionIsUserManagable: Boolean get() = false

            @Composable
            override fun windowName(): String =
                stringResource(R.string.action_translate_title)

            @Composable
            override fun WindowContents(keyboardShown: Boolean) {
                TranslateContents(
                    sourceText = sourceText,
                    translatedText = translatedText,
                    isDeToEn = isDeToEn,
                    statusMessage = statusMessage,
                    onInsert = {
                        if (translatedText.value.isNotBlank()) {
                            manager.typeText(translatedText.value)
                            sourceText.value = ""
                            translatedText.value = ""
                            manager.closeActionWindow()
                        }
                    }
                )
            }
        }
    }
)
