#!/bin/bash
# Prepares library dependencies for building the FUTO Keyboard fork.
# Downloads the VAD AAR and creates compilation stubs for the mozc AAR.
set -e

LIBS_DIR="$1"
ANDROID_JAR="$2"

mkdir -p "$LIBS_DIR"

# Download the real VAD WebRTC AAR (v2.0.2 matches the API used by the project)
echo "Downloading VAD AAR..."
curl -sL -o "$LIBS_DIR/vad-release.aar" \
  "https://github.com/gkonovalov/android-vad/releases/download/2.0.2/android-vad-v2.0.2-release.aar"

# Build mozc-release.aar from stub source files
# The real AAR is in a private GitLab repo; these stubs provide the compilation API surface
WORK=$(mktemp -d)
echo "Building mozc stubs in $WORK..."

# Download compile-time dependencies for stubs
curl -sL -o "$WORK/guava.jar" \
  "https://repo1.maven.org/maven2/com/google/guava/guava/33.4.8-android/guava-33.4.8-android.jar"

# --- Create Java stub source files ---

mkdir -p "$WORK/src/com/google/android/apps/inputmethod/libs/mozc/session"
cat > "$WORK/src/com/google/android/apps/inputmethod/libs/mozc/session/MozcJNI.java" << 'EOF'
package com.google.android.apps.inputmethod.libs.mozc.session;
public class MozcJNI {
    public static void load(String userProfileDir, String dictPath, long offset, long length) {}
}
EOF

mkdir -p "$WORK/src/org/futo/inputmethod/nativelib/mozc"
cat > "$WORK/src/org/futo/inputmethod/nativelib/mozc/MozcLog.java" << 'EOF'
package org.futo.inputmethod.nativelib.mozc;
public class MozcLog {
    public static boolean forceLoggable = false;
    public static void e(String msg) {}
    public static void d(String msg) {}
    public static void w(String msg) {}
}
EOF

mkdir -p "$WORK/src/org/futo/inputmethod/nativelib/mozc/keyboard"
cat > "$WORK/src/org/futo/inputmethod/nativelib/mozc/keyboard/Keyboard.java" << 'EOF'
package org.futo.inputmethod.nativelib.mozc.keyboard;
public class Keyboard {
    public enum KeyboardSpecification {
        SYMBOL_NUMBER, QWERTY_KANA, QWERTY_ALPHABET,
        TWELVE_KEY_TOGGLE_FLICK_ALPHABET, TWELVE_KEY_FLICK_ALPHABET,
        TWELVE_KEY_FLICK_KANA, TWELVE_KEY_TOGGLE_FLICK_KANA;
        public int compositionMode = 0;
    }
}
EOF

cat > "$WORK/src/org/futo/inputmethod/nativelib/mozc/KeycodeConverter.java" << 'EOF'
package org.futo.inputmethod.nativelib.mozc;
import android.view.KeyEvent;
import com.google.common.base.Optional;
import org.mozc.android.inputmethod.japanese.protobuf.ProtoCommands;
public abstract class KeycodeConverter {
    public static ProtoCommands.KeyEvent SPECIALKEY_SPACE;
    public static ProtoCommands.KeyEvent SPECIALKEY_VIRTUAL_ENTER;
    public static ProtoCommands.KeyEvent SPECIALKEY_BACKSPACE;
    public static ProtoCommands.KeyEvent SPECIALKEY_VIRTUAL_LEFT;
    public static ProtoCommands.KeyEvent SPECIALKEY_VIRTUAL_RIGHT;
    public static ProtoCommands.KeyEvent SPECIALKEY_UP;
    public static ProtoCommands.KeyEvent SPECIALKEY_DOWN;
    public interface KeyEventInterface {
        int getKeyCode();
        Optional<KeyEvent> getNativeEvent();
    }
    public static KeyEventInterface getKeyEventInterface(int keyCode) { return null; }
    public static KeyEventInterface getKeyEventInterface(KeyEvent event) { return null; }
    public static boolean isMetaKey(KeyEvent event) { return false; }
    public static ProtoCommands.KeyEvent getMozcKeyEvent(int codePoint) { return null; }
}
EOF

