import torch
from datasets import load_dataset
from transformers import (
    DistilBertTokenizerFast,
    DistilBertForSequenceClassification,
    Trainer,
    TrainingArguments,
)
from sklearn.metrics import accuracy_score, classification_report
import json
import os

# ── Label config ──────────────────────────────────────────────────────────────
NUM_LABELS = 6

ID2LABEL = {
    0: "sadness",
    1: "joy",
    2: "love",
    3: "anger",
    4: "fear",
    5: "surprise",
}
LABEL2ID = {v: k for k, v in ID2LABEL.items()}

# Maps fine-grained labels → your Android EmotionState enum
ANDROID_LABEL_MAP = {
    "sadness":  "SAD",
    "joy":      "HAPPY",
    "love":     "HAPPY",
    "anger":    "ANGRY",
    "fear":     "ANXIOUS",
    "surprise": "NEUTRAL",
}

MODEL_OUTPUT_DIR = "./emotion_model"
MAX_LENGTH = 64


# ── Metrics ───────────────────────────────────────────────────────────────────
def compute_metrics(pred):
    labels = pred.label_ids
    preds = pred.predictions.argmax(-1)
    acc = accuracy_score(labels, preds)
    return {"accuracy": acc}


# ── Main ──────────────────────────────────────────────────────────────────────
def main():
    print("=" * 50)
    print("PO App — DistilBERT Emotion Fine-Tuning")
    print("=" * 50)

    # 1. Dataset
    print("\n[1/5] Loading dataset...")
    dataset = load_dataset("dair-ai/emotion")
    print(f"  Train: {len(dataset['train'])} | "
          f"Val: {len(dataset['validation'])} | "
          f"Test: {len(dataset['test'])}")

    # 2. Tokenizer
    print("\n[2/5] Loading tokenizer...")
    tokenizer = DistilBertTokenizerFast.from_pretrained("distilbert-base-uncased")

    def tokenize(examples):
        return tokenizer(
            examples["text"],
            padding="max_length",
            truncation=True,
            max_length=MAX_LENGTH,
        )

    tokenized = dataset.map(tokenize, batched=True)

    # 3. Model
    print("\n[3/5] Loading model...")
    model = DistilBertForSequenceClassification.from_pretrained(
        "distilbert-base-uncased",
        num_labels=NUM_LABELS,
        id2label=ID2LABEL,
        label2id=LABEL2ID,
    )

    # 4. Training
    print("\n[4/5] Training...")
    use_gpu = torch.cuda.is_available()
    print(f"  Device: {'GPU ✓' if use_gpu else 'CPU (slower, ~1-2h)'}")

    training_args = TrainingArguments(
        output_dir=os.path.join(MODEL_OUTPUT_DIR, "results"),
        num_train_epochs=3,
        per_device_train_batch_size=16,
        per_device_eval_batch_size=64,
        eval_strategy="epoch",           # fixed from evaluation_strategy
        save_strategy="epoch",
        logging_dir=os.path.join(MODEL_OUTPUT_DIR, "logs"),
        logging_steps=100,
        load_best_model_at_end=True,
        metric_for_best_model="accuracy",
        report_to="none",                # no wandb / tensorboard needed
    )

    trainer = Trainer(
        model=model,
        args=training_args,
        train_dataset=tokenized["train"],
        eval_dataset=tokenized["validation"],
        compute_metrics=compute_metrics,
    )

    trainer.train()

    # 5. Evaluate + save
    print("\n[5/5] Final evaluation on test split...")
    test_results = trainer.predict(tokenized["test"])
    preds = test_results.predictions.argmax(-1)
    labels = test_results.label_ids
    print(classification_report(labels, preds, target_names=list(ID2LABEL.values())))

    trainer.save_model(MODEL_OUTPUT_DIR)
    tokenizer.save_pretrained(MODEL_OUTPUT_DIR)

    # Save Android label map alongside the model
    android_meta = {
        "id2label": ID2LABEL,
        "android_map": ANDROID_LABEL_MAP,
        "max_length": MAX_LENGTH,
        "num_labels": NUM_LABELS,
    }
    with open(os.path.join(MODEL_OUTPUT_DIR, "android_meta.json"), "w") as f:
        json.dump(android_meta, f, indent=2)

    print(f"\n✓ Model saved to {MODEL_OUTPUT_DIR}/")
    print("  Files: pytorch_model.bin, config.json, vocab.txt, android_meta.json")


if __name__ == "__main__":
    main()
