from transformers import TFDistilBertForSequenceClassification, DistilBertTokenizer
import tensorflow as tf
import os

def main():
    model_path = "./emotion_model"
    print(f"Loading HF model from {model_path} as TF model...")
    # Load PyTorch model as TensorFlow
    model = TFDistilBertForSequenceClassification.from_pretrained(model_path, from_pt=True)
    tokenizer = DistilBertTokenizer.from_pretrained(model_path)
    
    print("Converting to TFLite...")
    MAX_LENGTH = 64
    
    # We create a dummy input to trace the model
    dummy_input = {
        'input_ids': tf.zeros((1, MAX_LENGTH), dtype=tf.int32),
        'attention_mask': tf.zeros((1, MAX_LENGTH), dtype=tf.int32)
    }
    
    @tf.function(input_signature=[
        tf.TensorSpec(shape=(1, MAX_LENGTH), dtype=tf.int32, name='input_ids'),
        tf.TensorSpec(shape=(1, MAX_LENGTH), dtype=tf.int32, name='attention_mask')
    ])
    def serving_fn(input_ids, attention_mask):
        return model(input_ids=input_ids, attention_mask=attention_mask).logits

    # Save as temporary SavedModel
    print("Exporting to SavedModel...")
    tf.saved_model.save(model, "saved_model_tmp", signatures={"serving_default": serving_fn})
    
    # Convert SavedModel to TFLite
    print("Creating TFLite Converter...")
    converter = tf.lite.TFLiteConverter.from_saved_model("saved_model_tmp")
    converter.optimizations = [tf.lite.Optimize.DEFAULT] # INT8 quantization
    
    # Add dummy dataset for full INT8 quantization if required, but dynamic range is often enough for text models. 
    # Opt.DEFAULT does dynamic range quantization.
    
    tflite_model = converter.convert()
    
    out_path = "emotion_model.tflite"
    with open(out_path, "wb") as f:
        f.write(tflite_model)
        
    print(f"Saved TFLite model to {out_path}")
    print(f"File size: {os.path.getsize(out_path) / (1024*1024):.2f} MB")
    
    tokenizer.save_vocabulary(".")
    print("Exported vocab.txt")

if __name__ == "__main__":
    main()