cat > "$WORK/src/org/futo/inputmethod/nativelib/mozc/MozcUtil.java" << 'EOF'
package org.futo.inputmethod.nativelib.mozc;
import android.content.res.Configuration;
import org.futo.inputmethod.nativelib.mozc.keyboard.Keyboard;
import org.mozc.android.inputmethod.japanese.protobuf.ProtoCommands;
public class MozcUtil {
    public static final int CURSOR_POSITION_TAIL = -1;
    public static final int CURSOR_POSITION_HEAD = 0;
    public static boolean isPasswordField(int inputType) { return false; }
    public static ProtoCommands.Request.Builder getRequestBuilder(
        Keyboard.KeyboardSpecification spec, Configuration config, int density) {
        return ProtoCommands.Request.newBuilder();
    }
}
EOF

mkdir -p "$WORK/src/org/futo/inputmethod/nativelib/mozc/model"
cat > "$WORK/src/org/futo/inputmethod/nativelib/mozc/model/SelectionTracker.java" << 'EOF'
package org.futo.inputmethod.nativelib.mozc.model;
import org.mozc.android.inputmethod.japanese.protobuf.ProtoCommands;
public class SelectionTracker {
    public static final int DO_NOTHING = 0;
    public static final int RESET_CONTEXT = 1;
    public SelectionTracker() {}
    public void onStartInput(int selStart, int selEnd, boolean isBatchMode) {}
    public void onFinishInput() {}
    public int onUpdateSelection(int oldStart, int oldEnd, int newStart, int newEnd,
        int composingStart, int composingEnd, boolean isBatchMode) { return DO_NOTHING; }
    public void onConfigurationChanged() {}
    public void onRender(ProtoCommands.DeletionRange deletionRange, String result,
        ProtoCommands.Preedit preedit) {}
    public int preeditStartPosition = 0;
    public int lastSelectionStart = 0;
    public int lastSelectionEnd = 0;
}
EOF

mkdir -p "$WORK/src/org/futo/inputmethod/nativelib/mozc/session"
cat > "$WORK/src/org/futo/inputmethod/nativelib/mozc/session/SessionHandler.java" << 'EOF'
package org.futo.inputmethod.nativelib.mozc.session;
public interface SessionHandler {}
EOF

cat > "$WORK/src/org/futo/inputmethod/nativelib/mozc/session/SessionHandlerFactory.java" << 'EOF'
package org.futo.inputmethod.nativelib.mozc.session;
import com.google.common.base.Optional;
public class SessionHandlerFactory {
    public SessionHandlerFactory(Optional<SessionHandler> handler) {}
}
EOF

