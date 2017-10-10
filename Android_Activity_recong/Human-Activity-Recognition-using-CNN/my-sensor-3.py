import matplotlib.pyplot as plt
import numpy as np
from sklearn.metrics import precision_score, recall_score, f1_score, confusion_matrix, roc_curve, auc
from sklearn.preprocessing import label_binarize

#plt.style.use('ggplot')

def plot_confusion_matrix(cm, title='Normalized Confusion matrix', cmap=plt.cm.get_cmap("Blues")):
    cm = cm / cm.astype(np.float).sum(axis=1)
    print "confusion_matrix: \n{}".format(cm)
    plt.imshow(cm, interpolation='nearest', cmap=cmap)
    plt.title(title)
    plt.colorbar()
    tick_marks = np.arange(6)
    plt.xticks(tick_marks, [1, 2, 3, 4, 5, 6], rotation=45)
    plt.yticks(tick_marks, [1, 2, 3, 4, 5, 6])
    plt.tight_layout()
    plt.ylabel('True label')
    plt.xlabel('Predicted label')
    plt.show()

def test_har():
    y_true = np.load("y_true.npy")
    y_pred = np.load("y_pred.npy")
    print "Precision", precision_score(y_true.tolist(), y_pred.tolist(), average='weighted')
    print "Recall", recall_score(y_true, y_pred, average='weighted')
    print "f1_score", f1_score(y_true, y_pred, average='weighted')
    print "confusion_matrix"
    print confusion_matrix(y_true, y_pred)
    plot_confusion_matrix(confusion_matrix(y_true, y_pred))

    if __name__ == "__main__":
        test_har()
