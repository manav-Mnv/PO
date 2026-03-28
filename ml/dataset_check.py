import pandas as pd
from datasets import load_dataset

# Maps the dair-ai/emotion dataset labels to PO app emotion labels
LABEL_MAP = {
    0: "sadness",
    1: "joy",
    2: "love",
    3: "anger",
    4: "fear",
    5: "surprise"
}

# Maps to your Android EmotionState enum
ANDROID_MAP = {
    "sadness": "SAD",
    "joy":     "HAPPY",
    "love":    "HAPPY",
    "anger":   "ANGRY",
    "fear":    "ANXIOUS",
    "surprise":"NEUTRAL"
}

def main():
    print("=" * 50)
    print("PO App — Dataset Validation Check")
    print("=" * 50)

    print("\nLoading dair-ai/emotion dataset...")
    dataset = load_dataset("dair-ai/emotion")

    for split in ["train", "validation", "test"]:
        if split not in dataset:
            print(f"WARNING: '{split}' split not found — skipping.")
            continue

        df = pd.DataFrame(dataset[split])
        print(f"\n--- {split.upper()} split ({len(df)} samples) ---")
        dist = df["label"].value_counts().sort_index()
        for label_idx, count in dist.items():
            name = LABEL_MAP[label_idx]
            android = ANDROID_MAP[name]
            bar = "#" * (count // 100)
            print(f"  [{label_idx}] {name:10s} → Android:{android:8s}  {count:5d}  {bar}")

    print("\n--- 5 Sample Rows (train) ---")
    train_df = pd.DataFrame(dataset["train"])
    for _, row in train_df.head(5).iterrows():
        name = LABEL_MAP[row["label"]]
        android = ANDROID_MAP[name]
        print(f"  [{android}] {row['text'][:80]}")

    print("\n✓ Dataset check complete. Ready to train.")

if __name__ == "__main__":
    main()