cat > "$WORK/src/org/futo/inputmethod/nativelib/mozc/session/SessionExecutor.java" << 'EOF'
package org.futo.inputmethod.nativelib.mozc.session;
import android.content.Context;
import com.google.common.base.Optional;
import java.util.List;
import org.futo.inputmethod.nativelib.mozc.KeycodeConverter;
import org.mozc.android.inputmethod.japanese.protobuf.ProtoCommands;
import org.mozc.android.inputmethod.japanese.protobuf.ProtoConfig;
import org.mozc.android.inputmethod.japanese.protobuf.ProtoUserDictionaryStorage;
public class SessionExecutor {
    public interface EvaluationCallback {
        void onCompleted(Optional<ProtoCommands.Command> command,
            Optional<KeycodeConverter.KeyEventInterface> keyEvent);
    }
    public static SessionExecutor getInstanceInitializedIfNecessary(
        SessionHandlerFactory factory, Context context) { return new SessionExecutor(); }
    public static void setInstanceForTest(Optional<SessionExecutor> instance) {}
    public void reset(SessionHandlerFactory factory, Context context) {}
    public void setConfig(ProtoConfig.Config config) {}
    public ProtoConfig.Config getConfig() { return null; }
    public void setLogging(boolean enabled) {}
    public void syncData() {}
    public void removePendingEvaluations() {}
    public void resetContext() {}
    public void switchInputMode(Optional<KeycodeConverter.KeyEventInterface> keyEvent,
        int mode, EvaluationCallback callback) {}
    public void updateRequest(ProtoCommands.Request request,
        List<ProtoCommands.Input.TouchEvent> touchEvents) {}
    public void switchInputFieldType(ProtoCommands.Context.InputFieldType fieldType) {}
    public void moveCursor(int offset, EvaluationCallback callback) {}
    public void sendKey(ProtoCommands.KeyEvent keyEvent, KeycodeConverter.KeyEventInterface keyEventInterface,
        List<ProtoCommands.Input.TouchEvent> touchEvents, EvaluationCallback callback) {}
    public void submitCandidate(int candidateId, Optional<Integer> rowIdx,
        EvaluationCallback callback) {}
    public void undoOrRewind(List<ProtoCommands.Input.TouchEvent> touchEvents,
        EvaluationCallback callback) {}
    public ProtoUserDictionaryStorage.UserDictionaryCommandStatus sendUserDictionaryCommand(
        ProtoUserDictionaryStorage.UserDictionaryCommand command) { return null; }
    public void clearUserHistory() {}
    public void clearUserPrediction() {}
}
EOF

mkdir -p "$WORK/src/org/mozc/android/inputmethod/japanese/protobuf"

cat > "$WORK/src/org/mozc/android/inputmethod/japanese/protobuf/ProtoCommands.java" << 'EOF'
package org.mozc.android.inputmethod.japanese.protobuf;
import java.util.List;
import java.util.Collections;
public final class ProtoCommands {
    public static final class Command {
        public Input getInput() { return null; }
        public Output getOutput() { return null; }
    }
    public static final class Input {
        public enum CommandType { SEND_COMMAND, SEND_KEY, CREATE_SESSION, DELETE_SESSION; }
        public static final class TouchEvent {}
        public CommandType getType() { return null; }
        public SessionCommand getCommand() { return null; }
    }
    public static final class Output {
        public enum ErrorCode { SESSION_FAILURE; }
        public boolean getConsumed() { return false; }
        public boolean hasConsumed() { return false; }
        public boolean hasDeletionRange() { return false; }
        public DeletionRange getDeletionRange() { return null; }
        public boolean hasResult() { return false; }
        public Result getResult() { return null; }
        public boolean hasPreedit() { return false; }
        public Preedit getPreedit() { return null; }
        public boolean hasErrorCode() { return false; }
        public ErrorCode getErrorCode() { return null; }
        public ProtoCandidateWindow.CandidateList getAllCandidateWords() { return null; }
        public boolean hasAllCandidateWords() { return false; }
        public boolean hasCandidateWindow() { return false; }
        public ProtoCandidateWindow.CandidateWindow getCandidateWindow() { return null; }
    }
    public static final class KeyEvent {
        public static final class Builder { public KeyEvent build() { return null; } }
        public static Builder newBuilder() { return null; }
    }
    public static final class Preedit {
        public static final class Segment {
            public enum Annotation { HIGHLIGHT, UNDERLINE; }
            public String getValue() { return null; }
            public boolean hasAnnotation() { return false; }
            public Annotation getAnnotation() { return null; }
            public int getValueLength() { return 0; }
        }
        public int getCursor() { return 0; }
        public List<Segment> getSegmentList() { return Collections.emptyList(); }
        public int getSegmentCount() { return 0; }
        public Segment getSegment(int index) { return null; }
    }
    public static final class SessionCommand {
        public enum CommandType { SWITCH_INPUT_MODE, SUBMIT, UNDO; }
        public CommandType getType() { return null; }
    }
    public static final class Context {
        public enum InputFieldType { NORMAL, PASSWORD, TEL, NUMBER; }
    }
    public static final class DeletionRange {
        public int getOffset() { return 0; }
        public int getLength() { return 0; }
    }
    public static final class Result {
        public String getValue() { return null; }
        public boolean hasCursorOffset() { return false; }
        public int getCursorOffset() { return 0; }
    }
    public static final class Request {
        public static final class Builder { public Request build() { return null; } }
        public static Builder newBuilder() { return new Builder(); }
    }
}
EOF

