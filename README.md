<div align="center">

# PO

**An on-device AI companion for Android that notices how you feel and answers in kind.**

![Status](https://img.shields.io/badge/status-prototype-0969da?style=flat-square)
![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=flat-square&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=flat-square&logo=jetpackcompose&logoColor=white)
![TensorFlow Lite](https://img.shields.io/badge/TensorFlow%20Lite-FF6F00?style=flat-square&logo=tensorflow&logoColor=white)
![Hugging Face](https://img.shields.io/badge/DistilBERT-FFD21F?style=flat-square&logo=huggingface&logoColor=black)

</div>

---

## About

PO ("Hi, I'm PO. Tell me how you're feeling.") is a chat companion that runs its intelligence **entirely on the phone**. Every message goes through a fine-tuned DistilBERT emotion classifier and an intent detector; a rule-based response engine then picks a reply that fits both the emotion and the user's preferred tone. No message leaves the device.

## Features

- **Emotion detection on the device**: DistilBERT fine-tuned on [`dair-ai/emotion`](https://huggingface.co/datasets/dair-ai/emotion), exported to TFLite. Six labels: sadness, joy, love, anger, fear, surprise.
- **Fails safe**: if inference fails, `ResilientEmotionDetector` returns a neutral emotion instead of crashing, and it also exposes softmax confidence scores for every class.
- **Intent detection**: greeting, question, support, complaint, thanks, farewell.
- **Tone-aware replies**: a prioritised rule table maps intent + emotion to templates in four styles (formal, casual, empathetic, playful), with `{name}` personalisation and an LRU render cache.
- **Personas**: Analyst, Cyber, Growth and Listener.
- **Private local storage**: chat history, mood entries and profile in Room; the database key is kept in AES-256 `EncryptedSharedPreferences` (`DBKeyManager`).

## How a message flows

```
user text ─▶ BertTokenizer (WordPiece, 64 tokens)
          ─▶ emotion_classifier.tflite ─▶ emotion
          ─▶ IntentDetector            ─▶ intent
          ─▶ ResponseEngine: rules(intent, emotion, priority) ─▶ template(style) ─▶ reply
          ─▶ Room database ─▶ Compose chat UI
```

## Tech stack

| Layer | Tools |
| --- | --- |
| App | Kotlin 2.0 · Jetpack Compose · Material 3 · Hilt · Room 2.6 · Coroutines/Flow |
| On-device ML | TensorFlow Lite · custom WordPiece tokenizer |
| Model training | Python · PyTorch · Hugging Face Transformers + Datasets · Optimum · scikit-learn |
| Architecture | Clean, multi-module: `app` · `domain` · `data` · `ai` · `presentation` · `personalization` · `core` |

## Getting started

### 1. Train and export the model

The files under `app/src/main/assets/models/` are placeholders. Generate the real ones:

```sh
cd ml
pip install -r requirements.txt
python train_model.py      # fine-tunes DistilBERT on dair-ai/emotion
python export_tflite.py    # writes emotion_classifier.tflite, vocab.txt, android_meta.json
```

Copy the outputs into `app/src/main/assets/models/`.

### 2. Run the app

Open the project in Android Studio (AGP 8.13, compile SDK 36, min SDK 30) and run the `app` configuration on a device or emulator. Without real model files, every message is treated as neutral.

### 3. Tests

```sh
./gradlew test
```

Unit tests cover the emotion detectors (including the fallback path), the intent detector and the response engine.

## Project structure

```
app/              Android entry point, DI modules, response engine and templates
ai/               TFLite emotion detector, intent detector, tokenizer, personas
domain/           Models (Emotion, Intent, Persona, ChatMessage...), repositories, use cases
data/             Room database, DAOs, entities, Keystore-backed DB key manager
presentation/     Compose chat screen and ViewModel
personalization/  Behaviour tracking for future personalisation
ml/               Training, export and dataset-check scripts
```

## Author

Built by **[Manav](https://github.com/manav-Mnv)**.
