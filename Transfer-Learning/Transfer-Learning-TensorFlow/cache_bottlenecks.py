from __future__ import division
from __future__ import absolute_import
from __future__ import print_function
import tensorflow as tf
import os
import sys
import numpy as np


BOTTLENECK_TENSOR_NAME = 'pool_3/_reshape:0'
JPEG_DATA_TENSOR_NAME = 'DecodeJpeg/contents:0'
RESIZED_INPUT_TENSOR_NAME = 'ResizeBilinear:0'
BOTTLENECK_TENSOR_SIZE = 2048
MODEL_INPUT_WIDTH = 299
MODEL_INPUT_HEIGHT = 299
MODEL_INPUT_DEPTH = 3
FLOWER_PHOTOS_PATH = "flower_photos"
TRAIN_PERCENTAGE = 0.8
VALIDATE_PERCENTAGE = 0.2


def assemble_example(value, label):
    return_example = tf.train.Example(features=tf.train.Features(feature={
        "bottleneck_tensor_value": tf.train.Feature(bytes_list=tf.train.BytesList(value=[value.tostring()])),
        "label": tf.train.Feature(int64_list=tf.train.Int64List(value=[label]))
    }))
    return return_example


def convert_bottlenecks_to_tfrecords():
    graph_def = tf.GraphDef()
    with tf.gfile.FastGFile("model_dir/classify_image_graph_def.pb", "r") as f:
        graph_def.ParseFromString(f.read())
    (jpeg_data_tensor,
     resized_input_tensor,
     bottleneck_tensor) = tf.import_graph_def(graph_def=graph_def, name="", return_elements=[JPEG_DATA_TENSOR_NAME,
                                                                                             RESIZED_INPUT_TENSOR_NAME,
                                                                                             BOTTLENECK_TENSOR_NAME])

    sess = tf.Session()
    directories = os.listdir(FLOWER_PHOTOS_PATH)
    label2id = dict(zip(directories, range(len(directories))))
    train_writer = tf.python_io.TFRecordWriter("tfrecords/train.tfrecords")
    valid_writer = tf.python_io.TFRecordWriter("tfrecords/valid.tfrecords")
    for directory in directories:
        label_id = label2id[directory]
        sample_names = os.listdir(os.path.join(FLOWER_PHOTOS_PATH, directory))
        num_samples = len(sample_names)
        train_ids = np.random.choice(num_samples, size=int(num_samples * TRAIN_PERCENTAGE), replace=False)

        for index, sample_name in enumerate(sample_names):
            sys.stdout.write("\r")
            # sys.stdout.write("deal with %s, id %d\n" % (directory, label_id))
            sys.stdout.write("convert %dth %% %d example to tfrecords file, id: %d" % (index + 1, num_samples, label_id))
            sys.stdout.flush()
            sample_path = os.path.join(FLOWER_PHOTOS_PATH, directory, sample_name)
            with tf.gfile.FastGFile(sample_path, "r") as f:
                content = f.read()
                bottleneck_tensor_value = sess.run(bottleneck_tensor, feed_dict={jpeg_data_tensor: content})
                example = assemble_example(np.squeeze(bottleneck_tensor_value), label_id)
                if index in train_ids:
                    train_writer.write(example.SerializeToString())
                else:
                    valid_writer.write(example.SerializeToString())
    train_writer.close()
    valid_writer.close()


if __name__ == "__main__":
    convert_bottlenecks_to_tfrecords()