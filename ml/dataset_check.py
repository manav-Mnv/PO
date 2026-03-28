import pandas as pd
from datasets import load_dataset

def main():
    print("Loading dair-ai/emotion dataset...")
    # Using trust_remote_code=True as sometimes HF datasets require it
    dataset = load_dataset("dair-ai/emotion", trust_remote_code=True)
    
    train_df = pd.DataFrame(dataset['train'])
    
    # Map from output classes 0-5
    labels_map = {0: "sadness", 1: "joy", 2: "love", 3: "anger", 4: "fear", 5: "surprise"}
    
    print("\n--- Label Distribution (Train Split) ---")
    dist = train_df['label'].value_counts().sort_index()
    for label_idx, count in dist.items():
        print(f"{labels_map[label_idx]} ({label_idx}): {count}")
        
    print("\n--- 5 Sample Rows ---")
    sample = train_df.head(5)
    for idx, row in sample.iterrows():
        print(f"Text: {row['text']} | Label: {labels_map[row['label']]}")

if __name__ == "__main__":
    main()
