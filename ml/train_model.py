import pandas as pd
import torch
from datasets import load_dataset
from transformers import (DistilBertTokenizerFast, DistilBertForSequenceClassification,
                          Trainer, TrainingArguments)
from sklearn.metrics import accuracy_score

def compute_metrics(pred):
    labels = pred.label_ids
    preds = pred.predictions.argmax(-1)
    acc = accuracy_score(labels, preds)
    return {"accuracy": acc}

def main():
    print("Loading dataset...")
    dataset = load_dataset("dair-ai/emotion", trust_remote_code=True)
    
    tokenizer = DistilBertTokenizerFast.from_pretrained("distilbert-base-uncased")
    
    def tokenize_function(examples):
        return tokenizer(examples["text"], padding="max_length", truncation=True, max_length=64)
    
    tokenized_datasets = dataset.map(tokenize_function, batched=True)
    
    model = DistilBertForSequenceClassification.from_pretrained("distilbert-base-uncased", num_labels=6)
    
    # Mobile constrained training params
    training_args = TrainingArguments(
        output_dir="./emotion_model/results",
        num_train_epochs=3,
        per_device_train_batch_size=16,
        per_device_eval_batch_size=64,
        evaluation_strategy="epoch",
        save_strategy="epoch",
        logging_dir="./emotion_model/logs",
        logging_steps=100,
        load_best_model_at_end=True,
    )
    
    trainer = Trainer(
        model=model,
        args=training_args,
        train_dataset=tokenized_datasets["train"],
        eval_dataset=tokenized_datasets["test"],
        compute_metrics=compute_metrics,
    )
    
    print("Starting training...")
    trainer.train()
    
    print("Evaluating...")
    metrics = trainer.evaluate()
    print(f"Test Accuracy: {metrics['eval_accuracy']:.4f}")
    
    print("Saving model and tokenizer...")
    trainer.save_model("./emotion_model")
    tokenizer.save_pretrained("./emotion_model")
    print("Saved to ./emotion_model")

if __name__ == "__main__":
    main()
