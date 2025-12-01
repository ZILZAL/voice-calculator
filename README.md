# Voice Calculator - آلة حاسبة صوتية

A voice-controlled calculator Android app that supports three Arabic dialects:
- 🇱🇧 Levantine Arabic (Lebanese, Syrian, Jordanian)
- 🇵🇸 Palestinian Arabic
- 🇮🇱 Israeli Arabic

## Features

- ✅ **Continuous Voice Recognition**: Speak numbers and operations naturally
- ✅ **Three Arabic Dialects**: Full support for Levantine, Palestinian, and Israeli Arabic
- ✅ **Editable Input**: Each number appears on a new line and can be edited manually
- ✅ **Full Calculator**: Supports +, -, ×, ÷ with proper operator precedence
- ✅ **Voice Commands**: Clear, equals, and result commands in Arabic
- ✅ **Calculation History**: Keeps track of recent calculations

## Usage

1. Select your preferred Arabic dialect from the dropdown
2. Press "ابدأ" (Start) to begin listening
3. Speak numbers and operations (e.g., "خمسة زائد تلاتة ضرب اتنين")
4. Each recognized number/operation appears on a new line
5. Tap any number to edit it manually if needed
6. Press "احسب النتيجة" (Calculate Result) to see the answer

## Supported Voice Commands

### Numbers
- Basic numbers: واحد، اتنين، تلاتة، أربعة، خمسة...
- Teens: حداش، اطناش، تلطعش...
- Tens: عشرين، تلاتين، أربعين...
- Hundreds: مية، ميتين، تلتمية...

### Operations
- Addition: زائد، جمع، و
- Subtraction: ناقص، طرح، منه
- Multiplication: ضرب، في
- Division: قسمة، على

### Commands
- Equals: يساوي، نتيجة
- Clear: مسح، احذف

## Technical Details

- **Language**: Kotlin
- **Min SDK**: API 23 (Android 6.0)
- **Target SDK**: API 34 (Android 14)
- **Architecture**: MVVM with Clean Architecture
- **Speech Recognition**: Google Speech Recognition API

## Requirements

- Android device with API 23+
- Microphone permission
- Internet connection (for speech recognition)

## Building

```bash
./gradlew assembleDebug
```

## License

This project is open source and available under the MIT License.