cat > "$WORK/src/org/mozc/android/inputmethod/japanese/protobuf/ProtoCandidateWindow.java" << 'EOF'
package org.mozc.android.inputmethod.japanese.protobuf;
import java.util.List;
import java.util.Collections;
public final class ProtoCandidateWindow {
    public enum Category { CONVERSION, PREDICTION, SUGGESTION; }
    public static final class CandidateList {
        public int getCandidatesCount() { return 0; }
        public List<CandidateWord> getCandidatesList() { return Collections.emptyList(); }
        public Category getCategory() { return null; }
        public boolean hasCategory() { return false; }
    }
    public static final class CandidateWord {
        public String getValue() { return null; }
        public int getIndex() { return 0; }
        public int getId() { return 0; }
        public boolean hasAnnotation() { return false; }
        public Annotation getAnnotation() { return null; }
    }
    public static final class Annotation {
        public boolean hasDescription() { return false; }
        public String getDescription() { return null; }
    }
    public static final class CandidateWindow {
        public boolean hasFocusedIndex() { return false; }
        public int getFocusedIndex() { return 0; }
    }
}
EOF

cat > "$WORK/src/org/mozc/android/inputmethod/japanese/protobuf/ProtoConfig.java" << 'EOF'
package org.mozc.android.inputmethod.japanese.protobuf;
public final class ProtoConfig {
    public static final class Config {
        public enum SessionKeymap { MOBILE; }
        public enum SelectionShortcut { NO_SHORTCUT; }
        public enum FundamentalCharacterForm { FUNDAMENTAL_HALF_WIDTH, FUNDAMENTAL_INPUT_MODE; }
        public enum YenSignCharacter { YEN_SIGN; }
        public enum HistoryLearningLevel { READ_ONLY, DEFAULT_HISTORY; }
        public static Builder newBuilder() { return new Builder(); }
        public static final class Builder {
            public SessionKeymap sessionKeymap;
            public SelectionShortcut selectionShortcut;
            public boolean useEmojiConversion;
            public FundamentalCharacterForm spaceCharacterForm;
            public boolean useKanaModifierInsensitiveConversion;
            public boolean useTypingCorrection;
            public YenSignCharacter yenSignCharacter;
            public HistoryLearningLevel historyLearningLevel;
            public boolean incognitoMode;
            public GeneralConfig generalConfig;
            public Config build() { return null; }
        }
    }
    public static final class GeneralConfig {
        public static Builder newBuilder() { return new Builder(); }
        public static final class Builder {
            public boolean uploadUsageStats;
            public GeneralConfig build() { return null; }
        }
    }
}
EOF

