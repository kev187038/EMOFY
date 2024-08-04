import numpy as np
import pandas as pd
from tensorflow.keras.models import load_model
from tensorflow.keras.preprocessing.image import ImageDataGenerator
from sklearn.metrics import classification_report, confusion_matrix
import seaborn as sns
import matplotlib.pyplot as plt
from sklearn.metrics import accuracy_score

# Set the paths to the dataset
test_dir = '../fer-2013/test'

# Create an ImageDataGenerator for the test data
test_datagen = ImageDataGenerator(rescale=1.0/255)

# Create a generator to read images from the directory
test_generator = test_datagen.flow_from_directory(
    test_dir,
    target_size=(48, 48),
    color_mode='grayscale',
    batch_size=64,
    class_mode='categorical',
    shuffle=False  # Ensure predictions are in the same order as ground truth
)

# Load the trained model
model = load_model('C:\\Users\\P.Pellegriti\\Documents\\emofy\\EMOFY\\emotion_classification\\retraining\\emotion_recognition_model_updated.h5')

# Predict on the test data
test_generator.reset()
predictions = model.predict(test_generator, steps=test_generator.samples // test_generator.batch_size + 1)
y_pred = np.argmax(predictions, axis=1)

# Get the true labels
y_true = test_generator.classes
class_labels = list(test_generator.class_indices.keys())

# Calculate classification report
report = classification_report(y_true, y_pred, target_names=class_labels, output_dict=True)

#total accuracy
total_accuracy = accuracy_score(y_true, y_pred)
print("Total Accuracy: ", total_accuracy)

# Print classification report
print("Classification Report:")
print(pd.DataFrame(report).transpose())

# Calculate confusion matrix
conf_matrix = confusion_matrix(y_true, y_pred)

# Plot confusion matrix
plt.figure(figsize=(10, 7))
sns.heatmap(conf_matrix, annot=True, fmt='d', cmap='Blues', xticklabels=class_labels, yticklabels=class_labels)
plt.xlabel('Predicted Label')
plt.ylabel('True Label')
plt.title('Confusion Matrix')
plt.show()
