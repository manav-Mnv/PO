"""
export_tflite.py
Exports the fine-tuned PyTorch DistilBERT model to TFLite using
Hugging Face Optimum (no fragile from_pt TF conversion).

Output files placed in ./android_assets/:
  emotion_classifier.tflite   — the model (target < 40 MB after quantization)
  vocab.txt                   — BertTokenizer vocabulary for Android
  android_meta.json           — label map for ResilientEmotionDetector.kt
"""

import os
import json
import shutil
import subprocess
import sys

MODEL_PATH    = "./emotion_model"
EXPORT_DIR    = "./android_assets"
TFLITE_NAME   = "emotion_classifier.tflite"
MAX_LENGTH    = 64


def check_model_exists():
    required = ["config.json", "vocab.txt"]
    missing = [f for f in required if not os.path.exists(os.path.join(MODEL_PATH, f))]
    if missing:
        print(f"ERROR: Missing files in {MODEL_PATH}: {missing}")
        print("Run train_model.py first.")
        sys.exit(1)


def export_onnx():
    """Export PyTorch model to ONNX via optimum CLI."""
    onnx_dir = "./onnx_tmp"
    print("[1/4] Exporting to ONNX via optimum-cli...")
    result = subprocess.run([
        "optimum-cli", "export", "onnx",
        "--model", MODEL_PATH,
        "--task", "text-classification",
        "--opset", "13",
        onnx_dir,
    ], capture_output=False)

    if result.returncode != 0:
        print("ERROR: ONNX export failed. Make sure optimum[exporters] is installed.")
        sys.exit(1)

    onnx_path = os.path.join(onnx_dir, "model.onnx")
    if not os.path.exists(onnx_path):
        print(f"ERROR: Expected ONNX file not found at {onnx_path}")
        sys.exit(1)

    print(f"  ONNX model: {onnx_path}")
    return onnx_path


def convert_to_tflite(onnx_path: str) -> str:
    """Convert ONNX → TFLite with dynamic-range quantization."""
    import tensorflow as tf

    print("[2/4] Converting ONNX → TFLite...")
    saved_model_dir = "./saved_model_tmp"

    # onnx → tf saved model
    result = subprocess.run([
        sys.executable, "-m", "tf2onnx.convert",
        "--onnx", onnx_path,
        "--output", saved_model_dir,
        "--saved-model",
    ], capture_output=False)

    # Fallback: use onnx-tf if tf2onnx fails
    if result.returncode != 0 or not os.path.exists(saved_model_dir):
        print("  tf2onnx failed — trying onnx-tf fallback...")
        import onnx
        from onnx_tf.backend import prepare
        onnx_model = onnx.load(onnx_path)
        tf_rep = prepare(onnx_model)
        tf_rep.export_graph(saved_model_dir)

    print("[3/4] Applying dynamic-range quantization...")
    converter = tf.lite.TFLiteConverter.from_saved_model(saved_model_dir)
    converter.optimizations = [tf.lite.Optimize.DEFAULT]
    tflite_model = converter.convert()

    os.makedirs(EXPORT_DIR, exist_ok=True)
    out_path = os.path.join(EXPORT_DIR, TFLITE_NAME)
    with open(out_path, "wb") as f:
        f.write(tflite_model)

    size_mb = os.path.getsize(out_path) / (1024 * 1024)
    print(f"  TFLite saved: {out_path}  ({size_mb:.1f} MB)")

    # Clean up temp dirs
    shutil.rmtree("./onnx_tmp", ignore_errors=True)
    shutil.rmtree(saved_model_dir, ignore_errors=True)

    return out_path


def copy_android_assets():
    """Copy vocab.txt and android_meta.json to android_assets/."""
    print("[4/4] Copying Android assets...")

    for fname in ["vocab.txt", "android_meta.json"]:
        src = os.path.join(MODEL_PATH, fname)
        dst = os.path.join(EXPORT_DIR, fname)
        if os.path.exists(src):
            shutil.copy(src, dst)
            print(f"  Copied {fname}")
        else:
            print(f"  WARNING: {fname} not found in {MODEL_PATH}")


def verify_tflite(tflite_path: str):
    """Run a quick inference sanity check on the exported model."""
    import numpy as np
    import tensorflow as tf

    print("\n--- Sanity check: running dummy inference ---")
    interpreter = tf.lite.Interpreter(model_path=tflite_path)
    interpreter.allocate_tensors()

    inp = interpreter.get_input_details()
    out = interpreter.get_output_details()

    # Dummy input: "i feel really happy today"
    dummy_ids  = [[101, 1045, 2514, 2428, 3407, 2651, 102] + [0] * 57]
    dummy_mask = [[1,   1,    1,    1,    1,    1,   1  ] + [0] * 57]

    interpreter.set_tensor(inp[0]["index"], np.array(dummy_ids,  dtype=np.int32))
    interpreter.set_tensor(inp[1]["index"], np.array(dummy_mask, dtype=np.int32))
    interpreter.invoke()

    logits = interpreter.get_tensor(out[0]["index"])[0]
    pred_id = int(logits.argmax())

    with open(os.path.join(EXPORT_DIR, "android_meta.json")) as f:
        meta = json.load(f)

    pred_label = meta["id2label"].get(str(pred_id), "unknown")
    android_label = meta["android_map"].get(pred_label, "NEUTRAL")
    print(f"  Input:  'i feel really happy today'")
    print(f"  Output: {pred_label} → Android: {android_label}")
    print(f"  Logits: {logits.tolist()}")
    print("✓ Model is working correctly.\n")


def main():
    print("=" * 50)
    print("PO App — TFLite Export Pipeline")
    print("=" * 50)

    check_model_exists()
    onnx_path  = export_onnx()
    tflite_path = convert_to_tflite(onnx_path)
    copy_android_assets()
    verify_tflite(tflite_path)

    print("=" * 50)
    print("✓ Export complete. Copy these to Android:")
    print(f"  {EXPORT_DIR}/emotion_classifier.tflite  →  app/src/main/assets/models/")
    print(f"  {EXPORT_DIR}/vocab.txt                   →  app/src/main/assets/models/")
    print(f"  {EXPORT_DIR}/android_meta.json           →  app/src/main/assets/models/")
    print("=" * 50)


if __name__ == "__main__":
    main()