cat > "$WORK/src/org/mozc/android/inputmethod/japanese/protobuf/ProtoUserDictionaryStorage.java" << 'EOF'
package org.mozc.android.inputmethod.japanese.protobuf;
import java.util.List;
import java.util.Collections;
public final class ProtoUserDictionaryStorage {
    public static final class UserDictionary {
        public enum PosType { NO_POS, NOUN, ABBREVIATION, SUGGESTION_ONLY, PROPER_NOUN, PERSONAL_NAME, FAMILY_NAME, FIRST_NAME, ORGANIZATION_NAME, PLACE_NAME, SA_IRREGULAR_CONJUGATION_NOUN, ADJECTIVE_VERBAL_NOUN, NUMBER, ALPHABET, SYMBOL, EMOTICON, SUFFIX, COUNTER_SUFFIX, GENERIC_SUFFIX, PERSON_NAME_SUFFIX, PLACE_NAME_SUFFIX, WA_GROUP1_VERB, KA_GROUP1_VERB, SA_GROUP1_VERB, TA_GROUP1_VERB, NA_GROUP1_VERB, MA_GROUP1_VERB, RA_GROUP1_VERB, GA_GROUP1_VERB, BA_GROUP1_VERB, HA_GROUP1_VERB, GROUP2_VERB, KURU_GROUP3_VERB, SURU_GROUP3_VERB, ZURU_GROUP3_VERB, RU_GROUP3_VERB, ADJECTIVE, ADVERB, PRENOUN_ADJECTIVAL, CONJUNCTION, INTERJECTION, SENTENCE_ENDING_PARTICLE, PUNCTUATION, FREE_STANDING_WORD, SUPPRESSION_WORD, INDEPENDENT_WORD, PREFIX, ENGLISH_PREFIX, ENGLISH_SUFFIX, CONTENT_WORD, FUNCTION_WORD, SELECTION_ONLY, USED_IN_GRAMMAR; }
        public int getId() { return 0; }
        public String getName() { return null; }
        public static final class Entry {
            public static Builder newBuilder() { return new Builder(); }
            public static final class Builder {
                public Builder setKey(String key) { return this; }
                public Builder setValue(String value) { return this; }
                public Builder setPos(PosType pos) { return this; }
                public Entry build() { return null; }
            }
        }
    }
    public static final class UserDictionaryCommand {
        public enum CommandType { CREATE_SESSION, LOAD, GET_USER_DICTIONARY_NAME_LIST, DELETE_DICTIONARY, IMPORT_DATA, CREATE_DICTIONARY, ADD_ENTRY, SAVE, DELETE_SESSION; }
        public static Builder newBuilder() { return new Builder(); }
        public static final class Builder {
            public Builder setType(CommandType type) { return this; }
            public Builder setSessionId(int id) { return this; }
            public Builder setDictionaryId(int id) { return this; }
            public Builder setDictionaryName(String name) { return this; }
            public Builder setData(String data) { return this; }
            public Builder setIgnoreInvalidEntries(boolean v) { return this; }
            public Builder setEntry(UserDictionary.Entry entry) { return this; }
            public UserDictionaryCommand build() { return null; }
        }
    }
    public static final class UserDictionaryCommandStatus {
        public enum Status { USER_DICTIONARY_COMMAND_SUCCESS, UNKNOWN_ERROR; }
        public Status getStatus() { return null; }
        public int getSessionId() { return 0; }
        public boolean hasSessionId() { return false; }
        public int getDictionaryId() { return 0; }
        public boolean hasDictionaryId() { return false; }
        public UserDictionaryStorage getStorage() { return null; }
        public boolean hasStorage() { return false; }
    }
    public static final class UserDictionaryStorage {
        public List<UserDictionary> getDictionariesList() { return Collections.emptyList(); }
    }
}
EOF

# Compile all stubs
echo "Compiling stubs..."
find "$WORK/src" -name "*.java" > "$WORK/sources.txt"
mkdir -p "$WORK/classes"
javac -source 8 -target 8 \
  -cp "$ANDROID_JAR:$WORK/guava.jar" \
  -d "$WORK/classes" \
  @"$WORK/sources.txt"

# Package into mozc-release.aar
jar cf "$WORK/classes.jar" -C "$WORK/classes" .
echo '<manifest xmlns:android="http://schemas.android.com/apk/res/android" package="org.mozc.android.inputmethod.japanese"/>' > "$WORK/AndroidManifest.xml"
cd "$WORK" && zip "$LIBS_DIR/mozc-release.aar" AndroidManifest.xml classes.jar

echo "Library dependencies prepared:"
ls -la "$LIBS_DIR/"
